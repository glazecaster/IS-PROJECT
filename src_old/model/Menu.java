package model;

public class Menu {
    private String dia;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String proteinas;
    private String carbohidratos;
    private String calorias;
    private double precio;

    public Menu(String dia, String nombre, String tipo, String descripcion, 
                String proteinas, String carbohidratos, String calorias, double precio) {
        this.dia = dia;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.calorias = calorias;
        this.precio = precio;
    }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getProteinas() { return proteinas; }
    public void setProteinas(String proteinas) { this.proteinas = proteinas; }
    
    public String getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(String carbohidratos) { this.carbohidratos = carbohidratos; }
    
    public String getCalorias() { return calorias; }
    public void setCalorias(String calorias) { this.calorias = calorias; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return dia + "|" + nombre + "|" + tipo + "|" + descripcion + "|" + 
               proteinas + "|" + carbohidratos + "|" + calorias + "|" + precio;
    }
    
    public static Menu fromString(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length >= 8) {
            try {
                double precio = Double.parseDouble(partes[7]);
                return new Menu(partes[0], partes[1], partes[2], partes[3], 
                               partes[4], partes[5], partes[6], precio);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}