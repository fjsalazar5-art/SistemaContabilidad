
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import modelo.Comprobante;

public class ComprobanteDAO {

    // GUARDAR UTILIZANDO UNA CONEXION EXISTENTE
    public boolean guardar(Connection con, Comprobante comprobante) {

        String sql = """
                INSERT INTO COMPROBANTE
                (ID_COMPROBANTE, FECHA, OBSERVACION)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, comprobante.getIdComprobante());
            ps.setDate(2, comprobante.getFecha());
            ps.setString(3, comprobante.getObservacion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error al guardar cabecera:");
            e.printStackTrace();
            return false;
        }
    }

    // METODO ORIGINAL PARA CONSERVAR COMPATIBILIDAD
    public boolean guardar(Comprobante comprobante) {

        try (Connection con = Conexion.conectar()) {

            return guardar(con, comprobante);

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    // BUSCAR COMPROBANTE
    public Comprobante buscar(int id) {

        String sql = """
                SELECT *
                FROM COMPROBANTE
                WHERE ID_COMPROBANTE = ?
                """;

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Comprobante c = new Comprobante();

                    c.setIdComprobante(
                            rs.getInt("ID_COMPROBANTE"));

                    c.setFecha(
                            rs.getDate("FECHA"));

                    c.setObservacion(
                            rs.getString("OBSERVACION"));

                    return c;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // ELIMINAR COMPROBANTE
    public boolean eliminar(int id) {

        String sql = """
                DELETE FROM COMPROBANTE
                WHERE ID_COMPROBANTE = ?
                """;

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
}
