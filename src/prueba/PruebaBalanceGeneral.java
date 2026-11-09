package prueba;

import dao.BalanceGeneralDAO;

public class PruebaBalanceGeneral {

    public static void main(String[] args) {

        BalanceGeneralDAO balance =
                new BalanceGeneralDAO();

        balance.mostrarBalance();

    }

}