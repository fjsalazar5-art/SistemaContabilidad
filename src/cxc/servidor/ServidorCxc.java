package cxc.servidor;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Servidor de sockets para el módulo original CxC (puerto 5050 en loopback). */
public final class ServidorCxc {
    private static volatile boolean iniciado;
    private static ServerSocket servidor;
    private ServidorCxc() {}
    public static synchronized void iniciarEnSegundoPlano() {
        if (iniciado) return;
        try {
            // Acceso local por defecto para no exponer las operaciones de pago a la red.
            servidor = new ServerSocket(5050, 50, InetAddress.getLoopbackAddress());
            iniciado = true;
            Thread aceptar = new Thread(() -> {
                while(!servidor.isClosed()) {
                    try {
                        Socket socket=servidor.accept();
                        Thread hilo = new Thread(() -> atender(socket),"cxc-cliente");
                        hilo.setDaemon(true);hilo.start();
                    } catch(IOException ex) { if(!servidor.isClosed())System.err.println("CxC: " + ex.getMessage()); }
                }
            },"cxc-socket-server");
            aceptar.setDaemon(true);aceptar.start();
            System.out.println("Cuentas por Cobrar: servidor TCP iniciado en 127.0.0.1:5050");
        } catch(IOException e) {
            // Si ya hay un servidor, el cliente TCP enviará a ese puerto;
            // no se altera ni se detiene ningún proceso ajeno.
            System.err.println("CxC: no se inició servidor en 5050: "+e.getMessage());
        }
    }
    private static void atender(Socket socket) {
        try(socket;
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
            PrintWriter out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8),true)) {
            socket.setSoTimeout(15000);
            String linea=in.readLine();
            if(linea==null||linea.length()>8192){out.println("ERROR>Trama vacía o demasiado larga");return;}
            String[] p=linea.split(">",4);
            if(p.length<3||!"CXC".equalsIgnoreCase(p[0])){out.println("ERROR>Se esperaba trama CXC>ENTIDAD>ACCION>DATOS");return;}
            out.println(new CxcServicio().procesar(p[1].toUpperCase(),p[2].toUpperCase(),p));
        }catch(Exception e){System.err.println("CxC TCP: "+e.getMessage());}
    }
    /** Ejecución opcional como servidor separado, no requiere abrir la interfaz. */
    public static void main(String[] args) throws Exception {
        iniciarEnSegundoPlano();
        Thread.currentThread().join();
    }
}
