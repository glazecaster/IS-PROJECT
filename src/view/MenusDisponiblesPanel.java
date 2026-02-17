package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.MenuDia;

public class MenusDisponiblesPanel extends JPanel {

    private final JLabel banner = new JLabel(" ", SwingConstants.LEFT);
    private final JPanel grid = new JPanel(new GridBagLayout());

    public MenusDisponiblesPanel() {
        setLayout(new BorderLayout());
        setBackground(ComeUCVView.BLANCO);

        JLabel title = new JLabel("Menús disponibles (Semana)", SwingConstants.LEFT);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(ComeUCVView.TEXTO_OSCURO);

        banner.setOpaque(true);
        banner.setBackground(new Color(0xF7FBFF));
        banner.setForeground(ComeUCVView.AZUL_OSCURO);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11,45,91,35), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(banner, BorderLayout.CENTER);

        grid.setBackground(ComeUCVView.BLANCO);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ComeUCVView.BLANCO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void setBannerText(String text) {
        banner.setText(text);
    }

 
    public void renderMenus(List<MenuDia> menus) {
        grid.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;

        for (int i = 0; i < menus.size(); i++) {
            int row = i / 3;
            int col = i % 3;

            gbc.gridx = col;
            gbc.gridy = row;
            gbc.insets = new Insets(0, 0, 14, (col == 2 ? 0 : 14));

            grid.add(new DayMenuCard(menus.get(i)), gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = (menus.size() + 2) / 3;
        gbc.weighty = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0,0,0,0);
        grid.add(Box.createVerticalGlue(), gbc);

        revalidate();
        repaint();
    }
}
