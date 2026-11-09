package vista;

import dao.BalanceGeneralDAO;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
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
import modelo.SaldoCuenta;

public class FrmBalanceGeneral extends JFrame {

    private JTextField txtFechaCorte;

    private JButton btnGenerar;
    private JButton btnImprimir;
    private JButton btnLimpiar;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblActivo;
    private JLabel lblPasivo;
    private JLabel lblPatrimonio;
    private JLabel lblResultado;
    private JLabel lblComprobacion;

    private double totalActivo;
    private double totalPasivo;
    private double totalPatrimonio;
    private double resultadoEjercicio;
    private double diferencia;

    private boolean balanceGenerado = false;

    private final DecimalFormat formato =
            new DecimalFormat("#,##0.00");

    public FrmBalanceGeneral() {

        setTitle("Balance General");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializar();
    }

    private void inicializar() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JLabel lblFecha = new JLabel(
                "Fecha de corte (yyyy-MM-dd):");

        txtFechaCorte = new JTextField(12);

        btnGenerar = new JButton("Generar Balance");
        btnImprimir = new JButton("Generar PDF");
        btnLimpiar = new JButton("Limpiar");

        panelSuperior.add(lblFecha);
        panelSuperior.add(txtFechaCorte);
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

        lblActivo = new JLabel("TOTAL ACTIVO: 0.00");
        lblPasivo = new JLabel("TOTAL PASIVO: 0.00");
        lblPatrimonio = new JLabel("TOTAL PATRIMONIO: 0.00");
        lblResultado = new JLabel(
                "RESULTADO DEL EJERCICIO: 0.00");
        lblComprobacion = new JLabel(
                "COMPROBACIÓN CONTABLE: Pendiente");

        panelTotales.add(lblActivo);
        panelTotales.add(lblPasivo);
        panelTotales.add(lblPatrimonio);
        panelTotales.add(lblResultado);
        panelTotales.add(lblComprobacion);

        principal.add(panelSuperior, BorderLayout.NORTH);
        principal.add(scroll, BorderLayout.CENTER);
        principal.add(panelTotales, BorderLayout.SOUTH);

        add(principal);

        btnGenerar.addActionListener(e -> generarBalance());
        btnImprimir.addActionListener(e -> generarPDF());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void generarBalance() {

        try {

            String fechaTexto = txtFechaCorte.getText().trim();

            if (fechaTexto.isEmpty()) {
                throw new Exception("Ingrese la fecha de corte.");
            }

            Date fechaCorte = Date.valueOf(fechaTexto);

            BalanceGeneralDAO dao = new BalanceGeneralDAO();

            List<SaldoCuenta> lista =
                    dao.obtenerBalance(fechaCorte);

            resultadoEjercicio =
                    dao.obtenerResultadoEjercicio(fechaCorte);

            modeloTabla.setRowCount(0);

            totalActivo = 0;
            totalPasivo = 0;
            totalPatrimonio = 0;

            for (SaldoCuenta cuenta : lista) {

                modeloTabla.addRow(new Object[]{
                    cuenta.getIdCuenta(),
                    cuenta.getNombreCuenta(),
                    cuenta.getTipoCuenta(),
                    formato.format(cuenta.getTotalDebe()),
                    formato.format(cuenta.getTotalHaber()),
                    formato.format(cuenta.getSaldo())
                });

                switch (cuenta.getTipoCuenta()) {

                    case "ACTIVO":
                        totalActivo += cuenta.getSaldo();
                        break;

                    case "PASIVO":
                        totalPasivo += cuenta.getSaldo();
                        break;

                    case "PATRIMONIO":
                        totalPatrimonio += cuenta.getSaldo();
                        break;
                }
            }

            diferencia = totalActivo
                    - totalPasivo
                    - totalPatrimonio
                    - resultadoEjercicio;

            lblActivo.setText(
                    "TOTAL ACTIVO: " + formato.format(totalActivo));

            lblPasivo.setText(
                    "TOTAL PASIVO: " + formato.format(totalPasivo));

            lblPatrimonio.setText(
                    "TOTAL PATRIMONIO: "
                    + formato.format(totalPatrimonio));

            lblResultado.setText(
                    "RESULTADO DEL EJERCICIO: "
                    + formato.format(resultadoEjercicio));

            lblComprobacion.setText(
                    "DIFERENCIA CONTABLE: "
                    + formato.format(diferencia)
                    + (Math.abs(diferencia) < 0.01
                            ? " - BALANCE CUADRADO"
                            : " - REVISAR DIFERENCIA"));

            balanceGenerado = true;

            if (lista.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron cuentas para el Balance General.");
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese una fecha válida con formato yyyy-MM-dd.",
                    "Fecha incorrecta",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al generar Balance General",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarPDF() {

        if (!balanceGenerado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe generar el Balance General.");

            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar Balance General en PDF");

        selector.setSelectedFile(
                new File("Balance_General.pdf"));

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
                    new Paragraph("BALANCE GENERAL", titulo);

            tituloPDF.setAlignment(Element.ALIGN_CENTER);

            documento.add(tituloPDF);

            documento.add(new Paragraph(" "));

            Paragraph fecha = new Paragraph(
                    "Fecha de corte: "
                    + txtFechaCorte.getText().trim(),
                    normal);

            documento.add(fecha);

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
                    "TOTAL ACTIVO: " + formato.format(totalActivo),
                    normal));

            documento.add(new Paragraph(
                    "TOTAL PASIVO: " + formato.format(totalPasivo),
                    normal));

            documento.add(new Paragraph(
                    "TOTAL PATRIMONIO: "
                    + formato.format(totalPatrimonio),
                    normal));

            documento.add(new Paragraph(
                    "RESULTADO DEL EJERCICIO: "
                    + formato.format(resultadoEjercicio),
                    normal));

            documento.add(new Paragraph(
                    "DIFERENCIA CONTABLE: "
                    + formato.format(diferencia),
                    normal));

            String estado = Math.abs(diferencia) < 0.01
                    ? "BALANCE CUADRADO"
                    : "REVISAR DIFERENCIA";

            documento.add(new Paragraph(
                    "ESTADO DEL BALANCE: " + estado,
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

        txtFechaCorte.setText("");
        modeloTabla.setRowCount(0);

        totalActivo = 0;
        totalPasivo = 0;
        totalPatrimonio = 0;
        resultadoEjercicio = 0;
        diferencia = 0;

        balanceGenerado = false;

        lblActivo.setText("TOTAL ACTIVO: 0.00");
        lblPasivo.setText("TOTAL PASIVO: 0.00");
        lblPatrimonio.setText("TOTAL PATRIMONIO: 0.00");
        lblResultado.setText("RESULTADO DEL EJERCICIO: 0.00");

        lblComprobacion.setText(
                "COMPROBACIÓN CONTABLE: Pendiente");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new FrmBalanceGeneral().setVisible(true));
    }
}