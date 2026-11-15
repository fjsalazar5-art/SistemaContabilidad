package ec.edu.espe.appdistclient.interfaz.cuentasporcobrar;
import java.awt.*;import java.util.*;import javax.swing.*;import javax.swing.table.DefaultTableModel;

/** Ventana CRUD reutilizable para Cobradores y Formas de pago. */
class CatalogoCxcView extends JFrame {
 private final String entity,title; private final String[] fields,labels; private final JTable table; private final DefaultTableModel model; private final java.util.List<JTextField> inputs=new ArrayList<>(); private int selectedId=-1;
 CatalogoCxcView(String entity,String title,String[] fields,String[] labels){super(title+" | Alejandro Simba");this.entity=entity;this.title=title;this.fields=fields;this.labels=labels;
  JPanel root=new JPanel(new BorderLayout(0,12));root.setBackground(TemaCxc.BG);root.add(TemaCxc.header(title,"Administracion de catalogo | ALEJANDRO SIMBA"),BorderLayout.NORTH);
  JPanel body=new JPanel(new BorderLayout(0,10));body.setOpaque(false);body.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
  JPanel form=new JPanel(new GridBagLayout());form.setBorder(BorderFactory.createTitledBorder("Datos del registro"));GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(5,7,5,7);g.fill=GridBagConstraints.HORIZONTAL;
  for(int i=0;i<labels.length;i++){g.gridx=0;g.gridy=i;g.weightx=0;form.add(new JLabel(labels[i]+":"),g);JTextField t=new JTextField(22);inputs.add(t);g.gridx=1;g.weightx=1;form.add(t,g);}
  JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));actions.setOpaque(false);JButton save=new JButton("Guardar"),update=new JButton("Modificar"),del=new JButton("Eliminar"),clear=new JButton("Limpiar"),refresh=new JButton("Actualizar lista");actions.add(save);actions.add(update);actions.add(del);actions.add(clear);actions.add(refresh);g.gridx=0;g.gridy=labels.length;g.gridwidth=2;form.add(actions,g);
  model=new DefaultTableModel(labels,0){public boolean isCellEditable(int r,int c){return false;}};table=new JTable(model);JScrollPane sp=new JScrollPane(table);sp.setBorder(BorderFactory.createTitledBorder("Registros registrados"));body.add(form,BorderLayout.NORTH);body.add(sp,BorderLayout.CENTER);root.add(body,BorderLayout.CENTER);setContentPane(root);
  save.addActionListener(e->save());update.addActionListener(e->update());del.addActionListener(e->delete());clear.addActionListener(e->clear());refresh.addActionListener(e->load());table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())select();});
  TemaCxc.finish(this,850,650);load(); }
 private String[] values(){String[] a=new String[inputs.size()];for(int i=0;i<a.length;i++)a[i]=inputs.get(i).getText().trim();return a;}
 private boolean valid(){for(int i=0;i<inputs.size();i++)if(inputs.get(i).getText().trim().isEmpty()){TemaCxc.error(this,"Complete el campo: "+labels[i]);return false;}return true;}
 private void save(){if(!valid())return;String r=CxcApi.request(entity,"INSERTAR",values());if(CxcApi.ok(r)){TemaCxc.info(this,CxcApi.message(r));clear();load();}else TemaCxc.error(this,CxcApi.message(r));}
 private void update(){if(selectedId<0){TemaCxc.error(this,"Seleccione un registro de la tabla.");return;}if(!valid())return;String[] v=new String[inputs.size()+1];v[0]=String.valueOf(selectedId);System.arraycopy(values(),0,v,1,inputs.size());String r=CxcApi.request(entity,"MODIFICAR",v);if(CxcApi.ok(r)){TemaCxc.info(this,CxcApi.message(r));clear();load();}else TemaCxc.error(this,CxcApi.message(r));}
 private void delete(){if(selectedId<0){TemaCxc.error(this,"Seleccione un registro.");return;}if(JOptionPane.showConfirmDialog(this,"¿Eliminar el registro seleccionado?","Confirmar",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;String r=CxcApi.request(entity,"ELIMINAR",String.valueOf(selectedId));if(CxcApi.ok(r)){TemaCxc.info(this,CxcApi.message(r));clear();load();}else TemaCxc.error(this,CxcApi.message(r));}
 private void load(){model.setRowCount(0);String r=CxcApi.request(entity,"LISTAR");if(!CxcApi.ok(r)){TemaCxc.error(this,CxcApi.message(r));return;}for(String[] row:CxcApi.rows(r)){Object[] shown=new Object[row.length-1];System.arraycopy(row,1,shown,0,shown.length);model.addRow(shown);}}
 private void select(){int row=table.getSelectedRow();if(row<0)return;String r=CxcApi.request(entity,"LISTAR");if(!CxcApi.ok(r))return;String[][] rows=CxcApi.rows(r);if(row>=rows.length)return;selectedId=Integer.parseInt(rows[row][0]);for(int i=0;i<inputs.size()&&i+1<rows[row].length;i++)inputs.get(i).setText(rows[row][i+1]);}
 private void clear(){selectedId=-1;for(JTextField t:inputs)t.setText("");table.clearSelection();}
}
