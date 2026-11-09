package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.CuentaContable;

public class CuentaContableDAO {

    public boolean guardar(CuentaContable cuenta) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    INSERT INTO CUENTA_CONTABLE
                    (ID_CUENTA, NOMBRE, ID_TIPO_CUENTA)
                    VALUES (?, ?, ?)
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, cuenta.getIdCuenta());
            ps.setString(2, cuenta.getNombre());
            ps.setInt(3, cuenta.getIdTipoCuenta());

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public CuentaContable buscar(int id) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    SELECT *
                    FROM CUENTA_CONTABLE
                    WHERE ID_CUENTA = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                CuentaContable cuenta =
                        new CuentaContable();

                cuenta.setIdCuenta(
                        rs.getInt("ID_CUENTA"));

                cuenta.setNombre(
                        rs.getString("NOMBRE"));

                cuenta.setIdTipoCuenta(
                        rs.getInt("ID_TIPO_CUENTA"));

                return cuenta;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }

    public boolean modificar(CuentaContable cuenta) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    UPDATE CUENTA_CONTABLE
                    SET NOMBRE = ?,
                        ID_TIPO_CUENTA = ?
                    WHERE ID_CUENTA = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, cuenta.getNombre());
            ps.setInt(2, cuenta.getIdTipoCuenta());
            ps.setInt(3, cuenta.getIdCuenta());

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public boolean eliminar(int id) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    DELETE FROM CUENTA_CONTABLE
                    WHERE ID_CUENTA = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public List<CuentaContable> listar() {

        List<CuentaContable> lista =
                new ArrayList<>();

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT * FROM CUENTA_CONTABLE ORDER BY ID_CUENTA";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                CuentaContable cuenta =
                        new CuentaContable();

                cuenta.setIdCuenta(
                        rs.getInt("ID_CUENTA"));

                cuenta.setNombre(
                        rs.getString("NOMBRE"));

                cuenta.setIdTipoCuenta(
                        rs.getInt("ID_TIPO_CUENTA"));

                lista.add(cuenta);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return lista;
    }

}