package aquagestion;

import ventanasproyecto.Ventana0;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AquaGestion_PTAR {

    public static void main(String[] args) 
    {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
            {
                if ("Nimbus".equals(info.getName())) 
                {
                UIManager.setLookAndFeel(info.getClassName());
                break;
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
            SwingUtilities.invokeLater(() -> 
            {
            Ventana0 v0 = new Ventana0();
            v0.setLocationRelativeTo(null);
            v0.setVisible(true);
            });
    }
}