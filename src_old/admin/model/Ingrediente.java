package admin.model;
public class Ingrediente {
    private int id;
    private String nombre;
    private String unidadMedida; 
    private double precioUnitario;
    private double cantidadStock;
    private String categoria; 
    
    public Ingrediente(int id, String nombre, String unidadMedida, double precioUnitario, 
                        double cantidadStock, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.cantidadStock = cantidadStock;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public double getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(double cantidadStock) { this.cantidadStock = cantidadStock; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    @Override
    public String toString() {
        return nombre + " - $" + String.format("%.2f", precioUnitario) + " por " + unidadMedida;
    }
    
    public String toFileString() {
        return id + "|" + nombre + "|" + unidadMedida + "|" + precioUnitario + "|" + 
                cantidadStock + "|" + categoria;
    }
    
    public static Ingrediente fromFileString(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length >= 6) {
            try {
                int id = Integer.parseInt(partes[0]);
                double precio = Double.parseDouble(partes[3]);
                double stock = Double.parseDouble(partes[4]);
                return new Ingrediente(id, partes[1], partes[2], precio, stock, partes[5]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}