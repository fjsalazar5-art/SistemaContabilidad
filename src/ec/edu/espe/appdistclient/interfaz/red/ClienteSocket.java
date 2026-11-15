package ec.edu.espe.appdistclient.interfaz.red;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente TCP. Cada peticion abre una conexion, envia una trama y lee
 * una respuesta del servidor.
 */
public class ClienteSocket {

    private static String host = "localhost";
    private static int puerto = 5050;

    public static void configurar(String nuevoHost, int nuevoPuerto) {
        if (nuevoHost != null && !nuevoHost.trim().isEmpty()) {
            host = nuevoHost.trim();
        }
        if (nuevoPuerto > 0 && nuevoPuerto <= 65535) {
            puerto = nuevoPuerto;
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPuerto() {
        return puerto;
    }

    public static String getServidor() {
        return host + ":" + puerto;
    }

    public String enviarPeticion(String trama) {
        String respuesta = "";

        try (Socket socket = new Socket(host, puerto);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            socket.setSoTimeout(15000);

            salida.println(trama);
            respuesta = entrada.readLine();

        } catch (Exception ex) {
            respuesta = "ERROR>Fallo de conexion con el servidor "
                    + host + ":" + puerto + " (" + ex.getMessage() + ")";
        }

        return respuesta;
    }
}
