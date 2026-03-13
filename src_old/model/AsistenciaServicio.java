package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AsistenciaServicio {

    public static class RegistroAsistencia {
        public final String fechaHora;
        public final String servicio;
        public final String cedula;
        public final String tipo;

        public RegistroAsistencia(String fechaHora, String servicio, String cedula, String tipo) {
            this.fechaHora = fechaHora;
            this.servicio = servicio;
            this.cedula = cedula;
            this.tipo = tipo;
        }
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final File archivo;

    public AsistenciaServicio() {
        this("data/asistencias.txt");
    }

    public AsistenciaServicio(String rutaArchivo) {
        this.archivo = new File(rutaArchivo);
    }

    public void registrarAsistencia(String cedula, String tipoCodigo, String servicio) {
        if (cedula == null || cedula.trim().isEmpty()) return;
        if (servicio == null || servicio.trim().isEmpty()) return;

        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }

        String tipo = UsuarioServicio.descripcionTipoParaReporte(tipoCodigo);
        String fechaHora = LocalDateTime.now().format(FMT);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(fechaHora + "|" + servicio.trim() + "|" + cedula.trim() + "|" + tipo);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("No se pudo registrar la asistencia: " + e.getMessage());
        }
    }

    public List<RegistroAsistencia> listarPorServicio(String servicio) {
        List<RegistroAsistencia> lista = new ArrayList<>();
        if (!archivo.exists()) return lista;

        String servicioBuscado = servicio == null ? "" : servicio.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                String raw = line.trim();
                if (raw.isEmpty()) continue;
                String[] arr = raw.split("\\|");
                if (arr.length < 4) continue;

                String servicioLinea = arr[1].trim();
                if (!servicioBuscado.isEmpty() && !servicioLinea.equalsIgnoreCase(servicioBuscado)) {
                    continue;
                }

                lista.add(new RegistroAsistencia(arr[0].trim(), servicioLinea, arr[2].trim(), arr[3].trim()));
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer la asistencia: " + e.getMessage());
        }

        return lista;
    }

    public Map<String, Integer> contarPorTipo(String servicio) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        conteo.put("Regular", 0);
        conteo.put("Becario", 0);
        conteo.put("Exonerado", 0);
        conteo.put("Otros", 0);

        for (RegistroAsistencia reg : listarPorServicio(servicio)) {
            String tipo = reg.tipo == null ? "" : reg.tipo.trim();
            if (conteo.containsKey(tipo)) {
                conteo.put(tipo, conteo.get(tipo) + 1);
            } else {
                conteo.put("Otros", conteo.get("Otros") + 1);
            }
        }

        return conteo;
    }
}
