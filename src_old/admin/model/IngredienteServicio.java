package admin.model;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteServicio {
    private String nombreArchivo = "data/base_datos_ingredientes.txt";
    private List<Ingrediente> ingredientes = new ArrayList<>();
    private int nextId = 1;

    public IngredienteServicio() {
        detectarArchivoDisponible();
        cargarIngredientes();
        if (ingredientes.isEmpty()) {
            crearIngredientesEjemplo();
        }
    }

    private void detectarArchivoDisponible() {
        String[] candidatos = new String[] {
            "data/base_datos_ingredientes.txt",
            "test/base_datos_ingredientes.txt",
            "base_datos_ingredientes.txt"
        };
        for (String p : candidatos) {
            File f = new File(p);
            if (f.exists() && f.isFile() && f.length() > 0) { nombreArchivo = p; return; }
        }
    }

    private void cargarIngredientes() {
        ingredientes.clear();
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        Ingrediente ing = Ingrediente.fromFileString(linea);
                        if (ing != null) {
                            ingredientes.add(ing);
                            if (ing.getId() >= nextId) {
                                nextId = ing.getId() + 1;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al cargar ingredientes: " + e.getMessage());
            }
        }
    }

    public void guardarIngrediente(Ingrediente ingrediente) {
        if (ingrediente.getId() == 0) {
            ingrediente.setId(nextId++);
        }
        ingredientes.add(ingrediente);
        guardarTodosIngredientes();
    }

    public void actualizarIngrediente(int indice, Ingrediente ingrediente) {
        if (indice >= 0 && indice < ingredientes.size()) {
            ingredientes.set(indice, ingrediente);
            guardarTodosIngredientes();
        }
    }

    public void eliminarIngrediente(int indice) {
        if (indice >= 0 && indice < ingredientes.size()) {
            ingredientes.remove(indice);
            guardarTodosIngredientes();
        }
    }

    public List<Ingrediente> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }
    
    public Ingrediente buscarPorNombre(String nombre) {
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().equalsIgnoreCase(nombre)) {
                return ing;
            }
        }
        return null;
    }
    
    public List<Ingrediente> getIngredientesPorCategoria(String categoria) {
        List<Ingrediente> filtrados = new ArrayList<>();
        for (Ingrediente ing : ingredientes) {
            if (ing.getCategoria().equalsIgnoreCase(categoria)) {
                filtrados.add(ing);
            }
        }
        return filtrados;
    }

    private void guardarTodosIngredientes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Ingrediente ing : ingredientes) {
                writer.write(ing.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar ingredientes: " + e.getMessage());
        }
    }

    private void crearIngredientesEjemplo() {
        ingredientes.add(new Ingrediente(nextId++, "Arroz", "kg", 2.50, 100, "Granos"));
        ingredientes.add(new Ingrediente(nextId++, "Pollo", "kg", 5.80, 50, "Carnes"));
        ingredientes.add(new Ingrediente(nextId++, "Tomate", "kg", 1.20, 30, "Verduras"));
        ingredientes.add(new Ingrediente(nextId++, "Cebolla", "kg", 0.90, 40, "Verduras"));
        ingredientes.add(new Ingrediente(nextId++, "Queso Blanco", "kg", 8.50, 20, "Lácteos"));
        ingredientes.add(new Ingrediente(nextId++, "Harina de Maíz", "kg", 1.80, 60, "Granos"));
        ingredientes.add(new Ingrediente(nextId++, "Aceite", "litro", 3.20, 30, "Aceites"));
        ingredientes.add(new Ingrediente(nextId++, "Sal", "kg", 0.50, 10, "Condimentos"));
        ingredientes.add(new Ingrediente(nextId++, "Papa", "kg", 1.10, 80, "Verduras"));
        ingredientes.add(new Ingrediente(nextId++, "Carne Molida", "kg", 7.50, 40, "Carnes"));
        ingredientes.add(new Ingrediente(nextId++, "Lechuga", "unidad", 0.80, 25, "Verduras"));
        ingredientes.add(new Ingrediente(nextId++, "Huevos", "unidad", 0.25, 200, "Lácteos"));
        
        guardarTodosIngredientes();
    }
}