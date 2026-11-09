package com.aquagestion.model;

public class MuestraAgua {
    private double caudalBase;
    private String tipoFuente;       // "Doméstica", "Industrial", "Hospitalaria"
    private String estadoOperativo;  // "Normal", "Sobrecarga por Lluvia", "Falla Operativa"

    private double sstBase;
    private double dboBase;
    private double dqoBase;
    private double factorCaudal;
    private double factorCarga;

    public MuestraAgua(double caudalBase, String tipoFuente, String estadoOperativo) {
        this.caudalBase = caudalBase;
        this.tipoFuente = tipoFuente;
        this.estadoOperativo = estadoOperativo;
        calcularVariablesBase();
    }

    private void calcularVariablesBase() {
        // Asignación de concentraciones iniciales según el origen
        switch (tipoFuente) {
            case "Industrial":
                this.sstBase = 600.0;
                this.dboBase = 800.0;
                this.dqoBase = 1500.0;
                break;
            case "Hospitalaria":
                this.sstBase = 350.0;
                this.dboBase = 450.0;
                this.dqoBase = 900.0;
                break;
            case "Doméstica":
            default:
                this.sstBase = 220.0;
                this.dboBase = 200.0;
                this.dqoBase = 400.0;
                break;
        }

        // Multiplicadores según el estado de la planta
        switch (estadoOperativo) {
            case "Sobrecarga por Lluvia":
                this.factorCaudal = 2.5;
                this.factorCarga = 0.7;
                break;
            case "Falla Operativa":
                this.factorCaudal = 1.0;
                this.factorCarga = 1.8;
                break;
            case "Normal":
            default:
                this.factorCaudal = 1.0;
                this.factorCarga = 1.0;
                break;
        }
    }

    // Métodos que entregan los datos ajustados hacia la Ventana 2
    public double getCaudalReal() { return caudalBase * factorCaudal; }
    public double getSstInicial() { return sstBase * factorCarga; }
    public double getDboInicial() { return dboBase * factorCarga; }
    public double getDqoInicial() { return dqoBase * factorCarga; }

    public double getSstBase() { return sstBase; }
    public double getDboBase() { return dboBase; }
    public double getDqoBase() { return dqoBase; }
}