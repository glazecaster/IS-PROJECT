package admin.model;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private int id;
    private String dia;
    private String nombre;
    private String tipo;
    private String tipoServicio;
    private String descripcion;
    private String proteinas;
    private String carbohidratos;
    private String calorias;
    private double precioVenta;
    private List<Ingrediente> ingredientes;
    private List<Double> cantidades;

    public Menu(int id, String dia, String nombre, String tipo, String tipoServicio, String descripcion, 
                String proteinas, String carbohidratos, String calorias, double precioVenta) {
        this.id = id;
        this.dia = dia;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.calorias = calorias;
        this.precioVenta = precioVenta;
        this.ingredientes = new ArrayList<>();
        this.cantidades = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getProteinas() { return proteinas; }
    public void setProteinas(String proteinas) { this.proteinas = proteinas; }
    
    public String getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(String carbohidratos) { this.carbohidratos = carbohidratos; }
    
    public String getCalorias() { return calorias; }
    public void setCalorias(String calorias) { this.calorias = calorias; }
    
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    
    public List<Ingrediente> getIngredientes() { return ingredientes; }
    public List<Double> getCantidades() { return cantidades; }
    
    /**
     * Verifica si el menú tiene ingredientes asignados
     * @return true si tiene al menos un ingrediente
     */
    public boolean tieneIngredientes() {
        return ingredientes != null && !ingredientes.isEmpty();
    }
    
    public void agregarIngrediente(Ingrediente ing, double cantidad) {
        ingredientes.add(ing);
        cantidades.add(cantidad);
    }
    
    public void eliminarIngrediente(int index) {
        if (index >= 0 && index < ingredientes.size()) {
            ingredientes.remove(index);
            cantidades.remove(index);
        }
    }
    
    public void limpiarIngredientes() {
        ingredientes.clear();
        cantidades.clear();
    }
    
    public double calcularCostoIngredientes() {
        double total = 0;
        for (int i = 0; i < ingredientes.size(); i++) {
            total += ingredientes.get(i).getPrecioUnitario() * cantidades.get(i);
        }
        return total;
    }
    
    public String getIngredientesComoString() {
        if (ingredientes.isEmpty()) return "Sin ingredientes";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredientes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(ingredientes.get(i).getNombre())
                .append(" (").append(cantidades.get(i)).append(" ")
                .append(ingredientes.get(i).getUnidadMedida()).append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|").append(dia).append("|").append(nombre).append("|")
            .append(tipo).append("|").append(tipoServicio).append("|").append(descripcion).append("|")
            .append(proteinas).append("|").append(carbohidratos).append("|").append(calorias).append("|")
            .append(precioVenta).append("|").append(ingredientes.size());
        
        for (int i = 0; i < ingredientes.size(); i++) {
            sb.append("|").append(ingredientes.get(i).getId())
            .append("|").append(cantidades.get(i));
        }
        return sb.toString();
    }
    
    public static Menu fromString(String linea, List<Ingrediente> ingredientesDisponibles) {
        String[] partes = linea.split("\\|");
        if (partes.length < 10) {
            System.err.println("Error: Línea con menos de 10 partes: " + linea);
            return null;
        }
        
        try {
            int id = Integer.parseInt(partes[0]);
            String dia = partes[1];
            String nombre = partes[2];
            String tipo = partes[3];
            String tipoServicio = partes[4];
            String descripcion = partes[5];
            String proteinas = partes[6];
            String carbohidratos = partes[7];
            String calorias = partes[8];
            double precio = Double.parseDouble(partes[9]);
            
            Menu menu = new Menu(id, dia, nombre, tipo, tipoServicio, descripcion, 
                                proteinas, carbohidratos, calorias, precio);
            
            if (partes.length >= 11) {
                
                int numIngredientes;
                int startIdx;
                
                try {
                    numIngredientes = Integer.parseInt(partes[10]);
                    startIdx = 11;
                } catch (NumberFormatException e) {
                    numIngredientes = (partes.length - 10) / 2;
                    startIdx = 10;
                }
                
                // procesar ingredientes
                for (int i = 0; i < numIngredientes; i++) {
                    int idx = startIdx + (i * 2);
                    if (idx + 1 < partes.length) {
                        try {
                            int ingId = Integer.parseInt(partes[idx]);
                            double cantidad = Double.parseDouble(partes[idx + 1]);
                            
                            // buscar el ingrediente por ID
                            for (Ingrediente ing : ingredientesDisponibles) {
                                if (ing.getId() == ingId) {
                                    menu.agregarIngrediente(ing, cantidad);
                                    break;
                                }
                            }
                        } catch (NumberFormatException ex) {
                            System.err.println("Error al parsear ingrediente en posición " + idx + ": " + partes[idx] + "|" + partes[idx+1]);
                        }
                    }
                }
            }
            
            return menu;
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear número en línea: " + linea);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Error inesperado al parsear línea: " + linea);
            e.printStackTrace();
            return null;
        }
    }
}