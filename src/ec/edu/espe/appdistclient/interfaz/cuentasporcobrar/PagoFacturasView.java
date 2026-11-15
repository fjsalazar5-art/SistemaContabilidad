package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;
import java.awt.*;import java.time.LocalDate;import javax.swing.*;import javax.swing.table.DefaultTableModel;
public class PagoFacturasView extends JFrame {
 private final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Factura","Fecha","Cliente","Identificación","Total","Pagado","Saldo","Estado"},0){public boolean isCellEditable(int r,int c){return false;}};
 private final JTable table=new JTable(model);private final JComboBox<Item> cobrador=new JComboBox<>(),forma=new JComboBox<>();private final JTextField monto=new JTextField(10),referencia=new JTextField(14);private final JComboBox<Item> debe=new JComboBox<>(),haber=new JComboBox<>();private final JLabel saldo=new JLabel("Seleccione una factura");private int invoiceId=-1;private String invoiceNumber="";
 public PagoFacturasView(){super("Pago de facturas | Alejandro Simba");JPanel root=new JPanel(new BorderLayout(0,10));root.setBackground(TemaCxc.BG);root.add(TemaCxc.header("PAGO DE FACTURAS","Registro de pagos y abonos | ALEJANDRO SIMBA"),BorderLayout.NORTH);JPanel center=new JPanel(new BorderLayout(0,10));center.setOpaque(false);center.setBorder(BorderFactory.createEmptyBorder(14,16,14,16));center.add(new JScrollPane(table),BorderLayout.CENTER);
 JPanel pay=new JPanel(new GridBagLayout());pay.setBorder(BorderFactory.createTitledBorder("Registrar cobro de la factura seleccionada"));GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(5,6,5,6);g.fill=GridBagConstraints.HORIZONTAL;int y=0;add(pay,g,0,y,"Cobrador",cobrador);add(pay,g,2,y,"Forma de pago",forma);y++;add(pay,g,0,y,"Monto del pago",monto);add(pay,g,2,y,"Referencia",referencia);y++;add(pay,g,0,y,"Cuenta DEBE (banco/caja)",debe);add(pay,g,2,y,"Cuenta HABER (CxC)",haber);y++;JButton payBtn=new JButton("Registrar pago"),refresh=new JButton("Actualizar facturas");g.gridx=0;g.gridy=y;g.gridwidth=1;pay.add(payBtn,g);g.gridx=1;pay.add(refresh,g);g.gridx=2;g.gridwidth=2;pay.add(saldo,g);center.add(pay,BorderLayout.SOUTH);root.add(center,BorderLayout.CENTER);setContentPane(root);
 table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())selectInvoice();});payBtn.addActionListener(e->pay());refresh.addActionListener(e->load());TemaCxc.finish(this,1120,700);loadCatalogs();load();}
 private void add(JPanel p,GridBagConstraints g,int x,int y,String label,JComponent c){g.gridx=x;g.gridy=y;g.gridwidth=1;g.weightx=0;p.add(new JLabel(label+":"),g);g.gridx=x+1;g.weightx=1;p.add(c,g);}
 private void loadCatalogs(){loadCombo("COBRADOR",cobrador);loadCombo("FORMA_PAGO",forma);loadCombo("CUENTA",debe);loadCombo("CUENTA",haber);seleccionarCuenta(debe,"CAJA");seleccionarCuenta(haber,"COBRAR");}
 private void loadCombo(String entity,JComboBox<Item> combo){
    combo.removeAllItems();String r=CxcApi.request(entity,"LISTAR");
    if(!CxcApi.ok(r))return;
    for(String[] row:CxcApi.rows(r)) if(row.length>1){
        int nombre=entity.equals("COBRADOR")?2:1;
        if(row.length>nombre)combo.addItem(new Item(row[0],row[nombre]));
    }
 }
 private void seleccionarCuenta(JComboBox<Item> combo,String parteNombre){
    for(int i=0;i<combo.getItemCount();i++)if(combo.getItemAt(i).name.toUpperCase().contains(parteNombre)){
        combo.setSelectedIndex(i);return;
    }
 }
 private void load(){model.setRowCount(0);String r=CxcApi.request("FACTURA","LISTAR");if(!CxcApi.ok(r)){TemaCxc.error(this,CxcApi.message(r));return;}for(String[] a:CxcApi.rows(r))if(a.length>8 && !a[7].equals("0.00") && !a[7].equals("0"))model.addRow(a);invoiceId=-1;saldo.setText("Seleccione una factura");}
 private void selectInvoice(){int r=table.getSelectedRow();if(r<0)return;invoiceId=Integer.parseInt(model.getValueAt(r,0).toString());invoiceNumber=model.getValueAt(r,1).toString();saldo.setText("Saldo pendiente: $"+model.getValueAt(r,7));}
 private void pay(){if(invoiceId<0){TemaCxc.error(this,"Seleccione una factura pendiente.");return;}try{double amount=Double.parseDouble(monto.getText().trim().replace(',','.'));if(amount<=0)throw new NumberFormatException();Item c=(Item)cobrador.getSelectedItem(),f=(Item)forma.getSelectedItem();if(c==null||f==null)throw new IllegalStateException("Registre y active un cobrador y una forma de pago.");Item debeSeleccionada=(Item)debe.getSelectedItem(),haberSeleccionada=(Item)haber.getSelectedItem();
    if(debeSeleccionada==null||haberSeleccionada==null)throw new IllegalStateException("Primero cree las cuentas DEBE y HABER en Contabilidad.");
    if(debeSeleccionada.id.equals(haberSeleccionada.id))throw new IllegalStateException("DEBE y HABER no pueden ser la misma cuenta.");
    String r=CxcApi.request("PAGO","REGISTRAR",String.valueOf(invoiceId),c.id,f.id,LocalDate.now().toString(),String.format(java.util.Locale.US,"%.2f",amount),referencia.getText().trim(),debeSeleccionada.id,haberSeleccionada.id);if(CxcApi.ok(r)){TemaCxc.info(this,CxcApi.message(r));monto.setText("");referencia.setText("");load();}else TemaCxc.error(this,CxcApi.message(r));}catch(NumberFormatException ex){TemaCxc.error(this,"Ingrese un monto válido mayor que cero.");}catch(Exception ex){TemaCxc.error(this,ex.getMessage());}}
 private static class Item{String id,name;Item(String i,String n){id=i;name=n;}public String toString(){return name;}}
}
