package controller;

import view.*;
import admin.view.AdminVista;
import admin.controller.AdminControlador;
import model.*;

import javax.swing.*;
import java.io.File;
import java.util.regex.*;

public class LoginController {
    public LoginView v;
    private final UsuarioServicio usuarios = new UsuarioServicio("data/usuarios.txt");

    public LoginController(LoginView v) {
        this.v = v;

        v.btnModoRegistro.addActionListener(e -> {
            v.lblConfirmar.setVisible(true);
            v.txtConfirmar.setVisible(true);
            v.lblTipo.setVisible(true);
            v.rbComensal.setVisible(true);
            v.rbAdmin.setVisible(true);
            v.btnRegistrar.setVisible(true);
            v.btnVolverLogin.setVisible(true);
            v.btnEntrar.setVisible(false);
            v.btnModoRegistro.setVisible(false);

            v.clearFields();
            v.revalidate();
        });

        v.btnVolverLogin.addActionListener(e -> {
            ocultarModoRegistro();
        });

        v.rbComensal.addActionListener(e -> toggleCamposComensal());
        v.rbAdmin.addActionListener(e -> toggleCamposComensal());

        v.btnRegistrar.addActionListener(e -> registrar());
        v.btnEntrar.addActionListener(e -> iniciarSesion());
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
        v.btnModoRegistro.setVisible(true);

        // campos comensal
        v.lblTipoComensal.setVisible(false);
        v.cbTipoComensal.setVisible(false);
        v.lblFoto.setVisible(false);
        v.btnSeleccionarFoto.setVisible(false);
        v.lblFotoRuta.setVisible(false);

        v.grupoTipo.clearSelection();
        v.clearFields();
        v.revalidate();
        v.repaint();
    }

    private void toggleCamposComensal() {
        boolean isRegistro = v.btnRegistrar.isVisible();
        boolean isComensal = v.rbComensal.isSelected();

        boolean show = isRegistro && isComensal;
        v.lblTipoComensal.setVisible(show);
        v.cbTipoComensal.setVisible(show);
        v.lblFoto.setVisible(show);
        v.btnSeleccionarFoto.setVisible(show);
        v.lblFotoRuta.setVisible(show);

        if (!show) {
            v.clearFoto();
        }
        v.revalidate();
        v.repaint();
    }

    private void registrar() {
        String c = safe(v.txtCedula.getText());
        String p = new String(v.txtClave.getPassword());
        String p2 = new String(v.txtConfirmar.getPassword());

        if (c.isEmpty() || p.isEmpty() || p2.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debes completar todos los campos.");
            return;
        }

        if (!v.rbComensal.isSelected() && !v.rbAdmin.isSelected()) {
            JOptionPane.showMessageDialog(null, "ERROR: Debes seleccionar Comensal o Administrador");
            return;
        }

        if (v.rbAdmin.isSelected()) {
            JPasswordField pinField = new JPasswordField();
            int opt = JOptionPane.showConfirmDialog(null, pinField, "Ingrese PIN de Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt != JOptionPane.OK_OPTION) return;
            String pin = new String(pinField.getPassword()).trim();
            if (pin.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Campo vacío");
                return;
            }
            if (!pin.equals("12345")) {
                JOptionPane.showMessageDialog(null, "Pin incorrecto");
                return;
            }
        }

        if (!c.matches("\\d{7,8}")) {
            JOptionPane.showMessageDialog(null, "La cédula debe tener entre 7 y 8 dígitos (solo números). ");
            return;
        }

        if (!p.equals(p2)) {
            JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden. Verifica ambos campos.");
            return;
        }

        if (!isStrongPassword(p)) {
            JOptionPane.showMessageDialog(null,
                    "La contraseña debe tener mínimo 9 caracteres y contener al menos 1 número y 1 caracter especial.");
            return;
        }

        String tipo;
        String fotoHash = "";
        if (v.rbComensal.isSelected()) {
            tipo = TarifaServicio.mapCodigoDesdeEtiqueta(String.valueOf(v.cbTipoComensal.getSelectedItem()));

            String ruta = (v.fotoRuta == null ? "" : v.fotoRuta.trim());
            if (ruta.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Para el comensal, debes seleccionar una foto (JPG/PNG) para el reconocimiento.");
                return;
            }
            try {
                fotoHash = HashUtil.sha256(new File(ruta));
                if (fotoHash.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se pudo procesar la foto seleccionada.");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "No se pudo leer la foto seleccionada.");
                return;
            }
        } else {
            tipo = "A";
        }

        try {
            if (usuarios.existeCedula(c)) {
                JOptionPane.showMessageDialog(null, "Esta cédula ya está registrada. Inicia sesión.");
                return;
            }

            usuarios.registrar(c, p, 0.0, tipo, fotoHash);

            if (tipo.equals("A")) {
                JOptionPane.showMessageDialog(null, "Registro exitoso. Tu cuenta fue creada como Administrador.");
            } else {
                JOptionPane.showMessageDialog(null, "Registro exitoso. Cuenta de comensal creada con foto registrada.");
            }

            v.grupoTipo.clearSelection();
            v.clearFields();
            ocultarModoRegistro();
        } catch (Exception exx) {
            exx.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar. Intenta de nuevo.");
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

            if (!cedula.matches("\\d{7,8}")) {
                JOptionPane.showMessageDialog(null, "Cédula inválida. Debe tener 7 u 8 dígitos.");
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

                av.setOnSalir(() -> {
                    av.dispose();
                    LoginView login = new LoginView();
                    new LoginController(login);
                    login.setVisible(true);
                });

                av.setLocationRelativeTo(null);
                av.setVisible(true);
                return;
            }

            String fotoHash = (res.fotoHash == null ? "" : res.fotoHash.trim());

            ReconocimientoCarnetDialog dlg = new ReconocimientoCarnetDialog(v, cedula, fotoHash.isEmpty());
            File f = dlg.showAndGetFile();
            if (f == null) return;

            try {
                String hash = HashUtil.sha256(f);
                if (hash == null || hash.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se pudo procesar la foto seleccionada.");
                    return;
                }

                if (fotoHash.isEmpty()) {
                    fotoHash = hash;
                    usuarios.actualizarFotoHash(cedula, fotoHash);
                } else {
                    if (!hash.equalsIgnoreCase(fotoHash)) {
                        JOptionPane.showMessageDialog(null, "Acceso denegado: la foto no coincide con la registrada.");
                        return;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "No se pudo leer la imagen seleccionada.");
                return;
            }

            if (res.saldo < 6.0) {
                JOptionPane.showMessageDialog(null, "Saldo insuficiente. Para ingresar debes tener $6.00 o más.");
                return;
            }

            double nuevoSaldo = res.saldo - 6.0;
            try {
                usuarios.actualizarSaldo(cedula, nuevoSaldo);
            } catch (Exception ignored) {}

            v.dispose();

            ComeUCVView mainV = new ComeUCVView();

            RepositorioSemana repo = new RepositorioSemana();
            Monedero mon = new Monedero(cedula, nuevoSaldo);

            ComeUCVController con = new ComeUCVController(mainV, repo, mon, usuarios, res.tipo, fotoHash, true);
            con.init();
            mainV.setVisible(true);


        } catch (Exception error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al iniciar sesión. Verifica tus datos o los archivos del proyecto.");
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static boolean isStrongPassword(String p) {
        if (p == null) return false;
        if (p.length() < 9) return false;
        boolean hasDigit = Pattern.compile("[0-9]").matcher(p).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(p).find();
        return hasDigit && hasSpecial;
    }


    private String obtenerRutaUsuarios() {
        String[] candidatos = new String[] {
            "data/usuarios.txt",
            "usuarios.txt",
            "test/usuarios.txt"
        };
        for (String p : candidatos) {
            File f = new File(p);
            if (f.exists() && f.isFile()) return p;
        }
        return "data/usuarios.txt";
    }
}