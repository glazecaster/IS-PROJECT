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
                BorderFactory.createLineBorder(new Color(0, 0, 0, 18), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel leftHeader = new JPanel();
        leftHeader.setOpaque(false);
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));

        JLabel lblDia = new JLabel(menu.getparche() + "  " + menu.getDia());
        lblDia.setFont(lblDia.getFont().deriveFont(Font.BOLD, 16f));
        lblDia.setForeground(ComeUCVView.AZUL_OSCURO);

        JLabel lblHorario = new JLabel(menu.getHorario() == null ? "" : ("Horario: " + menu.getHorario()));
        lblHorario.setFont(lblHorario.getFont().deriveFont(Font.PLAIN, 12f));
        lblHorario.setForeground(new Color(0x173B66));
        lblHorario.setVisible(menu.getHorario() != null && !menu.getHorario().trim().isEmpty());

        leftHeader.add(lblDia);
        leftHeader.add(lblHorario);

        JLabel pill = new JLabel("Menú del día");
        pill.setOpaque(true);
        pill.setBackground(new Color(191, 227, 255, 85));
        pill.setForeground(ComeUCVView.AZUL_OSCURO);
        pill.setBorder(new EmptyBorder(4, 10, 4, 10));
        pill.setFont(pill.getFont().deriveFont(Font.BOLD, 11.5f));

        header.add(leftHeader, BorderLayout.WEST);
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

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTotales = new JLabel(
                "Calorías totales: " + menu.getTotalKcal() +
                        " | Proteínas totales: " + menu.getTotalProt() +
                        " | Carbohidratos totales: " + menu.getTotalCarb()
        );
        lblTotales.setFont(lblTotales.getFont().deriveFont(Font.BOLD, 12.5f));
        lblTotales.setForeground(ComeUCVView.TEXTO_OSCURO);

        String tarifaTxt = (menu.getTarifa() > 0 ? ("Tarifa: $" + String.format("%.2f", menu.getTarifa())) : "Tarifa: N/D");
        JLabel lblTarifa = new JLabel(tarifaTxt);
        lblTarifa.setFont(lblTarifa.getFont().deriveFont(Font.BOLD, 12.5f));
        lblTarifa.setForeground(ComeUCVView.AZUL_OSCURO);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(lblTarifa);

        footer.add(lblTotales, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(dishesBox, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}
