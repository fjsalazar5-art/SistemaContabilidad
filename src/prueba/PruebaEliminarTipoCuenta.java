package prueba;

import dao.TipoCuentaDAO;

public class PruebaEliminarTipoCuenta {

    public static void main(String[] args) {

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.eliminar(1)) {

            System.out.println("Registro eliminado");

        } else {

            System.out.println("Error");

        }

    }

}