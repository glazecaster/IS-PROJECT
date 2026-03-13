package admin.controller;

import admin.model.ArchivoServicio;
import admin.model.CCBCalculator;
import admin.view.UIConstants;
import model.AsistenciaServicio;
import model.UsuarioServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminControlador {
    private admin.view.AdminVista vista;
    private ArchivoServicio servicio;
    private final UsuarioServicio usuarios;
    private final AsistenciaServicio asistencias;
    private CCBCalculator ccbCalculator; 

    public AdminControlador(admin.view.AdminVista vista) {
        this.vista = vista;
        this.servicio = new ArchivoServicio();
        this.usuarios = new UsuarioServicio("data/usuarios.txt");
        this.asistencias = new AsistenciaServicio();
        this.ccbCalculator = new CCBCalculator(); 

        
        vista.setControlador(this);

        configurarTabla();
        cargarDatosExistentes();
        configurarListeners();
        cargarProyeccionInicial();
    }

    
    public CCBCalculator getCcbCalculator() {
        return ccbCalculator;
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

                if (!isSelected) {
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

        int[] anchos = {100, 80, 200, 80, 100};
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
        vista.btnGestionBeneficios.addActionListener(e -> gestionarBeneficioEstudiantil());
        vista.btnReporteAsistencias.addActionListener(e -> mostrarReporteAsistencias());
        vista.btnGuardarProyeccion.addActionListener(e -> guardarProyeccion());

        vista.tablaDatos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vista.tablaDatos.getSelectedRow() != -1) {
                vista.setFilaSeleccionada(vista.tablaDatos.getSelectedRow());
                vista.habilitarBotonesEdicionEliminacion(true);
            }
        });
    }

    private void cargarProyeccionInicial() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        for (int i = 0; i < dias.length; i++) {
            int val = ccbCalculator.getBandejasPorDia(dias[i]);
            vista.txtBandejasPorDia[i].setText(String.valueOf(val));
        }
        actualizarTotalProyeccion();
    }

    private void guardarProyeccion() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        int total = 0;
        boolean ok = true;
        for (int i = 0; i < dias.length; i++) {
            try {
                int val = Integer.parseInt(vista.txtBandejasPorDia[i].getText().trim());
                if (val < 0 || val > 1000) {
                    vista.mostrarMensaje("El valor para " + dias[i] + " debe estar entre 0 y 1000.");
                    ok = false;
                    break;
                }
                ccbCalculator.setBandejasPorDia(dias[i], val);
                total += val;
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Ingrese un número válido para " + dias[i]);
                ok = false;
                break;
            }
        }
        if (ok) {
            vista.lblTotalBandejas.setText("Total semanal: " + total);
            vista.mostrarMensaje("Proyección guardada correctamente.");
        }
    }

    private void actualizarTotalProyeccion() {
        int total = ccbCalculator.getTotalBandejasSemanal();
        vista.lblTotalBandejas.setText("Total semanal: " + total);
    }

    
    
    private void gestionarBeneficioEstudiantil() {
        try {
            
            String cedula = JOptionPane.showInputDialog(vista,
                    "Ingrese la cédula del estudiante a gestionar:",
                    "Gestionar becario / exonerado",
                    JOptionPane.PLAIN_MESSAGE);
            if (cedula == null) return;
            cedula = cedula.trim();

            
            if (!UsuarioServicio.esCedulaValida(cedula)) {
                mostrarMensaje("La cédula debe estar entre 5.000.000 y 35.000.000.");
                return;
            }

            
            UsuarioServicio.UsuarioRecord usuario = usuarios.findByCedula(cedula);
            if (usuario == null) {
                mostrarMensaje("No existe un usuario registrado con esa cédula.");
                return;
            }

            
            if (!UsuarioServicio.esTipoEstudiantil(usuario.tipo)) {
                mostrarMensaje("Solo se pueden gestionar estudiantes regulares, becarios o exonerados.");
                return;
            }

            
            String tipoActual = "";
            switch (usuario.tipo) {
                case "E": tipoActual = "Regular"; break;
                case "B": tipoActual = "Becario (" + usuario.porcentajeEspecial + "%)"; break;
                case "X": tipoActual = "Exonerado"; break;
            }

            int confirmar = JOptionPane.showConfirmDialog(vista,
                    "Estudiante: " + cedula + "\n" +
                    "Tipo actual: " + tipoActual + "\n\n" +
                    "¿Desea cambiar su tipo?",
                    "Confirmar gestión",
                    JOptionPane.YES_NO_OPTION);
            
            if (confirmar != JOptionPane.YES_OPTION) return;

            
            String[] opciones = {"Regular", "Becario", "Exonerado"};
            String seleccion = (String) JOptionPane.showInputDialog(vista,
                    "Seleccione el tipo que desea asignar:",
                    "Tipo de beneficio",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);
            if (seleccion == null) return;

            String nuevoTipo = "E";
            double porcentaje = 100.0;

            
            double porcentajeRegular = ccbCalculator.getFactorEstudiante() * 100;

            if ("Becario".equals(seleccion)) {
                
                String porcentajeRaw = JOptionPane.showInputDialog(vista,
                        "Indique el porcentaje de cobro para el becario (debe ser menor a " + 
                        String.format("%.0f", porcentajeRegular) + "%):\n" +
                        "Ejemplo: 5 para 5%",
                        usuario.porcentajeEspecial > 0 && usuario.porcentajeEspecial < porcentajeRegular
                                ? String.valueOf(usuario.porcentajeEspecial)
                                : "5");
                if (porcentajeRaw == null) return;
                porcentajeRaw = porcentajeRaw.trim();
                
                try {
                    porcentaje = Double.parseDouble(porcentajeRaw);
                } catch (Exception ex) {
                    mostrarMensaje("El porcentaje indicado no es válido.");
                    return;
                }
                
                
                if (porcentaje <= 0 || porcentaje >= porcentajeRegular) {
                    mostrarMensaje("El porcentaje del becario debe ser mayor que 0 y menor que " + 
                            String.format("%.0f", porcentajeRegular) + "%.");
                    return;
                }
                nuevoTipo = "B";
            } else if ("Exonerado".equals(seleccion)) {
                nuevoTipo = "X";
                porcentaje = 0.0;
            } else {
                
                nuevoTipo = "E";
                porcentaje = 100.0;
            }

            
            boolean ok = usuarios.actualizarBeneficioEstudiante(cedula, nuevoTipo, porcentaje);
            if (!ok) {
                mostrarMensaje("No se pudo actualizar el beneficio del estudiante.");
                return;
            }

            
            if ("E".equals(nuevoTipo)) {
                mostrarMensaje("El estudiante " + cedula + " quedó como regular nuevamente.");
            } else if ("B".equals(nuevoTipo)) {
                mostrarMensaje("Estudiante " + cedula + " marcado como becario con cobro de " + 
                        porcentaje + "% (menor al " + String.format("%.0f", porcentajeRegular) + "% de regulares).");
            } else {
                mostrarMensaje("Estudiante " + cedula + " marcado como exonerado (paga 0%).");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMensaje("No se pudo gestionar el beneficio del estudiante.");
        }
    }

    
    
    private void mostrarReporteAsistencias() {
        String[] servicios = {"Desayuno", "Almuerzo"};
        String servicioElegido = (String) JOptionPane.showInputDialog(vista,
                "Seleccione el servicio a consultar:",
                "Reporte de asistencias",
                JOptionPane.PLAIN_MESSAGE,
                null,
                servicios,
                servicios[0]);
        if (servicioElegido == null) return;

        List<AsistenciaServicio.RegistroAsistencia> registros = asistencias.listarPorServicio(servicioElegido);
        Map<String, Integer> conteo = asistencias.contarPorTipo(servicioElegido);

        
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║         REPORTE DE ASISTENCIAS - ").append(String.format("%-8s", servicioElegido.toUpperCase())).append("              ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║ Total de asistencias: ").append(String.format("%-34d", registros.size())).append("║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        
        int regulares = conteo.getOrDefault("Regular", 0);
        int becarios = conteo.getOrDefault("Becario", 0);
        int exonerados = conteo.getOrDefault("Exonerado", 0);
        int otros = conteo.getOrDefault("Otros", 0);
        
        sb.append("║ ESTUDIANTES:                                          ║\n");
        sb.append("║   • Regulares : ").append(String.format("%-38d", regulares)).append("║\n");
        sb.append("║   • Becarios  : ").append(String.format("%-38d", becarios)).append("║\n");
        sb.append("║   • Exonerados: ").append(String.format("%-38d", exonerados)).append("║\n");
        
        if (otros > 0) {
            sb.append("║   • Otros     : ").append(String.format("%-38d", otros)).append("║\n");
        }
        
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        
        if (registros.size() > 0) {
            double pctReg = (regulares * 100.0) / registros.size();
            double pctBec = (becarios * 100.0) / registros.size();
            double pctExo = (exonerados * 100.0) / registros.size();
            
            sb.append("║ PORCENTAJES:                                          ║\n");
            sb.append("║   • Regulares : ").append(String.format("%5.1f%% %34s", pctReg, "")).append("║\n");
            sb.append("║   • Becarios  : ").append(String.format("%5.1f%% %34s", pctBec, "")).append("║\n");
            sb.append("║   • Exonerados: ").append(String.format("%5.1f%% %34s", pctExo, "")).append("║\n");
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
        }
        
        sb.append("║ DETALLE DE ASISTENCIAS:                                 ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");

        if (registros.isEmpty()) {
            sb.append("║   No hay asistencias registradas para este servicio.   ║\n");
        } else {
            for (AsistenciaServicio.RegistroAsistencia reg : registros) {
                String tipoAbreviado = "";
                if ("Regular".equals(reg.tipo)) tipoAbreviado = "R";
                else if ("Becario".equals(reg.tipo)) tipoAbreviado = "B";
                else if ("Exonerado".equals(reg.tipo)) tipoAbreviado = "X";
                else tipoAbreviado = "O";
                
                String linea = String.format("  %s | %-9s | %s | %s",
                        tipoAbreviado,
                        reg.cedula,
                        reg.tipo,
                        reg.fechaHora);
                
                
                if (linea.length() > 55) {
                    linea = linea.substring(0, 52) + "...";
                }
                
                sb.append("║ ").append(String.format("%-52s", linea)).append(" ║\n");
            }
        }
        
        sb.append("╚══════════════════════════════════════════════════════════╝");

        
        JTextArea area = new JTextArea(sb.toString(), 25, 65);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(750, 500));
        
        JOptionPane.showMessageDialog(vista, scroll, "Reporte de asistencias - " + servicioElegido, 
                JOptionPane.INFORMATION_MESSAGE);
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

        Object[] datos = new Object[5];
        for (int i = 0; i < 5; i++) {
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

        return String.format("%s|%s|%s|%s|%s",
                tipo, dia, concepto, monto, periodo);
    }

    private String construirPeriodo(String tipo) {
        String cantidad = vista.txtCantidadPeriodo.getText().trim();
        String unidad = vista.comboUnidadPeriodo.getSelectedItem().toString();
        if (cantidad.isEmpty()) {
            mostrarMensaje("Por favor especifica el período");
            return null;
        }
        if (!validarNumero(cantidad, "La cantidad del período debe ser un número")) {
            return null;
        }
        return cantidad + " " + unidad;
    }

    private void limpiar() {
        vista.txtConcepto.setText("");
        vista.txtMonto.setText("");
        vista.txtCantidadPeriodo.setText("");
        vista.comboTipo.setSelectedIndex(0);
        vista.comboDia.setSelectedIndex(0);
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
}