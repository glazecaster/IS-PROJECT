package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
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

    public LoginView() {
        super("ComeUCV - Acceso");
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(new HeaderPanel("Portal de Acceso"), BorderLayout.NORTH);

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
        
        lblTipo = new JLabel("TIPO DE CUENTA:");
        rbComensal = new JRadioButton("Comensal");
        rbAdmin = new JRadioButton("Administrador");
        rbComensal.setBackground(new Color(0xF7FBFF));
        rbAdmin.setBackground(new Color(0xF7FBFF));
        
        grupoTipo = new ButtonGroup();
        grupoTipo.add(rbComensal);
        grupoTipo.add(rbAdmin);
        
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
        card.add(Box.createVerticalStrut(10));
        
        card.add(lblConfirmar);
        card.add(txtConfirmar);
        card.add(Box.createVerticalStrut(10));

        card.add(lblTipo);
        card.add(rbComensal);
        card.add(rbAdmin);
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

        centro.add(card);
        root.add(centro, BorderLayout.CENTER);
        root.add(new FooterPanel(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void styleB(JButton b, Color bg, Color fg) {
        b.setMaximumSize(new Dimension(350, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
    }
}