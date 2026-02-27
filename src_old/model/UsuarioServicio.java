package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioServicio {

    private final File archivo;

    public static class UsuarioRecord {
        public final String cedula;
        public final String clave;
        public final double saldo;
        public final String tipo;
        public final String fotoHash;

        public UsuarioRecord(String cedula, String clave, double saldo, String tipo, String fotoHash) {
            this.cedula = cedula;
            this.clave = clave;
            this.saldo = saldo;
            this.tipo = tipo;
            this.fotoHash = (fotoHash == null ? "" : fotoHash);
        }
    }

    public UsuarioServicio(String nombreArchivo) {
        this.archivo = new File(nombreArchivo);
    }

    public boolean existeCedula(String cedula) throws IOException {
        return findByCedula(cedula) != null;
    }

    public UsuarioRecord findByCedula(String cedula) throws IOException {
        if (!archivo.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                String raw = line;
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] arr = raw.split(",");
                if (arr.length < 4) continue;
                if (arr[0].trim().equals(cedula)) {
                    double saldo = 0.0;
                    try { saldo = Double.parseDouble(arr[2].trim()); } catch (Exception ignore) {}
                    String tipo = arr[3].trim();
                    String fotoHash = (arr.length >= 5 ? arr[4].trim() : "");
                    return new UsuarioRecord(arr[0].trim(), arr[1], saldo, tipo, fotoHash);
                }
            }
        }
        return null;
    }

    public UsuarioRecord autenticar(String cedula, String clave) throws IOException {
        UsuarioRecord u = findByCedula(cedula);
        if (u == null) return null;
        if (u.clave.equals(clave)) return u;
        return new UsuarioRecord(u.cedula, u.clave, u.saldo, "", u.fotoHash);
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo) throws IOException {
        registrar(cedula, clave, saldoInicial, tipo, "");
    }

    public void registrar(String cedula, String clave, double saldoInicial, String tipo, String fotoHash) throws IOException {
        if (archivo.getParentFile() != null) archivo.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(cedula + "," + clave + "," + saldoInicial + "," + tipo + "," + (fotoHash == null ? "" : fotoHash));
            bw.newLine();
        }
    }

    public boolean actualizarSaldo(String cedula, double nuevoSaldo) throws IOException {
        List<String> lineas = new ArrayList<>();
        boolean actualizado = false;

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String raw = line;
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] arr = raw.split(",");
                    if (arr.length >= 4 && arr[0].trim().equals(cedula)) {
                        String clave = arr[1];
                        String tipo = arr[3].trim();
                        String fotoHash = (arr.length >= 5 ? arr[4].trim() : "");
                        lineas.add(cedula + "," + clave + "," + nuevoSaldo + "," + tipo + "," + fotoHash);
                        actualizado = true;
                    } else {
                        if (arr.length >= 4 && arr.length < 5) {
                            lineas.add(arr[0].trim() + "," + arr[1] + "," + arr[2].trim() + "," + arr[3].trim() + ",");
                        } else {
                            lineas.add(raw);
                        }
                    }
                }
            }
        }

        if (!actualizado) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
        return true;
    }

    public boolean actualizarFotoHash(String cedula, String nuevoHash) throws IOException {
        List<String> lineas = new ArrayList<>();
        boolean actualizado = false;

        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String raw = line;
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] arr = raw.split(",");
                    if (arr.length >= 4 && arr[0].trim().equals(cedula)) {
                        String clave = arr[1];
                        String saldo = arr[2].trim();
                        String tipo = arr[3].trim();
                        lineas.add(cedula + "," + clave + "," + saldo + "," + tipo + "," + (nuevoHash == null ? "" : nuevoHash));
                        actualizado = true;
                    } else {
                        if (arr.length >= 4 && arr.length < 5) {
                            lineas.add(arr[0].trim() + "," + arr[1] + "," + arr[2].trim() + "," + arr[3].trim() + ",");
                        } else {
                            lineas.add(raw);
                        }
                    }
                }
            }
        }

        if (!actualizado) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        }
        return true;
    }
}
