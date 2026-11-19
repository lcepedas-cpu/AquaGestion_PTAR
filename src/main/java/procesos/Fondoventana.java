package procesos;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Fondoventana {

    public static void aplicar(JFrame ventana, String rutaImagen) {
        URL url = ventana.getClass().getResource(rutaImagen);
        if (url == null) {
            System.out.println("No se encontró la imagen: " + rutaImagen);
            return;
        }

        ImageIcon icono = new ImageIcon(url);
        JLabel lblFondo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(icono.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        lblFondo.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        ventana.getLayeredPane().add(lblFondo, JLayeredPane.FRAME_CONTENT_LAYER);
        ((JPanel) ventana.getContentPane()).setOpaque(false);

        // Vuelve transparentes automáticamente los paneles y radio buttons internos
        hacerTransparentes(ventana.getContentPane().getComponents());
    }

    private static void hacerTransparentes(Component[] componentes) {
        for (Component c : componentes) {
            if (c instanceof JPanel) {
                ((JPanel) c).setOpaque(false);
                hacerTransparentes(((JPanel) c).getComponents());
            } else if (c instanceof AbstractButton && !(c instanceof javax.swing.JButton)) {
                // Radio buttons y checkboxes sin cuadro gris, manteniendo los JButton normales
                ((AbstractButton) c).setOpaque(false);
            }
        }
    }
}
