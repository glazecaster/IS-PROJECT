package model;

public class Costo {
    private String concepto;
    private double monto;
    private String periodo;
    private String tipo;

    public Costo(String tipo, String concepto, double monto, String periodo) {
        this.tipo = tipo;
        this.concepto = concepto;
        this.monto = monto;
        this.periodo = periodo;
    }

    public String getTipo() { return tipo; }
    public String getConcepto() { return concepto; }
    public double getMonto() { return monto; }
    public String getPeriodo() { return periodo; }
}