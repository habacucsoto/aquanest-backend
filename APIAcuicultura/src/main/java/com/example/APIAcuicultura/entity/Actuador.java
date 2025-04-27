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
@Table(name = "actuador")
public class Actuador implements Serializable {

    @Id
    @GeneratedValue(generator = "actuador_actuatorid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "actuador_actuatorid_seq", sequenceName = "actuador_actuatorid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "actuatorid")
    private Long id;

    @Column(name = "type")
    private String tipo;

    @Column(name = "status")
    private String estado;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Estanque getEstanque() {
        return estanque;
    }

    public void setEstanque(Estanque estanque) {
        this.estanque = estanque;
    }
    
}
