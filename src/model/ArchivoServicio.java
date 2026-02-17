package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoServicio {
    private String nombreArchivo = "test/base_datos_comedor.txt";
    private List<String> registros = new ArrayList<>();

    public ArchivoServicio() {
        cargarRegistros();
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

    public void guardarCosto(String linea) {
        registros.add(linea);
        guardarTodosRegistros();
    }

    public void actualizarCosto(int indice, String nuevaLinea) {
        if (indice >= 0 && indice < registros.size()) {
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
    
    public Object[] parseLineaATabla(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length >= 7) {
            return new Object[]{
                partes[0], partes[1], partes[2], partes[3], 
                partes[4], partes[5], partes[6]
            };
        }
        return null;
    }
}