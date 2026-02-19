package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.BandejaProyeccion;

public class BandejaProyeccionDialog extends JDialog {
    private BandejaProyeccion proyeccion;
    private JTable tablaDesayunos;
    private JTable tablaAlmuerzos;
    private DefaultTableModel modeloTablaDesayunos;
    private DefaultTableModel modeloTablaAlmuerzos;
    private JTextField txtMerma;
    private JLabel lblTotalSemana;
    private JLabel lblTotalDesayunos;
    private JLabel lblTotalAlmuerzos;
    private boolean aceptado = false;
    
    private final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    
    private final int[][] VALOER_EJEMPLO = {
        {150, 300}, // Lunes: 150 desayunos, 300 almuerzos
        {150, 300}, // Martes
        {150, 300}, // Miércoles
        {150, 300}, // Jueves
        {120, 250}, // Viernes (menos gente)
        {80, 150},  // Sábado
        {50, 100}   // Domingo
    };
    
    public BandejaProyeccionDialog(JFrame parent, BandejaProyeccion proyeccion) {
        super(parent, "Proyección de Bandejas - ComeUCV", true);
        this.proyeccion = proyeccion;
        
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JLabel lblTitulo = new JLabel("PROYECCIÓN DE BANDEJAS SEMANALES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(255, 255, 225));
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Información",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            UIConstants.BLUE_DARK
        ));
        
        JTextArea txtInfo = new JTextArea(
            "Los valores de proyección afectan directamente el CCB:\n" +
            "• A mayor número de bandejas, menor será el CCB\n" +
            "• Los valores por defecto están basados en una población estudiantil típica\n" +
            "• Puede ajustar estos valores según la capacidad real del comedor"
        );
        txtInfo.setEditable(false);
        txtInfo.setBackground(new Color(255, 255, 225));
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelInfo.add(txtInfo, BorderLayout.CENTER);
        
        // Panel de merma
        JPanel panelMerma = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelMerma.setBackground(UIConstants.WHITE);
        panelMerma.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Porcentaje de Merma",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            UIConstants.BLUE_DARK
        ));
        
        panelMerma.add(new JLabel("Porcentaje de merma (%):"));
        txtMerma = new JTextField(5);
        txtMerma.setPreferredSize(new Dimension(80, 25));
        panelMerma.add(txtMerma);
        panelMerma.add(new JLabel("(Ej: 5 para 5%)"));
        panelMerma.add(new JLabel("   La merma representa pérdidas en preparación"));
        
        // Panel superior combinado
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelInfo, BorderLayout.NORTH);
        panelNorte.add(panelMerma, BorderLayout.CENTER);
        
        // Panel central con las tablas
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 0));
        panelCentral.setBackground(UIConstants.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de Desayunos
        panelCentral.add(crearPanelProyeccion("DESAYUNOS", true));
        
        // Panel de Almuerzos
        panelCentral.add(crearPanelProyeccion("ALMUERZOS", false));
        
        // Panel de resumen
        JPanel panelResumen = new JPanel(new GridLayout(1, 3, 10, 5));
        panelResumen.setBackground(new Color(240, 248, 255));
        panelResumen.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Resumen de Proyección",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            UIConstants.BLUE_DARK
        ));
        
        lblTotalDesayunos = new JLabel("Total Desayunos: 0", SwingConstants.CENTER);
        lblTotalDesayunos.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalAlmuerzos = new JLabel("Total Almuerzos: 0", SwingConstants.CENTER);
        lblTotalAlmuerzos.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalSemana = new JLabel("Total Semana: 0", SwingConstants.CENTER);
        lblTotalSemana.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotalSemana.setForeground(UIConstants.BLUE_DARK);
        
        panelResumen.add(lblTotalDesayunos);
        panelResumen.add(lblTotalAlmuerzos);
        panelResumen.add(lblTotalSemana);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(UIConstants.WHITE);
        
        JButton btnGuardar = new JButton("Guardar Proyección");
        btnGuardar.setBackground(UIConstants.GREEN_LIGHT);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarProyeccion();
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
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
        
        JButton btnLimpiar = new JButton("Valores por Defecto");
        btnLimpiar.setBackground(UIConstants.BLUE_LIGHT);
        btnLimpiar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLimpiar.setPreferredSize(new Dimension(200, 40));
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(BandejaProyeccionDialog.this,
                    "¿Desea cargar los valores por defecto (basados en población estudiantil típica)?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    cargarValoresPorDefecto();
                }
            }
        });
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnLimpiar);
        
        
        add(panelNorte, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelResumen, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelSur, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelProyeccion(String titulo, boolean esDesayuno) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            titulo,
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 16),
            UIConstants.BLUE_DARK
        ));
        
        // Crear modelo de tabla
        DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Día", "Cantidad de Bandejas"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la columna de cantidad es editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) return Integer.class;
                return String.class;
            }
        };
        
        // Agregar filas para cada día
        for (String dia : DIAS) {
            modeloTabla.addRow(new Object[]{dia, 0});
        }
        
        JTable tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.setRowHeight(35);
        
        // Guardar referencia a la tabla según corresponda
        if (esDesayuno) {
            this.tablaDesayunos = tabla;
            this.modeloTablaDesayunos = modeloTabla;
        } else {
            this.tablaAlmuerzos = tabla;
            this.modeloTablaAlmuerzos = modeloTabla;
        }
        
        // Agregar listener para actualizar totales
        modeloTabla.addTableModelListener(e -> {
            actualizarTotales();
        });
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(400, 300));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarDatos() {
        // Cargar porcentaje de merma
        txtMerma.setText(String.valueOf(proyeccion.getPorcentajeMerma()));
        
        // Cargar proyecciones existentes
        for (int i = 0; i < DIAS.length; i++) {
            String dia = DIAS[i];
            
            // Desayunos
            int valorDesayuno = proyeccion.getProyeccionDesayuno(dia);
            if (valorDesayuno > 0) {
                modeloTablaDesayunos.setValueAt(valorDesayuno, i, 1);
            } else {
                
                modeloTablaDesayunos.setValueAt(VALOER_EJEMPLO[i][0], i, 1);
            }
            
            // Almuerzos
            int valorAlmuerzo = proyeccion.getProyeccionAlmuerzo(dia);
            if (valorAlmuerzo > 0) {
                modeloTablaAlmuerzos.setValueAt(valorAlmuerzo, i, 1);
            } else {
                
                modeloTablaAlmuerzos.setValueAt(VALOER_EJEMPLO[i][1], i, 1);
            }
        }
        
        actualizarTotales();
    }
    
    private void cargarValoresPorDefecto() {
        for (int i = 0; i < DIAS.length; i++) {
            modeloTablaDesayunos.setValueAt(VALOER_EJEMPLO[i][0], i, 1);
            modeloTablaAlmuerzos.setValueAt(VALOER_EJEMPLO[i][1], i, 1);
        }
        txtMerma.setText("5.0");
        actualizarTotales();
    }
    
    private void guardarProyeccion() {
        try {
            // Validar y guardar porcentaje de merma
            double merma = Double.parseDouble(txtMerma.getText().trim().replace(",", "."));
            if (merma < 0 || merma > 30) {
                JOptionPane.showMessageDialog(this,
                    "El porcentaje de merma debe estar entre 0 y 30",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            proyeccion.setPorcentajeMerma(merma);
            
            // Guardar proyecciones de desayunos
            for (int i = 0; i < DIAS.length; i++) {
                Object valorObj = modeloTablaDesayunos.getValueAt(i, 1);
                if (valorObj != null) {
                    int cantidad = Integer.parseInt(valorObj.toString());
                    if (cantidad >= 0) {
                        proyeccion.setProyeccionDesayuno(DIAS[i], cantidad);
                    }
                }
            }
            
            // Guardar proyecciones de almuerzos
            for (int i = 0; i < DIAS.length; i++) {
                Object valorObj = modeloTablaAlmuerzos.getValueAt(i, 1);
                if (valorObj != null) {
                    int cantidad = Integer.parseInt(valorObj.toString());
                    if (cantidad >= 0) {
                        proyeccion.setProyeccionAlmuerzo(DIAS[i], cantidad);
                    }
                }
            }
            
            aceptado = true;
            
            
            int totalDesayunos = proyeccion.getTotalBandejasServicio("Desayuno");
            int totalAlmuerzos = proyeccion.getTotalBandejasServicio("Almuerzo");
            
            JOptionPane.showMessageDialog(this,
                String.format(
                    "Proyección guardada exitosamente.\n\n" +
                    "Resumen:\n" +
                    "• Total Desayunos: %d bandejas/semana\n" +
                    "• Total Almuerzos: %d bandejas/semana\n" +
                    "• Merma: %.1f%%\n\n" +
                    "Estos valores se usarán para calcular el CCB.",
                    totalDesayunos, totalAlmuerzos, merma),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            setVisible(false);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Ingrese números válidos en todos los campos",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTotales() {
        int totalDesayunos = 0;
        int totalAlmuerzos = 0;
        
        // Calcular total desayunos
        for (int i = 0; i < DIAS.length; i++) {
            Object valorObj = modeloTablaDesayunos.getValueAt(i, 1);
            if (valorObj != null) {
                try {
                    totalDesayunos += Integer.parseInt(valorObj.toString());
                } catch (NumberFormatException e) {
                    // Ignorar valores no numéricos
                }
            }
        }
        
        // Calcular total almuerzos
        for (int i = 0; i < DIAS.length; i++) {
            Object valorObj = modeloTablaAlmuerzos.getValueAt(i, 1);
            if (valorObj != null) {
                try {
                    totalAlmuerzos += Integer.parseInt(valorObj.toString());
                } catch (NumberFormatException e) {
                    // Ignorar valores no numéricos
                }
            }
        }
        
        lblTotalDesayunos.setText("Total Desayunos: " + totalDesayunos);
        lblTotalAlmuerzos.setText("Total Almuerzos: " + totalAlmuerzos);
        lblTotalSemana.setText("Total Semana: " + (totalDesayunos + totalAlmuerzos));
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}