
package vista;

import controlador.GestorComprobante;
import controlador.LineaComprobante;
import dao.ComprobanteDAO;
import dao.ComprobanteDetalleDAO;
import dao.CuentaContableDAO;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Comprobante;
import modelo.ComprobanteDetalle;
import modelo.CuentaContable;

public class FrmComprobante extends JFrame {

    private JTextField txtNumero;
    private JTextField txtFecha;
    private JTextField txtObservacion;

    private JComboBox<CuentaContable> cboCuenta;

    private JTextField txtDebe;
    private JTextField txtHaber;

    private JButton btnNuevo;
    private JButton btnBuscar;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;

    private JButton btnAgregar;
    private JButton btnModificarDetalle;
    private JButton btnEliminarDetalle;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalDebe;
    private JLabel lblTotalHaber;

    // Numero del comprobante cargado con Buscar.
    // null = comprobante nuevo (aun no guardado).
    private Integer comprobanteActual = null;

    public FrmComprobante() {

        setTitle("Comprobante Contable");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
        cargarCuentas();
        actualizarEstadoBotones();
    }

    private void inicializar() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // ---------------- CABECERA ----------------

        JLabel lblNumero = new JLabel("Número:");
        lblNumero.setBounds(30, 20, 100, 25);

        txtNumero = new JTextField();
        txtNumero.setBounds(120, 20, 100, 25);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 20, 100, 25);

        JLabel lblFecha = new JLabel("Fecha (yyyy-MM-dd):");
        lblFecha.setBounds(370, 20, 150, 25);

        txtFecha = new JTextField();
        txtFecha.setBounds(520, 20, 150, 25);

        JLabel lblObs = new JLabel("Observación:");
        lblObs.setBounds(30, 60, 100, 25);

        txtObservacion = new JTextField();
        txtObservacion.setBounds(120, 60, 550, 25);

        // ---------------- DETALLE ----------------

        JLabel lblCuenta = new JLabel("Cuenta:");
        lblCuenta.setBounds(30, 120, 100, 25);

        cboCuenta = new JComboBox<>();
        cboCuenta.setBounds(120, 120, 220, 25);

        JLabel lblDebe = new JLabel("Debe:");
        lblDebe.setBounds(360, 120, 60, 25);

        txtDebe = new JTextField();
        txtDebe.setBounds(420, 120, 100, 25);

        JLabel lblHaber = new JLabel("Haber:");
        lblHaber.setBounds(540, 120, 60, 25);

        txtHaber = new JTextField();
        txtHaber.setBounds(600, 120, 100, 25);

        btnAgregar = new JButton("Agregar Detalle");
        btnAgregar.setBounds(720, 120, 140, 25);

        btnModificarDetalle = new JButton("Modificar Detalle");
        btnModificarDetalle.setBounds(720, 155, 140, 25);

        btnEliminarDetalle = new JButton("Eliminar Detalle");
        btnEliminarDetalle.setBounds(720, 190, 140, 25);

        modeloTabla = new DefaultTableModel(
                new Object[]{"Cuenta", "Debe", "Haber"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 225, 830, 200);

        // ---------------- TOTALES Y ACCIONES ----------------

        lblTotalDebe = new JLabel("Total Debe: 0.00");
        lblTotalDebe.setBounds(30, 435, 200, 25);

        lblTotalHaber = new JLabel("Total Haber: 0.00");
        lblTotalHaber.setBounds(250, 435, 200, 25);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(30, 485, 110, 32);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(150, 485, 140, 32);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(300, 485, 140, 32);

        btnEliminar = new JButton("Eliminar Comprobante");
        btnEliminar.setBounds(450, 485, 190, 32);

        panel.add(lblNumero);
        panel.add(txtNumero);
        panel.add(btnBuscar);
        panel.add(lblFecha);
        panel.add(txtFecha);
        panel.add(lblObs);
        panel.add(txtObservacion);
        panel.add(lblCuenta);
        panel.add(cboCuenta);
        panel.add(lblDebe);
        panel.add(txtDebe);
        panel.add(lblHaber);
        panel.add(txtHaber);
        panel.add(btnAgregar);
        panel.add(btnModificarDetalle);
        panel.add(btnEliminarDetalle);
        panel.add(scroll);
        panel.add(lblTotalDebe);
        panel.add(lblTotalHaber);
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnModificar);
        panel.add(btnEliminar);

        add(panel, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> nuevo());
        btnBuscar.addActionListener(e -> buscarComprobante());
        btnGuardar.addActionListener(e -> guardarComprobante());
        btnModificar.addActionListener(e -> modificarComprobante());
        btnEliminar.addActionListener(e -> eliminarComprobante());

        btnAgregar.addActionListener(e -> agregarDetalle());
        btnModificarDetalle.addActionListener(e -> modificarDetalle());
        btnEliminarDetalle.addActionListener(e -> eliminarDetalle());

        // Al elegir una fila, sus valores pasan a los campos de edicion
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });
    }

    private void cargarCuentas() {

        cboCuenta.removeAllItems();

        CuentaContableDAO dao = new CuentaContableDAO();

        List<CuentaContable> lista = dao.listar();

        for (CuentaContable cuenta : lista) {
            cboCuenta.addItem(cuenta);
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay cuentas contables registradas");
        }
    }

    // ---------------------------------------------------------
    // ESTADO DE LA PANTALLA
    // Guardar solo para comprobantes nuevos.
    // Modificar y Eliminar solo para comprobantes ya buscados.
    // ---------------------------------------------------------
    private void actualizarEstadoBotones() {

        boolean existente = comprobanteActual != null;

        btnGuardar.setEnabled(!existente);
        btnModificar.setEnabled(existente);
        btnEliminar.setEnabled(existente);
    }

    private void nuevo() {

        limpiarFormulario();
    }

    // ---------------------------------------------------------
    // DETALLE (en pantalla, antes de guardar)
    // ---------------------------------------------------------

    // Lee y valida cuenta, debe y haber de los campos de edicion
    private Object[] leerLinea() throws Exception {

        CuentaContable cuenta =
                (CuentaContable) cboCuenta.getSelectedItem();

        if (cuenta == null) {
            throw new Exception("Seleccione una cuenta contable");
        }

        String textoDebe = txtDebe.getText().trim();
        String textoHaber = txtHaber.getText().trim();

        if (textoDebe.isEmpty()) {
            textoDebe = "0";
        }

        if (textoHaber.isEmpty()) {
            textoHaber = "0";
        }

        double debe;
        double haber;

        try {
            debe = Double.parseDouble(textoDebe.replace(",", "."));
            haber = Double.parseDouble(textoHaber.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Ingrese valores numéricos válidos");
        }

        if (!Double.isFinite(debe) || !Double.isFinite(haber)) {
            throw new Exception("Ingrese valores numéricos válidos");
        }

        if (debe < 0 || haber < 0) {
            throw new Exception("Los valores no pueden ser negativos");
        }

        if (debe > 0 && haber > 0) {
            throw new Exception(
                    "Ingrese el valor solamente en Debe o Haber");
        }

        if (debe == 0 && haber == 0) {
            throw new Exception("Ingrese un valor en Debe o Haber");
        }

        return new Object[]{cuenta, debe, haber};
    }

    private void agregarDetalle() {

        try {

            modeloTabla.addRow(leerLinea());

            calcularTotales();

            limpiarCamposDetalle();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void modificarDetalle() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione la fila que desea modificar");

            return;
        }

        try {

            Object[] linea = leerLinea();

            modeloTabla.setValueAt(linea[0], fila, 0);
            modeloTabla.setValueAt(linea[1], fila, 1);
            modeloTabla.setValueAt(linea[2], fila, 2);

            calcularTotales();

            tabla.clearSelection();
            limpiarCamposDetalle();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void eliminarDetalle() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una fila para eliminar");

            return;
        }

        modeloTabla.removeRow(fila);

        limpiarCamposDetalle();

        calcularTotales();
    }

    private void cargarFilaSeleccionada() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            return;
        }

        CuentaContable cuenta =
                (CuentaContable) modeloTabla.getValueAt(fila, 0);

        double debe = Double.parseDouble(
                modeloTabla.getValueAt(fila, 1).toString());

        double haber = Double.parseDouble(
                modeloTabla.getValueAt(fila, 2).toString());

        // Se selecciona en el combo la cuenta con el mismo ID
        for (int i = 0; i < cboCuenta.getItemCount(); i++) {

            if (cboCuenta.getItemAt(i).getIdCuenta()
                    == cuenta.getIdCuenta()) {

                cboCuenta.setSelectedIndex(i);
                break;
            }
        }

        txtDebe.setText(debe > 0 ? String.valueOf(debe) : "");
        txtHaber.setText(haber > 0 ? String.valueOf(haber) : "");
    }

    private void calcularTotales() {

        double totalDebe = 0;
        double totalHaber = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {

            totalDebe += Double.parseDouble(
                    modeloTabla.getValueAt(i, 1).toString());

            totalHaber += Double.parseDouble(
                    modeloTabla.getValueAt(i, 2).toString());
        }

        lblTotalDebe.setText(
                "Total Debe: " + String.format("%.2f", totalDebe));

        lblTotalHaber.setText(
                "Total Haber: " + String.format("%.2f", totalHaber));
    }

    // Convierte las filas de la tabla en lineas para el gestor
    private List<LineaComprobante> leerLineasDeLaTabla() {

        List<LineaComprobante> lineas = new ArrayList<>();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {

            CuentaContable cuenta =
                    (CuentaContable) modeloTabla.getValueAt(i, 0);

            double debe = Double.parseDouble(
                    modeloTabla.getValueAt(i, 1).toString());

            double haber = Double.parseDouble(
                    modeloTabla.getValueAt(i, 2).toString());

            lineas.add(
                    new LineaComprobante(
                            cuenta.getIdCuenta(),
                            cuenta.getNombre(),
                            debe,
                            haber));
        }

        return lineas;
    }

    // ---------------------------------------------------------
    // GUARDAR (comprobante nuevo)
    // ---------------------------------------------------------
    private void guardarComprobante() {

        try {

            GestorComprobante gestor = new GestorComprobante();

            boolean guardado = gestor.guardarComprobante(
                    txtFecha.getText().trim(),
                    txtObservacion.getText().trim(),
                    leerLineasDeLaTabla());

            if (guardado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante guardado correctamente");

                limpiarFormulario();
            }

        } catch (Exception e) {

            mostrarError("Error al guardar comprobante", e);
        }
    }

    // ---------------------------------------------------------
    // MODIFICAR (comprobante existente)
    // ---------------------------------------------------------
    private void modificarComprobante() {

        if (comprobanteActual == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero busque el comprobante que desea modificar");

            return;
        }

        try {

            GestorComprobante gestor = new GestorComprobante();

            boolean modificado = gestor.modificarComprobante(
                    comprobanteActual,
                    txtFecha.getText().trim(),
                    txtObservacion.getText().trim(),
                    leerLineasDeLaTabla());

            if (modificado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante " + comprobanteActual
                        + " modificado correctamente");

                // Se vuelve a leer desde la base para mostrar lo guardado
                cargarComprobante(comprobanteActual);
            }

        } catch (Exception e) {

            mostrarError("Error al modificar comprobante", e);
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR (cabecera y detalles)
    // ---------------------------------------------------------
    private void eliminarComprobante() {

        if (comprobanteActual == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero busque el comprobante que desea eliminar");

            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el comprobante " + comprobanteActual
                + " con todos sus detalles?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            GestorComprobante gestor = new GestorComprobante();

            if (gestor.eliminarComprobante(comprobanteActual)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante eliminado correctamente");

                limpiarFormulario();
            }

        } catch (Exception e) {

            mostrarError("Error al eliminar comprobante", e);
        }
    }

    // ---------------------------------------------------------
    // BUSCAR
    // ---------------------------------------------------------
    private void buscarComprobante() {

        try {

            if (txtNumero.getText().trim().isEmpty()) {
                throw new Exception(
                        "Ingrese el número del comprobante");
            }

            int id = Integer.parseInt(txtNumero.getText().trim());

            if (!cargarComprobante(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Comprobante no encontrado");
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El número debe ser un valor entero válido");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Carga cabecera y detalles. Devuelve false si no existe.
    private boolean cargarComprobante(int id) {

        Comprobante comp = new ComprobanteDAO().buscar(id);

        if (comp == null) {
            return false;
        }

        txtNumero.setText(String.valueOf(comp.getIdComprobante()));
        txtFecha.setText(comp.getFecha().toString());
        txtObservacion.setText(comp.getObservacion());

        modeloTabla.setRowCount(0);

        List<ComprobanteDetalle> detalles =
                new ComprobanteDetalleDAO().listarPorComprobante(id);

        CuentaContableDAO cuentaDAO = new CuentaContableDAO();

        for (ComprobanteDetalle d : detalles) {

            CuentaContable cuenta = cuentaDAO.buscar(d.getIdCuenta());

            if (cuenta != null) {

                modeloTabla.addRow(
                        new Object[]{
                            cuenta,
                            d.getDebe(),
                            d.getHaber()
                        });
            }
        }

        limpiarCamposDetalle();

        calcularTotales();

        comprobanteActual = id;
        actualizarEstadoBotones();

        return true;
    }

    // ---------------------------------------------------------
    // UTILIDADES
    // ---------------------------------------------------------
    private void mostrarError(String titulo, Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                titulo,
                JOptionPane.ERROR_MESSAGE);
    }

    private void limpiarCamposDetalle() {

        txtDebe.setText("");
        txtHaber.setText("");
    }

    private void limpiarFormulario() {

        txtNumero.setText("");
        txtFecha.setText("");
        txtObservacion.setText("");

        limpiarCamposDetalle();

        modeloTabla.setRowCount(0);

        calcularTotales();

        if (cboCuenta.getItemCount() > 0) {
            cboCuenta.setSelectedIndex(0);
        }

        comprobanteActual = null;
        actualizarEstadoBotones();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new FrmComprobante().setVisible(true));
    }
}
