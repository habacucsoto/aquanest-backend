package com.example.APIAcuicultura.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name = "datossensor")
public class DatosSensor implements Serializable {

    @Id
    @GeneratedValue(generator = "datossensor_sensordataid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "datossensor_sensordataid_seq", sequenceName = "datossensor_sensordataid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "sensordataid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensorid")
    private Sensor sensor;

    @Column(name = "value")
    private Double valor;  
    
    @Column(name = "\"timestamp\"")
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
}
