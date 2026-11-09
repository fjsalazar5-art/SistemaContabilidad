package prueba;

import dao.ComprobanteDetalleDAO;
import modelo.ComprobanteDetalle;

public class PruebaDetalleComprobante {

    public static void main(String[] args) {

        ComprobanteDetalle detalle =
                new ComprobanteDetalle();

        detalle.setIdDetalle(1);

        detalle.setIdComprobante(1);

        detalle.setIdCuenta(1);

        detalle.setDebe(100);

        detalle.setHaber(0);

        ComprobanteDetalleDAO dao =
                new ComprobanteDetalleDAO();

        if (dao.guardar(detalle)) {

            System.out.println(
                    "Detalle guardado");

        } else {

            System.out.println(
                    "Error");

        }

    }
}