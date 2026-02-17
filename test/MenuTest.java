import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {
    
    @Test
    void testConstructorYGetters() {
        // Given
        String dia = "Lunes";
        String nombre = "Pollo al Curry";
        String tipo = "Plato principal";
        String descripcion = "Pollo condimentado con especias";
        String proteinas = "35-45 g";
        String carbohidratos = "50-65 g";
        String calorias = "550-700 kcal";
        double precio = 8.50;
        
        // When
        Menu menu = new Menu(dia, nombre, tipo, descripcion, 
                           proteinas, carbohidratos, calorias, precio);
        
        // Then
        assertEquals(dia, menu.getDia());
        assertEquals(nombre, menu.getNombre());
        assertEquals(tipo, menu.getTipo());
        assertEquals(descripcion, menu.getDescripcion());
        assertEquals(proteinas, menu.getProteinas());
        assertEquals(carbohidratos, menu.getCarbohidratos());
        assertEquals(calorias, menu.getCalorias());
        assertEquals(precio, menu.getPrecio(), 0.001);
    }
    
    @Test
    void testSetters() {
        // Given
        Menu menu = new Menu("Lunes", "Pollo", "Principal", 
                           "Descripción", "30 g", "40 g", "500 kcal", 8.0);
        
        // When
        menu.setDia("Martes");
        menu.setNombre("Pescado");
        menu.setTipo("Plato principal");
        menu.setDescripcion("Pescado a la plancha");
        menu.setProteinas("25 g");
        menu.setCarbohidratos("20 g");
        menu.setCalorias("400 kcal");
        menu.setPrecio(9.50);
        
        // Then
        assertEquals("Martes", menu.getDia());
        assertEquals("Pescado", menu.getNombre());
        assertEquals("Plato principal", menu.getTipo());
        assertEquals("Pescado a la plancha", menu.getDescripcion());
        assertEquals("25 g", menu.getProteinas());
        assertEquals("20 g", menu.getCarbohidratos());
        assertEquals("400 kcal", menu.getCalorias());
        assertEquals(9.50, menu.getPrecio(), 0.001);
    }
    
    @Test
    void testToString() {
        // Given
        Menu menu = new Menu("Lunes", "Pollo al Curry", "Plato principal", 
                           "Pollo condimentado", "35 g", "50 g", "550 kcal", 8.50);
        
        // When
        String resultado = menu.toString();
        
        // Then
        String esperado = "Lunes|Pollo al Curry|Plato principal|Pollo condimentado|35 g|50 g|550 kcal|8.5";
        assertEquals(esperado, resultado);
    }
    
    @Test
    void testFromString() {
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
}