package prueba;

import dao.CuentaContableDAO;
import java.util.List;
import modelo.CuentaContable;

public class PruebaListarCuentas {

    public static void main(String[] args) {

        CuentaContableDAO dao =
                new CuentaContableDAO();

        List<CuentaContable> lista =
                dao.listar();

        for (CuentaContable c : lista) {

            System.out.println(
                    c.getIdCuenta() + " - " +
                    c.getNombre());
        }

    }

}