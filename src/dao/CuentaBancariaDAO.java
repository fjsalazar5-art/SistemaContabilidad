package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.CuentaBancaria;

public class CuentaBancariaDAO {

    // GUARDAR: genera automaticamente el ID con SEQ_CUENTA_BANCARIA
    public boolean guardar(CuentaBancaria cuenta) {

        String sql = """
            INSERT INTO CUENTA_BANCARIA
            (ID_CUENTA_BANCO, NUMERO_CUENTA, CLIENTE, DESCRIPCION)
            VALUES (SEQ_CUENTA_BANCARIA.NEXTVAL, ?, ?, ?)
            """;

        String sqlId = "SELECT SEQ_CUENTA_BANCARIA.CURRVAL FROM DUAL";

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, cuenta.getNumeroCuenta().trim());
                ps.setString(2, cuenta.getCliente());
                ps.setString(3, cuenta.getDescripcion());
                if (ps.executeUpdate() == 0) {
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cuenta.setIdCuentaBanco(rs.getInt(1));
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al guardar Cuenta Bancaria:");
            e.printStackTrace();
        }

        return false;
    }

    // BUSCAR POR ID
    public CuentaBancaria buscar(int id) {

        String sql = """
            SELECT ID_CUENTA_BANCO, NUMERO_CUENTA, CLIENTE, DESCRIPCION
            FROM CUENTA_BANCARIA
            WHERE ID_CUENTA_BANCO = ?
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Cuenta Bancaria:");
            e.printStackTrace();
        }

        return null;
    }

    // BUSCAR POR TEXTO: numero de cuenta, descripcion o cliente
    public CuentaBancaria buscarPorTexto(String texto) {

        String sql = """
            SELECT ID_CUENTA_BANCO, NUMERO_CUENTA, CLIENTE, DESCRIPCION
            FROM CUENTA_BANCARIA
            WHERE UPPER(NUMERO_CUENTA) LIKE UPPER(?)
               OR UPPER(DESCRIPCION) LIKE UPPER(?)
               OR UPPER(CLIENTE) LIKE UPPER(?)
            ORDER BY CASE
                       WHEN UPPER(DESCRIPCION) = UPPER(?) OR UPPER(NUMERO_CUENTA) = UPPER(?) THEN 0
                       WHEN UPPER(DESCRIPCION) LIKE UPPER(?) THEN 1
                       ELSE 2
                     END,
                     ID_CUENTA_BANCO
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Prioridad: nombre exacto, luego nombre que TERMINA en el texto
            // (buscar "B" encuentra "Cuenta Bancaria B" y no "Cuenta Bancaria X")
            String t = texto.trim();
            String patron = "%" + t + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, t);
            ps.setString(5, t);
            ps.setString(6, "% " + t);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Cuenta Bancaria:");
            e.printStackTrace();
        }

        return null;
    }

    // MODIFICAR
    public boolean modificar(CuentaBancaria cuenta) {

        String sql = """
            UPDATE CUENTA_BANCARIA
            SET NUMERO_CUENTA = ?, CLIENTE = ?, DESCRIPCION = ?
            WHERE ID_CUENTA_BANCO = ?
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cuenta.getNumeroCuenta().trim());
            ps.setString(2, cuenta.getCliente());
            ps.setString(3, cuenta.getDescripcion());
            ps.setInt(4, cuenta.getIdCuentaBanco());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al modificar Cuenta Bancaria:");
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR (falla si la cuenta tiene movimientos: llave foranea)
    public boolean eliminar(int id) {

        String sql = "DELETE FROM CUENTA_BANCARIA WHERE ID_CUENTA_BANCO = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al eliminar Cuenta Bancaria:");
            e.printStackTrace();
            return false;
        }
    }

    // LISTAR (para los combos)
    public List<CuentaBancaria> listar() {

        List<CuentaBancaria> lista = new ArrayList<>();

        String sql = """
            SELECT ID_CUENTA_BANCO, NUMERO_CUENTA, CLIENTE, DESCRIPCION
            FROM CUENTA_BANCARIA
            ORDER BY ID_CUENTA_BANCO
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (Exception e) {
            System.out.println("Error al listar Cuentas Bancarias:");
            e.printStackTrace();
        }

        return lista;
    }

    private CuentaBancaria mapear(ResultSet rs) throws Exception {
        return new CuentaBancaria(
                rs.getInt("ID_CUENTA_BANCO"),
                rs.getString("NUMERO_CUENTA"),
                rs.getString("CLIENTE"),
                rs.getString("DESCRIPCION"));
    }
}
