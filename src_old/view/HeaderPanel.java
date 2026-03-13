package view;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeaderPanel extends JPanel {

    private final JLabel lblTitulo = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblRol = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblUsuario = new JLabel("Usuario: ", SwingConstants.LEFT);

    private final JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    public HeaderPanel(String titulo) {
        setLayout(new BorderLayout());
        setBackground(ComeUCVView.AZUL_OSCURO);
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setPreferredSize(new Dimension(100, 110));

        JPanel userBox = new JPanel(new BorderLayout());
        userBox.setBackground(ComeUCVView.BLANCO);
        userBox.setBorder(new EmptyBorder(6, 10, 6, 10));
        lblUsuario.setForeground(ComeUCVView.TEXTO_OSCURO);
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD, 12.5f));
        userBox.add(lblUsuario, BorderLayout.CENTER);

        actionsPanel.setOpaque(false);
        actionsPanel.setVisible(false);

        JPanel leftStack = new JPanel();
        leftStack.setOpaque(false);
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));
        leftStack.add(userBox);
        leftStack.add(Box.createVerticalStrut(6));
        leftStack.add(actionsPanel);

        JPanel westWrap = new JPanel(new BorderLayout());
        westWrap.setOpaque(false);
        westWrap.add(leftStack, BorderLayout.NORTH);

        lblTitulo.setText(titulo);
        lblTitulo.setForeground(ComeUCVView.BLANCO);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 30f));

        lblRol.setForeground(new Color(191, 227, 255));
        lblRol.setFont(lblRol.getFont().deriveFont(Font.BOLD, 14f));

        JPanel centerStack = new JPanel();
        centerStack.setOpaque(false);
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setBorder(new EmptyBorder(2, 0, 0, 0));

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerStack.add(Box.createVerticalStrut(6));
        centerStack.add(lblTitulo);
        centerStack.add(Box.createVerticalStrut(4));
        centerStack.add(lblRol);
        centerStack.add(Box.createVerticalGlue());

        add(westWrap, BorderLayout.WEST);
        add(centerStack, BorderLayout.CENTER);
    }

    public void setLeftActions(JComponent... components) {
        actionsPanel.removeAll();
        boolean show = components != null && components.length > 0;
        if (show) {
            for (JComponent c : components) {
                if (c != null) actionsPanel.add(c);
            }
        }
        actionsPanel.setVisible(show);
        actionsPanel.revalidate();
        actionsPanel.repaint();
    }

    public void setUsuarioText(String usuario) {
        lblUsuario.setText("Usuario: " + usuario);
    }

    public void setRolText(String rol) {
        if (rol == null) rol = "";
        lblRol.setText(rol.toUpperCase());
        lblRol.setVisible(!rol.trim().isEmpty());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color deco1 = new Color(255, 255, 255, 28);
        Color deco2 = new Color(191, 227, 255, 35);

        g2.setColor(deco2);
        g2.fillOval(w - 160, -30, 220, 220);

        g2.setColor(deco1);
        g2.fillOval(w - 260, 30, 180, 180);

        g2.setColor(deco1);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(0, h - 8, w, h - 8);

        g2.dispose();
    }
}
