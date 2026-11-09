package prueba;

import conexion.Conexion;
import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        Connection con = Conexion.conectar();

        if (con != null) {
            System.out.println("Conexion establecida correctamente");
        } else {
            System.out.println("No se pudo conectar");
        }

    }
}