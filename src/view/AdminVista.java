package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import controller.MenuControlador;

public class AdminVista extends JFrame {
    // Componentes del Formulario
    public JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Costo Fijo", "Costo Variable", "Menú"});
    public JComboBox<String> comboDia = new JComboBox<>(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"});
    public JTextField txtConcepto = new JTextField(12);
    public JTextField txtMonto = new JTextField(5);
    public JTextField txtProt = new JTextField(5);
    public JTextField txtKcal = new JTextField(5);
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnEditar = new JButton("Editar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar");
    
    // Nuevos componentes para período
    public JPanel panelPeriodo = new JPanel();
    public JComboBox<String> comboUnidadPeriodo = new JComboBox<>(new String[]{"días", "semanas", "meses"});
    public JTextField txtCantidadPeriodo = new JTextField(5);
    
    // Tabla para visualizar
    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"Categoría", "Día", "Detalle", "Precio/Monto", "Período", "Prot", "Kcal"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    public JTable tablaDatos = new JTable(modeloTabla);
    private int filaSeleccionada = -1;
    private boolean modoEdicion = false;

    public AdminVista() {
        setTitle("Panel Administrativo ComeUCV - Carga de Costos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        configurarMenu();
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("CARGA DE COSTOS", SwingConstants.CENTER);
        lblTitulo.setFont(UIConstants.TITLE_FONT);
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JPanel panelForm = crearPanelFormulario();

        JScrollPane scroll = new JScrollPane(tablaDatos);
        tablaDatos.setFillsViewportHeight(true);

        tablaDatos.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaDatos.getTableHeader().setForeground(Color.WHITE);

        JPanel panelBotones = crearPanelBotones();

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.add(panelForm, BorderLayout.NORTH);
        panelCentral.add(scroll, BorderLayout.CENTER);
        
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelBotones, BorderLayout.CENTER);
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        setLocationRelativeTo(null);
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuItemSalir = new JMenuItem("Salir");
        menuItemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(menuItemSalir);
        
        JMenu menuNavegacion = new JMenu("Navegación");
        JMenuItem menuItemCostos = new JMenuItem("Gestión de Costos");
        menuItemCostos.setEnabled(false);
        JMenuItem menuItemMenus = new JMenuItem("Carga de Menús");
        menuItemMenus.addActionListener(e -> abrirGestionMenus());
        
        menuNavegacion.add(menuItemCostos);
        menuNavegacion.add(menuItemMenus);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuItemAcerca = new JMenuItem("Acerca de");
        menuItemAcerca.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Sistema Administrativo ComeUCV\n" +
                "Versión 1.0\n" +
                "Carga de Costos y Menús\n\n" +
                "Seleccione 'Carga de Menús' en el menú Navegación\n" +
                "para administrar los menús semanales.",
                "Acerca de ComeUCV",
                JOptionPane.INFORMATION_MESSAGE);
        });
        menuAyuda.add(menuItemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuNavegacion);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Carga de Datos"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; panel.add(comboTipo, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Nombre/Concepto:"), gbc);
        gbc.gridx = 3; panel.add(txtConcepto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Día (Menú):"), gbc);
        gbc.gridx = 1; panel.add(comboDia, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Monto $:"), gbc);
        gbc.gridx = 3; panel.add(txtMonto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Proteínas (g):"), gbc);
        gbc.gridx = 1; panel.add(txtProt, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Calorías (Kcal):"), gbc);
        gbc.gridx = 3; panel.add(txtKcal, gbc);

        panelPeriodo.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPeriodo.setBackground(UIConstants.WHITE);
        panelPeriodo.add(new JLabel("Período:"));
        txtCantidadPeriodo.setPreferredSize(new Dimension(50, 25));
        panelPeriodo.add(txtCantidadPeriodo);
        comboUnidadPeriodo.setPreferredSize(new Dimension(80, 25));
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
        panel.add(btnGuardar);
        
        btnEditar.setBackground(new Color(255, 204, 102));
        btnEditar.setFont(UIConstants.HEADER_FONT);
        btnEditar.setEnabled(false);
        panel.add(btnEditar);
        
        btnEliminar.setBackground(new Color(255, 102, 102));
        btnEliminar.setFont(UIConstants.HEADER_FONT);
        btnEliminar.setEnabled(false);
        panel.add(btnEliminar);
        
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(UIConstants.HEADER_FONT);
        btnCancelar.setVisible(false);
        panel.add(btnCancelar);
        
        return panel;
    }

    public void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        btnGuardar.setText(habilitar ? "Actualizar" : "Guardar");
        btnEditar.setEnabled(!habilitar);
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
        txtProt.setText(prot.replace("g", "").trim());
        txtKcal.setText(kcal.replace("kcal", "").trim());
    }
    
    private void abrirGestionMenus() {
        MenuVista vistaMenus = new MenuVista();
        new MenuControlador(vistaMenus);
        vistaMenus.setVisible(true);
    }
}