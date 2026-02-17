package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ComeUCVView extends JFrame {

    // Paleta
    public static final Color AZUL_OSCURO = new Color(0x0B2D5B);
    public static final Color AZUL_CLARO  = new Color(0xBFE3FF);
    public static final Color BLANCO      = Color.WHITE;
    public static final Color TEXTO_OSCURO= new Color(0x0B1B2B);

    // Cards
    private final JPanel cards = new JPanel(new CardLayout());
    public static final String CARD_MENUS = "menus";
    public static final String CARD_MONEDERO = "monedero";

    // Tabs
    private final JToggleButton tabMenus = new JToggleButton("Menús disponibles");
    private final JToggleButton tabMonedero = new JToggleButton("Mi monedero");
    private boolean menusEnabled = true;

    // Subvistas
    private final MenusDisponiblesPanel menusPanel = new MenusDisponiblesPanel();
    private final MonederoPanel monederoPanel = new MonederoPanel();
    private final HeaderPanel header = new HeaderPanel("ComeUCV");

    public ComeUCVView() {
        super("ComeUCV");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BLANCO);

        // Header
        root.add(header, BorderLayout.NORTH);

        // Barra tabs
        root.add(buildTabsBar(), BorderLayout.BEFORE_FIRST_LINE);

        // Cards
        cards.setBackground(BLANCO);
        cards.add(menusPanel, CARD_MENUS);
        cards.add(monederoPanel, CARD_MONEDERO);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BLANCO);
        center.setBorder(new EmptyBorder(12, 14, 12, 14));
        center.add(cards, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(new FooterPanel(), BorderLayout.SOUTH);

        setContentPane(root);

        // Estado inicial (por defecto Menús; el controller puede cambiarlo)
        selectTabMenus();

         //  listeners
        tabMenus.addActionListener(e -> {
            if (menusEnabled) {
                selectTabMenus();
            } else {
                // Si está deshabilitado, forzamos monedero
                selectTabMonedero();
            }
        });

        tabMonedero.addActionListener(e -> selectTabMonedero());

    }

    private JPanel buildTabsBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AZUL_CLARO);
        bar.setBorder(new EmptyBorder(10, 14, 10, 14));

        ButtonGroup group = new ButtonGroup();
        group.add(tabMenus);
        group.add(tabMonedero);

        styleTab(tabMenus);
        styleTab(tabMonedero);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(tabMenus);
        left.add(tabMonedero);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    private void styleTab(AbstractButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(BLANCO);
        b.setForeground(TEXTO_OSCURO);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showCard(String name) {
        ((CardLayout) cards.getLayout()).show(cards, name);
    }

    public void selectTabMenus() {
        tabMenus.setSelected(true);
        tabMonedero.setSelected(false);

        tabMenus.setBackground(AZUL_OSCURO);
        tabMenus.setForeground(BLANCO);

        tabMonedero.setBackground(BLANCO);
        tabMonedero.setForeground(TEXTO_OSCURO);

        showCard(CARD_MENUS);
    }

    public void selectTabMonedero() {
        tabMonedero.setSelected(true);
        tabMenus.setSelected(false);

        tabMonedero.setBackground(AZUL_OSCURO);
        tabMonedero.setForeground(BLANCO);

        tabMenus.setBackground(BLANCO);
        tabMenus.setForeground(TEXTO_OSCURO);

        showCard(CARD_MONEDERO);
    }

    
    public void setMenusEnabled(boolean enabled) {
        menusEnabled = enabled;
        tabMenus.setEnabled(enabled);

        if (!enabled) {
            selectTabMonedero();
        }
    }

    public boolean isMenusEnabled() {
        return menusEnabled;
    }

    // Getters para el Controller
    public HeaderPanel getHeader() { return header; }
    public JToggleButton getTabMenus() { return tabMenus; }
    public JToggleButton getTabMonedero() { return tabMonedero; }
    public MenusDisponiblesPanel getMenusPanel() { return menusPanel; }
    public MonederoPanel getMonederoPanel() { return monederoPanel; }
}
