package ventanasproyecto;

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

public class Ventana4Clarificacion extends javax.swing.JFrame {
    private double caudalReal = 10.0;
    private double sstBruto = 220.0;
    private double dboBruto = 200.0;
    private double dqoBruto = 400.0;
    private double sstSalidaV2 = 77.0;
    private double dboSalidaV3 = 13.0;
    private double dqoSalidaV3 = 45.0;
    private double lodosPrimarios = 120.0;
    private double energiaV3 = 15.0;
    private String estadoPlanta = "Normal";
    private boolean alertaMostrada = false;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Ventana4Clarificacion.class.getName());
    private Clip sound;

    public Ventana4Clarificacion() {
        this(10.0, 220.0, 200.0, 400.0, 77.0, 13.0, 45.0, 120.0, 15.0, "Normal");
    }

    public Ventana4Clarificacion(double caudalReal, double sstSalidaV2, double dboSalidaV3, double lodosPrimarios) {
        this(caudalReal, 220.0, 200.0, 400.0, sstSalidaV2, dboSalidaV3, dboSalidaV3 * 2.5, lodosPrimarios, 15.0, "Normal");
    }

    public Ventana4Clarificacion(double caudalReal, double sstBruto, double dboBruto, double dqoBruto,
                                 double sstSalidaV2, double dboSalidaV3, double dqoSalidaV3,
                                 double lodosPrimarios, double energiaV3, String estadoPlanta) {
        this.caudalReal = caudalReal;
        this.sstBruto = sstBruto;
        this.dboBruto = dboBruto;
        this.dqoBruto = dqoBruto;
        this.sstSalidaV2 = sstSalidaV2;
        this.dboSalidaV3 = dboSalidaV3;
        this.dqoSalidaV3 = dqoSalidaV3;
        this.lodosPrimarios = lodosPrimarios;
        this.energiaV3 = energiaV3;
        this.estadoPlanta = estadoPlanta;

        initComponents();
        this.setTitle("AquaGestion_PTAR - Etapa 4: Clarificación y Desinfección");
        this.setSize(720, 560);
        this.setLocationRelativeTo(null);
        procesos.Fondoventana.aplicar(this, "/ventanasproyecto/imagenes/Fondo.jpg");

        lblQreal.setText(String.format("Qreal: %.2f L/s", caudalReal));
        lblSstV2.setText(String.format("SST Salida V2: %.2f mg/L", sstSalidaV2));
        lblDboV3.setText(String.format("DBO Salida V3: %.2f mg/L", dboSalidaV3));
        lblLodosPrimarios.setText(String.format("Lodos Primarios: %.2f kg/día", lodosPrimarios));

        bgDesinfeccion.clearSelection();
        jComboBox1.setSelectedIndex(0);
        Desinfeccion.setIcon(null);
        Desinfeccion.setText("");
        Lodos.setIcon(null);
        Lodos.setText("");

        lblSstFinal.setText("SST Final (Clarificado): -- mg/L");
        lblLodosBiologicos.setText("Lodos Biológicos: -- kg/día");
        lblLodosTotales.setText("LODOS TOTALES PLANTA: -- kg/día");
        this.getRootPane().setDefaultButton(btnSiguiente);
        javax.swing.UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
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

    private String obtenerSistemaLodosSeleccionado() {
        int idx = jComboBox1.getSelectedIndex();
        if (idx == 2) {
            return "Lecho de Secado";
        } else if (idx == 3) {
            return "Filtro Prensa";
        }
        return "Ninguno";
    }

    private void actualizarImagenDesinfeccion() {
        String ruta = "";
        if (rbCloracion.isSelected()) {
            ruta = "/ventanasproyecto/imagenes/cloracion.gif";
        } else if (rbUV.isSelected()) {
            ruta = "/ventanasproyecto/imagenes/radiacion.gif";
        } else {
            Desinfeccion.setIcon(null);
            Desinfeccion.setText("");
            return;
        }

        java.net.URL imgURL = getClass().getResource(ruta);
        if (imgURL != null) {
            javax.swing.ImageIcon variable = new javax.swing.ImageIcon(imgURL);
            Desinfeccion.setIcon(new javax.swing.ImageIcon(
                    variable.getImage().getScaledInstance(Desinfeccion.getWidth(), Desinfeccion.getHeight(), java.awt.Image.SCALE_DEFAULT)
            ));
            Desinfeccion.setText("");
        } else {
            Desinfeccion.setIcon(null);
            Desinfeccion.setText("Sin imagen");
        }
    }

    private void actualizarImagenLodos() {
        int idx = jComboBox1.getSelectedIndex();
        String ruta = "";
        switch (idx) {
            case 1 -> ruta = "/ventanasproyecto/imagenes/purga.jpg";
            case 2 -> ruta = "/ventanasproyecto/imagenes/lecho.jpg";
            case 3 -> ruta = "/ventanasproyecto/imagenes/filtro.jpg";
            default -> {
                Lodos.setIcon(null);
                Lodos.setText("");
                return;
            }
        }

        java.net.URL imgURL = getClass().getResource(ruta);
        if (imgURL != null) {
            javax.swing.ImageIcon variable = new javax.swing.ImageIcon(imgURL);
            Lodos.setIcon(new javax.swing.ImageIcon(
                    variable.getImage().getScaledInstance(Lodos.getWidth(), Lodos.getHeight(), java.awt.Image.SCALE_DEFAULT)
            ));
            Lodos.setText("");
        } else {
            Lodos.setIcon(null);
            Lodos.setText("Sin imagen");
        }
    }

    private void actualizarSimulacion(boolean permitirAlertaFlotante) {
        if ((!rbCloracion.isSelected() && !rbUV.isSelected()) || jComboBox1.getSelectedIndex() == 0) {
            return;
        }

        boolean esCloracion = rbCloracion.isSelected();
        String sistemaLodos = obtenerSistemaLodosSeleccionado();

        procesos.TratamientoFinal finalT =
                new procesos.TratamientoFinal(caudalReal, sstSalidaV2, dboSalidaV3, lodosPrimarios, esCloracion);
        finalT.setSistemaLodos(sistemaLodos);

        lblSstFinal.setText(String.format("SST Final (Clarificado): %.2f mg/L", finalT.getSstFinal()));
        lblLodosBiologicos.setText(String.format("Lodos Biológicos: %.2f kg/día", finalT.getLodosBiologicosKgDia()));

        if (sistemaLodos.equals("Ninguno")) {
            lblLodosTotales.setText(String.format("LODOS TOTALES PLANTA: %.2f kg/día", finalT.getLodosTotalesKgDia()));
        } else {
            lblLodosTotales.setText(String.format("LODOS FINALES (%s): %.2f kg/día",
                    sistemaLodos, finalT.getLodosTotalesKgDia()));
        }

        if (finalT.tieneSaturacionLodos() && permitirAlertaFlotante && !alertaMostrada) {
            alertaMostrada = true;
            reproducirAlerta();
            String mensaje = String.format(
                    "¡ALERTA DE GESTIÓN DE LODOS!\n\n"
                    + "La acumulación bruta de lodos (%.2f kg/día) supera la capacidad de purga habitual (150,00 kg/día).\n"
                    + "Por favor, seleccione 'Lecho de Secado' o 'Filtro Prensa' en el formulario antes de continuar.",
                    finalT.getLodosBrutosKgDia()
            );
            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Alerta de Saturación de Lodos",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgDesinfeccion = new javax.swing.ButtonGroup();
        bgLodos = new javax.swing.ButtonGroup();
        rbCloracion = new javax.swing.JRadioButton();
        rbUV = new javax.swing.JRadioButton();
        btnVolver = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblQreal = new javax.swing.JLabel();
        lblSstV2 = new javax.swing.JLabel();
        lblDboV3 = new javax.swing.JLabel();
        lblLodosPrimarios = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblLodosBiologicos = new javax.swing.JLabel();
        lblLodosTotales = new javax.swing.JLabel();
        lblSstFinal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        Desinfeccion = new javax.swing.JLabel();
        Lodos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        bgDesinfeccion.add(rbCloracion);
        rbCloracion.setText("Cloración (Hipoclorito) ");
        rbCloracion.addActionListener(this::rbCloracionActionPerformed);

        bgDesinfeccion.add(rbUV);
        rbUV.setText("Radiación Ultravioleta ");
        rbUV.addActionListener(this::rbUVActionPerformed);

        btnVolver.setText("← Volver a V3");
        btnVolver.addActionListener(this::btnVolverActionPerformed);

        btnSiguiente.setText("Pasar a Evaluación →");
        btnSiguiente.addActionListener(this::btnSiguienteActionPerformed);

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        jLabel3.setText("Selecciona el método de desinfección:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos desde entrada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 0, 13))); // NOI18N

        lblQreal.setText("Qreal: [ ___ ] L/s");

        lblSstV2.setText("SST Salida V2: [ ___ ] mg/L");

        lblDboV3.setText("DBO Salida V3: [ ___ ] mg/L");

        lblLodosPrimarios.setText("Lodos Primarios: [ ___ ] kg/día");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblQreal, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSstV2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblDboV3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLodosPrimarios, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQreal)
                    .addComponent(lblSstV2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDboV3)
                    .addComponent(lblLodosPrimarios))
                .addGap(38, 38, 38))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        jLabel1.setText("Gestión y deshidratación de lodos:");

        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(230, 100));

        lblLodosBiologicos.setText("Lodos Biológicos: [ ___ ] kg/día");

        lblLodosTotales.setText("Lodos totales de planta: [ ___ ] kg/día");

        lblSstFinal.setText("SST Final (Clarificado): [ _ ] mg/L");

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 13)); // NOI18N
        jLabel2.setText("Salida y Balance de Lodos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSstFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLodosBiologicos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLodosTotales, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(lblSstFinal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLodosBiologicos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLodosTotales)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Seleccione un tratamiento --", "Purga directa", "Lecho de Secado", "Filtro Prensa" }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        Desinfeccion.setPreferredSize(new java.awt.Dimension(210, 125));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 2, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 102));
        jLabel4.setText("AquaGestion_PTAR");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Clarificación");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Lodos, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rbUV)
                                    .addComponent(rbCloracion))
                                .addGap(98, 98, 98)
                                .addComponent(Desinfeccion, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(201, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(btnVolver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSiguiente)
                .addGap(49, 49, 49))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbCloracion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbUV, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lodos, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnVolver)
                            .addComponent(btnSiguiente))
                        .addGap(54, 54, 54))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Desinfeccion, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbCloracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCloracionActionPerformed
        actualizarImagenDesinfeccion();
        actualizarSimulacion(true);
    }//GEN-LAST:event_rbCloracionActionPerformed

    private void rbUVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUVActionPerformed
        actualizarImagenDesinfeccion();
        actualizarSimulacion(true);
    }//GEN-LAST:event_rbUVActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        new Ventana3Reactor(
                caudalReal, sstBruto, dboBruto, dqoBruto,
                sstSalidaV2, dboBruto * 0.65, dqoBruto * 0.65, lodosPrimarios, estadoPlanta
        ).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        if (!rbCloracion.isSelected() && !rbUV.isSelected()) {
            reproducirAlerta();
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un método de desinfección antes de continuar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (jComboBox1.getSelectedIndex() == 0) {
            reproducirAlerta();
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar una opción de gestión y deshidratación de lodos antes de continuar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            jComboBox1.requestFocus();
            return;
        }

        boolean esCloracion = rbCloracion.isSelected();
        String sistemaLodos = obtenerSistemaLodosSeleccionado();

        procesos.TratamientoFinal finalT =
                new procesos.TratamientoFinal(caudalReal, sstSalidaV2, dboSalidaV3, lodosPrimarios, esCloracion);
        finalT.setSistemaLodos(sistemaLodos);

        if (finalT.requiereAccionLodos()) {
            reproducirAlerta();
            JOptionPane.showMessageDialog(
                    this,
                    String.format("⚠️ CAPACIDAD DE PURGA EXCEDIDA\n\n"
                            + "Los lodos acumulados (%.2f kg/día) superan el límite de 150,00 kg/día.\n"
                            + "Debe seleccionar 'Lecho de Secado' o 'Filtro Prensa' para poder continuar.",
                            finalT.getLodosBrutosKgDia()),
                    "Acción Operativa Requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double sstFinal = finalT.getSstFinal();
        double consumoTotal = energiaV3 + finalT.getConsumoEnergiaKwh();
        double lodosTotales = finalT.getLodosTotalesKgDia();

        Ventana5Evaluacion v5 = new Ventana5Evaluacion(
                sstBruto, sstFinal, dboBruto, dboSalidaV3, dqoBruto, dqoSalidaV3, consumoTotal, lodosTotales
        );
        v5.setLocationRelativeTo(null);
        v5.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (jComboBox1.getSelectedIndex() > 0 && !rbCloracion.isSelected() && !rbUV.isSelected()) {
            jComboBox1.setSelectedIndex(0);
            Lodos.setIcon(null);
            reproducirAlerta();
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe seleccionar el método de desinfección (Cloración o Radiación UV).",
                    "Orden de selección",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        actualizarImagenLodos();
        actualizarSimulacion(true);
    }//GEN-LAST:event_jComboBox1ActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new Ventana4Clarificacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Desinfeccion;
    private javax.swing.JLabel Lodos;
    private javax.swing.ButtonGroup bgDesinfeccion;
    private javax.swing.ButtonGroup bgLodos;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblDboV3;
    private javax.swing.JLabel lblLodosBiologicos;
    private javax.swing.JLabel lblLodosPrimarios;
    private javax.swing.JLabel lblLodosTotales;
    private javax.swing.JLabel lblQreal;
    private javax.swing.JLabel lblSstFinal;
    private javax.swing.JLabel lblSstV2;
    private javax.swing.JRadioButton rbCloracion;
    private javax.swing.JRadioButton rbUV;
    // End of variables declaration//GEN-END:variables
}