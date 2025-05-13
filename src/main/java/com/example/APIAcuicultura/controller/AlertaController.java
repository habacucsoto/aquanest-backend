package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.entity.Alerta;
import com.example.APIAcuicultura.service.AlertaService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/alerta")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<Alerta>> getAllAlertas() {
        return new ResponseEntity<>(alertaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerta> getAlertaById(@PathVariable Long id) {
        Optional<Alerta> alerta = alertaService.findById(id);
        return alerta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Alerta> createAlerta(@RequestBody Alerta alerta) {
        return new ResponseEntity<>(alertaService.save(alerta), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alerta> updateAlerta(@PathVariable Long id, @RequestBody Alerta alerta) {
        if (alertaService.findById(id).isPresent()) {
            alerta.setId(id);
            return new ResponseEntity<>(alertaService.save(alerta), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlerta(@PathVariable Long id) {
        if (alertaService.findById(id).isPresent()) {
            alertaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error message")
    public void handleError() {
    }
    
}
