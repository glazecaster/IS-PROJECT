package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuServicio {
    private String nombreArchivo = "test/base_datos_menus.txt";
    private List<Menu> menus = new ArrayList<>();

    public MenuServicio() {
        cargarMenus();
        if (menus.isEmpty()) {
            crearMenusEjemplo();
        }
    }

    private void cargarMenus() {
        menus.clear();
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        Menu menu = Menu.fromString(linea);
                        if (menu != null) {
                            menus.add(menu);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al cargar menús: " + e.getMessage());
            }
        }
    }

    public void guardarMenu(Menu menu) {
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
    
    public List<Menu> getMenusPorDia(String dia) {
        List<Menu> menusDia = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getDia().equalsIgnoreCase(dia)) {
                menusDia.add(menu);
            }
        }
        return menusDia;
    }

    private void guardarTodosMenus() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Menu menu : menus) {
                writer.write(menu.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar menús: " + e.getMessage());
        }
    }

    private void crearMenusEjemplo() {
        menus.add(new Menu("Lunes", "Pollo al Curry", "Plato principal", 
            "Pollo condimentado con especias y cocinado a la plancha", 
            "35-45 g", "50-65 g", "550-700 kcal", 8.50));
        menus.add(new Menu("Lunes", "Arroz Blanco", "Acompañante", 
            "Arroz blanco cocido al vapor", 
            "4.3 g", "45 g", "205-215 kcal", 1.50));
        menus.add(new Menu("Lunes", "Ensalada Mixta", "Ensalada", 
            "Ensalada a base de lechuga, tomate, cebolla y mayonesa", 
            "2.1 g", "9.5 g", "135 kcal", 2.00));

        menus.add(new Menu("Martes", "Lasaña de Carne", "Plato principal", 
            "Lasaña tradicional con carne molida y salsa bechamel", 
            "40-50 g", "60-70 g", "650-800 kcal", 9.00));
        menus.add(new Menu("Martes", "Puré de Papa", "Acompañante", 
            "Puré de papa cremoso con mantequilla", 
            "5.2 g", "35 g", "250 kcal", 2.00));
        menus.add(new Menu("Martes", "Sopa de Verduras", "Entrada", 
            "Sopa caliente con variedad de vegetales frescos", 
            "3.5 g", "20 g", "150 kcal", 2.50));

        menus.add(new Menu("Miércoles", "Pescado a la Plancha", "Plato principal", 
            "Filete de pescado fresco a la plancha con limón", 
            "45-55 g", "5-10 g", "400-500 kcal", 10.00));
        menus.add(new Menu("Miércoles", "Ensalada de Quinoa", "Acompañante", 
            "Quinoa con vegetales y aderezo de limón", 
            "8 g", "40 g", "280 kcal", 3.00));
        menus.add(new Menu("Miércoles", "Flan de Vainilla", "Postre", 
            "Flan casero de vainilla con caramelo", 
            "6 g", "30 g", "200 kcal", 2.50));

        menus.add(new Menu("Jueves", "Pasta Alfredo", "Plato principal", 
            "Pasta con salsa Alfredo y pollo", 
            "30-40 g", "70-80 g", "700-850 kcal", 8.50));
        menus.add(new Menu("Jueves", "Pan de Ajo", "Acompañante", 
            "Pan de ajo horneado con mantequilla y perejil", 
            "4 g", "25 g", "180 kcal", 1.80));
        menus.add(new Menu("Jueves", "Jugo Natural", "Bebida", 
            "Jugo natural de naranja o piña", 
            "0.5 g", "25 g", "120 kcal", 1.50));

        menus.add(new Menu("Viernes", "Hamburguesa Gourmet", "Plato principal", 
            "Hamburguesa con queso, lechuga, tomate y papas fritas", 
            "50-60 g", "80-90 g", "800-950 kcal", 11.00));
        menus.add(new Menu("Viernes", "Papas Fritas", "Acompañante", 
            "Papas fritas crujientes", 
            "4 g", "45 g", "350 kcal", 2.50));
        menus.add(new Menu("Viernes", "Helado", "Postre", 
            "Helado de vainilla o chocolate", 
            "3 g", "20 g", "150 kcal", 2.00));

        guardarTodosMenus();
    }
}