package modelo;

import java.util.List;

public class CCBCalculator {
    private static final double MARGEN_OPERATIVO = 0.25; // 25% de margen operativo
    private static final double IVA = 0.16; // 16% IVA
    
    // Factores de subsidio por tipo de usuario
    private static final double SUBSIDIO_ESTUDIANTE = 0.70; // 70% de subsidio (paga 30%)
    private static final double SUBSIDIO_PROFESOR = 0.30;   // 30% de subsidio (paga 70%)
    private static final double SUBSIDIO_ADMIN = 0.05;      // 5% de subsidio (paga 95%)
    
    private ArchivoServicio archivoServicio;
    private MenuServicio menuServicio;
    private IngredienteServicio ingredienteServicio;
    
    public CCBCalculator() {
        this.archivoServicio = new ArchivoServicio();
        this.menuServicio = new MenuServicio();
        this.ingredienteServicio = new IngredienteServicio();
    }
    
    public double calcularCCB() {
        double totalCostosFijos = archivoServicio.getTotalCostosFijos();
        double totalCostosVariables = archivoServicio.getTotalCostosVariables();
        
        // Número de comensales estimado
        int numComensalesEstimado = 500;
        
        // CCB Base = (Costos Fijos + Costos Variables) / Número de comensales
        double ccbBase = (totalCostosFijos + totalCostosVariables) / numComensalesEstimado;
        
        return ccbBase > 0 ? ccbBase : 1.0;
    }
    
    public double calcularPrecioMenu(Menu menu, String tipoUsuario, String tipoServicio) {
        
        if (!menu.tieneIngredientes()) {
            return menu.getPrecioVenta();
        }
        
        double ccbBase = calcularCCB();
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
        
        double ccbBase = calcularCCB();
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