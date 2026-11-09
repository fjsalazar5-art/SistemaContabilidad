package vista;

import dao.EstadoResultadosDAO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import modelo.ResultadoCuenta;

public class FrmEstadoResultados extends JFrame {

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;

    private JButton btnGenerar;
    private JButton btnImprimir;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblIngresos;
    private JLabel lblGastos;
    private JLabel lblResultado;

    private double totalIngresos;
    private double totalGastos;
    private double resultado;

    private boolean estadoGenerado = false;

    private final DecimalFormat formato =
            new DecimalFormat("#,##0.00");

    public FrmEstadoResultados() {

        setTitle("Estado de Resultados");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JPanel principal = new JPanel(
                new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JLabel lblInicio = new JLabel(
                "Fecha inicial (yyyy-MM-dd):");

        txtFechaInicio = new JTextField(10);

        JLabel lblFin = new JLabel(
                "Fecha final (yyyy-MM-dd):");

        txtFechaFin = new JTextField(10);

        btnGenerar = new JButton("Generar Estado");
        btnImprimir = new JButton("Generar PDF");
        btnLimpiar = new JButton("Limpiar");

        panelSuperior.add(lblInicio);
        panelSuperior.add(txtFechaInicio);
        panelSuperior.add(lblFin);
        panelSuperior.add(txtFechaFin);
        panelSuperior.add(btnGenerar);
        panelSuperior.add(btnImprimir);
        panelSuperior.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Cuenta",
                    "Tipo",
                    "Total Debe",
                    "Total Haber",
                    "Saldo"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panelTotales = new JPanel(
                new GridLayout(0, 1, 5, 5));

        lblIngresos = new JLabel(
                "TOTAL INGRESOS: 0.00");

        lblGastos = new JLabel(
                "TOTAL GASTOS: 0.00");

        lblResultado = new JLabel(
                "UTILIDAD / PÉRDIDA: 0.00");

        panelTotales.add(lblIngresos);
        panelTotales.add(lblGastos);
        panelTotales.add(lblResultado);

        principal.add(panelSuperior, BorderLayout.NORTH);
        principal.add(scroll, BorderLayout.CENTER);
        principal.add(panelTotales, BorderLayout.SOUTH);

        add(principal);

        btnGenerar.addActionListener(
                e -> generarEstadoResultados());

        btnImprimir.addActionListener(
                e -> generarPDF());

        btnLimpiar.addActionListener(
                e -> limpiar());
    }

    private void generarEstadoResultados() {

        try {

            String inicioTexto =
                    txtFechaInicio.getText().trim();

            String finTexto =
                    txtFechaFin.getText().trim();

            if (inicioTexto.isEmpty() || finTexto.isEmpty()) {

                throw new Exception(
                        "Ingrese la fecha inicial y la fecha final.");
            }

            Date fechaInicio = Date.valueOf(inicioTexto);
            Date fechaFin = Date.valueOf(finTexto);

            if (fechaInicio.after(fechaFin)) {

                throw new Exception(
                        "La fecha inicial no puede ser posterior "
                        + "a la fecha final.");
            }

            EstadoResultadosDAO dao =
                    new EstadoResultadosDAO();

            List<ResultadoCuenta> lista =
                    dao.obtenerEstadoResultados(
                            fechaInicio,
                            fechaFin);

            modeloTabla.setRowCount(0);

            totalIngresos = 0;
            totalGastos = 0;

            for (ResultadoCuenta cuenta : lista) {

                modeloTabla.addRow(new Object[]{
                    cuenta.getIdCuenta(),
                    cuenta.getNombreCuenta(),
                    cuenta.getTipoCuenta(),
                    formato.format(cuenta.getTotalDebe()),
                    formato.format(cuenta.getTotalHaber()),
                    formato.format(cuenta.getSaldo())
                });

                if ("INGRESO".equals(cuenta.getTipoCuenta())) {

                    totalIngresos += cuenta.getSaldo();

                } else if ("GASTO".equals(cuenta.getTipoCuenta())) {

                    totalGastos += cuenta.getSaldo();
                }
            }

            resultado = totalIngresos - totalGastos;

            lblIngresos.setText(
                    "TOTAL INGRESOS: "
                    + formato.format(totalIngresos));

            lblGastos.setText(
                    "TOTAL GASTOS: "
                    + formato.format(totalGastos));

            lblResultado.setText(
                    (resultado >= 0
                            ? "UTILIDAD DEL PERÍODO: "
                            : "PÉRDIDA DEL PERÍODO: ")
                    + formato.format(Math.abs(resultado)));

            estadoGenerado = true;

            if (lista.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron cuentas de ingresos "
                        + "o gastos registradas.");
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese fechas válidas con formato yyyy-MM-dd.",
                    "Fechas incorrectas",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al generar Estado de Resultados",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarPDF() {

        if (!estadoGenerado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe generar el Estado de Resultados.");

            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle(
                "Guardar Estado de Resultados en PDF");

        selector.setSelectedFile(
                new File("Estado_de_Resultados.pdf"));

        selector.setFileFilter(
                new FileNameExtensionFilter("Archivo PDF", "pdf"));

        int opcion = selector.showSaveDialog(this);

        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }

        if (archivo.exists()) {

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe. ¿Desea reemplazarlo?",
                    "Confirmar reemplazo",
                    JOptionPane.YES_NO_OPTION);

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        Document documento = new Document(PageSize.A4.rotate());

        try {

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream(archivo));

            documento.open();

            com.lowagie.text.Font titulo =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            18,
                            com.lowagie.text.Font.BOLD);

            com.lowagie.text.Font normal =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            10);

            com.lowagie.text.Font encabezado =
                    new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            9,
                            com.lowagie.text.Font.BOLD);

            Paragraph tituloPDF =
                    new Paragraph("ESTADO DE RESULTADOS", titulo);

            tituloPDF.setAlignment(Element.ALIGN_CENTER);

            documento.add(tituloPDF);

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Fecha inicial: "
                    + txtFechaInicio.getText().trim(),
                    normal));

            documento.add(new Paragraph(
                    "Fecha final: "
                    + txtFechaFin.getText().trim(),
                    normal));

            documento.add(new Paragraph(" "));

            PdfPTable tablaPDF = new PdfPTable(6);

            tablaPDF.setWidthPercentage(100);

            tablaPDF.setWidths(
                    new float[]{0.7f, 2.5f, 1.3f, 1.5f, 1.5f, 1.5f});

            String[] columnas = {
                "ID",
                "Cuenta",
                "Tipo",
                "Total Debe",
                "Total Haber",
                "Saldo"
            };

            for (String columna : columnas) {

                PdfPCell celda = new PdfPCell(
                        new Phrase(columna, encabezado));

                celda.setHorizontalAlignment(
                        Element.ALIGN_CENTER);

                celda.setPadding(6);

                celda.setBackgroundColor(
                        new java.awt.Color(220, 220, 220));

                tablaPDF.addCell(celda);
            }

            for (int i = 0; i < tabla.getRowCount(); i++) {

                int fila = tabla.convertRowIndexToModel(i);

                for (int j = 0; j < 6; j++) {

                    Object valor =
                            modeloTabla.getValueAt(fila, j);

                    PdfPCell celda = new PdfPCell(
                            new Phrase(
                                    valor == null ? "" : valor.toString(),
                                    normal));

                    celda.setPadding(5);

                    if (j == 0 || j >= 3) {
                        celda.setHorizontalAlignment(
                                Element.ALIGN_RIGHT);
                    }

                    tablaPDF.addCell(celda);
                }
            }

            documento.add(tablaPDF);

            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "TOTAL INGRESOS: "
                    + formato.format(totalIngresos),
                    normal));

            documento.add(new Paragraph(
                    "TOTAL GASTOS: "
                    + formato.format(totalGastos),
                    normal));

            documento.add(new Paragraph(
                    (resultado >= 0
                            ? "UTILIDAD DEL PERÍODO: "
                            : "PÉRDIDA DEL PERÍODO: ")
                    + formato.format(Math.abs(resultado)),
                    encabezado));

            documento.close();

            if (Desktop.isDesktopSupported()) {

                Desktop.getDesktop().open(archivo);

                JOptionPane.showMessageDialog(
                        this,
                        "PDF generado correctamente.");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "PDF guardado en:\n"
                        + archivo.getAbsolutePath());
            }

        } catch (Exception e) {

            if (documento.isOpen()) {
                documento.close();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {

        txtFechaInicio.setText("");
        txtFechaFin.setText("");

        modeloTabla.setRowCount(0);

        totalIngresos = 0;
        totalGastos = 0;
        resultado = 0;

        estadoGenerado = false;

        lblIngresos.setText("TOTAL INGRESOS: 0.00");
        lblGastos.setText("TOTAL GASTOS: 0.00");
        lblResultado.setText("UTILIDAD / PÉRDIDA: 0.00");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new FrmEstadoResultados().setVisible(true));
    }
}