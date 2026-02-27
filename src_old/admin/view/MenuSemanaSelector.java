package admin.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import admin.model.Menu;
import admin.model.MenuServicio;
import admin.model.MenuSemana;

public class MenuSemanaSelector extends JDialog {

    private final MenuServicio menuServicio;
    private final MenuSemana menuSemana;

    private boolean aceptado = false;

    private JLabel lblResumenSeleccion;

    private final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};

    private final String[] DESAYUNO_COMPONENTES = {
            MenuSemana.COMP_PLATO_PRINCIPAL,
            MenuSemana.COMP_ACOMPANANTE,
            MenuSemana.COMP_BEBIDA
    };
    private final String[] DESAYUNO_LABELS = {"Plato principal", "Acompañante", "Bebida"};

    private final String[] ALMUERZO_COMPONENTES = {
            MenuSemana.COMP_PLATO_PRINCIPAL,
            MenuSemana.COMP_ACOMPANANTE,
            MenuSemana.COMP_ENSALADA,
            MenuSemana.COMP_BEBIDA
    };
    private final String[] ALMUERZO_LABELS = {"Plato principal", "Acompañante", "Ensalada", "Bebida"};

    private final Map<String, JTable> tablas = new HashMap<>();
    private final Map<String, DefaultTableModel> modelos = new HashMap<>();

    private JButton btnAceptar;
    private JButton btnCancelar;
    private JButton btnLimpiarTodo;

    public MenuSemanaSelector(JFrame parent) {
        super(parent, "Seleccionar Menú de la Semana", true);
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();

        setSize(1350, 850);
        setLocationRelativeTo(parent);

        initComponents();
        cargarTablas();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("SELECCIONAR MENÚ DE LA SEMANA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 0));
        panelCentral.setBackground(UIConstants.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCentral.add(crearPanelServicio("DESAYUNOS", true, DESAYUNO_COMPONENTES, DESAYUNO_LABELS));
        panelCentral.add(crearPanelServicio("ALMUERZOS", false, ALMUERZO_COMPONENTES, ALMUERZO_LABELS));

        add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(UIConstants.WHITE);

        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setBackground(new Color(240, 248, 255));
        panelResumen.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
                "Resumen de Selección",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                UIConstants.BLUE_DARK
        ));

        lblResumenSeleccion = new JLabel();
        lblResumenSeleccion.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblResumenSeleccion.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panelResumen.add(lblResumenSeleccion, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(UIConstants.WHITE);

        btnAceptar = new JButton("Aceptar Selección");
        btnAceptar.setBackground(UIConstants.GREEN_LIGHT);
        btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAceptar.setPreferredSize(new Dimension(190, 40));
        btnAceptar.addActionListener(e -> {
            aceptado = true;
            setVisible(false);
        });

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.addActionListener(e -> {
            aceptado = false;
            setVisible(false);
        });

        btnLimpiarTodo = new JButton("Limpiar Todo");
        btnLimpiarTodo.setBackground(UIConstants.RED_LIGHT);
        btnLimpiarTodo.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLimpiarTodo.setPreferredSize(new Dimension(150, 40));
        btnLimpiarTodo.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(MenuSemanaSelector.this,
                    "¿Está seguro de limpiar toda la selección de la semana?",
                    "Confirmar limpieza",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                menuSemana.limpiarTodo();
                cargarTablas();
                actualizarResumen();
            }
        });

        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnLimpiarTodo);

        panelInferior.add(panelResumen, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelServicio(String titulo, boolean esDesayuno, String[] componentes, String[] labels) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
                titulo,
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16),
                UIConstants.BLUE_DARK
        ));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIConstants.WHITE);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 12));

        for (int i = 0; i < componentes.length; i++) {
            String comp = componentes[i];
            String lbl = labels[i];
            tabs.addTab(lbl, crearTabComponente(esDesayuno, comp, lbl));
        }

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JComponent crearTabComponente(boolean esDesayuno, String componenteKey, String componenteLabel) {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Día", "ID", "Nombre", "Tipo", "Precio", "Sel."}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(34);
        tabla.getTableHeader().setBackground(UIConstants.BLUE_LIGHT);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);   // Día
        tabla.getColumnModel().getColumn(1).setPreferredWidth(45);   // ID
        tabla.getColumnModel().getColumn(2).setPreferredWidth(300);  // Nombre
        tabla.getColumnModel().getColumn(3).setPreferredWidth(110);  // Tipo
        tabla.getColumnModel().getColumn(4).setPreferredWidth(70);   // Precio
        tabla.getColumnModel().getColumn(5).setPreferredWidth(55);   // Sel

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color COLOR_SELECCIONADO = new Color(255, 255, 200);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    Object estrellaObj = table.getValueAt(row, 5);
                    boolean tieneEstrella = estrellaObj != null && estrellaObj.toString().equals("★");
                    if (tieneEstrella) {
                        c.setBackground(COLOR_SELECCIONADO);
                        if (column == 5) {
                            setForeground(new Color(255, 140, 0));
                            setFont(c.getFont().deriveFont(Font.BOLD, 16));
                        } else {
                            setForeground(Color.BLACK);
                            setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                        setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    if (column == 5) {
                        setFont(c.getFont().deriveFont(Font.BOLD, 16));
                    }
                }

                if (column == 0 || column == 1 || column == 4 || column == 5) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        });

        String tablaKey = (esDesayuno ? "D_" : "A_") + componenteKey;
        tablas.put(tablaKey, tabla);
        modelos.put(tablaKey, modelo);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int fila = tabla.getSelectedRow();
            if (fila == -1) return;

            String dia = String.valueOf(tabla.getValueAt(fila, 0));
            Object idObj = tabla.getValueAt(fila, 1);
            if (!(idObj instanceof Integer) || ((Integer) idObj) <= 0) {
                tabla.clearSelection();
                JOptionPane.showMessageDialog(MenuSemanaSelector.this,
                        "No hay un menú disponible para este día/componente.\n" +
                                "Primero cree un menú en la gestión de menús.",
                        "Menú no disponible",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int menuId = (Integer) idObj;
            String nombre = String.valueOf(tabla.getValueAt(fila, 2));
            boolean yaSeleccionado = "★".equals(String.valueOf(tabla.getValueAt(fila, 5)));

            if (yaSeleccionado) {
                if (esDesayuno) menuSemana.eliminarDesayuno(dia, componenteKey);
                else menuSemana.eliminarAlmuerzo(dia, componenteKey);
                cargarTablas();
                actualizarResumen();
                tabla.clearSelection();
                return;
            }

            Integer seleccionadoActual = esDesayuno
                    ? menuSemana.getDesayuno(dia, componenteKey)
                    : menuSemana.getAlmuerzo(dia, componenteKey);

            if (seleccionadoActual != null && seleccionadoActual != menuId) {
                int respuesta = JOptionPane.showConfirmDialog(MenuSemanaSelector.this,
                        "Ya hay un menú seleccionado para '" + componenteLabel + "' el día " + dia + ".\n" +
                                "¿Desea reemplazarlo por '" + nombre + "'?",
                        "Reemplazar selección",
                        JOptionPane.YES_NO_OPTION);
                if (respuesta != JOptionPane.YES_OPTION) {
                    tabla.clearSelection();
                    return;
                }
            }

            if (esDesayuno) menuSemana.setDesayuno(dia, componenteKey, menuId);
            else menuSemana.setAlmuerzo(dia, componenteKey, menuId);

            cargarTablas();
            actualizarResumen();
            tabla.clearSelection();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(620, 560));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UIConstants.WHITE);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private void cargarTablas() {
        List<Menu> todosMenus = menuServicio.getMenus();

        // Desayuno
        for (String comp : DESAYUNO_COMPONENTES) {
            DefaultTableModel m = modelos.get("D_" + comp);
            if (m == null) continue;
            m.setRowCount(0);
            for (String dia : DIAS) {
                Integer seleccionadoId = menuSemana.getDesayuno(dia, comp);
                boolean encontrado = false;
                for (Menu menu : todosMenus) {
                    if (!diaEquals(menu.getDia(), dia)) continue;
                    if (!"Desayuno".equalsIgnoreCase(menu.getTipoServicio())) continue;
                    if (!matchesComponente(menu, true, comp)) continue;

                    boolean esSeleccionado = (seleccionadoId != null && menu.getId() == seleccionadoId);
                    m.addRow(new Object[]{
                            dia,
                            menu.getId(),
                            menu.getNombre(),
                            menu.getTipo(),
                            String.format("$%.2f", menu.getPrecioVenta()),
                            esSeleccionado ? "★" : ""
                    });
                    encontrado = true;
                }
                if (!encontrado) {
                    m.addRow(new Object[]{
                            dia,
                            0,
                            "❌ No hay menús para este componente",
                            "-",
                            "-",
                            (seleccionadoId != null ? "★" : "")
                    });
                }
            }
        }

        // Almuerzo
        for (String comp : ALMUERZO_COMPONENTES) {
            DefaultTableModel m = modelos.get("A_" + comp);
            if (m == null) continue;
            m.setRowCount(0);
            for (String dia : DIAS) {
                Integer seleccionadoId = menuSemana.getAlmuerzo(dia, comp);
                boolean encontrado = false;
                for (Menu menu : todosMenus) {
                    if (!diaEquals(menu.getDia(), dia)) continue;
                    if (!"Almuerzo".equalsIgnoreCase(menu.getTipoServicio())) continue;
                    if (!matchesComponente(menu, false, comp)) continue;

                    boolean esSeleccionado = (seleccionadoId != null && menu.getId() == seleccionadoId);
                    m.addRow(new Object[]{
                            dia,
                            menu.getId(),
                            menu.getNombre(),
                            menu.getTipo(),
                            String.format("$%.2f", menu.getPrecioVenta()),
                            esSeleccionado ? "★" : ""
                    });
                    encontrado = true;
                }
                if (!encontrado) {
                    m.addRow(new Object[]{
                            dia,
                            0,
                            "❌ No hay menús para este componente",
                            "-",
                            "-",
                            (seleccionadoId != null ? "★" : "")
                    });
                }
            }
        }

        actualizarResumen();
    }

    private boolean diaEquals(String diaMenu, String diaTabla) {
        if (diaMenu == null || diaTabla == null) return false;
        if (diaMenu.equalsIgnoreCase(diaTabla)) return true;
        if ("Miércoles".equalsIgnoreCase(diaTabla) && "Miercoles".equalsIgnoreCase(diaMenu)) return true;
        if ("Miércoles".equalsIgnoreCase(diaMenu) && "Miercoles".equalsIgnoreCase(diaTabla)) return true;
        return false;
    }

    private boolean matchesComponente(Menu menu, boolean esDesayuno, String componenteKey) {
        String tipo = menu.getTipo() == null ? "" : menu.getTipo().trim().toLowerCase();
        String tipoN = tipo
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");

        if (MenuSemana.COMP_PLATO_PRINCIPAL.equals(componenteKey)) {
            if (tipoN.contains("principal") || tipoN.contains("plato")) return true;
            if (esDesayuno && (tipoN.equals("desayuno") || tipoN.contains("desay"))) return true;
            return false;
        }
        if (MenuSemana.COMP_ACOMPANANTE.equals(componenteKey)) {
            return tipoN.contains("acompan") || tipoN.contains("guarn") || tipoN.contains("acompa");
        }
        if (MenuSemana.COMP_BEBIDA.equals(componenteKey)) {
            return tipoN.contains("bebida") || tipoN.contains("jugo") || tipoN.contains("cafe") || tipoN.contains("te");
        }
        if (MenuSemana.COMP_ENSALADA.equals(componenteKey)) {
            return tipoN.contains("ensalada");
        }
        return true;
    }

    private void actualizarResumen() {
        StringBuilder resumen = new StringBuilder("<html>");
        resumen.append("<div style='font-family:monospace;'>");
        resumen.append("<table border='1' cellpadding='5' cellspacing='0'>");
        resumen.append("<tr>");
        resumen.append("<th>Día</th>");
        resumen.append("<th colspan='3'>Desayuno</th>");
        resumen.append("<th colspan='4'>Almuerzo</th>");
        resumen.append("</tr>");
        resumen.append("<tr>");
        resumen.append("<th></th>");
        resumen.append("<th>P</th><th>A</th><th>B</th>");
        resumen.append("<th>P</th><th>A</th><th>E</th><th>B</th>");
        resumen.append("</tr>");

        int totalSel = 0;

        for (String dia : DIAS) {
            resumen.append("<tr>");
            resumen.append("<td><b>").append(dia).append("</b></td>");

            totalSel += appendCelda(resumen, menuSemana.getDesayuno(dia, MenuSemana.COMP_PLATO_PRINCIPAL));
            totalSel += appendCelda(resumen, menuSemana.getDesayuno(dia, MenuSemana.COMP_ACOMPANANTE));
            totalSel += appendCelda(resumen, menuSemana.getDesayuno(dia, MenuSemana.COMP_BEBIDA));

            totalSel += appendCelda(resumen, menuSemana.getAlmuerzo(dia, MenuSemana.COMP_PLATO_PRINCIPAL));
            totalSel += appendCelda(resumen, menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ACOMPANANTE));
            totalSel += appendCelda(resumen, menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ENSALADA));
            totalSel += appendCelda(resumen, menuSemana.getAlmuerzo(dia, MenuSemana.COMP_BEBIDA));

            resumen.append("</tr>");
        }

        resumen.append("</table>");
        resumen.append("<br><b>Total selecciones:</b> ").append(totalSel);
        resumen.append("</div></html>");
        lblResumenSeleccion.setText(resumen.toString());
    }

    private int appendCelda(StringBuilder sb, Integer id) {
        if (id == null) {
            sb.append("<td>—</td>");
            return 0;
        }
        Menu menu = menuServicio.getMenuPorId(id);
        if (menu != null) {
            sb.append("<td bgcolor='#FFFF99' title='")
                    .append(escape(menu.getNombre()))
                    .append("'>★</td>");
        } else {
            sb.append("<td bgcolor='#FFFF99' title='ID: ")
                    .append(id)
                    .append("'>★</td>");
        }
        return 1;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public MenuSemana getMenuSemana() {
        return menuSemana;
    }
}
