package modelo;

import java.sql.Date;

/** Cabecera del movimiento bancario (tabla MOV_BANCARIO). */
public class MovimientoBancario {

    private int idMovimiento;
    private Date fecha;
    private int idCuentaBanco;
    private String descripcion;

    public MovimientoBancario() {
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

    public int getIdCuentaBanco() {
        return idCuentaBanco;
    }

    public void setIdCuentaBanco(int idCuentaBanco) {
        this.idCuentaBanco = idCuentaBanco;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
