package model;

import admin.model.CCBCalculator;
import admin.model.Menu;
import admin.model.MenuServicio;

public class TarifaServicio {

    private final CCBCalculator ccb;
    private final MenuServicio menuServicio;

    public TarifaServicio() {
        this.ccb = new CCBCalculator();
        this.menuServicio = new MenuServicio();
    }

    public Menu getMenuPorId(int menuId) {
        return menuServicio.getMenuPorId(menuId);
    }

    public double calcularTarifa(int menuId, String codigoTipoUsuario, String tipoServicio) {
        return calcularTarifa(menuId, codigoTipoUsuario, tipoServicio, UsuarioServicio.porcentajePorDefecto(codigoTipoUsuario));
    }

    public double calcularTarifa(int menuId, String codigoTipoUsuario, String tipoServicio, double porcentajeEspecial) {
        Menu m = menuServicio.getMenuPorId(menuId);
        if (m == null) return -1;

        String tipoUsuario = mapTipoUsuario(codigoTipoUsuario);
        if ("Exonerado".equals(tipoUsuario)) {
            return 0.0;
        }
        if ("Becario".equals(tipoUsuario)) {
            
            CCBCalculator.ResultadoCCB resultado = ccb.calcularCCB();
            
            double tarifaEstudiante = ccb.calcularTarifa(resultado, "Estudiante");
            double porcentaje = porcentajeEspecial <= 0 ? 5.0 : porcentajeEspecial;
            return redondear2(tarifaEstudiante * (porcentaje / 100.0));
        }
        
        
        CCBCalculator.ResultadoCCB resultado = ccb.calcularCCB();
        return ccb.calcularTarifa(resultado, tipoUsuario);
    }

    public static String mapTipoUsuario(String codigo) {
        if (codigo == null) return "Estudiante";
        String c = codigo.trim().toUpperCase();
        if (c.equals("C") || c.equals("E")) return "Estudiante";
        if (c.equals("B")) return "Becario";
        if (c.equals("X")) return "Exonerado";
        if (c.equals("P")) return "Profesor";
        if (c.equals("T") || c.equals("I") || c.equals("AD")) return "Administrativo";
        return "Estudiante";
    }

    public static String mapCodigoDesdeEtiqueta(String etiqueta) {
        if (etiqueta == null) return "E";
        String e = etiqueta.trim().toLowerCase();
        if (e.startsWith("est")) return "E";
        if (e.startsWith("pro")) return "P";
        if (e.startsWith("emp") || e.startsWith("adm")) return "T";
        return "E";
    }

    private static double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}