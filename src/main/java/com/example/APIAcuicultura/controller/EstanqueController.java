package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.dto.EstanqueRequest;
import com.example.APIAcuicultura.entity.Estanque;
import com.example.APIAcuicultura.service.EstanqueService;
import com.example.APIAcuicultura.security.AuthUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/estanques")
public class EstanqueController {

    @Autowired
    private EstanqueService estanqueService;

    @Autowired
    private AuthUtil authUtil;

    @GetMapping
    public ResponseEntity<List<Estanque>> getEstanquesDelUsuario() {
        String email = authUtil.getCurrentUserEmail();
        return ResponseEntity.ok(estanqueService.findAllByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estanque> getEstanqueById(@PathVariable Long id) {
        try {
            String email = authUtil.getCurrentUserEmail();
            Estanque estanque = estanqueService.findByIdAndValidateOwnership(id, email);
            return ResponseEntity.ok(estanque);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Estanque> crearEstanque(@RequestBody EstanqueRequest request) {
        String email = authUtil.getCurrentUserEmail();
        Estanque creado = estanqueService.crearEstanqueConUsuario(request, email);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estanque> updateEstanque(@PathVariable Long id, @RequestBody EstanqueRequest request) {
        try {
            String email = authUtil.getCurrentUserEmail();
            Estanque actualizado = estanqueService.actualizarEstanque(id, request, email);
            return ResponseEntity.ok(actualizado);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstanque(@PathVariable Long id) {
        try {
            String email = authUtil.getCurrentUserEmail();
            estanqueService.eliminarEstanque(id, email);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
