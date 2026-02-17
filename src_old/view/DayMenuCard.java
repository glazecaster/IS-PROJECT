package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.MenuDia;
import model.Plato;

public class DayMenuCard extends JPanel {

    public DayMenuCard(MenuDia menu) {
        setLayout(new BorderLayout());
        setBackground(ComeUCVView.BLANCO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0,18), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblDia = new JLabel(menu.getparche() + "  " + menu.getDia());
        lblDia.setFont(lblDia.getFont().deriveFont(Font.BOLD, 16f));
        lblDia.setForeground(ComeUCVView.AZUL_OSCURO);

        // Detalle decorativo (sin funcionalidad)
        JLabel pill = new JLabel("Menú del día");
        pill.setOpaque(true);
        pill.setBackground(new Color(191, 227, 255, 85));
        pill.setForeground(ComeUCVView.AZUL_OSCURO);
        pill.setBorder(new EmptyBorder(4, 10, 4, 10));
        pill.setFont(pill.getFont().deriveFont(Font.BOLD, 11.5f));

        header.add(lblDia, BorderLayout.WEST);
        header.add(pill, BorderLayout.EAST);

        JPanel dishesBox = new JPanel();
        dishesBox.setLayout(new BoxLayout(dishesBox, BoxLayout.Y_AXIS));
        dishesBox.setBackground(ComeUCVView.BLANCO);

        for (Plato p : menu.getPlatos()) {
            dishesBox.add(new DishPanel(
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getTipo(),
                    p.getProteinas(),
                    p.getCarbohidratos(),
                    p.getCalorias(),
                    p.getIcono()
            ));
            dishesBox.add(Box.createVerticalStrut(8));
        }

        JLabel lblTotales = new JLabel(
                "Calorías totales: " + menu.getTotalKcal() +
                " | Proteínas totales: " + menu.getTotalProt() +
                " | Carbohidratos totales: " + menu.getTotalCarb()
        );
        lblTotales.setFont(lblTotales.getFont().deriveFont(Font.BOLD, 12.5f));
        lblTotales.setForeground(ComeUCVView.TEXTO_OSCURO);
        lblTotales.setBorder(new EmptyBorder(10, 0, 0, 0));

        add(header, BorderLayout.NORTH);
        add(dishesBox, BorderLayout.CENTER);
        add(lblTotales, BorderLayout.SOUTH);
    }
}
