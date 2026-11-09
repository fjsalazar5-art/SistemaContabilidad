package modelo;

public class CuentaContable {

    private int idCuenta;
    private String nombre;
    private int idTipoCuenta;

    public CuentaContable() {
    }

    public CuentaContable(int idCuenta, String nombre, int idTipoCuenta) {
        this.idCuenta = idCuenta;
        this.nombre = nombre;
        this.idTipoCuenta = idTipoCuenta;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdTipoCuenta() {
        return idTipoCuenta;
    }

    public void setIdTipoCuenta(int idTipoCuenta) {
        this.idTipoCuenta = idTipoCuenta;
    }

    @Override
    public String toString() {
        return nombre;
    }
}