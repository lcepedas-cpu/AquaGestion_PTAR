package procesos;

public class TratamientoFinal {
    private double caudalReal;
    private double sstSalidaV2;
    private double dboSalidaV3;
    private double lodosPrimarios;
    private boolean esCloracion; // true: Cloración, false: UV
    private String sistemaLodos = "Ninguno";

    public TratamientoFinal(double caudalReal, double sstSalidaV2, double dboSalidaV3, double lodosPrimarios, boolean esCloracion) {
        this.caudalReal = caudalReal;
        this.sstSalidaV2 = sstSalidaV2;
        this.dboSalidaV3 = dboSalidaV3;
        this.lodosPrimarios = lodosPrimarios;
        this.esCloracion = esCloracion;
    }

    public void setSistemaLodos(String sistemaLodos) {
        this.sistemaLodos = (sistemaLodos != null) ? sistemaLodos : "Ninguno";
    }

    public String getSistemaLodos() {
        return sistemaLodos;
    }

    // El clarificador secundario remueve un 85% de los sólidos suspendidos de V2
    public double getSstFinal() {
        return sstSalidaV2 * 0.15;
    }

    // Lodos biológicos retenidos en el clarificador (kg/día)
    public double getLodosBiologicosKgDia() {
        double deltaSst = sstSalidaV2 - getSstFinal();
        return caudalReal * deltaSst * 0.0864;
    }

    // Lodos totales generados antes de deshidratación (kg/día)
    public double getLodosBrutosKgDia() {
        return lodosPrimarios + getLodosBiologicosKgDia();
    }

    // Balance final de lodos a disponer tras aplicar Lecho de Secado o Filtro Prensa
    public double getLodosTotalesKgDia() {
        double brutos = getLodosBrutosKgDia();
        switch (sistemaLodos) {
            case "Lecho de Secado":
                return brutos * 0.60; // Reduce 40% la masa húmeda
            case "Filtro Prensa":
                return brutos * 0.35; // Reduce 65% por compactación mecánica
            default:
                return brutos;
        }
    }

    public double getEficaciaDesinfeccion() {
        return esCloracion ? 80.0 : 99.0;
    }

    // Consumo energético de desinfección + deshidratación mecánica
    public double getConsumoEnergiaKwh() {
        double energiaDesinfeccion = esCloracion ? 5.0 : 35.0;
        double energiaDeshidratacion = sistemaLodos.equals("Filtro Prensa") ? 25.0 : 0.0;
        return energiaDesinfeccion + energiaDeshidratacion;
    }

    // Alerta de saturación si los lodos sin tratar superan los 150 kg/día
    public boolean tieneSaturacionLodos() {
        return getLodosBrutosKgDia() > 150.0;
    }

    // Verifica si hay sobrecarga de lodos y aún no se ha activado ningún sistema
    public boolean requiereAccionLodos() {
        return tieneSaturacionLodos() && sistemaLodos.equals("Ninguno");
    }
}