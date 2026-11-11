package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.MovimientoBancario;
import modelo.MovimientoBancarioDetalle;

/**
 * Acceso a MOV_BANCARIO (cabecera) y MOV_BANCARIO_DET (detalle),
 * mas las consultas de los reportes del modulo Bancos.
 * Los metodos que reciben Connection se usan dentro de una transaccion
 * controlada por GestorMovimientoBancario.
 */
public class MovimientoBancarioDAO {

    // ================= SECUENCIAS =================

    public int siguienteMovimiento(Connection con) throws SQLException {
        return siguiente(con, "SELECT SEQ_MOV_BANCARIO.NEXTVAL FROM DUAL");
    }

    public int siguienteDetalle(Connection con) throws SQLException {
        return siguiente(con, "SELECT SEQ_MOV_BANCARIO_DET.NEXTVAL FROM DUAL");
    }

    private int siguiente(Connection con, String sql) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el siguiente valor de la secuencia");
    }

    // ================= CABECERA =================

    public void guardarCabecera(Connection con, MovimientoBancario m) throws SQLException {

        String sql = """
            INSERT INTO MOV_BANCARIO
            (ID_MOVIMIENTO, FECHA, ID_CUENTA_BANCO, DESCRIPCION)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getIdMovimiento());
            ps.setDate(2, m.getFecha());
            ps.setInt(3, m.getIdCuentaBanco());
            ps.setString(4, m.getDescripcion());
            ps.executeUpdate();
        }
    }

    public boolean actualizarCabecera(Connection con, MovimientoBancario m) throws SQLException {

        String sql = """
            UPDATE MOV_BANCARIO
            SET FECHA = ?, ID_CUENTA_BANCO = ?, DESCRIPCION = ?
            WHERE ID_MOVIMIENTO = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, m.getFecha());
            ps.setInt(2, m.getIdCuentaBanco());
            ps.setString(3, m.getDescripcion());
            ps.setInt(4, m.getIdMovimiento());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarCabecera(Connection con, int idMovimiento) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM MOV_BANCARIO WHERE ID_MOVIMIENTO = ?")) {
            ps.setInt(1, idMovimiento);
            return ps.executeUpdate() > 0;
        }
    }

    // Busca por numero de movimiento
    public MovimientoBancario buscar(int idMovimiento) {

        String sql = """
            SELECT ID_MOVIMIENTO, FECHA, ID_CUENTA_BANCO, DESCRIPCION
            FROM MOV_BANCARIO
            WHERE ID_MOVIMIENTO = ?
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCabecera(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Movimiento Bancario:");
            e.printStackTrace();
        }

        return null;
    }

    // Busca el ultimo movimiento cuya cuenta bancaria o descripcion
    // coincida con el texto (para buscar "Cuenta Bancaria X")
    public MovimientoBancario buscarPorTexto(String texto) {

        String sql = """
            SELECT M.ID_MOVIMIENTO, M.FECHA, M.ID_CUENTA_BANCO, M.DESCRIPCION
            FROM MOV_BANCARIO M
            JOIN CUENTA_BANCARIA C ON C.ID_CUENTA_BANCO = M.ID_CUENTA_BANCO
            WHERE UPPER(C.DESCRIPCION) LIKE UPPER(?)
               OR UPPER(C.NUMERO_CUENTA) LIKE UPPER(?)
               OR UPPER(M.DESCRIPCION) LIKE UPPER(?)
            ORDER BY M.ID_MOVIMIENTO DESC
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String patron = "%" + texto.trim() + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCabecera(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Movimiento Bancario:");
            e.printStackTrace();
        }

        return null;
    }

    private MovimientoBancario mapearCabecera(ResultSet rs) throws SQLException {
        MovimientoBancario m = new MovimientoBancario();
        m.setIdMovimiento(rs.getInt("ID_MOVIMIENTO"));
        m.setFecha(rs.getDate("FECHA"));
        m.setIdCuentaBanco(rs.getInt("ID_CUENTA_BANCO"));
        m.setDescripcion(rs.getString("DESCRIPCION"));
        return m;
    }

    // ================= DETALLE =================

    public void guardarDetalle(Connection con, MovimientoBancarioDetalle d) throws SQLException {

        String sql = """
            INSERT INTO MOV_BANCARIO_DET
            (ID_MOV_DET, ID_MOVIMIENTO, FECHA, ID_TIPO_TRANSACCION, VALOR)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdMovDet());
            ps.setInt(2, d.getIdMovimiento());
            ps.setDate(3, d.getFecha());
            ps.setInt(4, d.getIdTipoTransaccion());
            ps.setDouble(5, d.getValor());
            ps.executeUpdate();
        }
    }

    public void eliminarDetalles(Connection con, int idMovimiento) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM MOV_BANCARIO_DET WHERE ID_MOVIMIENTO = ?")) {
            ps.setInt(1, idMovimiento);
            ps.executeUpdate();
        }
    }

    public List<MovimientoBancarioDetalle> listarDetalles(int idMovimiento) {

        List<MovimientoBancarioDetalle> lista = new ArrayList<>();

        String sql = """
            SELECT D.ID_MOV_DET, D.ID_MOVIMIENTO, D.FECHA,
                   D.ID_TIPO_TRANSACCION, D.VALOR, T.NOMBRE, T.SIGNO
            FROM MOV_BANCARIO_DET D
            JOIN TIPO_TRANSACCION T
              ON T.ID_TIPO_TRANSACCION = D.ID_TIPO_TRANSACCION
            WHERE D.ID_MOVIMIENTO = ?
            ORDER BY D.ID_MOV_DET
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoBancarioDetalle d = new MovimientoBancarioDetalle();
                    d.setIdMovDet(rs.getInt("ID_MOV_DET"));
                    d.setIdMovimiento(rs.getInt("ID_MOVIMIENTO"));
                    d.setFecha(rs.getDate("FECHA"));
                    d.setIdTipoTransaccion(rs.getInt("ID_TIPO_TRANSACCION"));
                    d.setValor(rs.getDouble("VALOR"));
                    d.setNombreTipo(rs.getString("NOMBRE"));
                    d.setSigno(rs.getString("SIGNO"));
                    lista.add(d);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar detalle del movimiento:");
            e.printStackTrace();
        }

        return lista;
    }

    // ================= REPORTES =================

    /**
     * REPORTE A: Saldo por cuenta bancaria.
     * Cada fila: {id, numero, descripcion, cliente, ingresos, egresos, saldo}
     */
    public List<Object[]> reporteSaldoPorCuenta() {

        List<Object[]> filas = new ArrayList<>();

        String sql = """
            SELECT C.ID_CUENTA_BANCO, C.NUMERO_CUENTA, C.DESCRIPCION, C.CLIENTE,
                   NVL(SUM(CASE WHEN T.SIGNO = '+' THEN D.VALOR END), 0) AS INGRESOS,
                   NVL(SUM(CASE WHEN T.SIGNO = '-' THEN D.VALOR END), 0) AS EGRESOS
            FROM CUENTA_BANCARIA C
            LEFT JOIN MOV_BANCARIO M ON M.ID_CUENTA_BANCO = C.ID_CUENTA_BANCO
            LEFT JOIN MOV_BANCARIO_DET D ON D.ID_MOVIMIENTO = M.ID_MOVIMIENTO
            LEFT JOIN TIPO_TRANSACCION T ON T.ID_TIPO_TRANSACCION = D.ID_TIPO_TRANSACCION
            GROUP BY C.ID_CUENTA_BANCO, C.NUMERO_CUENTA, C.DESCRIPCION, C.CLIENTE
            ORDER BY C.ID_CUENTA_BANCO
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double ingresos = rs.getDouble("INGRESOS");
                double egresos = rs.getDouble("EGRESOS");
                filas.add(new Object[]{
                    rs.getInt("ID_CUENTA_BANCO"),
                    rs.getString("NUMERO_CUENTA"),
                    rs.getString("DESCRIPCION"),
                    rs.getString("CLIENTE"),
                    ingresos,
                    egresos,
                    ingresos - egresos
                });
            }

        } catch (Exception e) {
            System.out.println("Error en reporte Saldo por Cuenta:");
            e.printStackTrace();
        }

        return filas;
    }

    /**
     * REPORTE B: Movimientos entre dos fechas (por fecha del detalle).
     * idCuentaBanco = 0 significa todas las cuentas.
     * Cada fila: {fecha, nro mov, cuenta, tipo, signo, valor}
     */
    public List<Object[]> reporteMovimientosPorFechas(Date desde, Date hasta, int idCuentaBanco) {

        List<Object[]> filas = new ArrayList<>();

        String sql = """
            SELECT D.FECHA, M.ID_MOVIMIENTO,
                   C.DESCRIPCION || ' (' || C.NUMERO_CUENTA || ')' AS CUENTA,
                   T.NOMBRE AS TIPO, T.SIGNO, D.VALOR
            FROM MOV_BANCARIO_DET D
            JOIN MOV_BANCARIO M ON M.ID_MOVIMIENTO = D.ID_MOVIMIENTO
            JOIN CUENTA_BANCARIA C ON C.ID_CUENTA_BANCO = M.ID_CUENTA_BANCO
            JOIN TIPO_TRANSACCION T ON T.ID_TIPO_TRANSACCION = D.ID_TIPO_TRANSACCION
            WHERE D.FECHA BETWEEN ? AND ?
              AND (? = 0 OR M.ID_CUENTA_BANCO = ?)
            ORDER BY D.FECHA, M.ID_MOVIMIENTO, D.ID_MOV_DET
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, desde);
            ps.setDate(2, hasta);
            ps.setInt(3, idCuentaBanco);
            ps.setInt(4, idCuentaBanco);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getDate("FECHA"),
                        rs.getInt("ID_MOVIMIENTO"),
                        rs.getString("CUENTA"),
                        rs.getString("TIPO"),
                        rs.getString("SIGNO"),
                        rs.getDouble("VALOR")
                    });
                }
            }

        } catch (Exception e) {
            System.out.println("Error en reporte Movimientos por Fechas:");
            e.printStackTrace();
        }

        return filas;
    }
}
