
package procesos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EvaluacionEfluente {
    private double sstEntrada;
    private double sstSalida;
    private double dboEntrada;
    private double dboSalida;
    private double dqoEntrada;
    private double dqoSalida;
    private double consumoTotalEnergia;
    private double lodosTotales;

    // Límites Resolución 0631 de 2015
    public static final double LIMITE_SST = 90.0;
    public static final double LIMITE_DBO = 90.0;
    public static final double LIMITE_DQO = 180.0;

    public EvaluacionEfluente(double sstEntrada, double sstSalida, double dboEntrada, 
                              double dboSalida, double dqoEntrada, double dqoSalida, 
                              double consumoTotalEnergia, double lodosTotales) {
        this.sstEntrada = sstEntrada;
        this.sstSalida = sstSalida;
        this.dboEntrada = dboEntrada;
        this.dboSalida = dboSalida;
        this.dqoEntrada = dqoEntrada;
        this.dqoSalida = dqoSalida;
        this.consumoTotalEnergia = consumoTotalEnergia;
        this.lodosTotales = lodosTotales;

        // Si no cumple la norma, muestra automáticamente el diagnóstico del fallo y qué hacer
        if (!cumpleNormativaGlobal()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        getDiagnosticoFallo(),
                        "Alerta Normativa - Vertimiento NO Cumple (Res. 0631/2015)",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

    public double getEficienciaSST() {
        return Math.max(0, ((sstEntrada - sstSalida) / sstEntrada) * 100.0);
    }

    public double getEficienciaDBO() {
        return Math.max(0, ((dboEntrada - dboSalida) / dboEntrada) * 100.0);
    }

    public double getEficienciaDQO() {
        return Math.max(0, ((dqoEntrada - dqoSalida) / dqoEntrada) * 100.0);
    }

    public boolean cumpleNormativaGlobal() {
        double[] salidas = {sstSalida, dboSalida, dqoSalida};
        double[] limites = {LIMITE_SST, LIMITE_DBO, LIMITE_DQO};

        for (int i = 0; i < salidas.length; i++) {
            if (salidas[i] > limites[i]) {
                return false;
            }
        }
        return true;
    }

    // Identifica qué criterio falló y especifica las acciones correctivas requeridas
    public String getDiagnosticoFallo() {
        if (cumpleNormativaGlobal()) {
            return "El efluente CUMPLE con todos los criterios de la Resolución 0631 de 2015.\n"
                 + "No se requieren acciones correctivas.";
        }

        String[] parametros = {
            "SST (Sólidos Suspendidos Totales)",
            "DBO5 (Demanda Bioquímica de Oxígeno)",
            "DQO (Demanda Química de Oxígeno)"
        };
        double[] salidas = {sstSalida, dboSalida, dqoSalida};
        double[] limites = {LIMITE_SST, LIMITE_DBO, LIMITE_DQO};
        String[] acciones = {
            "Purgar lodos en el clarificador (Etapa 4) y seleccionar Sedimentador Avanzado (85%) en Etapa 2.",
            "Aumentar la aireación en el reactor biológico (Etapa 3) y verificar el nivel de oxígeno disuelto.",
            "Controlar descargas industriales en la captación (Etapa 1) y optimizar oxidación en Etapa 3."
        };

        String mensaje = "EL VERTIMIENTO NO CUMPLE CON LA RESOLUCIÓN 0631 DE 2015\n"
                       + "Criterios incumplidos y acciones que se deben realizar:\n\n";

        for (int i = 0; i < parametros.length; i++) {
            if (salidas[i] > limites[i]) {
                double exceso = salidas[i] - limites[i];
                mensaje += String.format("• FALLO EN %s:\n", parametros[i]);
                mensaje += String.format("  - Valor obtenido: %.2f mg/L (Límite: %.2f mg/L | Exceso: +%.2f mg/L)\n",
                        salidas[i], limites[i], exceso);
                mensaje += String.format("  - Qué se debe hacer: %s\n\n", acciones[i]);
            }
        }

        if (lodosTotales > 150.00) {
            mensaje += String.format("• SOBREPRODUCCIÓN DE LODOS (%.2f kg/día > 150.00 kg/día):\n"
                    + "  - Qué se debe hacer: Mantener activo el Lecho de Secado o Filtro Prensa en Etapa 4.\n",
                    lodosTotales);
        }

        return mensaje;
    }

    public boolean exportarReporteTXT(String rutaArchivo) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = LocalDateTime.now().format(formato);

        String[] parametros = {"SST ", "DBO5", "DQO "};
        double[] entradas = {sstEntrada, dboEntrada, dqoEntrada};
        double[] salidas = {sstSalida, dboSalida, dqoSalida};
        double[] limites = {LIMITE_SST, LIMITE_DBO, LIMITE_DQO};

        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("====================================================");
            writer.println("       INFORME TÉCNICO DE EVALUACIÓN AMBIENTAL      ");
            writer.println("                AquaGestion PTAR                    ");
            writer.println("====================================================");
            writer.printf("Fecha y Hora   : %s%n", fechaHora);
            writer.printf("Dictamen Global: %s%n", cumpleNormativaGlobal() ? "CUMPLE (Res. 0631/2015)" : "NO CUMPLE");
            writer.println("----------------------------------------------------");
            writer.println("TABLERO RESUMEN DE PARÁMETROS:");

            for (int i = 0; i < parametros.length; i++) {
                String estado = (salidas[i] <= limites[i]) ? "CUMPLE" : "NO CUMPLE";
                writer.printf("• %s : Entrada = %.2f mg/L | Salida = %.2f mg/L | Norma = %.2f mg/L [%s]%n",
                        parametros[i], entradas[i], salidas[i], limites[i], estado);
            }

            writer.println("----------------------------------------------------");
            writer.println("ESTADÍSTICAS GLOBALES:");
            writer.printf("• Eficiencia SST : %.2f %%%n", getEficienciaSST());
            writer.printf("• Eficiencia DBO : %.2f %%%n", getEficienciaDBO());
            writer.printf("• Eficiencia DQO : %.2f %%%n", getEficienciaDQO());
            writer.printf("• Consumo Total  : %.2f kWh/día%n", consumoTotalEnergia);
            writer.printf("• Lodos Totales  : %.2f kg/día%n", lodosTotales);
            writer.println("----------------------------------------------------");
            writer.println("DIAGNÓSTICO DE FALLOS Y ACCIONES CORRECTIVAS:");
            writer.println(getDiagnosticoFallo());
            writer.println("====================================================");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
