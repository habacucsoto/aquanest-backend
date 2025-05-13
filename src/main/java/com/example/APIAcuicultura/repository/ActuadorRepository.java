package com.example.APIAcuicultura.repository;

import com.example.APIAcuicultura.entity.Actuador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActuadorRepository extends JpaRepository<Actuador, Long> {
    
}
