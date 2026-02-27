import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import admin.model.Menu;
import admin.model.MenuServicio;

public class MenuServicioTest {

    @Test
    void testFiltroPorDiaYServicio() {
        MenuServicio servicio = new MenuServicio();

        List<Menu> almuerzosLunes = servicio.getMenusPorDiaYServicio("Lunes", "Almuerzo");

        assertNotNull(almuerzosLunes);
        assertFalse(almuerzosLunes.isEmpty(), "Se esperaba al menos un menú de Almuerzo para Lunes");
        for (Menu m : almuerzosLunes) {
            assertEquals("Lunes", m.getDia(), "El filtro por día debe devolver solo Lunes");
            assertEquals("Almuerzo", m.getTipoServicio(), "El filtro por servicio debe devolver solo Almuerzo");
        }
    }
}
