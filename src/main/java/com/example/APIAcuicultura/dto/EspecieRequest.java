package com.example.APIAcuicultura.dto;

public class EspecieRequest {
    private String nombre;
    private Double temperaturaOptimaMin;
    private Double temperaturaOptimaMax;
    private Double nitrateOptimoMin;
    private Double nitrateOptimoMax;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getTemperaturaOptimaMin() {
        return temperaturaOptimaMin;
    }

    public void setTemperaturaOptimaMin(Double temperaturaOptimaMin) {
        this.temperaturaOptimaMin = temperaturaOptimaMin;
    }

    public Double getTemperaturaOptimaMax() {
        return temperaturaOptimaMax;
    }

    public void setTemperaturaOptimaMax(Double temperaturaOptimaMax) {
        this.temperaturaOptimaMax = temperaturaOptimaMax;
    }

    public Double getNitrateOptimoMin() {
        return nitrateOptimoMin;
    }

    public void setNitrateOptimoMin(Double nitrateOptimoMin) {
        this.nitrateOptimoMin = nitrateOptimoMin;
    }

    public Double getNitrateOptimoMax() {
        return nitrateOptimoMax;
    }

    public void setNitrateOptimoMax(Double nitrateOptimoMax) {
        this.nitrateOptimoMax = nitrateOptimoMax;
    }
    
    
}
