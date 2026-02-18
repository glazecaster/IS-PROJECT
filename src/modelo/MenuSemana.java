package modelo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MenuSemana {
    private Map<String, Integer> desayunos;
    private Map<String, Integer> almuerzos;
    private String nombreArchivo = "test/base_datos_menu_semana.txt";
    
    public MenuSemana() {
        desayunos = new HashMap<>();
        almuerzos = new HashMap<>();
        cargarSeleccion();
    }
    
    public void setDesayuno(String dia, int menuId) {
        desayunos.put(dia, menuId);
        guardarSeleccion();
    }
    
    public void setAlmuerzo(String dia, int menuId) {
        almuerzos.put(dia, menuId);
        guardarSeleccion();
    }
    
    public Integer getDesayuno(String dia) {
        return desayunos.get(dia);
    }
    
    public Integer getAlmuerzo(String dia) {
        return almuerzos.get(dia);
    }
    
    public void eliminarDesayuno(String dia) {
        desayunos.remove(dia);
        guardarSeleccion();
    }
    
    public void eliminarAlmuerzo(String dia) {
        almuerzos.remove(dia);
        guardarSeleccion();
    }
    
    public boolean hayDesayunoSeleccionado(String dia) {
        return desayunos.containsKey(dia);
    }
    
    public boolean hayAlmuerzoSeleccionado(String dia) {
        return almuerzos.containsKey(dia);
    }
    
    public Map<String, Integer> getDesayunos() {
        return new HashMap<>(desayunos);
    }
    
    public Map<String, Integer> getAlmuerzos() {
        return new HashMap<>(almuerzos);
    }
    
    public void limpiarTodo() {
        desayunos.clear();
        almuerzos.clear();
        guardarSeleccion();
    }
    
    private void guardarSeleccion() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            
            writer.write("[DESAYUNOS]");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : desayunos.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
            
            
            writer.write("[ALMUERZOS]");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : almuerzos.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar menú de la semana: " + e.getMessage());
        }
    }
    
    private void cargarSeleccion() {
        desayunos.clear();
        almuerzos.clear();
        
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                if (linea.equals("[DESAYUNOS]")) {
                    seccionActual = "DESAYUNOS";
                } else if (linea.equals("[ALMUERZOS]")) {
                    seccionActual = "ALMUERZOS";
                } else {
                    String[] partes = linea.split("\\|");
                    if (partes.length == 2) {
                        String dia = partes[0];
                        int menuId = Integer.parseInt(partes[1]);
                        
                        if (seccionActual.equals("DESAYUNOS")) {
                            desayunos.put(dia, menuId);
                        } else if (seccionActual.equals("ALMUERZOS")) {
                            almuerzos.put(dia, menuId);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar menú de la semana: " + e.getMessage());
        }
    }
}