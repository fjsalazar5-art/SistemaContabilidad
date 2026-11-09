package conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    private static final String USER = "PROYECTO";

    private static final String PASSWORD = "Proyecto123";

    public static Connection conectar() {

        Connection con = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conexion exitosa con Oracle");

        } catch (Exception e) {

            System.out.println("Error de conexion");
            e.printStackTrace();

        }

        return con;
    }
}