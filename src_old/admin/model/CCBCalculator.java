package admin.model;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CCBCalculator {
    private static final double MARGEN_OPERATIVO = 0.25; 
    private static final double IVA = 0.16; 
    
    private static final double SUBSIDIO_ESTUDIANTE = 0.70; 
    private static final double SUBSIDIO_PROFESOR = 0.30;  
    private static final double SUBSIDIO_ADMIN = 0.05;      
    
    private ArchivoServicio archivoServicio;
    private MenuServicio menuServicio;
    private IngredienteServicio ingredienteServicio;

    private Map<Integer, Integer> bandejasPorMenu = new HashMap<>();
    
    public CCBCalculator() {
        this.archivoServicio = new ArchivoServicio();
        this.menuServicio = new MenuServicio();
        this.ingredienteServicio = new IngredienteServicio();
    }

    public double calcularCCB(int numComensalesEstimado) {
        if (numComensalesEstimado <= 0) numComensalesEstimado = 500;

        archivoServicio.recargar();

        double totalCostosFijos = archivoServicio.getTotalCostosFijos();
        double totalCostosVariables = archivoServicio.getTotalCostosVariables();

        double ccbBase = (totalCostosFijos + totalCostosVariables) / numComensalesEstimado;
        return ccbBase > 0 ? ccbBase : 1.0;
    }

    public double calcularCCB() {
        return calcularCCB(500);
    }

    public void setBandejasProyectadas(int menuId, int cantidad) {
        if (menuId <= 0) return;
        if (cantidad < 1) cantidad = 1;
        if (cantidad > 400) cantidad = 400;
        bandejasPorMenu.put(menuId, cantidad);
    }

    public int getBandejasProyectadas(int menuId) {
        Integer v = bandejasPorMenu.get(menuId);
        return (v == null) ? 500 : v;
    }
    
    public double calcularPrecioMenu(Menu menu, String tipoUsuario, String tipoServicio) {
        
        if (!menu.tieneIngredientes()) {
            return menu.getPrecioVenta();
        }
        
        double ccbBase = calcularCCB(getBandejasProyectadas(menu.getId()));
        double factorServicio = getFactorServicio(tipoServicio);
        
        
        double costoBase = menu.calcularCostoIngredientes();
        if (costoBase <= 0) {
            costoBase = menu.getPrecioVenta() * 0.4;
        }
        
        
        double precioConSubsidio;
        switch (tipoUsuario) {
            case "Estudiante":
                precioConSubsidio = (costoBase + (ccbBase * factorServicio)) * (1 - SUBSIDIO_ESTUDIANTE);
                break;
            case "Profesor":
                precioConSubsidio = (costoBase + (ccbBase * factorServicio)) * (1 - SUBSIDIO_PROFESOR);
                break;
            case "Administrativo":
                precioConSubsidio = (costoBase + (ccbBase * factorServicio)) * (1 - SUBSIDIO_ADMIN);
                break;
            default:
                precioConSubsidio = costoBase + (ccbBase * factorServicio);
        }
        
        
        double precioConMargen = precioConSubsidio * (1 + MARGEN_OPERATIVO);
        
        
        double precioFinal = precioConMargen * (1 + IVA);
        
        return Math.round(precioFinal * 100.0) / 100.0;
    }
    
    public double getPorcentajeSubsidio(String tipoUsuario) {
        switch (tipoUsuario) {
            case "Estudiante":
                return SUBSIDIO_ESTUDIANTE * 100;
            case "Profesor":
                return SUBSIDIO_PROFESOR * 100;
            case "Administrativo":
                return SUBSIDIO_ADMIN * 100;
            default:
                return 0;
        }
    }
    
    public String getDescripcionSubsidio(String tipoUsuario) {
        switch (tipoUsuario) {
            case "Estudiante":
                return "Subsidio Alto (70%) - Población sin ingresos propios";
            case "Profesor":
                return "Subsidio Medio (30%) - Incentivo permanencia en campus";
            case "Administrativo":
                return "Costo Recuperación (5%) - Paga costo operativo real";
            default:
                return "";
        }
    }
    
    private double getFactorServicio(String tipoServicio) {
        if ("Desayuno".equalsIgnoreCase(tipoServicio)) {
            return 0.6;
        } else {
            return 1.0;
        }
    }
    
    public DesgloseCostos calcularDesgloseCostos(Menu menu, String tipoUsuario, String tipoServicio) {
        
        if (!menu.tieneIngredientes()) {
            return null;
        }
        
        double ccbBase = calcularCCB(getBandejasProyectadas(menu.getId()));
        double factorServicio = getFactorServicio(tipoServicio);
        
        double costoIngredientes = menu.calcularCostoIngredientes();
        if (costoIngredientes <= 0) {
            costoIngredientes = menu.getPrecioVenta() * 0.4;
        }
        
        double costoCCB = ccbBase * factorServicio;
        double costoTotalBase = costoIngredientes + costoCCB;
        
        
        double subsidio = 0;
        switch (tipoUsuario) {
            case "Estudiante":
                subsidio = costoTotalBase * SUBSIDIO_ESTUDIANTE;
                break;
            case "Profesor":
                subsidio = costoTotalBase * SUBSIDIO_PROFESOR;
                break;
            case "Administrativo":
                subsidio = costoTotalBase * SUBSIDIO_ADMIN;
                break;
        }
        
        double costoConSubsidio = costoTotalBase - subsidio;
        double margen = costoConSubsidio * MARGEN_OPERATIVO;
        double iva = (costoConSubsidio + margen) * IVA;
        double total = costoConSubsidio + margen + iva;
        
        return new DesgloseCostos(
            costoIngredientes,
            costoCCB,
            subsidio,
            getPorcentajeSubsidio(tipoUsuario),
            margen,
            iva,
            total
        );
    }
    

    public static class DesgloseCostos {
        private double costoIngredientes;
        private double costoCCB;
        private double subsidio;
        private double porcentajeSubsidio;
        private double margen;
        private double iva;
        private double total;
        
        public DesgloseCostos(double costoIngredientes, double costoCCB, double subsidio,
                            double porcentajeSubsidio, double margen, double iva, double total) {
            this.costoIngredientes = costoIngredientes;
            this.costoCCB = costoCCB;
            this.subsidio = subsidio;
            this.porcentajeSubsidio = porcentajeSubsidio;
            this.margen = margen;
            this.iva = iva;
            this.total = total;
        }
        
        public double getCostoIngredientes() { return costoIngredientes; }
        public double getCostoCCB() { return costoCCB; }
        public double getSubsidio() { return subsidio; }
        public double getPorcentajeSubsidio() { return porcentajeSubsidio; }
        public double getMargen() { return margen; }
        public double getIva() { return iva; }
        public double getTotal() { return total; }
        
        @Override
        public String toString() {
            double subtotal = costoIngredientes + costoCCB;
            return String.format(
                "╔══════════════════════════════════════════════════════════╗\n" +
                "║                 DESGLOSE DE COSTOS                       ║\n" +
                "╠══════════════════════════════════════════════════════════╣\n" +
                "║ Costo Ingredientes:                $%8.2f              ║\n" +
                "║ Costo CCB:                          $%8.2f              ║\n" +
                "╠══════════════════════════════════════════════════════════╣\n" +
                "║ Subtotal Base:                      $%8.2f              ║\n" +
                "║ Subsidio (%.0f%%):                   $%8.2f              ║\n" +
                "╠══════════════════════════════════════════════════════════╣\n" +
                "║ Costo con Subsidio:                  $%8.2f              ║\n" +
                "║ Margen Operativo (25%%):              $%8.2f              ║\n" +
                "║ IVA (16%%):                           $%8.2f              ║\n" +
                "╠══════════════════════════════════════════════════════════╣\n" +
                "║ TOTAL A PAGAR:                       $%8.2f              ║\n" +
                "╚══════════════════════════════════════════════════════════╝",
                costoIngredientes, costoCCB, subtotal, 
                porcentajeSubsidio, subsidio,
                subtotal - subsidio, margen, iva, total
            );
        }
    }
}