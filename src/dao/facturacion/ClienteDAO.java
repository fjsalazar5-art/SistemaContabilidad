/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.facturacion;

/**
 *
 * @author KEVIN
 */


import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import modelo.facturacion.Cliente;

public class ClienteDAO {

    public boolean guardar(Cliente cliente){

        try{

            Connection con = Conexion.conectar();

            String sql =
                    "INSERT INTO CLIENTE "
                    + "(ID_CLIENTE,RUC,NOMBRE,DIRECCION) "
                    + "VALUES "
                    + "(SEQ_CLIENTE.NEXTVAL,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    cliente.getRuc());

            ps.setString(2,
                    cliente.getNombre());

            ps.setString(3,
                    cliente.getDireccion());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    }

    public ArrayList<Cliente> listar(){

        ArrayList<Cliente> lista =
                new ArrayList<>();

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT * FROM CLIENTE";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                Cliente c =
                        new Cliente();

                c.setIdCliente(
                        rs.getInt("ID_CLIENTE"));

                c.setRuc(
                        rs.getString("RUC"));

                c.setNombre(
                        rs.getString("NOMBRE"));

                c.setDireccion(
                        rs.getString("DIRECCION"));

                lista.add(c);
            }

            con.close();

        }catch(Exception e){

            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean modificar(Cliente cliente){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "UPDATE CLIENTE "
                    + "SET RUC=?, "
                    + "NOMBRE=?, "
                    + "DIRECCION=? "
                    + "WHERE ID_CLIENTE=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    cliente.getRuc());

            ps.setString(2,
                    cliente.getNombre());

            ps.setString(3,
                    cliente.getDireccion());

            ps.setInt(4,
                    cliente.getIdCliente());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    }

    public boolean eliminar(int idCliente){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "DELETE FROM CLIENTE "
                    + "WHERE ID_CLIENTE=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1,idCliente);

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    }

    public ArrayList<Cliente> buscarPorNombre(String nombre){

    ArrayList<Cliente> lista =
            new ArrayList<>();

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT * FROM CLIENTE "
                + "WHERE UPPER(NOMBRE) LIKE UPPER(?)";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setString(1,
                "%" + nombre + "%");

        ResultSet rs =
                ps.executeQuery();

        while(rs.next()){

            Cliente c = new Cliente();

            c.setIdCliente(
                    rs.getInt("ID_CLIENTE"));

            c.setRuc(
                    rs.getString("RUC"));

            c.setNombre(
                    rs.getString("NOMBRE"));

            c.setDireccion(
                    rs.getString("DIRECCION"));

            lista.add(c);
        }

        con.close();

    }catch(Exception e){

        System.out.println(e.getMessage());
    }

    return lista;
}
    public ArrayList<Cliente> obtenerClientes(){

    return listar();

}
}
