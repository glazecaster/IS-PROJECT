package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LoginView extends JFrame {
    private final HeaderPanel header = new HeaderPanel("COMEUCV");
    private final JCheckBox chkMostrarClave = new JCheckBox("Mostrar contraseña");

    public JTextField txtCedula;
    public JPasswordField txtClave;
    public JPasswordField txtConfirmar;
    public JLabel lblConfirmar;
    public JButton btnEntrar;
    public JButton btnRecargas;
    public JButton btnRegistrar;
    public JButton btnModoRegistro;
    public JButton btnVolverLogin;
    public JButton btnSeleccionarFoto;

    public JRadioButton rbComensal;
    public JRadioButton rbAdmin;
    public ButtonGroup grupoTipo;
    public JLabel lblTipo;

    public JLabel lblTipoComensal;
    public JComboBox<String> cbTipoComensal;
    public JLabel lblFotoRegistro;
    public JLabel lblFotoSeleccionada;

    private File archivoFotoSeleccionada;

    public LoginView() {
        super("ComeUCV - Acceso");
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());

        header.setRolText("PORTAL DE ACCESO");
        root.add(header, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(0xF7FBFF));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        txtCedula = new JTextField(20);
        txtClave = new JPasswordField(20);
        txtConfirmar = new JPasswordField(20);
        lblConfirmar = new JLabel("CONFIRMAR CONTRASEÑA:");
        lblFotoRegistro = new JLabel("FOTO PARA ESCANEO FACIAL:");
        lblFotoSeleccionada = new JLabel("Ninguna foto seleccionada");
        lblFotoSeleccionada.setForeground(new Color(0x173B66));

        chkMostrarClave.setOpaque(false);
        chkMostrarClave.addActionListener(e -> {
            boolean show = chkMostrarClave.isSelected();
            txtClave.setEchoChar(show ? (char) 0 : '•');
            txtConfirmar.setEchoChar(show ? (char) 0 : '•');
        });

        lblTipo = new JLabel("TIPO DE CUENTA:");
        rbComensal = new JRadioButton("Comensal");
        rbAdmin = new JRadioButton("Administrador");
        rbComensal.setBackground(new Color(0xF7FBFF));
        rbAdmin.setBackground(new Color(0xF7FBFF));

        grupoTipo = new ButtonGroup();
        grupoTipo.add(rbComensal);
        grupoTipo.add(rbAdmin);

        lblTipoComensal = new JLabel("TIPO DE COMENSAL:");
        cbTipoComensal = new JComboBox<>(new String[]{"Estudiante", "Profesor", "Empleado"});
        cbTipoComensal.setMaximumSize(new Dimension(350, 28));

        btnEntrar = new JButton("INICIAR SESIÓN");
        btnRecargas = new JButton("RECARGAS");
        btnRegistrar = new JButton("REGISTRARSE");
        btnModoRegistro = new JButton("¿No tienes cuenta? Regístrate aquí");
        btnVolverLogin = new JButton("Ya tengo cuenta, volver al inicio");
        btnSeleccionarFoto = new JButton("Seleccionar foto");

        styleB(btnEntrar, new Color(0x0B2D5B), Color.WHITE);
        styleB(btnRecargas, new Color(0x0B2D5B), Color.WHITE);
        styleB(btnRegistrar, new Color(0x0B2D5B), Color.WHITE);
        styleB(btnSeleccionarFoto, new Color(0x1B4F91), Color.WHITE);

        card.add(new JLabel("CÉDULA:"));
        card.add(txtCedula);
        card.add(Box.createVerticalStrut(10));
        card.add(new JLabel("CONTRASEÑA:"));
        card.add(txtClave);
        card.add(Box.createVerticalStrut(8));
        card.add(chkMostrarClave);
        card.add(Box.createVerticalStrut(10));

        card.add(lblConfirmar);
        card.add(txtConfirmar);
        card.add(Box.createVerticalStrut(10));

        card.add(lblTipo);
        card.add(rbComensal);
        card.add(rbAdmin);
        card.add(Box.createVerticalStrut(10));

        card.add(lblTipoComensal);
        card.add(cbTipoComensal);
        card.add(Box.createVerticalStrut(10));

        card.add(lblFotoRegistro);
        card.add(btnSeleccionarFoto);
        card.add(Box.createVerticalStrut(6));
        card.add(lblFotoSeleccionada);
        card.add(Box.createVerticalStrut(20));

        card.add(btnEntrar);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRecargas);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRegistrar);
        card.add(Box.createVerticalStrut(10));
        card.add(btnModoRegistro);
        card.add(btnVolverLogin);

        lblConfirmar.setVisible(false);
        txtConfirmar.setVisible(false);
        lblTipo.setVisible(false);
        rbComensal.setVisible(false);
        rbAdmin.setVisible(false);
        btnRegistrar.setVisible(false);
        btnVolverLogin.setVisible(false);

        lblTipoComensal.setVisible(false);
        cbTipoComensal.setVisible(false);
        setReferenceVisible(false);
        setPhotoVisible(false);

        centro.add(card);
        root.add(centro, BorderLayout.CENTER);
        root.add(new FooterPanel(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    public void clearFields() {
        txtCedula.setText("");
        txtClave.setText("");
        txtConfirmar.setText("");
        chkMostrarClave.setSelected(false);
        txtClave.setEchoChar('•');
        txtConfirmar.setEchoChar('•');
        setArchivoFotoSeleccionada(null);
    }

    public void setReferenceVisible(boolean visible) {
    }

    public void setPhotoVisible(boolean visible) {
        lblFotoRegistro.setVisible(visible);
        btnSeleccionarFoto.setVisible(visible);
        lblFotoSeleccionada.setVisible(visible);
    }

    public File getArchivoFotoSeleccionada() {
        return archivoFotoSeleccionada;
    }

    public void setArchivoFotoSeleccionada(File archivoFotoSeleccionada) {
        this.archivoFotoSeleccionada = archivoFotoSeleccionada;
        if (archivoFotoSeleccionada == null) {
            lblFotoSeleccionada.setText("Ninguna foto seleccionada");
        } else {
            lblFotoSeleccionada.setText("Foto seleccionada: " + archivoFotoSeleccionada.getName());
        }
    }

    private void styleB(JButton b, Color bg, Color fg) {
        b.setMaximumSize(new Dimension(350, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
    }
}
