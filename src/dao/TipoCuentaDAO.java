package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import modelo.TipoCuenta;

public class TipoCuentaDAO {

    public boolean guardar(TipoCuenta tipoCuenta) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    INSERT INTO TIPO_CUENTA
                    (ID_TIPO_CUENTA, NOMBRE)
                    VALUES (?, ?)
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, tipoCuenta.getIdTipoCuenta());
            ps.setString(2, tipoCuenta.getNombre());

            ps.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    public TipoCuenta buscar(int id) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    SELECT *
                    FROM TIPO_CUENTA
                    WHERE ID_TIPO_CUENTA = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                TipoCuenta tipoCuenta = new TipoCuenta();

                tipoCuenta.setIdTipoCuenta(
                        rs.getInt("ID_TIPO_CUENTA"));

                tipoCuenta.setNombre(
                        rs.getString("NOMBRE"));

                return tipoCuenta;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;
    }

    public boolean modificar(TipoCuenta tipoCuenta) {

        try {

            Connection con = Conexion.conectar();

            String sql = """
                    UPDATE TIPO_CUENTA
                    SET NOMBRE = ?
                    WHERE ID_TIPO_CUENTA = ?
                    """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, tipoCuenta.getNombre());
            ps.setInt(2, tipoCuenta.getIdTipoCuenta());

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
                    DELETE FROM TIPO_CUENTA
                    WHERE ID_TIPO_CUENTA = ?
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

}