package modelo;

/**
 * Tipo de transaccion bancaria (Deposito, Retiro, Nota de Credito...).
 * SIGNO '+' suma al saldo de la cuenta, '-' resta.
 */
public class TipoTransaccion {

    private int idTipoTransaccion;
    private String nombre;
    private String signo;

    public TipoTransaccion() {
    }

    public TipoTransaccion(int idTipoTransaccion, String nombre, String signo) {
        this.idTipoTransaccion = idTipoTransaccion;
        this.nombre = nombre;
        this.signo = signo;
    }

    public int getIdTipoTransaccion() {
        return idTipoTransaccion;
    }

    public void setIdTipoTransaccion(int idTipoTransaccion) {
        this.idTipoTransaccion = idTipoTransaccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigno() {
        return signo;
    }

    public void setSigno(String signo) {
        this.signo = signo;
    }

    @Override
    public String toString() {
        return idTipoTransaccion + " - " + nombre + " (" + signo + ")";
    }
}
