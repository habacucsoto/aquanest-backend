package com.example.APIAcuicultura.repository;

import com.example.APIAcuicultura.entity.DatosSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatosSensorRepository extends JpaRepository<DatosSensor, Long> {
    
}
