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
@Table(name = "alerta")
public class Alerta implements Serializable {

    @Id
    @GeneratedValue(generator = "alerta_alertid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "alerta_alertid_seq", sequenceName = "alerta_alertid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "alertid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensorid")
    private Sensor sensor;

    @Column(name = "message")
    private String mensaje;
    
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
}
