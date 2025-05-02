package com.example.APIAcuicultura.controller;

import com.example.APIAcuicultura.dto.AuthRequest;
import com.example.APIAcuicultura.dto.AuthResponse;
import com.example.APIAcuicultura.dto.PasswordChangeRequest;
import com.example.APIAcuicultura.entity.Usuario;
import com.example.APIAcuicultura.repository.UsuariosRepository;
import com.example.APIAcuicultura.service.JWTService;
import com.example.APIAcuicultura.service.UsuarioService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private final UsuariosRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService, JWTService jwtService,PasswordEncoder passwordEncoder, UsuariosRepository usuarioRepository ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()));

        UserDetails userDetails = usuarioService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario savedUser = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest changePasswordRequest, HttpServletRequest request) {
        if (changePasswordRequest == null
                || changePasswordRequest.getOldPassword() == null || changePasswordRequest.getOldPassword().trim().isEmpty()
                || changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().trim().isEmpty()
                || changePasswordRequest.getConfirmPassword() == null || changePasswordRequest.getConfirmPassword().trim().isEmpty()) {
            return new ResponseEntity<>("Todos los campos son requeridos", HttpStatus.BAD_REQUEST);
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return new ResponseEntity<>("La nueva contraseña y la confirmación no coinciden", HttpStatus.BAD_REQUEST);
        }

        return usuarioService.getCurrentLoggedInUser()
                .map(currentUser -> {
                    if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), currentUser.getPassword())) {
                        currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                        usuarioService.save(currentUser);
                        return new ResponseEntity<>("Contraseña actualizada exitosamente", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("La contraseña actual es incorrecta", HttpStatus.UNAUTHORIZED);
                    }
                })
                .orElseGet(() -> new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED));
    }
}
