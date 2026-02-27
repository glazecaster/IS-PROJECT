package admin.model;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuSemana {

    public static final String COMP_PLATO_PRINCIPAL = "PLATO_PRINCIPAL";
    public static final String COMP_ACOMPANANTE = "ACOMPANANTE";
    public static final String COMP_BEBIDA = "BEBIDA";
    public static final String COMP_ENSALADA = "ENSALADA";

    private final Map<String, Map<String, Integer>> desayunos; // día -> (componente -> id)
    private final Map<String, Map<String, Integer>> almuerzos; // día -> (componente -> id)

    private final String nombreArchivo = "data/base_datos_menu_semana.txt";

    public MenuSemana() {
        desayunos = new HashMap<>();
        almuerzos = new HashMap<>();
        cargarSeleccion();
    }

    // -------------------
    // API pública (nuevo)
    // -------------------

    public void setDesayuno(String dia, String componente, int menuId) {
        setSeleccion(desayunos, dia, componente, menuId);
        guardarSeleccion();
    }

    public void setAlmuerzo(String dia, String componente, int menuId) {
        setSeleccion(almuerzos, dia, componente, menuId);
        guardarSeleccion();
    }

    public Integer getDesayuno(String dia, String componente) {
        return getSeleccion(desayunos, dia, componente);
    }

    public Integer getAlmuerzo(String dia, String componente) {
        return getSeleccion(almuerzos, dia, componente);
    }

    public void eliminarDesayuno(String dia, String componente) {
        eliminarSeleccion(desayunos, dia, componente);
        guardarSeleccion();
    }

    public void eliminarAlmuerzo(String dia, String componente) {
        eliminarSeleccion(almuerzos, dia, componente);
        guardarSeleccion();
    }

    public boolean hayDesayunoSeleccionado(String dia, String componente) {
        return getDesayuno(dia, componente) != null;
    }

    public boolean hayAlmuerzoSeleccionado(String dia, String componente) {
        return getAlmuerzo(dia, componente) != null;
    }

    public Map<String, Map<String, Integer>> getDesayunosPorDia() {
        return deepCopy(desayunos);
    }

    public Map<String, Map<String, Integer>> getAlmuerzosPorDia() {
        return deepCopy(almuerzos);
    }

    // -------------------
    // API pública (legado)
    // -------------------
    // Mantener para no romper módulos que aún llamen set/get sin componente.

    public void setDesayuno(String dia, int menuId) {
        setDesayuno(dia, COMP_PLATO_PRINCIPAL, menuId);
    }

    public void setAlmuerzo(String dia, int menuId) {
        setAlmuerzo(dia, COMP_PLATO_PRINCIPAL, menuId);
    }

    public Integer getDesayuno(String dia) {
        return getDesayuno(dia, COMP_PLATO_PRINCIPAL);
    }

    public Integer getAlmuerzo(String dia) {
        return getAlmuerzo(dia, COMP_PLATO_PRINCIPAL);
    }

    public void eliminarDesayuno(String dia) {
        eliminarDesayuno(dia, COMP_PLATO_PRINCIPAL);
    }

    public void eliminarAlmuerzo(String dia) {
        eliminarAlmuerzo(dia, COMP_PLATO_PRINCIPAL);
    }

    /**
     * Devuelve SOLO el principal por día (compatibilidad con vistas viejas).
     */
    public Map<String, Integer> getDesayunos() {
        return getMapaPrincipal(desayunos);
    }

    /**
     * Devuelve SOLO el principal por día (compatibilidad con vistas viejas).
     */
    public Map<String, Integer> getAlmuerzos() {
        return getMapaPrincipal(almuerzos);
    }

    public void limpiarTodo() {
        desayunos.clear();
        almuerzos.clear();
        guardarSeleccion();
    }

    // -------------------
    // Persistencia
    // -------------------

    private void guardarSeleccion() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {

            writer.write("[DESAYUNOS]");
            writer.newLine();
            escribirSeccion(writer, desayunos);

            writer.write("[ALMUERZOS]");
            writer.newLine();
            escribirSeccion(writer, almuerzos);

        } catch (IOException e) {
            System.err.println("Error al guardar menú de la semana: " + e.getMessage());
        }
    }

    private void escribirSeccion(BufferedWriter writer, Map<String, Map<String, Integer>> data) throws IOException {
        // Orden estable: iterar por día y luego por componente
        for (Map.Entry<String, Map<String, Integer>> entryDia : data.entrySet()) {
            String dia = entryDia.getKey();
            Map<String, Integer> comps = entryDia.getValue();
            if (comps == null) continue;
            for (Map.Entry<String, Integer> entryComp : comps.entrySet()) {
                String comp = entryComp.getKey();
                Integer id = entryComp.getValue();
                if (comp == null || id == null) continue;
                writer.write(dia + "|" + comp + "|" + id);
                writer.newLine();
            }
        }
    }

    private void cargarSeleccion() {
        desayunos.clear();
        almuerzos.clear();

        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                if (linea.equals("[DESAYUNOS]")) {
                    seccionActual = "DESAYUNOS";
                    continue;
                }
                if (linea.equals("[ALMUERZOS]")) {
                    seccionActual = "ALMUERZOS";
                    continue;
                }

                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    // formato viejo: dia|id
                    String dia = partes[0];
                    int menuId = Integer.parseInt(partes[1]);
                    if ("DESAYUNOS".equals(seccionActual)) {
                        setSeleccion(desayunos, dia, COMP_PLATO_PRINCIPAL, menuId);
                    } else if ("ALMUERZOS".equals(seccionActual)) {
                        setSeleccion(almuerzos, dia, COMP_PLATO_PRINCIPAL, menuId);
                    }
                } else if (partes.length >= 3) {
                    // formato nuevo: dia|componente|id
                    String dia = partes[0];
                    String componente = partes[1];
                    int menuId = Integer.parseInt(partes[2]);
                    if ("DESAYUNOS".equals(seccionActual)) {
                        setSeleccion(desayunos, dia, componente, menuId);
                    } else if ("ALMUERZOS".equals(seccionActual)) {
                        setSeleccion(almuerzos, dia, componente, menuId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar menú de la semana: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al cargar menú de la semana: " + e.getMessage());
        }
    }

    // -------------------
    // Helpers
    // -------------------

    private void setSeleccion(Map<String, Map<String, Integer>> data, String dia, String componente, int menuId) {
        if (dia == null || componente == null) return;
        Map<String, Integer> comps = data.get(dia);
        if (comps == null) {
            comps = new LinkedHashMap<>();
            data.put(dia, comps);
        }
        comps.put(componente, menuId);
    }

    private Integer getSeleccion(Map<String, Map<String, Integer>> data, String dia, String componente) {
        if (dia == null || componente == null) return null;
        Map<String, Integer> comps = data.get(dia);
        if (comps == null) return null;
        return comps.get(componente);
    }

    private void eliminarSeleccion(Map<String, Map<String, Integer>> data, String dia, String componente) {
        if (dia == null || componente == null) return;
        Map<String, Integer> comps = data.get(dia);
        if (comps == null) return;
        comps.remove(componente);
        if (comps.isEmpty()) {
            data.remove(dia);
        }
    }

    private Map<String, Integer> getMapaPrincipal(Map<String, Map<String, Integer>> data) {
        Map<String, Integer> out = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> e : data.entrySet()) {
            Integer id = e.getValue() == null ? null : e.getValue().get(COMP_PLATO_PRINCIPAL);
            if (id != null) out.put(e.getKey(), id);
        }
        return out;
    }

    private Map<String, Map<String, Integer>> deepCopy(Map<String, Map<String, Integer>> src) {
        Map<String, Map<String, Integer>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> e : src.entrySet()) {
            Map<String, Integer> inner = new HashMap<>();
            if (e.getValue() != null) inner.putAll(e.getValue());
            copy.put(e.getKey(), inner);
        }
        return copy;
    }
}
