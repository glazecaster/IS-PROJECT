package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import model.MenuDia;
import view.listeners.MealSelectedListener;

public class MenusDisponiblesPanel extends JPanel {

    private final JLabel banner = new JLabel("Horario: ...");
    private final JPanel cardsContainer = new JPanel();

    private final JToggleButton btnDesayuno = new JToggleButton("DESAYUNO");
    private final JToggleButton btnAlmuerzo = new JToggleButton("ALMUERZO");

    private MealSelectedListener mealSelectedListener;

    public MenusDisponiblesPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildTopBar(), BorderLayout.NORTH);

        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        setBannerText("Horario: ...");

        ButtonGroup group = new ButtonGroup();
        group.add(btnDesayuno);
        group.add(btnAlmuerzo);

        styleToggle(btnDesayuno);
        styleToggle(btnAlmuerzo);

        btnDesayuno.addActionListener(e -> {
            if (mealSelectedListener != null) mealSelectedListener.onMealSelected("DESAYUNO");
        });
        btnAlmuerzo.addActionListener(e -> {
            if (mealSelectedListener != null) mealSelectedListener.onMealSelected("ALMUERZO");
        });
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(16, 18, 12, 18));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel title = new JLabel("Menús disponibles");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(ComeUCVView.AZUL_OSCURO);

        banner.setForeground(new Color(0x173B66));
        banner.setFont(banner.getFont().deriveFont(Font.PLAIN, 12.5f));

        left.add(title);
        left.add(Box.createHorizontalStrut(10));
        left.add(banner);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(btnDesayuno);
        right.add(btnAlmuerzo);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private void styleToggle(JToggleButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(0xF1F7FF));
        b.setForeground(ComeUCVView.AZUL_OSCURO);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 45), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
    }

    public void setBannerText(String text) {
        banner.setText(text);
    }

    public void setMealSelectedListener(MealSelectedListener l) {
        this.mealSelectedListener = l;
    }

    public void renderMenus(List<MenuDia> menus) {
        cardsContainer.removeAll();

        if (menus == null || menus.isEmpty()) {
            JLabel empty = new JLabel("No hay menús disponibles.");
            empty.setBorder(new EmptyBorder(30, 20, 20, 20));
            empty.setForeground(new Color(0x173B66));
            cardsContainer.add(empty);
        } else {
            for (MenuDia m : menus) {
                DayMenuCard card = new DayMenuCard(m);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardsContainer.add(card);
                cardsContainer.add(Box.createVerticalStrut(14));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    public void selectDesayuno() {
        btnDesayuno.setSelected(true);
        if (mealSelectedListener != null) mealSelectedListener.onMealSelected("DESAYUNO");
    }

    public void selectAlmuerzo() {
        btnAlmuerzo.setSelected(true);
        if (mealSelectedListener != null) mealSelectedListener.onMealSelected("ALMUERZO");
    }
}
