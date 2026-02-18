package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import modelo.Menu;
import modelo.MenuServicio;
import modelo.MenuSemana;

public class MenuSemanaSelector extends JDialog {
    private MenuServicio menuServicio;
    private MenuSemana menuSemana;
    private JFrame parentFrame;
    
    private JTable tablaDesayunos;
    private JTable tablaAlmuerzos;
    private DefaultTableModel modeloTablaDesayunos;
    private DefaultTableModel modeloTablaAlmuerzos;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JButton btnLimpiarTodo;
    private JLabel lblResumenSeleccion;
    
    private boolean aceptado = false;
    
    
    private final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    
    public MenuSemanaSelector(JFrame parent) {
        super(parent, "Seleccionar Menú de la Semana", true);
        this.parentFrame = parent;
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        
        setSize(1300, 800);
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
        
        
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 0));
        panelCentral.setBackground(UIConstants.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //Tabla de Desayunos
        JPanel panelDesayunos = crearPanelTabla("DESAYUNOS", true);
        panelDesayunos.setBackground(UIConstants.WHITE);
        
        //Tabla de Almuerzos
        JPanel panelAlmuerzos = crearPanelTabla("ALMUERZOS", false);
        panelAlmuerzos.setBackground(UIConstants.WHITE);
        
        panelCentral.add(panelDesayunos);
        panelCentral.add(panelAlmuerzos);
        
        
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
        lblResumenSeleccion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelResumen.add(lblResumenSeleccion, BorderLayout.CENTER);
        
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(UIConstants.WHITE);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(UIConstants.WHITE);
        
        btnAceptar = new JButton("Aceptar Selección");
        btnAceptar.setBackground(UIConstants.GREEN_LIGHT);
        btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAceptar.setPreferredSize(new Dimension(180, 40));
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptado = true;
                setVisible(false);
            }
        });
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptado = false;
                setVisible(false);
            }
        });
        
        btnLimpiarTodo = new JButton("Limpiar Todo");
        btnLimpiarTodo.setBackground(UIConstants.RED_LIGHT);
        btnLimpiarTodo.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLimpiarTodo.setPreferredSize(new Dimension(150, 40));
        btnLimpiarTodo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(MenuSemanaSelector.this,
                    "¿Está seguro de limpiar toda la selección de la semana?",
                    "Confirmar limpieza",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    menuSemana.limpiarTodo();
                    cargarTablas();
                    actualizarResumen();
                }
            }
        });
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnLimpiarTodo);
        
        panelInferior.add(panelResumen, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        
        add(lblTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTabla(String titulo, boolean esDesayuno) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            titulo,
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 16),
            UIConstants.BLUE_DARK
        ));
        
        
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Día", "ID", "Nombre", "Precio", "Seleccionado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(35);
        tabla.getTableHeader().setBackground(UIConstants.BLUE_LIGHT);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);  // Día
        tabla.getColumnModel().getColumn(1).setPreferredWidth(40);  // ID
        tabla.getColumnModel().getColumn(2).setPreferredWidth(300); // Nombre
        tabla.getColumnModel().getColumn(3).setPreferredWidth(70);  // Precio
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100); // Seleccionado
        
        
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color COLOR_SELECCIONADO = new Color(255, 255, 200);
            private final Color COLOR_OTRO_SELECCIONADO = new Color(255, 245, 220);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Verificar si esta fila tiene estrella (está seleccionada)
                    Object estrellaObj = table.getValueAt(row, 4);
                    boolean tieneEstrella = estrellaObj != null && estrellaObj.toString().equals("★");
                    
                    if (tieneEstrella) {
                        // Toda la fila se pinta de amarillo claro
                        c.setBackground(COLOR_SELECCIONADO);
                        
                        // La columna de la estrella se resalta más
                        if (column == 4) {
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
                    if (column == 4) {
                        setFont(c.getFont().deriveFont(Font.BOLD, 16));
                    }
                }
                
                
                if (column == 0 || column == 1 || column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        });
        
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                String dia = tabla.getValueAt(fila, 0).toString();
                Object idObj = tabla.getValueAt(fila, 1);
                
                
                if (idObj == null || !(idObj instanceof Integer) || (Integer)idObj == 0) {
                    tabla.clearSelection();
                    JOptionPane.showMessageDialog(MenuSemanaSelector.this,
                        "No hay un menú disponible para este día.\nPrimero debe crear un menú en la gestión de menús.",
                        "Menú no disponible",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int menuId = (Integer) idObj;
                String nombre = tabla.getValueAt(fila, 2).toString();
                
                // Verificar si este menú ya está seleccionado
                Object estrellaObj = tabla.getValueAt(fila, 4);
                boolean yaSeleccionado = estrellaObj != null && estrellaObj.toString().equals("★");
                
                String tipo = esDesayuno ? "desayuno" : "almuerzo";
                
                if (yaSeleccionado) {
                    
                    if (esDesayuno) {
                        menuSemana.eliminarDesayuno(dia);
                    } else {
                        menuSemana.eliminarAlmuerzo(dia);
                    }
                    cargarTablas();
                    actualizarResumen();
                    
                } else {
                    // Si no está seleccionado, verificamos si ya hay otro seleccionado para este día
                    Integer seleccionadoActual = esDesayuno ? 
                        menuSemana.getDesayuno(dia) : menuSemana.getAlmuerzo(dia);
                    
                    if (seleccionadoActual != null) {
                        // Ya hay otro menú seleccionado para este día - preguntar si reemplazar
                        int respuesta = JOptionPane.showConfirmDialog(MenuSemanaSelector.this,
                            "Ya hay un " + tipo + " seleccionado para el día " + dia + ".\n" +
                            "¿Desea reemplazarlo por '" + nombre + "'?",
                            "Reemplazar selección",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (respuesta == JOptionPane.YES_OPTION) {
                            if (esDesayuno) {
                                menuSemana.setDesayuno(dia, menuId);
                            } else {
                                menuSemana.setAlmuerzo(dia, menuId);
                            }
                            cargarTablas();
                            actualizarResumen();
                        }
                    } else {
                        
                        if (esDesayuno) {
                            menuSemana.setDesayuno(dia, menuId);
                        } else {
                            menuSemana.setAlmuerzo(dia, menuId);
                        }
                        cargarTablas();
                        actualizarResumen();
                    }
                }
                
                
                tabla.clearSelection();
            }
        });
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(550, 500));
        panel.add(scroll, BorderLayout.CENTER);
        
        
        if (esDesayuno) {
            this.tablaDesayunos = tabla;
            this.modeloTablaDesayunos = modeloTabla;
        } else {
            this.tablaAlmuerzos = tabla;
            this.modeloTablaAlmuerzos = modeloTabla;
        }
        
        return panel;
    }
    
    private void cargarTablas() {
        
        modeloTablaDesayunos.setRowCount(0);
        modeloTablaAlmuerzos.setRowCount(0);
        
        
        List<Menu> todosMenus = menuServicio.getMenus();
        
        
        for (String dia : DIAS) {
            Integer seleccionadoId = menuSemana.getDesayuno(dia);
            boolean encontrado = false;
            
            
            for (Menu menu : todosMenus) {
                if (menu.getDia().equalsIgnoreCase(dia) && 
                    menu.getTipoServicio().equalsIgnoreCase("Desayuno")) {
                    
                    String nombre = menu.getNombre();
                    boolean esSeleccionado = seleccionadoId != null && menu.getId() == seleccionadoId;
                    double precio = menu.getPrecioVenta();
                    
                    modeloTablaDesayunos.addRow(new Object[]{
                        dia,
                        menu.getId(),
                        nombre,
                        String.format("$%.2f", precio),
                        esSeleccionado ? "★" : ""
                    });
                    encontrado = true;
                }
            }
            
            
            if (!encontrado) {
                modeloTablaDesayunos.addRow(new Object[]{
                    dia, 
                    0, 
                    "❌ No hay desayunos disponibles", 
                    "-", 
                    seleccionadoId != null ? "★" : ""
                });
            }
        }
        
        
        for (String dia : DIAS) {
            Integer seleccionadoId = menuSemana.getAlmuerzo(dia);
            boolean encontrado = false;
            
            
            for (Menu menu : todosMenus) {
                if (menu.getDia().equalsIgnoreCase(dia) && 
                    menu.getTipoServicio().equalsIgnoreCase("Almuerzo")) {
                    
                    String nombre = menu.getNombre();
                    boolean esSeleccionado = seleccionadoId != null && menu.getId() == seleccionadoId;
                    double precio = menu.getPrecioVenta();
                    
                    modeloTablaAlmuerzos.addRow(new Object[]{
                        dia,
                        menu.getId(),
                        nombre,
                        String.format("$%.2f", precio),
                        esSeleccionado ? "★" : ""
                    });
                    encontrado = true;
                }
            }
            
            
            if (!encontrado) {
                modeloTablaAlmuerzos.addRow(new Object[]{
                    dia, 
                    0, 
                    "❌ No hay almuerzos disponibles", 
                    "-", 
                    seleccionadoId != null ? "★" : ""
                });
            }
        }
        
        actualizarResumen();
    }
    
    private void actualizarResumen() {
        StringBuilder resumen = new StringBuilder("<html>");
        resumen.append("<table border='1' cellpadding='5' cellspacing='0'>");
        resumen.append("<tr><th>Día</th><th>Desayuno</th><th>Almuerzo</th></tr>");
        
        Map<String, Integer> desayunos = menuSemana.getDesayunos();
        Map<String, Integer> almuerzos = menuSemana.getAlmuerzos();
        
        for (String dia : DIAS) {
            resumen.append("<tr>");
            resumen.append("<td><b>").append(dia).append("</b></td>");
            
            // Desayuno
            Integer idDesayuno = desayunos.get(dia);
            if (idDesayuno != null) {
                Menu menu = menuServicio.getMenuPorId(idDesayuno);
                if (menu != null) {
                    resumen.append("<td bgcolor='#FFFF99'>★ ").append(menu.getNombre())
                           .append(" ($").append(String.format("%.2f", menu.getPrecioVenta())).append(")</td>");
                } else {
                    resumen.append("<td bgcolor='#FFFF99'>★ ID: ").append(idDesayuno).append("</td>");
                }
            } else {
                resumen.append("<td>—</td>");
            }
            
            // Almuerzo
            Integer idAlmuerzo = almuerzos.get(dia);
            if (idAlmuerzo != null) {
                Menu menu = menuServicio.getMenuPorId(idAlmuerzo);
                if (menu != null) {
                    resumen.append("<td bgcolor='#FFFF99'>★ ").append(menu.getNombre())
                           .append(" ($").append(String.format("%.2f", menu.getPrecioVenta())).append(")</td>");
                } else {
                    resumen.append("<td bgcolor='#FFFF99'>★ ID: ").append(idAlmuerzo).append("</td>");
                }
            } else {
                resumen.append("<td>—</td>");
            }
            
            resumen.append("</tr>");
        }
        
        resumen.append("</table>");
        resumen.append("<br><b>Total seleccionados:</b> ");
        resumen.append(desayunos.size()).append(" desayunos, ");
        resumen.append(almuerzos.size()).append(" almuerzos");
        resumen.append("</html>");
        
        lblResumenSeleccion.setText(resumen.toString());
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
    
    public MenuSemana getMenuSemana() {
        return menuSemana;
    }
}