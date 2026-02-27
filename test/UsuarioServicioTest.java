import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

import model.UsuarioServicio;

public class UsuarioServicioTest {

    private Path tmpFile;
    private UsuarioServicio servicio;

    @BeforeEach
    void setUp() throws IOException {
        tmpFile = Files.createTempFile("usuarios-test-", ".csv");
        // empezamos vacío
        Files.writeString(tmpFile, "");
        servicio = new UsuarioServicio(tmpFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tmpFile);
    }

    @Test
    void testRegistrarYExisteCedula() throws IOException {
        // When
        servicio.registrar("123", "clave", 20.0, "E");

        // Then
        assertTrue(servicio.existeCedula("123"));
        assertFalse(servicio.existeCedula("999"));
    }

    @Test
    void testAutenticar_DiferenciaClaveMalaVsUsuarioInexistente() throws IOException {
        // Given
        servicio.registrar("123", "claveOK", 10.0, "E");

        // When
        UsuarioServicio.UsuarioRecord ok = servicio.autenticar("123", "claveOK");
        UsuarioServicio.UsuarioRecord badPass = servicio.autenticar("123", "mala");
        UsuarioServicio.UsuarioRecord notFound = servicio.autenticar("999", "x");

        // Then
        assertNotNull(ok);
        assertEquals("123", ok.cedula);
        assertEquals("E", ok.tipo);

        assertNotNull(badPass);
        assertEquals("123", badPass.cedula);
        assertEquals("", badPass.tipo, "Cuando la clave es incorrecta, el tipo debe venir vacío según la implementación");

        assertNull(notFound, "Si no existe la cédula, autenticar debe devolver null");
    }

    @Test
    void testActualizarSaldo_PersisteNuevoSaldo() throws IOException {
        // Given
        servicio.registrar("123", "clave", 10.0, "E");

        // When
        boolean ok = servicio.actualizarSaldo("123", 55.5);
        UsuarioServicio.UsuarioRecord rec = servicio.findByCedula("123");

        // Then
        assertTrue(ok, "Debe actualizar si la cédula existe");
        assertNotNull(rec);
        assertEquals(55.5, rec.saldo, 0.001);
    }

    @Test
    void testActualizarSaldo_FallaSiCedulaNoExiste() throws IOException {
        // When
        boolean ok = servicio.actualizarSaldo("999", 50.0);

        // Then
        assertFalse(ok, "No debe actualizar si la cédula no existe");
    }
}
