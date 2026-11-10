
package controlador;

import conexion.Conexion;
import dao.ComprobanteDAO;
import dao.ComprobanteDetalleDAO;
import dao.SecuenciaDAO;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import modelo.Comprobante;
import modelo.ComprobanteDetalle;

/**
 * Logica de negocio del comprobante contable.
 * Cada operacion (guardar, modificar, eliminar) se ejecuta en UNA sola
 * transaccion: o se guarda todo, o no se guarda nada.
 */
public class GestorComprobante {

    // ---------------------------------------------------------
    // GUARDAR (comprobante nuevo)
    // ---------------------------------------------------------
    public boolean guardarComprobante(
            String fecha,
            String observacion,
            List<LineaComprobante> lineas) {

        Connection con = null;

        try {

            Date fechaSQL = validar(fecha, observacion, lineas);

            con = abrirTransaccion();

            SecuenciaDAO secuenciaDAO = new SecuenciaDAO();

            int idComprobante = secuenciaDAO.siguienteComprobante(con);

            if (idComprobante <= 0) {
                throw new Exception(
                        "No se pudo generar el número del comprobante");
            }

            Comprobante cabecera = new Comprobante();

            cabecera.setIdComprobante(idComprobante);
            cabecera.setFecha(fechaSQL);
            cabecera.setObservacion(observacion.trim());

            if (!new ComprobanteDAO().guardar(con, cabecera)) {
                throw new Exception("No se pudo guardar la cabecera");
            }

            insertarDetalles(con, idComprobante, lineas);

            con.commit();

            System.out.println(
                    "Comprobante guardado correctamente. ID: "
                    + idComprobante);

            return true;

        } catch (Exception e) {

            deshacer(con, e, "guardar");

            throw new RuntimeException(e.getMessage(), e);

        } finally {

            cerrar(con);
        }
    }

    // ---------------------------------------------------------
    // MODIFICAR (comprobante existente)
    // Actualiza la cabecera y reemplaza todas sus lineas.
    // ---------------------------------------------------------
    public boolean modificarComprobante(
            int idComprobante,
            String fecha,
            String observacion,
            List<LineaComprobante> lineas) {

        Connection con = null;

        try {

            if (idComprobante <= 0) {
                throw new Exception(
                        "Primero busque el comprobante que desea modificar");
            }

            Date fechaSQL = validar(fecha, observacion, lineas);

            con = abrirTransaccion();

            Comprobante cabecera = new Comprobante();

            cabecera.setIdComprobante(idComprobante);
            cabecera.setFecha(fechaSQL);
            cabecera.setObservacion(observacion.trim());

            if (!new ComprobanteDAO().actualizar(con, cabecera)) {
                throw new Exception(
                        "El comprobante " + idComprobante
                        + " no existe o no se pudo actualizar");
            }

            // Se reemplazan las lineas: se borran las anteriores
            // y se insertan las que estan en pantalla.
            new ComprobanteDetalleDAO()
                    .eliminarPorComprobante(con, idComprobante);

            insertarDetalles(con, idComprobante, lineas);

            con.commit();

            System.out.println(
                    "Comprobante modificado correctamente. ID: "
                    + idComprobante);

            return true;

        } catch (Exception e) {

            deshacer(con, e, "modificar");

            throw new RuntimeException(e.getMessage(), e);

        } finally {

            cerrar(con);
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR (cabecera y todos sus detalles)
    // ---------------------------------------------------------
    public boolean eliminarComprobante(int idComprobante) {

        Connection con = null;

        try {

            if (idComprobante <= 0) {
                throw new Exception(
                        "Primero busque el comprobante que desea eliminar");
            }

            con = abrirTransaccion();

            // Primero los detalles (llave foranea), luego la cabecera
            new ComprobanteDetalleDAO()
                    .eliminarPorComprobante(con, idComprobante);

            if (!new ComprobanteDAO().eliminar(con, idComprobante)) {
                throw new Exception(
                        "El comprobante " + idComprobante
                        + " no existe o no se pudo eliminar");
            }

            con.commit();

            System.out.println(
                    "Comprobante eliminado correctamente. ID: "
                    + idComprobante);

            return true;

        } catch (Exception e) {

            deshacer(con, e, "eliminar");

            throw new RuntimeException(e.getMessage(), e);

        } finally {

            cerrar(con);
        }
    }

    // ---------------------------------------------------------
    // VALIDACIONES COMUNES
    // Devuelve la fecha ya convertida si todo es correcto.
    // ---------------------------------------------------------
    private Date validar(
            String fecha,
            String observacion,
            List<LineaComprobante> lineas) throws Exception {

        if (fecha == null || fecha.trim().isEmpty()) {
            throw new Exception("Ingrese la fecha del comprobante");
        }

        if (observacion == null || observacion.trim().isEmpty()) {
            throw new Exception("Ingrese una observación");
        }

        if (lineas == null || lineas.isEmpty()) {
            throw new Exception("Agregue al menos un detalle");
        }

        Date fechaSQL;

        try {
            fechaSQL = Date.valueOf(fecha.trim());
        } catch (IllegalArgumentException e) {
            throw new Exception(
                    "Fecha inválida. Use el formato yyyy-MM-dd "
                    + "(ejemplo: 2026-11-09)");
        }

        double totalDebe = 0;
        double totalHaber = 0;

        for (LineaComprobante linea : lineas) {

            if (linea.getIdCuenta() <= 0) {
                throw new Exception("Existe una cuenta no válida");
            }

            if (linea.getDebe() < 0 || linea.getHaber() < 0) {
                throw new Exception("Los valores no pueden ser negativos");
            }

            if (linea.getDebe() > 0 && linea.getHaber() > 0) {
                throw new Exception(
                        "Una línea no puede tener Debe y Haber simultáneamente");
            }

            if (linea.getDebe() == 0 && linea.getHaber() == 0) {
                throw new Exception(
                        "Una línea debe tener un valor en Debe o Haber");
            }

            totalDebe += linea.getDebe();
            totalHaber += linea.getHaber();
        }

        if (totalDebe <= 0 || totalHaber <= 0) {
            throw new Exception(
                    "El comprobante debe tener valores en Debe y Haber");
        }

        if (Math.abs(totalDebe - totalHaber) > 0.005) {
            throw new Exception(
                    "Comprobante descuadrado.\n"
                    + "Total Debe: " + String.format("%.2f", totalDebe)
                    + "\nTotal Haber: " + String.format("%.2f", totalHaber));
        }

        return fechaSQL;
    }

    // ---------------------------------------------------------
    // UTILIDADES INTERNAS
    // ---------------------------------------------------------
    private void insertarDetalles(
            Connection con,
            int idComprobante,
            List<LineaComprobante> lineas) throws Exception {

        SecuenciaDAO secuenciaDAO = new SecuenciaDAO();
        ComprobanteDetalleDAO detalleDAO = new ComprobanteDetalleDAO();

        for (LineaComprobante linea : lineas) {

            int idDetalle = secuenciaDAO.siguienteDetalle(con);

            if (idDetalle <= 0) {
                throw new Exception("No se pudo generar el ID del detalle");
            }

            ComprobanteDetalle detalle = new ComprobanteDetalle();

            detalle.setIdDetalle(idDetalle);
            detalle.setIdComprobante(idComprobante);
            detalle.setIdCuenta(linea.getIdCuenta());
            detalle.setDebe(linea.getDebe());
            detalle.setHaber(linea.getHaber());

            if (!detalleDAO.guardar(con, detalle)) {
                throw new Exception("No se pudo guardar uno de los detalles");
            }
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

    private void deshacer(Connection con, Exception causa, String accion) {

        if (con != null) {

            try {
                con.rollback();
                System.out.println(
                        "Transacción cancelada. Se ejecutó ROLLBACK.");
            } catch (Exception errorRollback) {
                errorRollback.printStackTrace();
            }
        }

        System.out.println("Error al " + accion + " comprobante:");
        causa.printStackTrace();
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
