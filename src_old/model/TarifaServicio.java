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
        Menu m = menuServicio.getMenuPorId(menuId);
        if (m == null) return -1;
        String tipoUsuario = mapTipoUsuario(codigoTipoUsuario);
        return ccb.calcularPrecioMenu(m, tipoUsuario, tipoServicio);
    }

    public static String mapTipoUsuario(String codigo) {
        if (codigo == null) return "Estudiante";
        String c = codigo.trim().toUpperCase();
        if (c.equals("C") || c.equals("E")) return "Estudiante";
        if (c.equals("P")) return "Profesor";
        if (c.equals("T") || c.equals("I") || c.equals("AD")) return "Administrativo";
        return "Estudiante";
    }

    public static String mapCodigoDesdeEtiqueta(String etiqueta) {
        if (etiqueta == null) return "E";
        String e = etiqueta.trim().toLowerCase();
        if (e.startsWith("est")) return "E";
        if (e.startsWith("pro")) return "P";
        if (e.startsWith("adm")) return "T";
        return "E";
    }
}
