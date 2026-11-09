package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SecuenciaDAO {

    public int siguienteComprobante() {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT SEQ_COMPROBANTE.NEXTVAL VALOR FROM DUAL";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return rs.getInt("VALOR");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return -1;
    }

    public int siguienteDetalle() {

        try {

            Connection con =
                    Conexion.conectar();

            String sql =
                    "SELECT SEQ_COMPROBANTE_DETALLE.NEXTVAL VALOR FROM DUAL";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return rs.getInt("VALOR");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return -1;
    }
}