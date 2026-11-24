package procesos;

public class ReactorBiologico {
    private double caudalReal;
    private double sstEntrada;
    private double dboEntrada;
    private double dqoEntrada;
    private double oxigenoDisuelto; 
    private boolean modoAutomatico;

    public ReactorBiologico(double caudalReal, double sstEntrada, double dboEntrada, double dqoEntrada, double oxigenoDisuelto, boolean modoAutomatico) {
        this.caudalReal = caudalReal;
        this.sstEntrada = sstEntrada;
        this.dboEntrada = dboEntrada;
        this.dqoEntrada = dqoEntrada;
        this.oxigenoDisuelto = oxigenoDisuelto;
        this.modoAutomatico = modoAutomatico;
    }

    public double getEficienciaDBO() {
        if (oxigenoDisuelto < 1.0) return 0.40; 
        if (oxigenoDisuelto < 2.0) return 0.75;
        return 0.90; 
    }

    public double getDboSalida() {
        return dboEntrada * (1.0 - getEficienciaDBO());
    }

    public double getDqoSalida() {
        return dqoEntrada * (1.0 - (getEficienciaDBO() * 0.85));
    }

    public double getMasaDboRemovidaKgDia() {
        double deltaDbo = dboEntrada - getDboSalida();
        return caudalReal * deltaDbo * 0.0864;
    }

    public double getConsumoEnergiaKwhDia() {
        if (oxigenoDisuelto < 1.0 || !modoAutomatico) {
            return caudalReal * 15.0; 
        }
        return getMasaDboRemovidaKgDia() * 1.6; 
    }

    public boolean tieneRiesgoAnoxia() {
        return oxigenoDisuelto < 1.0;
    }
}