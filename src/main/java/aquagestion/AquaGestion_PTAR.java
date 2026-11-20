package aquagestion;

import ventanasproyecto.Ventana0;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AquaGestion_PTAR {

    public static void main(String[] args) {
        // Aplicar apariencia del sistema operativo (Look and Feel)
        // Aplicar apariencia Nimbus (igual que en las ventanas)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
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