package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;
/** Formulario original ajustado a FORMA_PAGO(ID_FORMA_PAGO,NOMBRE). */
public class FormaPagoView extends CatalogoCxcView {
    public FormaPagoView() {
        super("FORMA_PAGO", "Formas de pago", new String[]{"nombre"},new String[]{"Nombre"});
    }
}
