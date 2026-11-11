
package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import vista.login.FrmLogin;

public class FrmPrincipal extends JFrame {

    public FrmPrincipal() {

        setTitle("Sistema de Contabilidad");
        setSize(600, 430);
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

       java.awt.EventQueue.invokeLater(
        new Runnable() {

            public void run() {

                new FrmLogin()
                        .setVisible(true);
            }
        });
    }
}
