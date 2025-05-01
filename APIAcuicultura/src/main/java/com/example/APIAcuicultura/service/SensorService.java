package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.entity.Sensor;
import com.example.APIAcuicultura.repository.SensorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorService {
    @Autowired
    private SensorRepository sensorRepository;

    public List<Sensor> findAll() {
        return sensorRepository.findAll();
    }

    public Optional<Sensor> findById(Long id) {
        return sensorRepository.findById(id);
    }

    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public void deleteById(Long id) {
        sensorRepository.deleteById(id);
    }
}
