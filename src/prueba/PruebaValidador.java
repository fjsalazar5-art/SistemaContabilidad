package prueba;

import controlador.ValidadorContable;
import java.util.ArrayList;
import java.util.List;
import modelo.ComprobanteDetalle;

public class PruebaValidador {

    public static void main(String[] args) {

        List<ComprobanteDetalle> detalles =
                new ArrayList<>();

        ComprobanteDetalle d1 =
                new ComprobanteDetalle();

        d1.setDebe(100);
        d1.setHaber(0);

        ComprobanteDetalle d2 =
                new ComprobanteDetalle();

        d2.setDebe(0);
        d2.setHaber(100);

        detalles.add(d1);
        detalles.add(d2);

        if (ValidadorContable
                .comprobanteCuadrado(detalles)) {

            System.out.println(
                    "Comprobante cuadrado");

        } else {

            System.out.println(
                    "Comprobante descuadrado");
        }

    }

}