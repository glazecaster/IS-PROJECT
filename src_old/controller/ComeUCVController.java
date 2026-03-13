package controller;

import model.AsistenciaServicio;
import model.MenuDia;
import model.Monedero;
import model.RecargaServicio;
import model.RepositorioSemana;
import model.TarifaServicio;
import model.UsuarioServicio;
import view.ComeUCVView;
import view.LoginView;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ComeUCVController {

    private final ComeUCVView view;
    private final RepositorioSemana repo;
    private final Monedero monedero;
    private final UsuarioServicio usuarios;
    private final AsistenciaServicio asistenciaServicio = new AsistenciaServicio();
    private final RecargaServicio recargas = new RecargaServicio();
    private final String tipoUsuarioCodigo;
    private final double porcentajeEspecial;
    private final Set<String> serviciosRegistrados = new HashSet<>();
    private boolean omitirRegistroInicial = false;

    public ComeUCVController(ComeUCVView view,
                             RepositorioSemana repo,
                             Monedero monedero,
                             UsuarioServicio usuarios,
                             String tipoUsuarioCodigo,
                             double porcentajeEspecial) {
        this.view = view;
        this.repo = repo;
        this.monedero = monedero;
        this.usuarios = usuarios;
        this.tipoUsuarioCodigo = tipoUsuarioCodigo == null ? "C" : tipoUsuarioCodigo;
        this.porcentajeEspecial = porcentajeEspecial;
    }

    public void init() {
        view.getHeader().setUsuarioText(monedero.getUsuario());
        view.getHeader().setRolText("COMENSAL • MENÚS DISPONIBLES");
        view.getMonederoPanel().setUsuario(monedero.getUsuario());
        view.getMonederoPanel().setSaldo(formatMoney(monedero.getSaldo()));

        view.setOnSalir(() -> {
            view.dispose();
            LoginView login = new LoginView();
            new LoginController(login);
            login.setVisible(true);
        });

        view.getMenusPanel().setMealSelectedListener(tipo -> {
            String tipoUsuario = TarifaServicio.mapTipoUsuario(tipoUsuarioCodigo);
            if ("DESAYUNO".equals(tipo)) {
                List<MenuDia> desayunos = repo.getMenusSemanaDesayuno(tipoUsuario);
                view.getMenusPanel().setBannerText("Horario de DESAYUNO: 7:00am – 10:00am");
                view.getMenusPanel().renderMenus(desayunos);
                registrarAsistenciaSiAplica("Desayuno");
            } else {
                List<MenuDia> almuerzos = repo.getMenusSemanaAlmuerzo(tipoUsuario);
                view.getMenusPanel().setBannerText("Horario de ALMUERZO: 12:00pm – 3:00pm");
                view.getMenusPanel().renderMenus(almuerzos);
                registrarAsistenciaSiAplica("Almuerzo");
            }
        });

        omitirRegistroInicial = true;
        view.getMenusPanel().selectDesayuno();
        omitirRegistroInicial = false;
        view.selectTabMenus();

        view.getTabMenus().addActionListener(e -> view.getHeader().setRolText("COMENSAL • MENÚS DISPONIBLES"));
        view.getTabMonedero().addActionListener(e -> view.getHeader().setRolText("COMENSAL • MI MONEDERO"));

        view.getMonederoPanel().setOnRecargar(this::procesarCanjeRecarga);
        view.getMonederoPanel().setOnSaldoPana(() -> {
            view.getHeader().setRolText("COMENSAL • SALDO PANA");
            view.selectSaldoPana();
        });

        view.getSaldoPanaView().setOnRegresar(() -> {
            view.getHeader().setRolText("COMENSAL • MI MONEDERO");
            view.selectTabMonedero();
        });
        view.getSaldoPanaView().setOnTransferir(this::transferirSaldoPana);
    }

    private boolean procesarCanjeRecarga(String codigoOperacion) {
        try {
            if (!RecargaServicio.esCodigoOperacionValido(codigoOperacion)) {
                JOptionPane.showMessageDialog(view, "El código de operación debe tener exactamente 10 dígitos numéricos.");
                return false;
            }

            UsuarioServicio.UsuarioRecord actual = usuarios.findByCedula(monedero.getUsuario());
            if (actual == null) {
                JOptionPane.showMessageDialog(view, "No se encontró el usuario actual.");
                return false;
            }

            RecargaServicio.RecargaRecord recarga = recargas.buscarPorCodigo(codigoOperacion.trim());
            if (recarga == null) {
                JOptionPane.showMessageDialog(view, "No se pudo efectuar el canje porque el código de operación no existe.");
                return false;
            }
            if (!actual.cedula.equals(recarga.cedula)) {
                JOptionPane.showMessageDialog(view, "El código de operación no corresponde al usuario actual.");
                return false;
            }
            if (recarga.canjeada) {
                JOptionPane.showMessageDialog(view, "Este código de operación ya fue canjeado.");
                return false;
            }
            if (redondear2(actual.saldo + recarga.monto) > 100.0) {
                JOptionPane.showMessageDialog(view, "No se pudo efectuar el canje porque el saldo máximo del monedero es 100 dólares.");
                return false;
            }
            if (!monedero.recargar(recarga.monto)) {
                JOptionPane.showMessageDialog(view, "No se pudo aplicar la recarga en el monedero.");
                return false;
            }
            if (!usuarios.actualizarSaldo(actual.cedula, monedero.getSaldo())) {
                JOptionPane.showMessageDialog(view, "No se pudo actualizar el saldo del usuario.");
                return false;
            }
            if (!recargas.marcarComoCanjeada(recarga.codigoOperacion)) {
                JOptionPane.showMessageDialog(view, "No se pudo actualizar el estado del código de operación.");
                return false;
            }
            view.getMonederoPanel().setSaldo(formatMoney(monedero.getSaldo()));
            JOptionPane.showMessageDialog(view, "Canje exitoso. Nuevo saldo: $" + formatMoney(monedero.getSaldo()));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "No se pudo procesar el canje de la recarga.");
            return false;
        }
    }

    private boolean transferirSaldoPana(String cedulaDestino, double monto, String clave, String confirmarClave) {
        try {
            if (cedulaDestino == null || !UsuarioServicio.esCedulaValida(cedulaDestino)) {
                JOptionPane.showMessageDialog(view, "La cédula del estudiante a recargar no es válida.");
                return false;
            }
            if (cedulaDestino.equals(monedero.getUsuario())) {
                JOptionPane.showMessageDialog(view, "No puedes usar Saldo Pana para recargarte a ti mismo.");
                return false;
            }
            if (monto <= 0 || monto > 100) {
                JOptionPane.showMessageDialog(view, "El monto debe ser mayor que 0 y menor o igual a 100 dólares.");
                return false;
            }
            if (clave == null || confirmarClave == null || clave.trim().isEmpty() || confirmarClave.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Debes ingresar la contraseña y su confirmación.");
                return false;
            }
            if (!clave.equals(confirmarClave)) {
                JOptionPane.showMessageDialog(view, "Las contraseñas no coinciden.");
                return false;
            }
            if (!isStrongPassword(clave)) {
                JOptionPane.showMessageDialog(view, "La contraseña debe tener mínimo 9 caracteres y contener al menos 1 número y 1 caracter especial.");
                return false;
            }

            UsuarioServicio.UsuarioRecord origen = usuarios.findByCedula(monedero.getUsuario());
            UsuarioServicio.UsuarioRecord destino = usuarios.findByCedula(cedulaDestino);
            if (origen == null || destino == null) {
                JOptionPane.showMessageDialog(view, "No se encontró el usuario origen o destino.");
                return false;
            }
            if (!UsuarioServicio.esTipoEstudiantil(origen.tipo) || !UsuarioServicio.esTipoEstudiantil(destino.tipo)) {
                JOptionPane.showMessageDialog(view, "Saldo Pana solo está disponible entre estudiantes.");
                return false;
            }

            UsuarioServicio.UsuarioRecord autenticado = usuarios.autenticar(origen.cedula, clave);
            if (autenticado == null || autenticado.tipo.equals("")) {
                JOptionPane.showMessageDialog(view, "La contraseña no coincide con el usuario que realiza la operación.");
                return false;
            }

            double comprometidoDestino = destino.saldo + recargas.totalPendienteParaCedula(destino.cedula);
            if (redondear2(comprometidoDestino + monto) > 100.0) {
                JOptionPane.showMessageDialog(view, "No se pudo efectuar la recarga porque el destinatario superaría el máximo de 100 dólares entre saldo actual y recargas pendientes.");
                return false;
            }

            String codigo = recargas.registrarRecarga(destino.cedula, monto);
            mostrarCodigoOperacion("Saldo Pana registrado", destino.cedula, monto, codigo);
            JOptionPane.showMessageDialog(view, "La operación fue registrada. Comparte el código con el otro estudiante para que pueda canjearlo desde su monedero.");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "No se pudo completar la operación de Saldo Pana.");
            return false;
        }
    }

    private void mostrarCodigoOperacion(String titulo, String cedulaDestino, double monto, String codigo) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText(titulo + ".\n\nCédula destino: " + cedulaDestino + "\nMonto: $" + formatMoney(monto) + "\nCódigo de operaciones: " + codigo + "\n\nPuedes seleccionar y copiar este código para compartirlo con el estudiante que canjeará la recarga.");
        area.setCaretPosition(0);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(420, 190));
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel("Código generado");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(lbl);
        wrapper.add(scroll);
        JOptionPane.showMessageDialog(view, wrapper, titulo, JOptionPane.INFORMATION_MESSAGE);
        area.requestFocusInWindow();
        area.selectAll();
    }

    private void registrarAsistenciaSiAplica(String servicio) {
        if (omitirRegistroInicial) return;
        if (servicio == null || serviciosRegistrados.contains(servicio)) return;
        if ("A".equals(tipoUsuarioCodigo)) return;
        asistenciaServicio.registrarAsistencia(monedero.getUsuario(), tipoUsuarioCodigo, servicio);
        serviciosRegistrados.add(servicio);
    }

    private boolean isStrongPassword(String password) {
        if (password == null) return false;
        if (password.length() < 9) return false;
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return hasDigit && hasSpecial;
    }

    private double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String formatMoney(double value) {
        return String.format("%.2f", value);
    }
}
