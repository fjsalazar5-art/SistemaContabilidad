
package vista;

import dao.CuentaContableDAO;
import dao.TipoCuentaDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import modelo.CuentaContable;
import modelo.TipoCuenta;

public class FrmCuentaContable extends JFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<TipoCuenta> cmbTipoCuenta;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;

    private final CuentaContableDAO cuentaDAO = new CuentaContableDAO();
    private final TipoCuentaDAO tipoDAO = new TipoCuentaDAO();

    public FrmCuentaContable() {

        setTitle("Cuenta Contable");
        setSize(560, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        inicializar();
        cargarTiposCuenta();
    }

    private void inicializar() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblId = new JLabel("ID Cuenta:");
        lblId.setBounds(40, 35, 120, 25);

        txtId = new JTextField();
        txtId.setBounds(170, 35, 300, 25);
        txtId.setEditable(false);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(40, 85, 120, 25);

        txtNombre = new JTextField();
        txtNombre.setBounds(170, 85, 300, 25);

        JLabel lblTipo = new JLabel("Tipo de Cuenta:");
        lblTipo.setBounds(40, 135, 120, 25);

        cmbTipoCuenta = new JComboBox<>();
        cmbTipoCuenta.setBounds(170, 135, 300, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, 220, 95, 35);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(125, 220, 105, 35);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 220, 95, 35);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(345, 220, 105, 35);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(455, 220, 95, 35);

        panel.add(lblId);
        panel.add(txtId);

        panel.add(lblNombre);
        panel.add(txtNombre);

        panel.add(lblTipo);
        panel.add(cmbTipoCuenta);

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnBuscar);
        panel.add(btnModificar);
        panel.add(btnEliminar);

        add(panel);

        eventos();
    }

    private void cargarTiposCuenta() {

        cmbTipoCuenta.removeAllItems();

        List<TipoCuenta> tipos = tipoDAO.listar();

        for (TipoCuenta tipo : tipos) {
            cmbTipoCuenta.addItem(tipo);
        }

        if (tipos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No existen tipos de cuenta registrados");
        }
    }

    private void eventos() {

        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> buscar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnNuevo.addActionListener(e -> limpiar());
    }

    private void guardar() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre de la cuenta");
            txtNombre.requestFocus();
            return;
        }

        TipoCuenta tipoSeleccionado =
                (TipoCuenta) cmbTipoCuenta.getSelectedItem();

        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un tipo de cuenta");
            return;
        }

        CuentaContable cuenta = new CuentaContable();

        cuenta.setNombre(nombre);
        cuenta.setIdTipoCuenta(
                tipoSeleccionado.getIdTipoCuenta());

        if (cuentaDAO.guardar(cuenta)) {

            txtId.setText(String.valueOf(cuenta.getIdCuenta()));

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta guardada correctamente.\nID generado: "
                    + cuenta.getIdCuenta());

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar la cuenta");
        }
    }

    private void buscar() {

        String idTexto = JOptionPane.showInputDialog(
                this,
                "Ingrese el ID de la cuenta:");

        if (idTexto == null) {
            return;
        }

        try {

            int id = Integer.parseInt(idTexto.trim());

            CuentaContable cuenta = cuentaDAO.buscar(id);

            if (cuenta != null) {

                txtId.setText(String.valueOf(cuenta.getIdCuenta()));
                txtNombre.setText(cuenta.getNombre());

                seleccionarTipoCuenta(cuenta.getIdTipoCuenta());

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró la cuenta");

                limpiar();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un ID numérico válido");
        }
    }

    private void seleccionarTipoCuenta(int idTipo) {

        for (int i = 0; i < cmbTipoCuenta.getItemCount(); i++) {

            TipoCuenta tipo = cmbTipoCuenta.getItemAt(i);

            if (tipo.getIdTipoCuenta() == idTipo) {
                cmbTipoCuenta.setSelectedIndex(i);
                return;
            }
        }
    }

    private void modificar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe buscar una cuenta");
            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre de la cuenta");
            return;
        }

        TipoCuenta tipoSeleccionado =
                (TipoCuenta) cmbTipoCuenta.getSelectedItem();

        if (tipoSeleccionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un tipo de cuenta");
            return;
        }

        CuentaContable cuenta = new CuentaContable();

        cuenta.setIdCuenta(Integer.parseInt(txtId.getText()));
        cuenta.setNombre(nombre);
        cuenta.setIdTipoCuenta(
                tipoSeleccionado.getIdTipoCuenta());

        if (cuentaDAO.modificar(cuenta)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta modificada correctamente");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo modificar la cuenta");
        }
    }

    private void eliminar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe buscar una cuenta");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar esta cuenta?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        if (cuentaDAO.eliminar(id)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cuenta eliminada correctamente");

            limpiar();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar la cuenta");
        }
    }

    private void limpiar() {

        txtId.setText("");
        txtNombre.setText("");

        if (cmbTipoCuenta.getItemCount() > 0) {
            cmbTipoCuenta.setSelectedIndex(0);
        }

        txtNombre.requestFocus();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new FrmCuentaContable().setVisible(true);
        });
    }
}
