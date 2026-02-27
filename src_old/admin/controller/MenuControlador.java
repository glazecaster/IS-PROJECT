package admin.controller;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import admin.model.Menu;
import admin.model.MenuServicio;
import admin.model.MenuSemana;
import admin.model.Ingrediente;
import admin.model.IngredienteServicio;
import admin.view.CCBPanel;
import admin.view.IngredienteSelector;
import admin.view.MenuSemanaSelector;

public class MenuControlador {
    private admin.view.MenuVista vista;
    private MenuServicio servicio;
    private IngredienteServicio ingredienteServicio;
    private MenuSemana menuSemana;
    private java.awt.Frame ventanaAnterior;
    
    public MenuControlador(admin.view.MenuVista vista) {
        this(vista, null);
    }
    
    public MenuControlador(admin.view.MenuVista vista, java.awt.Frame ventanaAnterior) {
        this.vista = vista;
        this.ventanaAnterior = ventanaAnterior;
        this.servicio = new MenuServicio();
        this.ingredienteServicio = new IngredienteServicio();
        this.menuSemana = new MenuSemana();
        
        configurarTabla();
        cargarTodosMenus();
        configurarListeners();
    }
    
    private void configurarTabla() {
        vista.tablaMenus.setRowSelectionAllowed(true);
        vista.tablaMenus.setColumnSelectionAllowed(false);
        vista.tablaMenus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        vista.tablaMenus.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        vista.tablaMenus.getColumnModel().getColumn(1).setPreferredWidth(60);   // Día
        vista.tablaMenus.getColumnModel().getColumn(2).setPreferredWidth(70);   // Servicio
        vista.tablaMenus.getColumnModel().getColumn(3).setPreferredWidth(120);  // Nombre
        vista.tablaMenus.getColumnModel().getColumn(4).setPreferredWidth(90);   // Tipo
        vista.tablaMenus.getColumnModel().getColumn(5).setPreferredWidth(200);  // Descripción
        vista.tablaMenus.getColumnModel().getColumn(6).setPreferredWidth(70);   // Proteínas
        vista.tablaMenus.getColumnModel().getColumn(7).setPreferredWidth(80);   // Carbohidratos
        vista.tablaMenus.getColumnModel().getColumn(8).setPreferredWidth(70);   // Calorías
        vista.tablaMenus.getColumnModel().getColumn(9).setPreferredWidth(70);   // Precio
        vista.tablaMenus.getColumnModel().getColumn(10).setPreferredWidth(80);  // Costo Ingredientes
        vista.tablaMenus.getColumnModel().getColumn(11).setPreferredWidth(200); // Ingredientes
        vista.tablaMenus.getColumnModel().getColumn(12).setPreferredWidth(60);  // Semana
    }
    
    private void configurarListeners() {
        vista.btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vista.isModoEdicion()) {
                    actualizarMenu();
                } else {
                    guardarMenu();
                }
            }
        });
        
        vista.btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarMenu();
            }
        });
        
        vista.btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarMenu();
            }
        });
        
        vista.btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarEdicion();
            }
        });
        
        vista.btnFiltrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtrarPorDia();
            }
        });
        
        vista.btnVerTodos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTodosMenus();
            }
        });
        
        vista.btnSeleccionarIngredientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarIngredientes();
            }
        });
        
        vista.btnVerCCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarPanelCCB();
            }
        });
        
        vista.btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                volverAVentanaAnterior();
            }
        });
        
        vista.btnMenuSemana.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirMenuSemanaSelector();
            }
        });
    }
    
    private void abrirMenuSemanaSelector() {
        MenuSemanaSelector selector = new MenuSemanaSelector(vista);
        selector.setVisible(true);
        
        if (selector.isAceptado()) {
            this.menuSemana = selector.getMenuSemana();
            actualizarIndicadoresSemanaEnTabla();
            vista.mostrarMensaje("Menú de la semana guardado exitosamente");
        }
    }
    
    private void actualizarIndicadoresSemanaEnTabla() {
        for (int row = 0; row < vista.modeloTabla.getRowCount(); row++) {
            int id = (int) vista.modeloTabla.getValueAt(row, 0);
            String dia = vista.modeloTabla.getValueAt(row, 1).toString();
            String servicio = vista.modeloTabla.getValueAt(row, 2).toString();
            
            boolean esDeLaSemana = false;
            
            if (servicio.equals("Desayuno")) {
                Integer selectedId = menuSemana.getDesayuno(dia);
                esDeLaSemana = (selectedId != null && selectedId == id);
            } else if (servicio.equals("Almuerzo")) {
                Integer selectedId = menuSemana.getAlmuerzo(dia);
                esDeLaSemana = (selectedId != null && selectedId == id);
            }
            
            vista.modeloTabla.setValueAt(esDeLaSemana ? "★" : "", row, 12);
        }
    }
    
    private void volverAVentanaAnterior() {
        vista.dispose();
        if (ventanaAnterior != null) {
            ventanaAnterior.setVisible(true);
        }
    }
    
    private void guardarMenu() {
        Menu menu = crearMenuDesdeFormulario();
        if (menu != null) {
            servicio.guardarMenu(menu);
            agregarMenuATabla(menu);
            vista.limpiarFormulario();
            vista.mostrarMensaje("¡Menú guardado exitosamente!");
        }
    }
    
    private void actualizarMenu() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            vista.mostrarMensaje("Seleccione un menú para editar");
            return;
        }
        
        Menu menu = crearMenuDesdeFormulario();
        if (menu != null) {
            
            int idOriginal = (int) vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            menu.setId(idOriginal);
            
            
            Menu menuOriginal = servicio.getMenuPorId(idOriginal);
            if (menuOriginal != null) {
                for (int i = 0; i < menuOriginal.getIngredientes().size(); i++) {
                    menu.agregarIngrediente(
                        menuOriginal.getIngredientes().get(i),
                        menuOriginal.getCantidades().get(i)
                    );
                }
            }
            
            java.util.List<Menu> todosMenus = servicio.getMenus();
            int indiceArchivo = -1;
            
            for (int i = 0; i < todosMenus.size(); i++) {
                if (todosMenus.get(i).getId() == idOriginal) {
                    indiceArchivo = i;
                    break;
                }
            }
            
            if (indiceArchivo != -1) {
                servicio.actualizarMenu(indiceArchivo, menu);
                
                vista.modeloTabla.removeRow(filaSeleccionada);
                vista.modeloTabla.insertRow(filaSeleccionada, convertirMenuAFila(menu));
                
                
                actualizarIndicadoresSemanaEnTabla();
                
                vista.limpiarFormulario();
                vista.habilitarEdicion(false);
                vista.deseleccionarFila();
                vista.mostrarMensaje("¡Menú actualizado exitosamente!");
            } else {
                vista.mostrarMensaje("Error: No se encontró el menú para actualizar");
            }
        }
    }
    
    private void editarMenu() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            vista.mostrarMensaje("Seleccione un menú para editar");
            return;
        }
        
        try {
            int id = (int) vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            String dia = vista.modeloTabla.getValueAt(filaSeleccionada, 1).toString();
            String servicio = vista.modeloTabla.getValueAt(filaSeleccionada, 2).toString();
            String nombre = vista.modeloTabla.getValueAt(filaSeleccionada, 3).toString();
            String tipo = vista.modeloTabla.getValueAt(filaSeleccionada, 4).toString();
            String descripcion = vista.modeloTabla.getValueAt(filaSeleccionada, 5).toString();
            String proteinas = vista.modeloTabla.getValueAt(filaSeleccionada, 6).toString();
            String carbohidratos = vista.modeloTabla.getValueAt(filaSeleccionada, 7).toString();
            String calorias = vista.modeloTabla.getValueAt(filaSeleccionada, 8).toString();
            
            String precioStr = vista.modeloTabla.getValueAt(filaSeleccionada, 9).toString();
            precioStr = precioStr.replace("$", "").replace(",", ".").trim();
            precioStr = precioStr.replaceAll("[^\\d.]", "");
            
            if (precioStr.isEmpty()) {
                vista.mostrarMensaje("Error: El precio está vacío o no es válido");
                return;
            }
            
            double precio = Double.parseDouble(precioStr);
            
            Menu menu = new Menu(id, dia, nombre, tipo, servicio, descripcion, 
                                proteinas, carbohidratos, calorias, precio);
            
            
            Menu menuCompleto = this.servicio.getMenuPorId(id);
            if (menuCompleto != null) {
                for (int i = 0; i < menuCompleto.getIngredientes().size(); i++) {
                    menu.agregarIngrediente(
                        menuCompleto.getIngredientes().get(i),
                        menuCompleto.getCantidades().get(i)
                    );
                }
            }
            
            vista.cargarDatosEnFormulario(menu);
            vista.habilitarEdicion(true);
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("Error: El precio no tiene un formato numérico válido");
            e.printStackTrace();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al cargar datos del menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void eliminarMenu() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            vista.mostrarMensaje("Seleccione un menú para eliminar");
            return;
        }
        
        int confirmacion = vista.confirmarEliminacion();
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = (int) vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            
            
            boolean estaEnSemana = false;
            for (String dia : new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}) {
                Integer desayunoId = menuSemana.getDesayuno(dia);
                Integer almuerzoId = menuSemana.getAlmuerzo(dia);
                
                if ((desayunoId != null && desayunoId == id) || (almuerzoId != null && almuerzoId == id)) {
                    estaEnSemana = true;
                    break;
                }
            }
            
            if (estaEnSemana) {
                int respuesta = JOptionPane.showConfirmDialog(vista,
                    "Este menú está seleccionado en el Menú de la Semana.\n" +
                    "Si lo elimina, también se eliminará de la selección semanal.\n\n" +
                    "¿Desea continuar?",
                    "Menú en selección semanal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
                
                
                for (String dia : new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"}) {
                    Integer desayunoId = menuSemana.getDesayuno(dia);
                    Integer almuerzoId = menuSemana.getAlmuerzo(dia);
                    
                    if (desayunoId != null && desayunoId == id) {
                        menuSemana.eliminarDesayuno(dia);
                    }
                    if (almuerzoId != null && almuerzoId == id) {
                        menuSemana.eliminarAlmuerzo(dia);
                    }
                }
            }
            
            java.util.List<Menu> todosMenus = servicio.getMenus();
            int indiceArchivo = -1;
            
            for (int i = 0; i < todosMenus.size(); i++) {
                if (todosMenus.get(i).getId() == id) {
                    indiceArchivo = i;
                    break;
                }
            }
            
            if (indiceArchivo != -1) {
                servicio.eliminarMenu(indiceArchivo);
                vista.modeloTabla.removeRow(filaSeleccionada);
                vista.deseleccionarFila();
                vista.mostrarMensaje("Menú eliminado exitosamente");
            } else {
                vista.mostrarMensaje("Error: No se encontró el menú para eliminar");
            }
        }
    }
    
    private void cancelarEdicion() {
        vista.limpiarFormulario();
        vista.habilitarEdicion(false);
        vista.deseleccionarFila();
    }
    
    private void filtrarPorDia() {
        
        JDialog dialog = new JDialog(vista, "Seleccionar Día", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(vista);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblSeleccion = new JLabel("Seleccione el día a filtrar:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblSeleccion, gbc);
        
        JComboBox<String> comboDiaFiltro = new JComboBox<>(new String[]{
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        });
        gbc.gridy = 1;
        panel.add(comboDiaFiltro, gbc);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(e -> {
            String diaSeleccionado = comboDiaFiltro.getSelectedItem().toString();
            aplicarFiltroPorDia(diaSeleccionado);
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void aplicarFiltroPorDia(String dia) {
        java.util.List<Menu> menus = servicio.getMenusPorDia(dia);
        if (menus.isEmpty()) {
            int respuesta = JOptionPane.showConfirmDialog(vista,
                "No hay menús disponibles para el día " + dia + ".\n" +
                "¿Desea agregar un nuevo menú?",
                "Sin menús disponibles",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                vista.limpiarFormulario();
                vista.comboDia.setSelectedItem(dia);
                vista.mostrarMensaje("Complete los datos para agregar un nuevo menú para " + dia);
            }
        } else {
            vista.modeloTabla.setRowCount(0);
            for (Menu menu : menus) {
                agregarMenuATabla(menu);
            }
            actualizarIndicadoresSemanaEnTabla();
        }
    }
    
    private void cargarTodosMenus() {
        java.util.List<Menu> menus = servicio.getMenus();
        System.out.println("Cargando todos los menús: " + menus.size() + " encontrados");
        
        vista.modeloTabla.setRowCount(0);
        
        if (menus.isEmpty()) {
            vista.mostrarMensaje("No hay menús disponibles en la base de datos");
        } else {
            for (Menu menu : menus) {
                agregarMenuATabla(menu);
            }
            actualizarIndicadoresSemanaEnTabla();
            vista.mostrarMensaje("Se cargaron " + menus.size() + " menús");
        }
    }
    
    private void seleccionarIngredientes() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (filaSeleccionada == -1) {
            vista.mostrarMensaje("Seleccione un menú para configurar sus ingredientes");
            return;
        }
        
        try {
            int id = (int) vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            Menu menu = servicio.getMenuPorId(id);
            
            if (menu != null) {
                IngredienteSelector selector = new IngredienteSelector(
                    vista, 
                    menu.getIngredientes(), 
                    menu.getCantidades()
                );
                
                selector.setVisible(true);
                
                if (selector.isAceptado()) {
                    java.util.List<Ingrediente> nuevosIngredientes = selector.getIngredientesSeleccionados();
                    java.util.List<Double> nuevasCantidades = selector.getCantidadesSeleccionadas();
                    
                    menu.limpiarIngredientes();
                    for (int i = 0; i < nuevosIngredientes.size(); i++) {
                        menu.agregarIngrediente(nuevosIngredientes.get(i), nuevasCantidades.get(i));
                    }
                    
                    java.util.List<Menu> todosMenus = servicio.getMenus();
                    int indiceArchivo = -1;
                    for (int i = 0; i < todosMenus.size(); i++) {
                        if (todosMenus.get(i).getId() == id) {
                            indiceArchivo = i;
                            break;
                        }
                    }
                    
                    if (indiceArchivo != -1) {
                        servicio.actualizarMenu(indiceArchivo, menu);
                        
                        // Actualizar la fila en la tabla
                        Object[] filaActualizada = convertirMenuAFila(menu);
                        for (int i = 0; i < filaActualizada.length; i++) {
                            vista.modeloTabla.setValueAt(filaActualizada[i], filaSeleccionada, i);
                        }
                        
                        vista.mostrarMensaje("Ingredientes actualizados exitosamente");
                    }
                }
            }
        } catch (Exception e) {
            vista.mostrarMensaje("Error al configurar ingredientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarPanelCCB() {
        JFrame frameCCB = new JFrame("Cálculo de CCB - ComeUCV");
        frameCCB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameCCB.setSize(1100, 750);
        frameCCB.setLocationRelativeTo(vista);
        
        CCBPanel ccbPanel = new CCBPanel(frameCCB, vista);
        frameCCB.add(ccbPanel);
        frameCCB.setVisible(true);
        vista.setVisible(false);
    }
    
    private Menu crearMenuDesdeFormulario() {
        String dia = vista.comboDia.getSelectedItem().toString();
        String servicio = vista.comboTipoServicio.getSelectedItem().toString();
        String nombre = vista.txtNombre.getText().trim();
        String tipo = vista.comboTipo.getSelectedItem().toString();
        String descripcion = vista.txtDescripcion.getText().trim();
        String proteinas = vista.txtProteinas.getText().trim();
        String carbohidratos = vista.txtCarbohidratos.getText().trim();
        String calorias = vista.txtCalorias.getText().trim();
        String precioStr = vista.txtPrecio.getText().trim();
        
        
        if (nombre.isEmpty()) {
            vista.mostrarMensaje("El nombre del menú es obligatorio");
            vista.txtNombre.requestFocus();
            return null;
        }
        
        if (descripcion.isEmpty()) {
            vista.mostrarMensaje("La descripción es obligatoria");
            vista.txtDescripcion.requestFocus();
            return null;
        }
        
        if (precioStr.isEmpty()) {
            vista.mostrarMensaje("El precio es obligatorio");
            vista.txtPrecio.requestFocus();
            return null;
        }
        
        
        if (!proteinas.isEmpty() && !proteinas.matches("^\\d+(\\.\\d+)?\\s*(-\\s*\\d+(\\.\\d+)?)?\\s*g$") 
            && !proteinas.equals("0 g") && !proteinas.equals("0g")) {
            vista.mostrarMensaje("Formato de proteínas inválido. Use: '30 g' o '30-40 g'");
            vista.txtProteinas.requestFocus();
            return null;
        }
        
        if (!carbohidratos.isEmpty() && !carbohidratos.matches("^\\d+(\\.\\d+)?\\s*(-\\s*\\d+(\\.\\d+)?)?\\s*g$")
            && !carbohidratos.equals("0 g") && !carbohidratos.equals("0g")) {
            vista.mostrarMensaje("Formato de carbohidratos inválido. Use: '45 g' o '40-50 g'");
            vista.txtCarbohidratos.requestFocus();
            return null;
        }
        
        if (!calorias.isEmpty() && !calorias.matches("^\\d+(\\.\\d+)?\\s*(-\\s*\\d+(\\.\\d+)?)?\\s*kcal$")
            && !calorias.equals("0 kcal") && !calorias.equals("0kcal")) {
            vista.mostrarMensaje("Formato de calorías inválido. Use: '500 kcal' o '450-550 kcal'");
            vista.txtCalorias.requestFocus();
            return null;
        }
        
        try {
            precioStr = precioStr.replace(",", ".").replaceAll("[^\\d.]", "");
            double precio = Double.parseDouble(precioStr);
            
            if (precio <= 0) {
                vista.mostrarMensaje("El precio debe ser mayor que 0");
                vista.txtPrecio.requestFocus();
                return null;
            }
            
            
            if (proteinas.isEmpty()) proteinas = "0 g";
            if (carbohidratos.isEmpty()) carbohidratos = "0 g";
            if (calorias.isEmpty()) calorias = "0 kcal";
            
            return new Menu(0, dia, nombre, tipo, servicio, descripcion, 
                          proteinas, carbohidratos, calorias, precio);
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El precio debe ser un número válido (ejemplo: 8.50)");
            vista.txtPrecio.requestFocus();
            return null;
        }
    }
    
    private void agregarMenuATabla(Menu menu) {
        vista.modeloTabla.addRow(convertirMenuAFila(menu));
    }
    
    private Object[] convertirMenuAFila(Menu menu) {
        
        boolean esDeLaSemana = false;
        
        if (menu.getTipoServicio().equals("Desayuno")) {
            Integer selectedId = menuSemana.getDesayuno(menu.getDia());
            esDeLaSemana = (selectedId != null && selectedId == menu.getId());
        } else if (menu.getTipoServicio().equals("Almuerzo")) {
            Integer selectedId = menuSemana.getAlmuerzo(menu.getDia());
            esDeLaSemana = (selectedId != null && selectedId == menu.getId());
        }
        
        return new Object[]{
            menu.getId(),
            menu.getDia(),
            menu.getTipoServicio(),
            menu.getNombre(),
            menu.getTipo(),
            menu.getDescripcion(),
            menu.getProteinas(),
            menu.getCarbohidratos(),
            menu.getCalorias(),
            String.format("$%.2f", menu.getPrecioVenta()),
            String.format("$%.2f", menu.calcularCostoIngredientes()),
            menu.getIngredientesComoString(),
            esDeLaSemana ? "★" : ""
        };
    }
}