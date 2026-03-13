package controller;

import admin.controller.AdminControlador;
import admin.view.AdminVista;
import model.Monedero;
import model.RecargaServicio;
import model.ReconocimientoFacialServicio;
import model.RepositorioSemana;
import model.TarifaServicio;
import model.UsuarioServicio;
import view.ComeUCVView;
import view.LoginView;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.util.regex.Pattern;

public class LoginController {
    public LoginView v;
    private final UsuarioServicio usuarios = new UsuarioServicio("data/usuarios.txt");
    private final ReconocimientoFacialServicio reconocimiento = new ReconocimientoFacialServicio(usuarios);
    private final RecargaServicio recargas = new RecargaServicio();

    public LoginController(LoginView v) {
        this.v = v;
        v.btnModoRegistro.addActionListener(e -> mostrarModoRegistro());
        v.btnVolverLogin.addActionListener(e -> ocultarModoRegistro());
        v.rbComensal.addActionListener(e -> toggleCamposComensal());
        v.rbAdmin.addActionListener(e -> toggleCamposComensal());
        v.btnSeleccionarFoto.addActionListener(e -> seleccionarFotoRegistro());
        v.btnRegistrar.addActionListener(e -> registrar());
        v.btnEntrar.addActionListener(e -> iniciarSesion());
        v.btnRecargas.addActionListener(e -> mostrarDialogoRecargas());
    }

    private void mostrarModoRegistro() {
        v.lblConfirmar.setVisible(true);
        v.txtConfirmar.setVisible(true);
        v.lblTipo.setVisible(true);
        v.rbComensal.setVisible(true);
        v.rbAdmin.setVisible(true);
        v.btnRegistrar.setVisible(true);
        v.btnVolverLogin.setVisible(true);
        v.btnEntrar.setVisible(false);
        v.btnRecargas.setVisible(false);
        v.btnModoRegistro.setVisible(false);
        v.setReferenceVisible(false);
        v.setPhotoVisible(true);
        v.clearFields();
        v.revalidate();
        v.repaint();
    }

    private void ocultarModoRegistro() {
        v.lblConfirmar.setVisible(false);
        v.txtConfirmar.setVisible(false);
        v.lblTipo.setVisible(false);
        v.rbComensal.setVisible(false);
        v.rbAdmin.setVisible(false);
        v.btnRegistrar.setVisible(false);
        v.btnVolverLogin.setVisible(false);
        v.btnEntrar.setVisible(true);
        v.btnRecargas.setVisible(true);
        v.btnModoRegistro.setVisible(true);
        v.lblTipoComensal.setVisible(false);
        v.cbTipoComensal.setVisible(false);
        v.setReferenceVisible(false);
        v.setPhotoVisible(false);
        v.grupoTipo.clearSelection();
        v.clearFields();
        v.revalidate();
        v.repaint();
    }

    private void toggleCamposComensal() {
        boolean show = v.btnRegistrar.isVisible() && v.rbComensal.isSelected();
        v.lblTipoComensal.setVisible(show);
        v.cbTipoComensal.setVisible(show);
        v.revalidate();
        v.repaint();
    }

    private void seleccionarFotoRegistro() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona la foto del usuario");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes JPG y PNG", "jpg", "jpeg", "png"));
        int result = chooser.showOpenDialog(v);
        if (result == JFileChooser.APPROVE_OPTION) {
            v.setArchivoFotoSeleccionada(chooser.getSelectedFile());
        }
    }

    private void registrar() {
        String cedula = safe(v.txtCedula.getText());
        String clave = new String(v.txtClave.getPassword());
        String confirmar = new String(v.txtConfirmar.getPassword());
        if (cedula.isEmpty() || clave.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debes completar todos los campos.");
            return;
        }
        if (!UsuarioServicio.esCedulaValida(cedula)) {
            JOptionPane.showMessageDialog(null, "La cédula debe estar entre 5.000.000 y 35.000.000 y contener solo números.");
            return;
        }
        if (!v.rbComensal.isSelected() && !v.rbAdmin.isSelected()) {
            JOptionPane.showMessageDialog(null, "Debes seleccionar Comensal o Administrador.");
            return;
        }
        if (!clave.equals(confirmar)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden. Verifica ambos campos.");
            return;
        }
        if (!isStrongPassword(clave)) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener mínimo 9 caracteres y contener al menos 1 número y 1 caracter especial.");
            return;
        }
        if (v.rbAdmin.isSelected()) {
            JPasswordField pinField = new JPasswordField();
            int opt = JOptionPane.showConfirmDialog(null, pinField, "Ingrese PIN de Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) {
                return;
            }
            String pin = new String(pinField.getPassword()).trim();
            if (!"12345".equals(pin)) {
                JOptionPane.showMessageDialog(null, "Pin incorrecto");
                return;
            }
        }
        String tipo;
        double porcentajeEspecial;
        if (v.rbComensal.isSelected()) {
            tipo = TarifaServicio.mapCodigoDesdeEtiqueta(String.valueOf(v.cbTipoComensal.getSelectedItem()));
            porcentajeEspecial = UsuarioServicio.porcentajePorDefecto(tipo);
            if (v.getArchivoFotoSeleccionada() == null) {
                JOptionPane.showMessageDialog(null, "Debes seleccionar una foto para el escaneo facial del comensal.");
                return;
            }
        } else {
            tipo = "A";
            porcentajeEspecial = 100.0;
        }
        try {
            if (usuarios.existeCedula(cedula)) {
                JOptionPane.showMessageDialog(null, "Esta cédula ya está registrada. Inicia sesión.");
                return;
            }
            String fotoPath = "";
            if (v.getArchivoFotoSeleccionada() != null) {
                fotoPath = reconocimiento.guardarFotoUsuario(cedula, v.getArchivoFotoSeleccionada());
            }
            usuarios.registrar(cedula, clave, 0.0, tipo, "", porcentajeEspecial, fotoPath);
            if ("A".equals(tipo)) {
                JOptionPane.showMessageDialog(null, "Registro exitoso. Tu cuenta fue creada como Administrador.");
            } else {
                JOptionPane.showMessageDialog(null, "Registro exitoso. Cuenta de comensal creada con foto para escaneo facial.");
            }
            v.grupoTipo.clearSelection();
            v.clearFields();
            ocultarModoRegistro();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar. Verifica la foto seleccionada e intenta de nuevo.");
        }
    }

    private void iniciarSesion() {
        try {
            String cedula = safe(v.txtCedula.getText());
            String clave = new String(v.txtClave.getPassword());
            if (cedula.isEmpty() || clave.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes completar cédula y contraseña.");
                return;
            }
            if (!UsuarioServicio.esCedulaValida(cedula)) {
                JOptionPane.showMessageDialog(null, "Cédula inválida. Debe estar entre 5.000.000 y 35.000.000.");
                return;
            }
            UsuarioServicio.UsuarioRecord res = usuarios.autenticar(cedula, clave);
            if (res == null) {
                JOptionPane.showMessageDialog(null, "Debes tener una cuenta para iniciar sesión");
                return;
            }
            if (res.tipo.equals("")) {
                JOptionPane.showMessageDialog(null, "Cédula o contraseña incorrecta.");
                return;
            }
            if (res.tipo.equals("A")) {
                v.dispose();
                AdminVista av = new AdminVista(cedula);
                new AdminControlador(av);
                av.setOnSalir(() -> volverAlLogin(av));
                av.setLocationRelativeTo(null);
                av.setVisible(true);
                return;
            }
            v.dispose();
            ComeUCVView mainV = new ComeUCVView();
            RepositorioSemana repo = new RepositorioSemana();
            Monedero mon = new Monedero(cedula, res.saldo);
            ComeUCVController con = new ComeUCVController(mainV, repo, mon, usuarios, res.tipo, res.porcentajeEspecial);
            con.init();
            mainV.setVisible(true);
        } catch (Exception error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al iniciar sesión. Verifica tus datos o los archivos del proyecto.");
        }
    }

    private void mostrarDialogoRecargas() {
        JTextField txtCedulaRecarga = new JTextField(15);
        JPasswordField txtClaveRecarga = new JPasswordField(15);
        JPasswordField txtConfirmarRecarga = new JPasswordField(15);
        JTextField txtMontoRecarga = new JTextField(15);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCedulaRecarga, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        panel.add(txtClaveRecarga, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1;
        panel.add(txtConfirmarRecarga, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Monto a recargar:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMontoRecarga, gbc);
        int result = JOptionPane.showConfirmDialog(v, panel, "Recargas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            String cedula = safe(txtCedulaRecarga.getText());
            String clave = new String(txtClaveRecarga.getPassword());
            String confirmar = new String(txtConfirmarRecarga.getPassword());
            String montoRaw = safe(txtMontoRecarga.getText());
            if (cedula.isEmpty() || clave.isEmpty() || confirmar.isEmpty() || montoRaw.isEmpty()) {
                JOptionPane.showMessageDialog(v, "Debes completar cédula, contraseña, confirmación y monto.");
                return;
            }
            if (!UsuarioServicio.esCedulaValida(cedula)) {
                JOptionPane.showMessageDialog(v, "La cédula debe estar entre 5.000.000 y 35.000.000 y contener solo números.");
                return;
            }
            if (!clave.equals(confirmar)) {
                JOptionPane.showMessageDialog(v, "Las contraseñas no coinciden.");
                return;
            }
            if (!isStrongPassword(clave)) {
                JOptionPane.showMessageDialog(v, "La contraseña debe tener mínimo 9 caracteres y contener al menos 1 número y 1 caracter especial.");
                return;
            }
            if (!montoRaw.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(v, "Monto inválido. Usa solo números.");
                return;
            }
            double monto = Double.parseDouble(montoRaw);
            if (monto <= 0) {
                JOptionPane.showMessageDialog(v, "El monto a recargar debe ser mayor que 0.");
                return;
            }
            if (monto > 100) {
                JOptionPane.showMessageDialog(v, "El monto máximo permitido por operación es 100 dólares.");
                return;
            }
            UsuarioServicio.UsuarioRecord usuario = usuarios.autenticar(cedula, clave);
            if (usuario == null) {
                JOptionPane.showMessageDialog(v, "Debes tener una cuenta registrada para recargar.");
                return;
            }
            if (usuario.tipo.equals("")) {
                JOptionPane.showMessageDialog(v, "Cédula o contraseña incorrecta.");
                return;
            }
            double comprometido = redondear2(usuario.saldo + recargas.totalPendienteParaCedula(usuario.cedula));
            if (redondear2(comprometido + monto) > 100.0) {
                JOptionPane.showMessageDialog(v, "No se pudo efectuar la recarga porque el usuario superaría el máximo de 100 dólares entre saldo actual y recargas pendientes.");
                return;
            }
            String codigo = recargas.registrarRecarga(usuario.cedula, monto);
            mostrarCodigoOperacion("Recarga exitosa", usuario.cedula, monto, codigo);
            v.txtCedula.setText(usuario.cedula);
            v.txtClave.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(v, "No se pudo completar la recarga.");
        }
    }

    private void mostrarCodigoOperacion(String titulo, String cedulaDestino, double monto, String codigo) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText(titulo + ".\n\nCédula: " + cedulaDestino + "\nMonto: $" + String.format("%.2f", monto) + "\nCódigo de operaciones: " + codigo + "\n\nPuedes seleccionar y copiar este código para canjear la recarga más tarde desde el monedero.");
        area.setCaretPosition(0);
        area.selectAll();
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 190));
        JOptionPane.showMessageDialog(v, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void volverAlLogin(Window ventanaActual) {
        for (Window window : Window.getWindows()) {
            if (window != null && window != ventanaActual) {
                window.dispose();
            }
        }
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }
        LoginView login = new LoginView();
        new LoginController(login);
        login.setVisible(true);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isStrongPassword(String p) {
        if (p == null || p.length() < 9) {
            return false;
        }
        boolean hasDigit = Pattern.compile("[0-9]").matcher(p).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(p).find();
        return hasDigit && hasSpecial;
    }

    private static double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
