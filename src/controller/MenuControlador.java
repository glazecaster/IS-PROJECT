package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import model.Menu;
import model.MenuServicio;

public class MenuControlador {
    private view.MenuVista vista;
    private MenuServicio servicio;
    
    public MenuControlador(view.MenuVista vista) {
        this.vista = vista;
        this.servicio = new MenuServicio();
        
        configurarTabla();
        cargarTodosMenusSilencioso();
        configurarListeners();
    }
    
    private void configurarTabla() {
        vista.tablaMenus.setRowSelectionAllowed(true);
        vista.tablaMenus.setColumnSelectionAllowed(false);
        vista.tablaMenus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        vista.tablaMenus.getColumnModel().getColumn(0).setPreferredWidth(80);
        vista.tablaMenus.getColumnModel().getColumn(1).setPreferredWidth(150);
        vista.tablaMenus.getColumnModel().getColumn(2).setPreferredWidth(100);
        vista.tablaMenus.getColumnModel().getColumn(3).setPreferredWidth(200);
        vista.tablaMenus.getColumnModel().getColumn(4).setPreferredWidth(80);
        vista.tablaMenus.getColumnModel().getColumn(5).setPreferredWidth(90);
        vista.tablaMenus.getColumnModel().getColumn(6).setPreferredWidth(80);
        vista.tablaMenus.getColumnModel().getColumn(7).setPreferredWidth(70);
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
                cargarTodosMenusSilencioso();
            }
        });
        
        vista.btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        });
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
            List<Menu> todosMenus = servicio.getMenus();
            int indiceArchivo = -1;
            
            Object nombreTabla = vista.modeloTabla.getValueAt(filaSeleccionada, 1);
            Object diaTabla = vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            
            for (int i = 0; i < todosMenus.size(); i++) {
                Menu m = todosMenus.get(i);
                if (m.getNombre().equals(nombreTabla.toString()) && 
                    m.getDia().equals(diaTabla.toString())) {
                    indiceArchivo = i;
                    break;
                }
            }
            
            if (indiceArchivo != -1) {
                servicio.actualizarMenu(indiceArchivo, menu);
                
                vista.modeloTabla.removeRow(filaSeleccionada);
                vista.modeloTabla.insertRow(filaSeleccionada, convertirMenuAFila(menu));
                
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
            String dia = vista.modeloTabla.getValueAt(filaSeleccionada, 0).toString();
            String nombre = vista.modeloTabla.getValueAt(filaSeleccionada, 1).toString();
            String tipo = vista.modeloTabla.getValueAt(filaSeleccionada, 2).toString();
            String descripcion = vista.modeloTabla.getValueAt(filaSeleccionada, 3).toString();
            String proteinas = vista.modeloTabla.getValueAt(filaSeleccionada, 4).toString();
            String carbohidratos = vista.modeloTabla.getValueAt(filaSeleccionada, 5).toString();
            String calorias = vista.modeloTabla.getValueAt(filaSeleccionada, 6).toString();
            
            String precioStr = vista.modeloTabla.getValueAt(filaSeleccionada, 7).toString();
            precioStr = precioStr.replace("$", "").replace(",", ".").trim();
            precioStr = precioStr.replaceAll("[^\\d.]", "");
            
            if (precioStr.isEmpty()) {
                vista.mostrarMensaje("Error: El precio está vacío o no es válido");
                return;
            }
            
            double precio = Double.parseDouble(precioStr);
            
            Menu menu = new Menu(dia, nombre, tipo, descripcion, proteinas, carbohidratos, calorias, precio);
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
            List<Menu> todosMenus = servicio.getMenus();
            int indiceArchivo = -1;
            
            Object nombreTabla = vista.modeloTabla.getValueAt(filaSeleccionada, 1);
            Object diaTabla = vista.modeloTabla.getValueAt(filaSeleccionada, 0);
            
            for (int i = 0; i < todosMenus.size(); i++) {
                Menu m = todosMenus.get(i);
                if (m.getNombre().equals(nombreTabla.toString()) && 
                    m.getDia().equals(diaTabla.toString())) {
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
        String dia = vista.pedirDiaFiltro();
        
        if (dia != null && !dia.trim().isEmpty()) {
            dia = dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();
            
            List<Menu> menus = servicio.getMenusPorDia(dia);
            if (menus.isEmpty()) {
                vista.mostrarMensaje("No hay menús para el día: " + dia);
            } else {
                vista.modeloTabla.setRowCount(0);
                for (Menu menu : menus) {
                    agregarMenuATabla(menu);
                }
            }
        }
    }
    
    private void cargarTodosMenusSilencioso() {
        vista.modeloTabla.setRowCount(0);
        List<Menu> menus = servicio.getMenus();
        for (Menu menu : menus) {
            agregarMenuATabla(menu);
        }
    }
    
    private void cerrarVentana() {
        vista.dispose();
    }
    
    private Menu crearMenuDesdeFormulario() {
        String dia = vista.comboDia.getSelectedItem().toString();
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
            
            return new Menu(dia, nombre, tipo, descripcion, proteinas, carbohidratos, calorias, precio);
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
        return new Object[]{
            menu.getDia(),
            menu.getNombre(),
            menu.getTipo(),
            menu.getDescripcion(),
            menu.getProteinas(),
            menu.getCarbohidratos(),
            menu.getCalorias(),
            String.format("$%.2f", menu.getPrecio())
        };
    }
}