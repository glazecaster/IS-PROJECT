package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.Menu;

public class MenuVista extends JFrame {
    public JComboBox<String> comboDia = new JComboBox<>(new String[]{
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes"
    });
    public JComboBox<String> comboTipo = new JComboBox<>(new String[]{
        "Plato principal", "Acompañante", "Ensalada", "Entrada", "Postre", "Bebida"
    });
    public JTextField txtNombre = new JTextField(15);
    public JTextArea txtDescripcion = new JTextArea(3, 20);
    public JTextField txtProteinas = new JTextField(8);
    public JTextField txtCarbohidratos = new JTextField(8);
    public JTextField txtCalorias = new JTextField(8);
    public JTextField txtPrecio = new JTextField(8);
    
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnEditar = new JButton("Editar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar");
    public JButton btnFiltrar = new JButton("Filtrar por Día");
    public JButton btnVolver = new JButton("← Volver a Costos");
    public JButton btnVerTodos = new JButton("Ver Todos");
    
    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"Día", "Nombre", "Tipo", "Descripción", "Proteínas", "Carbohidratos", "Calorías", "Precio"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public JTable tablaMenus = new JTable(modeloTabla);
    
    private int filaSeleccionada = -1;
    private boolean modoEdicion = false;
    
    public MenuVista() {
        setTitle("Gestión de Menús - ComeUCV");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        getContentPane().setBackground(UIConstants.WHITE);
        
        configurarMenuBar();
        
        JLabel lblTitulo = new JLabel("GESTIÓN DE MENÚS SEMANALES", SwingConstants.CENTER);
        lblTitulo.setFont(UIConstants.TITLE_FONT);
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelFormulario = crearPanelFormulario();
        
        JScrollPane scrollTabla = new JScrollPane(tablaMenus);
        tablaMenus.setFillsViewportHeight(true);
        tablaMenus.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaMenus.getTableHeader().setForeground(Color.WHITE);
        
        JPanel panelBotones = crearPanelBotones();
        
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.add(panelFormulario, BorderLayout.NORTH);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        tablaMenus.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMenus.getSelectedRow() != -1) {
                filaSeleccionada = tablaMenus.getSelectedRow();
                btnEditar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });
        
        setLocationRelativeTo(null);
    }
    
    private void configurarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuItemCerrar = new JMenuItem("Cerrar Menús");
        menuItemCerrar.addActionListener(e -> dispose());
        menuArchivo.add(menuItemCerrar);
        
        JMenu menuAcciones = new JMenu("Acciones");
        JMenuItem menuItemLimpiar = new JMenuItem("Limpiar Formulario");
        menuItemLimpiar.addActionListener(e -> limpiarFormulario());
        menuAcciones.add(menuItemLimpiar);
        
        JMenuItem menuItemExportar = new JMenuItem("Exportar Menús");
        menuItemExportar.addActionListener(e -> exportarMenus());
        menuAcciones.add(menuItemExportar);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuItemInfo = new JMenuItem("Información de Menús");
        menuItemInfo.addActionListener(e -> mostrarInfoMenus());
        menuAyuda.add(menuItemInfo);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAcciones);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private void exportarMenus() {
        JOptionPane.showMessageDialog(this,
            "Los menús se guardan automáticamente en 'test/base_datos_menus.txt'\n" +
            "Formato: Día|Nombre|Tipo|Descripción|Proteínas|Carbohidratos|Calorías|Precio",
            "Información de Exportación",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarInfoMenus() {
        JOptionPane.showMessageDialog(this,
            "GESTIÓN DE MENÚS SEMANALES\n\n" +
            "Funciones disponibles:\n" +
            "1. Agregar nuevo menú - Complete el formulario y presione Guardar\n" +
            "2. Editar menú - Seleccione en la tabla y presione Editar\n" +
            "3. Eliminar menú - Seleccione en la tabla y presione Eliminar\n" +
            "4. Filtrar por día - Ver menús de un día específico\n" +
            "5. Ver todos - Mostrar todos los menús\n\n" +
            "Los cambios se guardan automáticamente.",
            "Ayuda - Gestión de Menús",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Menú"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Día:"), gbc);
        gbc.gridx = 1;
        panel.add(comboDia, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3;
        panel.add(comboTipo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNombre.setPreferredSize(new Dimension(300, 25));
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        JPanel panelNutricion = new JPanel(new GridLayout(1, 6, 5, 0));
        panelNutricion.setBackground(UIConstants.WHITE);
        panelNutricion.setBorder(BorderFactory.createTitledBorder("Información Nutricional"));
        
        panelNutricion.add(new JLabel("Proteínas:"));
        txtProteinas.setPreferredSize(new Dimension(70, 25));
        panelNutricion.add(txtProteinas);
        
        panelNutricion.add(new JLabel("Carbohidratos:"));
        txtCarbohidratos.setPreferredSize(new Dimension(70, 25));
        panelNutricion.add(txtCarbohidratos);
        
        panelNutricion.add(new JLabel("Calorías:"));
        txtCalorias.setPreferredSize(new Dimension(70, 25));
        panelNutricion.add(txtCalorias);
        
        gbc.gridx = 0; gbc.gridwidth = 4;
        panel.add(panelNutricion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel panelPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPrecio.setBackground(UIConstants.WHITE);
        panelPrecio.add(new JLabel("Precio ($):"));
        txtPrecio.setPreferredSize(new Dimension(100, 25));
        panelPrecio.add(txtPrecio);
        panelPrecio.add(new JLabel("Ejemplo: 8.50"));
        
        panel.add(panelPrecio, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelSuperior.setBackground(UIConstants.WHITE);
        
        btnGuardar.setBackground(UIConstants.GREEN_LIGHT);
        btnGuardar.setFont(UIConstants.HEADER_FONT);
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        panelSuperior.add(btnGuardar);
        
        btnEditar.setBackground(UIConstants.YELLOW_LIGHT);
        btnEditar.setFont(UIConstants.HEADER_FONT);
        btnEditar.setPreferredSize(new Dimension(120, 35));
        btnEditar.setEnabled(false);
        panelSuperior.add(btnEditar);
        
        btnEliminar.setBackground(UIConstants.RED_LIGHT);
        btnEliminar.setFont(UIConstants.HEADER_FONT);
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        btnEliminar.setEnabled(false);
        panelSuperior.add(btnEliminar);
        
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(UIConstants.HEADER_FONT);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setVisible(false);
        panelSuperior.add(btnCancelar);
        
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferior.setBackground(UIConstants.WHITE);
        
        btnFiltrar.setBackground(new Color(173, 216, 230));
        btnFiltrar.setFont(UIConstants.HEADER_FONT);
        btnFiltrar.setPreferredSize(new Dimension(150, 35));
        panelInferior.add(btnFiltrar);
        
        btnVerTodos.setBackground(new Color(200, 230, 255));
        btnVerTodos.setFont(UIConstants.HEADER_FONT);
        btnVerTodos.setPreferredSize(new Dimension(120, 35));
        panelInferior.add(btnVerTodos);
        
        btnVolver.setBackground(new Color(150, 150, 200));
        btnVolver.setFont(UIConstants.HEADER_FONT);
        btnVolver.setPreferredSize(new Dimension(180, 35));
        panelInferior.add(btnVolver);
        
        panel.add(panelSuperior);
        panel.add(panelInferior);
        
        return panel;
    }
    
    public void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        btnGuardar.setText(habilitar ? "Actualizar" : "Guardar");
        btnEditar.setEnabled(!habilitar);
        btnEliminar.setEnabled(!habilitar);
        btnCancelar.setVisible(habilitar);
        btnFiltrar.setEnabled(!habilitar);
        btnVerTodos.setEnabled(!habilitar);
        
        if (habilitar) {
            btnGuardar.setBackground(new Color(102, 255, 102));
        } else {
            btnGuardar.setBackground(UIConstants.GREEN_LIGHT);
        }
    }
    
    public boolean isModoEdicion() {
        return modoEdicion;
    }
    
    public int getFilaSeleccionada() {
        return filaSeleccionada;
    }
    
    public void deseleccionarFila() {
        tablaMenus.clearSelection();
        filaSeleccionada = -1;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    public void cargarDatosEnFormulario(Menu menu) {
        comboDia.setSelectedItem(menu.getDia());
        comboTipo.setSelectedItem(menu.getTipo());
        txtNombre.setText(menu.getNombre());
        txtDescripcion.setText(menu.getDescripcion());
        txtProteinas.setText(menu.getProteinas());
        txtCarbohidratos.setText(menu.getCarbohidratos());
        txtCalorias.setText(menu.getCalorias());
        txtPrecio.setText(String.format("%.2f", menu.getPrecio()));
    }
    
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtProteinas.setText("");
        txtCarbohidratos.setText("");
        txtCalorias.setText("");
        txtPrecio.setText("");
        comboDia.setSelectedIndex(0);
        comboTipo.setSelectedIndex(0);
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
    public int confirmarEliminacion() {
        return JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este menú?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
    }
    
    public String pedirDiaFiltro() {
        return JOptionPane.showInputDialog(this,
            "Ingrese el día a filtrar (Lunes, Martes, etc.):\n" +
            "Deje vacío para cancelar",
            "Filtrar por Día",
            JOptionPane.QUESTION_MESSAGE);
    }
}