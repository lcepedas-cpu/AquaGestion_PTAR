package procesos;

import java.net.URL;
import javax.sound.sampled.*;

public class ReproductorAudio {

    public static void reproducirExito() {
        if (!reproducirWav("/proyecto/audio/exito.wav")) {
            reproducirSecuenciaTonos(new double[]{523.25, 659.25}, 150);
        }
    }

    public static void reproducirAlerta() {
        if (!reproducirWav("/proyecto/audio/alerta.wav")) {
            reproducirSecuenciaTonos(new double[]{440.0, 349.23}, 180);
        }
    }

    private static boolean reproducirWav(String ruta) {
        try {
            URL url = ReproductorAudio.class.getResource(ruta);
            if (url == null) {
                return false;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void reproducirSecuenciaTonos(double[] frecuencias, int duracionMs) {
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                    line.open(format);
                    line.start();
                    for (double freq : frecuencias) {
                        int numSamples = (int) ((duracionMs / 1000.0) * sampleRate);
                        byte[] buffer = new byte[numSamples];
                        for (int i = 0; i < numSamples; i++) {
                            double angulo = 2.0 * Math.PI * i * freq / sampleRate;
                            buffer[i] = (byte) (Math.sin(angulo) * 80.0);
                        }
                        line.write(buffer, 0, buffer.length);
                    }
                    line.drain();
                }
            } catch (Exception ignored) {
            }
        }).start();
    }
}