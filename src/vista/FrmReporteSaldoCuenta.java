package vista;

import dao.MovimientoBancarioDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** MODULO BANCOS - Reporte A: Saldo por Cuenta Bancaria. */
public class FrmReporteSaldoCuenta extends JFrame {

    private final DefaultTableModel modelo;
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblGenerado = new JLabel();
    private final DecimalFormat f = new DecimalFormat("#,##0.00");

    public FrmReporteSaldoCuenta() {

        setTitle("Bancos - Reporte: Saldo por Cuenta");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titulo = new JLabel("REPORTE DE SALDO POR CUENTA BANCARIA");
        titulo.setFont(titulo.getFont().deriveFont(16f));
        JButton btnActualizar = new JButton("Actualizar");
        arriba.add(titulo);
        arriba.add(Box.createHorizontalStrut(30));
        arriba.add(btnActualizar);
        arriba.add(lblGenerado);

        modelo = new DefaultTableModel(new Object[]{
            "ID", "N° Cuenta", "Cuenta", "Titular", "Ingresos (+)", "Egresos (-)", "SALDO"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal.setFont(lblTotal.getFont().deriveFont(14f));
        abajo.add(lblTotal);

        principal.add(arriba, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(abajo, BorderLayout.SOUTH);
        add(principal);

        btnActualizar.addActionListener(e -> cargar());
        cargar();
    }

    private void cargar() {

        modelo.setRowCount(0);
        double total = 0;

        List<Object[]> filas = new MovimientoBancarioDAO().reporteSaldoPorCuenta();
        for (Object[] r : filas) {
            double saldo = (Double) r[6];
            total += saldo;
            modelo.addRow(new Object[]{
                r[0], r[1], r[2], r[3],
                f.format((Double) r[4]), f.format((Double) r[5]), f.format(saldo)
            });
        }

        lblTotal.setText("Cuentas: " + filas.size() + "     SALDO TOTAL EN BANCOS: $ " + f.format(total));
        lblGenerado.setText("   Generado: "
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmReporteSaldoCuenta().setVisible(true));
    }
}
