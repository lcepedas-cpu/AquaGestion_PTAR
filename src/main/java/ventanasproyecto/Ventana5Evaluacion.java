/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventanasproyecto;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class Ventana5Evaluacion extends javax.swing.JFrame {

    private double sstEntrada = 220.0;
    private double sstSalida = 6.6;
    private double dboEntrada = 200.0;
    private double dboSalida = 20.0;
    private double dqoEntrada = 400.0;
    private double dqoSalida = 60.0;
    private double consumoTotal = 40.0;
    private double lodosTotales = 135.0;

    private procesos.EvaluacionEfluente evaluacion;
    private String resumenDiagnosticoTexto = "";
    private Clip sound;

    public Ventana5Evaluacion() {
        this(220.0, 6.6, 200.0, 20.0, 400.0, 60.0, 40.0, 135.0);
    }

    public Ventana5Evaluacion(double sstEntrada, double sstSalida, double dboEntrada,
                              double dboSalida, double dqoEntrada, double dqoSalida,
                              double consumoTotal, double lodosTotales) {
        this.sstEntrada = sstEntrada;
        this.sstSalida = sstSalida;
        this.dboEntrada = dboEntrada;
        this.dboSalida = dboSalida;
        this.dqoEntrada = dqoEntrada;
        this.dqoSalida = dqoSalida;
        this.consumoTotal = consumoTotal;
        this.lodosTotales = lodosTotales;

        initComponents();
        this.setTitle("AquaGestion_PTAR - Etapa 5: Evaluación y Cumplimiento");
        this.setSize(720, 560);
        this.setLocationRelativeTo(null);
        procesos.Fondoventana.aplicar(this, "/ventanasproyecto/imagenes/Fondo.jpg");
        this.getRootPane().setDefaultButton(btnReiniciar);

        javax.swing.SwingUtilities.invokeLater(() -> cargarDatos());
    }

    private void reproducirAlerta() {
        try {
            sound = AudioSystem.getClip();
            sound.open(AudioSystem.getAudioInputStream(
                    new BufferedInputStream(getClass().getResourceAsStream("/ventanasproyecto/sonidos/alerta.wav"))
            ));
            sound.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    private void reproducirCumple() {
        try {
            java.io.InputStream audioSrc = getClass().getResourceAsStream("/ventanasproyecto/sonidos/cumple.wav");
            if (audioSrc != null) {
                sound = AudioSystem.getClip();
                sound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc)));
                sound.start();
            }
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void cargarDatos() {
      evaluacion = new procesos.EvaluacionEfluente(
                sstEntrada, sstSalida, dboEntrada, dboSalida, dqoEntrada, dqoSalida, consumoTotal, lodosTotales);

        double limSST = procesos.EvaluacionEfluente.LIMITE_SST; // 90 mg/L
        double limDBO = procesos.EvaluacionEfluente.LIMITE_DBO; // 90 mg/L
        double limDQO = procesos.EvaluacionEfluente.LIMITE_DQO; // 180 mg/L

        boolean cumpleSST = sstSalida <= limSST;
        boolean cumpleDBO = dboSalida <= limDBO;
        boolean cumpleDQO = dqoSalida <= limDQO;
        boolean cumpleGlobal = cumpleSST && cumpleDBO && cumpleDQO;

        boolean alertaSST = cumpleSST && sstSalida >= (limSST * 0.80);
        boolean alertaDBO = cumpleDBO && dboSalida >= (limDBO * 0.80);
        boolean alertaDQO = cumpleDQO && dqoSalida >= (limDQO * 0.80);
        boolean hayAlertaPreventiva = cumpleGlobal && (alertaSST || alertaDBO || alertaDQO);

        pnlSemaforo.setOpaque(true);
        lblDictamen.setHorizontalAlignment(SwingConstants.CENTER);
        if (!cumpleGlobal) {
            pnlSemaforo.setBackground(new Color(255, 235, 238));
            pnlSemaforo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(198, 40, 40), 2),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            lblDictamen.setForeground(new Color(183, 28, 28));
            lblDictamen.setText("<html><center><b>&#10008; VERTIMIENTO NO CUMPLE</b><br>Resolución 0631 de 2015</center></html>");
            reproducirAlerta();
        } else if (hayAlertaPreventiva) {
            pnlSemaforo.setBackground(new Color(255, 248, 225));
            pnlSemaforo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(245, 127, 23), 2),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            lblDictamen.setForeground(new Color(230, 81, 0));
            lblDictamen.setText("<html><center><b>&#9888; CUMPLE (ALERTA PREVENTIVA)</b><br>Cercano a límite Res. 0631</center></html>");
        } else {
            pnlSemaforo.setBackground(new Color(232, 245, 233));
            pnlSemaforo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(46, 125, 50), 2),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            lblDictamen.setForeground(new Color(27, 94, 32));
            lblDictamen.setText("<html><center><b>&#10004; VERTIMIENTO CUMPLE</b><br>Resolución 0631 de 2015</center></html>");
            reproducirCumple();
        }

        Color okColor = new Color(27, 94, 32);
        Color warnColor = new Color(230, 81, 0);
        Color errColor = new Color(198, 40, 40);

        lblDashSST.setText(String.format("SST   │  %.2f mg/L  │  %.2f mg/L  │  %.0f mg/L", sstEntrada, sstSalida, limSST));
        lblDashSST.setForeground(!cumpleSST ? errColor : (alertaSST ? warnColor : okColor));

        lblDashDBO.setText(String.format("DBO5  │  %.2f mg/L  │  %.2f mg/L  │  %.0f mg/L", dboEntrada, dboSalida, limDBO));
        lblDashDBO.setForeground(!cumpleDBO ? errColor : (alertaDBO ? warnColor : okColor));

        lblDashDQO.setText(String.format("DQO   │  %.2f mg/L  │  %.2f mg/L  │  %.0f mg/L", dqoEntrada, dqoSalida, limDQO));
        lblDashDQO.setForeground(!cumpleDQO ? errColor : (alertaDQO ? warnColor : okColor));

        lblEficDBO.setText(String.format("Eficiencia DBO: %.2f %%", evaluacion.getEficienciaDBO()));
        lblEficSST.setText(String.format("Eficiencia SST: %.2f %%", evaluacion.getEficienciaSST()));
        lblConsumoTotal.setText(String.format("Consumo Total: %.2f kWh/día", consumoTotal));
        lblLodosTotales.setText(String.format("Lodos Totales: %.2f kg/día", lodosTotales));

        txtDiagnostico.setHorizontalAlignment(SwingConstants.LEFT);
        txtDiagnostico.setVerticalAlignment(SwingConstants.TOP);
        txtDiagnostico.setOpaque(true);

        List<String> acciones = new ArrayList<>();
        if (!cumpleSST) {
            acciones.add("- SST fuera de norma: purgar lodos en clarificador y verificar coagulación/sedimentación.");
        } else if (alertaSST) {
            acciones.add("- SST cercano al límite: monitorear turbidez y purga de sedimentador.");
        }

        if (!cumpleDBO) {
            acciones.add("- DBO5 excedida: aumentar aireación en el reactor biológico (V3) y revisar tiempo de retención.");
        } else if (alertaDBO) {
            acciones.add("- DBO5 en zona preventiva: ajustar suministro de oxígeno disuelto en V3.");
        }

        if (!cumpleDQO) {
            acciones.add("- DQO excedida: controlar vertimientos industriales en entrada (V1) y optimizar oxidación.");
        } else if (alertaDQO) {
            acciones.add("- DQO cercana al límite: vigilar carga orgánica no biodegradable en afluente.");
        }

        if (lodosTotales > 150.0) {
            acciones.add("- Lodos > 150 kg/día: mantener activo Lecho de Secado o Filtro Prensa.");
        }

        if (acciones.isEmpty()) {
            txtDiagnostico.setBackground(new Color(232, 245, 233));
            txtDiagnostico.setForeground(new Color(27, 94, 32));
            txtDiagnostico.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(165, 214, 167), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            resumenDiagnosticoTexto = "Operación óptima: todos los parámetros cumplen holgadamente la Res. 0631/2015. Mantener régimen actual.";
            txtDiagnostico.setText("<html><b>Diagnóstico Operativo:</b> " + resumenDiagnosticoTexto + "</html>");

            JOptionPane.showMessageDialog(this,
                    "✅ VERTIMIENTO CUMPLE (Resolución 0631 de 2015)\n\n"
                    + resumenDiagnosticoTexto,
                    "Dictamen Favorable - Evaluación Final",
                    JOptionPane.INFORMATION_MESSAGE);

        } else if (!cumpleGlobal) {
            txtDiagnostico.setBackground(new Color(255, 235, 238));
            txtDiagnostico.setForeground(new Color(183, 28, 28));
            txtDiagnostico.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(239, 154, 154), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            resumenDiagnosticoTexto = String.join("\n", acciones);
            txtDiagnostico.setText("<html><b>Acción Requerida en Planta:</b> " + String.join(" ", acciones) + "</html>");

            JOptionPane.showMessageDialog(this,
                    "🚨 ALERTA DE INCUMPLIMIENTO (Resolución 0631 de 2015)\n\n"
                    + "El efluente supera los límites máximos permisibles.\n\n"
                    + "Acciones requeridas en planta:\n" + resumenDiagnosticoTexto,
                    "Alerta Normativa - Evaluación Final",
                    JOptionPane.ERROR_MESSAGE);

        } else {
            txtDiagnostico.setBackground(new Color(255, 248, 225));
            txtDiagnostico.setForeground(new Color(230, 81, 0));
            txtDiagnostico.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 224, 130), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            resumenDiagnosticoTexto = String.join("\n", acciones);
            txtDiagnostico.setText("<html><b>Recomendación Preventiva:</b> " + String.join(" ", acciones) + "</html>");

            JOptionPane.showMessageDialog(this,
                    "⚠️ ALERTA PREVENTIVA (Cercano al límite Res. 0631)\n\n"
                    + "El vertimiento cumple, pero uno o más parámetros superan el 80% del límite.\n\n"
                    + "Recomendaciones:\n" + resumenDiagnosticoTexto,
                    "Alerta Preventiva - Evaluación Final",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ventana5Evaluacion.class.getName());

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlSemaforo = new javax.swing.JPanel();
        lblDictamen = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        btnReiniciar = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtDiagnostico = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblDashSST = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblDashDBO = new javax.swing.JLabel();
        lblDashDQO = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblEficDBO = new javax.swing.JLabel();
        lblEficSST = new javax.swing.JLabel();
        lblConsumoTotal = new javax.swing.JLabel();
        lblLodosTotales = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        pnlSemaforo.setPreferredSize(new java.awt.Dimension(200, 100));

        lblDictamen.setText("<html><center>Vertimiento CUMPLE<br>Resolución 0631 de 2015</center></html>");

        javax.swing.GroupLayout pnlSemaforoLayout = new javax.swing.GroupLayout(pnlSemaforo);
        pnlSemaforo.setLayout(pnlSemaforoLayout);
        pnlSemaforoLayout.setHorizontalGroup(
            pnlSemaforoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSemaforoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDictamen)
                .addContainerGap())
        );
        pnlSemaforoLayout.setVerticalGroup(
            pnlSemaforoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSemaforoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDictamen, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnVolver.setText("← Volver a Tratamiento final");
        btnVolver.addActionListener(this::btnVolverActionPerformed);

        btnReiniciar.setText("🔄 Reiniciar Simulación");
        btnReiniciar.addActionListener(this::btnReiniciarActionPerformed);

        btnExportar.setText("📄 Exportar Reporte .TXT");
        btnExportar.setActionCommand("<html><center>📄 Exportar Reporte .TXT</center></html>");
        btnExportar.addActionListener(this::btnExportarActionPerformed);

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("AquaGestion_PTAR");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Evaluación");

        txtDiagnostico.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Comparación Normativa (Res. 0631/2015)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 0, 13))); // NOI18N
        jPanel1.setOpaque(false);

        lblDashSST.setText("SST             │ 220 mg/L │ 6.6 mg/L    │ 90 mg/L");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Parámetro  |  Entrada     |  Salida     |  Límite Res.0631");

        lblDashDBO.setText("DBO5         │ 200 mg/L │ 20.0 mg/L  │ 90 mg/L");

        lblDashDQO.setText("DQO          │ 400 mg/L │ 60.0 mg/L   │ 180 mg/L");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDashSST)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDashDBO)
                    .addComponent(lblDashDQO))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDashSST)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDashDBO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDashDQO)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Indicadores Globales de la PTAR", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 0, 13))); // NOI18N
        jPanel2.setOpaque(false);

        lblEficDBO.setText("Eficiencia DBO: [ ___ ] %");

        lblEficSST.setText("Eficiencia SST: [ ___ ] %");

        lblConsumoTotal.setText("Consumo Total: [ ___ ] kWh/día");

        lblLodosTotales.setText("Lodos Totales: [ ___ ] kg/día");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLodosTotales)
                    .addComponent(lblConsumoTotal)
                    .addComponent(lblEficDBO)
                    .addComponent(lblEficSST))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEficDBO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEficSST)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblConsumoTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLodosTotales)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btnVolver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnReiniciar)
                            .addComponent(btnExportar))
                        .addGap(118, 118, 118))))
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 109, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSemaforo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(txtDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSemaforo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnReiniciar))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        new Ventana4Clarificacion().setVisible(true);
        this.dispose();
        sound.close();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Reporte_PTAR_Res0631.txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) {
                ruta += ".txt";
            }
            if (evaluacion.exportarReporteTXT(ruta)) {
                try (PrintWriter out = new PrintWriter(new FileWriter(ruta, true))) {
                    out.println();
                    out.println("==================================================");
                    out.println("DIAGNÓSTICO OPERATIVO Y PLAN DE ACCIÓN EN PLANTA:");
                    out.println(resumenDiagnosticoTexto);
                    out.println("==================================================");
                } catch (Exception ignored) {
                }
                JOptionPane.showMessageDialog(this, "¡Reporte exportado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnExportarActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        Ventana1Captacion v1 = new Ventana1Captacion();
        v1.setLocationRelativeTo(null);
        v1.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReiniciarActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new Ventana5Evaluacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JButton btnVolver;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblConsumoTotal;
    private javax.swing.JLabel lblDashDBO;
    private javax.swing.JLabel lblDashDQO;
    private javax.swing.JLabel lblDashSST;
    private javax.swing.JLabel lblDictamen;
    private javax.swing.JLabel lblEficDBO;
    private javax.swing.JLabel lblEficSST;
    private javax.swing.JLabel lblLodosTotales;
    private javax.swing.JPanel pnlSemaforo;
    private javax.swing.JLabel txtDiagnostico;
    // End of variables declaration//GEN-END:variables
}