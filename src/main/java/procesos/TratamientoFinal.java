package procesos;

public class TratamientoFinal {
    private double caudalReal;
    private double sstSalidaV2;
    private double dboSalidaV3;
    private double lodosPrimarios;
    private boolean esCloracion; 
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

    public double getSstFinal() {
        return sstSalidaV2 * 0.15;
    }

    public double getLodosBiologicosKgDia() {
        double deltaSst = sstSalidaV2 - getSstFinal();
        return caudalReal * deltaSst * 0.0864;
    }

    public double getLodosBrutosKgDia() {
        return lodosPrimarios + getLodosBiologicosKgDia();
    }

    public double getLodosTotalesKgDia() {
        double brutos = getLodosBrutosKgDia();
        switch (sistemaLodos) {
            case "Lecho de Secado":
                return brutos * 0.60; 
            case "Filtro Prensa":
                return brutos * 0.35; 
            default:
                return brutos;
        }
    }

    public double getEficaciaDesinfeccion() {
        return esCloracion ? 80.0 : 99.0;
    }

    public double getConsumoEnergiaKwh() {
        double energiaDesinfeccion = esCloracion ? 5.0 : 35.0;
        double energiaDeshidratacion = sistemaLodos.equals("Filtro Prensa") ? 25.0 : 0.0;
        return energiaDesinfeccion + energiaDeshidratacion;
    }

    public boolean tieneSaturacionLodos() {
        return getLodosBrutosKgDia() > 150.0;
    }

    public boolean requiereAccionLodos() {
        return tieneSaturacionLodos() && sistemaLodos.equals("Ninguno");
    }
}