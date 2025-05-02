package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.dto.EstadoUpdateRequest;
import com.example.APIAcuicultura.entity.Actuador;
import com.example.APIAcuicultura.service.ActuadorService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/actuadores")
public class ActuadorController {

    @Autowired
    private ActuadorService actuadorService;

    @GetMapping
    public ResponseEntity<List<Actuador>> getAllActuadores() {
        return new ResponseEntity<>(actuadorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actuador> getActuadorById(@PathVariable Long id) {
        Optional<Actuador> actuador = actuadorService.findById(id);
        return actuador.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Actuador> actualizarEstadoActuador(
            @PathVariable Long id,
            @RequestBody EstadoUpdateRequest request) {
        Actuador actuador = actuadorService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(actuador);
    }

    
}
