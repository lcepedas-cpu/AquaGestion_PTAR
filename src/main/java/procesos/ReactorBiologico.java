package procesos;

public class ReactorBiologico {
    private double caudalReal;
    private double sstEntrada;
    private double dboEntrada;
    private double dqoEntrada;
    private double oxigenoDisuelto; // OD en mg/L
    private boolean modoAutomatico;

    public ReactorBiologico(double caudalReal, double sstEntrada, double dboEntrada, double dqoEntrada, double oxigenoDisuelto, boolean modoAutomatico) {
        this.caudalReal = caudalReal;
        this.sstEntrada = sstEntrada;
        this.dboEntrada = dboEntrada;
        this.dqoEntrada = dqoEntrada;
        this.oxigenoDisuelto = oxigenoDisuelto;
        this.modoAutomatico = modoAutomatico;
    }

    // Eficiencia según la disponibilidad de oxígeno
    public double getEficienciaDBO() {
        if (oxigenoDisuelto < 1.0) return 0.40; // Anoxia / Muerte bacteriana
        if (oxigenoDisuelto < 2.0) return 0.75;
        return 0.90; // Rango óptimo (>= 2.0 mg/L)
    }

    public double getDboSalida() {
        return dboEntrada * (1.0 - getEficienciaDBO());
    }

    public double getDqoSalida() {
        return dqoEntrada * (1.0 - (getEficienciaDBO() * 0.85));
    }

    // Masa de DBO removida en kg/día: Q (L/s) * Delta_DBO (mg/L) * 0.0864
    public double getMasaDboRemovidaKgDia() {
        double deltaDbo = dboEntrada - getDboSalida();
        return caudalReal * deltaDbo * 0.0864;
    }

    // Estimación de consumo energético en kWh/día
    public double getConsumoEnergiaKwhDia() {
        if (oxigenoDisuelto < 1.0 || !modoAutomatico) {
            return caudalReal * 15.0; // Sopladores al 100% (potencia máxima continua)
        }
        return getMasaDboRemovidaKgDia() * 1.6; // Consumo optimizado dinámico
    }

    // Alerta operativa por baja concentración de Oxígeno Disuelto
    public boolean tieneRiesgoAnoxia() {
        return oxigenoDisuelto < 1.0;
    }
}