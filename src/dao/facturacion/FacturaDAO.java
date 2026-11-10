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
import modelo.facturacion.Factura;
import java.util.ArrayList;

public class FacturaDAO {

    public int guardar(Factura factura){

        int idFactura = 0;

        try{

            Connection con =
                    Conexion.conectar();

            String obtenerId =
                    "SELECT SEQ_FACTURA.NEXTVAL "
                    + "FROM DUAL";

            PreparedStatement psId =
                    con.prepareStatement(obtenerId);

            ResultSet rs =
                    psId.executeQuery();

            if(rs.next()){

                idFactura =
                        rs.getInt(1);
            }

            String sql =
                    "INSERT INTO FACTURA "
                    + "(ID_FACTURA,FECHA,"
                    + "ID_CLIENTE,ID_CIUDAD,TOTAL) "
                    + "VALUES "
                    + "(?,?,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1,
                    idFactura);

            ps.setDate(2,
                    new java.sql.Date(
                            factura.getFecha().getTime()));

            ps.setInt(3,
                    factura.getIdCliente());

            ps.setInt(4,
                    factura.getIdCiudad());

            ps.setDouble(5,
                    factura.getTotal());

            ps.executeUpdate();

            con.close();

        }catch(Exception e){

            System.out.println(
                    e.getMessage());
        }

        return idFactura;
    }
    public boolean modificar(Factura factura){

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "UPDATE FACTURA "
                + "SET FECHA = ?, "
                + "ID_CLIENTE = ?, "
                + "ID_CIUDAD = ?, "
                + "TOTAL = ? "
                + "WHERE ID_FACTURA = ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setDate(1,
                new java.sql.Date(
                        factura.getFecha().getTime()));

        ps.setInt(2,
                factura.getIdCliente());

        ps.setInt(3,
                factura.getIdCiudad());

        ps.setDouble(4,
                factura.getTotal());

        ps.setInt(5,
                factura.getIdFactura());

        int filas =
                ps.executeUpdate();

        con.close();

        return filas > 0;

    }catch(Exception e){

        e.printStackTrace();

        return false;
    }
}
   public ArrayList<Factura> buscarPorCliente(String nombreCliente){

    ArrayList<Factura> lista =
            new ArrayList<>();

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT F.* "
                + "FROM FACTURA F "
                + "INNER JOIN CLIENTE C "
                + "ON F.ID_CLIENTE = C.ID_CLIENTE "
                + "WHERE UPPER(C.NOMBRE) "
                + "LIKE UPPER(?)";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setString(1,
                "%" + nombreCliente + "%");

        ResultSet rs =
                ps.executeQuery();

        while(rs.next()){

            Factura factura =
                    new Factura();

            factura.setIdFactura(
                    rs.getInt("ID_FACTURA"));

            factura.setFecha(
                    rs.getDate("FECHA"));

            factura.setIdCliente(
                    rs.getInt("ID_CLIENTE"));

            factura.setIdCiudad(
                    rs.getInt("ID_CIUDAD"));

            factura.setTotal(
                    rs.getDouble("TOTAL"));

            lista.add(factura);
        }

        con.close();

    }catch(Exception e){

        e.printStackTrace();
    }

    return lista;
}
   public Factura buscarPorId(int idFactura){

    Factura factura = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT * FROM FACTURA "
                + "WHERE ID_FACTURA = ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1,idFactura);

        ResultSet rs =
                ps.executeQuery();

        if(rs.next()){

            factura = new Factura();

            factura.setIdFactura(
                    rs.getInt("ID_FACTURA"));

            factura.setFecha(
                    rs.getDate("FECHA"));

            factura.setIdCliente(
                    rs.getInt("ID_CLIENTE"));

            factura.setIdCiudad(
                    rs.getInt("ID_CIUDAD"));

            factura.setTotal(
                    rs.getDouble("TOTAL"));
        }

        con.close();

    }catch(Exception e){

        e.printStackTrace();
    }

    return factura;
}
}
