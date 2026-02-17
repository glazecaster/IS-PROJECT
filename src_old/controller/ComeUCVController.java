package controller;

import java.util.List;
import model.MenuDia;
import model.Monedero;
import model.RepositorioSemana;
import view.ComeUCVView;

public class ComeUCVController {

    private final ComeUCVView view;
    private final RepositorioSemana repo;
    private final Monedero monedero;

    public ComeUCVController(ComeUCVView view, RepositorioSemana repo, Monedero monedero) {
        this.view = view;
        this.repo = repo;
        this.monedero = monedero;
    }

    public void init() {
        // header + monedero
        view.getHeader().setUsuarioText(monedero.getUsuario());
        view.getMonederoPanel().setUsuario(monedero.getUsuario());
        view.getMonederoPanel().setSaldo(formatMoney(monedero.getSaldo()));

        // menus: solo mostrar (sin seleccion, sin pago, sin bloqueo)
        List<MenuDia> menus = repo.getMenusSemana();
        view.getMenusPanel().renderMenus(menus);
        view.getMenusPanel().setBannerText("📌 Menús de la semana (Lunes a Viernes).");

        view.selectTabMenus();
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }
}
