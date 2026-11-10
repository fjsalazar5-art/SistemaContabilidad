
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

    private final TipoCuentaDAO dao = new TipoCuentaDAO();

    public FrmTipoCuenta() {

        setTitle("Tipo Cuenta");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblId = new JLabel("ID Tipo Cuenta:");
        lblId.setBounds(30, 30, 120, 25);

        txtId = new JTextField();
        txtId.setBounds(150, 30, 200, 25);
        txtId.setEditable(false);

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

    // GUARDAR
    private void guardar() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre del tipo de cuenta");
            txtNombre.requestFocus();
            return;
        }

        TipoCuenta tipo = new TipoCuenta();
        tipo.setNombre(nombre);

        if (dao.guardar(tipo)) {

            txtId.setText(
                    String.valueOf(tipo.getIdTipoCuenta()));

            JOptionPane.showMessageDialog(
                    this,
                    "Registro guardado correctamente.\nID generado: "
                    + tipo.getIdTipoCuenta());

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar el registro");
        }
    }

    // BUSCAR
    private void buscar() {

        String idTexto = JOptionPane.showInputDialog(
                this,
                "Ingrese el ID del tipo de cuenta:");

        if (idTexto == null) {
            return;
        }

        try {

            int id = Integer.parseInt(idTexto.trim());

            TipoCuenta tipo = dao.buscar(id);

            if (tipo != null) {

                txtId.setText(
                        String.valueOf(tipo.getIdTipoCuenta()));

                txtNombre.setText(tipo.getNombre());

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el tipo de cuenta");

                limpiar();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un ID numérico válido");
        }
    }

    // MODIFICAR
    private void modificar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe buscar un registro");
            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre del tipo de cuenta");
            return;
        }

        TipoCuenta tipo = new TipoCuenta();

        tipo.setIdTipoCuenta(
                Integer.parseInt(txtId.getText()));

        tipo.setNombre(nombre);

        if (dao.modificar(tipo)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro modificado correctamente");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo modificar el registro");
        }
    }

    // ELIMINAR
    private void eliminar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe buscar un registro");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este registro?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        if (dao.eliminar(id)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro eliminado correctamente");

            limpiar();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar el registro");
        }
    }

    // LIMPIAR
    private void limpiar() {

        txtId.setText("");
        txtNombre.setText("");
        txtNombre.requestFocus();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new FrmTipoCuenta().setVisible(true);
        });
    }
}
