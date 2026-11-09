
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecuenciaDAO {

    // OBTENER SIGUIENTE ID DE COMPROBANTE
    // UTILIZANDO LA CONEXION DE LA TRANSACCION
    public int siguienteComprobante(Connection con)
            throws SQLException {

        String sql =
                "SELECT SEQ_COMPROBANTE.NEXTVAL FROM DUAL";

        return obtenerSiguienteValor(con, sql);
    }

    // OBTENER SIGUIENTE ID DE DETALLE
    // UTILIZANDO LA CONEXION DE LA TRANSACCION
    public int siguienteDetalle(Connection con)
            throws SQLException {

        String sql =
                "SELECT SEQ_COMPROBANTE_DETALLE.NEXTVAL FROM DUAL";

        return obtenerSiguienteValor(con, sql);
    }

    // METODO GENERAL PARA OBTENER EL SIGUIENTE VALOR
    private int obtenerSiguienteValor(
            Connection con,
            String sql) throws SQLException {

        if (con == null) {
            throw new SQLException(
                    "La conexión con Oracle no está disponible");
        }

        try (
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException(
                    "No se pudo obtener el siguiente valor de la secuencia");
        }
    }

    // METODO ORIGINAL PARA CONSERVAR COMPATIBILIDAD
    public int siguienteComprobante() {

        try (Connection con = Conexion.conectar()) {

            return siguienteComprobante(con);

        } catch (Exception e) {

            System.out.println(
                    "Error al obtener secuencia de comprobante:");

            e.printStackTrace();

            return -1;
        }
    }

    // METODO ORIGINAL PARA CONSERVAR COMPATIBILIDAD
    public int siguienteDetalle() {

        try (Connection con = Conexion.conectar()) {

            return siguienteDetalle(con);

        } catch (Exception e) {

            System.out.println(
                    "Error al obtener secuencia de detalle:");

            e.printStackTrace();

            return -1;
        }
    }
}
