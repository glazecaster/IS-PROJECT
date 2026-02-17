package model;


public class Monedero {
    private final String usuario;
    private double saldo;

    public Monedero(String usuario, double saldo) {
        this.usuario = usuario;
        this.saldo = saldo;
    }

    public String getUsuario() { return usuario; }
    public double getSaldo() { return saldo; }


}
