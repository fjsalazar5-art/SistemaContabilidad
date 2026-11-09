package prueba;

import dao.TipoCuentaDAO;
import modelo.TipoCuenta;

public class PruebaModificarTipoCuenta {

    public static void main(String[] args) {

        TipoCuenta tipoCuenta = new TipoCuenta();

        tipoCuenta.setIdTipoCuenta(1);
        tipoCuenta.setNombre("ACTIVO MODIFICADO");

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.modificar(tipoCuenta)) {

            System.out.println("Registro actualizado");

        } else {

            System.out.println("Error");

        }

    }

}