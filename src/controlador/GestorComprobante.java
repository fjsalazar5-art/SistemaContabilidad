
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

public class GestorComprobante {

    public boolean guardarComprobante(
            String fecha,
            String observacion,
            List<LineaComprobante> lineas) {

        Connection con = null;

        try {

            // VALIDAR DATOS
            if (fecha == null || fecha.trim().isEmpty()) {
                throw new Exception("Ingrese la fecha del comprobante");
            }

            if (observacion == null || observacion.trim().isEmpty()) {
                throw new Exception("Ingrese una observación");
            }

            if (lineas == null || lineas.isEmpty()) {
                throw new Exception("Agregue al menos un detalle");
            }

            Date fechaSQL = Date.valueOf(fecha.trim());

            // VALIDAR Y CALCULAR TOTALES
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

            if (Math.abs(totalDebe - totalHaber) > 0.000001) {
                throw new Exception(
                        "Comprobante descuadrado.\n"
                        + "Total Debe: " + totalDebe
                        + "\nTotal Haber: " + totalHaber);
            }

            // ABRIR UNA SOLA CONEXION
            con = Conexion.conectar();

            if (con == null) {
                throw new Exception("No se pudo conectar con Oracle");
            }

            // INICIAR TRANSACCION
            con.setAutoCommit(false);

            SecuenciaDAO secuenciaDAO = new SecuenciaDAO();

            int idComprobante = secuenciaDAO.siguienteComprobante();

            if (idComprobante <= 0) {
                throw new Exception(
                        "No se pudo generar el número del comprobante");
            }

            // CREAR CABECERA
            Comprobante cabecera = new Comprobante();

            cabecera.setIdComprobante(idComprobante);
            cabecera.setFecha(fechaSQL);
            cabecera.setObservacion(observacion.trim());

            ComprobanteDAO cabeceraDAO = new ComprobanteDAO();

            if (!cabeceraDAO.guardar(con, cabecera)) {
                throw new Exception("No se pudo guardar la cabecera");
            }

            // CREAR DETALLES
            ComprobanteDetalleDAO detalleDAO =
                    new ComprobanteDetalleDAO();

            for (LineaComprobante linea : lineas) {

                int idDetalle = secuenciaDAO.siguienteDetalle();

                if (idDetalle <= 0) {
                    throw new Exception(
                            "No se pudo generar el ID del detalle");
                }

                ComprobanteDetalle detalle =
                        new ComprobanteDetalle();

                detalle.setIdDetalle(idDetalle);
                detalle.setIdComprobante(idComprobante);
                detalle.setIdCuenta(linea.getIdCuenta());
                detalle.setDebe(linea.getDebe());
                detalle.setHaber(linea.getHaber());

                if (!detalleDAO.guardar(con, detalle)) {
                    throw new Exception(
                            "No se pudo guardar uno de los detalles");
                }
            }

            // CONFIRMAR TODA LA TRANSACCION
            con.commit();

            System.out.println(
                    "Comprobante guardado correctamente. ID: "
                    + idComprobante);

            return true;

        } catch (Exception e) {

            // DESHACER OPERACIONES SI EXISTE UN ERROR
            if (con != null) {

                try {
                    con.rollback();

                    System.out.println(
                            "Transacción cancelada. Se ejecutó ROLLBACK.");

                } catch (Exception errorRollback) {
                    errorRollback.printStackTrace();
                }
            }

            System.out.println("Error al guardar comprobante:");
            e.printStackTrace();

            throw new RuntimeException(e.getMessage(), e);

        } finally {

            // CERRAR CONEXION
            if (con != null) {

                try {
                    con.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
