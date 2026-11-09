package controlador;

import java.util.List;
import modelo.ComprobanteDetalle;

public class ValidadorContable {

    public static boolean comprobanteCuadrado(
            List<ComprobanteDetalle> detalles) {

        double totalDebe = 0;
        double totalHaber = 0;

        for (ComprobanteDetalle d : detalles) {

            totalDebe += d.getDebe();
            totalHaber += d.getHaber();

        }

        return totalDebe == totalHaber;
    }

}