package procesos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
        return sstSalida <= LIMITE_SST && dboSalida <= LIMITE_DBO && dqoSalida <= LIMITE_DQO;
    }

    // Exportación del reporte a archivo físico .TXT
    public boolean exportarReporteTXT(String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("====================================================");
            writer.println("       INFORME TÉCNICO DE EVALUACIÓN AMBIENTAL      ");
            writer.println("                AquaGestion PTAR                    ");
            writer.println("====================================================");
            writer.printf("Dictamen Global: %s%n", cumpleNormativaGlobal() ? "CUMPLE (Res. 0631/2015)" : "NO CUMPLE");
            writer.println("----------------------------------------------------");
            writer.println("TABLERO RESUMEN DE PARÁMETROS:");
            writer.printf("• SST  : Entrada = %.1f mg/L | Salida = %.1f mg/L | Norma = %.0f mg/L%n", sstEntrada, sstSalida, LIMITE_SST);
            writer.printf("• DBO5 : Entrada = %.1f mg/L | Salida = %.1f mg/L | Norma = %.0f mg/L%n", dboEntrada, dboSalida, LIMITE_DBO);
            writer.printf("• DQO  : Entrada = %.1f mg/L | Salida = %.1f mg/L | Norma = %.0f mg/L%n", dqoEntrada, dqoSalida, LIMITE_DQO);
            writer.println("----------------------------------------------------");
            writer.println("ESTADÍSTICAS GLOBALES:");
            writer.printf("• Eficiencia DBO : %.2f %%%n", getEficienciaDBO());
            writer.printf("• Eficiencia SST : %.2f %%%n", getEficienciaSST());
            writer.printf("• Consumo Total  : %.1f kWh/día%n", consumoTotalEnergia);
            writer.printf("• Lodos Totales  : %.1f kg/día%n", lodosTotales);
            writer.println("====================================================");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}