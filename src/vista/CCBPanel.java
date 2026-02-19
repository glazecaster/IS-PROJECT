// Archivo: vista/CCBPanel.java (COMPLETO Y ACTUALIZADO)
package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import modelo.CCBCalculator;
import modelo.Menu;
import modelo.MenuServicio;

public class CCBPanel extends JPanel {
    private CCBCalculator ccbCalculator;
    private MenuServicio menuServicio;
    private JFrame parentFrame;
    private JFrame ventanaAnterior;
    
    private JLabel lblCCBBase;
    private JComboBox<String> comboTipoUsuario;
    private JComboBox<String> comboTipoServicio;
    private JComboBox<String> comboDia;
    private JTable tablaPrecios;
    private DefaultTableModel modeloTabla;
    private JTextArea txtDesglose;
    private JLabel lblInfoSubsidio;
    private JLabel lblEstadoIngredientes;
    private JLabel lblInfoProyeccion;
    
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
        
        // Configurar menú en el JFrame padre
        if (parentFrame != null) {
            configurarMenuBar(parentFrame);
        }
        
        initComponents();
        actualizarCCB();
        actualizarPrecios();
        actualizarInfoProyeccion();
    }
    
    private void configurarMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        
        JMenuItem cerrarSesionMenuItem = new JMenuItem("Cerrar sesión");
        cerrarSesionMenuItem.addActionListener(e -> cerrarSesion(frame));
        
        JMenuItem salirMenuItem = new JMenuItem("Salir");
        salirMenuItem.addActionListener(e -> System.exit(0));
        
        archivoMenu.add(cerrarSesionMenuItem);
        archivoMenu.addSeparator();
        archivoMenu.add(salirMenuItem);
        
        // Menú Navegación
        JMenu navegacionMenu = new JMenu("Navegación");
        
        JMenuItem costosMenuItem = new JMenuItem("Gestión de Costos");
        costosMenuItem.addActionListener(e -> abrirGestionCostos(frame));
        
        JMenuItem menusMenuItem = new JMenuItem("Gestión de Menús");
        menusMenuItem.addActionListener(e -> abrirGestionMenus(frame));
        
        JMenuItem ccbMenuItem = new JMenuItem("Cálculo CCB");
        ccbMenuItem.setEnabled(false); // Deshabilitado porque ya estamos aquí
        
        navegacionMenu.add(costosMenuItem);
        navegacionMenu.add(menusMenuItem);
        navegacionMenu.addSeparator();
        navegacionMenu.add(ccbMenuItem);
        
        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaMenuItem = new JMenuItem("Acerca de CCB");
        acercaMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                "CÁLCULO DE CCB (Costo por Comensal)\n\n" +
                "Nueva fórmula implementada:\n" +
                "CCB = [(CF + CV) / NB] * (1 + %Merma)\n\n" +
                "Donde:\n" +
                "• CF: Costos Fijos Totales\n" +
                "• CV: Costos Variables Totales\n" +
                "• NB: Número de Bandejas proyectadas\n" +
                "• %Merma: Porcentaje de merma (5% por defecto)\n\n" +
                "Distribución de costos:\n" +
                "• Desayunos: 25% de costos fijos y variables\n" +
                "• Almuerzos: 75% de costos fijos y variables\n\n" +
                "Subsidios aplicados:\n" +
                "• Estudiante: 75% de subsidio (paga 25%)\n" +
                "• Profesor: 40% de subsidio (paga 60%)\n" +
                "• Administrativo: 15% de subsidio (paga 85%)\n\n" +
                "Margen operativo: 15%\n" +
                "IVA: 16%",
                "Acerca de CCB",
                JOptionPane.INFORMATION_MESSAGE);
        });
        ayudaMenu.add(acercaMenuItem);
        
        menuBar.add(archivoMenu);
        menuBar.add(navegacionMenu);
        menuBar.add(ayudaMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    private void cerrarSesion(JFrame frame) {
        frame.dispose();
        if (ventanaAnterior != null) {
            ventanaAnterior.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            view.LoginView loginView = new view.LoginView();
            new controller.LoginController(loginView);
            loginView.setVisible(true);
        });
    }
    
    private void abrirGestionCostos(JFrame frame) {
        frame.dispose();
        AdminVista vistaCostos = new AdminVista();
        new controlador.AdminControlador(vistaCostos);
        vistaCostos.setVisible(true);
    }
    
    private void abrirGestionMenus(JFrame frame) {
        frame.dispose();
        MenuVista vistaMenus = new MenuVista();
        new controlador.MenuControlador(vistaMenus, ventanaAnterior);
        vistaMenus.setVisible(true);
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
        
        // Fila 0: CCB Base y Tipo Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCCBLabel = new JLabel("CCB Base calculado:");
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
        
        // Fila 1: Tipo Servicio y Día
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblServicio = new JLabel("Tipo Servicio:");
        lblServicio.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelSuperior.add(lblServicio, gbc);
        
        comboTipoServicio = new JComboBox<>(new String[]{
            "Desayuno", "Almuerzo"
        });
        comboTipoServicio.setPreferredSize(new Dimension(150, 30));
        comboTipoServicio.setBackground(Color.WHITE);
        comboTipoServicio.addActionListener(e -> {
            actualizarCCB();
            actualizarPrecios();
            actualizarInfoProyeccion();
        });
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
        
        // Fila 2: Información de Subsidio
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
        
        // Fila 2 (continuación): Estado de Ingredientes
        JPanel panelEstadoIngredientes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstadoIngredientes.setBackground(UIConstants.WHITE);
        lblEstadoIngredientes = new JLabel("✓ Todos los menús tienen ingredientes asignados");
        lblEstadoIngredientes.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstadoIngredientes.setForeground(UIConstants.GREEN_SUCCESS);
        panelEstadoIngredientes.add(lblEstadoIngredientes);
        
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 2;
        panelSuperior.add(panelEstadoIngredientes, gbc);
        
        // Fila 3: Información de Proyección
        JPanel panelInfoProyeccion = new JPanel(new BorderLayout());
        panelInfoProyeccion.setBackground(new Color(255, 255, 225));
        panelInfoProyeccion.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.YELLOW_WARNING),
            "Proyección de Bandejas"
        ));
        
        lblInfoProyeccion = new JLabel();
        lblInfoProyeccion.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblInfoProyeccion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelInfoProyeccion.add(lblInfoProyeccion, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panelSuperior.add(panelInfoProyeccion, gbc);
        
        // Fila 4: Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(UIConstants.WHITE);
        
        JButton btnActualizarCCB = new JButton("Recalcular CCB");
        btnActualizarCCB.setBackground(UIConstants.BLUE_LIGHT);
        btnActualizarCCB.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnActualizarCCB.setPreferredSize(new Dimension(150, 35));
        btnActualizarCCB.addActionListener(e -> {
            actualizarCCB();
            actualizarPrecios();
        });
        panelBotones.add(btnActualizarCCB);
        
        JButton btnProyeccion = new JButton("📊 Proyección de Bandejas");
        btnProyeccion.setBackground(UIConstants.YELLOW_LIGHT);
        btnProyeccion.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnProyeccion.setPreferredSize(new Dimension(220, 35));
        btnProyeccion.addActionListener(e -> abrirProyeccionBandejas());
        panelBotones.add(btnProyeccion);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panelSuperior.add(panelBotones, gbc);
        
        // Tabla de precios
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Día", "Servicio", "Nombre", "Precio Base", "Precio Final", "% Pago", "Estado", "Bandejas"}, 0
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
        
        // Panel de desglose
        JPanel panelDesglose = new JPanel(new BorderLayout());
        panelDesglose.setBackground(UIConstants.WHITE);
        panelDesglose.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Desglose de Costos - Menú Seleccionado"
        ));
        
        txtDesglose = new JTextArea(12, 50);
        txtDesglose.setEditable(false);
        txtDesglose.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDesglose.setBackground(new Color(245, 245, 245));
        JScrollPane scrollDesglose = new JScrollPane(txtDesglose);
        scrollDesglose.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelDesglose.add(scrollDesglose, BorderLayout.CENTER);
        
        tablaPrecios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrecios.getSelectedRow() != -1) {
                mostrarDesgloseSeleccionado();
            }
        });
        
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.add(new JScrollPane(tablaPrecios), BorderLayout.CENTER);
        panelCentro.add(panelDesglose, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
    }
    
    private void abrirProyeccionBandejas() {
        BandejaProyeccionDialog dialog = new BandejaProyeccionDialog(parentFrame, ccbCalculator.getBandejaProyeccion());
        dialog.setVisible(true);
        
        if (dialog.isAceptado()) {
            actualizarCCB();
            actualizarPrecios();
            actualizarInfoProyeccion();
        }
    }
    
    private void actualizarCCB() {
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        double ccb = ccbCalculator.calcularCCB(tipoServicio);
        lblCCBBase.setText(String.format("$%.2f", ccb));
        actualizarInfoSubsidio();
    }
    
    private void actualizarInfoSubsidio() {
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String descripcion = ccbCalculator.getDescripcionSubsidio(tipoUsuario);
        double porcentaje = ccbCalculator.getPorcentajeSubsidio(tipoUsuario);
        
        lblInfoSubsidio.setText(String.format("<html><b>%s:</b> %s (%.0f%% de subsidio - paga %.0f%%)</html>",
            tipoUsuario, descripcion, porcentaje, 100 - porcentaje));
    }
    
    private void actualizarInfoProyeccion() {
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        int totalBandejas = ccbCalculator.getBandejaProyeccion().getTotalBandejasServicio(tipoServicio);
        double merma = ccbCalculator.getBandejaProyeccion().getPorcentajeMerma();
        
        if (totalBandejas == 0) {
            lblInfoProyeccion.setText("<html>⚠ No hay proyecciones configuradas. Use el botón 'Proyección de Bandejas' para configurar.<br>" +
                "Valores por defecto: Desayuno=800, Almuerzo=1500 bandejas/semana, Merma=5%</html>");
        } else {
            lblInfoProyeccion.setText(String.format(
                "<html>✓ Proyección activa: %d bandejas/semana para %s | Merma: %.1f%% | " +
                "Fórmula: CCB = [(CF+CV)/%d]*(1+%.0f%%)</html>",
                totalBandejas, tipoServicio, merma, totalBandejas, merma));
        }
    }
    
    private void actualizarPrecios() {
        modeloTabla.setRowCount(0);
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        String dia = comboDia.getSelectedItem().toString();
        
        List<Menu> todosMenus = menuServicio.getMenus();
        
        List<Menu> menus;
        if ("Todos".equals(dia)) {
            menus = menuServicio.getMenusPorServicio(tipoServicio);
        } else {
            menus = menuServicio.getMenusPorDiaYServicio(dia, tipoServicio);
        }
        
        // Verificar si hay menús para mostrar
        if (menus.isEmpty()) {
            if (!"Todos".equals(dia)) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "No hay menús disponibles para " + tipoServicio + " el día " + dia + ".\n" +
                    "¿Desea ir a la gestión de menús para agregar uno?",
                    "Sin menús disponibles",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (respuesta == JOptionPane.YES_OPTION) {
                    if (parentFrame != null) {
                        parentFrame.dispose();
                    }
                    abrirGestionMenus(parentFrame);
                }
            }
            return;
        }
        
        double porcentajePago = 100 - ccbCalculator.getPorcentajeSubsidio(tipoUsuario);
        boolean todosConIngredientes = true;
        
        // Obtener número de bandejas para mostrar información
        int bandejasServicio = ccbCalculator.getBandejaProyeccion().getTotalBandejasServicio(tipoServicio);
        String infoBandejas = bandejasServicio > 0 ? String.valueOf(bandejasServicio) : "Por defecto";
        
        for (Menu menu : menus) {
            boolean tieneIngredientes = menu.tieneIngredientes();
            if (!tieneIngredientes) {
                todosConIngredientes = false;
            }
            
            double precioFinal;
            if ("Todos".equals(dia)) {
                precioFinal = tieneIngredientes ? 
                    ccbCalculator.calcularPrecioMenu(menu, tipoUsuario, tipoServicio) : 
                    menu.getPrecioVenta();
            } else {
                precioFinal = tieneIngredientes ? 
                    ccbCalculator.calcularPrecioMenuPorDia(menu, dia, tipoUsuario, tipoServicio) : 
                    menu.getPrecioVenta();
            }
            
            String estado = tieneIngredientes ? "COMPLETO" : "SIN INGREDIENTES";
            
            // Obtener bandejas específicas para este día (si aplica)
            String bandejasDia = "";
            if (!"Todos".equals(dia)) {
                int bandejas = "Desayuno".equals(tipoServicio) ? 
                    ccbCalculator.getBandejaProyeccion().getProyeccionDesayuno(dia) :
                    ccbCalculator.getBandejaProyeccion().getProyeccionAlmuerzo(dia);
                bandejasDia = bandejas > 0 ? String.valueOf(bandejas) : "N/E";
            }
            
            modeloTabla.addRow(new Object[]{
                menu.getId(),
                menu.getDia(),
                menu.getTipoServicio(),
                menu.getNombre(),
                String.format("$%.2f", menu.getPrecioVenta()),
                tieneIngredientes ? String.format("$%.2f", precioFinal) : "No disponible",
                tieneIngredientes ? String.format("%.0f%%", porcentajePago) : "-",
                estado,
                "Todos".equals(dia) ? infoBandejas : bandejasDia
            });
        }
        
        if (todosConIngredientes) {
            lblEstadoIngredientes.setText("✓ Todos los menús tienen ingredientes asignados");
            lblEstadoIngredientes.setForeground(UIConstants.GREEN_SUCCESS);
        } else {
            lblEstadoIngredientes.setText("⚠ Algunos menús no tienen ingredientes asignados");
            lblEstadoIngredientes.setForeground(UIConstants.YELLOW_WARNING);
        }
        
        // Configurar anchos de columnas
        tablaPrecios.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaPrecios.getColumnModel().getColumn(1).setPreferredWidth(60);  // Día
        tablaPrecios.getColumnModel().getColumn(2).setPreferredWidth(70);  // Servicio
        tablaPrecios.getColumnModel().getColumn(3).setPreferredWidth(250); // Nombre
        tablaPrecios.getColumnModel().getColumn(4).setPreferredWidth(80);  // Precio Base
        tablaPrecios.getColumnModel().getColumn(5).setPreferredWidth(80);  // Precio Final
        tablaPrecios.getColumnModel().getColumn(6).setPreferredWidth(60);  // % Pago
        tablaPrecios.getColumnModel().getColumn(7).setPreferredWidth(100); // Estado
        tablaPrecios.getColumnModel().getColumn(8).setPreferredWidth(80);  // Bandejas
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
        
        // Verificar si el menú tiene ingredientes
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
                abrirGestionMenus(parentFrame);
            }
            return;
        }
        
        // Calcular desglose
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        String tipoServicio = comboTipoServicio.getSelectedItem().toString();
        
        try {
            CCBCalculator.DesgloseCostos desglose = 
                ccbCalculator.calcularDesgloseCostos(menuSeleccionado, tipoUsuario, tipoServicio);
            
            if (desglose != null) {
                // Agregar información de proyección al desglose
                int bandejas = ccbCalculator.getBandejaProyeccion().getTotalBandejasServicio(tipoServicio);
                double merma = ccbCalculator.getBandejaProyeccion().getPorcentajeMerma();
                
                String infoProyeccion = String.format(
                    "\n╟──────────────────────────────────────────────────────────╢\n" +
                    "║  Proyección: %d bandejas/semana | Merma: %.1f%%            ║",
                    bandejas, merma);
                
                txtDesglose.setText(desglose.toString().replace(
                    "╚══════════════════════════════════════════════════════════╝",
                    infoProyeccion + "\n╚══════════════════════════════════════════════════════════╝"));
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
    
    public void refrescar() {
        actualizarCCB();
        actualizarPrecios();
        actualizarInfoProyeccion();
    }
}