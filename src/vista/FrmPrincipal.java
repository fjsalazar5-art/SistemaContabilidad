
package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FrmPrincipal extends JFrame {

    public FrmPrincipal() {

        setTitle("Sistema de Contabilidad");
        setSize(600, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("SISTEMA DE CONTABILIDAD");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(80, 30, 440, 40);
        panel.add(titulo);

        JButton btnTipoCuenta = crearBoton("Tipos de Cuenta", 80, 110);
        JButton btnCuentaContable = crearBoton("Cuentas Contables", 320, 110);
        JButton btnComprobante = crearBoton("Comprobantes", 80, 185);
        JButton btnBalanceGeneral = crearBoton("Balance General", 320, 185);
        JButton btnEstadoResultados = crearBoton("Estado de Resultados", 200, 260);

        panel.add(btnTipoCuenta);
        panel.add(btnCuentaContable);
        panel.add(btnComprobante);
        panel.add(btnBalanceGeneral);
        panel.add(btnEstadoResultados);

        // ---------------- MODULO BANCOS ----------------
        JLabel lblBancos = new JLabel("MÓDULO BANCOS");
        lblBancos.setFont(new Font("Arial", Font.BOLD, 16));
        lblBancos.setHorizontalAlignment(SwingConstants.CENTER);
        lblBancos.setBounds(80, 325, 440, 25);
        panel.add(lblBancos);

        JButton btnTipoTransaccion = crearBoton("Tipo Transacción", 80, 360);
        JButton btnCuentaBancaria = crearBoton("Cuenta Bancaria", 320, 360);
        JButton btnMovimientoBancario = crearBoton("Movimiento Bancario", 80, 420);
        JButton btnSaldoCuenta = crearBoton("Rep. Saldo por Cuenta", 320, 420);
        JButton btnMovFechas = crearBoton("Rep. Movimientos Fechas", 200, 480);

        panel.add(btnTipoTransaccion);
        panel.add(btnCuentaBancaria);
        panel.add(btnMovimientoBancario);
        panel.add(btnSaldoCuenta);
        panel.add(btnMovFechas);

        btnTipoTransaccion.addActionListener(e ->
                abrirVentana(new FrmTipoTransaccion()));

        btnCuentaBancaria.addActionListener(e ->
                abrirVentana(new FrmCuentaBancaria()));

        btnMovimientoBancario.addActionListener(e ->
                abrirVentana(new FrmMovimientoBancario()));

        btnSaldoCuenta.addActionListener(e ->
                abrirVentana(new FrmReporteSaldoCuenta()));

        btnMovFechas.addActionListener(e ->
                abrirVentana(new FrmReporteMovimientosFechas()));

        btnTipoCuenta.addActionListener(e ->
                abrirVentana(new FrmTipoCuenta()));

        btnCuentaContable.addActionListener(e ->
                abrirVentana(new FrmCuentaContable()));

        btnComprobante.addActionListener(e ->
                abrirVentana(new FrmComprobante()));

        btnBalanceGeneral.addActionListener(e ->
                abrirVentana(new FrmBalanceGeneral()));

        btnEstadoResultados.addActionListener(e ->
                abrirVentana(new FrmEstadoResultados()));

        setContentPane(panel);
    }

    private JButton crearBoton(String texto, int x, int y) {

        JButton boton = new JButton(texto);
        boton.setBounds(x, y, 200, 45);
        boton.setFont(new Font("Arial", Font.BOLD, 13));

        return boton;
    }

    private void abrirVentana(JFrame ventana) {

        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new FrmPrincipal().setVisible(true);
        });
    }
}
