package model;

import java.util.*;

import admin.model.Menu;
import admin.model.MenuSemana;
import admin.model.MenuServicio;


public class RepositorioSemana {

    private static final String COMP_PLATO_PRINCIPAL = "PLATO_PRINCIPAL";
    private static final String COMP_ACOMPANANTE = "ACOMPANANTE";
    private static final String COMP_BEBIDA = "BEBIDA";
    private static final String COMP_ENSALADA = "ENSALADA";

    public List<MenuDia> getMenusSemanaDesayuno() {
        List<MenuDia> db = buildFromAdminDB(true);
        if (db != null && !db.isEmpty()) return db;
        return hardcodedDesayuno();
    }

    public List<MenuDia> getMenusSemanaAlmuerzo() {
        List<MenuDia> db = buildFromAdminDB(false);
        if (db != null && !db.isEmpty()) return db;
        return hardcodedAlmuerzo();
    }

    private List<MenuDia> buildFromAdminDB(boolean desayuno) {
        try {
            MenuSemana ms = new MenuSemana();
            MenuServicio menuServicio = new MenuServicio();
            List<Menu> todosMenus = menuServicio.getMenus();

            boolean hayAlMenosUno = false;

            String[] dias = new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
            List<MenuDia> out = new ArrayList<>();

            for (String dia : dias) {
                MenuDia md = new MenuDia(dia, parcheDia(dia, desayuno));
                md.setHorario(desayuno ? "7:00am – 10:00am" : "12:00pm – 3:00pm");
                md.setTipoServicio(desayuno ? "Desayuno" : "Almuerzo");

                if (desayuno) {
                    Integer p = getIdDesayuno(ms, dia, COMP_PLATO_PRINCIPAL);
                    Integer a = getIdDesayuno(ms, dia, COMP_ACOMPANANTE);
                    Integer b = getIdDesayuno(ms, dia, COMP_BEBIDA);
                    if (p != null || a != null || b != null) hayAlMenosUno = true;

                    a = autoSeleccionarSiFalta(todosMenus, dia, "Desayuno", true, COMP_ACOMPANANTE, a, p, b);
                    b = autoSeleccionarSiFalta(todosMenus, dia, "Desayuno", true, COMP_BEBIDA, b, p, a);

                    construirComponentes(md, menuServicio, true, p, a, null, b);
                } else {
                    Integer p = getIdAlmuerzo(ms, dia, COMP_PLATO_PRINCIPAL);
                    Integer a = getIdAlmuerzo(ms, dia, COMP_ACOMPANANTE);
                    Integer e = getIdAlmuerzo(ms, dia, COMP_ENSALADA);
                    Integer b = getIdAlmuerzo(ms, dia, COMP_BEBIDA);
                    if (p != null || a != null || e != null || b != null) hayAlMenosUno = true;

                    a = autoSeleccionarSiFalta(todosMenus, dia, "Almuerzo", false, COMP_ACOMPANANTE, a, p, e, b);
                    e = autoSeleccionarSiFalta(todosMenus, dia, "Almuerzo", false, COMP_ENSALADA, e, p, a, b);
                    b = autoSeleccionarSiFalta(todosMenus, dia, "Almuerzo", false, COMP_BEBIDA, b, p, a, e);

                    construirComponentes(md, menuServicio, false, p, a, e, b);
                }

                out.add(md);
            }

            if (!hayAlMenosUno) return Collections.emptyList();
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private Integer getIdDesayuno(MenuSemana ms, String dia, String componente) {
        Integer id = ms.getDesayuno(dia, componente);
        if (id == null && "Miércoles".equals(dia)) id = ms.getDesayuno("Miercoles", componente);
        if (id == null && COMP_PLATO_PRINCIPAL.equals(componente)) {
            id = ms.getDesayuno(dia);
            if (id == null && "Miércoles".equals(dia)) id = ms.getDesayuno("Miercoles");
        }
        return id;
    }

    private Integer getIdAlmuerzo(MenuSemana ms, String dia, String componente) {
        Integer id = ms.getAlmuerzo(dia, componente);
        if (id == null && "Miércoles".equals(dia)) id = ms.getAlmuerzo("Miercoles", componente);
        if (id == null && COMP_PLATO_PRINCIPAL.equals(componente)) {
            id = ms.getAlmuerzo(dia);
            if (id == null && "Miércoles".equals(dia)) id = ms.getAlmuerzo("Miercoles");
        }
        return id;
    }

    private void construirComponentes(MenuDia md,
                                     MenuServicio menuServicio,
                                     boolean desayuno,
                                     Integer idPrincipal,
                                     Integer idAcompanante,
                                     Integer idEnsalada,
                                     Integer idBebida) {
        md.getPlatos().clear();

        if (idPrincipal != null) md.setMenuIdComponente(COMP_PLATO_PRINCIPAL, idPrincipal);
        if (idAcompanante != null) md.setMenuIdComponente(COMP_ACOMPANANTE, idAcompanante);
        if (idEnsalada != null) md.setMenuIdComponente(COMP_ENSALADA, idEnsalada);
        if (idBebida != null) md.setMenuIdComponente(COMP_BEBIDA, idBebida);

        md.setMenuId((idPrincipal != null && idPrincipal > 0) ? idPrincipal : -1);

        double totProt = 0, totCarb = 0, totKcal = 0;
        boolean pudoSumar = true;
        PlatoInfo[] orden = desayuno
                ? new PlatoInfo[]{
                    new PlatoInfo(COMP_PLATO_PRINCIPAL, "Plato principal", idPrincipal, "🍳"),
                    new PlatoInfo(COMP_ACOMPANANTE, "Acompañante", idAcompanante, "🥖"),
                    new PlatoInfo(COMP_BEBIDA, "Bebida", idBebida, "🥤")
                }
                : new PlatoInfo[]{
                    new PlatoInfo(COMP_PLATO_PRINCIPAL, "Plato principal", idPrincipal, "🍗"),
                    new PlatoInfo(COMP_ACOMPANANTE, "Acompañante", idAcompanante, "🍚"),
                    new PlatoInfo(COMP_ENSALADA, "Ensalada", idEnsalada, "🥗"),
                    new PlatoInfo(COMP_BEBIDA, "Bebida", idBebida, "🥤")
                };

        boolean hayAlgo = false;

        for (PlatoInfo info : orden) {
            if (info.menuId == null || info.menuId <= 0) {
                md.addPlato(new Plato(
                        "Sin " + info.etiqueta,
                        "No hay un " + info.etiqueta.toLowerCase() + " configurado para este día.",
                        info.etiqueta,
                        "0", "0", "0",
                        info.icono
                ));
                continue;
            }

            hayAlgo = true;
            Menu m = menuServicio.getMenuPorId(info.menuId);
            if (m == null) {
                md.addPlato(new Plato(
                        "No encontrado",
                        "No se encontró el menú con ID " + info.menuId + ".",
                        info.etiqueta,
                        "0", "0", "0",
                        "⚠️"
                ));
                pudoSumar = false;
                continue;
            }

            String prot = safeStr(m.getProteinas());
            String carb = safeStr(m.getCarbohidratos());
            String kcal = safeStr(m.getCalorias());

            md.addPlato(new Plato(
                    m.getNombre(),
                    m.getDescripcion(),
                    info.etiqueta,
                    prot,
                    carb,
                    kcal,
                    info.icono
            ));

            Double p = parseNumero(prot);
            Double c = parseNumero(carb);
            Double k = parseNumero(kcal);
            if (p == null || c == null || k == null) {
                pudoSumar = false;
            } else {
                totProt += p;
                totCarb += c;
                totKcal += k;
            }
        }

        if (!hayAlgo) {
            md.getPlatos().clear();
            md.addPlato(new Plato(
                    "Sin menú configurado",
                    "No hay menú seleccionado para este día.",
                    "—",
                    "0", "0", "0",
                    "—"
            ));
            md.setTotales("0", "0", "0");
            return;
        }

        if (pudoSumar) {
            md.setTotales(format0(totKcal), format0(totProt), format0(totCarb));
        } else {
            md.setTotales("...", "...", "...");
        }
    }

    private Integer autoSeleccionarSiFalta(List<Menu> todos,
                                          String dia,
                                          String servicio,
                                          boolean desayuno,
                                          String componente,
                                          Integer idActual,
                                          Integer... idsYaUsados) {
        if (idActual != null && idActual > 0) return idActual;
        if (todos == null) return null;

        for (Menu m : todos) {
            if (m == null) continue;
            if (!diaEquals(m.getDia(), dia)) continue;
            if (m.getTipoServicio() == null || !m.getTipoServicio().equalsIgnoreCase(servicio)) continue;
            if (!matchesComponenteByTipo(m.getTipo(), desayuno, componente)) continue;

            boolean usado = false;
            if (idsYaUsados != null) {
                for (Integer x : idsYaUsados) {
                    if (x != null && x == m.getId()) {
                        usado = true;
                        break;
                    }
                }
            }
            if (usado) continue;
            return m.getId();
        }
        return null;
    }

    private boolean diaEquals(String diaMenu, String diaTabla) {
        if (diaMenu == null || diaTabla == null) return false;
        if (diaMenu.equalsIgnoreCase(diaTabla)) return true;
        if ("Miércoles".equalsIgnoreCase(diaTabla) && "Miercoles".equalsIgnoreCase(diaMenu)) return true;
        if ("Miércoles".equalsIgnoreCase(diaMenu) && "Miercoles".equalsIgnoreCase(diaTabla)) return true;
        return false;
    }

    private boolean matchesComponenteByTipo(String tipo, boolean desayuno, String componenteKey) {
        String t = (tipo == null ? "" : tipo.trim().toLowerCase());
        String tn = t
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");

        if (COMP_PLATO_PRINCIPAL.equals(componenteKey)) {
            if (tn.contains("principal") || tn.contains("plato")) return true;
            if (desayuno && (tn.equals("desayuno") || tn.contains("desay"))) return true;
            return false;
        }
        if (COMP_ACOMPANANTE.equals(componenteKey)) {
            return tn.contains("acompan") || tn.contains("guarn") || tn.contains("acompa");
        }
        if (COMP_BEBIDA.equals(componenteKey)) {
            return tn.contains("bebida") || tn.contains("jugo") || tn.contains("cafe") || tn.contains("te");
        }
        if (COMP_ENSALADA.equals(componenteKey)) {
            return tn.contains("ensalada");
        }
        return false;
    }

    private static class PlatoInfo {
        final String key;
        final String etiqueta;
        final Integer menuId;
        final String icono;

        PlatoInfo(String key, String etiqueta, Integer menuId, String icono) {
            this.key = key;
            this.etiqueta = etiqueta;
            this.menuId = menuId;
            this.icono = icono;
        }
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    private Double parseNumero(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("([0-9]+(?:\\.[0-9]+)?)").matcher(t);
        java.util.List<Double> nums = new java.util.ArrayList<>();
        while (m.find()) {
            try {
                nums.add(Double.parseDouble(m.group(1)));
            } catch (Exception ignored) {}
        }
        if (nums.isEmpty()) return null;
        if (nums.size() >= 2 && t.contains("-")) {
            return (nums.get(0) + nums.get(1)) / 2.0;
        }
        return nums.get(0);
    }

    private String format0(double v) {
        return String.valueOf((int)Math.round(v));
    }

    private String parcheDia(String dia, boolean desayuno) {
        if (desayuno) {
            if ("Lunes".equals(dia)) return "🥣";
            if ("Martes".equals(dia)) return "🥤";
            if ("Miércoles".equals(dia)) return "🥞";
            if ("Jueves".equals(dia)) return "🥖";
            return "🍓";
        } else {
            if ("Lunes".equals(dia)) return "🍽️";
            if ("Martes".equals(dia)) return "🍗";
            if ("Miércoles".equals(dia)) return "🥗";
            if ("Jueves".equals(dia)) return "🍲";
            return "🍝";
        }
    }
    
    private List<MenuDia> hardcodedDesayuno() {
        List<MenuDia> list = new ArrayList<>();
        final String horario = "7:00am – 10:00am";

        MenuDia lunes = new MenuDia("Lunes", "🥣");
        lunes.setHorario(horario);
        lunes.addPlato(new Plato("Avena con fruta", "Avena + banana + miel", "Plato principal", "9", "54", "320", "🍳"));
        lunes.addPlato(new Plato("Tostadas", "Pan tostado con mantequilla", "Acompañante", "4", "22", "140", "🥖"));
        lunes.addPlato(new Plato("Jugo natural", "Naranja", "Bebida", "2", "25", "110", "🥤"));
        lunes.setTotales("570", "15", "101");
        list.add(lunes);

        MenuDia martes = new MenuDia("Martes", "🥤");
        martes.setHorario(horario);
        martes.addPlato(new Plato("Arepa con queso", "Arepa rellena con queso", "Plato principal", "16", "46", "420", "🍳"));
        martes.addPlato(new Plato("Fruta", "Manzana", "Acompañante", "0", "14", "55", "🍎"));
        martes.addPlato(new Plato("Café", "Café negro", "Bebida", "0", "2", "10", "☕"));
        martes.setTotales("485", "16", "62");
        list.add(martes);

        MenuDia miercoles = new MenuDia("Miércoles", "🥞");
        miercoles.setHorario(horario);
        miercoles.addPlato(new Plato("Panquecas", "Con sirope", "Plato principal", "10", "85", "500", "🍳"));
        miercoles.addPlato(new Plato("Queso", "Queso blanco (porción)", "Acompañante", "7", "1", "90", "🧀"));
        miercoles.addPlato(new Plato("Leche", "Vaso de leche", "Bebida", "6", "12", "120", "🥛"));
        miercoles.setTotales("710", "23", "98");
        list.add(miercoles);

        MenuDia jueves = new MenuDia("Jueves", "🥖");
        jueves.setHorario(horario);
        jueves.addPlato(new Plato("Pan con jamón", "Sándwich", "Plato principal", "18", "40", "450", "🍳"));
        jueves.addPlato(new Plato("Mandarina", "1 unidad", "Acompañante", "1", "12", "50", "🍊"));
        jueves.addPlato(new Plato("Agua", "Vaso de agua", "Bebida", "0", "0", "0", "💧"));
        jueves.setTotales("500", "19", "52");
        list.add(jueves);

        MenuDia viernes = new MenuDia("Viernes", "🍓");
        viernes.setHorario(horario);
        viernes.addPlato(new Plato("Yogurt con granola", "Yogurt + granola", "Plato principal", "14", "55", "380", "🍳"));
        viernes.addPlato(new Plato("Galletas", "Galletas integrales (porción)", "Acompañante", "3", "20", "120", "🍪"));
        viernes.addPlato(new Plato("Té", "Té de manzanilla", "Bebida", "0", "1", "5", "🫖"));
        viernes.setTotales("505", "17", "76");
        list.add(viernes);

        return list;
    }

    private List<MenuDia> hardcodedAlmuerzo() {
        List<MenuDia> list = new ArrayList<>();
        final String horario = "12:00pm – 3:00pm";

        MenuDia lunes = new MenuDia("Lunes", "🍽️");
        lunes.setHorario(horario);
        lunes.addPlato(new Plato("Pollo a la plancha", "Con especias", "Plato principal", "45", "10", "550", "🍗"));
        lunes.addPlato(new Plato("Arroz", "Arroz blanco", "Acompañante", "4", "44", "200", "🍚"));
        lunes.addPlato(new Plato("Ensalada mixta", "Lechuga y tomate", "Ensalada", "2", "8", "60", "🥗"));
        lunes.addPlato(new Plato("Jugo", "Jugo natural", "Bebida", "1", "22", "100", "🥤"));
        lunes.setTotales("910", "52", "84");
        list.add(lunes);

        MenuDia martes = new MenuDia("Martes", "🍗");
        martes.setHorario(horario);
        martes.addPlato(new Plato("Carne guisada", "Con vegetales", "Plato principal", "40", "20", "600", "🥩"));
        martes.addPlato(new Plato("Puré", "Puré de papa", "Acompañante", "5", "40", "250", "🥔"));
        martes.addPlato(new Plato("Ensalada", "Repollo", "Ensalada", "2", "9", "70", "🥗"));
        martes.addPlato(new Plato("Agua", "Vaso de agua", "Bebida", "0", "0", "0", "💧"));
        martes.setTotales("920", "47", "69");
        list.add(martes);

        MenuDia miercoles = new MenuDia("Miércoles", "🥗");
        miercoles.setHorario(horario);
        miercoles.addPlato(new Plato("Pescado", "Al horno", "Plato principal", "42", "12", "520", "🐟"));
        miercoles.addPlato(new Plato("Pasta", "Pasta corta", "Acompañante", "10", "55", "300", "🍝"));
        miercoles.addPlato(new Plato("Ensalada", "Mixta", "Ensalada", "3", "10", "80", "🥗"));
        miercoles.addPlato(new Plato("Jugo", "Jugo natural", "Bebida", "1", "22", "100", "🥤"));
        miercoles.setTotales("1000", "56", "99");
        list.add(miercoles);

        MenuDia jueves = new MenuDia("Jueves", "🍲");
        jueves.setHorario(horario);
        jueves.addPlato(new Plato("Pollo guisado", "Con papas", "Plato principal", "38", "25", "580", "🍗"));
        jueves.addPlato(new Plato("Arroz integral", "Porción", "Acompañante", "5", "45", "220", "🍚"));
        jueves.addPlato(new Plato("Ensalada", "Mixta", "Ensalada", "3", "10", "80", "🥗"));
        jueves.addPlato(new Plato("Agua", "Vaso de agua", "Bebida", "0", "0", "0", "💧"));
        jueves.setTotales("880", "46", "80");
        list.add(jueves);

        MenuDia viernes = new MenuDia("Viernes", "🍝");
        viernes.setHorario(horario);
        viernes.addPlato(new Plato("Pasta boloñesa", "Con carne", "Plato principal", "32", "85", "700", "🍝"));
        viernes.addPlato(new Plato("Pan", "Pan (porción)", "Acompañante", "4", "20", "120", "🥖"));
        viernes.addPlato(new Plato("Ensalada", "Rúgula", "Ensalada", "2", "7", "60", "🥗"));
        viernes.addPlato(new Plato("Té", "Té frío", "Bebida", "0", "2", "10", "🧊"));
        viernes.setTotales("890", "38", "114");
        list.add(viernes);

        return list;
    }
}
