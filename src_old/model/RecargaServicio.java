package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RecargaServicio {

    public static class RecargaRecord {
        public final String cedula;
        public final String codigoOperacion;
        public final double monto;
        public final boolean canjeada;

        public RecargaRecord(String cedula, String codigoOperacion, double monto, boolean canjeada) {
            this.cedula = cedula;
            this.codigoOperacion = codigoOperacion;
            this.monto = monto;
            this.canjeada = canjeada;
        }
    }

    private final File archivo;
    private final SecureRandom random = new SecureRandom();

    public RecargaServicio() {
        this("data/recargas.txt");
    }

    public RecargaServicio(String rutaArchivo) {
        this.archivo = new File(rutaArchivo);
        asegurarArchivo();
    }

    public String registrarRecarga(String cedulaDestino, double monto) throws IOException {
        String codigo = generarCodigoUnico();
        agregarRegistro(new RecargaRecord(cedulaDestino, codigo, redondear2(monto), false));
        return codigo;
    }

    public RecargaRecord buscarPorCodigo(String codigoOperacion) throws IOException {
        if (!esCodigoOperacionValido(codigoOperacion)) return null;
        for (RecargaRecord recarga : listarRecargas()) {
            if (recarga.codigoOperacion.equals(codigoOperacion.trim())) {
                return recarga;
            }
        }
        return null;
    }

    public double totalPendienteParaCedula(String cedula) throws IOException {
        double total = 0.0;
        for (RecargaRecord recarga : listarRecargas()) {
            if (recarga.cedula.equals(cedula) && !recarga.canjeada) {
                total += recarga.monto;
            }
        }
        return redondear2(total);
    }

    public boolean marcarComoCanjeada(String codigoOperacion) throws IOException {
        List<RecargaRecord> recargas = listarRecargas();
        boolean actualizado = false;
        for (int i = 0; i < recargas.size(); i++) {
            RecargaRecord recarga = recargas.get(i);
            if (recarga.codigoOperacion.equals(codigoOperacion)) {
                recargas.set(i, new RecargaRecord(recarga.cedula, recarga.codigoOperacion, recarga.monto, true));
                actualizado = true;
                break;
            }
        }
        if (!actualizado) return false;
        escribirTodas(recargas);
        return true;
    }

    public List<RecargaRecord> listarRecargas() throws IOException {
        asegurarArchivo();
        List<RecargaRecord> recargas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                RecargaRecord recarga = parseRecord(line);
                if (recarga != null) {
                    recargas.add(recarga);
                }
            }
        }
        return recargas;
    }

    public static boolean esCodigoOperacionValido(String codigoOperacion) {
        return codigoOperacion != null && codigoOperacion.trim().matches("\\d{10}");
    }

    private void agregarRegistro(RecargaRecord recarga) throws IOException {
        asegurarArchivo();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(construirLinea(recarga));
            bw.newLine();
        }
    }

    private String generarCodigoUnico() throws IOException {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder();
            sb.append(random.nextInt(9) + 1);
            for (int i = 1; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            codigo = sb.toString();
        } while (buscarPorCodigo(codigo) != null);
        return codigo;
    }

    private void escribirTodas(List<RecargaRecord> recargas) throws IOException {
        asegurarArchivo();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            for (RecargaRecord recarga : recargas) {
                bw.write(construirLinea(recarga));
                bw.newLine();
            }
        }
    }

    private String construirLinea(RecargaRecord recarga) {
        return recarga.cedula + "," + recarga.codigoOperacion + "," + redondear2(recarga.monto) + "," + recarga.canjeada;
    }

    private RecargaRecord parseRecord(String rawLine) {
        if (rawLine == null) return null;
        String line = rawLine.trim();
        if (line.isEmpty()) return null;
        String[] partes = rawLine.split(",", -1);
        if (partes.length < 4) return null;
        String cedula = partes[0].trim();
        String codigo = partes[1].trim();
        double monto;
        try {
            monto = Double.parseDouble(partes[2].trim());
        } catch (Exception ex) {
            return null;
        }
        boolean canjeada = Boolean.parseBoolean(partes[3].trim());
        return new RecargaRecord(cedula, codigo, monto, canjeada);
    }

    private void asegurarArchivo() {
        try {
            File padre = archivo.getParentFile();
            if (padre != null && !padre.exists()) padre.mkdirs();
            if (!archivo.exists()) archivo.createNewFile();
        } catch (IOException ignored) {
        }
    }

    private double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
