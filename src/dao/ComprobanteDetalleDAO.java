
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.ComprobanteDetalle;

public class ComprobanteDetalleDAO {

    // GUARDAR DETALLE UTILIZANDO UNA CONEXION EXISTENTE
    public boolean guardar(
            Connection con,
            ComprobanteDetalle detalle) {

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

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdDetalle());
            ps.setInt(2, detalle.getIdComprobante());
            ps.setInt(3, detalle.getIdCuenta());
            ps.setDouble(4, detalle.getDebe());
            ps.setDouble(5, detalle.getHaber());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error al guardar detalle:");
            e.printStackTrace();
            return false;
        }
    }

    // METODO ORIGINAL PARA CONSERVAR COMPATIBILIDAD
    public boolean guardar(ComprobanteDetalle detalle) {

        try (Connection con = Conexion.conectar()) {

            return guardar(con, detalle);

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR TODOS LOS DETALLES DE UN COMPROBANTE
    // (se usa dentro de una transaccion existente)
    public void eliminarPorComprobante(Connection con, int idComprobante)
            throws java.sql.SQLException {

        String sql = """
                DELETE FROM COMPROBANTE_DETALLE
                WHERE ID_COMPROBANTE = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComprobante);
            ps.executeUpdate();
        }
    }

    // LISTAR DETALLES DE UN COMPROBANTE
    public List<ComprobanteDetalle> listarPorComprobante(
            int idComprobante) {

        List<ComprobanteDetalle> lista = new ArrayList<>();

        String sql = """
                SELECT *
                FROM COMPROBANTE_DETALLE
                WHERE ID_COMPROBANTE = ?
                ORDER BY ID_COMP_DET
                """;

        try (
                Connection con = Conexion.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, idComprobante);

            try (ResultSet rs = ps.executeQuery()) {

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
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return lista;
    }
}
