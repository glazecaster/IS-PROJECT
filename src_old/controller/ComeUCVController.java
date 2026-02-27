package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.MenuDia;
import model.Monedero;
import model.RepositorioSemana;
import model.TarifaServicio;
import model.UsuarioServicio;
import view.ComeUCVView;
import view.LoginView;

public class ComeUCVController {

    private final ComeUCVView view;
    private final RepositorioSemana repo;
    private final Monedero monedero;
    private final UsuarioServicio usuarios;

    private final String tipoUsuarioCodigo; // E/P/T/C
    private String fotoHash;                // hash registrado
    private boolean reconocimientoValidado; // si ya se validó en login

    private final TarifaServicio tarifaServicio = new TarifaServicio();
    private String servicioActual = "Desayuno"; // Desayuno | Almuerzo

    public ComeUCVController(ComeUCVView view,
                             RepositorioSemana repo,
                             Monedero monedero,
                             UsuarioServicio usuarios,
                             String tipoUsuarioCodigo,
                             String fotoHash,
                             boolean reconocimientoValidado) {
        this.view = view;
        this.repo = repo;
        this.monedero = monedero;
        this.usuarios = usuarios;
        this.tipoUsuarioCodigo = (tipoUsuarioCodigo == null ? "C" : tipoUsuarioCodigo);
        this.fotoHash = (fotoHash == null ? "" : fotoHash);
        this.reconocimientoValidado = reconocimientoValidado;
    }

    public void init() {
        // header + monedero
        view.getHeader().setUsuarioText(monedero.getUsuario());
        view.getHeader().setRolText("COMENSAL • MENÚS DISPONIBLES");
        view.getMonederoPanel().setUsuario(monedero.getUsuario());
        view.getMonederoPanel().setSaldo(formatMoney(monedero.getSaldo()));

        // botón salir
        view.setOnSalir(() -> {
            view.dispose();
            LoginView login = new LoginView();
            new LoginController(login);
            login.setVisible(true);
        });

        view.getMenusPanel().setMealSelectedListener(tipo -> {
            if ("DESAYUNO".equals(tipo)) {
                servicioActual = "Desayuno";
                List<MenuDia> desayunos = repo.getMenusSemanaDesayuno();
                aplicarTarifas(desayunos, "Desayuno");
                view.getMenusPanel().setBannerText("Horario de DESAYUNO: 7:00am – 10:00am");
                view.getMenusPanel().renderMenus(desayunos);
            } else {
                servicioActual = "Almuerzo";
                List<MenuDia> almuerzos = repo.getMenusSemanaAlmuerzo();
                aplicarTarifas(almuerzos, "Almuerzo");
                view.getMenusPanel().setBannerText("Horario de ALMUERZO: 12:00pm – 3:00pm");
                view.getMenusPanel().renderMenus(almuerzos);
            }
        });

        view.getMenusPanel().selectDesayuno();
        view.selectTabMenus();

        view.getTabMenus().addActionListener(e -> view.getHeader().setRolText("COMENSAL • MENÚS DISPONIBLES"));
        view.getTabMonedero().addActionListener(e -> view.getHeader().setRolText("COMENSAL • MI MONEDERO"));

        view.getMonederoPanel().setOnRecargar(monto -> {
            if (monedero.getSaldo() + monto > 100) {
                JOptionPane.showMessageDialog(view, "El saldo máximo del monedero es 100 dolares.");
                return false;
            }
            if (!monedero.recargar(monto)) {
                return false;
            }
            view.getMonederoPanel().setSaldo(formatMoney(monedero.getSaldo()));
            try {
                usuarios.actualizarSaldo(monedero.getUsuario(), monedero.getSaldo());
            } catch (Exception ignored) {}
            return true;
        });
    }

    private void aplicarTarifas(List<MenuDia> menus, String tipoServicio) {
        if (menus == null) return;
        for (MenuDia m : menus) {
            if (m == null) continue;
            m.setTipoServicio(tipoServicio);
            if (m.tieneComponentes()) {
                double total = 0;
                boolean alMenosUno = false;
                for (Integer id : m.getMenuIdsPorComponente().values()) {
                    if (id == null || id <= 0) continue;
                    double t = tarifaServicio.calcularTarifa(id, tipoUsuarioCodigo, tipoServicio);
                    if (t > 0) {
                        total += t;
                        alMenosUno = true;
                    }
                }
                m.setTarifa(alMenosUno ? total : -1);
            } else if (m.getMenuId() > 0) {
                double t = tarifaServicio.calcularTarifa(m.getMenuId(), tipoUsuarioCodigo, tipoServicio);
                m.setTarifa(t);
            } else {
                m.setTarifa(-1);
            }
        }
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }
}
