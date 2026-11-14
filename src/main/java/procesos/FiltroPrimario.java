package procesos;

public class FiltroPrimario {
    private double caudalReal;
    private double sstInicial;
    private double eficiencia; // 0.70 o 0.85

    public FiltroPrimario(double caudalReal, double sstInicial, double eficiencia) {
        this.caudalReal = caudalReal;
        this.sstInicial = sstInicial;
        this.eficiencia = eficiencia;
    }

    public double getSstSalida() {
        return sstInicial * (1.0 - eficiencia);
    }

    public double getLodosRetenidosKgDia() {
        double deltaSst = sstInicial - getSstSalida();
        return caudalReal * deltaSst * 0.0864; // kg/día
    }

    // Riesgo de desbordamiento si se usa rejilla básica con SST alto o Caudal alto
    public boolean tieneRiesgoDesbordamiento() {
        return (eficiencia == 0.70) && (sstInicial > 250.0 || caudalReal > 15.0);
    }
}