package vista;

import dao.TipoTransaccionDAO;
import javax.swing.*;
import modelo.TipoTransaccion;

/** MODULO BANCOS - Pantalla simple 1: Tipo de Transaccion. */
public class FrmTipoTransaccion extends JFrame {

    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cboSigno;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;

    private final TipoTransaccionDAO dao = new TipoTransaccionDAO();

    private static final String INGRESO = "+  (Ingreso: suma al saldo)";
    private static final String EGRESO = "-  (Egreso: resta del saldo)";

    public FrmTipoTransaccion() {

        setTitle("Bancos - Tipo de Transacción");
        setSize(520, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JPanel panel = new JPanel(null);

        JLabel lblId = new JLabel("ID Tipo Transacción:");
        lblId.setBounds(30, 30, 140, 25);
        txtId = new JTextField();
        txtId.setBounds(180, 30, 260, 25);
        txtId.setEditable(false);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 70, 140, 25);
        txtNombre = new JTextField();
        txtNombre.setBounds(180, 70, 260, 25);

        JLabel lblSigno = new JLabel("Signo:");
        lblSigno.setBounds(30, 110, 140, 25);
        cboSigno = new JComboBox<>(new String[]{INGRESO, EGRESO});
        cboSigno.setBounds(180, 110, 260, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, 165, 90, 30);
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(115, 165, 90, 30);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(210, 165, 90, 30);
        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(305, 165, 100, 30);
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(410, 165, 90, 30);

        panel.add(lblId);
        panel.add(txtId);
        panel.add(lblNombre);
        panel.add(txtNombre);
        panel.add(lblSigno);
        panel.add(cboSigno);
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

    private String signoSeleccionado() {
        return cboSigno.getSelectedIndex() == 0 ? "+" : "-";
    }

    private void guardar() {

        if (!txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Este registro ya está guardado. Use Modificar o presione Nuevo.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del tipo de transacción");
            txtNombre.requestFocus();
            return;
        }

        TipoTransaccion t = new TipoTransaccion();
        t.setNombre(nombre);
        t.setSigno(signoSeleccionado());

        if (dao.guardar(t)) {
            txtId.setText(String.valueOf(t.getIdTipoTransaccion()));
            JOptionPane.showMessageDialog(this,
                    "Registro guardado correctamente.\nID generado: " + t.getIdTipoTransaccion());
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el registro");
        }
    }

    private void buscar() {

        String texto = JOptionPane.showInputDialog(this,
                "Ingrese el ID o el nombre del tipo de transacción:");

        if (texto == null || texto.trim().isEmpty()) {
            return;
        }

        TipoTransaccion t;
        try {
            t = dao.buscar(Integer.parseInt(texto.trim()));
        } catch (NumberFormatException e) {
            t = dao.buscarPorNombre(texto);
        }

        if (t != null) {
            txtId.setText(String.valueOf(t.getIdTipoTransaccion()));
            txtNombre.setText(t.getNombre());
            cboSigno.setSelectedIndex("+".equals(t.getSigno()) ? 0 : 1);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el tipo de transacción");
            limpiar();
        }
    }

    private void modificar() {

        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar un registro");
            return;
        }

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del tipo de transacción");
            return;
        }

        TipoTransaccion t = new TipoTransaccion(
                Integer.parseInt(txtId.getText()), nombre, signoSeleccionado());

        if (dao.modificar(t)) {
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
                "¿Está seguro de eliminar \"" + txtNombre.getText() + "\"?",
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
                    + "Verifique que no esté usado en algún movimiento bancario.");
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        cboSigno.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmTipoTransaccion().setVisible(true));
    }
}
