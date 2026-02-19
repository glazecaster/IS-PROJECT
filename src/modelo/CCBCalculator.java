package modelo;

public class CCBCalculator {
    private static final double MARGEN_OPERATIVO = 0.15;
    private static final double IVA = 0.16;
    
    //Subsidios
    private static final double SUBSIDIO_ESTUDIANTE = 0.75;
    private static final double SUBSIDIO_PROFESOR = 0.40;
    private static final double SUBSIDIO_ADMIN = 0.15;
    
    //distribución de costos fijos
    private static final double FACTOR_CF_DESAYUNO = 0.25;
    private static final double FACTOR_CF_ALMUERZO = 0.75;
    
    //servicio ajustados
    private static final double FACTOR_SERVICIO_DESAYUNO = 0.3;
    private static final double FACTOR_SERVICIO_ALMUERZO = 1.0;
    
    // Valores por defecto para bandejas si no hay proyecciones
    private static final int BANDEJAS_DESAYUNO_DEFECTO = 800;
    private static final int BANDEJAS_ALMUERZO_DEFECTO = 1500;
    
    private ArchivoServicio archivoServicio;
    private MenuServicio menuServicio;
    private IngredienteServicio ingredienteServicio;
    private BandejaProyeccion bandejaProyeccion;
    
    public CCBCalculator() {
        this.archivoServicio = new ArchivoServicio();
        this.menuServicio = new MenuServicio();
        this.ingredienteServicio = new IngredienteServicio();
        this.bandejaProyeccion = new BandejaProyeccion();
    }
    
    /**
     * Calcula el CCB base según la fórmula: CCB = [(CF + CV)/NB] * (1 + %Merma)
     * @param tipoServicio Tipo de servicio (Desayuno o Almuerzo)
     * @return CCB calculado
     */
    public double calcularCCB(String tipoServicio) {
        double totalCostosFijos = archivoServicio.getTotalCostosFijos();
        double totalCostosVariables = archivoServicio.getTotalCostosVariables();
        
        // Distribuir costos según el servicio
        double factorCF = "Desayuno".equalsIgnoreCase(tipoServicio) ? 
            FACTOR_CF_DESAYUNO : FACTOR_CF_ALMUERZO;
        
        double costosFijosAplicables = totalCostosFijos * factorCF;
        double costosVariablesAplicables = totalCostosVariables * factorCF;
        
        // Obtener número de bandejas proyectadas para este servicio
        int numBandejas = bandejaProyeccion.getTotalBandejasServicio(tipoServicio);
        
        // Si no hay proyecciones, usar valores por defecto más realistas
        if (numBandejas <= 0) {
            numBandejas = "Desayuno".equalsIgnoreCase(tipoServicio) ? 
                BANDEJAS_DESAYUNO_DEFECTO : BANDEJAS_ALMUERZO_DEFECTO;
        }
        
        // Obtener porcentaje de merma
        double porcentajeMerma = bandejaProyeccion.getPorcentajeMerma() / 100.0;
        
        // Calcular CCB según la fórmula
        double ccbBase = (costosFijosAplicables + costosVariablesAplicables) / numBandejas;
        double ccbConMerma = ccbBase * (1 + porcentajeMerma);
        
        return ccbConMerma > 0 ? ccbConMerma : 1.0;
    }
    
    /**
     * Calcula el CCB para un día específico
     * @param dia Día de la semana
     * @param tipoServicio Tipo de servicio
     * @return CCB calculado para ese día
     */
    public double calcularCCBPorDia(String dia, String tipoServicio) {
        double totalCostosFijos = archivoServicio.getTotalCostosFijos();
        double totalCostosVariables = archivoServicio.getTotalCostosVariables();
        
        // Distribuir costos por día (7 días) y por servicio
        double factorCF = "Desayuno".equalsIgnoreCase(tipoServicio) ? 
            FACTOR_CF_DESAYUNO : FACTOR_CF_ALMUERZO;
        
        double costosFijosDiarios = (totalCostosFijos * factorCF) / 7.0;
        double costosVariablesDiarios = (totalCostosVariables * factorCF) / 7.0;
        
        // Obtener número de bandejas para este día específico
        int numBandejas = 0;
        if ("Desayuno".equalsIgnoreCase(tipoServicio)) {
            numBandejas = bandejaProyeccion.getProyeccionDesayuno(dia);
            if (numBandejas <= 0) numBandejas = BANDEJAS_DESAYUNO_DEFECTO / 7;
        } else {
            numBandejas = bandejaProyeccion.getProyeccionAlmuerzo(dia);
            if (numBandejas <= 0) numBandejas = BANDEJAS_ALMUERZO_DEFECTO / 7;
        }
        
        double porcentajeMerma = bandejaProyeccion.getPorcentajeMerma() / 100.0;
        
        double ccbBase = (costosFijosDiarios + costosVariablesDiarios) / numBandejas;
        return ccbBase * (1 + porcentajeMerma);
    }
    
    public double calcularPrecioMenu(Menu menu, String tipoUsuario, String tipoServicio) {
        if (!menu.tieneIngredientes()) {
            return menu.getPrecioVenta();
        }
        
        // Usar CCB específico para el servicio
        double ccbBase = calcularCCB(tipoServicio);
        double factorServicio = getFactorServicio(tipoServicio);
        
        double costoBase = menu.calcularCostoIngredientes();
        if (costoBase <= 0) {
            // Estimar costo basado en precio de venta (40% es costo de ingredientes)
            costoBase = menu.getPrecioVenta() * 0.4;
        }
        
        // Calcular precio con subsidio
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
    
    public double calcularPrecioMenuPorDia(Menu menu, String dia, String tipoUsuario, String tipoServicio) {
        if (!menu.tieneIngredientes()) {
            return menu.getPrecioVenta();
        }
        
        double ccbDia = calcularCCBPorDia(dia, tipoServicio);
        double factorServicio = getFactorServicio(tipoServicio);
        
        double costoBase = menu.calcularCostoIngredientes();
        if (costoBase <= 0) {
            costoBase = menu.getPrecioVenta() * 0.4;
        }
        
        double precioConSubsidio;
        switch (tipoUsuario) {
            case "Estudiante":
                precioConSubsidio = (costoBase + ccbDia * factorServicio) * (1 - SUBSIDIO_ESTUDIANTE);
                break;
            case "Profesor":
                precioConSubsidio = (costoBase + ccbDia * factorServicio) * (1 - SUBSIDIO_PROFESOR);
                break;
            case "Administrativo":
                precioConSubsidio = (costoBase + ccbDia * factorServicio) * (1 - SUBSIDIO_ADMIN);
                break;
            default:
                precioConSubsidio = costoBase + ccbDia * factorServicio;
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
                return "Subsidio Alto (75%) - Población sin ingresos propios";
            case "Profesor":
                return "Subsidio Medio (40%) - Incentivo permanencia en campus";
            case "Administrativo":
                return "Subsidio Básico (15%) - Apoyo al personal";
            default:
                return "";
        }
    }
    
    private double getFactorServicio(String tipoServicio) {
        if ("Desayuno".equalsIgnoreCase(tipoServicio)) {
            return FACTOR_SERVICIO_DESAYUNO;
        } else {
            return FACTOR_SERVICIO_ALMUERZO;
        }
    }
    
    public String getResumenProyeccion() {
        int totalBandejas = bandejaProyeccion.getTotalBandejasSemana();
        double merma = bandejaProyeccion.getPorcentajeMerma();
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║           RESUMEN DE PROYECCIÓN DE BANDEJAS             ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Total Bandejas Semana: %35d ║\n", totalBandejas));
        sb.append(String.format("║ Porcentaje de Merma: %37.1f%% ║\n", merma));
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║                    PROYECCIÓN POR DÍA                    ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (String dia : dias) {
            int desayunos = bandejaProyeccion.getProyeccionDesayuno(dia);
            int almuerzos = bandejaProyeccion.getProyeccionAlmuerzo(dia);
            int total = desayunos + almuerzos;
            
            sb.append(String.format("║ %-9s: D:%-5d A:%-5d Total:%-5d           ║\n", 
                dia, desayunos, almuerzos, total));
        }
        
        sb.append("╚══════════════════════════════════════════════════════════╝");
        
        return sb.toString();
    }
    
    public DesgloseCostos calcularDesgloseCostos(Menu menu, String tipoUsuario, String tipoServicio) {
        if (!menu.tieneIngredientes()) {
            return null;
        }
        
        double ccbBase = calcularCCB(tipoServicio);
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
    
    // Getters y setters
    public BandejaProyeccion getBandejaProyeccion() {
        return bandejaProyeccion;
    }
    
    public void setBandejaProyeccion(BandejaProyeccion bandejaProyeccion) {
        this.bandejaProyeccion = bandejaProyeccion;
    }
    
    // Clase interna para desglose
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
                "║ Margen Operativo (15%%):              $%8.2f              ║\n" +
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