package vista;

import dao.CuentaContableDAO;
import dao.TipoCuentaDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import modelo.CuentaContable;

public class FrmCuentaContable extends JFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTipoCuenta;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;

    public FrmCuentaContable() {

        setTitle("Cuenta Contable");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblId = new JLabel("ID Cuenta:");
        lblId.setBounds(30, 30, 120, 25);

        txtId = new JTextField();
        txtId.setBounds(150, 30, 200, 25);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 70, 120, 25);

        txtNombre = new JTextField();
        txtNombre.setBounds(150, 70, 200, 25);

        JLabel lblTipo = new JLabel("ID Tipo Cuenta:");
        lblTipo.setBounds(30, 110, 120, 25);

        txtTipoCuenta = new JTextField();
        txtTipoCuenta.setBounds(150, 110, 200, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, 180, 90, 30);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(115, 180, 90, 30);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(210, 180, 90, 30);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(305, 180, 100, 30);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(160, 220, 100, 30);

        panel.add(lblId);
        panel.add(txtId);

        panel.add(lblNombre);
        panel.add(txtNombre);

        panel.add(lblTipo);
        panel.add(txtTipoCuenta);

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnBuscar);
        panel.add(btnModificar);
        panel.add(btnEliminar);

        add(panel);

        eventos();
    }

    private void eventos() {

        btnGuardar.addActionListener(e -> guardar());

        btnBuscar.addActionListener(e -> buscar());

        btnModificar.addActionListener(e -> modificar());

        btnEliminar.addActionListener(e -> eliminar());

        btnNuevo.addActionListener(e -> limpiar());
    }

    private void guardar() {

        CuentaContable cuenta =
                new CuentaContable();

        cuenta.setIdCuenta(
                Integer.parseInt(txtId.getText()));

        cuenta.setNombre(
                txtNombre.getText());

        cuenta.setIdTipoCuenta(
                Integer.parseInt(txtTipoCuenta.getText()));

        CuentaContableDAO dao =
                new CuentaContableDAO();

        if (dao.guardar(cuenta)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta guardada");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error");
        }
    }

    private void buscar() {

        CuentaContableDAO dao =
                new CuentaContableDAO();

        CuentaContable cuenta =
                dao.buscar(
                        Integer.parseInt(txtId.getText()));

        if (cuenta != null) {

            txtNombre.setText(
                    cuenta.getNombre());

            txtTipoCuenta.setText(
                    String.valueOf(
                            cuenta.getIdTipoCuenta()));

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No encontrada");
        }
    }

    private void modificar() {

        CuentaContable cuenta =
                new CuentaContable();

        cuenta.setIdCuenta(
                Integer.parseInt(txtId.getText()));

        cuenta.setNombre(
                txtNombre.getText());

        cuenta.setIdTipoCuenta(
                Integer.parseInt(txtTipoCuenta.getText()));

        CuentaContableDAO dao =
                new CuentaContableDAO();

        if (dao.modificar(cuenta)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta modificada");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error");
        }
    }

    private void eliminar() {

        CuentaContableDAO dao =
                new CuentaContableDAO();

        if (dao.eliminar(
                Integer.parseInt(txtId.getText()))) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta eliminada");

            limpiar();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error");
        }
    }

    private void limpiar() {

        txtId.setText("");
        txtNombre.setText("");
        txtTipoCuenta.setText("");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new FrmCuentaContable().setVisible(true);
        });

    }
}