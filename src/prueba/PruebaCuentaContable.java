package prueba;

import dao.CuentaContableDAO;
import modelo.CuentaContable;

public class PruebaCuentaContable {

    public static void main(String[] args) {

        CuentaContable cuenta =
                new CuentaContable();

        cuenta.setIdCuenta(1);
        cuenta.setNombre("CAJA");
        cuenta.setIdTipoCuenta(1);

        CuentaContableDAO dao =
                new CuentaContableDAO();

        if (dao.guardar(cuenta)) {

            System.out.println(
                    "Cuenta guardada");

        } else {

            System.out.println(
                    "Error");
        }

    }
}