package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BalanceGeneralDAO {

    public void mostrarBalance() {

        try {

            Connection con =
                    Conexion.conectar();

            String sql = """
                    SELECT
                    TC.NOMBRE TIPO_CUENTA,
                    NVL(SUM(CD.DEBE),0) TOTAL_DEBE,
                    NVL(SUM(CD.HABER),0) TOTAL_HABER
                    FROM TIPO_CUENTA TC
                    LEFT JOIN CUENTA_CONTABLE CC
                    ON TC.ID_TIPO_CUENTA =
                    CC.ID_TIPO_CUENTA
                    LEFT JOIN COMPROBANTE_DETALLE CD
                    ON CC.ID_CUENTA =
                    CD.ID_CUENTA
                    GROUP BY TC.NOMBRE
                    ORDER BY TC.NOMBRE
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            System.out.println(
                    "===== BALANCE GENERAL =====");

            while (rs.next()) {

                System.out.println(

                        rs.getString("TIPO_CUENTA")
                        + " | DEBE: "
                        + rs.getDouble("TOTAL_DEBE")
                        + " | HABER: "
                        + rs.getDouble("TOTAL_HABER")
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}