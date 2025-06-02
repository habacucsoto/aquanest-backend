package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.entity.LogActuador;
import com.example.APIAcuicultura.service.LogActuadorService;
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

@RestController
@RequestMapping("/log")
public class LogActuadorController {

    @Autowired
    private LogActuadorService logActuadorService;

    @GetMapping
    public ResponseEntity<List<LogActuador>> getAllLogActuadores() {
        return new ResponseEntity<>(logActuadorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogActuador> getLogActuadorById(@PathVariable Long id) {
        Optional<LogActuador> logActuador = logActuadorService.findById(id);
        return logActuador.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<LogActuador> createLogActuador(@RequestBody LogActuador logActuador) {
        return new ResponseEntity<>(logActuadorService.save(logActuador), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogActuador> updateLogActuador(@PathVariable Long id, @RequestBody LogActuador logActuador) {
        if (logActuadorService.findById(id).isPresent()) {
            logActuador.setId(id);
            return new ResponseEntity<>(logActuadorService.save(logActuador), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogActuador(@PathVariable Long id) {
        if (logActuadorService.findById(id).isPresent()) {
            logActuadorService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
