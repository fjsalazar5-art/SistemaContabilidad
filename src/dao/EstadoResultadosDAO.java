
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.ResultadoCuenta;

public class EstadoResultadosDAO {

    public List<ResultadoCuenta> obtenerEstadoResultados(
            Date fechaInicio,
            Date fechaFin) {

        List<ResultadoCuenta> lista = new ArrayList<>();

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Las fechas son obligatorias."
            );
        }

        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final."
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
                        WHEN {TIPO} = 'INGRESO'
                            THEN NVL(SUM(MOV.HABER), 0)
                               - NVL(SUM(MOV.DEBE), 0)
                        WHEN {TIPO} = 'GASTO'
                            THEN NVL(SUM(MOV.DEBE), 0)
                               - NVL(SUM(MOV.HABER), 0)
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
                    WHERE C.FECHA >= ?
                      AND C.FECHA < ? + 1
                ) MOV
                    ON CC.ID_CUENTA = MOV.ID_CUENTA
                WHERE {TIPO} IN ('INGRESO', 'GASTO')
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

                ps.setDate(1, fechaInicio);
                ps.setDate(2, fechaFin);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        ResultadoCuenta resultado =
                                new ResultadoCuenta();

                        resultado.setIdCuenta(
                                rs.getInt("ID_CUENTA")
                        );

                        resultado.setNombreCuenta(
                                rs.getString("CUENTA")
                        );

                        resultado.setTipoCuenta(
                                rs.getString("TIPO_CUENTA")
                        );

                        resultado.setTotalDebe(
                                rs.getDouble("TOTAL_DEBE")
                        );

                        resultado.setTotalHaber(
                                rs.getDouble("TOTAL_HABER")
                        );

                        resultado.setSaldo(
                                rs.getDouble("SALDO")
                        );

                        lista.add(resultado);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener el Estado de Resultados: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "No se pudo generar el Estado de Resultados.",
                    e
            );
        }

        return lista;
    }
}
