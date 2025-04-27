package com.example.APIAcuicultura.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "especie")
public class Especie implements Serializable {

    @Id
    @GeneratedValue(generator = "especie_speciesid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "especie_speciesid_seq", sequenceName = "especie_speciesid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "speciesid")
    private Long id;

    @Column(name = "name")
    private String nombre;

    @Column(name = "optimaltempmin")
    private Double temperaturaOptimaMin;

    @Column(name = "optimaltempmax")
    private Double temperaturaOptimaMax;

    @Column(name = "optimalnitratemin")
    private Double nitrateOptimoMin;

    @Column(name = "optimalnitratemax")
    private Double nitrateOptimoMax;    
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userid")
    private Usuario usuario;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


}
