package view;


import java.awt.*;
import javax.swing.*;

public class FooterPanel extends JPanel {

    public FooterPanel() {
        setPreferredSize(new Dimension(100, 70));
        setBackground(ComeUCVView.AZUL_OSCURO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();

        g2.setColor(new Color(255,255,255,22));
        g2.fillRoundRect(14, 12, w - 28, 10, 12, 12);

        g2.setColor(new Color(191,227,255,25));
        g2.fillRoundRect(14, 30, w - 28, 10, 12, 12);

        g2.setColor(new Color(255,255,255,18));
        g2.fillRoundRect(14, 48, w - 28, 10, 12, 12);

        g2.dispose();
    }
}
