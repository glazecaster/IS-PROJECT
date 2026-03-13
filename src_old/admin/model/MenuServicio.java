package admin.model;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuServicio {
    private String nombreArchivo = "data/base_datos_menus.txt";
    private List<Menu> menus = new ArrayList<>();
    private int nextId = 1;
    private IngredienteServicio ingServicio;

    public MenuServicio() {
        this.ingServicio = new IngredienteServicio();
        cargarMenus();
        
        if (menus.isEmpty()) {
            System.out.println("No se encontraron menús en el archivo. Creando menús de ejemplo...");
            crearMenusEjemplo();
        } else {
            System.out.println("Menús cargados desde archivo: " + menus.size() + " registros");
        }
    }

    private void cargarMenus() {
        menus.clear();
        File archivo = new File(nombreArchivo);
        
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                int lineasLeidas = 0;
                int exitosos = 0;
                int errores = 0;
                
                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Menu menu = Menu.fromString(linea, ingServicio.getIngredientes());
                        if (menu != null) {
                            menus.add(menu);
                            if (menu.getId() >= nextId) {
                                nextId = menu.getId() + 1;
                            }
                            exitosos++;
                        } else {
                            errores++;
                        }
                        lineasLeidas++;
                    }
                }
                System.out.println("Líneas procesadas: " + lineasLeidas + " (Exitosos: " + exitosos + ", Errores: " + errores + ")");
            } catch (IOException e) {
                System.err.println("Error al cargar menús: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Archivo no encontrado: " + nombreArchivo);
        }
    }

    public void guardarMenu(Menu menu) {
        if (menu.getId() == 0) {
            menu.setId(nextId++);
        }
        menus.add(menu);
        guardarTodosMenus();
    }

    public void actualizarMenu(int indice, Menu menu) {
        if (indice >= 0 && indice < menus.size()) {
            menus.set(indice, menu);
            guardarTodosMenus();
        }
    }

    public void eliminarMenu(int indice) {
        if (indice >= 0 && indice < menus.size()) {
            menus.remove(indice);
            guardarTodosMenus();
        }
    }

    public List<Menu> getMenus() {
        return new ArrayList<>(menus);
    }
    
    public Menu getMenuPorId(int id) {
        for (Menu menu : menus) {
            if (menu.getId() == id) {
                return menu;
            }
        }
        return null;
    }
    
    public List<Menu> getMenusPorDia(String dia) {
        List<Menu> menusDia = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getDia() != null && menu.getDia().equalsIgnoreCase(dia)) {
                menusDia.add(menu);
            }
        }
        return menusDia;
    }
    
    public List<Menu> getMenusPorServicio(String tipoServicio) {
        List<Menu> menusServicio = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getTipoServicio() != null && 
                menu.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                menusServicio.add(menu);
            }
        }
        return menusServicio;
    }
    
    public List<Menu> getMenusPorDiaYServicio(String dia, String tipoServicio) {
        List<Menu> menusFiltrados = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getDia() != null && menu.getTipoServicio() != null &&
                menu.getDia().equalsIgnoreCase(dia) && 
                menu.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                menusFiltrados.add(menu);
            }
        }
        return menusFiltrados;
    }

    private void guardarTodosMenus() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Menu menu : menus) {
                writer.write(menu.toString());
                writer.newLine();
            }
            System.out.println("Menús guardados en archivo: " + menus.size());
        } catch (IOException e) {
            System.err.println("Error al guardar menús: " + e.getMessage());
        }
    }

    private void crearMenusEjemplo() {
        IngredienteServicio ingServ = new IngredienteServicio();
        List<Ingrediente> ingredientes = ingServ.getIngredientes();
        
        
        Menu menu1 = new Menu(nextId++, "Lunes", "Desayuno Continental", "Desayuno", "Desayuno", 
            "Café, jugo de naranja, pan tostado con mermelada y mantequilla", 
            "8-10 g", "50-60 g", "350-400 kcal", 4.50);
        
        
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().contains("Huevos")) menu1.agregarIngrediente(ing, 2);
            if (ing.getNombre().contains("Harina")) menu1.agregarIngrediente(ing, 0.2);
            if (ing.getNombre().contains("Aceite")) menu1.agregarIngrediente(ing, 0.05);
        }
        menus.add(menu1);
        
        Menu menu2 = new Menu(nextId++, "Lunes", "Arepas con Queso", "Desayuno", "Desayuno", 
            "Arepas de maíz rellenas con queso blanco", 
            "15-20 g", "45-50 g", "380-420 kcal", 3.50);
        
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().contains("Harina")) menu2.agregarIngrediente(ing, 0.15);
            if (ing.getNombre().contains("Queso")) menu2.agregarIngrediente(ing, 0.1);
            if (ing.getNombre().contains("Sal")) menu2.agregarIngrediente(ing, 0.01);
        }
        menus.add(menu2);
        
        
        Menu menu3 = new Menu(nextId++, "Lunes", "Pollo al Curry", "Plato principal", "Almuerzo", 
            "Pollo condimentado con especias y cocinado a la plancha", 
            "35-45 g", "50-65 g", "550-700 kcal", 8.50);
        
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().contains("Pollo")) menu3.agregarIngrediente(ing, 0.25);
            if (ing.getNombre().contains("Arroz")) menu3.agregarIngrediente(ing, 0.2);
            if (ing.getNombre().contains("Cebolla")) menu3.agregarIngrediente(ing, 0.05);
        }
        menus.add(menu3);
        
        Menu menu4 = new Menu(nextId++, "Lunes", "Arroz Blanco", "Acompañante", "Almuerzo", 
            "Arroz blanco cocido al vapor", 
            "4.3 g", "45 g", "205-215 kcal", 1.50);
        
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().contains("Arroz")) menu4.agregarIngrediente(ing, 0.15);
            if (ing.getNombre().contains("Sal")) menu4.agregarIngrediente(ing, 0.005);
            if (ing.getNombre().contains("Aceite")) menu4.agregarIngrediente(ing, 0.01);
        }
        menus.add(menu4);

        guardarTodosMenus();
        System.out.println("Menús de ejemplo creados: " + menus.size());
    }
}