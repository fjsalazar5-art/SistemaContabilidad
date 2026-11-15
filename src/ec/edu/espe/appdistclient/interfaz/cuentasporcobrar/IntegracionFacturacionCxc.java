package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;

import ec.edu.espe.appdistclient.interfaz.red.ClienteSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

/** Punto de entrada publico para que Facturacion remita facturas a CxC. */
public final class IntegracionFacturacionCxc {
    private IntegracionFacturacionCxc() {}
    private static String enc(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    public static String recibirFactura(String numero, LocalDate fecha, String codigoCliente, String identificacion, String nombreCliente, String total) {
        String[] values = {numero, fecha.toString(), codigoCliente, identificacion, nombreCliente, total};
        StringBuilder frame = new StringBuilder("CXC>FACTURA>RECIBIR>");
        for (int i=0;i<values.length;i++) { if (i>0) frame.append(';'); frame.append(enc(values[i])); }
        return new ClienteSocket().enviarPeticion(frame.toString());
    }
}
