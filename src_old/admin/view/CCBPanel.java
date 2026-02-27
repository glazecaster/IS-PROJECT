package admin.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import admin.model.CCBCalculator;
import admin.model.Menu;
import admin.model.MenuServicio;
import admin.controller.MenuControlador;

public class CCBPanel extends JPanel {
    private CCBCalculator ccbCalculator;
    private MenuServicio menuServicio;
    private JFrame parentFrame;
    private JFrame ventanaAnterior;
    
    private JLabel lblCCBBase;
    private JLabel lblCCBLabel;
    private JComboBox<String> comboTipoUsuario;
    private JComboBox<String> comboTipoServicio;
    private JComboBox<String> comboDia;
    private JTable tablaPrecios;
    private DefaultTableModel modeloTabla;
    private JTextArea txtDesglose;
    private JLabel lblInfoSubsidio;
    private JLabel lblEstadoIngredientes;
    private JButton btnVolver;
    
    public CCBPanel(JFrame parent) {
        this(parent, null);
    }
    
    public CCBPanel(JFrame parent, JFrame ventanaAnterior) {
        this.parentFrame = parent;
        this.ventanaAnterior = ventanaAnterior;
        this.ccbCalculator = new CCBCalculator();
        this.menuServicio = new MenuServicio();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        actualizarCCB();
        actualizarPrecios();
    }
    
    private void initComponents() {
        
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(UIConstants.WHITE);
        panelSuperior.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK), 
            "Configuración de Cálculo CCB",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            UIConstants.BLUE_DARK
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        gbc.gridx = 0; gbc.gridy = 0;
        lblCCBLabel = new JLabel("CCB Base calculado (bandejas: 500):");
        lblCCBLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelSuperior.add(lblCCBLabel, gbc);
        
        lblCCBBase = new JLabel("$0.00");
        lblCCBBase.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCCBBase.setForeground(UIConstants.BLUE_DARK);
        gbc.gridx = 1;
        panelSuperior.add(lblCCBBase, gbc);
        
        
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel lblUsuario = new JLabel("Tipo Usuario:");
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelSuperior.add(lblUsuario, gbc);
        
        comboTipoUsuario = new JComboBox<>(new String[]{
            "Estudiante", "Profesor", "Administrativo"
        });
        comboTipoUsuario.setPreferredSize(new Dimension(150, 30));
        comboTipoUsuario.setBackground(Color.WHITE);
        comboTipoUsuario.addActionListener(e -> {
            actualizarPrecios();
            actualizarInfoSubsidio();
        });
        gbc.gridx = 3;
        panelSuperior.add(comboTipoUsuario, gbc);
        
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblServicio = new JLabel("Tipo Servicio:");
        lblServicio.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelSuperior.add(lblServicio, gbc);
        
        comboTipoServicio = new JComboBox<>(new String[]{
            "Desayuno", "Almuerzo"
        });
        comboTipoServicio.setPreferredSize(new Dimension(150, 30));
        comboTipoServicio.setBackground(Color.WHITE);
        comboTipoServicio.addActionListener(e -> actualizarPrecios());
        gbc.gridx = 1;
        panelSuperior.add(comboTipoServicio, gbc);
        
        
        gbc.gridx = 2; gbc.gridy = 1;
        JLabel lblDia = new JLabel("Filtrar por Día:");
        lblDia.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelSuperior.add(lblDia, gbc);
        
        comboDia = new JComboBox<>(new String[]{
            "Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        });
        comboDia.setPreferredSize(new Dimension(150, 30));
        comboDia.setBackground(Color.WHITE);
        comboDia.addActionListener(e -> actualizarPrecios());
        gbc.gridx = 3;
        panelSuperior.add(comboDia, gbc);
        
        
        JPanel panelInfoSubsidio = new JPanel(new BorderLayout());
        panelInfoSubsidio.setBackground(new Color(240, 248, 255));
        panelInfoSubsidio.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237)),
            "Información de Subsidio"
        ));
        
        lblInfoSubsidio = new JLabel();
        lblInfoSubsidio.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblInfoSubsidio.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelInfoSubsidio.add(lblInfoSubsidio, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelSuperior.add(panelInfoSubsidio, gbc);
        
        
        JPanel panelEstadoIngredientes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstadoIngredientes.setBackground(UIConstants.WHITE);
        lblEstadoIngredientes = new JLabel("✓ Todos los menús tienen ingredientes asignados");
        lblEstadoIngredientes.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstadoIngredientes.setForeground(UIConstants.GREEN_SUCCESS);
        panelEstadoIngredientes.add(lblEstadoIngredientes);
        
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        panelSuperior.add(panelEstadoIngredientes, gbc);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(UIConstants.WHITE);
        
        JButton btnActualizarCCB = new JButton("Recalcular CCB");
        btnActualizarCCB.setBackground(UIConstants.BLUE_LIGHT);
        btnActualizarCCB.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnActualizarCCB.setPreferredSize(new Dimension(150, 35));
        btnActualizarCCB.addActionListener(e -> actualizarCCB());
        panelBotones.add(btnActualizarCCB);

        JButton btnProyeccionBandejas = new JButton("Proyección de bandejas");
        btnProyeccionBandejas.setBackground(new Color(230, 230, 255));
        btnProyeccionBandejas.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnProyeccionBandejas.setPreferredSize(new Dimension(190, 35));
        btnProyeccionBandejas.addActionListener(e -> configurarProyeccionBandejas());
        panelBotones.add(btnProyeccionBandejas);
        
        btnVolver = new JButton("← Volver");
        btnVolver.setBackground(new Color(150, 150, 200));
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVolver.setForeground(UIConstants.BLUE_DARK);
        btnVolver.setPreferredSize(new Dimension(120, 35));
        btnVolver.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            if (ventanaAnterior != null) {
                ventanaAnterior.setVisible(true);
            }
        });
        panelBotones.add(btnVolver);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panelSuperior.add(panelBotones, gbc);
        
        // Tabla de precios
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Día", "Servicio", "Nombre", "Precio Base", "Precio Final", "% Pago", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPrecios = new JTable(modeloTabla);
        tablaPrecios.setFillsViewportHeight(true);
        tablaPrecios.setRowHeight(25);
        tablaPrecios.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaPrecios.getTableHeader().setForeground(UIConstants.BLUE_DARK);
        tablaPrecios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        
        tablaPrecios.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String estado = table.getValueAt(row, 7).toString();
                    if (estado.equals("SIN INGREDIENTES")) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.RED);
                    } else {
                        String usuario = comboTipoUsuario.getSelectedItem().toString();
                        switch (usuario) {
                            case "Estudiante":
                                c.setBackground(new Color(220, 255, 220));
                                break;
                            case "Profesor":
                                c.setBackground(new Color(255, 255, 200));
                                break;
                            case "Administrativo":
                                c.setBackground(new Color(255, 220, 220));
                                break;
                        }
                        c.setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JPanel panelDesglose = new JPanel(new BorderLayout());
        panelDesglose.setBackground(UIConstants.WHITE);
        panelDesglose.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Desglose de Costos - Menú Seleccionado"
        ));
        
        txtDesglose = new JTextArea(10, 50);
        txtDesglose.setEditable(false);
        txtDesglose.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDesglose.setBackground(new Color(245, 245, 245));
        JScrollPane scrollDesglose = new JScrollPane(txtDesglose);
        scrollDesglose.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelDesglose.add(scrollDesglose, BorderLayout.CENTER);
        
        
        tablaPrecios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrecios.getSelectedRow() != -1) {
                mostrarDesgloseSeleccionado();
                actualizarEtiquetaCCB();
            }
        });
        
        
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.add(new JScrollPane(tablaPrecios), BorderLayout.CENTER);
        panelCentro.add(panelDesglose, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
    }
    
    private void actualizarCCB() {
        actualizarEtiquetaCCB();
        actualizarPrecios();
        actualizarInfoSubsidio();
    }

    private void actualizarEtiquetaCCB() {
        int bandejas = 500;
        int idSel = getIdMenuSeleccionado();
        if (idSel > 0) {
            bandejas = ccbCalculator.getBandejasProyectadas(idSel);
        }
        double ccb = ccbCalculator.calcularCCB(bandejas);
        lblCCBLabel.setText("CCB Base calculado (bandejas: " + bandejas + "):");
        lblCCBBase.setText(String.format("$%.2f", ccb));
    }

    private int getIdMenuSeleccionado() {
        int fila = tablaPrecios.getSelectedRow();
        if (fila == -1) return -1;
        try {
            Object v = tablaPrecios.getValueAt(fila, 0);
            if (v instanceof Integer) return (Integer) v;
            return Integer.parseInt(v.toString());
        } catch (Exception ex) {
            return -1;
        }
    }

    private void configurarProyeccionBandejas() {
        int id = getIdMenuSeleccionado();
        if (id <= 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un menú en la tabla para proyectar bandejas (máx. 400 por menú).",
                "Seleccione un menú",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Menu menu = menuServicio.getMenuPorId(id);
        if (menu == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo cargar el menú seleccionado.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int actual = ccbCalculator.getBandejasProyectadas(id);
        int valorInicial = Math.min(Math.max(1, actual), 400);

        JSpinner sp = new JSpinner(new SpinnerNumberModel(valorInicial, 1, 400, 1));
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.add(new JLabel("Menú: " + menu.getNombre()));
        p.add(new JLabel("Día: " + menu.getDia() + "  |  Servicio: " + menu.getTipoServicio()));
        p.add(new JLabel("Ingrese la cantidad de bandejas a proyectar (1 - 400):"));
        p.add(sp);

        int op = JOptionPane.showConfirmDialog(this, p, "Proyección de bandejas", JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;

        int cantidad = (Integer) sp.getValue();
        ccbCalculator.setBandejasProyectadas(id, cantidad);

        actualizarEtiquetaCCB();
        actualizarPrecios();
        mostrarDesgloseSeleccionado();
    }
    
    private void actualizarInfoSubsidio() {
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String descripcion = ccbCalculator.getDescripcionSubsidio(tipoUsuario);
        double porcentaje = ccbCalculator.getPorcentajeSubsidio(tipoUsuario);
        
        lblInfoSubsidio.setText(String.format("<html><b>%s:</b> %s (%.0f%% de subsidio - paga %.0f%%)</html>",
            tipoUsuario, descripcion, porcentaje, 100 - porcentaje));
    }
    
    private void actualizarPrecios() {
        modeloTabla.setRowCount(0);
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        String dia = comboDia.getSelectedItem().toString();
        
        List<Menu> todosMenus = menuServicio.getMenus();
        System.out.println("Total menús disponibles: " + todosMenus.size());
        
        List<Menu> menus;
        if ("Todos".equals(dia)) {
            menus = todosMenus;
        } else {
            menus = menuServicio.getMenusPorDia(dia);
        }
        
        if (menus.isEmpty() && !"Todos".equals(dia)) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "No hay menús disponibles para el día " + dia + ".\n" +
                "¿Desea ir a la gestión de menús para agregar uno?",
                "Sin menús disponibles",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                
                if (parentFrame != null) {
                    parentFrame.dispose();
                }
                abrirGestionMenus();
            }
            return;
        }
        
        double porcentajePago = 100 - ccbCalculator.getPorcentajeSubsidio(tipoUsuario);
        boolean todosConIngredientes = true;
        
        for (Menu menu : menus) {
            
            if (!"Todos".equals(dia) || menu.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                boolean tieneIngredientes = menu.tieneIngredientes();
                if (!tieneIngredientes) {
                    todosConIngredientes = false;
                }
                
                double precioFinal = tieneIngredientes ? 
                    ccbCalculator.calcularPrecioMenu(menu, tipoUsuario, tipoServicio) : 
                    menu.getPrecioVenta();
                
                String estado = tieneIngredientes ? "COMPLETO" : "SIN INGREDIENTES";
                
                modeloTabla.addRow(new Object[]{
                    menu.getId(),
                    menu.getDia(),
                    menu.getTipoServicio(),
                    menu.getNombre(),
                    String.format("$%.2f", menu.getPrecioVenta()),
                    tieneIngredientes ? String.format("$%.2f", precioFinal) : "No disponible",
                    tieneIngredientes ? String.format("%.0f%%", porcentajePago) : "-",
                    estado
                });
            }
        }
        
        
        if (todosConIngredientes) {
            lblEstadoIngredientes.setText("✓ Todos los menús tienen ingredientes asignados");
            lblEstadoIngredientes.setForeground(UIConstants.GREEN_SUCCESS);
        } else {
            lblEstadoIngredientes.setText("⚠ Algunos menús no tienen ingredientes asignados");
            lblEstadoIngredientes.setForeground(UIConstants.YELLOW_WARNING);
        }
        
        
        tablaPrecios.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaPrecios.getColumnModel().getColumn(1).setPreferredWidth(60);  // Día
        tablaPrecios.getColumnModel().getColumn(2).setPreferredWidth(70);  // Servicio
        tablaPrecios.getColumnModel().getColumn(3).setPreferredWidth(250); // Nombre
        tablaPrecios.getColumnModel().getColumn(4).setPreferredWidth(80);  // Precio Base
        tablaPrecios.getColumnModel().getColumn(5).setPreferredWidth(80);  // Precio Final
        tablaPrecios.getColumnModel().getColumn(6).setPreferredWidth(60);  // % Pago
        tablaPrecios.getColumnModel().getColumn(7).setPreferredWidth(100); // Estado
    }
    
    private void mostrarDesgloseSeleccionado() {
        int fila = tablaPrecios.getSelectedRow();
        if (fila == -1) return;
        
        int id = (int) tablaPrecios.getValueAt(fila, 0);
        String dia = tablaPrecios.getValueAt(fila, 1).toString();
        String servicio = tablaPrecios.getValueAt(fila, 2).toString();
        String nombre = tablaPrecios.getValueAt(fila, 3).toString();
        String estado = tablaPrecios.getValueAt(fila, 7).toString();
        
        Menu menuSeleccionado = menuServicio.getMenuPorId(id);
        
        if (menuSeleccionado == null) {
            txtDesglose.setText("╔══════════════════════════════════════════════════════════╗\n" +
                                "║                    ERROR AL CARGAR MENÚ                    ║\n" +
                                "╠══════════════════════════════════════════════════════════╣\n" +
                                "║  No se pudo encontrar el menú seleccionado.               ║\n" +
                                "║                                                            ║\n" +
                                "║  ID: " + String.format("%-43d", id) + "║\n" +
                                "║  Nombre: " + String.format("%-40s", nombre.length() > 35 ? nombre.substring(0, 35) + "..." : nombre) + "║\n" +
                                "╚══════════════════════════════════════════════════════════╝");
            return;
        }
        
        if (estado.equals("SIN INGREDIENTES") || !menuSeleccionado.tieneIngredientes()) {
            String mensaje = "╔══════════════════════════════════════════════════════════╗\n" +
                            "║                    MENÚ SIN INGREDIENTES                   ║\n" +
                            "╠══════════════════════════════════════════════════════════╣\n" +
                            "║  El menú '" + String.format("%-40s", nombre.length() > 35 ? nombre.substring(0, 35) + "..." : nombre) + "║\n" +
                            "║  no tiene ingredientes asignados.                         ║\n" +
                            "║                                                            ║\n" +
                            "║  Para calcular el CCB es necesario asignar                 ║\n" +
                            "║  ingredientes a este menú.                                 ║\n" +
                            "║                                                            ║\n" +
                            "║  Use el botón 'Seleccionar Ingredientes' en la            ║\n" +
                            "║  gestión de menús para configurarlos.                     ║\n" +
                            "╚══════════════════════════════════════════════════════════╝";
            
            txtDesglose.setText(mensaje);
            
            int respuesta = JOptionPane.showConfirmDialog(this,
                "El menú '" + nombre + "' no tiene ingredientes asignados.\n" +
                "Para calcular el CCB es necesario asignar ingredientes.\n\n" +
                "¿Desea ir a la gestión de menús para asignar ingredientes?",
                "Menú sin ingredientes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                if (parentFrame != null) {
                    parentFrame.dispose();
                }
                abrirGestionMenus();
            }
            return;
        }
        
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        
        try {
            CCBCalculator.DesgloseCostos desglose = 
                ccbCalculator.calcularDesgloseCostos(menuSeleccionado, tipoUsuario, tipoServicio);
            
            if (desglose != null) {
                int bandejas = ccbCalculator.getBandejasProyectadas(menuSeleccionado.getId());
                String extra = "";
                if (bandejas != 500) {
                    extra = "Bandejas proyectadas para este menú: " + bandejas + " (máx. 400)\n\n";
                }
                txtDesglose.setText(extra + desglose.toString());
            } else {
                txtDesglose.setText("╔══════════════════════════════════════════════════════════╗\n" +
                                    "║              ERROR EN EL CÁLCULO DE COSTOS                ║\n" +
                                    "╠══════════════════════════════════════════════════════════╣\n" +
                                    "║  No se pudo calcular el desglose de costos                ║\n" +
                                    "║  para este menú.                                          ║\n" +
                                    "║                                                            ║\n" +
                                    "║  Posibles causas:                                         ║\n" +
                                    "║  • Ingredientes con precios inválidos                     ║\n" +
                                    "║  • Problemas en la configuración del CCB                  ║\n" +
                                    "╚══════════════════════════════════════════════════════════╝");
            }
        } catch (Exception e) {
            txtDesglose.setText("╔══════════════════════════════════════════════════════════╗\n" +
                                "║              ERROR EN EL CÁLCULO DE COSTOS                ║\n" +
                                "╠══════════════════════════════════════════════════════════╣\n" +
                                "║  Error: " + String.format("%-42s", e.getMessage().length() > 35 ? e.getMessage().substring(0, 35) + "..." : e.getMessage()) + "║\n" +
                                "║                                                            ║\n" +
                                "║  Consulte el log para más detalles.                       ║\n" +
                                "╚══════════════════════════════════════════════════════════╝");
            e.printStackTrace();
        }
    }
    
    private void abrirGestionMenus() {
        MenuVista vistaMenus = new MenuVista();
        new MenuControlador(vistaMenus, ventanaAnterior);
        vistaMenus.setVisible(true);
    }
    
    public void refrescar() {
        actualizarCCB();
    }
}