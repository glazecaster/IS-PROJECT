package model;

import java.util.ArrayList;
import java.util.List;

public class MenuDia {
    private final String dia;
    private final String parche;
    private final List<Plato> platos = new ArrayList<>();

    private String totalKcal = "...";
    private String totalProt = "...";
    private String totalCarb = "...";

    public MenuDia(String dia, String parche) {
        this.dia = dia;
        this.parche = parche;
    }

    public void addPlato(Plato p) { platos.add(p); }

    public String getDia() { return dia; }
    public String getparche() { return parche; }
    public List<Plato> getPlatos() { return platos; }

    public String getTotalKcal() { return totalKcal; }
    public String getTotalProt() { return totalProt; }
    public String getTotalCarb() { return totalCarb; }

    public void setTotales(String kcal, String prot, String carb) {
        this.totalKcal = kcal;
        this.totalProt = prot;
        this.totalCarb = carb;
    }
}
