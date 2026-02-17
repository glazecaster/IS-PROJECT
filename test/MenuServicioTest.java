import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuServicioTest {
    private MenuServicio menuServicio;
    private static final String TEST_FILE = "test_base_datos_menus.txt";
    
    @BeforeEach
    void setUp() {
        menuServicio = new MenuServicio();
        // Cambiar el nombre del archivo para pruebas
        try {
            java.lang.reflect.Field field = MenuServicio.class.getDeclaredField("nombreArchivo");
            field.setAccessible(true);
            field.set(menuServicio, TEST_FILE);
            
            // Limpiar lista de menús
            java.lang.reflect.Field menusField = MenuServicio.class.getDeclaredField("menus");
            menusField.setAccessible(true);
            menusField.set(menuServicio, new java.util.ArrayList<>());
            
        } catch (Exception e) {
            fail("No se pudo configurar para pruebas: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Limpiar archivo de prueba
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }
    
    @Test
    void testGuardarYRecuperarMenu() {
        // Given
        Menu menu = new Menu("Lunes", "Pollo al Curry", "Plato principal", 
            "Pollo condimentado", "35 g", "50 g", "550 kcal", 8.50);
        
        // When
        menuServicio.guardarMenu(menu);
        List<Menu> menus = menuServicio.getMenus();
        
        // Then
        assertEquals(1, menus.size());
        Menu menuRecuperado = menus.get(0);
        assertEquals("Lunes", menuRecuperado.getDia());
        assertEquals("Pollo al Curry", menuRecuperado.getNombre());
        assertEquals("Plato principal", menuRecuperado.getTipo());
        assertEquals("Pollo condimentado", menuRecuperado.getDescripcion());
        assertEquals(8.50, menuRecuperado.getPrecio(), 0.001);
    }
    
    @Test
    void testActualizarMenu() {
        // Given
        Menu menuOriginal = new Menu("Lunes", "Pollo", "Principal", 
            "Descripción", "30 g", "40 g", "500 kcal", 8.0);
        Menu menuActualizado = new Menu("Lunes", "Pollo al Curry", "Plato principal", 
            "Pollo condimentado", "35 g", "50 g", "550 kcal", 8.50);
        
        menuServicio.guardarMenu(menuOriginal);
        
        // When
        menuServicio.actualizarMenu(0, menuActualizado);
        List<Menu> menus = menuServicio.getMenus();
        
        // Then
        assertEquals(1, menus.size());
        Menu menuRecuperado = menus.get(0);
        assertEquals("Pollo al Curry", menuRecuperado.getNombre());
        assertEquals(8.50, menuRecuperado.getPrecio(), 0.001);
    }
    
    @Test
    void testEliminarMenu() {
        // Given
        Menu menu1 = new Menu("Lunes", "Pollo", "Principal", 
            "Descripción", "30 g", "40 g", "500 kcal", 8.0);
        Menu menu2 = new Menu("Martes", "Pescado", "Principal", 
            "Descripción", "25 g", "30 g", "400 kcal", 9.0);
        
        menuServicio.guardarMenu(menu1);
        menuServicio.guardarMenu(menu2);
        
        // When
        menuServicio.eliminarMenu(0);
        List<Menu> menus = menuServicio.getMenus();
        
        // Then
        assertEquals(1, menus.size());
        assertEquals("Pescado", menus.get(0).getNombre());
    }
    
    @Test
    void testGetMenusPorDia() {
        // Given
        Menu menuLunes1 = new Menu("Lunes", "Pollo", "Principal", 
            "Descripción", "30 g", "40 g", "500 kcal", 8.0);
        Menu menuLunes2 = new Menu("Lunes", "Arroz", "Acompañante", 
            "Descripción", "5 g", "45 g", "200 kcal", 2.0);
        Menu menuMartes = new Menu("Martes", "Pescado", "Principal", 
            "Descripción", "25 g", "30 g", "400 kcal", 9.0);
        
        menuServicio.guardarMenu(menuLunes1);
        menuServicio.guardarMenu(menuLunes2);
        menuServicio.guardarMenu(menuMartes);
        
        // When
        List<Menu> menusLunes = menuServicio.getMenusPorDia("Lunes");
        
        // Then
        assertEquals(2, menusLunes.size());
        assertEquals("Pollo", menusLunes.get(0).getNombre());
        assertEquals("Arroz", menusLunes.get(1).getNombre());
    }
    
    @Test
    void testFromStringValido() {
        // Given
        String linea = "Lunes|Pollo al Curry|Plato principal|Pollo condimentado|35 g|50 g|550 kcal|8.50";
        
        // When
        Menu menu = Menu.fromString(linea);
        
        // Then
        assertNotNull(menu);
        assertEquals("Lunes", menu.getDia());
        assertEquals("Pollo al Curry", menu.getNombre());
        assertEquals("Plato principal", menu.getTipo());
        assertEquals("Pollo condimentado", menu.getDescripcion());
        assertEquals("35 g", menu.getProteinas());
        assertEquals("50 g", menu.getCarbohidratos());
        assertEquals("550 kcal", menu.getCalorias());
        assertEquals(8.50, menu.getPrecio(), 0.001);
    }
    
    @Test
    void testFromStringInvalido() {
        // Given
        String linea = "Linea|Incompleta";
        
        // When
        Menu menu = Menu.fromString(linea);
        
        // Then
        assertNull(menu);
    }
    
    @Test
    void testFromStringPrecioInvalido() {
        // Given
        String linea = "Lunes|Pollo|Principal|Desc|30 g|40 g|500 kcal|no_es_numero";
        
        // When
        Menu menu = Menu.fromString(linea);
        
        // Then
        assertNull(menu);
    }
}