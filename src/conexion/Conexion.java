package conexion;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Conexion a Oracle.
 *
 * Los datos de conexion se buscan en este orden:
 *   1. Variables de entorno: DB_URL, DB_USER, DB_PASSWORD
 *   2. Archivo conexion.properties (en la carpeta del proyecto;
 *      esta en .gitignore, NO se sube al repositorio)
 *   3. Valores por defecto de desarrollo local (abajo)
 *
 * Para no exponer la clave en un repositorio publico, crea
 * conexion.properties a partir de conexion.properties.example,
 * cambia la clave en Oracle y deja de usar el valor por defecto.
 */
public class Conexion {

    private static final String URL_DEFECTO =
            "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    private static final String USER_DEFECTO = "PROYECTO";

    private static final String PASSWORD_DEFECTO = "Proyecto123";

    private static final Properties CONFIG = cargarConfiguracion();

    private static Properties cargarConfiguracion() {

        Properties props = new Properties();

        try (FileInputStream in =
                new FileInputStream("conexion.properties")) {

            props.load(in);

        } catch (Exception e) {
            // Si el archivo no existe se usan variables o valores por defecto
        }

        return props;
    }

    private static String valor(
            String variableEntorno,
            String claveArchivo,
            String defecto) {

        String desdeEntorno = System.getenv(variableEntorno);

        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno;
        }

        return CONFIG.getProperty(claveArchivo, defecto);
    }

    public static Connection conectar() {

        Connection con = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            con = DriverManager.getConnection(
                    valor("DB_URL", "url", URL_DEFECTO),
                    valor("DB_USER", "usuario", USER_DEFECTO),
                    valor("DB_PASSWORD", "clave", PASSWORD_DEFECTO)
            );

            System.out.println("Conexion exitosa con Oracle");

        } catch (Exception e) {

            System.out.println("Error de conexion");
            e.printStackTrace();

        }

        return con;
    }
}
