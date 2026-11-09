
package modelo;

public class ResultadoCuenta {

    private int idCuenta;
    private String nombreCuenta;
    private String tipoCuenta;
    private double totalDebe;
    private double totalHaber;
    private double saldo;

    public ResultadoCuenta() {
    }

    public ResultadoCuenta(
            int idCuenta,
            String nombreCuenta,
            String tipoCuenta,
            double totalDebe,
            double totalHaber,
            double saldo) {

        this.idCuenta = idCuenta;
        this.nombreCuenta = nombreCuenta;
        this.tipoCuenta = tipoCuenta;
        this.totalDebe = totalDebe;
        this.totalHaber = totalHaber;
        this.saldo = saldo;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public double getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(double totalDebe) {
        this.totalDebe = totalDebe;
    }

    public double getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(double totalHaber) {
        this.totalHaber = totalHaber;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
