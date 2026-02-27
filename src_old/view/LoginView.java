package view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    public JButton btnRegistrar;
    public JButton btnModoRegistro;
    public JButton btnVolverLogin;

    public JRadioButton rbComensal;
    public JRadioButton rbAdmin;
    public ButtonGroup grupoTipo;
    public JLabel lblTipo;

    public JLabel lblTipoComensal;
    public JComboBox<String> cbTipoComensal;

    public JLabel lblFoto;
    public JButton btnSeleccionarFoto;
    public JLabel lblFotoRuta;
    public String fotoRuta; 

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
        cbTipoComensal = new JComboBox<>(new String[]{"Estudiante", "Profesor"});
        cbTipoComensal.setMaximumSize(new Dimension(350, 28));

        lblFoto = new JLabel("FOTO (para reconocimiento):");
        btnSeleccionarFoto = new JButton("Seleccionar foto (JPG/PNG)");
        lblFotoRuta = new JLabel("(no seleccionada)");
        lblFotoRuta.setFont(lblFotoRuta.getFont().deriveFont(Font.PLAIN, 11.5f));
        lblFotoRuta.setForeground(new Color(0x173B66));
        fotoRuta = "";

        btnSeleccionarFoto.setFocusPainted(false);
        btnSeleccionarFoto.setBackground(Color.WHITE);
        btnSeleccionarFoto.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSeleccionarFoto.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, JPEG, PNG)", "jpg", "jpeg", "png"));
            int opt = chooser.showOpenDialog(this);
            if (opt == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (f != null) {
                    fotoRuta = f.getAbsolutePath();
                    lblFotoRuta.setText(f.getName());
                }
            }
        });

        btnEntrar = new JButton("INICIAR SESIÓN");
        btnRegistrar = new JButton("REGISTRARSE");
        btnModoRegistro = new JButton("¿No tienes cuenta? Regístrate aquí");
        btnVolverLogin = new JButton("Ya tengo cuenta, volver al inicio");

        styleB(btnEntrar, new Color(0x0B2D5B), Color.WHITE);
        styleB(btnRegistrar, new Color(0x0B2D5B), Color.WHITE);

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

        card.add(lblFoto);
        card.add(btnSeleccionarFoto);
        card.add(lblFotoRuta);
        card.add(Box.createVerticalStrut(20));

        card.add(btnEntrar);
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
        lblFoto.setVisible(false);
        btnSeleccionarFoto.setVisible(false);
        lblFotoRuta.setVisible(false);

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
        clearFoto();
    }

    public void clearFoto() {
        fotoRuta = "";
        lblFotoRuta.setText("(no seleccionada)");
    }

    private void styleB(JButton b, Color bg, Color fg) {
        b.setMaximumSize(new Dimension(350, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
    }
}
