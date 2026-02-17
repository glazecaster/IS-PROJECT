package model;

import java.util.ArrayList;
import java.util.List;

public class RepositorioSemana {

    public List<MenuDia> getMenusSemana() {
        List<MenuDia> list = new ArrayList<>();

        MenuDia lunes = new MenuDia("Lunes", "🍛");
        lunes.addPlato(new Plato("Pollo al curry",
                "Pollo condimentado con especias y cocinado a la plancha",
                "Plato principal", "35 - 45 g", "50 - 65 g", "550 - 700 kcal", "🍗"));
        lunes.addPlato(new Plato("Arroz",
                "Arroz blanco cocido al vapor",
                "Acompañante", "4.3 g", "45 g", "205 - 215 kcal", "🍚"));
        lunes.addPlato(new Plato("Ensalada",
                "Ensalada a base de lechuga, tomate, cebolla y mayonesa",
                "Ensalada", "2.1 g", "9.5 g", "135 kcal", "🥗"));
        lunes.addPlato(new Plato("Jugo natural",
                "Jugo natural a base de pulpa de manzana sin azúcar",
                "Bebida", "0.5 g", "28 - 30 g", "110 - 120 kcal", "🍎"));
        lunes.setTotales("1000 - 1170 kcal", "41.4 - 51.9 g", "132.5 - 149.5 g");
        list.add(lunes);

        MenuDia martes = new MenuDia("Martes", "🐟");
        martes.addPlato(new Plato("Pescado a la plancha",
                "Filete de pescado sazonado con limón y hierbas, cocido a la plancha",
                "Plato principal", "32 - 40 g", "20 - 30 g", "420 - 520 kcal", "🐟"));
        martes.addPlato(new Plato("Puré de papas",
                "Puré cremoso de papa con toque de mantequilla",
                "Acompañante", "4 - 6 g", "35 - 45 g", "200 - 280 kcal", "🥔"));
        martes.addPlato(new Plato("Ensalada mixta",
                "Lechuga, tomate y zanahoria con aderezo suave",
                "Ensalada", "2 - 4 g", "8 - 14 g", "90 - 160 kcal", "🥗"));
        martes.addPlato(new Plato("Limonada",
                "Limonada natural sin azúcar añadida",
                "Bebida", "0 g", "18 - 25 g", "70 - 110 kcal", "🍋"));
        martes.setTotales("780 - 1070 kcal", "38 - 50 g", "81 - 109 g");
        list.add(martes);

        MenuDia miercoles = new MenuDia("Miércoles", "🍝");
        miercoles.addPlato(new Plato("Pasta con salsa de tomate",
                "Pasta con salsa casera de tomate y especias",
                "Plato principal", "18 - 25 g", "65 - 85 g", "520 - 680 kcal", "🍝"));
        miercoles.addPlato(new Plato("Pan tostado",
                "Pan tostado ligero para acompañar",
                "Acompañante", "3 - 5 g", "20 - 30 g", "120 - 180 kcal", "🍞"));
        miercoles.addPlato(new Plato("Ensalada",
                "Ensalada fresca del día",
                "Ensalada", "2 - 4 g", "8 - 14 g", "90 - 160 kcal", "🥗"));
        miercoles.addPlato(new Plato("Jugo de naranja",
                "Jugo natural de naranja",
                "Bebida", "1 - 2 g", "22 - 30 g", "90 - 140 kcal", "🍊"));
        miercoles.setTotales("820 - 1160 kcal", "24 - 36 g", "115 - 159 g");
        list.add(miercoles);

        MenuDia jueves = new MenuDia("Jueves", "🥩");
        jueves.addPlato(new Plato("Carne guisada",
                "Carne guisada con vegetales y especias",
                "Plato principal", "30 - 42 g", "20 - 35 g", "520 - 690 kcal", "🥩"));
        jueves.addPlato(new Plato("Arroz integral",
                "Arroz integral cocido al vapor",
                "Acompañante", "5 - 7 g", "40 - 55 g", "210 - 260 kcal", "🍚"));
        jueves.addPlato(new Plato("Ensalada",
                "Ensalada fresca con pepino y tomate",
                "Ensalada", "2 - 4 g", "8 - 14 g", "90 - 160 kcal", "🥗"));
        jueves.addPlato(new Plato("Té frío",
                "Té frío natural",
                "Bebida", "0 g", "10 - 18 g", "30 - 80 kcal", "🧊"));
        jueves.setTotales("850 - 1190 kcal", "37 - 53 g", "78 - 122 g");
        list.add(jueves);

        MenuDia viernes = new MenuDia("Viernes", "🌯");
        viernes.addPlato(new Plato("Wrap de pollo",
                "Tortilla rellena de pollo, vegetales y salsa suave",
                "Plato principal", "28 - 38 g", "45 - 60 g", "480 - 650 kcal", "🌯"));
        viernes.addPlato(new Plato("Papas horneadas",
                "Papas horneadas crujientes",
                "Acompañante", "3 - 5 g", "30 - 45 g", "180 - 260 kcal", "🍟"));
        viernes.addPlato(new Plato("Ensalada",
                "Ensalada ligera del día",
                "Ensalada", "2 - 4 g", "8 - 14 g", "90 - 160 kcal", "🥗"));
        viernes.addPlato(new Plato("Batido de frutas",
                "Batido natural de frutas mixtas",
                "Bebida", "1 - 3 g", "25 - 40 g", "120 - 220 kcal", "🍓"));
        viernes.setTotales("870 - 1290 kcal", "34 - 50 g", "108 - 159 g");
        list.add(viernes);

        return list;
    }
}


