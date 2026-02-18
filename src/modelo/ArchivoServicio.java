package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoServicio {
    private String nombreArchivo = "src/test/base_datos_costos.txt";
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
                    total += Double.parseDouble(partes[3]);
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
                    total += Double.parseDouble(partes[3]);
                } catch (NumberFormatException e) {
                    
                }
            }
        }
        return total;
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
        if (partes.length < 7) {
            System.err.println("Error: Línea con menos de 7 partes: " + linea);
            return false;
        }
        
        
        try {
            Double.parseDouble(partes[3]);
        } catch (NumberFormatException e) {
            System.err.println("Error: El monto no es un número válido: " + partes[3]);
            return false;
        }
        
        
        String proteinas = partes[5].trim().toLowerCase();
        if (!proteinas.matches("^\\d+(\\.\\d+)?\\s*(-\\s*\\d+(\\.\\d+)?)?\\s*g$") && 
            !proteinas.equals("0g") && !proteinas.equals("0 g")) {
            System.err.println("Error: Formato inválido de proteínas: " + proteinas);
            return false;
        }
        
        
        String calorias = partes[6].trim().toLowerCase();
        if (!calorias.matches("^\\d+(\\.\\d+)?\\s*(-\\s*\\d+(\\.\\d+)?)?\\s*kcal$") && 
            !calorias.equals("0 kcal") && !calorias.equals("0kcal")) {
            System.err.println("Error: Formato inválido de calorías: " + calorias);
            return false;
        }
        
        return true;
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