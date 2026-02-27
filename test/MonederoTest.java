import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import model.Monedero;

public class MonederoTest {

    @Test
    void testRecargar_AumentaSaldoCuandoMontoValido() {
        // Given
        Monedero m = new Monedero("123", 10.0);

        // When
        boolean ok = m.recargar(15.0);

        // Then
        assertTrue(ok);
        assertEquals(25.0, m.getSaldo(), 0.001);
    }

    @Test
    void testRecargar_FallaSiExcedeMaximo100() {
        // Given
        Monedero m = new Monedero("123", 95.0);

        // When
        boolean ok = m.recargar(10.0);

        // Then
        assertFalse(ok);
        assertEquals(95.0, m.getSaldo(), 0.001);
    }

    @Test
    void testRecargar_FallaSiMontoNoEsPositivo() {
        // Given
        Monedero m = new Monedero("123", 50.0);

        // When
        boolean okCero = m.recargar(0.0);
        boolean okNeg = m.recargar(-1.0);

        // Then
        assertFalse(okCero);
        assertFalse(okNeg);
        assertEquals(50.0, m.getSaldo(), 0.001);
    }

    @Test
    void testCobrar_DescuentaSiHaySaldoSuficiente() {
        // Given
        Monedero m = new Monedero("123", 10.0);

        // When
        boolean ok = m.cobrar(6.0);

        // Then
        assertTrue(ok);
        assertEquals(4.0, m.getSaldo(), 0.001);
    }

    @Test
    void testCobrar_FallaSiSaldoInsuficiente() {
        // Given
        Monedero m = new Monedero("123", 5.0);

        // When
        boolean ok = m.cobrar(6.0);

        // Then
        assertFalse(ok);
        assertEquals(5.0, m.getSaldo(), 0.001);
    }

    @Test
    void testCobrar_FallaSiMontoNoEsPositivo() {
        // Given
        Monedero m = new Monedero("123", 10.0);

        // When
        boolean okCero = m.cobrar(0.0);
        boolean okNeg = m.cobrar(-2.0);

        // Then
        assertFalse(okCero);
        assertFalse(okNeg);
        assertEquals(10.0, m.getSaldo(), 0.001);
    }
}
