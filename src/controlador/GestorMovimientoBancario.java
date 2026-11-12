package controlador;

import conexion.Conexion;
import dao.MovimientoBancarioDAO;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import modelo.MovimientoBancario;
import modelo.MovimientoBancarioDetalle;

/**
 * Logica de negocio del Movimiento Bancario (pantalla compleja).
 * Guardar, modificar y eliminar se ejecutan en UNA sola transaccion:
 * cabecera y detalle se guardan juntos o no se guarda nada (ROLLBACK).
 */
public class GestorMovimientoBancario {

    private final MovimientoBancarioDAO dao = new MovimientoBancarioDAO();

    // GUARDAR: devuelve el numero de movimiento generado
    public int guardar(MovimientoBancario cabecera,
            List<MovimientoBancarioDetalle> detalles) {

        validar(cabecera, detalles);

        Connection con = null;

        try {
            con = abrirTransaccion();

            int id = dao.siguienteMovimiento(con);
            cabecera.setIdMovimiento(id);

            dao.guardarCabecera(con, cabecera);
            insertarDetalles(con, id, detalles);

            con.commit();
            return id;

        } catch (Exception e) {
            deshacer(con);
            throw new RuntimeException(
                    "No se pudo guardar el movimiento: " + e.getMessage(), e);
        } finally {
            cerrar(con);
        }
    }

    // MODIFICAR: actualiza cabecera y reemplaza todo el detalle
    public void modificar(MovimientoBancario cabecera,
            List<MovimientoBancarioDetalle> detalles) {

        if (cabecera.getIdMovimiento() <= 0) {
            throw new RuntimeException("Primero busque el movimiento a modificar");
        }

        validar(cabecera, detalles);

        Connection con = null;

        try {
            con = abrirTransaccion();

            if (!dao.actualizarCabecera(con, cabecera)) {
                throw new Exception("El movimiento " + cabecera.getIdMovimiento()
                        + " no existe");
            }

            dao.eliminarDetalles(con, cabecera.getIdMovimiento());
            insertarDetalles(con, cabecera.getIdMovimiento(), detalles);

            con.commit();

        } catch (Exception e) {
            deshacer(con);
            throw new RuntimeException(
                    "No se pudo modificar el movimiento: " + e.getMessage(), e);
        } finally {
            cerrar(con);
        }
    }

    // ELIMINAR: primero el detalle (llave foranea) y luego la cabecera
    public void eliminar(int idMovimiento) {

        if (idMovimiento <= 0) {
            throw new RuntimeException("Primero busque el movimiento a eliminar");
        }

        Connection con = null;

        try {
            con = abrirTransaccion();

            dao.eliminarDetalles(con, idMovimiento);

            if (!dao.eliminarCabecera(con, idMovimiento)) {
                throw new Exception("El movimiento " + idMovimiento + " no existe");
            }

            con.commit();

        } catch (Exception e) {
            deshacer(con);
            throw new RuntimeException(
                    "No se pudo eliminar el movimiento: " + e.getMessage(), e);
        } finally {
            cerrar(con);
        }
    }

    // ---------------- utilidades ----------------

    private void validar(MovimientoBancario c, List<MovimientoBancarioDetalle> detalles) {

        if (c.getFecha() == null) {
            throw new RuntimeException("Ingrese la fecha del movimiento");
        }
        if (c.getIdCuentaBanco() <= 0) {
            throw new RuntimeException("Seleccione la cuenta bancaria");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new RuntimeException("Agregue al menos una línea de detalle");
        }
        for (MovimientoBancarioDetalle d : detalles) {
            if (d.getIdTipoTransaccion() <= 0) {
                throw new RuntimeException("Existe una línea sin tipo de transacción");
            }
            if (d.getValor() <= 0) {
                throw new RuntimeException("La cantidad (valor) debe ser mayor a cero");
            }
        }
    }

    private void insertarDetalles(Connection con, int idMovimiento,
            List<MovimientoBancarioDetalle> detalles) throws Exception {

        for (MovimientoBancarioDetalle d : detalles) {
            d.setIdMovDet(dao.siguienteDetalle(con));
            d.setIdMovimiento(idMovimiento);
            if (d.getFecha() == null) {
                d.setFecha(new Date(System.currentTimeMillis()));
            }
            dao.guardarDetalle(con, d);
        }
    }

    private Connection abrirTransaccion() throws Exception {
        Connection con = Conexion.conectar();
        if (con == null) {
            throw new Exception("No se pudo conectar con Oracle");
        }
        con.setAutoCommit(false);
        return con;
    }

    private void deshacer(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                System.out.println("Transacción cancelada. Se ejecutó ROLLBACK.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cerrar(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
