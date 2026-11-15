/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ventanasproyecto;

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
        this.getRootPane().setDefaultButton(btnSiguiente);
        this.setSize(650, 440);
        this.setLocationRelativeTo(null);

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

    private void cargarGifAireacion() {
        java.net.URL url = getClass().getResource("/proyecto/imagenes/aireacion.gif");
        if (url != null) {
            lblAireacion.setIcon(new javax.swing.ImageIcon(url));
            lblAireacion.setText("");
        }
    }

    private void actualizarSimulacion() {
        String textoOd = txtOxigeno.getText().trim();
        if (textoOd.isEmpty()) {
            return;
        }

        try {
            double od = Double.parseDouble(textoOd);
            if (od < 0) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "El Oxígeno Disuelto (OD) no puede ser un valor negativo.",
                        "Dato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!rbControlAuto.isSelected() && !rbControlManual.isSelected()) {
                return;
            }

            boolean esAuto = rbControlAuto.isSelected();

            // Arreglo de umbrales [0]=Anoxia, [1]=Mínimo Óptimo, [2]=Máximo Óptimo, [3]=Sobre-oxigenación
            double[] umbralesOD = {1.0, 1.5, 3.0, 4.0};

            procesos.ReactorBiologico reactor =
                    new procesos.ReactorBiologico(caudalReal, sstEntrada, dboEntrada, dqoEntrada, od, esAuto);

            lblDboSalida.setText(String.format("DBO5 Salida: %.2f mg/L", reactor.getDboSalida()));
            lblDqoSalida.setText(String.format("DQO Salida: %.2f mg/L", reactor.getDqoSalida()));
            lblMasaRemovida.setText(String.format("Masa DBO Removida: %.2f kg/día", reactor.getMasaDboRemovidaKgDia()));
            lblConsumoEnergia.setText(String.format("Consumo Energético: %.2f kWh/día", reactor.getConsumoEnergiaKwhDia()));

            // Alertas operativas (Anoxia < 1.0 mg/L y Desperdicio Energético > 4.0 mg/L)
            if (od < umbralesOD[0] || reactor.tieneRiesgoAnoxia()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "🚨 ALERTA CRÍTICA DE ANOXIA (OD < 1.0 mg/L)\n\n"
                        + "Riesgo de muerte bacteriana por falta de oxígeno.\n"
                        + "Los sopladores se han encendido al 100% de emergencia.",
                        "Alerta Operativa - Reactor Biológico",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            } else if (od > umbralesOD[3]) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "⚠️ ALERTA DE EFICIENCIA (OD > 4.0 mg/L)\n\n"
                        + "Reactor sobre-oxigenado. Inyección de aire suspendida para evitar desperdicio de energía eléctrica.",
                        "Alerta de Desperdicio Energético",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Ingrese un valor numérico válido para el Oxígeno Disuelto (use punto para decimales).",
                    "Formato inválido", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
        }
    }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ventana3Reactor.class.getName());

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
        lblAireacion = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtOxigeno.addActionListener(this::txtOxigenoActionPerformed);

        rbControlAuto.setText("Control Automático por Umbrales");
        rbControlAuto.addActionListener(this::rbControlAutoActionPerformed);

        rbControlManual.setText("Control Manual de Sopladores");
        rbControlManual.addActionListener(this::rbControlManualActionPerformed);

        btnVolver.setText("← Volver a V2");
        btnVolver.addActionListener(this::btnVolverActionPerformed);

        btnSiguiente.setText("Pasar a Clarificación →");
        btnSiguiente.addActionListener(this::btnSiguienteActionPerformed);

        jLabel1.setText("mg/L");

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("AquaGestion_PTAR");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        jLabel3.setText("CONTROL DE AIREACIÓN");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos desde Entrada"));

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
                        .addGap(0, 0, Short.MAX_VALUE)))
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
        lblResultados.setText("RESULTADOS BIOLOGICOS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDboSalida)
                    .addComponent(lblDqoSalida)
                    .addComponent(lblMasaRemovida)
                    .addComponent(lblConsumoEnergia)
                    .addComponent(lblResultados))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblResultados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDboSalida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDqoSalida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMasaRemovida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblConsumoEnergia))
        );

        lblAireacion.setMaximumSize(new java.awt.Dimension(180, 95));
        lblAireacion.setMinimumSize(new java.awt.Dimension(180, 95));
        lblAireacion.setPreferredSize(new java.awt.Dimension(180, 95));

        jLabel2.setText("Etapa 3: Tratamiento Secundario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtOxigeno, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel1))
                                    .addComponent(rbControlAuto)
                                    .addComponent(rbControlManual)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(btnVolver)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSiguiente)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAireacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtOxigeno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblAireacion, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rbControlAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbControlManual)
                        .addGap(239, 239, 239))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSiguiente)
                            .addComponent(btnVolver))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtOxigenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOxigenoActionPerformed
    actualizarSimulacion();        // TODO add your handling code here:
    }//GEN-LAST:event_txtOxigenoActionPerformed

    private void rbControlAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbControlAutoActionPerformed
actualizarSimulacion();        // TODO add your handling code here:
    }//GEN-LAST:event_rbControlAutoActionPerformed

    private void rbControlManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbControlManualActionPerformed
actualizarSimulacion();        // TODO add your handling code here:
    }//GEN-LAST:event_rbControlManualActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
    new Ventana2Tratamiento(caudalReal, sstBruto, dboBruto, dqoBruto, estadoPlanta).setVisible(true);
    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
    String textoOd = txtOxigeno.getText().trim();
        if (textoOd.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese la cantidad de Oxígeno Disuelto (OD).",
                    "Campo vacío", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtOxigeno.requestFocus();
            return;
        }

        if (!rbControlAuto.isSelected() && !rbControlManual.isSelected()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un Modo de Control de Aireación antes de continuar.",
                    "Selección requerida", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double od = Double.parseDouble(textoOd);
            if (od < 0) {
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
    private javax.swing.JLabel lblMasaRemovida;
    private javax.swing.JLabel lblQreal;
    private javax.swing.JLabel lblResultados;
    private javax.swing.JLabel lblSstEntrada;
    private javax.swing.JRadioButton rbControlAuto;
    private javax.swing.JRadioButton rbControlManual;
    private javax.swing.JTextField txtOxigeno;
    // End of variables declaration//GEN-END:variables
}
