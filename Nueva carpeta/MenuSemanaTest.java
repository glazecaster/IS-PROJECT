import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

import admin.model.MenuSemana;

public class MenuSemanaTest {

    private String backup;
    private Path ruta;

    @BeforeEach
    void backupArchivo() throws IOException {
        ruta = Path.of("test", "base_datos_menu_semana.txt");
        if (Files.exists(ruta)) {
            backup = Files.readString(ruta);
        } else {
            backup = null;
            Files.createDirectories(ruta.getParent());
            Files.writeString(ruta, "");
        }
    }

    @AfterEach
    void restoreArchivo() throws IOException {
        if (backup == null) {
            Files.writeString(ruta, "");
        } else {
            Files.writeString(ruta, backup);
        }
    }

    @Test
    void testSetDesayuno_GuardaYRecargaSeleccion() {
        MenuSemana ms = new MenuSemana();
        ms.limpiarTodo();

        ms.setDesayuno("Lunes", MenuSemana.COMP_BEBIDA, 123);

        MenuSemana recargado = new MenuSemana();
        Integer id = recargado.getDesayuno("Lunes", MenuSemana.COMP_BEBIDA);
        assertNotNull(id);
        assertEquals(123, id.intValue());
    }
}
