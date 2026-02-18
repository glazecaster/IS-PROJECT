package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import modelo.Menu;
import modelo.MenuSemana;

public class MenuVista extends JFrame {
    // Componentes del formulario
    public JComboBox<String> comboDia = new JComboBox<>(new String[]{
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    });
    public JComboBox<String> comboTipoServicio = new JComboBox<>(new String[]{
        "Desayuno", "Almuerzo"
    });
    public JComboBox<String> comboTipo = new JComboBox<>(new String[]{
        "Plato principal", "Acompañante", "Ensalada", "Entrada", "Postre", "Bebida", "Desayuno"
    });
    public JTextField txtNombre = new JTextField(15);
    public JTextArea txtDescripcion = new JTextArea(3, 20);
    public JTextField txtProteinas = new JTextField(8);
    public JTextField txtCarbohidratos = new JTextField(8);
    public JTextField txtCalorias = new JTextField(8);
    public JTextField txtPrecio = new JTextField(8);
    
    // Botones
    public JButton btnGuardar = new JButton("Guardar");
    public JButton btnEditar = new JButton("Editar");
    public JButton btnEliminar = new JButton("Eliminar");
    public JButton btnCancelar = new JButton("Cancelar");
    public JButton btnFiltrar = new JButton("Filtrar por Día");
    public JButton btnVerTodos = new JButton("Ver Todos");
    public JButton btnSeleccionarIngredientes = new JButton("Seleccionar Ingredientes");
    public JButton btnVerCCB = new JButton("Ver Cálculo CCB");
    public JButton btnMenuSemana = new JButton("📅 Menú de esta Semana");
    
    // Tabla
    public DefaultTableModel modeloTabla = new DefaultTableModel(
        new Object[]{"ID", "Día", "Servicio", "Nombre", "Tipo", "Descripción", 
                    "Proteínas", "Carbohidratos", "Calorías", "Precio", 
                    "Costo Ingred.", "Ingredientes", "Semana"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    public JTable tablaMenus = new JTable(modeloTabla);
    
    private int filaSeleccionada = -1;
    private boolean modoEdicion = false;
    private MenuSemana menuSemana;
    
    public MenuVista() {
        setTitle("Gestión de Menús - ComeUCV");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        getContentPane().setBackground(UIConstants.WHITE);
        this.menuSemana = new MenuSemana();
        
        configurarMenu(); // Misma estructura que AdminVista
        
        JLabel lblTitulo = new JLabel("ADMIN - GESTIÓN DE MENÚS SEMANALES", SwingConstants.CENTER);
        lblTitulo.setFont(UIConstants.TITLE_FONT);
        lblTitulo.setForeground(UIConstants.BLUE_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(UIConstants.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelFormulario = crearPanelFormulario();
        
        JScrollPane scrollTabla = new JScrollPane(tablaMenus);
        tablaMenus.setFillsViewportHeight(true);
        tablaMenus.setRowHeight(30);
        tablaMenus.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        tablaMenus.getTableHeader().setForeground(UIConstants.BLUE_DARK);
        tablaMenus.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Renderizador personalizado para la tabla
        tablaMenus.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color COLOR_SELECCIONADO_SEMANA = new Color(255, 255, 200);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    Object semanaObj = table.getValueAt(row, 12);
                    boolean esDeLaSemana = semanaObj != null && semanaObj.toString().equals("★");
                    
                    if (esDeLaSemana) {
                        c.setBackground(COLOR_SELECCIONADO_SEMANA);
                        
                        if (column == 12) {
                            setForeground(new Color(255, 140, 0));
                            setFont(c.getFont().deriveFont(Font.BOLD, 16));
                        } else {
                            setForeground(Color.BLACK);
                            setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        try {
                            double precio = Double.parseDouble(table.getValueAt(row, 9).toString().replace("$", "").replace(",", ""));
                            double costo = Double.parseDouble(table.getValueAt(row, 10).toString().replace("$", "").replace(",", ""));
                            
                            if (costo > 0) {
                                double margen = ((precio - costo) / precio) * 100;
                                if (margen >= 40) {
                                    c.setBackground(new Color(200, 255, 200));
                                } else if (margen >= 20) {
                                    c.setBackground(new Color(255, 255, 200));
                                } else {
                                    c.setBackground(new Color(255, 200, 200));
                                }
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        } catch (Exception e) {
                            c.setBackground(Color.WHITE);
                        }
                        setForeground(Color.BLACK);
                        setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    if (column == 12) {
                        setFont(c.getFont().deriveFont(Font.BOLD, 16));
                    }
                }
                
                if (column == 0 || column == 1 || column == 2 || column == 9 || column == 10 || column == 12) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        });
        
        JPanel panelBotones = crearPanelBotones();
        
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.add(panelFormulario, BorderLayout.NORTH);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        tablaMenus.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMenus.getSelectedRow() != -1) {
                filaSeleccionada = tablaMenus.getSelectedRow();
                btnEditar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnSeleccionarIngredientes.setEnabled(true);
            }
        });
        
        setLocationRelativeTo(null);
    }
    
    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        
        JMenuItem cerrarSesionMenuItem = new JMenuItem("Cerrar sesión");
        cerrarSesionMenuItem.addActionListener(e -> cerrarSesion());
        
        JMenuItem salirMenuItem = new JMenuItem("Salir");
        salirMenuItem.addActionListener(e -> System.exit(0));
        
        archivoMenu.add(cerrarSesionMenuItem);
        archivoMenu.addSeparator();
        archivoMenu.add(salirMenuItem);
        
        // Menú Navegación (igual que AdminVista)
        JMenu navegacionMenu = new JMenu("Navegación");
        
        JMenuItem costosMenuItem = new JMenuItem("Gestión de Costos");
        costosMenuItem.addActionListener(e -> abrirGestionCostos());
        
        JMenuItem menusMenuItem = new JMenuItem("Gestión de Menús");
        menusMenuItem.setEnabled(false); // Deshabilitado porque ya estamos aquí
        
        JMenuItem ccbMenuItem = new JMenuItem("Cálculo CCB");
        ccbMenuItem.addActionListener(e -> abrirPanelCCB());
        
        navegacionMenu.add(costosMenuItem);
        navegacionMenu.add(menusMenuItem);
        navegacionMenu.addSeparator();
        navegacionMenu.add(ccbMenuItem);
        
        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaMenuItem = new JMenuItem("Acerca de");
        acercaMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Sistema Administrativo ComeUCV\n" +
                "Versión 3.0\n\n" +
                "Módulo: Gestión de Menús Semanales\n\n" +
                "Funciones:\n" +
                "• Crear, editar y eliminar menús\n" +
                "• Asignar ingredientes a cada menú\n" +
                "• Seleccionar menús para la semana\n" +
                "• Ver cálculos de costos y márgenes",
                "Acerca de ComeUCV - Menús",
                JOptionPane.INFORMATION_MESSAGE);
        });
        ayudaMenu.add(acercaMenuItem);
        
        menuBar.add(archivoMenu);
        menuBar.add(navegacionMenu);
        menuBar.add(ayudaMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void cerrarSesion() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            view.LoginView loginView = new view.LoginView();
            new controller.LoginController(loginView);
            loginView.setVisible(true);
        });
    }
    
    private void abrirGestionCostos() {
        this.dispose();
        AdminVista vistaCostos = new AdminVista();
        new controlador.AdminControlador(vistaCostos);
        vistaCostos.setVisible(true);
    }
    
    private void abrirPanelCCB() {
        this.dispose();
        JFrame frameCCB = new JFrame("Cálculo de CCB - ComeUCV");
        frameCCB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameCCB.setSize(1100, 750);
        frameCCB.setLocationRelativeTo(null);
    
        CCBPanel ccbPanel = new CCBPanel(frameCCB);
        frameCCB.add(ccbPanel);
        frameCCB.setVisible(true);
    }
    
    // ... (todos los demás métodos existentes se mantienen igual: crearPanelFormulario, crearPanelBotones, etc.)
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BLUE_DARK),
            "Datos del Menú",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            UIConstants.BLUE_DARK
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //fila 1: Día, Servicio y Tipo
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Día:"), gbc);
        gbc.gridx = 1;
        comboDia.setPreferredSize(new Dimension(120, 25));
        panel.add(comboDia, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Servicio:"), gbc);
        gbc.gridx = 3;
        comboTipoServicio.setPreferredSize(new Dimension(120, 25));
        panel.add(comboTipoServicio, gbc);
        
        gbc.gridx = 4;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 5;
        comboTipo.setPreferredSize(new Dimension(150, 25));
        panel.add(comboTipo, gbc);
        
        //fila 2: Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        txtNombre.setPreferredSize(new Dimension(500, 25));
        panel.add(txtNombre, gbc);
        
        //fila 3: Descripción
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5;
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(500, 60));
        panel.add(scrollDescripcion, gbc);
        
        //fila 4: Información Nutricional
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 6;
        JPanel panelNutricion = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelNutricion.setBackground(UIConstants.WHITE);
        panelNutricion.setBorder(BorderFactory.createTitledBorder("Información Nutricional"));
        
        panelNutricion.add(new JLabel("Proteínas (g):"));
        txtProteinas.setPreferredSize(new Dimension(80, 25));
        panelNutricion.add(txtProteinas);
        
        panelNutricion.add(new JLabel("Carbohidratos (g):"));
        txtCarbohidratos.setPreferredSize(new Dimension(80, 25));
        panelNutricion.add(txtCarbohidratos);
        
        panelNutricion.add(new JLabel("Calorías (kcal):"));
        txtCalorias.setPreferredSize(new Dimension(80, 25));
        panelNutricion.add(txtCalorias);
        
        panel.add(panelNutricion, gbc);
        
        //fila 5: Precio
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel panelPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPrecio.setBackground(UIConstants.WHITE);
        panelPrecio.add(new JLabel("Precio de Venta ($):"));
        txtPrecio.setPreferredSize(new Dimension(100, 25));
        panelPrecio.add(txtPrecio);
        panelPrecio.add(new JLabel("Ejemplo: 8.50"));
        
        panel.add(panelPrecio, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //Botones de acción principal
        gbc.gridy = 0;
        gbc.gridx = 0;
        btnGuardar.setBackground(UIConstants.GREEN_LIGHT);
        btnGuardar.setFont(UIConstants.HEADER_FONT);
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        panel.add(btnGuardar, gbc);
        
        gbc.gridx = 1;
        btnEditar.setBackground(UIConstants.YELLOW_LIGHT);
        btnEditar.setFont(UIConstants.HEADER_FONT);
        btnEditar.setPreferredSize(new Dimension(120, 35));
        btnEditar.setEnabled(false);
        panel.add(btnEditar, gbc);
        
        gbc.gridx = 2;
        btnEliminar.setBackground(UIConstants.RED_LIGHT);
        btnEliminar.setFont(UIConstants.HEADER_FONT);
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        btnEliminar.setEnabled(false);
        panel.add(btnEliminar, gbc);
        
        gbc.gridx = 3;
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFont(UIConstants.HEADER_FONT);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setVisible(false);
        panel.add(btnCancelar, gbc);
        
        //Botones de utilidad
        gbc.gridy = 1;
        gbc.gridx = 0;
        btnFiltrar.setBackground(new Color(173, 216, 230));
        btnFiltrar.setFont(UIConstants.HEADER_FONT);
        btnFiltrar.setPreferredSize(new Dimension(150, 35));
        panel.add(btnFiltrar, gbc);
        
        gbc.gridx = 1;
        btnVerTodos.setBackground(new Color(200, 230, 255));
        btnVerTodos.setFont(UIConstants.HEADER_FONT);
        btnVerTodos.setPreferredSize(new Dimension(120, 35));
        panel.add(btnVerTodos, gbc);
        
        gbc.gridx = 2;
        btnSeleccionarIngredientes.setBackground(new Color(200, 180, 255));
        btnSeleccionarIngredientes.setFont(UIConstants.HEADER_FONT);
        btnSeleccionarIngredientes.setPreferredSize(new Dimension(180, 35));
        btnSeleccionarIngredientes.setEnabled(false);
        panel.add(btnSeleccionarIngredientes, gbc);
        
        gbc.gridx = 3;
        btnVerCCB.setBackground(new Color(255, 215, 0));
        btnVerCCB.setFont(UIConstants.HEADER_FONT);
        btnVerCCB.setPreferredSize(new Dimension(150, 35));
        panel.add(btnVerCCB, gbc);
        
        //Botón Menú de la Semana
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        btnMenuSemana.setBackground(new Color(255, 200, 150));
        btnMenuSemana.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnMenuSemana.setForeground(UIConstants.BLUE_DARK);
        btnMenuSemana.setPreferredSize(new Dimension(300, 45));
        panel.add(btnMenuSemana, gbc);
        
        return panel;
    }
    
    private void mostrarResumenSemana() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("MENÚ DE LA SEMANA\n");
        mensaje.append("=================\n\n");
        
        mensaje.append("DESAYUNOS:\n");
        for (String dia : new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}) {
            Integer id = menuSemana.getDesayuno(dia);
            if (id != null) {
                Menu menu = new modelo.MenuServicio().getMenuPorId(id);
                if (menu != null) {
                    mensaje.append(String.format("  %-9s: ★ %s ($%.2f)\n", 
                        dia, menu.getNombre(), menu.getPrecioVenta()));
                } else {
                    mensaje.append(String.format("  %-9s: ★ (ID: %d)\n", dia, id));
                }
            } else {
                mensaje.append(String.format("  %-9s: —\n", dia));
            }
        }
        
        mensaje.append("\nALMUERZOS:\n");
        for (String dia : new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}) {
            Integer id = menuSemana.getAlmuerzo(dia);
            if (id != null) {
                Menu menu = new modelo.MenuServicio().getMenuPorId(id);
                if (menu != null) {
                    mensaje.append(String.format("  %-9s: ★ %s ($%.2f)\n", 
                        dia, menu.getNombre(), menu.getPrecioVenta()));
                } else {
                    mensaje.append(String.format("  %-9s: ★ (ID: %d)\n", dia, id));
                }
            } else {
                mensaje.append(String.format("  %-9s: —\n", dia));
            }
        }
        
        JTextArea textArea = new JTextArea(mensaje.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
            "Resumen del Menú Semanal", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actualizarIndicadoresSemana() {
        for (int row = 0; row < modeloTabla.getRowCount(); row++) {
            int id = (int) modeloTabla.getValueAt(row, 0);
            String dia = modeloTabla.getValueAt(row, 1).toString();
            String servicio = modeloTabla.getValueAt(row, 2).toString();
            
            boolean esDeLaSemana = false;
            
            if (servicio.equals("Desayuno")) {
                Integer selectedId = menuSemana.getDesayuno(dia);
                esDeLaSemana = (selectedId != null && selectedId == id);
            } else if (servicio.equals("Almuerzo")) {
                Integer selectedId = menuSemana.getAlmuerzo(dia);
                esDeLaSemana = (selectedId != null && selectedId == id);
            }
            
            modeloTabla.setValueAt(esDeLaSemana ? "★" : "", row, 12);
        }
    }
    
    private void abrirMenuSemanaSelector() {
        MenuSemanaSelector selector = new MenuSemanaSelector(this);
        selector.setVisible(true);
        
        if (selector.isAceptado()) {
            this.menuSemana = selector.getMenuSemana();
            actualizarIndicadoresSemana();
            mostrarMensaje("Menú de la semana guardado exitosamente");
        }
    }
    
    public void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        btnGuardar.setText(habilitar ? "Actualizar" : "Guardar");
        btnEditar.setEnabled(!habilitar);
        btnEliminar.setEnabled(!habilitar);
        btnSeleccionarIngredientes.setEnabled(!habilitar ? false : btnSeleccionarIngredientes.isEnabled());
        btnCancelar.setVisible(habilitar);
        btnFiltrar.setEnabled(!habilitar);
        btnVerTodos.setEnabled(!habilitar);
        btnVerCCB.setEnabled(!habilitar);
        btnMenuSemana.setEnabled(!habilitar);
        
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
        btnSeleccionarIngredientes.setEnabled(false);
    }
    
    public void cargarDatosEnFormulario(Menu menu) {
        comboDia.setSelectedItem(menu.getDia());
        comboTipoServicio.setSelectedItem(menu.getTipoServicio());
        comboTipo.setSelectedItem(menu.getTipo());
        txtNombre.setText(menu.getNombre());
        txtDescripcion.setText(menu.getDescripcion());
        txtProteinas.setText(menu.getProteinas());
        txtCarbohidratos.setText(menu.getCarbohidratos());
        txtCalorias.setText(menu.getCalorias());
        txtPrecio.setText(String.format("%.2f", menu.getPrecioVenta()));
    }
    
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtProteinas.setText("");
        txtCarbohidratos.setText("");
        txtCalorias.setText("");
        txtPrecio.setText("");
        comboDia.setSelectedIndex(0);
        comboTipoServicio.setSelectedIndex(0);
        comboTipo.setSelectedIndex(0);
    }
    
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
    public int confirmarEliminacion() {
        return JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este menú?\n" +
            "Esto también eliminará todos sus ingredientes asociados.",
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