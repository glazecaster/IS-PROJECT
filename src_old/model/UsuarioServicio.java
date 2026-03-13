package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UsuarioServicio {

    private static final long CEDULA_MIN = 5000000L;
    private static final long CEDULA_MAX = 35000000L;
    private static final int REFERENCIA_DIGITOS = 10;
    private final File archivo;
    private final Random random = new Random();

    public static class UsuarioRecord {
        public final String cedula;
        public final String clave;
        public final double saldo;
        public final String tipo;
        public final String numeroReferencia;
        public final double porcentajeEspecial;
        public final String fotoPath;

        public UsuarioRecord(String cedula, String clave, double saldo, String tipo) {
            this(cedula, clave, saldo, tipo, "", porcentajePorDefecto(tipo), "");
        }

        public UsuarioRecord(String cedula, String clave, double saldo, String tipo, String numeroReferencia) {
            this(cedula, clave, saldo, tipo, numeroReferencia, porcentajePorDefecto(tipo), "");
        }

        public UsuarioRecord(String cedula, String clave, double saldo, String tipo, String numeroReferencia, double porcentajeEspecial) {
            this(cedula, clave, saldo, tipo, numeroReferencia, porcentajeEspecial, "");
        }

        public UsuarioRecord(String cedula, String clave, double saldo, String tipo, String numeroReferencia,
                double porcentajeEspecial, String fotoPath) {
            this.cedula = cedula;
            this.clave = clave;
            this.saldo = saldo;
            this.tipo = tipo;
            this.numeroReferencia = numeroReferencia == null ? "" : numeroReferencia.trim();
            this.porcentajeEspecial = porcentajeEspecial;
            this.fotoPath = fotoPath == null ? "" : fotoPath.trim();
        }
    }

    public UsuarioServicio(String nombreArchivo) {
        this.archivo = new File(nombreArchivo);
    }

    public boolean existeCedula(String cedula) throws IOException {
        return findByCedula(cedula) != null;
    }

    public boolean existeNumeroReferencia(String numeroReferencia) throws IOException {
        if (!esNumeroReferenciaValido(numeroReferencia) || !archivo.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                UsuarioRecord record = parseRecord(line);
                if (record != null && numeroReferencia.trim().equals(record.numeroReferencia)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<UsuarioRecord> listarUsuarios() throws IOException {
        List<UsuarioRecord> usuarios = new ArrayList<>();
        if (!archivo.exists()) return usuarios;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                UsuarioRecord record = parseRecord(line);
                if (record != null) {
                    usuarios.add(record);
                }
            }
        }
        return usuarios;
    }

    public UsuarioRecord findByCedula(String cedula) throws IOException {
        if (!archivo.exists()) return null;

        String referenciaMigrada = null;
        double porcentajeMigrado = -1;
        UsuarioRecord encontrado = null;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                UsuarioRecord record = parseRecord(line);
                if (record == null) continue;
                if (record.cedula.equals(cedula)) {
                    encontrado = record;
                    if (!esNumeroReferenciaValido(record.numeroReferencia)) {
                        referenciaMigrada = generarNumeroReferenciaDeterminista(record.cedula);
                        porcentajeMigrado = porcentajePorDefecto(record.tipo);
                        encontrado = new UsuarioRecord(record.cedula, record.clave, record.saldo, record.tipo,
                                referenciaMigrada, porcentajeMigrado, record.fotoPath);
                    }
                    break;
                }
            }
        }

        if (encontrado != null && referenciaMigrada != null) {
            actualizarRegistroCompleto(encontrado.cedula, encontrado.clave, encontrado.saldo, encontrado.tipo,
                    referenciaMigrada, porcentajeMigrado, encontrado.fotoPath);
        }

        return encontrado;
    }

    public UsuarioRecord autenticar(String cedula, String clave) throws IOException {
        UsuarioRecord u = findByCedula(cedula);
        if (u == null) return null;
        if (u.clave.equals(clave)) return u;
        return new UsuarioRecord(u.cedula, u.clave, u.saldo, "", u.numeroReferencia, u.porcentajeEspecial, u.fotoPath);
    }

    public UsuarioRecord autenticar(String cedula, String clave, String numeroReferencia) throws IOException {
        UsuarioRecord u = findByCedula(cedula);
        if (u == null) return null;
        if (u.clave.equals(clave) && u.numeroReferencia.equals(numeroReferencia == null ? "" : numeroReferencia.trim())) {
            return u;
        }
        return new UsuarioRecord(u.cedula, u.clave, u.saldo, "", u.numeroReferencia, u.porcentajeEspecial, u.fotoPath);
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo) throws IOException {
        registrar(cedula, clave, saldoInicial, tipo, generarNumeroReferenciaUnico(), porcentajePorDefecto(tipo), "");
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo, String numeroReferencia) throws IOException {
        registrar(cedula, clave, saldoInicial, tipo, numeroReferencia, porcentajePorDefecto(tipo), "");
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo, String numeroReferencia,
            double porcentajeEspecial) throws IOException {
        registrar(cedula, clave, saldoInicial, tipo, numeroReferencia, porcentajeEspecial, "");
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo, String numeroReferencia,
            double porcentajeEspecial, String fotoPath) throws IOException {
        if (archivo.getParentFile() != null) archivo.getParentFile().mkdirs();
        String referenciaFinal = esNumeroReferenciaValido(numeroReferencia) ? numeroReferencia.trim() : generarNumeroReferenciaUnico();
        double porcentajeFinal = normalizarPorcentaje(tipo, porcentajeEspecial);
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(construirLinea(cedula, clave, saldoInicial, tipo, referenciaFinal, porcentajeFinal, fotoPath));
            bw.newLine();
        }
    }

    public boolean actualizarSaldo(String cedula, double nuevoSaldo) throws IOException {
        UsuarioRecord actual = findByCedula(cedula);
        if (actual == null) return false;
        return actualizarRegistroCompleto(actual.cedula, actual.clave, nuevoSaldo, actual.tipo,
                actual.numeroReferencia, actual.porcentajeEspecial, actual.fotoPath);
    }

    public boolean actualizarBeneficioEstudiante(String cedula, String nuevoTipo, double porcentajeEspecial) throws IOException {
        UsuarioRecord actual = findByCedula(cedula);
        if (actual == null) return false;
        if (!esTipoEstudiantil(actual.tipo)) return false;
        return actualizarRegistroCompleto(actual.cedula, actual.clave, actual.saldo, nuevoTipo,
                actual.numeroReferencia, normalizarPorcentaje(nuevoTipo, porcentajeEspecial), actual.fotoPath);
    }

    public boolean actualizarNumeroReferencia(String cedula, String numeroReferencia) throws IOException {
        UsuarioRecord actual = findByCedula(cedula);
        if (actual == null || !esNumeroReferenciaValido(numeroReferencia)) return false;
        return actualizarRegistroCompleto(actual.cedula, actual.clave, actual.saldo, actual.tipo,
                numeroReferencia.trim(), actual.porcentajeEspecial, actual.fotoPath);
    }

    public boolean actualizarFoto(String cedula, String fotoPath) throws IOException {
        UsuarioRecord actual = findByCedula(cedula);
        if (actual == null) return false;
        return actualizarRegistroCompleto(actual.cedula, actual.clave, actual.saldo, actual.tipo,
                actual.numeroReferencia, actual.porcentajeEspecial, fotoPath);
    }

    public String generarNumeroReferenciaUnico() throws IOException {
        String referencia;
        do {
            StringBuilder sb = new StringBuilder();
            int primero = random.nextInt(9) + 1;
            sb.append(primero);
            for (int i = 1; i < REFERENCIA_DIGITOS; i++) {
                sb.append(random.nextInt(10));
            }
            referencia = sb.toString();
        } while (existeNumeroReferencia(referencia));
        return referencia;
    }

    private boolean actualizarRegistroCompleto(String cedula, String clave, double saldo, String tipo,
            String numeroReferencia, double porcentajeEspecial, String fotoPath) throws IOException {
        List<String> lineas = new ArrayList<>();
        boolean actualizado = false;

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    UsuarioRecord record = parseRecord(line);
                    if (record == null) continue;
                    if (record.cedula.equals(cedula)) {
                        lineas.add(construirLinea(cedula, clave, saldo, tipo, numeroReferencia,
                                normalizarPorcentaje(tipo, porcentajeEspecial), fotoPath));
                        actualizado = true;
                    } else {
                        lineas.add(construirLinea(record.cedula, record.clave, record.saldo, record.tipo,
                                record.numeroReferencia, record.porcentajeEspecial, record.fotoPath));
                    }
                }
            }
        }

        if (!actualizado) return false;

        escribirTodas(lineas);
        return true;
    }

    private void escribirTodas(List<String> lineas) throws IOException {
        if (archivo.getParentFile() != null) archivo.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
    }

    private String construirLinea(String cedula, String clave, double saldo, String tipo,
            String numeroReferencia, double porcentajeEspecial, String fotoPath) {
        return cedula + "," + clave + "," + saldo + "," + tipo + "," + numeroReferencia + "," + porcentajeEspecial + "," + limpiarFotoPath(fotoPath);
    }

    private String limpiarFotoPath(String fotoPath) {
        if (fotoPath == null) return "";
        return fotoPath.trim().replace(',', '_');
    }

    private UsuarioRecord parseRecord(String rawLine) {
        if (rawLine == null) return null;
        String line = rawLine.trim();
        if (line.isEmpty()) return null;

        String[] arr = rawLine.split(",", -1);
        if (arr.length < 4) return null;

        String cedula = arr[0].trim();
        String clave = arr[1];
        double saldo = 0.0;
        try {
            saldo = Double.parseDouble(arr[2].trim());
        } catch (Exception ignore) {
        }
        String tipo = arr[3].trim();
        String referencia = arr.length >= 5 ? arr[4].trim() : "";
        double porcentajeEspecial = porcentajePorDefecto(tipo);
        if (arr.length >= 6) {
            try {
                porcentajeEspecial = Double.parseDouble(arr[5].trim());
            } catch (Exception ignore) {
                porcentajeEspecial = porcentajePorDefecto(tipo);
            }
        }
        String fotoPath = arr.length >= 7 ? arr[6].trim() : "";

        return new UsuarioRecord(cedula, clave, saldo, tipo, referencia, porcentajeEspecial, fotoPath);
    }

    private String generarNumeroReferenciaDeterminista(String cedula) {
        long base = 1000000000L + Math.abs((cedula == null ? "0" : cedula).hashCode());
        String referencia = String.valueOf(base);
        if (referencia.length() > REFERENCIA_DIGITOS) {
            referencia = referencia.substring(0, REFERENCIA_DIGITOS);
        }
        while (referencia.length() < REFERENCIA_DIGITOS) {
            referencia = "0" + referencia;
        }
        if (referencia.startsWith("0")) {
            referencia = "1" + referencia.substring(1);
        }
        return referencia;
    }

    private static double normalizarPorcentaje(String tipo, double porcentajeEspecial) {
        String t = tipo == null ? "" : tipo.trim().toUpperCase();
        if ("X".equals(t)) return 0.0;
        if ("B".equals(t)) {
            double valor = porcentajeEspecial <= 0 ? 5.0 : porcentajeEspecial;
            if (valor >= 100.0) return 99.0;
            return valor;
        }
        return 100.0;
    }

    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || !cedula.matches("\\d+")) return false;
        try {
            long valor = Long.parseLong(cedula);
            return valor >= CEDULA_MIN && valor <= CEDULA_MAX;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esNumeroReferenciaValido(String numeroReferencia) {
        return numeroReferencia != null && numeroReferencia.trim().matches("\\d{" + REFERENCIA_DIGITOS + "}");
    }

    public static boolean esTipoEstudiantil(String tipo) {
        if (tipo == null) return false;
        String t = tipo.trim().toUpperCase();
        return "E".equals(t) || "B".equals(t) || "X".equals(t);
    }

    public static double porcentajePorDefecto(String tipo) {
        if (tipo == null) return 100.0;
        String t = tipo.trim().toUpperCase();
        if ("B".equals(t)) return 5.0;
        if ("X".equals(t)) return 0.0;
        return 100.0;
    }

    public static String descripcionTipoParaReporte(String tipo) {
        if (tipo == null) return "Otros";
        String t = tipo.trim().toUpperCase();
        if ("B".equals(t)) return "Becario";
        if ("X".equals(t)) return "Exonerado";
        if ("E".equals(t)) return "Regular";
        if ("P".equals(t)) return "Profesor";
        if ("T".equals(t)) return "Empleado";
        return "Otros";
    }

    public static double calcularCargoAcceso(UsuarioRecord usuario, double tarifaBase) {
        if (usuario == null) return tarifaBase;
        String tipo = usuario.tipo == null ? "" : usuario.tipo.trim().toUpperCase();
        if ("X".equals(tipo)) return 0.0;
        if ("B".equals(tipo)) return redondear2(tarifaBase * (usuario.porcentajeEspecial / 100.0));
        return tarifaBase;
    }

    private static double redondear2(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
