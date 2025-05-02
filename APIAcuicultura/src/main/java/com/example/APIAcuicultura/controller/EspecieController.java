package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.dto.EspecieRequest;
import com.example.APIAcuicultura.entity.Especie;
import com.example.APIAcuicultura.security.AuthUtil;
import com.example.APIAcuicultura.service.EspecieService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/especies")
public class EspecieController {

    @Autowired
    private EspecieService especieService;

    @Autowired
    private AuthUtil authUtil;

    @GetMapping
    public ResponseEntity<List<Especie>> getEspeciesDelUsuario() {
        String email = authUtil.getCurrentUserEmail();
        return ResponseEntity.ok(especieService.findAllByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especie> getEspecieById(@PathVariable Long id) {
        try {
            String email = authUtil.getCurrentUserEmail();
            return ResponseEntity.ok(especieService.findByIdAndValidateOwnership(id, email));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Especie> crearEspecie(@RequestBody EspecieRequest request) {
        String email = authUtil.getCurrentUserEmail();
        Especie especie = especieService.crearEspecie(request, email);
        return new ResponseEntity<>(especie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Especie> actualizarEspecie(@PathVariable Long id, @RequestBody EspecieRequest request) {
        try {
            String email = authUtil.getCurrentUserEmail();
            return ResponseEntity.ok(especieService.actualizarEspecie(id, request, email));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecie(@PathVariable Long id) {
        try {
            String email = authUtil.getCurrentUserEmail();
            especieService.eliminarEspecie(id, email);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
