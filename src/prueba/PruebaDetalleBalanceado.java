package prueba;

import controlador.LineaComprobante;
import java.util.ArrayList;
import java.util.List;

public class PruebaDetalleBalanceado {

    public static void main(String[] args) {

        List<LineaComprobante> lista =
                new ArrayList<>();

        lista.add(
                new LineaComprobante(
                        1,
                        "CAJA",
                        100,
                        0));

        lista.add(
                new LineaComprobante(
                        2,
                        "VENTAS",
                        0,
                        100));

        double debe = 0;
        double haber = 0;

        for (LineaComprobante l : lista) {

            debe += l.getDebe();
            haber += l.getHaber();

        }

        System.out.println(
                "Debe: " + debe);

        System.out.println(
                "Haber: " + haber);

        if (debe == haber) {

            System.out.println(
                    "Comprobante cuadrado");

        } else {

            System.out.println(
                    "Comprobante descuadrado");
        }
    }
}