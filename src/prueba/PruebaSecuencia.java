package prueba;

import dao.SecuenciaDAO;

public class PruebaSecuencia {

    public static void main(String[] args) {

        SecuenciaDAO dao =
                new SecuenciaDAO();

        System.out.println(
                dao.siguienteComprobante());

        System.out.println(
                dao.siguienteDetalle());
    }
}
