package prueba;

import dao.ComprobanteDAO;
import dao.SecuenciaDAO;
import java.sql.Date;
import modelo.Comprobante;

public class PruebaGuardarComprobanteAutomatico {

    public static void main(String[] args) {

        SecuenciaDAO sec =
                new SecuenciaDAO();

        int nuevoId =
                sec.siguienteComprobante();

        Comprobante c =
                new Comprobante();

        c.setIdComprobante(nuevoId);

        c.setFecha(
                Date.valueOf("2026-11-08"));

        c.setObservacion(
                "COMPROBANTE AUTOMATICO");

        ComprobanteDAO dao =
                new ComprobanteDAO();

        if (dao.guardar(c)) {

            System.out.println(
                    "Guardado ID: "
                    + nuevoId);

        } else {

            System.out.println(
                    "Error");
        }

    }
}