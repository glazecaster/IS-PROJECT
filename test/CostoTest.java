import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CostoTest {
    
    @Test
    void testConstructorYGetters() {
        // Given
        String tipo = "Costo Fijo";
        String concepto = "Alquiler";
        double monto = 1000.0;
        String periodo = "Fijo";
        
        // When
        Costo costo = new Costo(tipo, concepto, monto, periodo);
        
        // Then
        assertEquals(tipo, costo.getTipo());
        assertEquals(concepto, costo.getConcepto());
        assertEquals(monto, costo.getMonto(), 0.001);
        assertEquals(periodo, costo.getPeriodo());
    }
}