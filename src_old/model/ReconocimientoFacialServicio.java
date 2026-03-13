package model;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class ReconocimientoFacialServicio {

    public static class ResultadoReconocimiento {
        public final UsuarioServicio.UsuarioRecord usuario;
        public final double similitud;
        public final File fotoRegistrada;

        public ResultadoReconocimiento(UsuarioServicio.UsuarioRecord usuario, double similitud, File fotoRegistrada) {
            this.usuario = usuario;
            this.similitud = similitud;
            this.fotoRegistrada = fotoRegistrada;
        }
    }

    private static final int HASH_SIZE = 16;
    private static final int NORMALIZED_SIZE = 32;
    private static final double UMBRAL_RECONOCIMIENTO = 0.93;

    private final UsuarioServicio usuarios;
    private final File directorioFotos;

    public ReconocimientoFacialServicio(UsuarioServicio usuarios) {
        this(usuarios, "data/faces");
    }

    public ReconocimientoFacialServicio(UsuarioServicio usuarios, String rutaDirectorioFotos) {
        this.usuarios = usuarios;
        this.directorioFotos = new File(rutaDirectorioFotos);
    }

    public String guardarFotoUsuario(String cedula, File fotoSeleccionada) throws IOException {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new IOException("La cédula es obligatoria para guardar la foto.");
        }
        BufferedImage imagen = leerImagenValida(fotoSeleccionada);
        if (!directorioFotos.exists()) {
            directorioFotos.mkdirs();
        }
        File destino = new File(directorioFotos, cedula.trim() + ".png");
        if (!ImageIO.write(imagen, "png", destino)) {
            throw new IOException("No se pudo guardar la foto del usuario.");
        }
        String base = directorioFotos.getPath().replace(File.separatorChar, '/');
        return base + "/" + destino.getName();
    }

    public ResultadoReconocimiento reconocer(File fotoEscaneada) throws IOException {
        BufferedImage imagenConsulta = leerImagenValida(fotoEscaneada);
        List<UsuarioServicio.UsuarioRecord> lista = usuarios.listarUsuarios();

        UsuarioServicio.UsuarioRecord mejorUsuario = null;
        File mejorFoto = null;
        double mejorSimilitud = -1.0;
        double segundaMejor = -1.0;

        for (UsuarioServicio.UsuarioRecord usuario : lista) {
            if (usuario == null || usuario.fotoPath == null || usuario.fotoPath.trim().isEmpty()) {
                continue;
            }

            File fotoRegistrada = resolverFoto(usuario.fotoPath);
            if (!fotoRegistrada.exists() || !fotoRegistrada.isFile()) {
                continue;
            }

            BufferedImage imagenRegistrada;
            try {
                imagenRegistrada = leerImagenValida(fotoRegistrada);
            } catch (IOException ex) {
                continue;
            }

            double similitud = compararImagenes(imagenConsulta, imagenRegistrada);
            if (similitud > mejorSimilitud) {
                segundaMejor = mejorSimilitud;
                mejorSimilitud = similitud;
                mejorUsuario = usuario;
                mejorFoto = fotoRegistrada;
            } else if (similitud > segundaMejor) {
                segundaMejor = similitud;
            }
        }

        if (mejorUsuario == null) return null;
        if (mejorSimilitud < UMBRAL_RECONOCIMIENTO) return null;
        if (segundaMejor >= 0 && (mejorSimilitud - segundaMejor) < 0.03) return null;

        return new ResultadoReconocimiento(mejorUsuario, mejorSimilitud, mejorFoto);
    }

    public BufferedImage leerImagenValida(File archivo) throws IOException {
        if (archivo == null || !archivo.exists() || !archivo.isFile()) {
            throw new IOException("Debes seleccionar un archivo de imagen válido.");
        }
        BufferedImage imagen = ImageIO.read(archivo);
        if (imagen == null) {
            throw new IOException("El archivo seleccionado no es una imagen JPG o PNG válida.");
        }
        return imagen;
    }

    private File resolverFoto(String fotoPath) {
        File foto = new File(fotoPath);
        if (foto.isAbsolute()) return foto;
        return new File(fotoPath.replace("/", File.separator));
    }

    private double compararImagenes(BufferedImage a, BufferedImage b) {
        BufferedImage imgA = normalizar(a, NORMALIZED_SIZE);
        BufferedImage imgB = normalizar(b, NORMALIZED_SIZE);

        double similitudPixeles = calcularSimilitudPixeles(imgA, imgB);
        double similitudHash = calcularSimilitudHash(imgA, imgB);

        return redondear4((similitudPixeles * 0.55) + (similitudHash * 0.45));
    }

    private BufferedImage normalizar(BufferedImage original, int lado) {
        BufferedImage escalada = new BufferedImage(lado, lado, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(original, 0, 0, lado, lado, null);
        g2.dispose();

        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(escalada, escalada);
        return escalada;
    }

    private double calcularSimilitudPixeles(BufferedImage a, BufferedImage b) {
        int w = Math.min(a.getWidth(), b.getWidth());
        int h = Math.min(a.getHeight(), b.getHeight());
        double diff = 0.0;
        double total = w * h * 255.0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int va = a.getRaster().getSample(x, y, 0);
                int vb = b.getRaster().getSample(x, y, 0);
                diff += Math.abs(va - vb);
            }
        }
        double similitud = 1.0 - (diff / total);
        if (similitud < 0) return 0;
        if (similitud > 1) return 1;
        return similitud;
    }

    private double calcularSimilitudHash(BufferedImage a, BufferedImage b) {
        boolean[] hashA = hashPromedio(a);
        boolean[] hashB = hashPromedio(b);
        int distintos = 0;

        for (int i = 0; i < hashA.length; i++) {
            if (hashA[i] != hashB[i]) distintos++;
        }

        return 1.0 - (distintos / (double) hashA.length);
    }

    private boolean[] hashPromedio(BufferedImage imagen) {
        BufferedImage reducida = normalizar(imagen, HASH_SIZE);
        boolean[] hash = new boolean[HASH_SIZE * HASH_SIZE];
        double suma = 0.0;

        for (int y = 0; y < HASH_SIZE; y++) {
            for (int x = 0; x < HASH_SIZE; x++) {
                suma += reducida.getRaster().getSample(x, y, 0);
            }
        }

        double promedio = suma / hash.length;
        int idx = 0;
        for (int y = 0; y < HASH_SIZE; y++) {
            for (int x = 0; x < HASH_SIZE; x++) {
                hash[idx++] = reducida.getRaster().getSample(x, y, 0) >= promedio;
            }
        }
        return hash;
    }

    private double redondear4(double valor) {
        return Math.round(valor * 10000.0) / 10000.0;
    }
}
