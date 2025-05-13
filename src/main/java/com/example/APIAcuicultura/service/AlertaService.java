package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.entity.Alerta;
import com.example.APIAcuicultura.repository.AlertaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    public List<Alerta> findAll() {
        return alertaRepository.findAll();
    }

    public Optional<Alerta> findById(Long id) {
        return alertaRepository.findById(id);
    }

    public Alerta save(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    public void deleteById(Long id) {
        alertaRepository.deleteById(id);
    }

}
