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
import modelo.facturacion.FacturaDetalle;
import java.util.ArrayList;
import java.sql.ResultSet;

public class FacturaDetalleDAO {

    public boolean guardar(FacturaDetalle detalle){

        try{

            Connection con =
                    Conexion.conectar();

            String sql =
                    "INSERT INTO FACTURA_DETALLE "
                    + "(ID_DETALLE,ID_FACTURA,"
                    + "ARTICULO,CANTIDAD,"
                    + "PRECIO) "
                    + "VALUES "
                    + "(SEQ_FACTURA_DETALLE.NEXTVAL,"
                    + "?,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1,
                    detalle.getIdFactura());

            ps.setString(2,
                    detalle.getArticulo());

            ps.setInt(3,
                    detalle.getCantidad());

            ps.setDouble(4,
                    detalle.getPrecio());

            int filas =
                    ps.executeUpdate();

            con.close();

            return filas > 0;

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
    } 
    public ArrayList<FacturaDetalle> listarPorFactura(
        int idFactura){

    ArrayList<FacturaDetalle> lista =
            new ArrayList<>();

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT * "
                + "FROM FACTURA_DETALLE "
                + "WHERE ID_FACTURA = ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, idFactura);

        ResultSet rs =
                ps.executeQuery();

        while(rs.next()){

            FacturaDetalle detalle =
                    new FacturaDetalle();

            detalle.setIdDetalle(
                    rs.getInt("ID_DETALLE"));

            detalle.setIdFactura(
                    rs.getInt("ID_FACTURA"));

            detalle.setArticulo(
                    rs.getString("ARTICULO"));

            detalle.setCantidad(
                    rs.getInt("CANTIDAD"));

            detalle.setPrecio(
                    rs.getDouble("PRECIO"));

            detalle.setSubtotal(
                    rs.getDouble("SUBTOTAL"));

            lista.add(detalle);
        }

        con.close();

    }catch(Exception e){

        e.printStackTrace();
    }

    return lista;
}
    
public boolean eliminarPorFactura(int idFactura){

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "DELETE FROM FACTURA_DETALLE "
                + "WHERE ID_FACTURA = ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, idFactura);

        ps.executeUpdate();

        con.close();

        return true;

    }catch(Exception e){

        e.printStackTrace();

        return false;
    }
}

}