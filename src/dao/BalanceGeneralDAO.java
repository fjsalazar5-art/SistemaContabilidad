
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.SaldoCuenta;

public class BalanceGeneralDAO {

    public List<SaldoCuenta> obtenerBalance(Date fechaCorte) {

        List<SaldoCuenta> lista = new ArrayList<>();

        if (fechaCorte == null) {
            throw new IllegalArgumentException(
                    "La fecha de corte es obligatoria."
            );
        }

        String sql = """
                SELECT
                    CC.ID_CUENTA,
                    CC.NOMBRE AS CUENTA,
                    {TIPO} AS TIPO_CUENTA,
                    NVL(SUM(MOV.DEBE), 0) AS TOTAL_DEBE,
                    NVL(SUM(MOV.HABER), 0) AS TOTAL_HABER,
                    CASE
                        WHEN {TIPO} = 'ACTIVO'
                            THEN NVL(SUM(MOV.DEBE), 0)
                               - NVL(SUM(MOV.HABER), 0)
                        WHEN {TIPO} IN ('PASIVO', 'PATRIMONIO')
                            THEN NVL(SUM(MOV.HABER), 0)
                               - NVL(SUM(MOV.DEBE), 0)
                        ELSE 0
                    END AS SALDO
                FROM CUENTA_CONTABLE CC
                INNER JOIN TIPO_CUENTA TC
                    ON CC.ID_TIPO_CUENTA = TC.ID_TIPO_CUENTA
                LEFT JOIN (
                    SELECT
                        CD.ID_CUENTA,
                        CD.DEBE,
                        CD.HABER
                    FROM COMPROBANTE_DETALLE CD
                    INNER JOIN COMPROBANTE C
                        ON C.ID_COMPROBANTE = CD.ID_COMPROBANTE
                    WHERE C.FECHA < ? + 1
                ) MOV
                    ON CC.ID_CUENTA = MOV.ID_CUENTA
                WHERE {TIPO} IN (
                    'ACTIVO',
                    'PASIVO',
                    'PATRIMONIO'
                )
                GROUP BY
                    CC.ID_CUENTA,
                    CC.NOMBRE,
                    {TIPO}
                ORDER BY
                    {TIPO},
                    CC.NOMBRE
                """;

        try {
            Connection con = Conexion.conectar();

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con Oracle."
                );
            }

            try (
                    con;
                    PreparedStatement ps = con.prepareStatement(TipoCuentaSql.aplicar(sql))
            ) {

                ps.setDate(1, fechaCorte);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        SaldoCuenta saldo = new SaldoCuenta();

                        saldo.setIdCuenta(rs.getInt("ID_CUENTA"));
                        saldo.setNombreCuenta(rs.getString("CUENTA"));
                        saldo.setTipoCuenta(rs.getString("TIPO_CUENTA"));
                        saldo.setTotalDebe(rs.getDouble("TOTAL_DEBE"));
                        saldo.setTotalHaber(rs.getDouble("TOTAL_HABER"));
                        saldo.setSaldo(rs.getDouble("SALDO"));

                        lista.add(saldo);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al obtener el Balance General: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "No se pudo generar el Balance General.",
                    e
            );
        }

        return lista;
    }

    public double obtenerResultadoEjercicio(Date fechaCorte) {

        if (fechaCorte == null) {
            throw new IllegalArgumentException(
                    "La fecha de corte es obligatoria."
            );
        }

        String sql = """
                SELECT
                    NVL(
                        SUM(
                            CASE
                                WHEN {TIPO} IN ('INGRESO', 'GASTO')
                                    THEN NVL(CD.HABER, 0)
                                       - NVL(CD.DEBE, 0)
                                ELSE 0
                            END
                        ),
                        0
                    ) AS RESULTADO
                FROM COMPROBANTE C
                INNER JOIN COMPROBANTE_DETALLE CD
                    ON C.ID_COMPROBANTE = CD.ID_COMPROBANTE
                INNER JOIN CUENTA_CONTABLE CC
                    ON CD.ID_CUENTA = CC.ID_CUENTA
                INNER JOIN TIPO_CUENTA TC
                    ON CC.ID_TIPO_CUENTA = TC.ID_TIPO_CUENTA
                WHERE C.FECHA < ? + 1
                  AND {TIPO} IN ('INGRESO', 'GASTO')
                """;

        try {
            Connection con = Conexion.conectar();

            if (con == null) {
                throw new SQLException(
                        "No se pudo conectar con Oracle."
                );
            }

            try (
                    con;
                    PreparedStatement ps = con.prepareStatement(TipoCuentaSql.aplicar(sql))
            ) {

                ps.setDate(1, fechaCorte);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return rs.getDouble("RESULTADO");
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al calcular el resultado del ejercicio: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "No se pudo calcular el resultado del ejercicio.",
                    e
            );
        }

        return 0;
    }
}
