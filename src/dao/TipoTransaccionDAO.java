package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.TipoTransaccion;

public class TipoTransaccionDAO {

    // GUARDAR: genera automaticamente el ID con SEQ_TIPO_TRANSACCION
    public boolean guardar(TipoTransaccion tipo) {

        String sql = """
            INSERT INTO TIPO_TRANSACCION
            (ID_TIPO_TRANSACCION, NOMBRE, SIGNO)
            VALUES (SEQ_TIPO_TRANSACCION.NEXTVAL, ?, ?)
            """;

        String sqlId = "SELECT SEQ_TIPO_TRANSACCION.CURRVAL FROM DUAL";

        try (Connection con = Conexion.conectar()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tipo.getNombre().trim());
                ps.setString(2, tipo.getSigno());
                if (ps.executeUpdate() == 0) {
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlId);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tipo.setIdTipoTransaccion(rs.getInt(1));
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error al guardar Tipo de Transaccion:");
            e.printStackTrace();
        }

        return false;
    }

    // BUSCAR POR ID
    public TipoTransaccion buscar(int id) {

        String sql = """
            SELECT ID_TIPO_TRANSACCION, NOMBRE, SIGNO
            FROM TIPO_TRANSACCION
            WHERE ID_TIPO_TRANSACCION = ?
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
            System.out.println("Error al buscar Tipo de Transaccion:");
            e.printStackTrace();
        }

        return null;
    }

    // BUSCAR POR NOMBRE (primera coincidencia, sin distinguir mayusculas)
    public TipoTransaccion buscarPorNombre(String nombre) {

        String sql = """
            SELECT ID_TIPO_TRANSACCION, NOMBRE, SIGNO
            FROM TIPO_TRANSACCION
            WHERE UPPER(NOMBRE) LIKE UPPER(?)
            ORDER BY CASE
                       WHEN UPPER(NOMBRE) = UPPER(?) THEN 0
                       WHEN UPPER(NOMBRE) LIKE UPPER(?) THEN 1
                       ELSE 2
                     END,
                     ID_TIPO_TRANSACCION
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Prioridad: nombre exacto, luego nombre que TERMINA en el texto
            String t = nombre.trim();
            ps.setString(1, "%" + t + "%");
            ps.setString(2, t);
            ps.setString(3, "% " + t);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar Tipo de Transaccion:");
            e.printStackTrace();
        }

        return null;
    }

    // MODIFICAR
    public boolean modificar(TipoTransaccion tipo) {

        String sql = """
            UPDATE TIPO_TRANSACCION
            SET NOMBRE = ?, SIGNO = ?
            WHERE ID_TIPO_TRANSACCION = ?
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.getNombre().trim());
            ps.setString(2, tipo.getSigno());
            ps.setInt(3, tipo.getIdTipoTransaccion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al modificar Tipo de Transaccion:");
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR (falla si algun movimiento usa este tipo: llave foranea)
    public boolean eliminar(int id) {

        String sql = "DELETE FROM TIPO_TRANSACCION WHERE ID_TIPO_TRANSACCION = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al eliminar Tipo de Transaccion:");
            e.printStackTrace();
            return false;
        }
    }

    // LISTAR (para los combos)
    public List<TipoTransaccion> listar() {

        List<TipoTransaccion> lista = new ArrayList<>();

        String sql = """
            SELECT ID_TIPO_TRANSACCION, NOMBRE, SIGNO
            FROM TIPO_TRANSACCION
            ORDER BY ID_TIPO_TRANSACCION
            """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (Exception e) {
            System.out.println("Error al listar Tipos de Transaccion:");
            e.printStackTrace();
        }

        return lista;
    }

    private TipoTransaccion mapear(ResultSet rs) throws Exception {
        return new TipoTransaccion(
                rs.getInt("ID_TIPO_TRANSACCION"),
                rs.getString("NOMBRE"),
                rs.getString("SIGNO"));
    }
}
