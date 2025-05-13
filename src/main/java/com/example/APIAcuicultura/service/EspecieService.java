package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.dto.EspecieRequest;
import com.example.APIAcuicultura.entity.Especie;
import com.example.APIAcuicultura.entity.Usuario;
import com.example.APIAcuicultura.repository.EspecieRepository;
import com.example.APIAcuicultura.repository.UsuariosRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EspecieService {

    @Autowired
    private EspecieRepository especieRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    public List<Especie> findAllByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return especieRepository.findByUsuario(usuario);
    }

    public Especie findByIdAndValidateOwnership(Long id, String email) {
        Especie especie = especieRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Especie no encontrada"));
        if (!especie.getUsuario().getEmail().equals(email)) {
            throw new SecurityException("Acceso denegado");
        }
        return especie;
    }

    public Especie crearEspecie(EspecieRequest request, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Especie especie = new Especie();
        especie.setNombre(request.getNombre());
        especie.setTemperaturaOptimaMin(request.getTemperaturaOptimaMin());
        especie.setTemperaturaOptimaMax(request.getTemperaturaOptimaMax());
        especie.setNitrateOptimoMin(request.getNitrateOptimoMin());
        especie.setNitrateOptimoMax(request.getNitrateOptimoMax());
        especie.setUsuario(usuario);

        return especieRepository.save(especie);
    }

    public Especie actualizarEspecie(Long id, EspecieRequest request, String email) {
        Especie especie = findByIdAndValidateOwnership(id, email);
        especie.setNombre(request.getNombre());
        especie.setTemperaturaOptimaMin(request.getTemperaturaOptimaMin());
        especie.setTemperaturaOptimaMax(request.getTemperaturaOptimaMax());
        especie.setNitrateOptimoMin(request.getNitrateOptimoMin());
        especie.setNitrateOptimoMax(request.getNitrateOptimoMax());
        return especieRepository.save(especie);
    }

    public void eliminarEspecie(Long id, String email) {
        Especie especie = findByIdAndValidateOwnership(id, email);
        especieRepository.delete(especie);
    }
}
