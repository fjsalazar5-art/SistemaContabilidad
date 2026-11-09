package controlador;

public class LineaComprobante {

    private int idCuenta;
    private String cuenta;
    private double debe;
    private double haber;

    public LineaComprobante() {
    }

    public LineaComprobante(int idCuenta,
                            String cuenta,
                            double debe,
                            double haber) {

        this.idCuenta = idCuenta;
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getDebe() {
        return debe;
    }

    public void setDebe(double debe) {
        this.debe = debe;
    }

    public double getHaber() {
        return haber;
    }

    public void setHaber(double haber) {
        this.haber = haber;
    }

}