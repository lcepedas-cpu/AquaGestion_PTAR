package procesos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        double[] salidas = {sstSalida, dboSalida, dqoSalida};
        double[] limites = {LIMITE_SST, LIMITE_DBO, LIMITE_DQO};

        for (int i = 0; i < salidas.length; i++) {
            if (salidas[i] > limites[i]) {
                return false;
            }
        }
        return true;
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
            writer.println("====================================================");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}