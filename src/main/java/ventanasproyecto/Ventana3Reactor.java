/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventanasproyecto;

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Fa051
 */
public class Ventana3Reactor extends javax.swing.JFrame {
    
    private double caudalReal = 10.0;
    private double sstBruto = 220.0;
    private double dboBruto = 200.0;
    private double dqoBruto = 400.0;
    private double sstEntrada = 77.0;
    private double dboEntrada = 130.0;
    private double dqoEntrada = 260.0;
    private double lodosPrimarios = 120.0;
    private String estadoPlanta = "Normal";
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ventana3Reactor.class.getName());
    private Clip sound;

    public Ventana3Reactor() {
        this(10.0, 220.0, 200.0, 400.0, 77.0, 130.0, 260.0, 120.0, "Normal");
    }

    public Ventana3Reactor(double caudalReal, double sstEntrada, double dboEntrada, double dqoEntrada) {
        this(caudalReal, 220.0, 200.0, 400.0, sstEntrada, dboEntrada, dqoEntrada, 120.0, "Normal");
    }

    public Ventana3Reactor(double caudalReal, double sstBruto, double dboBruto, double dqoBruto,
                           double sstEntrada, double dboEntrada, double dqoEntrada,
                           double lodosPrimarios, String estadoPlanta) {
        this.caudalReal = caudalReal;
        this.sstBruto = sstBruto;
        this.dboBruto = dboBruto;
        this.dqoBruto = dqoBruto;
        this.sstEntrada = sstEntrada;
        this.dboEntrada = dboEntrada;
        this.dqoEntrada = dqoEntrada;
        this.lodosPrimarios = lodosPrimarios;
        this.estadoPlanta = estadoPlanta;

        initComponents();
        buttonGroup1.add(rbControlAuto);
        buttonGroup1.add(rbControlManual);

        this.setTitle("AquaGestion_PTAR - Etapa 3: Reactor Biológico");
       
        this.setSize(720, 560);
        this.setLocationRelativeTo(null);
        procesos.Fondoventana.aplicar(this, "/ventanasproyecto/imagenes/Fondo.jpg");

        cargarGifAireacion();

        lblQreal.setText(String.format("Caudal Real: %.2f L/s", caudalReal));
        lblSstEntrada.setText(String.format("SST Remanentes: %.2f mg/L", sstEntrada));
        lblDboEntrada.setText(String.format("DBO5 Inicial: %.2f mg/L", dboEntrada));
        lblDqoEntrada.setText(String.format("DQO Inicial: %.2f mg/L", dqoEntrada));

        lblDboSalida.setText("DBO5 Salida: -- mg/L");
        lblDqoSalida.setText("DQO Salida: -- mg/L");
        lblMasaRemovida.setText("Masa DBO Removida: -- kg/día");
        lblConsumoEnergia.setText("Consumo Energético: -- kWh/día");
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

    private void cargarGifAireacion() {
        java.net.URL url = getClass().getResource("/ventanasproyecto/imagenes/aireacion.gif");
        if (url != null) {
            lblAireacion.setIcon(new javax.swing.ImageIcon(url));
            lblAireacion.setText("");
        }
    }

    private void actualizarSimulacion(boolean desdeEnter) {
        String textoOd = txtOxigeno.getText().trim();
        if (textoOd.isEmpty()) {
            if (desdeEnter) {
                reproducirAlerta();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese la cantidad de Oxígeno Disuelto (OD).",
                        "Campo vacío", javax.swing.JOptionPane.WARNING_MESSAGE);
                txtOxigeno.requestFocus();
            }
            return;
        }

        if (textoOd.contains(",")) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se permite el uso de coma (,). Use punto (.) para decimales, por ejemplo: 1.56",
                    "Use punto decimal", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }

        try {
            double od = Double.parseDouble(textoOd);
            if (od < 0) {
                reproducirAlerta();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "El Oxígeno Disuelto (OD) no puede ser un valor negativo.",
                        "Dato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
                txtOxigeno.requestFocus();
                return;
            }

            if (!rbControlAuto.isSelected() && !rbControlManual.isSelected()) {
                if (desdeEnter) {
                    reproducirAlerta();
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Ahora seleccione el modo de Control del Reactor (Automático o Manual).",
                            "Paso siguiente", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    rbControlAuto.requestFocus();
                }
                return;
            }

            boolean esAuto = rbControlAuto.isSelected();

            double[] umbralesOD = {1.0, 1.5, 3.0, 4.0};

            procesos.ReactorBiologico reactor =
                    new procesos.ReactorBiologico(caudalReal, sstEntrada, dboEntrada, dqoEntrada, od, esAuto);

            lblDboSalida.setText(String.format("DBO5 Salida: %.2f mg/L", reactor.getDboSalida()));
            lblDqoSalida.setText(String.format("DQO Salida: %.2f mg/L", reactor.getDqoSalida()));
            lblMasaRemovida.setText(String.format("Masa DBO Removida: %.2f kg/día", reactor.getMasaDboRemovidaKgDia()));
            lblConsumoEnergia.setText(String.format("Consumo Energético: %.2f kWh/día", reactor.getConsumoEnergiaKwhDia()));

            if (od < umbralesOD[0] || reactor.tieneRiesgoAnoxia()) {
                reproducirAlerta();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "🚨 ALERTA CRÍTICA DE ANOXIA (OD < 1.0 mg/L)\n\n"
                        + "Riesgo de muerte bacteriana por falta de oxígeno.\n"
                        + "Los sopladores se han encendido al 100% de emergencia.",
                        "Alerta Operativa - Reactor Biológico",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            } else if (od > umbralesOD[3]) {
                reproducirAlerta();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "⚠️ ALERTA DE EFICIENCIA (OD > 4.0 mg/L)\n\n"
                        + "Reactor sobre-oxigenado. Inyección de aire suspendida para evitar desperdicio de energía eléctrica.",
                        "Alerta de Desperdicio Energético",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Ingrese un valor numérico válido para el Oxígeno Disuelto (use punto para decimales).",
                    "Formato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        txtOxigeno = new javax.swing.JTextField();
        rbControlAuto = new javax.swing.JRadioButton();
        rbControlManual = new javax.swing.JRadioButton();
        btnVolver = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblQreal = new javax.swing.JLabel();
        lblDboEntrada = new javax.swing.JLabel();
        lblDqoEntrada = new javax.swing.JLabel();
        lblSstEntrada = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblDboSalida = new javax.swing.JLabel();
        lblDqoSalida = new javax.swing.JLabel();
        lblMasaRemovida = new javax.swing.JLabel();
        lblConsumoEnergia = new javax.swing.JLabel();
        lblResultados = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblAireacion = new javax.swing.JLabel();
        lblImgControl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(720, 560));
        setResizable(false);

        txtOxigeno.addActionListener(this::txtOxigenoActionPerformed);

        rbControlAuto.setText("Control Automático por Umbrales");
        rbControlAuto.addActionListener(this::rbControlAutoActionPerformed);

        rbControlManual.setText("Control Manual de Sopladores");
        rbControlManual.addActionListener(this::rbControlManualActionPerformed);

        btnVolver.setText("← Volver a Primario");
        btnVolver.addActionListener(this::btnVolverActionPerformed);

        btnSiguiente.setText("Pasar a Clarificación →");
        btnSiguiente.addActionListener(this::btnSiguienteActionPerformed);

        jLabel1.setText("mg/L");

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("AquaGestion_PTAR");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        jLabel3.setText("Seleccione modo de Control del Reactor :");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos desde Entrada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 0, 12))); // NOI18N

        lblQreal.setText("Caudal Real :  L/s");

        lblDboEntrada.setText("DBO5 Inicial: mg/L");

        lblDqoEntrada.setText("DQO Inicial: mg/L");

        lblSstEntrada.setText("SST Remanentes : mg/L");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblDboEntrada, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(lblQreal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSstEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDqoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 146, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQreal)
                    .addComponent(lblSstEntrada))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDboEntrada)
                    .addComponent(lblDqoEntrada)))
        );

        jLabel4.setText("Ingrese la cantidad de Oxigeno Disuelto");

        lblDboSalida.setText("DBO5 Salida:  mg/L");

        lblDqoSalida.setText("DQO Salida: mg/L");

        lblMasaRemovida.setText("Masa DBO Removida: kg/día");

        lblConsumoEnergia.setText("Consumo Energético: kWh/día");

        lblResultados.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        lblResultados.setText("Resultados Biológicos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblResultados)
                    .addComponent(lblDboSalida)
                    .addComponent(lblDqoSalida)
                    .addComponent(lblMasaRemovida)
                    .addComponent(lblConsumoEnergia))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblResultados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDboSalida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDqoSalida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMasaRemovida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblConsumoEnergia)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("            Tratamiento Secundario");

        lblAireacion.setMaximumSize(new java.awt.Dimension(180, 95));
        lblAireacion.setMinimumSize(new java.awt.Dimension(180, 95));
        lblAireacion.setPreferredSize(new java.awt.Dimension(180, 95));

        lblImgControl.setPreferredSize(new java.awt.Dimension(150, 85));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(lblAireacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))))
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbControlManual)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(txtOxigeno, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(rbControlAuto)
                            .addComponent(lblImgControl, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 123, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVolver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSiguiente)
                        .addGap(49, 49, 49))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtOxigeno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(36, 36, 36)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblImgControl, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(rbControlAuto)
                        .addGap(18, 18, 18)
                        .addComponent(rbControlManual)
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnVolver)
                            .addComponent(btnSiguiente))
                        .addGap(105, 105, 105))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(lblAireacion, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(269, 269, 269))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtOxigenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOxigenoActionPerformed
        actualizarSimulacion(true);
    }//GEN-LAST:event_txtOxigenoActionPerformed

    private void rbControlAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbControlAutoActionPerformed
        if (txtOxigeno.getText().trim().isEmpty()) {
            buttonGroup1.clearSelection();
            lblImgControl.setIcon(null);
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Primero debe ingresar la cantidad de Oxígeno Disuelto (OD).",
                    "Campo vacío", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }
        actualizarImagenControl();
        actualizarSimulacion(false);
    }//GEN-LAST:event_rbControlAutoActionPerformed

    private void rbControlManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbControlManualActionPerformed
        if (txtOxigeno.getText().trim().isEmpty()) {
            buttonGroup1.clearSelection();
            lblImgControl.setIcon(null);
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Primero debe ingresar la cantidad de Oxígeno Disuelto (OD).",
                    "Campo vacío", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }
        actualizarImagenControl();
        actualizarSimulacion(false);
    }//GEN-LAST:event_rbControlManualActionPerformed

    private void actualizarImagenControl() {
        String ruta = "";
        if (rbControlAuto.isSelected()) {
            ruta = "/ventanasproyecto/imagenes/umbrales.jpg";
        } else if (rbControlManual.isSelected()) {
            ruta = "/ventanasproyecto/imagenes/sopladores.jpg";
        } else {
            lblImgControl.setIcon(null);
            lblImgControl.setText("");
            return;
        }

        java.net.URL imgURL = getClass().getResource(ruta);
        if (imgURL != null) {
            javax.swing.ImageIcon variable = new javax.swing.ImageIcon(imgURL);
            lblImgControl.setIcon(new javax.swing.ImageIcon(
                    variable.getImage().getScaledInstance(lblImgControl.getWidth(), lblImgControl.getHeight(), java.awt.Image.SCALE_DEFAULT)
            ));
            lblImgControl.setText("");
        } else {
            lblImgControl.setIcon(null);
            lblImgControl.setText("Sin imagen");
        }
    }

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        new Ventana2Tratamiento(caudalReal, sstBruto, dboBruto, dqoBruto, estadoPlanta).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        String textoOd = txtOxigeno.getText().trim();
        if (textoOd.isEmpty()) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese la cantidad de Oxígeno Disuelto (OD).",
                    "Campo vacío", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }
        if (textoOd.contains(",")) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No se permite el uso de coma (,). Use punto (.) para decimales, por ejemplo: 1.56",
                    "Use punto decimal", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }

        if (!rbControlAuto.isSelected() && !rbControlManual.isSelected()) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un Modo de Control de Aireación antes de continuar.",
                    "Selección requerida", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double od = Double.parseDouble(textoOd);
            if (od < 0) {
                reproducirAlerta();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "El Oxígeno Disuelto (OD) no puede ser negativo.",
                        "Dato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean esAuto = rbControlAuto.isSelected();
            procesos.ReactorBiologico reactor =
                    new procesos.ReactorBiologico(caudalReal, sstEntrada, dboEntrada, dqoEntrada, od, esAuto);

            Ventana4Clarificacion v4 = new Ventana4Clarificacion(
                    caudalReal, sstBruto, dboBruto, dqoBruto,
                    sstEntrada, reactor.getDboSalida(), reactor.getDqoSalida(),
                    lodosPrimarios, reactor.getConsumoEnergiaKwhDia(), estadoPlanta
            );
            v4.setVisible(true);
            this.dispose();
        } catch (NumberFormatException e) {
            reproducirAlerta();
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Ingrese un valor numérico válido para el Oxígeno Disuelto (ej: 2.5).",
                    "Formato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
        }
    }//GEN-LAST:event_btnSiguienteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Ventana3Reactor().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnVolver;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAireacion;
    private javax.swing.JLabel lblConsumoEnergia;
    private javax.swing.JLabel lblDboEntrada;
    private javax.swing.JLabel lblDboSalida;
    private javax.swing.JLabel lblDqoEntrada;
    private javax.swing.JLabel lblDqoSalida;
    private javax.swing.JLabel lblImgControl;
    private javax.swing.JLabel lblMasaRemovida;
    private javax.swing.JLabel lblQreal;
    private javax.swing.JLabel lblResultados;
    private javax.swing.JLabel lblSstEntrada;
    private javax.swing.JRadioButton rbControlAuto;
    private javax.swing.JRadioButton rbControlManual;
    private javax.swing.JTextField txtOxigeno;
    // End of variables declaration//GEN-END:variables
}