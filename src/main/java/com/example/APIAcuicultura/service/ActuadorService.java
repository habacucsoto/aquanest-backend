package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.entity.Actuador;
import com.example.APIAcuicultura.entity.LogActuador;
import com.example.APIAcuicultura.repository.ActuadorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActuadorService {
    @Autowired
    private LogActuadorService logActuadorService;
    
     @Autowired
    private ActuadorRepository actuadorRepository;

    public List<Actuador> findAll() {
        return actuadorRepository.findAll();
    }

    public Optional<Actuador> findById(Long id) {
        return actuadorRepository.findById(id);
    }

    public Actuador save(Actuador actuador) {
        return actuadorRepository.save(actuador);
    }

    public void deleteById(Long id) {
        actuadorRepository.deleteById(id);
    }
    
    public Actuador actualizarEstado(Long id, String nuevoEstado) {
        Actuador actuador = actuadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actuador no encontrado"));

        actuador.setEstado(nuevoEstado);
        Actuador actualizado = actuadorRepository.save(actuador);

        // Crear log
        LogActuador log = new LogActuador();
        log.setActuador(actualizado);
        log.setAccion("Cambio de estado");
        log.setResultado("Estado cambiado a " + nuevoEstado);
        logActuadorService.save(log);

        return actualizado;
    }

}
