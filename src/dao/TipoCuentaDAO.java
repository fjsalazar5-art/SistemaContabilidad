
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.TipoCuenta;

public class TipoCuentaDAO {

    // GUARDAR: genera automáticamente el ID
    public boolean guardar(TipoCuenta tipoCuenta) {

        String sql = """
            INSERT INTO TIPO_CUENTA
            (ID_TIPO_CUENTA, NOMBRE)
            VALUES (SEQ_TIPO_CUENTA.NEXTVAL, ?)
            """;

        String sqlId = "SELECT SEQ_TIPO_CUENTA.CURRVAL FROM DUAL";

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, tipoCuenta.getNombre().trim());

                if (ps.executeUpdate() == 0) {
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    tipoCuenta.setIdTipoCuenta(rs.getInt(1));
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al guardar Tipo de Cuenta:");
            e.printStackTrace();
        }

        return false;
    }

    // BUSCAR
    public TipoCuenta buscar(int id) {

        String sql = """
            SELECT ID_TIPO_CUENTA, NOMBRE
            FROM TIPO_CUENTA
            WHERE ID_TIPO_CUENTA = ?
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new TipoCuenta(
                            rs.getInt("ID_TIPO_CUENTA"),
                            rs.getString("NOMBRE")
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Tipo de Cuenta:");
            e.printStackTrace();
        }

        return null;
    }

    // MODIFICAR
    public boolean modificar(TipoCuenta tipoCuenta) {

        String sql = """
            UPDATE TIPO_CUENTA
            SET NOMBRE = ?
            WHERE ID_TIPO_CUENTA = ?
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, tipoCuenta.getNombre().trim());
                ps.setInt(2, tipoCuenta.getIdTipoCuenta());

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.out.println("Error al modificar Tipo de Cuenta:");
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {

        String sql = """
            DELETE FROM TIPO_CUENTA
            WHERE ID_TIPO_CUENTA = ?
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.out.println("Error al eliminar Tipo de Cuenta:");
            e.printStackTrace();
            return false;
        }
    }

    // LISTAR TODOS LOS TIPOS DE CUENTA
    public List<TipoCuenta> listar() {

        List<TipoCuenta> lista = new ArrayList<>();

        String sql = """
            SELECT ID_TIPO_CUENTA, NOMBRE
            FROM TIPO_CUENTA
            ORDER BY ID_TIPO_CUENTA
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    TipoCuenta tipo = new TipoCuenta(
                        rs.getInt("ID_TIPO_CUENTA"),
                        rs.getString("NOMBRE")
                    );

                    lista.add(tipo);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar Tipos de Cuenta:");
            e.printStackTrace();
        }

        return lista;
    }
}
