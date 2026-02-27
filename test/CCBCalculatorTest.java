import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import admin.model.CCBCalculator;
import admin.model.Ingrediente;
import admin.model.Menu;

public class CCBCalculatorTest {

    @Test
    void testSetBandejasProyectadas_RespetaMaximo400() {
        CCBCalculator calc = new CCBCalculator();

        calc.setBandejasProyectadas(10, 999);

        assertEquals(400, calc.getBandejasProyectadas(10));
    }

    @Test
    void testSetBandejasProyectadas_AjustaMinimo1YIgnoraIdInvalido() {
        CCBCalculator calc = new CCBCalculator();

        calc.setBandejasProyectadas(7, 0);
        assertEquals(1, calc.getBandejasProyectadas(7));

        calc.setBandejasProyectadas(0, 10);
        assertEquals(500, calc.getBandejasProyectadas(0), "Si no se setea, debe devolver el default 500");
    }

    @Test
    void testCalcularPrecioMenu_UsaCostoIngredientesCCBYSubsidio() {
        CCBCalculator calc = new CCBCalculator();

        Menu menu = new Menu(
                10,
                "Lunes",
                "Plato de prueba",
                "Plato principal",
                "Almuerzo",
                "Solo para test",
                "0g",
                "0g",
                "0 kcal",
                99.99 
        );

        Ingrediente ing = new Ingrediente(1, "IngredienteTest", "unidad", 10.0, 999, "Test");
        menu.agregarIngrediente(ing, 1.0); // costo ingredientes = 10.0


        double precio = calc.calcularPrecioMenu(menu, "Estudiante", "Almuerzo");
        assertEquals(4.79, precio, 0.001);

        double precioDesayuno = calc.calcularPrecioMenu(menu, "Estudiante", "Desayuno");
        assertEquals(4.61, precioDesayuno, 0.001);
    }
}
