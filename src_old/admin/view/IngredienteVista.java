package admin.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import admin.model.Ingrediente;

public class IngredienteVista extends JFrame {
    //Campos del formulario
    public JTextField txtNombre = new JTextField(15);
    public JComboBox<String> comboUnidad = new JComboBox<>(new String[]{
        "kg", "g", "litro", "ml", "unidad", "docena", "libra"
    });
    public JTextField txtPrecio = new JTextField(10);
    public JTextField txtStock = new JTextField(10);
    public JComboBox<String> comboCategoria = new JComboBox<>(new String[]{
        "Granos", "Carnes", "Verduras", "Frutas", "Lácteos", 
        "Aceites", "Condimentos", "Bebidas", "Otros"
    });
    
    //Botones
    public JButton btnGuardar = new JButton("Guardar Ingrediente");
    public JButton btnEditar = new JButton("Editar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar");
    public JButton btnVolver = new JButton("← Volver a Costos");
    
    //Tabla
    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"ID", "Nombre", "Categoría", "Unidad", "Precio Unit.", "Stock"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public JTable tablaIngredientes = new JTable(modeloTabla);
    
    private int filaSeleccionada = -1;
    private boolean modoEdicion = false;
    
    public IngredienteVista() {
        setTitle("Gestión de Ingredientes - ComeUCV");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        getContentPane().setBackground(UIConstants.WHITE);
        
        JLabel lblTitulo = new JLabel("ADMIN - GESTIÓN DE INGREDIENTES", SwingConstants.CENTER);
        lblTitulo.setFont(UIConstants.TITLE_FONT);
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelFormulario = crearPanelFormulario();
        
        JScrollPane scrollTabla = new JScrollPane(tablaIngredientes);
        tablaIngredientes.setFillsViewportHeight(true);
        tablaIngredientes.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaIngredientes.getTableHeader().setForeground(UIConstants.BLUE_DARK);
        tablaIngredientes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JPanel panelBotones = crearPanelBotones();
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(scrollTabla, BorderLayout.SOUTH);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        
        tablaIngredientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaIngredientes.getSelectedRow() != -1) {
                filaSeleccionada = tablaIngredientes.getSelectedRow();
                btnEditar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });
        
        setLocationRelativeTo(null);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Datos del Ingrediente"
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //fila 1: Nombre y Categoría
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre.setPreferredSize(new Dimension(200, 25));
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 3;
        comboCategoria.setPreferredSize(new Dimension(150, 25));
        panel.add(comboCategoria, gbc);
        
        //fila 2: Unidad y Precio
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Unidad de Medida:"), gbc);
        gbc.gridx = 1;
        comboUnidad.setPreferredSize(new Dimension(100, 25));
        panel.add(comboUnidad, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Precio Unitario ($):"), gbc);
        gbc.gridx = 3;
        txtPrecio.setPreferredSize(new Dimension(100, 25));
        panel.add(txtPrecio, gbc);
        
        //fila 3: Stock
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Cantidad en Stock:"), gbc);
        gbc.gridx = 1;
        txtStock.setPreferredSize(new Dimension(100, 25));
        panel.add(txtStock, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(en la unidad seleccionada)"), gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(UIConstants.WHITE);
        
        btnGuardar.setBackground(UIConstants.GREEN_LIGHT);
        btnGuardar.setFont(UIConstants.HEADER_FONT);
        btnGuardar.setPreferredSize(new Dimension(150, 35));
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
        
        btnVolver.setBackground(new Color(150, 150, 200));
        btnVolver.setFont(UIConstants.HEADER_FONT);
        btnVolver.setForeground(UIConstants.BLUE_DARK);
        btnVolver.setPreferredSize(new Dimension(180, 35));
        panel.add(btnVolver);
        
        return panel;
    }
    
    public void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        btnGuardar.setText(habilitar ? "Actualizar" : "Guardar Ingrediente");
        btnEditar.setEnabled(!habilitar);
        btnEliminar.setEnabled(!habilitar);
        btnCancelar.setVisible(habilitar);
        
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
        tablaIngredientes.clearSelection();
        filaSeleccionada = -1;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        comboUnidad.setSelectedIndex(0);
        comboCategoria.setSelectedIndex(0);
    }
    
    public void cargarDatosEnFormulario(Ingrediente ing) {
        txtNombre.setText(ing.getNombre());
        comboUnidad.setSelectedItem(ing.getUnidadMedida());
        txtPrecio.setText(String.valueOf(ing.getPrecioUnitario()));
        txtStock.setText(String.valueOf(ing.getCantidadStock()));
        comboCategoria.setSelectedItem(ing.getCategoria());
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
    public int confirmarEliminacion() {
        return JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este ingrediente?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
    }
}