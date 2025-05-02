package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.dto.UpdateNameRequest;
import com.example.APIAcuicultura.entity.Usuario;
import com.example.APIAcuicultura.service.UsuarioService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/me")
    public ResponseEntity<Usuario> getCurrentUser() {
        Optional<Usuario> currentUser = usuarioService.getCurrentLoggedInUser();
        return currentUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return new ResponseEntity<>(usuarioService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (usuarioService.findById(id).isPresent()) {
            usuario.setId(id);
            return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PatchMapping("/me/name")
    public ResponseEntity<Usuario> updateCurrentUserName(@RequestBody UpdateNameRequest updateNameRequest, HttpServletRequest request) {
        if (updateNameRequest == null || updateNameRequest.getNombre() == null || updateNameRequest.getNombre().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna ResponseEntity<Usuario> con solo el status
        }

        if (updateNameRequest.getNombre().trim().length() > 100) { // Ejemplo de límite de longitud
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna ResponseEntity<Usuario> con solo el status
        }

        return usuarioService.getCurrentLoggedInUser()
                .map(currentUser -> {
                    currentUser.setNombre(updateNameRequest.getNombre());
                    Usuario updatedUser = usuarioService.save(currentUser);
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}
