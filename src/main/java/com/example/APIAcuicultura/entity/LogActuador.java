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
@Table(name = "logactuador")
public class LogActuador implements Serializable {

    @Id
    @GeneratedValue(generator = "logactuador_actuatorlogid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "logactuador_actuatorlogid_seq", sequenceName = "logactuador_actuatorlogid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "actuatorlogid")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "actuatorid")
    private Actuador actuador;

    @Column(name = "action")
    private String accion;
    
    @Column(name = "\"timestamp\"")
    private LocalDateTime timestamp;
    
    @Column(name = "result")
    private String resultado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Actuador getActuador() {
        return actuador;
    }

    public void setActuador(Actuador actuador) {
        this.actuador = actuador;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
}
