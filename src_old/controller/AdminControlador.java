package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import model.ArchivoServicio;
import view.UIConstants;  // Pendiente con este import 

public class AdminControlador {
    private view.AdminVista vista;
    private ArchivoServicio servicio;

    public AdminControlador(view.AdminVista vista) {
        this.vista = vista;
        this.servicio = new ArchivoServicio();
        
        configurarTabla();
        cargarDatosExistentes();
        configurarListeners();
    }

    private void configurarTabla() {
        vista.tablaDatos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color COLOR_FIJO = new Color(220, 240, 255);
            private final Color COLOR_VARIABLE = new Color(255, 245, 220);
            private final Color COLOR_MENU = new Color(220, 255, 220);
            private final Color COLOR_SELECCION = new Color(10, 57, 102);
            private final Color COLOR_CABECERA_CELDA = new Color(200, 220, 255);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (column == 0 || column == 1) {
                    if (!isSelected) {
                        c.setBackground(COLOR_CABECERA_CELDA);
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
                            c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(COLOR_SELECCION);
                    c.setForeground(Color.WHITE);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        vista.tablaDatos.getTableHeader().setBackground(UIConstants.BLUE_DARK);
        vista.tablaDatos.getTableHeader().setForeground(Color.WHITE);
        vista.tablaDatos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        vista.tablaDatos.setRowSelectionAllowed(true);
        vista.tablaDatos.setColumnSelectionAllowed(false);
        vista.tablaDatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        int[] anchos = {100, 80, 150, 80, 100, 60, 70};
        for (int i = 0; i < anchos.length; i++) {
            vista.tablaDatos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void configurarListeners() {
        vista.btnGuardar.addActionListener(e -> {
            if (vista.isModoEdicion()) {
                actualizarRegistro();
            } else {
                guardarNuevoRegistro();
            }
        });

        vista.btnEditar.addActionListener(e -> editarRegistro());
        vista.btnEliminar.addActionListener(e -> eliminarRegistro());
        vista.btnCancelar.addActionListener(e -> cancelarEdicion());
        
        vista.tablaDatos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vista.tablaDatos.getSelectedRow() != -1) {
                vista.setFilaSeleccionada(vista.tablaDatos.getSelectedRow());
                vista.habilitarBotonesEdicionEliminacion(true);
            }
        });
    }

    private void guardarNuevoRegistro() {
        String registro = construirRegistro();
        if (registro != null) {
            servicio.guardarCosto(registro);
            vista.modeloTabla.addRow(servicio.parseLineaATabla(registro));
            limpiar();
            mostrarMensaje("¡Registro guardado exitosamente!");
        }
    }

    private void actualizarRegistro() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (!validarFilaSeleccionada(filaSeleccionada, "editar")) return;

        String nuevoRegistro = construirRegistro();
        if (nuevoRegistro != null) {
            servicio.actualizarCosto(filaSeleccionada, nuevoRegistro);
            actualizarFilaTabla(filaSeleccionada, nuevoRegistro);
            finalizarEdicion();
            mostrarMensaje("¡Registro actualizado exitosamente!");
        }
    }

    private void editarRegistro() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (!validarFilaSeleccionada(filaSeleccionada, "editar")) return;

        Object[] datos = new Object[7];
        for (int i = 0; i < 7; i++) {
            datos[i] = vista.modeloTabla.getValueAt(filaSeleccionada, i);
        }
        vista.cargarDatosEnFormulario(datos);
        vista.habilitarEdicion(true);
    }

    private void eliminarRegistro() {
        int filaSeleccionada = vista.getFilaSeleccionada();
        if (!validarFilaSeleccionada(filaSeleccionada, "eliminar")) return;

        if (confirmarAccion("¿Está seguro de eliminar este registro?", "Confirmar eliminación")) {
            servicio.eliminarCosto(filaSeleccionada);
            vista.modeloTabla.removeRow(filaSeleccionada);
            vista.deseleccionarFila();
            mostrarMensaje("Registro eliminado exitosamente");
        }
    }

    private void cancelarEdicion() {
        limpiar();
        finalizarEdicion();
    }

    private String construirRegistro() {
        String tipo = vista.comboTipo.getSelectedItem().toString();
        String dia = vista.comboDia.getSelectedItem().toString();
        String concepto = vista.txtConcepto.getText().trim();
        String monto = vista.txtMonto.getText().trim();
        
        if (!validarCamposObligatorios(concepto, monto)) return null;
        if (!validarNumero(monto, "El monto debe ser un número válido")) return null;
        
        String periodo = construirPeriodo(tipo);
        if (periodo == null) return null;
        
        String prot = formatearValor(vista.txtProt.getText().trim(), "g", "0g");
        String kcal = formatearValor(vista.txtKcal.getText().trim(), " kcal", "0 kcal");
        
        return String.format("%s|%s|%s|%s|%s|%s|%s", 
                tipo, dia, concepto, monto, periodo, prot, kcal);
    }

    private String construirPeriodo(String tipo) {
        switch (tipo) {
            case "Costo Variable":
                String cantidad = vista.txtCantidadPeriodo.getText().trim();
                String unidad = vista.comboUnidadPeriodo.getSelectedItem().toString();
                if (cantidad.isEmpty()) {
                    mostrarMensaje("Por favor especifica el período para Costo Variable");
                    return null;
                }
                if (!validarNumero(cantidad, "La cantidad del período debe ser un número")) {
                    return null;
                }
                return cantidad + " " + unidad;
            case "Costo Fijo":
                return "Fijo";
            case "Menú":
                return vista.comboDia.getSelectedItem().toString();
            default:
                return "";
        }
    }

    private void limpiar() {
        vista.txtConcepto.setText("");
        vista.txtMonto.setText("");
        vista.txtProt.setText("");
        vista.txtKcal.setText("");
        vista.txtCantidadPeriodo.setText("");
        vista.comboTipo.setSelectedIndex(0);
        vista.comboDia.setSelectedIndex(0);
        vista.panelPeriodo.setVisible(false);
    }

    private void cargarDatosExistentes() {
        vista.modeloTabla.setRowCount(0);
        for (String registro : servicio.getRegistros()) {
            Object[] datosTabla = servicio.parseLineaATabla(registro);
            if (datosTabla != null) {
                vista.modeloTabla.addRow(datosTabla);
            }
        }
    }

    private boolean validarFilaSeleccionada(int fila, String accion) {
        if (fila == -1) {
            mostrarMensaje("Seleccione un registro para " + accion);
            return false;
        }
        return true;
    }

    private void actualizarFilaTabla(int fila, String registro) {
        Object[] datosTabla = servicio.parseLineaATabla(registro);
        for (int i = 0; i < datosTabla.length; i++) {
            vista.modeloTabla.setValueAt(datosTabla[i], fila, i);
        }
    }

    private void finalizarEdicion() {
        vista.habilitarEdicion(false);
        vista.deseleccionarFila();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje);
    }

    private boolean confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(vista, mensaje, titulo, 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private boolean validarCamposObligatorios(String... campos) {
        for (String campo : campos) {
            if (campo.isEmpty()) {
                mostrarMensaje("Por favor complete todos los campos obligatorios");
                return false;
            }
        }
        return true;
    }

    private boolean validarNumero(String valor, String mensajeError) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            mostrarMensaje(mensajeError);
            return false;
        }
    }

    private String formatearValor(String valor, String unidad, String valorPorDefecto) {
        return valor.isEmpty() ? valorPorDefecto : valor + unidad;
    }
}