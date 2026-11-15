package cxc.servidor;

import conexion.Conexion;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Base64;

/**
 * Adaptación del CxcServicio recibido por Alejandro: mantiene las tramas TCP,
 * pero trabaja con las tablas ORACLE existentes del grupo (sin crear CXC_*).
 * Las facturas ya son compartidas con Facturación: NO se duplican.
 */
public class CxcServicio {
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private static String enc(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                (s == null ? "" : s).getBytes(StandardCharsets.UTF_8));
    }
    private static String dec(String s) {
        try { return new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Valor Base64 inválido"); }
    }
    private static String fila(String... cols) {
        StringJoiner j = new StringJoiner(";");
        for (String s : cols) j.add(enc(s));
        return j.toString();
    }
    private static String[] datos(String[] trama) {
        if (trama.length < 4 || trama[3].isEmpty()) return new String[0];
        String[] items = trama[3].split(";", -1);
        for (int i=0; i<items.length; i++) items[i] = dec(items[i]);
        return items;
    }
    private static String ok(String m) { return "OK>" + m; }
    private static String error(String m) { return "ERROR>" + m; }
    private static void require(String[] v,int n) { if (v.length < n) throw new IllegalArgumentException("Faltan datos para completar la operación."); }
    private static String money(BigDecimal x) { return (x == null ? ZERO : x).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(); }
    private static Connection conectar() throws SQLException {
        Connection c=Conexion.conectar();
        if(c==null) throw new SQLException("No se pudo conectar a Oracle (revise conexion.properties)");
        return c;
    }
    private static int next(Connection c, String seq) throws SQLException {
        try (Statement st=c.createStatement(); ResultSet rs=st.executeQuery("SELECT " + seq + ".NEXTVAL FROM DUAL")) {
            if(rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Secuencia no disponible: " + seq);
    }
    private static boolean existe(Connection c,String tabla,String clave,int id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM "+tabla+" WHERE "+clave+"=?")) {
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){rs.next();return rs.getInt(1)>0;}
        }
    }
    private static String tabla(Connection c, String sql, String... params) throws SQLException {
        StringBuilder b=new StringBuilder();
        try(PreparedStatement ps=c.prepareStatement(sql)) {
            for(int i=0;i<params.length;i++)ps.setString(i+1,params[i]);
            try(ResultSet rs=ps.executeQuery()) {
                ResultSetMetaData md=rs.getMetaData(); int n=md.getColumnCount();
                String[] cols=new String[n];
                for(int i=0;i<n;i++)cols[i]=md.getColumnLabel(i+1);
                b.append(fila(cols));
                while(rs.next()){
                    String[] cells=new String[n];for(int i=0;i<n;i++)cells[i]=rs.getString(i+1);
                    b.append('#').append(fila(cells));
                }
            }
        }
        return ok(b.toString());
    }
    private static String listado(Connection c, String sql) throws SQLException {
        StringBuilder b=new StringBuilder();
        try(PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()) {
            int n=rs.getMetaData().getColumnCount();
            while(rs.next()){
                String[] cells=new String[n];for(int i=0;i<n;i++)cells[i]=rs.getString(i+1);
                if(b.length()>0)b.append('#'); b.append(fila(cells));
            }
        }
        return ok(b.toString());
    }
    public String procesar(String entidad,String accion,String[] trama) {
        try {
            String[] v=datos(trama);
            return switch(entidad) {
                case "COBRADOR" -> cobrador(accion,v);
                case "FORMA_PAGO" -> formaPago(accion,v);
                case "CUENTA" -> cuentas(accion);
                case "FACTURA" -> facturas(accion,v);
                case "PAGO" -> pagos(accion,v);
                case "REPORTE" -> reportes(accion,v);
                default -> error("Entidad CxC no reconocida: "+entidad);
            };
        } catch(Exception ex) {
            return error("Cuentas por Cobrar: " + ex.getMessage());
        }
    }
    private String cobrador(String accion,String[] v) throws SQLException {
        try(Connection c=conectar()) {
            switch(accion) {
                case "LISTAR":return listado(c,"SELECT ID_COBRADOR,CEDULA,NOMBRE,DIRECCION FROM COBRADOR ORDER BY NOMBRE");
                case "INSERTAR":{
                    require(v,3);if(v[0].isBlank()||v[1].isBlank())return error("Cédula y nombre obligatorios");
                    try(PreparedStatement ps=c.prepareStatement("INSERT INTO COBRADOR(ID_COBRADOR,CEDULA,NOMBRE,DIRECCION) VALUES(SEQ_COBRADOR.NEXTVAL,?,?,?)")) {
                        ps.setString(1,v[0]);ps.setString(2,v[1]);ps.setString(3,v[2]);ps.executeUpdate();
                    } return ok("Cobrador registrado.");
                }
                case "MODIFICAR":{
                    require(v,4);try(PreparedStatement ps=c.prepareStatement("UPDATE COBRADOR SET CEDULA=?,NOMBRE=?,DIRECCION=? WHERE ID_COBRADOR=?")) {
                        ps.setString(1,v[1]);ps.setString(2,v[2]);ps.setString(3,v[3]);ps.setInt(4,Integer.parseInt(v[0]));
                        return ps.executeUpdate()>0?ok("Cobrador actualizado."):error("Cobrador no encontrado.");
                    }
                }
                case "ELIMINAR":{
                    require(v,1);try(PreparedStatement ps=c.prepareStatement("DELETE FROM COBRADOR WHERE ID_COBRADOR=?")) {
                        ps.setInt(1,Integer.parseInt(v[0]));return ps.executeUpdate()>0?ok("Cobrador eliminado."):error("Cobrador no encontrado.");
                    }
                }
                default:return error("Acción desconocida para Cobrador");
            }
        }
    }
    private String formaPago(String accion,String[] v) throws SQLException {
        try(Connection c=conectar()) {
            switch(accion) {
                case "LISTAR":return listado(c,"SELECT ID_FORMA_PAGO,NOMBRE FROM FORMA_PAGO ORDER BY NOMBRE");
                case "INSERTAR":{
                    require(v,1);if(v[0].isBlank())return error("El nombre es obligatorio");
                    try(PreparedStatement ps=c.prepareStatement("INSERT INTO FORMA_PAGO(ID_FORMA_PAGO,NOMBRE) VALUES(SEQ_FORMA_PAGO.NEXTVAL,?)")){
                        ps.setString(1,v[0]);ps.executeUpdate();
                    }return ok("Forma de pago registrada.");
                }
                case "MODIFICAR":{
                    require(v,2);try(PreparedStatement ps=c.prepareStatement("UPDATE FORMA_PAGO SET NOMBRE=? WHERE ID_FORMA_PAGO=?")){
                        ps.setString(1,v[1]);ps.setInt(2,Integer.parseInt(v[0]));return ps.executeUpdate()>0?ok("Forma de pago modificada."):error("No encontrada.");
                    }
                }
                case "ELIMINAR":{
                    require(v,1);try(PreparedStatement ps=c.prepareStatement("DELETE FROM FORMA_PAGO WHERE ID_FORMA_PAGO=?")){
                        ps.setInt(1,Integer.parseInt(v[0]));return ps.executeUpdate()>0?ok("Forma de pago eliminada."):error("No encontrada.");
                    }
                }
                default:return error("Acción desconocida para Forma de pago");
            }
        }
    }
    private String cuentas(String accion) throws SQLException {
        if(!accion.equals("LISTAR"))return error("Acción inválida para cuentas");
        try(Connection c=conectar()) { return listado(c,"SELECT ID_CUENTA,NOMBRE FROM CUENTA_CONTABLE ORDER BY NOMBRE"); }
    }
    private static String pagosFacturaSql() {
        return "SELECT p.ID_FACTURA,NVL(SUM(d.VALOR),0) PAGADO FROM PAGO p " +
               "JOIN PAGO_DETALLE d ON d.ID_PAGO=p.ID_PAGO GROUP BY p.ID_FACTURA";
    }
    private String facturas(String accion,String[] v) throws SQLException {
        if(accion.equals("RECIBIR")) {
            require(v,6);
            // Compartimos tabla FACTURA, por lo que una recepción NO inserta duplicados.
            int id;try{id=Integer.parseInt(v[0]);}catch(NumberFormatException ex){return error("Indique el número (ID) de una factura ya registrada en Facturación.");}
            try(Connection c=conectar();PreparedStatement ps=c.prepareStatement("SELECT ID_FACTURA FROM FACTURA WHERE ID_FACTURA=?")) {
                ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()) {
                    return rs.next()?ok("Factura #"+id+" disponible en Cuentas por Cobrar mediante la base compartida.")
                           :error("Factura #"+id+" no existe. Regístrela primero en Facturación.");
                }
            }
        }
        if(!accion.equals("LISTAR"))return error("Acción inválida para Factura");
        String sql="SELECT f.ID_FACTURA,TO_CHAR(f.ID_FACTURA),TO_CHAR(f.FECHA,'YYYY-MM-DD'),"+
                "cl.NOMBRE,cl.RUC,TO_CHAR(NVL(f.TOTAL,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,'''),"+
                "TO_CHAR(NVL(p.PAGADO,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,'''),"+
                "TO_CHAR(NVL(f.TOTAL,0)-NVL(p.PAGADO,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,'''),"+
                "CASE WHEN NVL(f.TOTAL,0)-NVL(p.PAGADO,0)<=0 THEN 'PAGADA' ELSE 'PENDIENTE' END " +
                "FROM FACTURA f JOIN CLIENTE cl ON cl.ID_CLIENTE=f.ID_CLIENTE " +
                "LEFT JOIN ("+pagosFacturaSql()+") p ON p.ID_FACTURA=f.ID_FACTURA " +
                "ORDER BY f.FECHA DESC,f.ID_FACTURA DESC";
        try(Connection c=conectar()){return listado(c,sql);}
    }
    private String pagos(String accion,String[] v) throws SQLException {
        if(!accion.equals("REGISTRAR"))return error("Acción inválida para Pago");
        require(v,8);
        int factura=Integer.parseInt(v[0]),cobrador=Integer.parseInt(v[1]),forma=Integer.parseInt(v[2]);
        LocalDate fecha=LocalDate.parse(v[3]);BigDecimal valor=new BigDecimal(v[4]).setScale(2,java.math.RoundingMode.HALF_UP);
        if(valor.signum()<=0)return error("Monto inválido, debe ser mayor que cero");
        int debe=Integer.parseInt(v[6]),haber=Integer.parseInt(v[7]);
        if(debe==haber)return error("La cuenta DEBE y HABER deben ser distintas");
        try(Connection c=conectar()) {
            c.setAutoCommit(false);
            try {
                BigDecimal total;
                // bloqueo por factura impide cobrar simultáneamente más del saldo real
                try(PreparedStatement ps=c.prepareStatement("SELECT NVL(TOTAL,0) FROM FACTURA WHERE ID_FACTURA=? FOR UPDATE")) {
                    ps.setInt(1,factura);try(ResultSet rs=ps.executeQuery()) {
                        if(!rs.next())throw new SQLException("Factura no encontrada");total=rs.getBigDecimal(1);
                    }
                }
                BigDecimal pagado=ZERO;
                try(PreparedStatement ps=c.prepareStatement("SELECT NVL(SUM(d.VALOR),0) FROM PAGO p JOIN PAGO_DETALLE d ON d.ID_PAGO=p.ID_PAGO WHERE p.ID_FACTURA=?")){
                    ps.setInt(1,factura);try(ResultSet rs=ps.executeQuery()){rs.next();pagado=rs.getBigDecimal(1);}
                }
                BigDecimal saldo=total.subtract(pagado);
                if(valor.compareTo(saldo)>0)throw new SQLException("El cobro supera el saldo de factura: $"+money(saldo));
                if(!existe(c,"COBRADOR","ID_COBRADOR",cobrador))throw new SQLException("No existe el cobrador seleccionado");
                if(!existe(c,"FORMA_PAGO","ID_FORMA_PAGO",forma))throw new SQLException("No existe la forma de pago");
                if(!existe(c,"CUENTA_CONTABLE","ID_CUENTA",debe)||!existe(c,"CUENTA_CONTABLE","ID_CUENTA",haber))throw new SQLException("La cuenta contable seleccionada no existe");
                int pagoId=next(c,"SEQ_PAGO");
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO PAGO(ID_PAGO,ID_FACTURA,FECHA,VALOR) VALUES(?,?,?,?)")) {
                    ps.setInt(1,pagoId);ps.setInt(2,factura);ps.setDate(3,java.sql.Date.valueOf(fecha));ps.setBigDecimal(4,valor);ps.executeUpdate();
                }
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO PAGO_DETALLE(ID_PAGO_DETALLE,ID_PAGO,FECHA_PAGO,ID_FORMA_PAGO,ID_COBRADOR,VALOR) VALUES(SEQ_PAGO_DETALLE.NEXTVAL,?,?,?,?,?)")) {
                    ps.setInt(1,pagoId);ps.setDate(2,java.sql.Date.valueOf(fecha));ps.setInt(3,forma);ps.setInt(4,cobrador);ps.setBigDecimal(5,valor);ps.executeUpdate();
                }
                // Integración CxC -> Contabilidad, MISMA transacción Oracle.
                int asientoId=next(c,"SEQ_COMPROBANTE");
                String obs="CXC PAGO #"+pagoId+" FACTURA #"+factura;
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO COMPROBANTE(ID_COMPROBANTE,FECHA,OBSERVACION) VALUES(?,?,?)")){
                    ps.setInt(1,asientoId);ps.setDate(2,java.sql.Date.valueOf(fecha));ps.setString(3,obs);ps.executeUpdate();
                }
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO COMPROBANTE_DETALLE(ID_COMP_DET,ID_COMPROBANTE,ID_CUENTA,DEBE,HABER) VALUES(SEQ_COMPROBANTE_DETALLE.NEXTVAL,?,?,?,?)")) {
                    ps.setInt(1,asientoId);ps.setInt(2,debe);ps.setBigDecimal(3,valor);ps.setBigDecimal(4,ZERO);ps.executeUpdate();
                    ps.setInt(1,asientoId);ps.setInt(2,haber);ps.setBigDecimal(3,ZERO);ps.setBigDecimal(4,valor);ps.executeUpdate();
                }
                c.commit();
                return ok("Pago #"+pagoId+" registrado. Comprobante #"+asientoId+" generado. Saldo restante $"+money(saldo.subtract(valor)));
            } catch(Exception ex){ c.rollback();throw ex; }
        } catch(Exception ex) {throw new SQLException("No se guardó el pago: "+ex.getMessage(),ex);}
    }
    private String reportes(String accion,String[] v) throws SQLException {
        if(accion.equals("ESTADO_CLIENTE")) {
            require(v,3);
            LocalDate desde=LocalDate.parse(v[1]),hasta=LocalDate.parse(v[2]);
            if(hasta.isBefore(desde))return error("Rango de fechas invertido");
            String base="SELECT 'FACTURA' MOVIMIENTO,TO_CHAR(f.ID_FACTURA) DOCUMENTO,TO_CHAR(f.FECHA,'YYYY-MM-DD') FECHA,"+
                    "TO_CHAR(NVL(f.TOTAL,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,''') CARGO,'0.00' ABONO,"+
                    "TO_CHAR(NVL(f.TOTAL,0)-NVL(p.PAGADO,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,''') SALDO,"+
                    "CASE WHEN NVL(f.TOTAL,0)-NVL(p.PAGADO,0)<=0 THEN 'PAGADA' ELSE 'PENDIENTE' END ESTADO " +
                    "FROM FACTURA f JOIN CLIENTE cl ON cl.ID_CLIENTE=f.ID_CLIENTE " +
                    "LEFT JOIN ("+pagosFacturaSql()+") p ON p.ID_FACTURA=f.ID_FACTURA " +
                    "WHERE cl.RUC=? AND f.FECHA>=TO_DATE(?,'YYYY-MM-DD') AND f.FECHA<TO_DATE(?,'YYYY-MM-DD')+1 " +
                    "UNION ALL " +
                    "SELECT 'PAGO' MOVIMIENTO,TO_CHAR(f.ID_FACTURA) DOCUMENTO,TO_CHAR(d.FECHA_PAGO,'YYYY-MM-DD') FECHA,"+
                    "'0.00' CARGO,TO_CHAR(d.VALOR,'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,''') ABONO,"+
                    "TO_CHAR(NVL(f.TOTAL,0)-NVL(pp.PAGADO,0),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,''') SALDO,"+
                    "'REGISTRADO' ESTADO FROM PAGO_DETALLE d JOIN PAGO p ON p.ID_PAGO=d.ID_PAGO " +
                    "JOIN FACTURA f ON f.ID_FACTURA=p.ID_FACTURA JOIN CLIENTE cl ON cl.ID_CLIENTE=f.ID_CLIENTE " +
                    "LEFT JOIN ("+pagosFacturaSql()+") pp ON pp.ID_FACTURA=f.ID_FACTURA " +
                    "WHERE cl.RUC=? AND d.FECHA_PAGO>=TO_DATE(?,'YYYY-MM-DD') AND d.FECHA_PAGO<TO_DATE(?,'YYYY-MM-DD')+1";
            try(Connection c=conectar();PreparedStatement ps=c.prepareStatement("SELECT * FROM ("+base+") ORDER BY FECHA,DOCUMENTO")) {
                ps.setString(1,v[0]);ps.setString(2,v[1]);ps.setString(3,v[2]);
                ps.setString(4,v[0]);ps.setString(5,v[1]);ps.setString(6,v[2]);
                return ejecutarReporte(ps);
            }
        }
        if(accion.equals("COBROS_COBRADOR")) {
            require(v,2); LocalDate desde=LocalDate.parse(v[0]),hasta=LocalDate.parse(v[1]);
            if(hasta.isBefore(desde))return error("Rango de fechas invertido");
            String sql="SELECT c.NOMBRE COBRADOR,fp.NOMBRE FORMA_PAGO,"+
                    "TO_CHAR(SUM(d.VALOR),'FM999999990D00','NLS_NUMERIC_CHARACTERS=''.,''') TOTAL " +
                    "FROM PAGO_DETALLE d JOIN COBRADOR c ON c.ID_COBRADOR=d.ID_COBRADOR " +
                    "JOIN FORMA_PAGO fp ON fp.ID_FORMA_PAGO=d.ID_FORMA_PAGO " +
                    "WHERE d.FECHA_PAGO>=TO_DATE(?,'YYYY-MM-DD') AND d.FECHA_PAGO<TO_DATE(?,'YYYY-MM-DD')+1 " +
                    "GROUP BY c.NOMBRE,fp.NOMBRE ORDER BY c.NOMBRE,fp.NOMBRE";
            try(Connection c=conectar();PreparedStatement ps=c.prepareStatement(sql)) {
                ps.setString(1,v[0]);ps.setString(2,v[1]);return ejecutarReporte(ps);
            }
        }
        return error("Reporte no reconocido");
    }
    private static String ejecutarReporte(PreparedStatement ps) throws SQLException {
        StringBuilder b=new StringBuilder();
        try(ResultSet rs=ps.executeQuery()){
            ResultSetMetaData md=rs.getMetaData();int n=md.getColumnCount();String[] head=new String[n];
            for(int i=0;i<n;i++)head[i]=md.getColumnLabel(i+1);b.append(fila(head));
            while(rs.next()){
                String[] cols=new String[n];for(int i=0;i<n;i++)cols[i]=rs.getString(i+1);
                b.append('#').append(fila(cols));
            }
        }
        return ok(b.toString());
    }
}
