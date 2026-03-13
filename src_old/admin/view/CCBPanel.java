package admin.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import admin.model.CCBCalculator;
import admin.model.CCBConfigService;
import admin.model.Menu;
import admin.model.MenuServicio;
import admin.model.MenuSemana;

public class CCBPanel extends JPanel {
    private CCBCalculator ccbCalculator;
    private MenuServicio menuServicio;
    private MenuSemana menuSemana;
    private final CCBConfigService configService = new CCBConfigService();
    private JFrame parentFrame;
    private JFrame ventanaAnterior;
    
    private JLabel lblCCBBase;
    private JLabel lblCCBLabel;
    private JComboBox<String> comboTipoUsuario;
    private JComboBox<String> comboTipoServicio;
    private JTable tablaPrecios;
    private DefaultTableModel modeloTabla;
    private JTextArea txtDesglose;
    private JLabel lblInfoSubsidio;
    private JButton btnVolver;
    
    
    private JTextField txtMerma;
    private JTextField txtEstudiante;
    private JTextField txtProfesor;
    private JTextField txtAdmin;
    private JTextField txtBecario;
    private JTextField txtExonerado;
    private JButton btnActualizar;
    private JTextArea txtFormulaCCB;
    private JLabel lblRangoBecario;
    
    public CCBPanel(JFrame parent) {
        this(parent, null);
    }
    
    public CCBPanel(JFrame parent, JFrame ventanaAnterior) {
        this.parentFrame = parent;
        this.ventanaAnterior = ventanaAnterior;
        this.ccbCalculator = new CCBCalculator();
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        
        setLayout(new BorderLayout());
        setBackground(UIConstants.WHITE);
        
        initComponents();
        configService.cargarEn(ccbCalculator);
        cargarValoresIniciales();
        actualizarCCB();
        cargarMenusSemanales();
        actualizarInfoSubsidio();
    }
    
    
    public CCBPanel(JFrame parent, JFrame ventanaAnterior, CCBCalculator calculator) {
        this.parentFrame = parent;
        this.ventanaAnterior = ventanaAnterior;
        this.ccbCalculator = calculator;
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        
        setLayout(new BorderLayout());
        setBackground(UIConstants.WHITE);
        
        initComponents();
        configService.cargarEn(ccbCalculator);
        cargarValoresIniciales();
        actualizarCCB();
        cargarMenusSemanales();
        actualizarInfoSubsidio();
    }
    
    private void initComponents() {
        
        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        
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
        panelSuperior.setMaximumSize(new Dimension(1200, 500));
        panelSuperior.setPreferredSize(new Dimension(1100, 450));
        panelSuperior.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        lblCCBLabel = new JLabel("CCB Base calculado (bandejas semanales):", SwingConstants.CENTER);
        lblCCBLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelSuperior.add(lblCCBLabel, gbc);
        
        gbc.gridy = 1;
        lblCCBBase = new JLabel("$0.00", SwingConstants.CENTER);
        lblCCBBase.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCCBBase.setForeground(UIConstants.BLUE_DARK);
        panelSuperior.add(lblCCBBase, gbc);
        
        
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0;
        panelSuperior.add(new JLabel("Merma (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        txtMerma = new JTextField(6);
        txtMerma.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtMerma, gbc);
        
        gbc.gridx = 2;
        panelSuperior.add(new JLabel("Estudiante (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 3;
        txtEstudiante = new JTextField(6);
        txtEstudiante.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtEstudiante, gbc);
        
        
        gbc.gridy = 3; gbc.gridx = 0;
        panelSuperior.add(new JLabel("Profesor (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        txtProfesor = new JTextField(6);
        txtProfesor.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtProfesor, gbc);
        
        gbc.gridx = 2;
        panelSuperior.add(new JLabel("Administrativo (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 3;
        txtAdmin = new JTextField(6);
        txtAdmin.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtAdmin, gbc);
        
        
        gbc.gridy = 4; gbc.gridx = 0;
        panelSuperior.add(new JLabel("Becario (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        txtBecario = new JTextField(6);
        txtBecario.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtBecario, gbc);
        
        gbc.gridx = 2;
        panelSuperior.add(new JLabel("Exonerado (%):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 3;
        txtExonerado = new JTextField(6);
        txtExonerado.setHorizontalAlignment(JTextField.CENTER);
        panelSuperior.add(txtExonerado, gbc);
        
        
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        lblRangoBecario = new JLabel(" ", SwingConstants.CENTER);
        lblRangoBecario.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblRangoBecario.setForeground(Color.BLUE);
        panelSuperior.add(lblRangoBecario, gbc);
        
        
        gbc.gridy = 6; gbc.gridx = 1; gbc.gridwidth = 2;
        btnActualizar = new JButton("Actualizar Parámetros");
        btnActualizar.setBackground(UIConstants.BLUE_LIGHT);
        btnActualizar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnActualizar.setPreferredSize(new Dimension(200, 35));
        btnActualizar.addActionListener(e -> actualizarParametros());
        panelSuperior.add(btnActualizar, gbc);
        
        
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 4;
        txtFormulaCCB = new JTextArea(2, 50);
        txtFormulaCCB.setEditable(false);
        txtFormulaCCB.setFont(new Font("Monospaced", Font.BOLD, 12));
        txtFormulaCCB.setBackground(new Color(255, 255, 200));
        txtFormulaCCB.setLineWrap(true);
        txtFormulaCCB.setWrapStyleWord(true);
        JScrollPane scrollFormula = new JScrollPane(txtFormulaCCB);
        scrollFormula.setPreferredSize(new Dimension(900, 60));
        panelSuperior.add(scrollFormula, gbc);
        
        
        gbc.gridy = 8; gbc.gridwidth = 1; gbc.gridx = 0;
        panelSuperior.add(new JLabel("Tipo Usuario:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        comboTipoUsuario = new JComboBox<>(new String[]{
            "Estudiante", "Profesor", "Administrativo", "Becario", "Exonerado"
        });
        comboTipoUsuario.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboTipoUsuario.addActionListener(e -> {
            cargarMenusSemanales();
            actualizarInfoSubsidio();
        });
        panelSuperior.add(comboTipoUsuario, gbc);
        
        gbc.gridx = 2;
        panelSuperior.add(new JLabel("Servicio:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 3;
        comboTipoServicio = new JComboBox<>(new String[]{"Desayuno", "Almuerzo"});
        comboTipoServicio.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboTipoServicio.addActionListener(e -> cargarMenusSemanales());
        panelSuperior.add(comboTipoServicio, gbc);
        
        
        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 4;
        lblInfoSubsidio = new JLabel();
        lblInfoSubsidio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblInfoSubsidio.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237)));
        lblInfoSubsidio.setBackground(new Color(240, 248, 255));
        lblInfoSubsidio.setOpaque(true);
        lblInfoSubsidio.setPreferredSize(new Dimension(900, 35));
        lblInfoSubsidio.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(lblInfoSubsidio, gbc);
        
        
        gbc.gridy = 10; gbc.gridx = 3; gbc.gridwidth = 1;
        btnVolver = new JButton("← Volver");
        btnVolver.setBackground(new Color(150, 150, 200));
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(120, 35));
        btnVolver.addActionListener(e -> {
            if (parentFrame != null) parentFrame.dispose();
            if (ventanaAnterior != null) ventanaAnterior.setVisible(true);
        });
        panelSuperior.add(btnVolver, gbc);
        
        
        txtEstudiante.addActionListener(e -> actualizarRangoBecario());
        txtEstudiante.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                actualizarRangoBecario();
            }
        });
        
        panelPrincipal.add(panelSuperior);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(UIConstants.WHITE);
        panelTabla.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Menús de la Semana con CCB",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            UIConstants.BLUE_DARK
        ));
        panelTabla.setPreferredSize(new Dimension(1100, 280));
        panelTabla.setMaximumSize(new Dimension(1200, 300));
        panelTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        modeloTabla = new DefaultTableModel(
            new Object[]{"Día", "Menú Completo", "Componentes", "Precio Unit.", "Precio CCB"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPrecios = new JTable(modeloTabla);
        tablaPrecios.setFillsViewportHeight(true);
        tablaPrecios.setRowHeight(28);
        tablaPrecios.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaPrecios.getTableHeader().setForeground(Color.WHITE);
        tablaPrecios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        
        tablaPrecios.getColumnModel().getColumn(0).setPreferredWidth(60);  
        tablaPrecios.getColumnModel().getColumn(1).setPreferredWidth(150); 
        tablaPrecios.getColumnModel().getColumn(2).setPreferredWidth(400); 
        tablaPrecios.getColumnModel().getColumn(3).setPreferredWidth(100); 
        tablaPrecios.getColumnModel().getColumn(4).setPreferredWidth(100); 
        
        tablaPrecios.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
                    switch (tipoUsuario) {
                        case "Estudiante": c.setBackground(new Color(220, 255, 220)); break;
                        case "Profesor": c.setBackground(new Color(255, 255, 200)); break;
                        case "Administrativo": c.setBackground(new Color(255, 220, 220)); break;
                        case "Becario": c.setBackground(new Color(200, 230, 255)); break;
                        case "Exonerado": c.setBackground(new Color(230, 230, 230)); break;
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollTabla = new JScrollPane(tablaPrecios);
        scrollTabla.setPreferredSize(new Dimension(1050, 200));
        scrollTabla.setMinimumSize(new Dimension(800, 150));
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        
        panelPrincipal.add(panelTabla);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        
        JPanel panelDesglose = new JPanel(new BorderLayout());
        panelDesglose.setBackground(UIConstants.WHITE);
        panelDesglose.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Desglose del Día Seleccionado",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            UIConstants.BLUE_DARK
        ));
        panelDesglose.setPreferredSize(new Dimension(1100, 200));
        panelDesglose.setMaximumSize(new Dimension(1200, 250));
        panelDesglose.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtDesglose = new JTextArea(8, 60);
        txtDesglose.setEditable(false);
        txtDesglose.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDesglose.setBackground(new Color(245, 245, 245));
        txtDesglose.setLineWrap(true);
        txtDesglose.setWrapStyleWord(true);
        JScrollPane scrollDesglose = new JScrollPane(txtDesglose);
        scrollDesglose.setPreferredSize(new Dimension(1050, 140));
        panelDesglose.add(scrollDesglose, BorderLayout.CENTER);
        
        tablaPrecios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPrecios.getSelectedRow() != -1) {
                mostrarDesgloseSeleccionado();
            }
        });
        
        panelPrincipal.add(panelDesglose);
        
        
        
        JScrollPane scrollPrincipal = new JScrollPane(panelPrincipal);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(20);
        scrollPrincipal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        
        setLayout(new BorderLayout());
        add(scrollPrincipal, BorderLayout.CENTER);
    }
    
    private void cargarValoresIniciales() {
        txtMerma.setText(String.valueOf((int)(ccbCalculator.getMerma() * 100)));
        txtEstudiante.setText(String.valueOf((int)(ccbCalculator.getFactorEstudiante() * 100)));
        txtProfesor.setText(String.valueOf((int)(ccbCalculator.getFactorProfesor() * 100)));
        txtAdmin.setText(String.valueOf((int)(ccbCalculator.getFactorAdmin() * 100)));
        txtBecario.setText(String.valueOf((int)(ccbCalculator.getFactorBecario() * 100)));
        txtExonerado.setText(String.valueOf((int)(ccbCalculator.getFactorExonerado() * 100)));
        actualizarRangoBecario();
    }
    
    private void actualizarRangoBecario() {
        try {
            double est = Double.parseDouble(txtEstudiante.getText().trim());
            double maxBecario = est - 1;
            if (maxBecario < 5) maxBecario = 5;
            lblRangoBecario.setText("Becario debe ser entre 5% y " + String.format("%.0f", maxBecario) + "% (menor que Estudiante " + String.format("%.0f", est) + "%)");
        } catch (NumberFormatException e) {
            lblRangoBecario.setText("Ingrese un valor válido para Estudiante");
        }
    }
    
    private void actualizarParametros() {
        try {
            double merma = Double.parseDouble(txtMerma.getText().trim()) / 100.0;
            double est = Double.parseDouble(txtEstudiante.getText().trim()) / 100.0;
            double prof = Double.parseDouble(txtProfesor.getText().trim()) / 100.0;
            double admin = Double.parseDouble(txtAdmin.getText().trim()) / 100.0;
            double becario = Double.parseDouble(txtBecario.getText().trim()) / 100.0;
            double exonerado = Double.parseDouble(txtExonerado.getText().trim()) / 100.0;
            
            
            if (merma < 0 || merma > 1) {
                JOptionPane.showMessageDialog(this, "La merma debe estar entre 0% y 100%");
                return;
            }
            
            double estPct = est * 100;
            double profPct = prof * 100;
            double adminPct = admin * 100;
            double becarioPct = becario * 100;
            double exoneradoPct = exonerado * 100;
            
            
            if (estPct < 20 || estPct > 30) {
                JOptionPane.showMessageDialog(this, "Estudiante debe estar entre 20% y 30%");
                return;
            }
            if (profPct < 70 || profPct > 90) {
                JOptionPane.showMessageDialog(this, "Profesor debe estar entre 70% y 90%");
                return;
            }
            if (adminPct < 90 || adminPct > 110) {
                JOptionPane.showMessageDialog(this, "Administrativo debe estar entre 90% y 110%");
                return;
            }
            
            
            if (becarioPct < 5 || becarioPct >= estPct) {
                JOptionPane.showMessageDialog(this, 
                    "Becario debe estar entre 5% y " + String.format("%.0f", estPct-1) + 
                    "% (debe ser menor que Estudiante: " + String.format("%.0f", estPct) + "%)");
                return;
            }
            
            
            if (exoneradoPct < 0 || exoneradoPct > 5) {
                JOptionPane.showMessageDialog(this, "Exonerado debe estar entre 0% y 5%");
                return;
            }
            
            ccbCalculator.setMerma(merma);
            ccbCalculator.setFactorEstudiante(est);
            ccbCalculator.setFactorProfesor(prof);
            ccbCalculator.setFactorAdmin(admin);
            ccbCalculator.setFactorBecario(becario);
            ccbCalculator.setFactorExonerado(exonerado);
            
            configService.guardar(ccbCalculator);
            actualizarCCB();
            cargarMenusSemanales();
            actualizarInfoSubsidio();
            
            JOptionPane.showMessageDialog(this, "Parámetros actualizados correctamente");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese números válidos en todos los campos");
        }
    }
    
    private void actualizarCCB() {
        CCBCalculator.ResultadoCCB resultado = ccbCalculator.calcularCCB();
        int totalBandejas = ccbCalculator.getTotalBandejasSemanal();
        lblCCBLabel.setText("CCB Base calculado (bandejas semanales: " + totalBandejas + "):");
        lblCCBBase.setText(String.format("$%.2f", resultado.getCcbConMerma()));
        
        txtFormulaCCB.setText(
            "  FÓRMULA: CCB = [(CF + CV) / NB] * (1 + %Merma) = " +
            String.format("[($%.2f + $%.2f) / %d] * (1 + %.1f%%) = $%.2f",
            resultado.getCostosFijos(), resultado.getCostosVariables(), 
            resultado.getNumBandejas(), resultado.getPorcentajeMerma(),
            resultado.getCcbConMerma()));
    }
    
    private void actualizarInfoSubsidio() {
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        double factor = ccbCalculator.getFactorUsuario(tipoUsuario);
        double porcentajePago = factor * 100;
        double porcentajeSubsidio = 100 - porcentajePago;
        
        String infoAdicional = "";
        if ("Becario".equals(tipoUsuario)) {
            infoAdicional = " (menor que Estudiante: " + 
                String.format("%.0f", ccbCalculator.getFactorEstudiante() * 100) + "%)";
        } else if ("Exonerado".equals(tipoUsuario)) {
            infoAdicional = " (pago simbólico)";
        }
        
        lblInfoSubsidio.setText(String.format("%s: Paga %.0f%% del CCB (Subsidio: %.0f%%)%s", 
            tipoUsuario, porcentajePago, porcentajeSubsidio, infoAdicional));
    }
    
    
    private void cargarMenusSemanales() {
        modeloTabla.setRowCount(0);
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        boolean esDesayuno = comboTipoServicio.getSelectedItem().toString().equals("Desayuno");
        
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        
        for (String dia : dias) {
            String nombreMenu = "";
            String componentes = "";
            double precioTotalUnitario = 0;
            double precioTotalCCB = 0;
            List<String> nombresComponentes = new ArrayList<>();
            
            if (esDesayuno) {
                
                Integer idPrincipal = menuSemana.getDesayuno(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
                Integer idAcompanante = menuSemana.getDesayuno(dia, MenuSemana.COMP_ACOMPANANTE);
                
                Menu menuPrincipal = (idPrincipal != null && idPrincipal > 0) ? menuServicio.getMenuPorId(idPrincipal) : null;
                Menu menuAcompanante = (idAcompanante != null && idAcompanante > 0) ? menuServicio.getMenuPorId(idAcompanante) : null;
                
                if (menuPrincipal != null) {
                    nombresComponentes.add(menuPrincipal.getNombre());
                    precioTotalUnitario += menuPrincipal.getPrecioVenta();
                    precioTotalCCB += ccbCalculator.calcularPrecioMenuCompleto(idPrincipal, tipoUsuario);
                }
                if (menuAcompanante != null) {
                    nombresComponentes.add(menuAcompanante.getNombre());
                    precioTotalUnitario += menuAcompanante.getPrecioVenta();
                    precioTotalCCB += ccbCalculator.calcularPrecioMenuCompleto(idAcompanante, tipoUsuario);
                }
                
                nombreMenu = "Desayuno " + dia;
            } else {
                
                Integer idPrincipal = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
                Integer idAcompanante = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ACOMPANANTE);
                Integer idEnsalada = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ENSALADA);
                
                Menu menuPrincipal = (idPrincipal != null && idPrincipal > 0) ? menuServicio.getMenuPorId(idPrincipal) : null;
                Menu menuAcompanante = (idAcompanante != null && idAcompanante > 0) ? menuServicio.getMenuPorId(idAcompanante) : null;
                Menu menuEnsalada = (idEnsalada != null && idEnsalada > 0) ? menuServicio.getMenuPorId(idEnsalada) : null;
                
                if (menuPrincipal != null) {
                    nombresComponentes.add(menuPrincipal.getNombre());
                    precioTotalUnitario += menuPrincipal.getPrecioVenta();
                    precioTotalCCB += ccbCalculator.calcularPrecioMenuCompleto(idPrincipal, tipoUsuario);
                }
                if (menuAcompanante != null) {
                    nombresComponentes.add(menuAcompanante.getNombre());
                    precioTotalUnitario += menuAcompanante.getPrecioVenta();
                    precioTotalCCB += ccbCalculator.calcularPrecioMenuCompleto(idAcompanante, tipoUsuario);
                }
                if (menuEnsalada != null) {
                    nombresComponentes.add(menuEnsalada.getNombre());
                    precioTotalUnitario += menuEnsalada.getPrecioVenta();
                    precioTotalCCB += ccbCalculator.calcularPrecioMenuCompleto(idEnsalada, tipoUsuario);
                }
                
                nombreMenu = "Almuerzo " + dia;
            }
            
            
            if (nombresComponentes.isEmpty()) {
                componentes = "❌ No hay menú completo";
            } else {
                componentes = String.join(" + ", nombresComponentes);
                if (componentes.length() > 60) {
                    componentes = componentes.substring(0, 57) + "...";
                }
            }
            
            modeloTabla.addRow(new Object[]{
                dia,
                nombreMenu,
                componentes,
                String.format("$%.2f", precioTotalUnitario),
                String.format("$%.2f", precioTotalCCB)
            });
        }
    }
    
    private void mostrarDesgloseSeleccionado() {
        int fila = tablaPrecios.getSelectedRow();
        if (fila == -1) return;
        
        String dia = tablaPrecios.getValueAt(fila, 0).toString();
        boolean esDesayuno = comboTipoServicio.getSelectedItem().toString().equals("Desayuno");
        String tipoUsuario = comboTipoUsuario.getSelectedItem().toString();
        
        double total = ccbCalculator.calcularPrecioDiaCompleto(dia, esDesayuno, tipoUsuario);
        CCBCalculator.ResultadoCCB resultado = ccbCalculator.calcularCCB();
        double factor = ccbCalculator.getFactorUsuario(tipoUsuario);
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║           DESGLOSE - %s (%s)                            ║\n", 
            dia, esDesayuno ? "Desayuno" : "Almuerzo"));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        
        double subtotal = 0;
        
        if (esDesayuno) {
            Integer idPrincipal = menuSemana.getDesayuno(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
            Integer idAcompanante = menuSemana.getDesayuno(dia, MenuSemana.COMP_ACOMPANANTE);
            
            if (idPrincipal != null && idPrincipal > 0) {
                Menu menu = menuServicio.getMenuPorId(idPrincipal);
                double precio = ccbCalculator.calcularPrecioMenuCompleto(idPrincipal, tipoUsuario);
                subtotal += precio;
                sb.append(String.format("║ Plato principal: %-25s $%8.2f                    ║\n", 
                    menu != null ? menu.getNombre() : "N/A", precio));
            }
            
            if (idAcompanante != null && idAcompanante > 0) {
                Menu menu = menuServicio.getMenuPorId(idAcompanante);
                double precio = ccbCalculator.calcularPrecioMenuCompleto(idAcompanante, tipoUsuario);
                subtotal += precio;
                sb.append(String.format("║ Acompañante:     %-25s $%8.2f                    ║\n", 
                    menu != null ? menu.getNombre() : "N/A", precio));
            }
            
        } else {
            Integer idPrincipal = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_PLATO_PRINCIPAL);
            Integer idAcompanante = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ACOMPANANTE);
            Integer idEnsalada = menuSemana.getAlmuerzo(dia, MenuSemana.COMP_ENSALADA);
            
            if (idPrincipal != null && idPrincipal > 0) {
                Menu menu = menuServicio.getMenuPorId(idPrincipal);
                double precio = ccbCalculator.calcularPrecioMenuCompleto(idPrincipal, tipoUsuario);
                subtotal += precio;
                sb.append(String.format("║ Plato principal: %-25s $%8.2f                    ║\n", 
                    menu != null ? menu.getNombre() : "N/A", precio));
            }
            
            if (idAcompanante != null && idAcompanante > 0) {
                Menu menu = menuServicio.getMenuPorId(idAcompanante);
                double precio = ccbCalculator.calcularPrecioMenuCompleto(idAcompanante, tipoUsuario);
                subtotal += precio;
                sb.append(String.format("║ Acompañante:     %-25s $%8.2f                    ║\n", 
                    menu != null ? menu.getNombre() : "N/A", precio));
            }
            
            if (idEnsalada != null && idEnsalada > 0) {
                Menu menu = menuServicio.getMenuPorId(idEnsalada);
                double precio = ccbCalculator.calcularPrecioMenuCompleto(idEnsalada, tipoUsuario);
                subtotal += precio;
                sb.append(String.format("║ Ensalada:        %-25s $%8.2f                    ║\n", 
                    menu != null ? menu.getNombre() : "N/A", precio));
            }
        }
        
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ TOTAL DEL DÍA:                          $%8.2f                    ║\n", subtotal));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ CCB Base:                               $%8.2f                    ║\n", resultado.getCcbConMerma()));
        sb.append(String.format("║ Factor %s:                           %5.2f (%.0f%%)                ║\n", 
            tipoUsuario, factor, factor * 100));
        sb.append("╚════════════════════════════════════════════════════════════════╝");
        
        txtDesglose.setText(sb.toString());
    }
    
    public void refrescar() {
        actualizarCCB();
        cargarMenusSemanales();
        actualizarInfoSubsidio();
    }
}