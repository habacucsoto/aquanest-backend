package com.example.APIAcuicultura.repository;

import com.example.APIAcuicultura.entity.LogActuador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogActuadorRepository extends JpaRepository<LogActuador, Long> {
    
}
