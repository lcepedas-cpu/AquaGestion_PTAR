package com.aquagestion.model;

public class TratamientoFinal {
    private double caudalReal;
    private double sstSalidaV2;
    private double dboSalidaV3;
    private double lodosPrimarios;
    private boolean esCloracion; // true: Cloración, false: UV

    public TratamientoFinal(double caudalReal, double sstSalidaV2, double dboSalidaV3, double lodosPrimarios, boolean esCloracion) {
        this.caudalReal = caudalReal;
        this.sstSalidaV2 = sstSalidaV2;
        this.dboSalidaV3 = dboSalidaV3;
        this.lodosPrimarios = lodosPrimarios;
        this.esCloracion = esCloracion;
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

    // Balance total de lodos acumulados en la planta (kg/día)
    public double getLodosTotalesKgDia() {
        return lodosPrimarios + getLodosBiologicosKgDia();
    }

    public double getEficaciaDesinfeccion() {
        return esCloracion ? 80.0 : 99.0;
    }

    public double getConsumoEnergiaKwh() {
        return esCloracion ? 5.0 : 35.0;
    }

    // Alerta de saturación si los lodos totales superan los 150 kg/día
    public boolean tieneSaturacionLodos() {
        return getLodosTotalesKgDia() > 150.0;
    }
}