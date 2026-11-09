package vista;

import controlador.GestorComprobante;
import controlador.LineaComprobante;
import dao.ComprobanteDAO;
import dao.ComprobanteDetalleDAO;
import dao.CuentaContableDAO;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Comprobante;
import modelo.ComprobanteDetalle;
import modelo.CuentaContable;

public class FrmComprobante extends JFrame {

    private JTextField txtNumero;
    private JTextField txtFecha;
    private JTextField txtObservacion;

    private JComboBox<CuentaContable> cboCuenta;

    private JTextField txtDebe;
    private JTextField txtHaber;

    private JButton btnAgregar;
    private JButton btnEliminarDetalle;
    private JButton btnGuardar;
    private JButton btnBuscar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalDebe;
    private JLabel lblTotalHaber;

    public FrmComprobante() {

        setTitle("Comprobante Contable");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializar();
        cargarCuentas();
    }

    private void inicializar() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblNumero = new JLabel("Número:");
        lblNumero.setBounds(30, 20, 100, 25);

        txtNumero = new JTextField();
        txtNumero.setBounds(120, 20, 100, 25);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 20, 100, 25);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(370, 20, 100, 25);

        txtFecha = new JTextField();
        txtFecha.setBounds(430, 20, 150, 25);

        JLabel lblObs = new JLabel("Observación:");
        lblObs.setBounds(30, 60, 100, 25);

        txtObservacion = new JTextField();
        txtObservacion.setBounds(120, 60, 460, 25);

        JLabel lblCuenta = new JLabel("Cuenta:");
        lblCuenta.setBounds(30, 120, 100, 25);

        cboCuenta = new JComboBox<>();
        cboCuenta.setBounds(120, 120, 220, 25);

        JLabel lblDebe = new JLabel("Debe:");
        lblDebe.setBounds(360, 120, 60, 25);

        txtDebe = new JTextField();
        txtDebe.setBounds(420, 120, 100, 25);

        JLabel lblHaber = new JLabel("Haber:");
        lblHaber.setBounds(540, 120, 60, 25);

        txtHaber = new JTextField();
        txtHaber.setBounds(600, 120, 100, 25);

        btnAgregar = new JButton("Agregar Detalle");
        btnAgregar.setBounds(720, 120, 140, 25);

        btnEliminarDetalle = new JButton("Eliminar Detalle");
        btnEliminarDetalle.setBounds(720, 155, 140, 25);

        btnGuardar = new JButton("Guardar Comprobante");
        btnGuardar.setBounds(620, 470, 220, 30);

        modeloTabla = new DefaultTableModel();

        modeloTabla.addColumn("Cuenta");
        modeloTabla.addColumn("Debe");
        modeloTabla.addColumn("Haber");

        tabla = new JTable(modeloTabla);

        JScrollPane scroll =
                new JScrollPane(tabla);

        scroll.setBounds(30, 200, 830, 230);

        lblTotalDebe =
                new JLabel("Total Debe: 0");

        lblTotalDebe.setBounds(30, 470, 200, 25);

        lblTotalHaber =
                new JLabel("Total Haber: 0");

        lblTotalHaber.setBounds(250, 470, 200, 25);

        panel.add(lblNumero);
        panel.add(txtNumero);
        panel.add(btnBuscar);

        panel.add(lblFecha);
        panel.add(txtFecha);

        panel.add(lblObs);
        panel.add(txtObservacion);

        panel.add(lblCuenta);
        panel.add(cboCuenta);

        panel.add(lblDebe);
        panel.add(txtDebe);

        panel.add(lblHaber);
        panel.add(txtHaber);

        panel.add(btnAgregar);
        panel.add(btnEliminarDetalle);
        panel.add(btnGuardar);

        panel.add(scroll);

        panel.add(lblTotalDebe);
        panel.add(lblTotalHaber);

        add(panel, BorderLayout.CENTER);

        btnAgregar.addActionListener(
                e -> agregarDetalle());

        btnEliminarDetalle.addActionListener(
                e -> eliminarDetalle());

        btnGuardar.addActionListener(
                e -> guardarComprobante());

        btnBuscar.addActionListener(
                e -> buscarComprobante());
    }

    private void cargarCuentas() {

        cboCuenta.removeAllItems();

        CuentaContableDAO dao =
                new CuentaContableDAO();

        List<CuentaContable> lista =
                dao.listar();

        for (CuentaContable cuenta : lista) {

            cboCuenta.addItem(cuenta);
        }
    }

    private void agregarDetalle() {

        CuentaContable cuenta =
                (CuentaContable) cboCuenta.getSelectedItem();

        double debe =
                Double.parseDouble(txtDebe.getText());

        double haber =
                Double.parseDouble(txtHaber.getText());

        modeloTabla.addRow(
                new Object[]{
                    cuenta.getNombre(),
                    debe,
                    haber
                });

        calcularTotales();

        txtDebe.setText("");
        txtHaber.setText("");
    }

    private void eliminarDetalle() {

        int fila =
                tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una fila");

            return;
        }

        modeloTabla.removeRow(fila);

        calcularTotales();
    }

    private void calcularTotales() {

        double totalDebe = 0;
        double totalHaber = 0;

        for (int i = 0;
                i < modeloTabla.getRowCount();
                i++) {

            totalDebe += Double.parseDouble(
                    modeloTabla.getValueAt(i, 1).toString());

            totalHaber += Double.parseDouble(
                    modeloTabla.getValueAt(i, 2).toString());
        }

        lblTotalDebe.setText(
                "Total Debe: " + totalDebe);

        lblTotalHaber.setText(
                "Total Haber: " + totalHaber);
    }

    private void guardarComprobante() {

        try {

            List<LineaComprobante> lineas =
                    new ArrayList<>();

            for (int i = 0;
                    i < modeloTabla.getRowCount();
                    i++) {

                String nombreCuenta =
                        modeloTabla.getValueAt(i, 0).toString();

                double debe =
                        Double.parseDouble(
                                modeloTabla.getValueAt(i, 1).toString());

                double haber =
                        Double.parseDouble(
                                modeloTabla.getValueAt(i, 2).toString());

                for (int j = 0;
                        j < cboCuenta.getItemCount();
                        j++) {

                    CuentaContable cuenta =
                            cboCuenta.getItemAt(j);

                    if (cuenta.getNombre().equals(nombreCuenta)) {

                        lineas.add(
                                new LineaComprobante(
                                        cuenta.getIdCuenta(),
                                        cuenta.getNombre(),
                                        debe,
                                        haber));

                        break;
                    }
                }
            }

            GestorComprobante gestor =
                    new GestorComprobante();

            boolean guardado =
                    gestor.guardarComprobante(
                            txtFecha.getText(),
                            txtObservacion.getText(),
                            lineas);

            if (guardado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante guardado correctamente");

                txtNumero.setText("");
                txtFecha.setText("");
                txtObservacion.setText("");

                modeloTabla.setRowCount(0);

                calcularTotales();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage());

        }
    }

    private void buscarComprobante() {

        try {

            int id =
                    Integer.parseInt(
                            txtNumero.getText());

            ComprobanteDAO dao =
                    new ComprobanteDAO();

            Comprobante comp =
                    dao.buscar(id);

            if (comp == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante no encontrado");

                return;
            }

            txtFecha.setText(
                    comp.getFecha().toString());

            txtObservacion.setText(
                    comp.getObservacion());

            modeloTabla.setRowCount(0);

            ComprobanteDetalleDAO detalleDAO =
                    new ComprobanteDetalleDAO();

            List<ComprobanteDetalle> detalles =
                    detalleDAO.listarPorComprobante(id);

            CuentaContableDAO cuentaDAO =
                    new CuentaContableDAO();

            for (ComprobanteDetalle d : detalles) {

                CuentaContable cuenta =
                        cuentaDAO.buscar(
                                d.getIdCuenta());

                modeloTabla.addRow(
                        new Object[]{
                            cuenta.getNombre(),
                            d.getDebe(),
                            d.getHaber()
                        });
            }

            calcularTotales();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new FrmComprobante().setVisible(true);

        });
    }
}