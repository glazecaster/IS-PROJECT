package admin.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CCBConfigService {

    private final File archivo;

    public CCBConfigService() {
        this("data/ccb_config.txt");
    }

    public CCBConfigService(String ruta) {
        this.archivo = new File(ruta);
    }

    public void cargarEn(CCBCalculator calculator) {
        if (calculator == null || !archivo.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                return;
            }
            String[] partes = line.split(",", -1);
            if (partes.length < 6) {
                return;
            }
            calculator.setMerma(parse(partes[0], calculator.getMerma()));
            calculator.setFactorEstudiante(parse(partes[1], calculator.getFactorEstudiante()));
            calculator.setFactorProfesor(parse(partes[2], calculator.getFactorProfesor()));
            calculator.setFactorAdmin(parse(partes[3], calculator.getFactorAdmin()));
            calculator.setFactorBecario(parse(partes[4], calculator.getFactorBecario()));
            calculator.setFactorExonerado(parse(partes[5], calculator.getFactorExonerado()));
        } catch (Exception ignored) {
        }
    }

    public void guardar(CCBCalculator calculator) {
        if (calculator == null) {
            return;
        }
        try {
            File padre = archivo.getParentFile();
            if (padre != null && !padre.exists()) {
                padre.mkdirs();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
                bw.write(calculator.getMerma() + "," + calculator.getFactorEstudiante() + "," + calculator.getFactorProfesor() + "," + calculator.getFactorAdmin() + "," + calculator.getFactorBecario() + "," + calculator.getFactorExonerado());
                bw.newLine();
            }
        } catch (IOException ignored) {
        }
    }

    private double parse(String valor, double fallback) {
        try {
            return Double.parseDouble(valor.trim());
        } catch (Exception ex) {
            return fallback;
        }
    }
}
