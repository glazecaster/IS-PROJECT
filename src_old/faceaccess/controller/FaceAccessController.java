package faceaccess.controller;

import admin.model.CCBCalculator;
import admin.model.CCBConfigService;
import admin.model.Menu;
import admin.model.MenuSemana;
import admin.model.MenuServicio;
import faceaccess.view.FaceAccessView;
import model.AsistenciaServicio;
import model.Monedero;
import model.ReconocimientoFacialServicio;
import model.TarifaServicio;
import model.UsuarioServicio;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FaceAccessController {

    private static class OpcionCobro {
        private final String etiqueta;
        private final String servicio;
        private final String dia;
        private final double tarifa;
        private final String detalle;
        private final String descripcionCorta;
        private final boolean bandejaCompleta;

        private OpcionCobro(String etiqueta, String servicio, String dia, double tarifa, String detalle, String descripcionCorta, boolean bandejaCompleta) {
            this.etiqueta = etiqueta;
            this.servicio = servicio;
            this.dia = dia;
            this.tarifa = tarifa;
            this.detalle = detalle;
            this.descripcionCorta = descripcionCorta;
            this.bandejaCompleta = bandejaCompleta;
        }
    }

    private final FaceAccessView view;
    private final UsuarioServicio usuarios;
    private final ReconocimientoFacialServicio reconocimiento;
    private final AsistenciaServicio asistenciaServicio;
    private final CCBCalculator ccbCalculator;
    private final MenuServicio menuServicio;
    private final MenuSemana menuSemana;
    private final CCBConfigService configService;
    private final List<OpcionCobro> opcionesDisponibles = new ArrayList<>();

    private UsuarioServicio.UsuarioRecord usuarioActual;
    private OpcionCobro opcionActual;

    public FaceAccessController(FaceAccessView view) {
        this.view = view;
        this.usuarios = new UsuarioServicio("data/usuarios.txt");
        this.reconocimiento = new ReconocimientoFacialServicio(usuarios);
        this.asistenciaServicio = new AsistenciaServicio();
        this.ccbCalculator = new CCBCalculator();
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        this.configService = new CCBConfigService();
    }

    public void init() {
        view.setOnEscanear(e -> escanearRostro());
        view.setOnAutorizar(e -> autorizarAccesoYCobrar());
        view.setOnSeleccionCambio(e -> recargarOpciones(false));
        view.setOnMenuCambio(e -> actualizarResumenSeleccion());
        view.setOnSalir(() -> view.dispose());
        cargarConfiguracionCCB();
        recargarOpciones(true);
    }

    public void solicitarEscaneoInicial() {
        escanearRostro();
    }

    private void escanearRostro() {
        try {
            File archivo = view.solicitarArchivoImagen();
            if (archivo == null) return;

            BufferedImage imagenEscaneada = reconocimiento.leerImagenValida(archivo);
            view.setImagenEscaneada(imagenEscaneada);

            ReconocimientoFacialServicio.ResultadoReconocimiento resultado = reconocimiento.reconocer(archivo);
            if (resultado == null || resultado.usuario == null) {
                usuarioActual = null;
                opcionActual = null;
                view.setImagenRegistrada(null);
                view.actualizarUsuario("—", "No reconocido", "$0.00");
                view.actualizarEstadoEscaneo("No se encontró coincidencia facial válida para la imagen escaneada.", new Color(0xA32020));
                view.actualizarCobro("$" + formatMoney(ccbCalculator.calcularCCB().getCcbConMerma()), "$0.00", "No se pudo reconocer al usuario. Escanea una foto válida para continuar.");
                view.setAccionHabilitada(false);
                recargarOpciones(true);
                return;
            }

            usuarioActual = usuarios.findByCedula(resultado.usuario.cedula);
            if (usuarioActual == null) {
                view.mostrarMensaje("El usuario reconocido no pudo recargarse desde usuarios.txt.");
                return;
            }

            BufferedImage imagenRegistrada = reconocimiento.leerImagenValida(resultado.fotoRegistrada);
            view.setImagenRegistrada(imagenRegistrada);
            view.actualizarUsuario(
                    usuarioActual.cedula,
                    UsuarioServicio.descripcionTipoParaReporte(usuarioActual.tipo),
                    "$" + formatMoney(usuarioActual.saldo)
            );
            view.actualizarEstadoEscaneo("Usuario reconocido correctamente. Selecciona el menú o plato y autoriza el cobro.", new Color(0x156F39));
            recargarOpciones(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            usuarioActual = null;
            opcionActual = null;
            view.setAccionHabilitada(false);
            view.actualizarEstadoEscaneo("Ocurrió un error al procesar la imagen del escaneo.", new Color(0xA32020));
            view.mostrarMensaje("No se pudo procesar la foto seleccionada.");
        }
    }

    private void recargarOpciones(boolean resetSelection) {
        try {
            cargarConfiguracionCCB();
            String servicio = view.getServicioSeleccionado();
            String dia = view.getDiaSeleccionado();
            String codigoTipo = usuarioActual == null ? "E" : usuarioActual.tipo;
            double porcentajeEspecial = usuarioActual == null ? UsuarioServicio.porcentajePorDefecto(codigoTipo) : usuarioActual.porcentajeEspecial;

            opcionesDisponibles.clear();
            opcionesDisponibles.addAll(construirOpciones(servicio, dia, codigoTipo, porcentajeEspecial));
            view.setOpcionesMenu(extraerEtiquetas(opcionesDisponibles), resetSelection);
            actualizarResumenSeleccion();
        } catch (Exception ex) {
            ex.printStackTrace();
            opcionesDisponibles.clear();
            opcionActual = null;
            view.setOpcionesMenu(new String[]{"Sin opciones disponibles"}, true);
            view.actualizarCobro("$0.00", "$0.00", "No se pudieron cargar los menús o platos para la selección actual.");
            view.setAccionHabilitada(false);
        }
    }

    private void actualizarResumenSeleccion() {
        double ccbActual = ccbCalculator.calcularCCB().getCcbConMerma();
        int indice = view.getIndiceMenuSeleccionado();

        if (indice < 0 || indice >= opcionesDisponibles.size()) {
            opcionActual = null;
            String detalle = opcionesDisponibles.isEmpty()
                    ? "No hay menús o platos disponibles para el día y servicio seleccionados.\n\nAsegúrate de que el administrador haya cargado previamente el menú semanal."
                    : "Selecciona una opción para ver el detalle del cobro.";
            view.actualizarCobro("$" + formatMoney(ccbActual), "$0.00", detalle);
            view.setAccionHabilitada(false);
            return;
        }

        opcionActual = opcionesDisponibles.get(indice);
        view.actualizarCobro("$" + formatMoney(ccbActual), "$" + formatMoney(opcionActual.tarifa), opcionActual.detalle);
        view.setAccionHabilitada(usuarioActual != null && opcionActual.tarifa >= 0);
    }

    private List<OpcionCobro> construirOpciones(String servicio, String dia, String codigoTipo, double porcentajeEspecial) {
        List<OpcionCobro> opciones = construirOpcionesSemana(servicio, dia, codigoTipo, porcentajeEspecial);
        if (!opciones.isEmpty()) {
            return opciones;
        }
        return construirOpcionesBase(servicio, dia, codigoTipo, porcentajeEspecial);
    }

    private List<OpcionCobro> construirOpcionesSemana(String servicio, String dia, String codigoTipo, double porcentajeEspecial) {
        List<OpcionCobro> opciones = new ArrayList<>();
        List<Menu> componentes = new ArrayList<>();
        List<Double> tarifas = new ArrayList<>();
        List<String> nombresComponentes = new ArrayList<>();

        String[] componentesIds;
        String[] etiquetas;
        if ("Desayuno".equalsIgnoreCase(servicio)) {
            componentesIds = new String[]{MenuSemana.COMP_PLATO_PRINCIPAL, MenuSemana.COMP_ACOMPANANTE, MenuSemana.COMP_BEBIDA};
            etiquetas = new String[]{"Plato principal", "Acompañante", "Bebida"};
        } else {
            componentesIds = new String[]{MenuSemana.COMP_PLATO_PRINCIPAL, MenuSemana.COMP_ACOMPANANTE, MenuSemana.COMP_ENSALADA, MenuSemana.COMP_BEBIDA};
            etiquetas = new String[]{"Plato principal", "Acompañante", "Ensalada", "Bebida"};
        }

        for (int i = 0; i < componentesIds.length; i++) {
            Integer idMenu = obtenerMenuSemana(servicio, dia, componentesIds[i]);
            if (idMenu == null || idMenu <= 0) continue;
            Menu menu = menuServicio.getMenuPorId(idMenu);
            if (menu == null) continue;
            double tarifa = calcularTarifaMenu(idMenu, codigoTipo, porcentajeEspecial);
            componentes.add(menu);
            tarifas.add(tarifa);
            nombresComponentes.add(etiquetas[i]);
            opciones.add(crearOpcionIndividualSemana(servicio, dia, menu, tarifas.get(tarifas.size() - 1), etiquetas[i]));
        }

        if (componentes.isEmpty()) {
            return opciones;
        }

        double total = 0.0;
        StringBuilder detalle = new StringBuilder();
        detalle.append("Origen: Menú semanal configurado por el administrador\n");
        detalle.append("Servicio: ").append(servicio).append("\n");
        detalle.append("Día: ").append(dia).append("\n\n");
        detalle.append("Opciones incluidas en la bandeja:\n");
        for (int i = 0; i < componentes.size(); i++) {
            Menu menu = componentes.get(i);
            double tarifa = tarifas.get(i);
            total += tarifa;
            detalle.append(i + 1).append(") ").append(nombresComponentes.get(i)).append(": ").append(menu.getNombre()).append("\n");
            detalle.append("   ").append(menu.getDescripcion()).append("\n");
            detalle.append("   Tarifa: $").append(formatMoney(tarifa)).append("\n");
        }
        detalle.append("\nTotal a debitar por la bandeja completa: $").append(formatMoney(total));

        opciones.add(0, new OpcionCobro(
                "Bandeja completa • $" + formatMoney(total),
                servicio,
                dia,
                redondear2(total),
                detalle.toString(),
                "Bandeja completa",
                true
        ));

        return opciones;
    }

    private OpcionCobro crearOpcionIndividualSemana(String servicio, String dia, Menu menu, double tarifa, String nombreComponente) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("Origen: Menú semanal configurado por el administrador\n");
        detalle.append("Servicio: ").append(servicio).append("\n");
        detalle.append("Día: ").append(dia).append("\n");
        detalle.append("Componente: ").append(nombreComponente).append("\n\n");
        detalle.append("Nombre: ").append(menu.getNombre()).append("\n");
        detalle.append("Tipo: ").append(menu.getTipo()).append("\n");
        detalle.append("Descripción: ").append(menu.getDescripcion()).append("\n");
        detalle.append("Proteínas: ").append(menu.getProteinas()).append("\n");
        detalle.append("Carbohidratos: ").append(menu.getCarbohidratos()).append("\n");
        detalle.append("Calorías: ").append(menu.getCalorias()).append("\n\n");
        detalle.append("Tarifa a debitar: $").append(formatMoney(tarifa));
        return new OpcionCobro(
                nombreComponente + ": " + menu.getNombre() + " • $" + formatMoney(tarifa),
                servicio,
                dia,
                redondear2(tarifa),
                detalle.toString(),
                menu.getNombre(),
                false
        );
    }

    private List<OpcionCobro> construirOpcionesBase(String servicio, String dia, String codigoTipo, double porcentajeEspecial) {
        List<OpcionCobro> opciones = new ArrayList<>();
        List<Menu> menusDia = menuServicio.getMenusPorDiaYServicio(dia, servicio);
        for (Menu menu : menusDia) {
            double tarifa = calcularTarifaMenu(menu.getId(), codigoTipo, porcentajeEspecial);
            StringBuilder detalle = new StringBuilder();
            detalle.append("Origen: Base general de menús de ComeUCV\n");
            detalle.append("Servicio: ").append(servicio).append("\n");
            detalle.append("Día: ").append(dia).append("\n\n");
            detalle.append("Nombre: ").append(menu.getNombre()).append("\n");
            detalle.append("Tipo: ").append(menu.getTipo()).append("\n");
            detalle.append("Descripción: ").append(menu.getDescripcion()).append("\n");
            detalle.append("Proteínas: ").append(menu.getProteinas()).append("\n");
            detalle.append("Carbohidratos: ").append(menu.getCarbohidratos()).append("\n");
            detalle.append("Calorías: ").append(menu.getCalorias()).append("\n\n");
            detalle.append("Tarifa a debitar: $").append(formatMoney(tarifa));
            opciones.add(new OpcionCobro(
                    menu.getNombre() + " • $" + formatMoney(tarifa),
                    servicio,
                    dia,
                    tarifa,
                    detalle.toString(),
                    menu.getNombre(),
                    false
            ));
        }
        return opciones;
    }

    private Integer obtenerMenuSemana(String servicio, String dia, String componente) {
        if ("Desayuno".equalsIgnoreCase(servicio)) {
            return menuSemana.getDesayuno(dia, componente);
        }
        return menuSemana.getAlmuerzo(dia, componente);
    }

    private double calcularTarifaMenu(int menuId, String codigoTipo, double porcentajeEspecial) {
        String tipoUsuario = TarifaServicio.mapTipoUsuario(codigoTipo);
        if ("Exonerado".equals(tipoUsuario)) {
            return 0.0;
        }
        if ("Becario".equals(tipoUsuario)) {
            double tarifaRegular = ccbCalculator.calcularPrecioMenuCompleto(menuId, "Estudiante");
            double porcentaje = porcentajeEspecial <= 0 ? 5.0 : porcentajeEspecial;
            return redondear2(tarifaRegular * (porcentaje / 100.0));
        }
        return redondear2(ccbCalculator.calcularPrecioMenuCompleto(menuId, tipoUsuario));
    }

    private String[] extraerEtiquetas(List<OpcionCobro> opciones) {
        if (opciones.isEmpty()) {
            return new String[]{"Sin opciones disponibles"};
        }
        String[] etiquetas = new String[opciones.size()];
        for (int i = 0; i < opciones.size(); i++) {
            etiquetas[i] = opciones.get(i).etiqueta;
        }
        return etiquetas;
    }

    private void autorizarAccesoYCobrar() {
        if (usuarioActual == null) {
            view.mostrarMensaje("Primero debes reconocer al usuario mediante el escaneo facial.");
            return;
        }
        if (opcionActual == null || opcionActual.tarifa < 0) {
            view.mostrarMensaje("No hay un menú o plato válido seleccionado para cobrar.");
            return;
        }

        try {
            UsuarioServicio.UsuarioRecord usuarioRefrescado = usuarios.findByCedula(usuarioActual.cedula);
            if (usuarioRefrescado == null) {
                view.mostrarMensaje("El usuario reconocido ya no existe en la base de datos.");
                return;
            }

            Monedero monedero = new Monedero(usuarioRefrescado.cedula, usuarioRefrescado.saldo);
            double monto = opcionActual.tarifa;
            if (!monedero.cobrar(monto)) {
                view.actualizarEstadoEscaneo("Saldo insuficiente para debitar la opción seleccionada.", new Color(0xA32020));
                view.mostrarMensaje("El usuario no tiene saldo suficiente para pagar esta opción.");
                return;
            }
            boolean actualizado = usuarios.actualizarSaldo(usuarioRefrescado.cedula, monedero.getSaldo());
            if (!actualizado) {
                view.mostrarMensaje("No se pudo actualizar el saldo del usuario en usuarios.txt.");
                return;
            }

            asistenciaServicio.registrarAsistencia(usuarioRefrescado.cedula, usuarioRefrescado.tipo, opcionActual.servicio);
            usuarioActual = usuarios.findByCedula(usuarioRefrescado.cedula);
            if (usuarioActual == null) {
                view.mostrarMensaje("No se pudo recargar el usuario luego del cobro.");
                return;
            }

            view.actualizarUsuario(
                    usuarioActual.cedula,
                    UsuarioServicio.descripcionTipoParaReporte(usuarioActual.tipo),
                    "$" + formatMoney(usuarioActual.saldo)
            );
            view.actualizarEstadoEscaneo("Acceso aprobado. El cobro fue debitado correctamente del monedero.", new Color(0x156F39));
            view.actualizarCobro(
                    "$" + formatMoney(ccbCalculator.calcularCCB().getCcbConMerma()),
                    "$" + formatMoney(monto),
                    construirResumenOperacion(monto)
            );
            recargarOpciones(false);
            view.mostrarMensaje("Acceso aprobado y monedero debitado correctamente.");
        } catch (IOException ex) {
            ex.printStackTrace();
            view.mostrarMensaje("Ocurrió un error al debitar el monedero del usuario.");
        }
    }

    private String construirResumenOperacion(double monto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Operación realizada\n");
        sb.append("Fecha/Hora: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Usuario: ").append(usuarioActual.cedula).append("\n");
        sb.append("Servicio: ").append(opcionActual.servicio).append("\n");
        sb.append("Día del menú: ").append(opcionActual.dia).append("\n");
        sb.append("Selección: ").append(opcionActual.descripcionCorta).append("\n");
        sb.append("Modalidad: ").append(opcionActual.bandejaCompleta ? "Bandeja completa" : "Plato individual").append("\n");
        sb.append("Monto debitado: $").append(formatMoney(monto)).append("\n");
        sb.append("Nuevo saldo: $").append(formatMoney(usuarioActual.saldo)).append("\n\n");
        sb.append("El usuario puede pasar al comedor.");
        return sb.toString();
    }


    private void cargarConfiguracionCCB() {
        configService.cargarEn(ccbCalculator);
    }

    private double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String formatMoney(double valor) {
        return String.format("%.2f", valor);
    }
}
