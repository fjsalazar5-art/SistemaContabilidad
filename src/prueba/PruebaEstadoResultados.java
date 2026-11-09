package prueba;

import dao.EstadoResultadosDAO;

public class PruebaEstadoResultados {

    public static void main(String[] args) {

        EstadoResultadosDAO estado =
                new EstadoResultadosDAO();

        estado.mostrarEstadoResultados();

    }

}