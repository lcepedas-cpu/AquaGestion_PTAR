package aquagestion;

import ventanasproyecto.Ventana0;
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

        // Iniciar la aplicación desde Ventana0 en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            Ventana0 v0 = new Ventana0();
            v0.setLocationRelativeTo(null); // Centrar en pantalla
            v0.setVisible(true);
        });
    }
}