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


    public boolean recargar(double monto) {
        if (monto <= 0) return false;
        if (saldo + monto > 100) return false;
        saldo += monto;
        return true;
    }

    public boolean cobrar(double monto) {
        if (monto <= 0) return false;
        if (saldo < monto) return false;
        saldo -= monto;
        return true;
    }
}
