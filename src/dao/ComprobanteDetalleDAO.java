package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.ComprobanteDetalle;

public class ComprobanteDetalleDAO {

    public boolean guardar(ComprobanteDetalle detalle) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    INSERT INTO COMPROBANTE_DETALLE
                    (
                    ID_COMP_DET,
                    ID_COMPROBANTE,
                    ID_CUENTA,
                    DEBE,
                    HABER
                    )
                    VALUES (?, ?, ?, ?, ?)
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, detalle.getIdDetalle());
            ps.setInt(2, detalle.getIdComprobante());
            ps.setInt(3, detalle.getIdCuenta());
            ps.setDouble(4, detalle.getDebe());
            ps.setDouble(5, detalle.getHaber());

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    public List<ComprobanteDetalle> listarPorComprobante(
            int idComprobante) {

        List<ComprobanteDetalle> lista =
                new ArrayList<>();

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    """
                    SELECT *
                    FROM COMPROBANTE_DETALLE
                    WHERE ID_COMPROBANTE = ?
                    ORDER BY ID_COMP_DET
                    """;

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, idComprobante);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                ComprobanteDetalle d =
                        new ComprobanteDetalle();

                d.setIdDetalle(
                        rs.getInt("ID_COMP_DET"));

                d.setIdComprobante(
                        rs.getInt("ID_COMPROBANTE"));

                d.setIdCuenta(
                        rs.getInt("ID_CUENTA"));

                d.setDebe(
                        rs.getDouble("DEBE"));

                d.setHaber(
                        rs.getDouble("HABER"));

                lista.add(d);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return lista;
    }

}