package prueba;

import dao.TipoCuentaDAO;
import modelo.TipoCuenta;

public class PruebaTipoCuenta {

    public static void main(String[] args) {

        TipoCuenta tipoCuenta = new TipoCuenta();

        tipoCuenta.setIdTipoCuenta(1);
        tipoCuenta.setNombre("ACTIVO");

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.guardar(tipoCuenta)) {
            System.out.println("Registro guardado");
        } else {
            System.out.println("Error al guardar");
        }

    }
}