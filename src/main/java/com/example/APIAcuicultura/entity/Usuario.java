package com.example.APIAcuicultura.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(generator = "usuario_userid_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "usuario_userid_seq", sequenceName = "usuario_userid_seq", initialValue = 1, allocationSize = 1)
    @Column(name = "userid")
    private Long id;

    @Column(name = "name")
    private String nombre;

    @Column(unique = true)
    private String email;

    @Column(name = "passwordhash")
    private String password;

    @Column(name = "phone")
    private String telefono;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Estanque> estanques;
    
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Especie> especies;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Estanque> getEstanques() {
        return estanques;
    }

    public void setEstanques(List<Estanque> estanques) {
        this.estanques = estanques;
    }

    public List<Especie> getEspecies() {
        return especies;
    }

    public void setEspecies(List<Especie> especies) {
        this.especies = especies;
    }
}
