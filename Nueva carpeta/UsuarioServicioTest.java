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
        Files.writeString(tmpFile, "");
        servicio = new UsuarioServicio(tmpFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tmpFile);
    }

    @Test
    void testRegistrarYExisteCedula() throws IOException {
        servicio.registrar("123", "clave", 20.0, "E");

        assertTrue(servicio.existeCedula("123"));
        assertFalse(servicio.existeCedula("999"));
    }

    @Test
    void testAutenticar_DiferenciaClaveMalaVsUsuarioInexistente() throws IOException {
        servicio.registrar("123", "claveOK", 10.0, "E");

        UsuarioServicio.UsuarioRecord ok = servicio.autenticar("123", "claveOK");
        UsuarioServicio.UsuarioRecord badPass = servicio.autenticar("123", "mala");
        UsuarioServicio.UsuarioRecord notFound = servicio.autenticar("999", "x");

        assertNotNull(ok);
        assertEquals("123", ok.cedula);
        assertEquals("E", ok.tipo);

        assertNotNull(badPass);
        assertEquals("123", badPass.cedula);
        assertEquals("", badPass.tipo);

        assertNull(notFound);
    }

    @Test
    void testActualizarSaldo_PersisteNuevoSaldo() throws IOException {
        servicio.registrar("123", "clave", 10.0, "E");

        boolean ok = servicio.actualizarSaldo("123", 55.5);
        UsuarioServicio.UsuarioRecord rec = servicio.findByCedula("123");

        assertTrue(ok);
        assertNotNull(rec);
        assertEquals(55.5, rec.saldo, 0.001);
    }

    @Test
    void testActualizarSaldo_FallaSiCedulaNoExiste() throws IOException {
        boolean ok = servicio.actualizarSaldo("999", 50.0);

        assertFalse(ok);
    }

    @Test
    void testAutenticarConReferencia_CredencialesCorrectas() throws IOException {
        servicio.registrar("5000001", "claveOK", 10.0, "E", "1234567890", 100.0);

        UsuarioServicio.UsuarioRecord ok = servicio.autenticar("5000001", "claveOK", "1234567890");

        assertNotNull(ok);
        assertEquals("1234567890", ok.numeroReferencia);
        assertEquals("E", ok.tipo);
    }

    @Test
    void testActualizarBeneficioEstudiante_PasaABecario() throws IOException {
        servicio.registrar("5000002", "clave", 20.0, "E", "1234567891", 100.0);

        boolean ok = servicio.actualizarBeneficioEstudiante("5000002", "B", 5.0);
        UsuarioServicio.UsuarioRecord rec = servicio.findByCedula("5000002");

        assertTrue(ok);
        assertNotNull(rec);
        assertEquals("B", rec.tipo);
        assertEquals(5.0, rec.porcentajeEspecial, 0.001);
    }

    @Test
    void testNumeroReferenciaValido_CubreErroresDeTipeo() {
        assertTrue(UsuarioServicio.esNumeroReferenciaValido("1234567890"));
        assertFalse(UsuarioServicio.esNumeroReferenciaValido("12345"));
        assertFalse(UsuarioServicio.esNumeroReferenciaValido("123456789A"));
        assertFalse(UsuarioServicio.esNumeroReferenciaValido("12 34567890"));
        assertFalse(UsuarioServicio.esNumeroReferenciaValido(""));
    }

    @Test
    void testGenerarNumeroReferenciaUnico_GeneraDosReferenciasDistintasDe10Digitos() throws IOException {
        String referencia1 = servicio.generarNumeroReferenciaUnico();
        servicio.registrar("5000003", "claveOK", 10.0, "E", referencia1, 100.0);
        String referencia2 = servicio.generarNumeroReferenciaUnico();

        assertEquals(10, referencia1.length());
        assertEquals(10, referencia2.length());
        assertTrue(referencia1.matches("\\d{10}"));
        assertTrue(referencia2.matches("\\d{10}"));
        assertNotEquals(referencia1, referencia2);
    }

    @Test
    void testCalcularCargoAccesoRegular_DescuentaSeisDolares() {
        UsuarioServicio.UsuarioRecord regular = new UsuarioServicio.UsuarioRecord(
                "5000004", "clave", 20.0, "E", "1234567892", 100.0);

        double cargo = UsuarioServicio.calcularCargoAcceso(regular, 6.0);

        assertEquals(6.0, cargo, 0.001);
    }
}
