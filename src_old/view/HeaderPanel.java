package view;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeaderPanel extends JPanel {

    private final JLabel lblTitulo = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblUsuario = new JLabel("Usuario: ", SwingConstants.LEFT);

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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(userBox);

        lblTitulo.setText(titulo);
        lblTitulo.setForeground(ComeUCVView.BLANCO);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 30f));

        add(top, BorderLayout.NORTH);
        add(lblTitulo, BorderLayout.CENTER);
    }

    public void setUsuarioText(String usuario) {
        lblUsuario.setText("Usuario: " + usuario);
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
