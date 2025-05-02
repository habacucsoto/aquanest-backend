package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.entity.DatosSensor;
import com.example.APIAcuicultura.service.DatosSensorService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/datos-sensor")
public class DatosSensorController {

    @Autowired
    private DatosSensorService datosSensorService;

    @GetMapping
    public ResponseEntity<List<DatosSensor>> getAllDatosSensor() {
        return new ResponseEntity<>(datosSensorService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosSensor> getDatosSensorById(@PathVariable Long id) {
        Optional<DatosSensor> datosSensor = datosSensorService.findById(id);
        return datosSensor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error message")
    public void handleError() {
    }
    
}
