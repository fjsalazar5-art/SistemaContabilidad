package vista;

import dao.CuentaBancariaDAO;
import javax.swing.*;
import modelo.CuentaBancaria;

/** MODULO BANCOS - Pantalla simple 2: Cuenta Bancaria. */
public class FrmCuentaBancaria extends JFrame {

    private JTextField txtId;
    private JTextField txtDescripcion;
    private JTextField txtNumero;
    private JTextField txtCliente;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;

    private final CuentaBancariaDAO dao = new CuentaBancariaDAO();

    public FrmCuentaBancaria() {

        setTitle("Bancos - Cuenta Bancaria");
        setSize(520, 310);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JPanel panel = new JPanel(null);

        JLabel lblId = new JLabel("ID Cuenta Bancaria:");
        lblId.setBounds(30, 25, 140, 25);
        txtId = new JTextField();
        txtId.setBounds(180, 25, 280, 25);
        txtId.setEditable(false);

        JLabel lblDesc = new JLabel("Nombre / Descripción:");
        lblDesc.setBounds(30, 60, 150, 25);
        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(180, 60, 280, 25);

        JLabel lblNumero = new JLabel("Número de cuenta:");
        lblNumero.setBounds(30, 95, 140, 25);
        txtNumero = new JTextField();
        txtNumero.setBounds(180, 95, 280, 25);

        JLabel lblCliente = new JLabel("Titular / Cliente:");
        lblCliente.setBounds(30, 130, 140, 25);
        txtCliente = new JTextField();
        txtCliente.setBounds(180, 130, 280, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, 190, 90, 30);
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(115, 190, 90, 30);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(210, 190, 90, 30);
        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(305, 190, 100, 30);
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(410, 190, 90, 30);

        panel.add(lblId);
        panel.add(txtId);
        panel.add(lblDesc);
        panel.add(txtDescripcion);
        panel.add(lblNumero);
        panel.add(txtNumero);
        panel.add(lblCliente);
        panel.add(txtCliente);
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnBuscar);
        panel.add(btnModificar);
        panel.add(btnEliminar);

        add(panel);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> buscar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
    }

    private CuentaBancaria leerFormulario() {

        String desc = txtDescripcion.getText().trim();
        String numero = txtNumero.getText().trim();

        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre / descripción de la cuenta");
            txtDescripcion.requestFocus();
            return null;
        }
        if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de cuenta");
            txtNumero.requestFocus();
            return null;
        }

        CuentaBancaria c = new CuentaBancaria();
        c.setDescripcion(desc);
        c.setNumeroCuenta(numero);
        c.setCliente(txtCliente.getText().trim());
        return c;
    }

    private void guardar() {

        if (!txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Este registro ya está guardado. Use Modificar o presione Nuevo.");
            return;
        }

        CuentaBancaria c = leerFormulario();
        if (c == null) {
            return;
        }

        if (dao.guardar(c)) {
            txtId.setText(String.valueOf(c.getIdCuentaBanco()));
            JOptionPane.showMessageDialog(this,
                    "Registro guardado correctamente.\nID generado: " + c.getIdCuentaBanco());
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el registro");
        }
    }

    private void buscar() {

        String texto = JOptionPane.showInputDialog(this,
                "Ingrese el ID, nombre o número de la cuenta bancaria:");

        if (texto == null || texto.trim().isEmpty()) {
            return;
        }

        CuentaBancaria c;
        try {
            c = dao.buscar(Integer.parseInt(texto.trim()));
        } catch (NumberFormatException e) {
            c = null;
        }
        if (c == null) {
            c = dao.buscarPorTexto(texto);
        }

        if (c != null) {
            txtId.setText(String.valueOf(c.getIdCuentaBanco()));
            txtDescripcion.setText(c.getDescripcion());
            txtNumero.setText(c.getNumeroCuenta());
            txtCliente.setText(c.getCliente());
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró la cuenta bancaria");
            limpiar();
        }
    }

    private void modificar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar un registro");
            return;
        }

        CuentaBancaria c = leerFormulario();
        if (c == null) {
            return;
        }
        c.setIdCuentaBanco(Integer.parseInt(txtId.getText()));

        if (dao.modificar(c)) {
            JOptionPane.showMessageDialog(this, "Registro modificado correctamente");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo modificar el registro");
        }
    }

    private void eliminar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar un registro");
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar \"" + txtDescripcion.getText() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }

        if (dao.eliminar(Integer.parseInt(txtId.getText()))) {
            JOptionPane.showMessageDialog(this, "Registro eliminado correctamente");
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el registro.\n"
                    + "Verifique que la cuenta no tenga movimientos bancarios.");
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtNumero.setText("");
        txtCliente.setText("");
        txtDescripcion.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmCuentaBancaria().setVisible(true));
    }
}
