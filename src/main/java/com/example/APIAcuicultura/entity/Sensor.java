package com.example.APIAcuicultura.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "sensor")
public class Sensor implements Serializable {

    @Id
    @GeneratedValue(generator = "sensores_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sensores_id_seq", sequenceName = "sensor_sensorid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "sensorid")
    private Long id;

    @Column(name = "type")
    private String tipo;

    @Column(name = "measurementinterval")
    private Integer intervaloMedicion;

    @Column(name = "minthreshold")
    private Double umbralMin;

    @Column(name = "maxthreshold")
    private Double umbralMax;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pondid")
    private Estanque estanque;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getIntervaloMedicion() {
        return intervaloMedicion;
    }

    public void setIntervaloMedicion(Integer intervaloMedicion) {
        this.intervaloMedicion = intervaloMedicion;
    }

    public Double getUmbralMin() {
        return umbralMin;
    }

    public void setUmbralMin(Double umbralMin) {
        this.umbralMin = umbralMin;
    }

    public Double getUmbralMax() {
        return umbralMax;
    }

    public void setUmbralMax(Double umbralMax) {
        this.umbralMax = umbralMax;
    }

    public Estanque getEstanque() {
        return estanque;
    }

    public void setEstanque(Estanque estanque) {
        this.estanque = estanque;
    }
    
}
