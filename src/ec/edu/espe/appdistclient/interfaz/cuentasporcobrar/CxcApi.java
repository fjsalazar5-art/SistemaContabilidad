package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;

import ec.edu.espe.appdistclient.interfaz.red.ClienteSocket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Cliente de servicios del modulo Cuentas por Cobrar. */
final class CxcApi {
    private CxcApi() {}
    static String enc(String s) { return Base64.getUrlEncoder().withoutPadding().encodeToString((s == null ? "" : s).getBytes(StandardCharsets.UTF_8)); }
    static String dec(String s) { try { return new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8); } catch (Exception ex) { return s; } }
    static String request(String entity, String action, String... values) {
        StringBuilder b = new StringBuilder("CXC>").append(entity).append('>').append(action);
        if (values.length > 0) {
            b.append('>');
            for (int i=0;i<values.length;i++) { if(i>0)b.append(';'); b.append(enc(values[i])); }
        }
        return new ClienteSocket().enviarPeticion(b.toString());
    }
    static String[][] rows(String response) {
        if (response == null || !response.startsWith("OK>")) return new String[0][];
        String payload=response.substring(3);
        if(payload.isBlank()) return new String[0][];
        String[] lines=payload.split("#",-1); String[][] result=new String[lines.length][];
        for(int i=0;i<lines.length;i++){ String[] cells=lines[i].split(";",-1); result[i]=new String[cells.length]; for(int j=0;j<cells.length;j++)result[i][j]=dec(cells[j]); }
        return result;
    }
    static boolean ok(String r) { return r != null && r.startsWith("OK>"); }
    static String message(String r) { return r == null ? "Sin respuesta del servidor" : r.substring(r.indexOf('>')+1); }
}
