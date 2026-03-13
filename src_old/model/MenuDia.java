package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuDia {
    private final String dia;
    private final String parche;
    private final List<Plato> platos = new ArrayList<>();

    private String horario = "";

    private String totalKcal = "...";
    private String totalProt = "...";
    private String totalCarb = "...";

    private int menuId = -1; 
    private String tipoServicio = ""; 
    private double tarifa = -1.0; 

    private final Map<String, Integer> menuIdsPorComponente = new LinkedHashMap<>();

    public MenuDia(String dia, String parche) {
        this.dia = dia;
        this.parche = parche;
    }

    public void addPlato(Plato p) { platos.add(p); }

    public String getDia() { return dia; }
    public String getparche() { return parche; }
    public List<Plato> getPlatos() { return platos; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = (horario == null ? "" : horario); }

    public String getTotalKcal() { return totalKcal; }
    public String getTotalProt() { return totalProt; }
    public String getTotalCarb() { return totalCarb; }

    public void setTotales(String kcal, String prot, String carb) {
        this.totalKcal = kcal;
        this.totalProt = prot;
        this.totalCarb = carb;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public void setMenuIdComponente(String componente, int menuId) {
        if (componente == null) return;
        menuIdsPorComponente.put(componente, menuId);
    }

    public Integer getMenuIdComponente(String componente) {
        if (componente == null) return null;
        return menuIdsPorComponente.get(componente);
    }

    public Map<String, Integer> getMenuIdsPorComponente() {
        return menuIdsPorComponente;
    }

    public boolean tieneComponentes() {
        return !menuIdsPorComponente.isEmpty();
    }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = (tipoServicio == null ? "" : tipoServicio); }

    public double getTarifa() { return tarifa; }
    public void setTarifa(double tarifa) { this.tarifa = tarifa; }
}
