package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.entity.LogActuador;
import com.example.APIAcuicultura.repository.LogActuadorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogActuadorService {
    
    @Autowired
    private LogActuadorRepository logActuadorRepository;

    public List<LogActuador> findAll() {
        return logActuadorRepository.findAll();
    }

    public Optional<LogActuador> findById(Long id) {
        return logActuadorRepository.findById(id);
    }

    public LogActuador save(LogActuador logActuador) {
        return logActuadorRepository.save(logActuador);
    }

    public void deleteById(Long id) {
        logActuadorRepository.deleteById(id);
    }
    
}
