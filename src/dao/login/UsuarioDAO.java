/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.login;

/**
 *
 * @author KEVIN
 */


import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import modelo.login.Usuario;

public class UsuarioDAO {

    public Usuario login(String usuario, String clave) {

        Usuario u = null;

        try {

            Connection con = Conexion.conectar();

            String sql =
                    "SELECT * FROM USUARIO "
                    + "WHERE NOMBRE_USUARIO = ? "
                    + "AND CLAVE = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, usuario);
            ps.setString(2, clave);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                u = new Usuario();

                u.setIdUsuario(
                        rs.getInt("ID_USUARIO"));

                u.setNombre(
                        rs.getString("NOMBRE"));

                u.setNombreUsuario(
                        rs.getString("NOMBRE_USUARIO"));

                u.setClave(
                        rs.getString("CLAVE"));

                u.setRol(
                        rs.getString("ROL"));
            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        return u;
    }

    public boolean guardar(Usuario usuario) {

        try {

            Connection con = Conexion.conectar();

            String sql =
                    "INSERT INTO USUARIO "
                    + "(ID_USUARIO,NOMBRE,NOMBRE_USUARIO,CLAVE,ROL) "
                    + "VALUES "
                    + "(SEQ_USUARIO.NEXTVAL,?,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    usuario.getNombre());

            ps.setString(2,
                    usuario.getNombreUsuario());

            ps.setString(3,
                    usuario.getClave());

            ps.setString(4,
                    usuario.getRol());

            int filas = ps.executeUpdate();

            con.close();

            return filas > 0;

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return false;
        }
    }

    public ArrayList<Usuario> listar() {

        ArrayList<Usuario> lista =
                new ArrayList<>();

        try {

            Connection con = Conexion.conectar();

            String sql =
                    "SELECT * FROM USUARIO";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Usuario u =
                        new Usuario();

                u.setIdUsuario(
                        rs.getInt("ID_USUARIO"));

                u.setNombre(
                        rs.getString("NOMBRE"));

                u.setNombreUsuario(
                        rs.getString("NOMBRE_USUARIO"));

                u.setClave(
                        rs.getString("CLAVE"));

                u.setRol(
                        rs.getString("ROL"));

                lista.add(u);
            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        return lista;
    }

    public ArrayList<Usuario> buscarPorUsuario(String usuario) {

        ArrayList<Usuario> lista =
                new ArrayList<>();

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT * FROM USUARIO "
                    + "WHERE NOMBRE_USUARIO = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, usuario);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                Usuario u =
                        new Usuario();

                u.setIdUsuario(
                        rs.getInt("ID_USUARIO"));

                u.setNombre(
                        rs.getString("NOMBRE"));

                u.setNombreUsuario(
                        rs.getString("NOMBRE_USUARIO"));

                u.setClave(
                        rs.getString("CLAVE"));

                u.setRol(
                        rs.getString("ROL"));

                lista.add(u);
            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean modificar(Usuario usuario) {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "UPDATE USUARIO "
                    + "SET NOMBRE = ?, "
                    + "NOMBRE_USUARIO = ?, "
                    + "CLAVE = ?, "
                    + "ROL = ? "
                    + "WHERE ID_USUARIO = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    usuario.getNombre());

            ps.setString(2,
                    usuario.getNombreUsuario());

            ps.setString(3,
                    usuario.getClave());

            ps.setString(4,
                    usuario.getRol());

            ps.setInt(5,
                    usuario.getIdUsuario());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return false;
        }
    }

    public boolean eliminar(int idUsuario) {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "DELETE FROM USUARIO "
                    + "WHERE ID_USUARIO = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, idUsuario);

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return false;
        }
    }
}