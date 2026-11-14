package aquagestion;

import ventanasproyecto.Ventana1Captacion;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AquaGestion_PTAR {
    public static void main(String[] args) {
        // Aplicar apariencia del sistema operativo (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar la aplicación en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            Ventana1Captacion v1 = new Ventana1Captacion();
            v1.setLocationRelativeTo(null); // Centrar en pantalla
            v1.setVisible(true);
        });
    }
}