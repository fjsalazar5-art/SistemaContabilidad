package vista;

import dao.TipoCuentaDAO;
import javax.swing.*;
import modelo.TipoCuenta;
import java.awt.*;

public class FrmTipoCuenta extends JFrame {

    private JTextField txtId;
    private JTextField txtNombre;

    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnNuevo;

    public FrmTipoCuenta() {

        setTitle("Tipo Cuenta");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblId = new JLabel("ID Tipo Cuenta:");
        lblId.setBounds(30, 30, 120, 25);

        txtId = new JTextField();
        txtId.setBounds(150, 30, 200, 25);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 70, 120, 25);

        txtNombre = new JTextField();
        txtNombre.setBounds(150, 70, 200, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, 130, 90, 30);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(115, 130, 90, 30);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(210, 130, 90, 30);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(305, 130, 100, 30);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(170, 170, 100, 30);

        panel.add(lblId);
        panel.add(txtId);

        panel.add(lblNombre);
        panel.add(txtNombre);

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

        TipoCuenta tipo = new TipoCuenta();

        tipo.setIdTipoCuenta(
                Integer.parseInt(txtId.getText()));

        tipo.setNombre(
                txtNombre.getText());

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.guardar(tipo)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro guardado");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar");
        }
    }

    private void buscar() {

        TipoCuentaDAO dao = new TipoCuentaDAO();

        TipoCuenta tipo =
                dao.buscar(
                        Integer.parseInt(txtId.getText()));

        if (tipo != null) {

            txtNombre.setText(
                    tipo.getNombre());

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No encontrado");
        }
    }

    private void modificar() {

        TipoCuenta tipo = new TipoCuenta();

        tipo.setIdTipoCuenta(
                Integer.parseInt(txtId.getText()));

        tipo.setNombre(
                txtNombre.getText());

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.modificar(tipo)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro modificado");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error");
        }
    }

    private void eliminar() {

        TipoCuentaDAO dao = new TipoCuentaDAO();

        if (dao.eliminar(
                Integer.parseInt(txtId.getText()))) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro eliminado");

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
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new FrmTipoCuenta().setVisible(true);
        });

    }
}