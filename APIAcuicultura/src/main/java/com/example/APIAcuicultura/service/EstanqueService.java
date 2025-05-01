package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.dto.EstanqueRequest;
import com.example.APIAcuicultura.entity.Actuador;
import com.example.APIAcuicultura.entity.Especie;
import com.example.APIAcuicultura.entity.Estanque;
import com.example.APIAcuicultura.entity.Sensor;
import com.example.APIAcuicultura.entity.Usuario;
import com.example.APIAcuicultura.repository.EspecieRepository;
import com.example.APIAcuicultura.repository.EstanqueRepository;
import com.example.APIAcuicultura.repository.UsuariosRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstanqueService {

    @Autowired
    private EstanqueRepository estanqueRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private EspecieRepository especieRepository;
        
    public List<Estanque> findAllByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return estanqueRepository.findByUsuario(usuario);
    }

    public Estanque findByIdAndValidateOwnership(Long id, String email) {
        Estanque estanque = estanqueRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Estanque no encontrado"));
        if (!estanque.getUsuario().getEmail().equals(email)) {
            throw new SecurityException("Acceso denegado");
        }
        return estanque;
    }

    public Estanque crearEstanqueConUsuario(EstanqueRequest request, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Especie especie = especieRepository.findById(request.getEspecieId())
                .orElseThrow(() -> new RuntimeException("Especie no encontrada"));

        Estanque estanque = new Estanque();
        estanque.setNombre(request.getNombre());
        estanque.setUbicacion(request.getUbicacion());
        estanque.setDimensiones(request.getDimensiones());
        estanque.setTipoAgua(request.getTipoAgua());
        estanque.setUsuario(usuario);
        estanque.setEspecie(especie);

        List<Sensor> sensores = new ArrayList<>();
        sensores.add(crearSensor("temperatura", 5,
                especie.getTemperaturaOptimaMin(),
                especie.getTemperaturaOptimaMax(),
                estanque));

        sensores.add(crearSensor("nitrato", 5,
                especie.getNitrateOptimoMin(),
                especie.getNitrateOptimoMax(),
                estanque));

        estanque.setSensores(sensores);

        // Actuadores (igual que antes)
        List<Actuador> actuadores = new ArrayList<>();
        actuadores.add(crearActuador("calentador", "OFF", estanque));
        actuadores.add(crearActuador("enfriador", "OFF", estanque));
        actuadores.add(crearActuador("bomba_recirculadora", "OFF", estanque));
        estanque.setActuadores(actuadores);

        return estanqueRepository.save(estanque);
    }

    public Estanque actualizarEstanque(Long id, EstanqueRequest request, String email) {
        Estanque estanque = findByIdAndValidateOwnership(id, email);
        estanque.setNombre(request.getNombre());
        estanque.setUbicacion(request.getUbicacion());
        estanque.setDimensiones(request.getDimensiones());
        estanque.setTipoAgua(request.getTipoAgua());
        return estanqueRepository.save(estanque);
    }

    public void eliminarEstanque(Long id, String email) {
        Estanque estanque = findByIdAndValidateOwnership(id, email);
        estanqueRepository.delete(estanque);
    }

    private Sensor crearSensor(String tipo, int intervalo, double min, double max, Estanque estanque) {
        Sensor sensor = new Sensor();
        sensor.setTipo(tipo);
        sensor.setIntervaloMedicion(intervalo);
        sensor.setUmbralMin(min);
        sensor.setUmbralMax(max);
        sensor.setEstanque(estanque);
        return sensor;
    }

    private Actuador crearActuador(String tipo, String estado, Estanque estanque) {
        Actuador actuador = new Actuador();
        actuador.setTipo(tipo);
        actuador.setEstado(estado);
        actuador.setEstanque(estanque);
        return actuador;
    }
}
