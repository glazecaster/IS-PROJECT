import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArchivoServicioTest {
    private ArchivoServicio archivoServicio;
    private static final String TEST_FILE = "test_base_datos.txt";
    
    @BeforeEach
    void setUp() {
        archivoServicio = new ArchivoServicio();
        // Cambiar el nombre del archivo para pruebas
        try {
            java.lang.reflect.Field field = ArchivoServicio.class.getDeclaredField("nombreArchivo");
            field.setAccessible(true);
            field.set(archivoServicio, TEST_FILE);
        } catch (Exception e) {
            fail("No se pudo cambiar el nombre del archivo: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Limpiar archivo de prueba
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }
    
    @Test
    void testGuardarYRecuperarRegistros() {
        // Given
        String linea1 = "Costo Fijo|Lunes|Alquiler|1000.0|Fijo|0g|0 kcal";
        String linea2 = "Costo Variable|Martes|Verduras|200.0|7 días|0g|0 kcal";
        
        // When
        archivoServicio.guardarCosto(linea1);
        archivoServicio.guardarCosto(linea2);
        List<String> registros = archivoServicio.getRegistros();
        
        // Then
        assertEquals(2, registros.size());
        assertTrue(registros.contains(linea1));
        assertTrue(registros.contains(linea2));
    }
    
    @Test
    void testParseLineaATabla_LineaValida() {
        // Given
        String linea = "Costo Fijo|Lunes|Alquiler|1000.0|Fijo|0g|0 kcal";
        
        // When
        Object[] resultado = archivoServicio.parseLineaATabla(linea);
        
        // Then
        assertNotNull(resultado);
        assertEquals(7, resultado.length);
        assertEquals("Costo Fijo", resultado[0]);
        assertEquals("Lunes", resultado[1]);
        assertEquals("Alquiler", resultado[2]);
        assertEquals("1000.0", resultado[3]);
        assertEquals("Fijo", resultado[4]);
        assertEquals("0g", resultado[5]);
        assertEquals("0 kcal", resultado[6]);
    }
    
    @Test
    void testParseLineaATabla_LineaInvalida() {
        // Given
        String linea = "Linea|Incompleta";
        
        // When
        Object[] resultado = archivoServicio.parseLineaATabla(linea);
        
        // Then
        assertNull(resultado);
    }
    
    @Test
    void testActualizarCosto() {
        // Given
        String lineaOriginal = "Costo Fijo|Lunes|Alquiler|1000.0|Fijo|0g|0 kcal";
        String lineaNueva = "Costo Fijo|Lunes|Alquiler|1200.0|Fijo|0g|0 kcal";
        
        archivoServicio.guardarCosto(lineaOriginal);
        
        // When
        archivoServicio.actualizarCosto(0, lineaNueva);
        List<String> registros = archivoServicio.getRegistros();
        
        // Then
        assertEquals(1, registros.size());
        assertEquals(lineaNueva, registros.get(0));
    }
    
    @Test
    void testEliminarCosto() {
        // Given
        String linea1 = "Costo Fijo|Lunes|Alquiler|1000.0|Fijo|0g|0 kcal";
        String linea2 = "Costo Variable|Martes|Verduras|200.0|7 días|0g|0 kcal";
        
        archivoServicio.guardarCosto(linea1);
        archivoServicio.guardarCosto(linea2);
        
        // When
        archivoServicio.eliminarCosto(0);
        List<String> registros = archivoServicio.getRegistros();
        
        // Then
        assertEquals(1, registros.size());
        assertEquals(linea2, registros.get(0));
    }
    
    @Test
    void testArchivoNoExiste() {
        // Given - archivo no existe
        
        // When
        List<String> registros = archivoServicio.getRegistros();
        
        // Then - debería devolver lista vacía sin errores
        assertNotNull(registros);
        assertTrue(registros.isEmpty());
    }
}