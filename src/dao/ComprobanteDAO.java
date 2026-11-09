package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import modelo.Comprobante;

public class ComprobanteDAO {

    public boolean guardar(Comprobante comprobante) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    INSERT INTO COMPROBANTE
                    (ID_COMPROBANTE, FECHA, OBSERVACION)
                    VALUES (?, ?, ?)
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1,
                    comprobante.getIdComprobante());

            ps.setDate(2,
                    comprobante.getFecha());

            ps.setString(3,
                    comprobante.getObservacion());

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    public Comprobante buscar(int id) {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    """
                    SELECT *
                    FROM COMPROBANTE
                    WHERE ID_COMPROBANTE = ?
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                Comprobante c =
                        new Comprobante();

                c.setIdComprobante(
                        rs.getInt("ID_COMPROBANTE"));

                c.setFecha(
                        rs.getDate("FECHA"));

                c.setObservacion(
                        rs.getString("OBSERVACION"));

                return c;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }

    public boolean eliminar(int id) {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    """
                    DELETE FROM COMPROBANTE
                    WHERE ID_COMPROBANTE = ?
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }
}