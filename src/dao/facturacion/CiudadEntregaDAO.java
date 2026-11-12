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
import modelo.facturacion.CiudadEntrega;

public class CiudadEntregaDAO {

    public boolean guardar(CiudadEntrega ciudad){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "INSERT INTO CIUDAD_ENTREGA "
                    + "(ID_CIUDAD,NOMBRE) "
                    + "VALUES "
                    + "(SEQ_CIUDAD_ENTREGA.NEXTVAL,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    ciudad.getNombre());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    }

    public ArrayList<CiudadEntrega> listar(){

        ArrayList<CiudadEntrega> lista =
                new ArrayList<>();

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT * FROM CIUDAD_ENTREGA";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                CiudadEntrega ciudad =
                        new CiudadEntrega();

                ciudad.setIdCiudad(
                        rs.getInt("ID_CIUDAD"));

                ciudad.setNombre(
                        rs.getString("NOMBRE"));

                lista.add(ciudad);
            }

            con.close();

        }catch(Exception e){

            System.out.println(e.getMessage());
        }

        return lista;
    }

    public ArrayList<CiudadEntrega> buscarPorNombre(String nombre){

        ArrayList<CiudadEntrega> lista =
                new ArrayList<>();

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT * FROM CIUDAD_ENTREGA "
                    + "WHERE UPPER(NOMBRE) LIKE UPPER(?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    "%" + nombre + "%");

            ResultSet rs =
                    ps.executeQuery();

            while(rs.next()){

                CiudadEntrega ciudad =
                        new CiudadEntrega();

                ciudad.setIdCiudad(
                        rs.getInt("ID_CIUDAD"));

                ciudad.setNombre(
                        rs.getString("NOMBRE"));

                lista.add(ciudad);
            }

            con.close();

        }catch(Exception e){

            System.out.println(e.getMessage());
        }

        return lista;
    }

    public boolean modificar(CiudadEntrega ciudad){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "UPDATE CIUDAD_ENTREGA "
                    + "SET NOMBRE=? "
                    + "WHERE ID_CIUDAD=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1,
                    ciudad.getNombre());

            ps.setInt(2,
                    ciudad.getIdCiudad());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    }

    public boolean eliminar(int idCiudad){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "DELETE FROM CIUDAD_ENTREGA "
                    + "WHERE ID_CIUDAD=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1,
                    idCiudad);

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            System.out.println(e.getMessage());

            return false;
        }
    
}
}