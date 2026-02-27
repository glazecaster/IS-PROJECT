package admin.view;
import javax.swing.*;
import view.HeaderPanel;
import view.ComeUCVView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import admin.controller.AdminControlador;
import admin.controller.MenuControlador;

public class AdminVista extends JFrame {
    
    // Componentes del formulario
    public JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Costo Fijo", "Costo Variable", "Menú"});
    public JComboBox<String> comboDia = new JComboBox<>(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"});
    public JTextField txtConcepto = new JTextField(12);
    public JTextField txtMonto = new JTextField(5);
    public JTextField txtProt = new JTextField(5);
    public JTextField txtKcal = new JTextField(5);
    
    // Botones
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnEditar = new JButton("Editar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar");
    
    // Panel de período
    public JPanel panelPeriodo = new JPanel();
    public JComboBox<String> comboUnidadPeriodo = new JComboBox<>(new String[]{"días", "semanas", "meses"});
    public JTextField txtCantidadPeriodo = new JTextField(5);
    
    // Tabla
    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"Menú/ Costo variable/ Costo fijo", "Día", "Nombre", "Monto", "Período", "Proteínas", "Calorías"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    public JTable tablaDatos = new JTable(modeloTabla);
    private int filaSeleccionada = -1;
    private boolean modoEdicion = false;

    private final HeaderPanel header = new HeaderPanel("COMEUCV");
    private final String cedula;

    private final JButton btnSalir = new JButton("Salir");
    private Runnable onSalir;

    public AdminVista(String cedula) {
        this.cedula = cedula;

        setTitle("Panel Administrativo ComeUCV - Gestión de Costos");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        configurarMenu();
        header.setRolText("ADMINISTRADOR");
        header.setUsuarioText(cedula);

        JButton btnGestionMenus = new JButton("Gestión de menús");
        JButton btnCalculoCCB = new JButton("Cálculo CCB");
        styleHeaderAction(btnGestionMenus);
        styleHeaderAction(btnCalculoCCB);
        btnGestionMenus.addActionListener(e -> abrirGestionMenus());
        btnCalculoCCB.addActionListener(e -> abrirPanelCCB());
        header.setLeftActions(btnGestionMenus, btnCalculoCCB);
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("ADMIN - GESTIÓN DE COSTOS OPERATIVOS", SwingConstants.CENTER);
        lblTitulo.setFont(UIConstants.TITLE_FONT);
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JPanel panelForm = crearPanelFormulario();

        JScrollPane scroll = new JScrollPane(tablaDatos);
        tablaDatos.setFillsViewportHeight(true);
        tablaDatos.setRowHeight(25);
        
        
        txtProt.setToolTipText("<html><b>Formato para proteínas:</b><br>" +
                                "• Número entero: 25g<br>" +
                                "• Número decimal: 25.5g<br>" +
                                "• Rango: 20-30g<br>" +
                                "• Valor cero: 0g<br>" +
                                "• Campo vacío = 0g</html>");
        
        txtKcal.setToolTipText("<html><b>Formato para calorías:</b><br>" +
                                "• Número entero: 350kcal<br>" +
                                "• Número decimal: 350.5 kcal<br>" +
                                "• Rango: 300-400kcal<br>" +
                                "• Valor cero: 0 kcal, 0kcal<br>" +
                                "• Campo vacío = 0 kcal</html>");
        
        tablaDatos.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaDatos.getTableHeader().setForeground(Color.WHITE);
        tablaDatos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaDatos.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                lbl.setBackground(UIConstants.BLUE_DARK);
                lbl.setForeground(Color.WHITE);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                lbl.setText(value == null ? "" : value.toString());
                return lbl;
            }
        });

        
        
        tablaDatos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color COLOR_FIJO = new Color(220, 240, 255);
            private final Color COLOR_VARIABLE = new Color(255, 245, 220);
            private final Color COLOR_MENU = new Color(220, 255, 220);
            private final Color COLOR_SELECCION = new Color(10, 57, 102);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (column == 0 || column == 1) {
                    if (!isSelected) {
                        c.setBackground(new Color(200, 220, 255));
                        c.setForeground(Color.BLACK);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                } else if (!isSelected) {
                    String categoria = "";
                    if (table.getModel().getValueAt(row, 0) != null) {
                        categoria = table.getModel().getValueAt(row, 0).toString();
                    }
                    
                    switch (categoria) {
                        case "Costo Fijo": 
                            c.setBackground(COLOR_FIJO);
                            break;
                        case "Costo Variable": 
                            c.setBackground(COLOR_VARIABLE);
                            break;
                        case "Menú": 
                            c.setBackground(COLOR_MENU);
                            break;
                        default: 
                            c.setBackground(UIConstants.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(COLOR_SELECCION);
                    c.setForeground(UIConstants.WHITE);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JPanel panelBotones = crearPanelBotones();

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.add(panelForm, BorderLayout.NORTH);
        panelCentral.add(scroll, BorderLayout.CENTER);
        
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelBotones, BorderLayout.CENTER);
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        styleSalir(btnSalir);
        btnSalir.addActionListener(e -> {
            if (onSalir != null) onSalir.run();
        });
        JPanel barSalir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        barSalir.setOpaque(false);
        barSalir.add(btnSalir);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(barSalir, BorderLayout.SOUTH);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(topPanel, BorderLayout.NORTH);
        rootPanel.add(panelPrincipal, BorderLayout.CENTER);

        setContentPane(rootPanel);
        setLocationRelativeTo(null);
    }

    private void styleHeaderAction(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setOpaque(true);
        b.setBackground(ComeUCVView.BLANCO);
        b.setForeground(ComeUCVView.TEXTO_OSCURO);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12.5f));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(11, 45, 91, 55), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSalir(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(ComeUCVView.BLANCO);
        b.setForeground(ComeUCVView.TEXTO_OSCURO);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setOnSalir(Runnable r) { this.onSalir = r; }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuItemSalir = new JMenuItem("Cerrar sesión");
        menuItemSalir.addActionListener(e -> {
            if (onSalir != null) onSalir.run();
            else System.exit(0);
        });
        menuArchivo.add(menuItemSalir);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuItemAcerca = new JMenuItem("Acerca de");
        menuItemAcerca.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Sistema Administrativo ComeUCV\n" +
                "Versión 3.0\n\n" +
                "Módulos disponibles:\n" +
                "• Gestión de Costos Operativos\n" +
                "• Gestión de Menús Semanales\n" +
                "• Configuración de Ingredientes por menú\n" +
                "• Cálculo de CCB con subsidios\n\n" +
                "Desarrollado para la gestión eficiente del comedor universitario",
                "Acerca de ComeUCV",
                JOptionPane.INFORMATION_MESSAGE);
        });
        menuAyuda.add(menuItemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Registro de Costos",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            UIConstants.BLUE_DARK
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tipo de costo:"), gbc);
        gbc.gridx = 1;
        comboTipo.setPreferredSize(new Dimension(120, 25));
        panel.add(comboTipo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Nombre/Concepto:"), gbc);
        gbc.gridx = 3;
        txtConcepto.setPreferredSize(new Dimension(200, 25));
        panel.add(txtConcepto, gbc);

        //fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Día (para menú):"), gbc);
        gbc.gridx = 1;
        comboDia.setPreferredSize(new Dimension(120, 25));
        panel.add(comboDia, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Monto ($):"), gbc);
        gbc.gridx = 3;
        txtMonto.setPreferredSize(new Dimension(100, 25));
        panel.add(txtMonto, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblProt = new JLabel("Proteínas (g):");
        lblProt.setToolTipText("Formato: 25g, 25.5g, 20-30g o 0g");
        panel.add(lblProt, gbc);
        gbc.gridx = 1;
        txtProt.setPreferredSize(new Dimension(100, 25));
        txtProt.setToolTipText("<html><b>Ejemplos válidos:</b><br>• 25g<br>• 25.5g<br>• 20-30g<br>• 0g<br>• Vacío = 0g</html>");
        panel.add(txtProt, gbc);
        
        gbc.gridx = 2;
        JLabel lblKcal = new JLabel("Calorías (kcal):");
        lblKcal.setToolTipText("Formato: 350kcal, 350.5 kcal, 300-400kcal o 0 kcal");
        panel.add(lblKcal, gbc);
        gbc.gridx = 3;
        txtKcal.setPreferredSize(new Dimension(100, 25));
        txtKcal.setToolTipText("<html><b>Ejemplos válidos:</b><br>• 350kcal<br>• 350.5 kcal<br>• 300-400kcal<br>• 0 kcal<br>• Vacío = 0 kcal</html>");
        panel.add(txtKcal, gbc);

        panelPeriodo.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPeriodo.setBackground(UIConstants.WHITE);
        panelPeriodo.add(new JLabel("Período:"));
        txtCantidadPeriodo.setPreferredSize(new Dimension(60, 25));
        panelPeriodo.add(txtCantidadPeriodo);
        comboUnidadPeriodo.setPreferredSize(new Dimension(90, 25));
        panelPeriodo.add(comboUnidadPeriodo);
        panelPeriodo.setVisible(false);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panel.add(panelPeriodo, gbc);

        
        comboTipo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String tipoSeleccionado = comboTipo.getSelectedItem().toString();
                    panelPeriodo.setVisible("Costo Variable".equals(tipoSeleccionado));
                    comboDia.setEnabled("Menú".equals(tipoSeleccionado));
                    
                    
                    if ("Costo Fijo".equals(tipoSeleccionado)) {
                        txtConcepto.setToolTipText("Ejemplo: Alquiler, Servicios, Salarios");
                        txtMonto.setToolTipText("Monto mensual del costo fijo");
                    } else if ("Costo Variable".equals(tipoSeleccionado)) {
                        txtConcepto.setToolTipText("Ejemplo: Insumos, Mantenimiento, Transporte");
                        txtMonto.setToolTipText("Monto por el período indicado");
                    } else {
                        txtConcepto.setToolTipText("Nombre del menú");
                        txtMonto.setToolTipText("Precio de venta del menú");
                    }
                    
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(UIConstants.WHITE);
        
        btnGuardar.setBackground(UIConstants.BLUE_LIGHT);
        btnGuardar.setFont(UIConstants.HEADER_FONT);
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        panel.add(btnGuardar);
        
        btnEditar.setBackground(UIConstants.YELLOW_LIGHT);
        btnEditar.setFont(UIConstants.HEADER_FONT);
        btnEditar.setPreferredSize(new Dimension(120, 35));
        btnEditar.setEnabled(false);
        panel.add(btnEditar);
        
        btnEliminar.setBackground(UIConstants.RED_LIGHT);
        btnEliminar.setFont(UIConstants.HEADER_FONT);
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        btnEliminar.setEnabled(false);
        panel.add(btnEliminar);
        
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(UIConstants.HEADER_FONT);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setVisible(false);
        panel.add(btnCancelar);
        
        return panel;
    }

    private void abrirGestionMenus() {
        MenuVista vistaMenus = new MenuVista();
        new MenuControlador(vistaMenus, this);
        vistaMenus.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirPanelCCB() {
        JFrame frameCCB = new JFrame("Cálculo de CCB - ComeUCV");
        frameCCB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameCCB.setSize(1100, 750);
        frameCCB.setLocationRelativeTo(this);
        
        CCBPanel ccbPanel = new CCBPanel(frameCCB, this);
        frameCCB.add(ccbPanel);
        frameCCB.setVisible(true);
        this.setVisible(false);
    }

    public void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        btnGuardar.setText(habilitar ? "Actualizar" : "Guardar");
        btnEditar.setEnabled(!habilitar);
        btnEliminar.setEnabled(!habilitar);
        btnCancelar.setVisible(habilitar);
        
        if (habilitar) {
            btnGuardar.setBackground(new Color(102, 255, 102));
        } else {
            btnGuardar.setBackground(UIConstants.BLUE_LIGHT);
        }
    }
    
    public boolean isModoEdicion() {
        return modoEdicion;
    }
    
    public int getFilaSeleccionada() {
        return filaSeleccionada;
    }
    
    public void setFilaSeleccionada(int fila) {
        this.filaSeleccionada = fila;
    }
    
    public void deseleccionarFila() {
        tablaDatos.clearSelection();
        filaSeleccionada = -1;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    public void habilitarBotonesEdicionEliminacion(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
        btnEliminar.setEnabled(habilitar);
    }
    
    public void cargarDatosEnFormulario(Object[] datos) {
        comboTipo.setSelectedItem(datos[0].toString());
        comboDia.setSelectedItem(datos[1].toString());
        txtConcepto.setText(datos[2].toString());
        txtMonto.setText(datos[3].toString());
        
        String periodo = datos[4].toString();
        if ("Fijo".equals(periodo)) {
            panelPeriodo.setVisible(false);
            txtCantidadPeriodo.setText("");
        } else if (periodo.contains(" ") && !periodo.equals("Lunes") && !periodo.equals("Martes") && 
                    !periodo.equals("Miércoles") && !periodo.equals("Jueves") && !periodo.equals("Viernes")) {
            panelPeriodo.setVisible(true);
            String[] partes = periodo.split(" ");
            txtCantidadPeriodo.setText(partes[0]);
            comboUnidadPeriodo.setSelectedItem(partes[1]);
        } else {
            panelPeriodo.setVisible(false);
            txtCantidadPeriodo.setText("");
        }
        
        String prot = datos[5].toString();
        String kcal = datos[6].toString();
        
        
        if (prot.endsWith("g")) {
            prot = prot.replace("g", "").trim();
        }
        if (kcal.endsWith("kcal")) {
            kcal = kcal.replace("kcal", "").trim();
        }
        txtProt.setText(prot);
        txtKcal.setText(kcal);
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
    public int confirmarEliminacion() {
        return JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este registro?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
    }
}