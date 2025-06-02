package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.entity.DatosSensor;
import com.example.APIAcuicultura.repository.DatosSensorRepository;
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

@RestController
@RequestMapping("/datos-sensor")
public class DatosSensorController {

    @Autowired
    private DatosSensorRepository datosSensorRepository;
    
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
    
        // Nuevo endpoint para obtener datos de sensores para un estanque específico
    @GetMapping("/estanque/{pondId}/history")
    public ResponseEntity<List<DatosSensor>> getHistoricalDataByPondId(@PathVariable Long pondId) {
        List<DatosSensor> datos = datosSensorRepository.findBySensorEstanqueIdOrderByTimestampAsc(pondId);
        if (datos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 si no hay datos
        }
        return ResponseEntity.ok(datos);
    }
}
