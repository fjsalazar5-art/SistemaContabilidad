package prueba;

import dao.TipoCuentaDAO;
import modelo.TipoCuenta;

public class PruebaBuscarTipoCuenta {

    public static void main(String[] args) {

        TipoCuentaDAO dao = new TipoCuentaDAO();

        TipoCuenta tipoCuenta = dao.buscar(1);

        if (tipoCuenta != null) {

            System.out.println(
                    tipoCuenta.getIdTipoCuenta());

            System.out.println(
                    tipoCuenta.getNombre());

        } else {

            System.out.println("No existe");

        }

    }

}