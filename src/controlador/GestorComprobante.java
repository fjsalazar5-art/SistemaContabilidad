package controlador;

import dao.ComprobanteDAO;
import dao.ComprobanteDetalleDAO;
import dao.SecuenciaDAO;
import java.sql.Date;
import java.util.List;
import modelo.Comprobante;
import modelo.ComprobanteDetalle;

public class GestorComprobante {

    public boolean guardarComprobante(
            String fecha,
            String observacion,
            List<LineaComprobante> lineas) {

        try {

            double totalDebe = 0;
            double totalHaber = 0;

            for (LineaComprobante l : lineas) {

                totalDebe += l.getDebe();
                totalHaber += l.getHaber();

            }

            if (totalDebe != totalHaber) {

                System.out.println(
                        "Comprobante descuadrado");

                return false;
            }

            SecuenciaDAO sec =
                    new SecuenciaDAO();

            int idComprobante =
                    sec.siguienteComprobante();

            Comprobante cabecera =
                    new Comprobante();

            cabecera.setIdComprobante(
                    idComprobante);

            cabecera.setFecha(
                    Date.valueOf(fecha));

            cabecera.setObservacion(
                    observacion);

            ComprobanteDAO daoCabecera =
                    new ComprobanteDAO();

            daoCabecera.guardar(cabecera);

            ComprobanteDetalleDAO daoDetalle =
                    new ComprobanteDetalleDAO();

            for (LineaComprobante l : lineas) {

                ComprobanteDetalle d =
                        new ComprobanteDetalle();

                d.setIdDetalle(
                        sec.siguienteDetalle());

                d.setIdComprobante(
                        idComprobante);

                d.setIdCuenta(
                        l.getIdCuenta());

                d.setDebe(
                        l.getDebe());

                d.setHaber(
                        l.getHaber());

                daoDetalle.guardar(d);

            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}