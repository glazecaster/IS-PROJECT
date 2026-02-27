package model;

public class Plato {
    private final String nombre;
    private final String descripcion;
    private final String tipo;
    private final String proteinas;
    private final String carbohidratos;
    private final String calorias;
    private final String icono;

    public Plato(String nombre, String descripcion, String tipo,
                 String proteinas, String carbohidratos, String calorias, String icono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.calorias = calorias;
        this.icono = icono;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }
    public String getProteinas() { return proteinas; }
    public String getCarbohidratos() { return carbohidratos; }
    public String getCalorias() { return calorias; }
    public String getIcono() { return icono; }
}

