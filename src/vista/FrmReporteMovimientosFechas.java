package vista;

import dao.CuentaBancariaDAO;
import dao.MovimientoBancarioDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CuentaBancaria;

/** MODULO BANCOS - Reporte B: Movimientos por Fechas. */
public class FrmReporteMovimientosFechas extends JFrame {

    private final JTextField txtDesde = new JTextField(10);
    private final JTextField txtHasta = new JTextField(10);
    private final JComboBox<Object> cboCuenta = new JComboBox<>();
    private final DefaultTableModel modelo;
    private final JLabel lblResumen = new JLabel();
    private final DecimalFormat f = new DecimalFormat("#,##0.00");

    private static final String TODAS = "-- Todas las cuentas --";

    public FrmReporteMovimientosFechas() {

        setTitle("Bancos - Reporte: Movimientos por Fechas");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        LocalDate hoy = LocalDate.now();
        txtDesde.setText(hoy.withDayOfMonth(1).toString());
        txtHasta.setText(hoy.toString());

        cboCuenta.addItem(TODAS);
        for (CuentaBancaria c : new CuentaBancariaDAO().listar()) {
            cboCuenta.addItem(c);
        }

        JButton btnGenerar = new JButton("Generar");

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Desde (yyyy-MM-dd):"));
        arriba.add(txtDesde);
        arriba.add(new JLabel("Hasta:"));
        arriba.add(txtHasta);
        arriba.add(new JLabel("Cuenta:"));
        arriba.add(cboCuenta);
        arriba.add(btnGenerar);

        modelo = new DefaultTableModel(new Object[]{
            "Fecha", "N° Mov.", "Cuenta Bancaria", "Tipo Transacción", "Signo",
            "Ingreso", "Egreso", "Saldo acumulado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblResumen.setFont(lblResumen.getFont().deriveFont(14f));
        abajo.add(lblResumen);

        principal.add(arriba, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(abajo, BorderLayout.SOUTH);
        add(principal);

        btnGenerar.addActionListener(e -> generar());
        generar();
    }

    private void generar() {

        Date desde;
        Date hasta;
        try {
            desde = Date.valueOf(txtDesde.getText().trim());
            hasta = Date.valueOf(txtHasta.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Fechas inválidas. Use yyyy-MM-dd");
            return;
        }
        if (desde.after(hasta)) {
            JOptionPane.showMessageDialog(this, "La fecha 'Desde' no puede ser mayor que 'Hasta'");
            return;
        }

        Object sel = cboCuenta.getSelectedItem();
        int idCuenta = (sel instanceof CuentaBancaria c) ? c.getIdCuentaBanco() : 0;

        modelo.setRowCount(0);
        double ingresos = 0;
        double egresos = 0;
        double saldo = 0;

        List<Object[]> filas = new MovimientoBancarioDAO()
                .reporteMovimientosPorFechas(desde, hasta, idCuenta);

        for (Object[] r : filas) {
            double valor = (Double) r[5];
            boolean ingreso = "+".equals(r[4]);
            if (ingreso) {
                ingresos += valor;
                saldo += valor;
            } else {
                egresos += valor;
                saldo -= valor;
            }
            modelo.addRow(new Object[]{
                r[0], r[1], r[2], r[3], r[4],
                ingreso ? f.format(valor) : "",
                ingreso ? "" : f.format(valor),
                f.format(saldo)
            });
        }

        lblResumen.setText("Registros: " + filas.size()
                + "    Ingresos: " + f.format(ingresos)
                + "    Egresos: " + f.format(egresos)
                + "    Neto del período: " + f.format(ingresos - egresos));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmReporteMovimientosFechas().setVisible(true));
    }
}
