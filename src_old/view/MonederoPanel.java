package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MonederoPanel extends JPanel {

    private final JLabel lblUsuario = new JLabel("Usuario: v12345678");
    private final JLabel lblSaldo = new JLabel("$9.50");

    public MonederoPanel() {
        setLayout(new GridBagLayout());
        setBackground(ComeUCVView.BLANCO);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintDecor((Graphics2D) g);
            }
        };
        card.setLayout(new BorderLayout(0, 14));
        card.setBackground(new Color(0xF7FBFF));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(720, 340));

        JLabel titulo = new JLabel("Mi monedero");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(ComeUCVView.AZUL_OSCURO);

        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD, 13.5f));
        lblUsuario.setForeground(ComeUCVView.TEXTO_OSCURO);

        JLabel subt = new JLabel("Saldo actual");
        subt.setFont(subt.getFont().deriveFont(Font.PLAIN, 12.5f));
        subt.setForeground(new Color(0x173B66));

        lblSaldo.setFont(lblSaldo.getFont().deriveFont(Font.BOLD, 42f));
        lblSaldo.setForeground(ComeUCVView.AZUL_OSCURO);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(lblUsuario);
        body.add(Box.createVerticalStrut(18));
        body.add(subt);
        body.add(Box.createVerticalStrut(6));
        body.add(lblSaldo);

        card.add(titulo, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        add(card);
    }

    private void paintDecor(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(191, 227, 255, 50));
        g2.fillOval(w - 220, -50, 260, 260);

        g2.setColor(new Color(11, 45, 91, 25));
        g2.fillOval(w - 340, 80, 220, 220);

        g2.setColor(new Color(11, 45, 91, 18));
        g2.setStroke(new BasicStroke(2f));
        g2.drawArc(-40, h - 140, 260, 180, 0, 180);
        g2.drawArc(40, h - 120, 260, 180, 0, 180);
    }

    public void setUsuario(String usuario) { lblUsuario.setText("Usuario: " + usuario); }
    public void setSaldo(String saldo) { lblSaldo.setText("$" + saldo); }
}
