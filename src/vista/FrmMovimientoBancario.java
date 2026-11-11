package vista;

import controlador.GestorMovimientoBancario;
import controlador.IntegracionBancosContabilidad;
import dao.CuentaBancariaDAO;
import dao.CuentaContableDAO;
import dao.MovimientoBancarioDAO;
import dao.TipoTransaccionDAO;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.CuentaBancaria;
import modelo.CuentaContable;
import modelo.MovimientoBancario;
import modelo.MovimientoBancarioDetalle;
import modelo.TipoTransaccion;

/**
 * MODULO BANCOS - Pantalla compleja: Movimiento Bancario.
 * Cabecera: cuenta bancaria, fecha, descripcion.
 * Detalle:  fecha, tipo de transaccion y cantidad (valor).
 * Incluye la integracion "Enviar a Contabilidad".
 */
public class FrmMovimientoBancario extends JFrame {

    private JTextField txtNumero;
    private JTextField txtFecha;
    private JComboBox<CuentaBancaria> cboCuenta;
    private JTextField txtDescripcion;

    private JTextField txtFechaDet;
    private JComboBox<TipoTransaccion> cboTipo;
    private JTextField txtCantidad;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblIngresos;
    private JLabel lblEgresos;
    private JLabel lblNeto;

    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificarDet;
    private JButton btnEliminarDet;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnEnviar;

    // Lineas del detalle en memoria (sincronizadas con la tabla)
    private final List<MovimientoBancarioDetalle> detalles = new ArrayList<>();

    // Movimiento cargado: null = movimiento nuevo
    private Integer movimientoActual = null;

    private final MovimientoBancarioDAO dao = new MovimientoBancarioDAO();
    private final GestorMovimientoBancario gestor = new GestorMovimientoBancario();
    private final DecimalFormat formato = new DecimalFormat("#,##0.00");

    public FrmMovimientoBancario() {

        setTitle("Bancos - Movimiento Bancario");
        setSize(920, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
        cargarCombos();
        nuevo();
    }

    private void inicializar() {

        JPanel p = new JPanel(null);

        // ---------------- CABECERA ----------------
        JLabel lblCab = new JLabel("CABECERA");
        lblCab.setBounds(30, 5, 200, 20);

        JLabel lblNumero = new JLabel("N° Movimiento:");
        lblNumero.setBounds(30, 30, 110, 25);
        txtNumero = new JTextField();
        txtNumero.setBounds(140, 30, 90, 25);
        txtNumero.setEditable(false);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 30, 100, 25);

        JLabel lblFecha = new JLabel("Fecha (yyyy-MM-dd):");
        lblFecha.setBounds(370, 30, 140, 25);
        txtFecha = new JTextField();
        txtFecha.setBounds(510, 30, 130, 25);

        JLabel lblCuenta = new JLabel("Cuenta Bancaria:");
        lblCuenta.setBounds(30, 65, 110, 25);
        cboCuenta = new JComboBox<>();
        cboCuenta.setBounds(140, 65, 300, 25);

        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setBounds(460, 65, 90, 25);
        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(550, 65, 320, 25);

        // ---------------- DETALLE ----------------
        JLabel lblDet = new JLabel("DETALLE");
        lblDet.setBounds(30, 105, 200, 20);

        JLabel lblFechaDet = new JLabel("Fecha:");
        lblFechaDet.setBounds(30, 130, 50, 25);
        txtFechaDet = new JTextField();
        txtFechaDet.setBounds(80, 130, 100, 25);

        JLabel lblTipo = new JLabel("Tipo Transacción:");
        lblTipo.setBounds(195, 130, 115, 25);
        cboTipo = new JComboBox<>();
        cboTipo.setBounds(310, 130, 220, 25);

        JLabel lblCant = new JLabel("Cantidad ($):");
        lblCant.setBounds(545, 130, 90, 25);
        txtCantidad = new JTextField();
        txtCantidad.setBounds(630, 130, 90, 25);

        btnAgregar = new JButton("Agregar Detalle");
        btnAgregar.setBounds(735, 130, 145, 25);
        btnModificarDet = new JButton("Modificar Detalle");
        btnModificarDet.setBounds(735, 162, 145, 25);
        btnEliminarDet = new JButton("Eliminar Detalle");
        btnEliminarDet.setBounds(735, 194, 145, 25);

        modeloTabla = new DefaultTableModel(
                new Object[]{"#", "Fecha", "Tipo Transacción", "Signo", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(30, 230, 850, 220);

        lblIngresos = new JLabel();
        lblIngresos.setBounds(30, 460, 220, 25);
        lblEgresos = new JLabel();
        lblEgresos.setBounds(260, 460, 220, 25);
        lblNeto = new JLabel();
        lblNeto.setBounds(490, 460, 250, 25);

        // ---------------- ACCIONES ----------------
        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(30, 510, 100, 35);
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(140, 510, 120, 35);
        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(270, 510, 120, 35);
        btnEliminar = new JButton("Eliminar Movimiento");
        btnEliminar.setBounds(400, 510, 170, 35);
        btnEnviar = new JButton("Enviar a Contabilidad");
        btnEnviar.setBounds(680, 510, 200, 35);

        for (JComponent c : new JComponent[]{lblCab, lblNumero, txtNumero, btnBuscar,
            lblFecha, txtFecha, lblCuenta, cboCuenta, lblDesc, txtDescripcion,
            lblDet, lblFechaDet, txtFechaDet, lblTipo, cboTipo, lblCant, txtCantidad,
            btnAgregar, btnModificarDet, btnEliminarDet, scroll,
            lblIngresos, lblEgresos, lblNeto,
            btnNuevo, btnGuardar, btnModificar, btnEliminar, btnEnviar}) {
            p.add(c);
        }

        add(p);

        btnBuscar.addActionListener(e -> buscar());
        btnAgregar.addActionListener(e -> agregarDetalle());
        btnModificarDet.addActionListener(e -> modificarDetalle());
        btnEliminarDet.addActionListener(e -> eliminarDetalle());
        btnNuevo.addActionListener(e -> nuevo());
        btnGuardar.addActionListener(e -> guardar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnEnviar.addActionListener(e -> enviarAContabilidad());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });
    }

    private void cargarCombos() {
        cboCuenta.removeAllItems();
        for (CuentaBancaria c : new CuentaBancariaDAO().listar()) {
            cboCuenta.addItem(c);
        }
        cboTipo.removeAllItems();
        for (TipoTransaccion t : new TipoTransaccionDAO().listar()) {
            cboTipo.addItem(t);
        }
    }

    // ================= DETALLE EN MEMORIA =================

    private MovimientoBancarioDetalle leerLinea() {

        TipoTransaccion tipo = (TipoTransaccion) cboTipo.getSelectedItem();
        if (tipo == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay tipos de transacción. Créelos en la pantalla Tipo de Transacción.");
            return null;
        }

        Date fecha = leerFecha(txtFechaDet, "del detalle");
        if (fecha == null) {
            return null;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad numérica válida");
            txtCantidad.requestFocus();
            return null;
        }
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero");
            return null;
        }

        MovimientoBancarioDetalle d = new MovimientoBancarioDetalle();
        d.setFecha(fecha);
        d.setIdTipoTransaccion(tipo.getIdTipoTransaccion());
        d.setNombreTipo(tipo.getNombre());
        d.setSigno(tipo.getSigno());
        d.setValor(Math.round(cantidad * 100.0) / 100.0);
        return d;
    }

    private void agregarDetalle() {
        MovimientoBancarioDetalle d = leerLinea();
        if (d != null) {
            detalles.add(d);
            refrescarTabla();
            txtCantidad.setText("");
            txtCantidad.requestFocus();
        }
    }

    private void modificarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione la línea del detalle a modificar");
            return;
        }
        MovimientoBancarioDetalle d = leerLinea();
        if (d != null) {
            detalles.set(fila, d);
            refrescarTabla();
            tabla.setRowSelectionInterval(fila, fila);
        }
    }

    private void eliminarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione la línea del detalle a eliminar");
            return;
        }
        detalles.remove(fila);
        refrescarTabla();
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= detalles.size()) {
            return;
        }
        MovimientoBancarioDetalle d = detalles.get(fila);
        txtFechaDet.setText(d.getFecha().toString());
        txtCantidad.setText(String.valueOf(d.getValor()));
        for (int i = 0; i < cboTipo.getItemCount(); i++) {
            if (cboTipo.getItemAt(i).getIdTipoTransaccion() == d.getIdTipoTransaccion()) {
                cboTipo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        double ingresos = 0;
        double egresos = 0;
        int n = 1;
        for (MovimientoBancarioDetalle d : detalles) {
            modeloTabla.addRow(new Object[]{
                n++, d.getFecha(), d.getNombreTipo(), d.getSigno(), formato.format(d.getValor())
            });
            if ("+".equals(d.getSigno())) {
                ingresos += d.getValor();
            } else {
                egresos += d.getValor();
            }
        }
        lblIngresos.setText("Total ingresos (+): " + formato.format(ingresos));
        lblEgresos.setText("Total egresos (-): " + formato.format(egresos));
        lblNeto.setText("Neto del movimiento: " + formato.format(ingresos - egresos));
    }

    // ================= CABECERA / TRANSACCIONES =================

    private MovimientoBancario leerCabecera() {

        Date fecha = leerFecha(txtFecha, "del movimiento");
        if (fecha == null) {
            return null;
        }

        CuentaBancaria cuenta = (CuentaBancaria) cboCuenta.getSelectedItem();
        if (cuenta == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay cuentas bancarias. Créelas en la pantalla Cuenta Bancaria.");
            return null;
        }

        MovimientoBancario m = new MovimientoBancario();
        m.setFecha(fecha);
        m.setIdCuentaBanco(cuenta.getIdCuentaBanco());
        m.setDescripcion(txtDescripcion.getText().trim());
        return m;
    }

    private void guardar() {

        if (movimientoActual != null) {
            JOptionPane.showMessageDialog(this,
                    "Este movimiento ya existe. Use Modificar o presione Nuevo.");
            return;
        }

        MovimientoBancario m = leerCabecera();
        if (m == null) {
            return;
        }

        try {
            int id = gestor.guardar(m, detalles);
            movimientoActual = id;
            txtNumero.setText(String.valueOf(id));
            JOptionPane.showMessageDialog(this,
                    "Movimiento bancario guardado correctamente.\nN° " + id);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {

        if (movimientoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busque el movimiento a modificar");
            return;
        }

        MovimientoBancario m = leerCabecera();
        if (m == null) {
            return;
        }
        m.setIdMovimiento(movimientoActual);

        try {
            gestor.modificar(m, detalles);
            JOptionPane.showMessageDialog(this,
                    "Movimiento N° " + movimientoActual + " modificado correctamente");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {

        if (movimientoActual == null) {
            JOptionPane.showMessageDialog(this, "Primero busque el movimiento a eliminar");
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el movimiento N° " + movimientoActual + " y todo su detalle?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestor.eliminar(movimientoActual);
            JOptionPane.showMessageDialog(this, "Movimiento eliminado correctamente");
            nuevo();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {

        String texto = JOptionPane.showInputDialog(this,
                "Ingrese el N° de movimiento o el nombre de la cuenta bancaria:");
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }

        MovimientoBancario m;
        try {
            m = dao.buscar(Integer.parseInt(texto.trim()));
        } catch (NumberFormatException e) {
            m = dao.buscarPorTexto(texto);
        }

        if (m == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el movimiento bancario");
            return;
        }

        cargarCombos();

        movimientoActual = m.getIdMovimiento();
        txtNumero.setText(String.valueOf(m.getIdMovimiento()));
        txtFecha.setText(m.getFecha().toString());
        txtDescripcion.setText(m.getDescripcion() == null ? "" : m.getDescripcion());

        for (int i = 0; i < cboCuenta.getItemCount(); i++) {
            if (cboCuenta.getItemAt(i).getIdCuentaBanco() == m.getIdCuentaBanco()) {
                cboCuenta.setSelectedIndex(i);
                break;
            }
        }

        detalles.clear();
        detalles.addAll(dao.listarDetalles(m.getIdMovimiento()));
        refrescarTabla();
    }

    private void nuevo() {
        movimientoActual = null;
        String hoy = new Date(System.currentTimeMillis()).toString();
        txtNumero.setText("");
        txtFecha.setText(hoy);
        txtFechaDet.setText(hoy);
        txtDescripcion.setText("");
        txtCantidad.setText("");
        detalles.clear();
        refrescarTabla();
        cargarCombos();
    }

    // ================= INTEGRACION =================

    private void enviarAContabilidad() {

        if (movimientoActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero guarde o busque el movimiento que desea enviar");
            return;
        }

        List<CuentaContable> cuentas = new ArrayList<>();
        for (CuentaContable c : new CuentaContableDAO().listar()) {
            if (!"BANCOS".equalsIgnoreCase(c.getNombre())) {
                cuentas.add(c);
            }
        }
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen cuentas contables");
            return;
        }

        CuentaContable contrapartida = (CuentaContable) JOptionPane.showInputDialog(
                this,
                "Se generará un comprobante contable:\n"
                + "  • Ingresos (+):  DEBE BANCOS  /  HABER contrapartida\n"
                + "  • Egresos (-):    DEBE contrapartida  /  HABER BANCOS\n\n"
                + "Seleccione la cuenta contable de contrapartida:",
                "Enviar movimiento a Contabilidad",
                JOptionPane.QUESTION_MESSAGE,
                null,
                cuentas.toArray(),
                cuentas.get(0));

        if (contrapartida == null) {
            return;
        }

        try {
            int idComp = new IntegracionBancosContabilidad()
                    .enviarAContabilidad(movimientoActual, contrapartida.getIdCuenta());

            JOptionPane.showMessageDialog(this,
                    "Movimiento N° " + movimientoActual + " enviado a Contabilidad.\n"
                    + "Se generó el Comprobante contable N° " + idComp + ".\n\n"
                    + "Puede verlo en el menú Contabilidad > Comprobantes.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Integración",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ================= UTILIDAD =================

    private Date leerFecha(JTextField campo, String que) {
        try {
            return Date.valueOf(campo.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Fecha " + que + " inválida. Use yyyy-MM-dd (ej: 2026-11-11)");
            campo.requestFocus();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMovimientoBancario().setVisible(true));
    }
}
