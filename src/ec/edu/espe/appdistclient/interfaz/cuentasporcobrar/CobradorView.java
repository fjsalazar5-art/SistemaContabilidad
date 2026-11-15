package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;
/** Formulario original ajustado a COBRADOR(ID_COBRADOR,CEDULA,NOMBRE,DIRECCION). */
public class CobradorView extends CatalogoCxcView {
    public CobradorView() {
        super("COBRADOR", "Cobradores", new String[]{"cedula","nombre","direccion"},
                new String[]{"Cédula", "Nombre completo", "Dirección"});
    }
}
