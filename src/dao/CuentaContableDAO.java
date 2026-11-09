
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.CuentaContable;

public class CuentaContableDAO {

    // GUARDAR: genera automáticamente el ID
    public boolean guardar(CuentaContable cuenta) {

        String sql = """
            INSERT INTO CUENTA_CONTABLE
            (ID_CUENTA, NOMBRE, ID_TIPO_CUENTA)
            VALUES (SEQ_CUENTA_CONTABLE.NEXTVAL, ?, ?)
            """;

        String sqlId = "SELECT SEQ_CUENTA_CONTABLE.CURRVAL FROM DUAL";

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, cuenta.getNombre().trim());
                ps.setInt(2, cuenta.getIdTipoCuenta());

                if (ps.executeUpdate() == 0) {
                    return false;
                }
            }

            try (PreparedStatement psId = con.prepareStatement(sqlId);
                 ResultSet rs = psId.executeQuery()) {

                if (rs.next()) {
                    cuenta.setIdCuenta(rs.getInt(1));
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al guardar Cuenta Contable:");
            e.printStackTrace();
        }

        return false;
    }

    // BUSCAR
    public CuentaContable buscar(int id) {

        String sql = """
            SELECT ID_CUENTA, NOMBRE, ID_TIPO_CUENTA
            FROM CUENTA_CONTABLE
            WHERE ID_CUENTA = ?
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        CuentaContable cuenta = new CuentaContable();

                        cuenta.setIdCuenta(rs.getInt("ID_CUENTA"));
                        cuenta.setNombre(rs.getString("NOMBRE"));
                        cuenta.setIdTipoCuenta(
                                rs.getInt("ID_TIPO_CUENTA"));

                        return cuenta;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Cuenta Contable:");
            e.printStackTrace();
        }

        return null;
    }

    // MODIFICAR
    public boolean modificar(CuentaContable cuenta) {

        String sql = """
            UPDATE CUENTA_CONTABLE
            SET NOMBRE = ?,
                ID_TIPO_CUENTA = ?
            WHERE ID_CUENTA = ?
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, cuenta.getNombre().trim());
                ps.setInt(2, cuenta.getIdTipoCuenta());
                ps.setInt(3, cuenta.getIdCuenta());

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.out.println("Error al modificar Cuenta Contable:");
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {

        String sql = """
            DELETE FROM CUENTA_CONTABLE
            WHERE ID_CUENTA = ?
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
            System.out.println("Error al eliminar Cuenta Contable:");
            e.printStackTrace();
            return false;
        }
    }

    // LISTAR
    public List<CuentaContable> listar() {

        List<CuentaContable> lista = new ArrayList<>();

        String sql = """
            SELECT ID_CUENTA, NOMBRE, ID_TIPO_CUENTA
            FROM CUENTA_CONTABLE
            ORDER BY ID_CUENTA
            """;

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return lista;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    CuentaContable cuenta = new CuentaContable();

                    cuenta.setIdCuenta(rs.getInt("ID_CUENTA"));
                    cuenta.setNombre(rs.getString("NOMBRE"));
                    cuenta.setIdTipoCuenta(
                            rs.getInt("ID_TIPO_CUENTA"));

                    lista.add(cuenta);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar Cuentas Contables:");
            e.printStackTrace();
        }

        return lista;
    }
}
