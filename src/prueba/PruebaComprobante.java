package prueba;

import dao.ComprobanteDAO;
import java.sql.Date;
import modelo.Comprobante;

public class PruebaComprobante {

    public static void main(String[] args) {

        Comprobante c = new Comprobante();

        c.setIdComprobante(1);

        c.setFecha(
                Date.valueOf("2026-11-08"));

        c.setObservacion(
                "ASIENTO DE PRUEBA");

        ComprobanteDAO dao =
                new ComprobanteDAO();

        if (dao.guardar(c)) {

            System.out.println(
                    "Comprobante guardado");

        } else {

            System.out.println(
                    "Error");

        }

    }
}