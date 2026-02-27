import org.junit.jupiter.api.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

import admin.model.Menu;

public class MenuTest {

    @Test
    void testConstructorGettersYSetters() {
        Menu menu = new Menu(
                1,
                "Lunes",
                "Pollo al Curry",
                "Plato principal",
                "Almuerzo",
                "Pollo condimentado con especias",
                "35-45 g",
                "50-65 g",
                "550-700 kcal",
                8.50
        );

        assertEquals(1, menu.getId());
        assertEquals("Lunes", menu.getDia());
        assertEquals("Pollo al Curry", menu.getNombre());
        assertEquals("Plato principal", menu.getTipo());
        assertEquals("Almuerzo", menu.getTipoServicio());
        assertEquals("Pollo condimentado con especias", menu.getDescripcion());
        assertEquals("35-45 g", menu.getProteinas());
        assertEquals("50-65 g", menu.getCarbohidratos());
        assertEquals("550-700 kcal", menu.getCalorias());
        assertEquals(8.50, menu.getPrecioVenta(), 0.001);
        assertTrue(menu.getIngredientes().isEmpty());
        assertTrue(menu.getCantidades().isEmpty());

        menu.setDia("Martes");
        menu.setNombre("Pescado a la plancha");
        menu.setTipoServicio("Almuerzo");
        menu.setPrecioVenta(9.25);

        assertEquals("Martes", menu.getDia());
        assertEquals("Pescado a la plancha", menu.getNombre());
        assertEquals("Almuerzo", menu.getTipoServicio());
        assertEquals(9.25, menu.getPrecioVenta(), 0.001);
    }

    @Test
    void testToStringYFromString_SinIngredientes() {
        Menu menu = new Menu(
                7,
                "Lunes",
                "Arepas con Queso",
                "Desayuno",
                "Desayuno",
                "Arepas de maíz rellenas con queso",
                "15-20 g",
                "45-50 g",
                "380-420 kcal",
                3.50
        );

        String linea = menu.toString();
        Menu reconstruido = Menu.fromString(linea, Collections.emptyList());

        assertNotNull(reconstruido);
        assertEquals(menu.getId(), reconstruido.getId());
        assertEquals(menu.getDia(), reconstruido.getDia());
        assertEquals(menu.getNombre(), reconstruido.getNombre());
        assertEquals(menu.getTipo(), reconstruido.getTipo());
        assertEquals(menu.getTipoServicio(), reconstruido.getTipoServicio());
        assertEquals(menu.getDescripcion(), reconstruido.getDescripcion());
        assertEquals(menu.getProteinas(), reconstruido.getProteinas());
        assertEquals(menu.getCarbohidratos(), reconstruido.getCarbohidratos());
        assertEquals(menu.getCalorias(), reconstruido.getCalorias());
        assertEquals(menu.getPrecioVenta(), reconstruido.getPrecioVenta(), 0.001);
        assertTrue(reconstruido.getIngredientes().isEmpty());
    }
}
