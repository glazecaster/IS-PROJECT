package admin.model;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoServicio {
    private String nombreArchivo = "data/base_datos_costos.txt";
    private List<String> registros = new ArrayList<>();

    public ArchivoServicio() {
        detectarArchivoDisponible();
        cargarRegistros();
    }

    private void detectarArchivoDisponible() {
        String[] candidatos = new String[] {
            "data/base_datos_costos.txt",
            "data/base_datos_comedor.txt",
            "test/base_datos_costos.txt",
            "base_datos_costos.txt",
            "base_datos_comedor.txt",
            "test/base_datos_comedor.txt"
        };

        for (String path : candidatos) {
            File f = new File(path);
            if (f.exists() && f.isFile() && f.length() > 0) {
                nombreArchivo = path;
                return;
            }
        }

        for (String path : candidatos) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                nombreArchivo = path;
                return;
            }
        }
    }

    private void cargarRegistros() {
        registros.clear();
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        registros.add(linea);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al cargar registros: " + e.getMessage());
            }
        }
    }

    public void recargar() {
        detectarArchivoDisponible();
        cargarRegistros();
    }

    public void guardarCosto(String linea) {
        if (validarRegistro(linea)) {
            registros.add(linea);
            guardarTodosRegistros();
        }
    }

    public void actualizarCosto(int indice, String nuevaLinea) {
        if (indice >= 0 && indice < registros.size() && validarRegistro(nuevaLinea)) {
            registros.set(indice, nuevaLinea);
            guardarTodosRegistros();
        }
    }

    public void eliminarCosto(int indice) {
        if (indice >= 0 && indice < registros.size()) {
            registros.remove(indice);
            guardarTodosRegistros();
        }
    }

    public List<String> getRegistros() {
        return new ArrayList<>(registros);
    }
    
    public double getTotalCostosFijos() {
        double total = 0;
        for (String registro : registros) {
            String[] partes = registro.split("\\|");
            if (partes.length >= 4 && "Costo Fijo".equals(partes[0])) {
                try {
                    total += parseMonto(partes[3]);
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        return total;
    }
    
    public double getTotalCostosVariables() {
        double total = 0;
        for (String registro : registros) {
            String[] partes = registro.split("\\|");
            if (partes.length >= 4 && "Costo Variable".equals(partes[0])) {
                try {
                    total += parseMonto(partes[3]);
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        return total;
    }

    private double parseMonto(String raw) throws NumberFormatException {
        if (raw == null) throw new NumberFormatException("monto null");
        String s = raw.trim();
        s = s.replace("$", "").replace("USD", "").trim();
        s = s.replace(",", ".");
        return Double.parseDouble(s);
    }

    private void guardarTodosRegistros() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (String registro : registros) {
                writer.write(registro);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
    
    private boolean validarRegistro(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length < 4) {
            System.err.println("Error: Línea con menos de 4 partes: " + linea);
            return false;
        }
        
        try {
            parseMonto(partes[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error: El monto no es un número válido: " + partes[3]);
            return false;
        }
        
        return true;
    }
    
    public Object[] parseLineaATabla(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length < 4) return null;

        String tipo = partes[0];
        String dia = partes[1];
        String nombre = partes[2];
        String monto = partes[3];
        String periodo = (partes.length >= 5) ? partes[4] : inferirPeriodo(tipo);

        return new Object[]{ tipo, dia, nombre, monto, periodo };
    }

    private String inferirPeriodo(String tipo) {
        if ("Costo Fijo".equalsIgnoreCase(tipo)) return "1 meses";
        if ("Costo Variable".equalsIgnoreCase(tipo)) return "1 días";
        return "";
    }
}