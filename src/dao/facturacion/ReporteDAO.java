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

public class ReporteDAO {

   public ResultSet ventasPorCiudad(
        int anioDesde,
        int anioHasta){

    ResultSet rs = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT C.NOMBRE CIUDAD, "
                + "COUNT(F.ID_FACTURA) FACTURAS, "
                + "SUM(F.TOTAL) TOTAL "
                + "FROM FACTURA F "
                + "INNER JOIN CIUDAD_ENTREGA C "
                + "ON F.ID_CIUDAD = C.ID_CIUDAD "
                + "WHERE EXTRACT(YEAR FROM F.FECHA) "
                + "BETWEEN ? AND ? "
                + "GROUP BY C.NOMBRE "
                + "ORDER BY C.NOMBRE";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, anioDesde);
        ps.setInt(2, anioHasta);

        rs = ps.executeQuery();

    }catch(Exception e){

        e.printStackTrace();
    }

    return rs;
}
   public ResultSet clienteArticulo(
        int anioDesde,
        int anioHasta){

    ResultSet rs = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT CL.NOMBRE CLIENTE, "
                + "FD.ARTICULO, "
                + "SUM(FD.CANTIDAD) CANTIDAD, "
                + "SUM(FD.SUBTOTAL) TOTAL "
                + "FROM FACTURA F "
                + "INNER JOIN CLIENTE CL "
                + "ON F.ID_CLIENTE = CL.ID_CLIENTE "
                + "INNER JOIN FACTURA_DETALLE FD "
                + "ON F.ID_FACTURA = FD.ID_FACTURA "
                + "WHERE EXTRACT(YEAR FROM F.FECHA) "
                + "BETWEEN ? AND ? "
                + "GROUP BY CL.NOMBRE, "
                + "FD.ARTICULO "
                + "ORDER BY CL.NOMBRE";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, anioDesde);
        ps.setInt(2, anioHasta);

        rs = ps.executeQuery();

    }catch(Exception e){

        e.printStackTrace();
    }

    return rs;
}
   public ResultSet clienteArticuloCruzado(
        int anioDesde,
        int anioHasta){

    ResultSet rs = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT FD.ARTICULO, "

                + "SUM(CASE "
                + "WHEN C.NOMBRE = 'te' "
                + "THEN FD.SUBTOTAL "
                + "ELSE 0 END) TE, "

                + "SUM(CASE "
                + "WHEN C.NOMBRE = 'vivianasssad' "
                + "THEN FD.SUBTOTAL "
                + "ELSE 0 END) VIVIAN "

                + "FROM FACTURA F "

                + "INNER JOIN CLIENTE C "
                + "ON F.ID_CLIENTE = C.ID_CLIENTE "

                + "INNER JOIN FACTURA_DETALLE FD "
                + "ON F.ID_FACTURA = FD.ID_FACTURA "

                + "WHERE EXTRACT(YEAR FROM F.FECHA) "
                + "BETWEEN ? AND ? "

                + "GROUP BY FD.ARTICULO "
                + "ORDER BY FD.ARTICULO";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, anioDesde);
        ps.setInt(2, anioHasta);

        rs = ps.executeQuery();

    }catch(Exception e){

        e.printStackTrace();
    }

    return rs;
}
   public ResultSet obtenerClientes(){

    ResultSet rs = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT NOMBRE "
                + "FROM CLIENTE "
                + "ORDER BY NOMBRE";

        PreparedStatement ps =
                con.prepareStatement(sql);

        rs = ps.executeQuery();

    }catch(Exception e){

        e.printStackTrace();
    }

    return rs;
}
   public double obtenerTotalClienteArticulo(
        String cliente,
        String articulo,
        int anioDesde,
        int anioHasta){

    double total = 0;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT NVL(SUM(FD.SUBTOTAL),0) TOTAL "
                + "FROM FACTURA F "
                + "INNER JOIN CLIENTE C "
                + "ON F.ID_CLIENTE = C.ID_CLIENTE "
                + "INNER JOIN FACTURA_DETALLE FD "
                + "ON F.ID_FACTURA = FD.ID_FACTURA "
                + "WHERE C.NOMBRE = ? "
                + "AND FD.ARTICULO = ? "
                + "AND EXTRACT(YEAR FROM F.FECHA) "
                + "BETWEEN ? AND ?";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setString(1, cliente);
        ps.setString(2, articulo);
        ps.setInt(3, anioDesde);
        ps.setInt(4, anioHasta);

        ResultSet rs =
                ps.executeQuery();

        if(rs.next()){

            total =
                    rs.getDouble("TOTAL");
        }

    }catch(Exception e){

        e.printStackTrace();
    }

    return total;
}
   public ResultSet obtenerArticulos(
        int anioDesde,
        int anioHasta){

    ResultSet rs = null;

    try{

        Connection con =
                Conexion.conectar();

        String sql =
                "SELECT DISTINCT ARTICULO "
                + "FROM FACTURA_DETALLE FD "
                + "INNER JOIN FACTURA F "
                + "ON FD.ID_FACTURA = F.ID_FACTURA "
                + "WHERE EXTRACT(YEAR FROM F.FECHA) "
                + "BETWEEN ? AND ? "
                + "ORDER BY ARTICULO";

        PreparedStatement ps =
                con.prepareStatement(sql);

        ps.setInt(1, anioDesde);
        ps.setInt(2, anioHasta);

        rs = ps.executeQuery();

    }catch(Exception e){

        e.printStackTrace();
    }

    return rs;
}
    
}
