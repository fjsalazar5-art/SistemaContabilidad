package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;
import java.awt.*;import javax.swing.*;import javax.swing.table.DefaultTableModel;
public class ReporteCxcView extends JFrame {
 private final boolean porCobrador;private final JTextField filtro=new JTextField(16),desde=new JTextField("2026-01-01",10),hasta=new JTextField(java.time.LocalDate.now().toString(),10);private final DefaultTableModel model=new DefaultTableModel(){public boolean isCellEditable(int r,int c){return false;}};private final JTable table=new JTable(model);
 public ReporteCxcView(boolean porCobrador){super((porCobrador?"Cobros por cobrador":"Estado de cuenta cliente")+" | Alejandro Simba");this.porCobrador=porCobrador;JPanel root=new JPanel(new BorderLayout(0,10));root.setBackground(TemaCxc.BG);root.add(TemaCxc.header(porCobrador?"COBROS POR COBRADOR":"ESTADO DE CUENTA CLIENTE","Reportes de Cuentas por Cobrar | ALEJANDRO SIMBA"),BorderLayout.NORTH);JPanel body=new JPanel(new BorderLayout(0,10));body.setOpaque(false);body.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));JPanel search=new JPanel(new FlowLayout(FlowLayout.LEFT));search.setBorder(BorderFactory.createTitledBorder("Parámetros de consulta"));if(porCobrador){search.add(new JLabel("Desde (AAAA-MM-DD):"));search.add(desde);search.add(new JLabel("Hasta:"));search.add(hasta);}else{search.add(new JLabel("RUC del cliente:"));search.add(filtro);search.add(new JLabel("Desde:"));search.add(desde);search.add(new JLabel("Hasta:"));search.add(hasta);}JButton go=new JButton("Consultar"),imprimir=new JButton("Imprimir");search.add(go);search.add(imprimir);body.add(search,BorderLayout.NORTH);body.add(new JScrollPane(table),BorderLayout.CENTER);root.add(body,BorderLayout.CENTER);setContentPane(root);go.addActionListener(e->query());imprimir.addActionListener(e->{try{table.print();}catch(Exception ex){TemaCxc.error(this,ex.getMessage());}});TemaCxc.finish(this,1000,620);}
 private void query(){model.setRowCount(0);model.setColumnCount(0);String r=porCobrador?CxcApi.request("REPORTE","COBROS_COBRADOR",desde.getText().trim(),hasta.getText().trim()):CxcApi.request("REPORTE","ESTADO_CLIENTE",filtro.getText().trim(),desde.getText().trim(),hasta.getText().trim());if(!CxcApi.ok(r)){TemaCxc.error(this,CxcApi.message(r));return;}String[][] rows=CxcApi.rows(r);if(rows.length<=1){TemaCxc.info(this,"No se encontraron registros para los parámetros indicados.");return;}if(porCobrador){mostrarMatriz(rows);return;}for(int i=0;i<rows.length;i++){if(i==0)for(String h:rows[i])model.addColumn(h);else model.addRow(rows[i]);}}

 /** Matriz solicitada: cobradores en filas y formas de pago en columnas. */
 private void mostrarMatriz(String[][] rows){
  java.util.SortedSet<String> formas=new java.util.TreeSet<>();
  java.util.Map<String,java.util.Map<String,java.math.BigDecimal>> datos=new java.util.TreeMap<>();
  for(int i=1;i<rows.length;i++){
   String[] r=rows[i];if(r.length<3)continue;
   String cobrador=r[0],fp=r[1];
   java.math.BigDecimal valor=new java.math.BigDecimal(r[2]);
   formas.add(fp);datos.computeIfAbsent(cobrador,k->new java.util.TreeMap<>()).merge(fp,valor,java.math.BigDecimal::add);
  }
  model.addColumn("COBRADOR");for(String forma:formas)model.addColumn(forma);model.addColumn("TOTAL");
  java.util.Map<String,java.math.BigDecimal> totales=new java.util.HashMap<>();
  java.math.BigDecimal general=java.math.BigDecimal.ZERO;
  for(var entry:datos.entrySet()){
   java.util.List<Object> fila=new java.util.ArrayList<>();fila.add(entry.getKey());
   java.math.BigDecimal total=java.math.BigDecimal.ZERO;
   for(String fp:formas){
    java.math.BigDecimal valor=entry.getValue().getOrDefault(fp,java.math.BigDecimal.ZERO);
    fila.add(valor.setScale(2).toPlainString());total=total.add(valor);
    totales.merge(fp,valor,java.math.BigDecimal::add);
   }
   fila.add(total.setScale(2).toPlainString());model.addRow(fila.toArray());general=general.add(total);
  }
  java.util.List<Object> resumen=new java.util.ArrayList<>();resumen.add("TOTAL");
  for(String fp:formas)resumen.add(totales.getOrDefault(fp,java.math.BigDecimal.ZERO).setScale(2).toPlainString());
  resumen.add(general.setScale(2).toPlainString());model.addRow(resumen.toArray());
 }
}
