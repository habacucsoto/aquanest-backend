package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.entity.DatosSensor;
import com.example.APIAcuicultura.repository.DatosSensorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatosSensorService {
    
    @Autowired
    private DatosSensorRepository datosSensorRepository;

    public List<DatosSensor> findAll() {
        return datosSensorRepository.findAll();
    }

    public Optional<DatosSensor> findById(Long id) {
        return datosSensorRepository.findById(id);
    }

    public DatosSensor save(DatosSensor datosSensor) {
        return datosSensorRepository.save(datosSensor);
    }

    public void deleteById(Long id) {
        datosSensorRepository.deleteById(id);
    }
    
}
