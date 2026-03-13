package admin.model;

import java.util.HashMap;
import java.util.Map;

public class CCBCalculator {
    
    
    private double factorEstudiante = 0.25; 
    private double factorProfesor = 0.80;   
    private double factorAdmin = 1.00;       
    private double factorBecario = 0.05;     
    private double factorExonerado = 0.00;   
    
    private double merma = 0.05; 
    private double margenOperativo = 0.25; 
    private double iva = 0.16; 
    
    private ArchivoServicio archivoServicio;
    private MenuServicio menuServicio;
    private MenuSemana menuSemana;
    
    private Map<String, Integer> bandejasPorDia = new HashMap<>();
    
    public CCBCalculator() {
        this.archivoServicio = new ArchivoServicio();
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        
        
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        for (String dia : dias) {
            bandejasPorDia.put(dia, 400);
        }
    }
    
    
    public double getMerma() { return merma; }
    public void setMerma(double merma) { this.merma = merma; }
    
    public double getMargenOperativo() { return margenOperativo; }
    public void setMargenOperativo(double margenOperativo) { this.margenOperativo = margenOperativo; }
    
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    
    public double getFactorEstudiante() { return factorEstudiante; }
    public void setFactorEstudiante(double factor) { this.factorEstudiante = factor; }
    
    public double getFactorProfesor() { return factorProfesor; }
    public void setFactorProfesor(double factor) { this.factorProfesor = factor; }
    
    public double getFactorAdmin() { return factorAdmin; }
    public void setFactorAdmin(double factor) { this.factorAdmin = factor; }
    
    public double getFactorBecario() { return factorBecario; }
    public void setFactorBecario(double factor) { this.factorBecario = factor; }
    
    public double getFactorExonerado() { return factorExonerado; }
    public void setFactorExonerado(double factor) { this.factorExonerado = factor; }
    
    
    public double getFactorUsuario(String tipoUsuario) {
        switch (tipoUsuario) {
            case "Estudiante": return factorEstudiante;
            case "Profesor": return factorProfesor;
            case "Administrativo": return factorAdmin;
            case "Becario": return factorBecario;
            case "Exonerado": return factorExonerado;
            default: return 1.0;
        }
    }
    
    
    public void setBandejasPorDia(String dia, int cantidad) {
        if (cantidad < 0) cantidad = 0;
        if (cantidad > 1000) cantidad = 1000;
        bandejasPorDia.put(dia, cantidad);
    }
    
    public int getBandejasPorDia(String dia) {
        return bandejasPorDia.getOrDefault(dia, 400);
    }
    
    public int getTotalBandejasSemanal() {
        int total = 0;
        for (int val : bandejasPorDia.values()) {
            total += val;
        }
        return total;
    }
    
    
    
    
    public ResultadoCCB calcularCCB() {
        int totalBandejas = getTotalBandejasSemanal();
        if (totalBandejas <= 0) totalBandejas = 500;
        
        archivoServicio.recargar();
        
        double costosFijos = archivoServicio.getTotalCostosFijos();
        double costosVariables = archivoServicio.getTotalCostosVariables();
        double costosTotales = costosFijos + costosVariables;
        
        double ccbBase = costosTotales / totalBandejas;
        double ccbConMerma = ccbBase * (1 + merma);
        
        return new ResultadoCCB(
            costosFijos,
            costosVariables,
            costosTotales,
            totalBandejas,
            merma * 100,
            ccbBase,
            ccbConMerma
        );
    }
    
    
    public double calcularTarifa(ResultadoCCB resultado, String tipoUsuario) {
        double ccb = resultado.getCcbConMerma();
        double factor = getFactorUsuario(tipoUsuario);
        
        
        double tarifaBase = ccb * factor;
        
        
        double conMargen = tarifaBase * (1 + margenOperativo);
        double conIVA = conMargen * (1 + iva);
        
        return Math.round(conIVA * 100.0) / 100.0;
    }
    
    public double getPorcentajeSubsidio(String tipoUsuario) {
        double factor = getFactorUsuario(tipoUsuario);
        return (1 - factor) * 100;
    }
    
    public String getDescripcionSubsidio(String tipoUsuario) {
        double factor = getFactorUsuario(tipoUsuario);
        return String.format("Paga %.0f%% del CCB", factor * 100);
    }
    
    
    public double calcularPrecioMenuCompleto(int idMenu, String tipoUsuario) {
        Menu menu = menuServicio.getMenuPorId(idMenu);
        if (menu == null) return 0;
        
        ResultadoCCB resultado = calcularCCB();
        double factor = getFactorUsuario(tipoUsuario);
        double ccb = resultado.getCcbConMerma();
        
        
        
        double precioBase = menu.getPrecioVenta() * factor;
        double conMargen = precioBase * (1 + margenOperativo);
        double conIVA = conMargen * (1 + iva);
        
        return Math.round(conIVA * 100.0) / 100.0;
    }
    
    
    public double calcularPrecioDiaCompleto(String dia, boolean esDesayuno, String tipoUsuario) {
        double total = 0;
        ResultadoCCB resultado = calcularCCB();
        double factor = getFactorUsuario(tipoUsuario);
        double ccb = resultado.getCcbConMerma();
        
        if (esDesayuno) {
            
            Integer idPrincipal = menuSemana.getDesayuno(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
            Integer idAcompanante = menuSemana.getDesayuno(dia, MenuSemana.COMP_ACOMPANANTE);
            
            total += getPrecioMenu(idPrincipal, factor, ccb);
            total += getPrecioMenu(idAcompanante, factor, ccb);
            
        } else {
            
            Integer idPrincipal = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
            Integer idAcompanante = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ACOMPANANTE);
            Integer idEnsalada = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ENSALADA);
            
            total += getPrecioMenu(idPrincipal, factor, ccb);
            total += getPrecioMenu(idAcompanante, factor, ccb);
            total += getPrecioMenu(idEnsalada, factor, ccb);
        }
        
        return Math.round(total * 100.0) / 100.0;
    }
    
    private double getPrecioMenu(Integer idMenu, double factor, double ccb) {
        if (idMenu == null || idMenu <= 0) return 0;
        Menu menu = menuServicio.getMenuPorId(idMenu);
        if (menu == null) return 0;
        
        double precioBase = menu.getPrecioVenta() * factor;
        double conMargen = precioBase * (1 + margenOperativo);
        double conIVA = conMargen * (1 + iva);
        
        return conIVA;
    }
    
    
    public static class ResultadoCCB {
        private final double costosFijos;
        private final double costosVariables;
        private final double costosTotales;
        private final int numBandejas;
        private final double porcentajeMerma;
        private final double ccbBase;
        private final double ccbConMerma;
        
        public ResultadoCCB(double costosFijos, double costosVariables, double costosTotales,
                           int numBandejas, double porcentajeMerma, double ccbBase, double ccbConMerma) {
            this.costosFijos = costosFijos;
            this.costosVariables = costosVariables;
            this.costosTotales = costosTotales;
            this.numBandejas = numBandejas;
            this.porcentajeMerma = porcentajeMerma;
            this.ccbBase = ccbBase;
            this.ccbConMerma = ccbConMerma;
        }
        
        public double getCostosFijos() { return costosFijos; }
        public double getCostosVariables() { return costosVariables; }
        public double getCostosTotales() { return costosTotales; }
        public int getNumBandejas() { return numBandejas; }
        public double getPorcentajeMerma() { return porcentajeMerma; }
        public double getCcbBase() { return ccbBase; }
        public double getCcbConMerma() { return ccbConMerma; }
        
        public String getFormula() {
            return String.format("CCB = [(CF + CV) / NB] * (1 + %%) = [(%.2f + %.2f) / %d] * (1 + %.2f%%) = %.2f",
                costosFijos, costosVariables, numBandejas, porcentajeMerma, ccbConMerma);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════════════════════════════╗\n");
            sb.append("║                 CÁLCULO DEL CCB                          ║\n");
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ Costos Fijos (CF):               $%10.2f              ║\n", costosFijos));
            sb.append(String.format("║ Costos Variables (CV):           $%10.2f              ║\n", costosVariables));
            sb.append(String.format("║ Costos Totales (CF + CV):        $%10.2f              ║\n", costosTotales));
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ Número de Bandejas (NB):         %10d              ║\n", numBandejas));
            sb.append(String.format("║ CCB Base (sin merma):            $%10.2f              ║\n", ccbBase));
            sb.append(String.format("║ Porcentaje de Merma:             %10.2f%%              ║\n", porcentajeMerma));
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ CCB CON MERMA:                   $%10.2f              ║\n", ccbConMerma));
            sb.append("╚══════════════════════════════════════════════════════════╝\n");
            sb.append("\n");
            sb.append("FÓRMULA: ").append(getFormula());
            return sb.toString();
        }
    }
    
    
    public static class DesgloseDia {
        private final String dia;
        private final boolean esDesayuno;
        private final Map<String, Double> preciosComponentes;
        private final double total;
        private final double ccb;
        private final double factor;
        private final String tipoUsuario;
        
        public DesgloseDia(String dia, boolean esDesayuno, Map<String, Double> preciosComponentes,
                          double total, double ccb, double factor, String tipoUsuario) {
            this.dia = dia;
            this.esDesayuno = esDesayuno;
            this.preciosComponentes = preciosComponentes;
            this.total = total;
            this.ccb = ccb;
            this.factor = factor;
            this.tipoUsuario = tipoUsuario;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════════════════════════════╗\n");
            sb.append(String.format("║           DESGLOSE - %s (%s)                  ║\n", 
                dia, esDesayuno ? "Desayuno" : "Almuerzo"));
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            
            for (Map.Entry<String, Double> entry : preciosComponentes.entrySet()) {
                sb.append(String.format("║ %-28s $%8.2f              ║\n", 
                    entry.getKey() + ":", entry.getValue()));
            }
            
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ SUBTOTAL:                         $%8.2f              ║\n", total));
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ CCB Base:                         $%8.2f              ║\n", ccb));
            sb.append(String.format("║ Factor %s:                       %5.2f (%.0f%%)          ║\n", 
                tipoUsuario, factor, factor * 100));
            sb.append(String.format("║ TOTAL A PAGAR:                    $%8.2f              ║\n", total));
            sb.append("╚══════════════════════════════════════════════════════════╝");
            return sb.toString();
        }
    }
}