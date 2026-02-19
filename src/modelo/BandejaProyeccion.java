// Archivo: modelo/BandejaProyeccion.java
package modelo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BandejaProyeccion {
    private Map<String, Integer> proyeccionesDesayuno;
    private Map<String, Integer> proyeccionesAlmuerzo;
    private String nombreArchivo = "src/test/base_datos_proyecciones.txt";
    
    // Porcentaje de merma por defecto
    private double porcentajeMerma = 5.0; // 5% por defecto
    
    public BandejaProyeccion() {
        proyeccionesDesayuno = new HashMap<>();
        proyeccionesAlmuerzo = new HashMap<>();
        cargarProyecciones();
    }
    
    public void setProyeccionDesayuno(String dia, int cantidad) {
        if (cantidad >= 0) {
            proyeccionesDesayuno.put(dia, cantidad);
            guardarProyecciones();
        }
    }
    
    public void setProyeccionAlmuerzo(String dia, int cantidad) {
        if (cantidad >= 0) {
            proyeccionesAlmuerzo.put(dia, cantidad);
            guardarProyecciones();
        }
    }
    
    public Integer getProyeccionDesayuno(String dia) {
        return proyeccionesDesayuno.getOrDefault(dia, 0);
    }
    
    public Integer getProyeccionAlmuerzo(String dia) {
        return proyeccionesAlmuerzo.getOrDefault(dia, 0);
    }
    
    public int getTotalBandejasSemana() {
        int total = 0;
        for (int cantidad : proyeccionesDesayuno.values()) {
            total += cantidad;
        }
        for (int cantidad : proyeccionesAlmuerzo.values()) {
            total += cantidad;
        }
        return total;
    }
    
    public int getTotalBandejasServicio(String tipoServicio) {
        int total = 0;
        if ("Desayuno".equalsIgnoreCase(tipoServicio)) {
            for (int cantidad : proyeccionesDesayuno.values()) {
                total += cantidad;
            }
        } else if ("Almuerzo".equalsIgnoreCase(tipoServicio)) {
            for (int cantidad : proyeccionesAlmuerzo.values()) {
                total += cantidad;
            }
        }
        return total;
    }
    
    public int getTotalBandejasDia(String dia) {
        return proyeccionesDesayuno.getOrDefault(dia, 0) + 
               proyeccionesAlmuerzo.getOrDefault(dia, 0);
    }
    
    public double getPorcentajeMerma() {
        return porcentajeMerma;
    }
    
    public void setPorcentajeMerma(double porcentajeMerma) {
        if (porcentajeMerma >= 0 && porcentajeMerma <= 100) {
            this.porcentajeMerma = porcentajeMerma;
            guardarProyecciones();
        }
    }
    
    public Map<String, Integer> getProyeccionesDesayuno() {
        return new HashMap<>(proyeccionesDesayuno);
    }
    
    public Map<String, Integer> getProyeccionesAlmuerzo() {
        return new HashMap<>(proyeccionesAlmuerzo);
    }
    
    public void limpiarTodo() {
        proyeccionesDesayuno.clear();
        proyeccionesAlmuerzo.clear();
        porcentajeMerma = 5.0;
        guardarProyecciones();
    }
    
    private void guardarProyecciones() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write("MERMA=" + porcentajeMerma);
            writer.newLine();
            
            writer.write("[DESAYUNOS]");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : proyeccionesDesayuno.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
            
            writer.write("[ALMUERZOS]");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : proyeccionesAlmuerzo.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar proyecciones: " + e.getMessage());
        }
    }
    
    private void cargarProyecciones() {
        proyeccionesDesayuno.clear();
        proyeccionesAlmuerzo.clear();
        
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            // Valores por defecto
            porcentajeMerma = 5.0;
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                if (linea.startsWith("MERMA=")) {
                    try {
                        porcentajeMerma = Double.parseDouble(linea.substring(6));
                    } catch (NumberFormatException e) {
                        porcentajeMerma = 5.0;
                    }
                } else if (linea.equals("[DESAYUNOS]")) {
                    seccionActual = "DESAYUNOS";
                } else if (linea.equals("[ALMUERZOS]")) {
                    seccionActual = "ALMUERZOS";
                } else {
                    String[] partes = linea.split("\\|");
                    if (partes.length == 2) {
                        String dia = partes[0];
                        int cantidad = Integer.parseInt(partes[1]);
                        
                        if (seccionActual.equals("DESAYUNOS")) {
                            proyeccionesDesayuno.put(dia, cantidad);
                        } else if (seccionActual.equals("ALMUERZOS")) {
                            proyeccionesAlmuerzo.put(dia, cantidad);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar proyecciones: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear número en proyecciones: " + e.getMessage());
        }
    }
}