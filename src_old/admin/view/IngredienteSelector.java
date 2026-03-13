package admin.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import admin.model.Ingrediente;
import admin.model.IngredienteServicio;

public class IngredienteSelector extends JDialog {
    private List<Ingrediente> ingredientesSeleccionados;
    private List<Double> cantidadesSeleccionadas;
    private List<Ingrediente> ingredientesOriginales;
    private List<Double> cantidadesOriginales;
    private JTable tablaIngredientes;
    private DefaultTableModel modeloTabla;
    private JComboBox<Ingrediente> comboIngredientes;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JLabel lblCostoTotal;
    private boolean aceptado = false;
    
    public IngredienteSelector(JFrame parent, List<Ingrediente> ingredientesActuales, List<Double> cantidadesActuales) {
        super(parent, "Seleccionar Ingredientes", true);
        
        
        this.ingredientesOriginales = new ArrayList<>(ingredientesActuales);
        this.cantidadesOriginales = new ArrayList<>(cantidadesActuales);
        this.ingredientesSeleccionados = new ArrayList<>(ingredientesActuales);
        this.cantidadesSeleccionadas = new ArrayList<>(cantidadesActuales);
        
        setSize(700, 500);
        setLocationRelativeTo(parent);
        
        initComponents();
        cargarIngredientesEnCombo();
        cargarTabla();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Agregar Ingrediente"));
        panelSuperior.setBackground(UIConstants.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Ingrediente:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        comboIngredientes = new JComboBox<>();
        comboIngredientes.setPreferredSize(new Dimension(300, 30));
        panelSuperior.add(comboIngredientes, gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelSuperior.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 4;
        txtCantidad = new JTextField(8);
        txtCantidad.setPreferredSize(new Dimension(80, 30));
        panelSuperior.add(txtCantidad, gbc);
        
        gbc.gridx = 5; gbc.gridy = 0;
        btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(UIConstants.GREEN_LIGHT);
        btnAgregar.setPreferredSize(new Dimension(100, 30));
        panelSuperior.add(btnAgregar, gbc);
        
        modeloTabla = new DefaultTableModel(
            new Object[]{"Ingrediente", "Categoría", "Unidad", "Cantidad", "Precio Unit.", "Costo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaIngredientes = new JTable(modeloTabla);
        tablaIngredientes.setFillsViewportHeight(true);
        tablaIngredientes.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaIngredientes.getTableHeader().setForeground(UIConstants.BLUE_DARK);
        
        JScrollPane scrollTabla = new JScrollPane(tablaIngredientes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Ingredientes del Menú"));
        
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(UIConstants.WHITE);
        
        JPanel panelCosto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCosto.setBackground(UIConstants.WHITE);
        lblCostoTotal = new JLabel("Costo Total: $0.00");
        lblCostoTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblCostoTotal.setForeground(UIConstants.BLUE_DARK);
        panelCosto.add(lblCostoTotal);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(UIConstants.WHITE);
        
        btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(UIConstants.RED_LIGHT);
        btnEliminar.setPreferredSize(new Dimension(180, 35));
        panelBotones.add(btnEliminar);
        
        btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(UIConstants.GREEN_LIGHT);
        btnAceptar.setPreferredSize(new Dimension(120, 35));
        panelBotones.add(btnAceptar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        panelBotones.add(btnCancelar);
        
        panelInferior.add(panelCosto, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarIngrediente();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarIngrediente();
            }
        });
        
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptado = true;
                setVisible(false);
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                ingredientesSeleccionados.clear();
                cantidadesSeleccionadas.clear();
                ingredientesSeleccionados.addAll(ingredientesOriginales);
                cantidadesSeleccionadas.addAll(cantidadesOriginales);
                aceptado = false;
                setVisible(false);
            }
        });
    }
    
    private void cargarIngredientesEnCombo() {
        IngredienteServicio servicio = new IngredienteServicio();
        List<Ingrediente> todos = servicio.getIngredientes();
        
        comboIngredientes.removeAllItems();
        for (Ingrediente ing : todos) {
            comboIngredientes.addItem(ing);
        }
        
        comboIngredientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Ingrediente) {
                    Ingrediente ing = (Ingrediente) value;
                    value = ing.getNombre() + " - $" + String.format("%.2f", ing.getPrecioUnitario()) + "/" + ing.getUnidadMedida();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }
    
    private void agregarIngrediente() {
        Ingrediente seleccionado = (Ingrediente) comboIngredientes.getSelectedItem();
        String cantidadStr = txtCantidad.getText().trim();
        
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente");
            return;
        }
        
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad");
            txtCantidad.requestFocus();
            return;
        }
        
        try {
            double cantidad = Double.parseDouble(cantidadStr.replace(",", "."));
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que 0");
                return;
            }
            
            for (int i = 0; i < ingredientesSeleccionados.size(); i++) {
                if (ingredientesSeleccionados.get(i).getId() == seleccionado.getId()) {
                    int respuesta = JOptionPane.showConfirmDialog(this,
                        "Este ingrediente ya está en la lista. ¿Desea actualizar la cantidad?",
                        "Ingrediente duplicado",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        cantidadesSeleccionadas.set(i, cantidad);
                        cargarTabla();
                    }
                    txtCantidad.setText("");
                    return;
                }
            }
            
            ingredientesSeleccionados.add(seleccionado);
            cantidadesSeleccionadas.add(cantidad);
            cargarTabla();
            txtCantidad.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido");
        }
    }
    
    private void eliminarIngrediente() {
        int fila = tablaIngredientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ingrediente para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar este ingrediente?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            ingredientesSeleccionados.remove(fila);
            cantidadesSeleccionadas.remove(fila);
            cargarTabla();
        }
    }
    
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        double total = 0;
        
        for (int i = 0; i < ingredientesSeleccionados.size(); i++) {
            Ingrediente ing = ingredientesSeleccionados.get(i);
            double cantidad = cantidadesSeleccionadas.get(i);
            double costo = ing.getPrecioUnitario() * cantidad;
            total += costo;
            
            modeloTabla.addRow(new Object[]{
                ing.getNombre(),
                ing.getCategoria(),
                ing.getUnidadMedida(),
                String.format("%.3f", cantidad),
                String.format("$%.2f", ing.getPrecioUnitario()),
                String.format("$%.2f", costo)
            });
        }
        
        lblCostoTotal.setText(String.format("Costo Total: $%.2f", total));
    }
    
    public List<Ingrediente> getIngredientesSeleccionados() {
        return ingredientesSeleccionados;
    }
    
    public List<Double> getCantidadesSeleccionadas() {
        return cantidadesSeleccionadas;
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}