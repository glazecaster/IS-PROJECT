package model;

import java.util.*;

import admin.model.CCBCalculator;
import admin.model.Menu;
import admin.model.MenuSemana;
import admin.model.MenuServicio;

public class RepositorioSemana {

    private static final String COMP_PLATO_PRINCIPAL = "PLATO_PRINCIPAL";
    private static final String COMP_ACOMPANANTE = "ACOMPANANTE";
    private static final String COMP_ENSALADA = "ENSALADA";

    private final MenuServicio menuServicio;
    private final MenuSemana menuSemana;
    private final CCBCalculator ccbCalculator;
    private String tipoUsuarioActual = "Estudiante"; 

    public RepositorioSemana() {
        this.menuServicio = new MenuServicio();
        this.menuSemana = new MenuSemana();
        this.ccbCalculator = new CCBCalculator();
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuarioActual = tipoUsuario;
    }

    public List<MenuDia> getMenusSemanaDesayuno() {
        return construirMenusPorServicio(true, tipoUsuarioActual);
    }

    public List<MenuDia> getMenusSemanaAlmuerzo() {
        return construirMenusPorServicio(false, tipoUsuarioActual);
    }

    public List<MenuDia> getMenusSemanaDesayuno(String tipoUsuario) {
        return construirMenusPorServicio(true, tipoUsuario);
    }

    public List<MenuDia> getMenusSemanaAlmuerzo(String tipoUsuario) {
        return construirMenusPorServicio(false, tipoUsuario);
    }

    private List<MenuDia> construirMenusPorServicio(boolean desayuno, String tipoUsuario) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
        List<MenuDia> resultado = new ArrayList<>();

        for (String dia : dias) {
            MenuDia menuDia = new MenuDia(dia, obtenerIcono(dia, desayuno));
            menuDia.setHorario(desayuno ? "7:00am – 10:00am" : "12:00pm – 3:00pm");
            menuDia.setTipoServicio(desayuno ? "Desayuno" : "Almuerzo");

            boolean tieneAlgunComponente = false;
            double totalProteina = 0;
            double totalCarbohidratos = 0;
            double totalCalorias = 0;
            double precioTotalCCB = 0; 

            if (desayuno) {
                Integer idPrincipal = menuSemana.getDesayuno(dia, COMP_PLATO_PRINCIPAL);
                Integer idAcompanante = menuSemana.getDesayuno(dia, COMP_ACOMPANANTE);

                if (idPrincipal != null && idPrincipal > 0) {
                    menuDia.setMenuIdComponente(COMP_PLATO_PRINCIPAL, idPrincipal);
                    tieneAlgunComponente = true;
                }
                if (idAcompanante != null && idAcompanante > 0) {
                    menuDia.setMenuIdComponente(COMP_ACOMPANANTE, idAcompanante);
                    tieneAlgunComponente = true;
                }

                
                double[] totals = construirPlatosDesayuno(menuDia, idPrincipal, idAcompanante, tipoUsuario);
                totalProteina = totals[0];
                totalCarbohidratos = totals[1];
                totalCalorias = totals[2];
                precioTotalCCB = totals[3]; 
                
            } else {
                Integer idPrincipal = menuSemana.getAlmuerzo(dia, COMP_PLATO_PRINCIPAL);
                Integer idAcompanante = menuSemana.getAlmuerzo(dia, COMP_ACOMPANANTE);
                Integer idEnsalada = menuSemana.getAlmuerzo(dia, COMP_ENSALADA);

                if (idPrincipal != null && idPrincipal > 0) {
                    menuDia.setMenuIdComponente(COMP_PLATO_PRINCIPAL, idPrincipal);
                    tieneAlgunComponente = true;
                }
                if (idAcompanante != null && idAcompanante > 0) {
                    menuDia.setMenuIdComponente(COMP_ACOMPANANTE, idAcompanante);
                    tieneAlgunComponente = true;
                }
                if (idEnsalada != null && idEnsalada > 0) {
                    menuDia.setMenuIdComponente(COMP_ENSALADA, idEnsalada);
                    tieneAlgunComponente = true;
                }

                
                double[] totals = construirPlatosAlmuerzo(menuDia, idPrincipal, idAcompanante, idEnsalada, tipoUsuario);
                totalProteina = totals[0];
                totalCarbohidratos = totals[1];
                totalCalorias = totals[2];
                precioTotalCCB = totals[3]; 
            }

            if (!tieneAlgunComponente) {
                menuDia.getPlatos().clear();
                menuDia.addPlato(new Plato(
                        "Sin menú configurado",
                        "No hay menú seleccionado para este día en la administración.",
                        "—",
                        "0", "0", "0",
                        "⚠️"
                ));
                menuDia.setTotales("0", "0", "0");
                menuDia.setTarifa(-1);
            } else {
                menuDia.setTotales(
                    String.valueOf((int) Math.round(totalCalorias)),
                    String.valueOf((int) Math.round(totalProteina)),
                    String.valueOf((int) Math.round(totalCarbohidratos))
                );
                
                menuDia.setTarifa(Math.round(precioTotalCCB * 100.0) / 100.0);
            }

            resultado.add(menuDia);
        }

        return resultado;
    }

    
    private double[] construirPlatosDesayuno(MenuDia menuDia, Integer idPrincipal, Integer idAcompanante, String tipoUsuario) {
        double totalProteina = 0;
        double totalCarbohidratos = 0;
        double totalCalorias = 0;
        double precioTotalCCB = 0;

        
        double[] p1 = agregarPlato(menuDia, idPrincipal, "Plato principal", "🍳", tipoUsuario);
        totalProteina += p1[0];
        totalCarbohidratos += p1[1];
        totalCalorias += p1[2];
        precioTotalCCB += p1[3]; 

        
        double[] p2 = agregarPlato(menuDia, idAcompanante, "Acompañante", "🥖", tipoUsuario);
        totalProteina += p2[0];
        totalCarbohidratos += p2[1];
        totalCalorias += p2[2];
        precioTotalCCB += p2[3]; 

        return new double[]{totalProteina, totalCarbohidratos, totalCalorias, precioTotalCCB};
    }

    
    private double[] construirPlatosAlmuerzo(MenuDia menuDia, Integer idPrincipal, Integer idAcompanante, 
                                              Integer idEnsalada, String tipoUsuario) {
        double totalProteina = 0;
        double totalCarbohidratos = 0;
        double totalCalorias = 0;
        double precioTotalCCB = 0;

        
        double[] p1 = agregarPlato(menuDia, idPrincipal, "Plato principal", "🍗", tipoUsuario);
        totalProteina += p1[0];
        totalCarbohidratos += p1[1];
        totalCalorias += p1[2];
        precioTotalCCB += p1[3];

        
        double[] p2 = agregarPlato(menuDia, idAcompanante, "Acompañante", "🍚", tipoUsuario);
        totalProteina += p2[0];
        totalCarbohidratos += p2[1];
        totalCalorias += p2[2];
        precioTotalCCB += p2[3];

        
        double[] p3 = agregarPlato(menuDia, idEnsalada, "Ensalada", "🥗", tipoUsuario);
        totalProteina += p3[0];
        totalCarbohidratos += p3[1];
        totalCalorias += p3[2];
        precioTotalCCB += p3[3];

        return new double[]{totalProteina, totalCarbohidratos, totalCalorias, precioTotalCCB};
    }

    
    private double[] agregarPlato(MenuDia menuDia, Integer idMenu, String tipoPlato, String icono, String tipoUsuario) {
        if (idMenu == null || idMenu <= 0) {
            menuDia.addPlato(new Plato(
                    "No disponible",
                    "Este componente no ha sido seleccionado para hoy",
                    tipoPlato,
                    "0", "0", "0",
                    icono
            ));
            return new double[]{0, 0, 0, 0};
        }

        Menu menu = menuServicio.getMenuPorId(idMenu);
        if (menu == null) {
            menuDia.addPlato(new Plato(
                    "Menú no encontrado",
                    "ID: " + idMenu + " no existe en la base de datos",
                    tipoPlato,
                    "0", "0", "0",
                    "⚠️"
            ));
            return new double[]{0, 0, 0, 0};
        }

        double proteina = extraerNumero(menu.getProteinas());
        double carbohidratos = extraerNumero(menu.getCarbohidratos());
        double calorias = extraerNumero(menu.getCalorias());
        
        
        double precioCCB = ccbCalculator.calcularPrecioMenuCompleto(idMenu, tipoUsuario);

        
        menuDia.addPlato(new Plato(
                menu.getNombre(),
                menu.getDescripcion(),
                tipoPlato,
                menu.getProteinas(),
                menu.getCarbohidratos(),
                menu.getCalorias(),
                icono
        ));

        
        return new double[]{proteina, carbohidratos, calorias, precioCCB};
    }

    
    private double extraerNumero(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        String limpio = texto.replaceAll("[^0-9.,-]", "").replace(",", ".");
        if (limpio.contains("-")) {
            String[] partes = limpio.split("-");
            try {
                double min = Double.parseDouble(partes[0].trim());
                double max = Double.parseDouble(partes[1].trim());
                return (min + max) / 2.0;
            } catch (Exception e) {
                return 0;
            }
        } else {
            try {
                return Double.parseDouble(limpio);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private String obtenerIcono(String dia, boolean desayuno) {
        if (desayuno) {
            switch (dia) {
                case "Lunes": return "🥣";
                case "Martes": return "🥤";
                case "Miércoles": return "🥞";
                case "Jueves": return "🥖";
                case "Viernes": return "🍓";
                default: return "🍽️";
            }
        } else {
            switch (dia) {
                case "Lunes": return "🍽️";
                case "Martes": return "🍗";
                case "Miércoles": return "🥗";
                case "Jueves": return "🍲";
                case "Viernes": return "🍝";
                default: return "🍽️";
            }
        }
    }
}