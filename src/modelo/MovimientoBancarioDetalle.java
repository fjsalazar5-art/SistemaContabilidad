package modelo;

import java.sql.Date;

/**
 * Linea del movimiento bancario (tabla MOV_BANCARIO_DET).
 * VALOR es la "cantidad" monetaria de la transaccion.
 */
public class MovimientoBancarioDetalle {

    private int idMovDet;
    private int idMovimiento;
    private Date fecha;
    private int idTipoTransaccion;
    private double valor;

    // Datos de apoyo para mostrar en pantalla (no se guardan)
    private String nombreTipo;
    private String signo;

    public MovimientoBancarioDetalle() {
    }

    public int getIdMovDet() {
        return idMovDet;
    }

    public void setIdMovDet(int idMovDet) {
        this.idMovDet = idMovDet;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdTipoTransaccion() {
        return idTipoTransaccion;
    }

    public void setIdTipoTransaccion(int idTipoTransaccion) {
        this.idTipoTransaccion = idTipoTransaccion;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getSigno() {
        return signo;
    }

    public void setSigno(String signo) {
        this.signo = signo;
    }
}
