package prueba;

import controlador.GestorComprobante;
import controlador.LineaComprobante;
import java.util.ArrayList;
import java.util.List;

public class PruebaGuardarCompleto {

    public static void main(String[] args) {

        List<LineaComprobante> lineas =
                new ArrayList<>();

        lineas.add(
                new LineaComprobante(
                        1,
                        "CAJA",
                        200,
                        0));

        lineas.add(
                new LineaComprobante(
                        2,
                        "VENTAS",
                        0,
                        200));

        GestorComprobante gestor =
                new GestorComprobante();

        if (gestor.guardarComprobante(
                "2026-11-08",
                "PRUEBA COMPLETA",
                lineas)) {

            System.out.println(
                    "Comprobante guardado");

        } else {

            System.out.println(
                    "Error");
        }

    }
}