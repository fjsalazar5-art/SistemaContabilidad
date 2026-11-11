package modelo;

public class CuentaBancaria {

    private int idCuentaBanco;
    private String numeroCuenta;
    private String cliente;
    private String descripcion;

    public CuentaBancaria() {
    }

    public CuentaBancaria(int idCuentaBanco, String numeroCuenta,
            String cliente, String descripcion) {
        this.idCuentaBanco = idCuentaBanco;
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
        this.descripcion = descripcion;
    }

    public int getIdCuentaBanco() {
        return idCuentaBanco;
    }

    public void setIdCuentaBanco(int idCuentaBanco) {
        this.idCuentaBanco = idCuentaBanco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return idCuentaBanco + " - " + descripcion + " (" + numeroCuenta + ")";
    }
}
