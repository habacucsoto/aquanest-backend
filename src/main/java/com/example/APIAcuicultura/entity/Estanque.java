package com.example.APIAcuicultura.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "estanque")
public class Estanque implements Serializable {

    @Id
    @GeneratedValue(generator = "estanque_pondid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "estanque_pondid_seq", sequenceName = "estanque_pondid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "pondid")
    private Long id;

    @Column(name = "name")
    private String nombre;

    @Column(name = "location")
    private String ubicacion;

    @Column(name = "dimensions")
    private String dimensiones;

    @Column(name = "watertype")
    private String tipoAgua;
    
    @ManyToOne
    @JoinColumn(name = "speciesid")
    private Especie especie;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userid")
    private Usuario usuario;

    @JsonManagedReference
    @OneToMany(mappedBy = "estanque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensores;

    @JsonManagedReference
    @OneToMany(mappedBy = "estanque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Actuador> actuadores;

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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getTipoAgua() {
        return tipoAgua;
    }

    public void setTipoAgua(String tipoAgua) {
        this.tipoAgua = tipoAgua;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public void setSensores(List<Sensor> sensores) {
        this.sensores = sensores;
    }

    public List<Actuador> getActuadores() {
        return actuadores;
    }

    public void setActuadores(List<Actuador> actuadores) {
        this.actuadores = actuadores;
    }
    
    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
    
}