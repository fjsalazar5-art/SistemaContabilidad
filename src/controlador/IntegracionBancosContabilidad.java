package controlador;

import conexion.Conexion;
import dao.ComprobanteDAO;
import dao.ComprobanteDetalleDAO;
import dao.MovimientoBancarioDAO;
import dao.SecuenciaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import modelo.Comprobante;
import modelo.ComprobanteDetalle;
import modelo.CuentaBancaria;
import modelo.MovimientoBancario;
import modelo.MovimientoBancarioDetalle;

/**
 * INTEGRACION Bancos -> Contabilidad.
 *
 * Convierte un Movimiento Bancario en un Comprobante contable (asiento):
 *   - Transaccion con signo '+' (deposito):  DEBE  BANCOS / HABER contrapartida
 *   - Transaccion con signo '-' (retiro):    DEBE  contrapartida / HABER BANCOS
 *
 * La cuenta contable "BANCOS" (tipo ACTIVO) se crea automaticamente
 * la primera vez si no existe. La observacion del comprobante guarda
 * la referencia "MOV. BANCARIO N° X" para evitar enviarlo dos veces
 * y para poder rastrearlo con un SELECT.
 */
public class IntegracionBancosContabilidad {

    public static final String PREFIJO = "MOV. BANCARIO N° ";
    private static final String CUENTA_BANCOS = "BANCOS";

    /** Devuelve el numero de comprobante generado. */
    public int enviarAContabilidad(int idMovimiento, int idCuentaContrapartida) {

        MovimientoBancarioDAO movDAO = new MovimientoBancarioDAO();

        MovimientoBancario mov = movDAO.buscar(idMovimiento);
        if (mov == null) {
            throw new RuntimeException("Primero guarde o busque un movimiento bancario");
        }

        List<MovimientoBancarioDetalle> detalles = movDAO.listarDetalles(idMovimiento);
        if (detalles.isEmpty()) {
            throw new RuntimeException("El movimiento no tiene detalle");
        }

        CuentaBancaria cuenta = new dao.CuentaBancariaDAO().buscar(mov.getIdCuentaBanco());

        Connection con = null;

        try {
            con = Conexion.conectar();
            if (con == null) {
                throw new Exception("No se pudo conectar con Oracle");
            }
            con.setAutoCommit(false);

            Integer existente = comprobanteExistente(con, idMovimiento);
            if (existente != null) {
                throw new Exception("Este movimiento ya fue enviado a Contabilidad "
                        + "(Comprobante N° " + existente + ")");
            }

            int idBancos = obtenerOCrearCuentaBancos(con);

            if (idBancos == idCuentaContrapartida) {
                throw new Exception("La contrapartida no puede ser la misma cuenta BANCOS");
            }

            // ---------- Cabecera del comprobante ----------
            SecuenciaDAO sec = new SecuenciaDAO();
            int idComprobante = sec.siguienteComprobante(con);

            String obs = PREFIJO + idMovimiento + " - "
                    + (cuenta != null ? cuenta.getDescripcion() : "")
                    + (mov.getDescripcion() != null ? " - " + mov.getDescripcion() : "");
            if (obs.length() > 250) {
                obs = obs.substring(0, 250);
            }

            Comprobante c = new Comprobante();
            c.setIdComprobante(idComprobante);
            c.setFecha(mov.getFecha());
            c.setObservacion(obs);

            if (!new ComprobanteDAO().guardar(con, c)) {
                throw new Exception("No se pudo guardar la cabecera del comprobante");
            }

            // ---------- Detalle (asiento) ----------
            ComprobanteDetalleDAO detDAO = new ComprobanteDetalleDAO();

            for (MovimientoBancarioDetalle d : detalles) {

                boolean ingreso = "+".equals(d.getSigno());

                // Linea de BANCOS
                insertarLinea(con, sec, detDAO, idComprobante, idBancos,
                        ingreso ? d.getValor() : 0, ingreso ? 0 : d.getValor());

                // Linea de contrapartida
                insertarLinea(con, sec, detDAO, idComprobante, idCuentaContrapartida,
                        ingreso ? 0 : d.getValor(), ingreso ? d.getValor() : 0);
            }

            con.commit();
            return idComprobante;

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void insertarLinea(Connection con, SecuenciaDAO sec,
            ComprobanteDetalleDAO detDAO, int idComprobante, int idCuenta,
            double debe, double haber) throws Exception {

        ComprobanteDetalle det = new ComprobanteDetalle();
        det.setIdDetalle(sec.siguienteDetalle(con));
        det.setIdComprobante(idComprobante);
        det.setIdCuenta(idCuenta);
        det.setDebe(debe);
        det.setHaber(haber);

        if (!detDAO.guardar(con, det)) {
            throw new Exception("No se pudo guardar el detalle del comprobante");
        }
    }

    private Integer comprobanteExistente(Connection con, int idMovimiento) throws Exception {

        String sql = """
            SELECT ID_COMPROBANTE FROM COMPROBANTE
            WHERE OBSERVACION LIKE ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, PREFIJO + idMovimiento + " -%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }

    private int obtenerOCrearCuentaBancos(Connection con) throws Exception {

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT ID_CUENTA FROM CUENTA_CONTABLE WHERE UPPER(NOMBRE) = ?")) {
            ps.setString(1, CUENTA_BANCOS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        int idActivo;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT ID_TIPO_CUENTA FROM TIPO_CUENTA WHERE UPPER(NOMBRE) = 'ACTIVO'");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new Exception("No existe el tipo de cuenta ACTIVO");
            }
            idActivo = rs.getInt(1);
        }

        int id;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT SEQ_CUENTA_CONTABLE.NEXTVAL FROM DUAL");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            id = rs.getInt(1);
        }

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO CUENTA_CONTABLE (ID_CUENTA, NOMBRE, ID_TIPO_CUENTA) VALUES (?, ?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, CUENTA_BANCOS);
            ps.setInt(3, idActivo);
            ps.executeUpdate();
        }

        return id;
    }
}
