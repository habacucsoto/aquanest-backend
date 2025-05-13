package com.example.APIAcuicultura.service;

import com.example.APIAcuicultura.MQTT.MqttPublisher;
import com.example.APIAcuicultura.dto.EstanqueRequest;
import com.example.APIAcuicultura.entity.Actuador;
import com.example.APIAcuicultura.entity.Especie;
import com.example.APIAcuicultura.entity.Estanque;
import com.example.APIAcuicultura.entity.Sensor;
import com.example.APIAcuicultura.entity.Usuario;
import com.example.APIAcuicultura.repository.EspecieRepository;
import com.example.APIAcuicultura.repository.EstanqueRepository;
import com.example.APIAcuicultura.repository.UsuariosRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstanqueService {
    
    private static final Logger logger = LoggerFactory.getLogger(EstanqueService.class);

    @Autowired
    private EstanqueRepository estanqueRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private EspecieRepository especieRepository;

    @Autowired
    private MqttPublisher mqttPublisher; // Inyecta el MqttPublisher

    @Autowired
    private ObjectMapper objectMapper; // Para construir el JSON


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
        Sensor sensorTemperatura = crearSensor("temperatura", 5,
                especie.getTemperaturaOptimaMin(),
                especie.getTemperaturaOptimaMax(),
                estanque);
        sensores.add(sensorTemperatura);

        Sensor sensorNitrato = crearSensor("nitrato", 5,
                especie.getNitrateOptimoMin(),
                especie.getNitrateOptimoMax(),
                estanque);
        sensores.add(sensorNitrato);

        estanque.setSensores(sensores);

        List<Actuador> actuadores = new ArrayList<>();
        Actuador calentador = crearActuador("calentador", "OFF", estanque);
        actuadores.add(calentador);
        Actuador enfriador = crearActuador("enfriador", "OFF", estanque);
        actuadores.add(enfriador);
        Actuador bombaRecirculadora = crearActuador("bomba_recirculadora", "OFF", estanque);
        actuadores.add(bombaRecirculadora);
        estanque.setActuadores(actuadores);

        Estanque estanqueGuardado = estanqueRepository.save(estanque);

        publishEstanqueInfo(estanqueGuardado);

        return estanqueGuardado;
    }

    private void publishEstanqueInfo(Estanque estanque) {
        logger.debug("Preparando publicación MQTT para estanque ID: {}", estanque.getId());
        Map<String, Object> pondInfo = new LinkedHashMap<>();
        Map<String, Object> sensoresInfo = new LinkedHashMap<>();

        for (Sensor sensor : estanque.getSensores()) {
             if (sensor == null || sensor.getId() == null) {
                 logger.warn("Se encontró un sensor nulo o sin ID en el estanque {}, se omitirá.", estanque.getId());
                 continue;
             }

            Map<String, Object> sensorData = new LinkedHashMap<>();

            if ("temperatura".equals(sensor.getTipo())) {
                logger.trace("Procesando sensor de temperatura ID: {}", sensor.getId());
                sensorData.put("temperatura_minima", sensor.getUmbralMin());
                sensorData.put("temperatura_maxima", sensor.getUmbralMax());

                Actuador enfriador = estanque.getActuadores().stream()
                        .filter(a -> a != null && "enfriador".equals(a.getTipo()))
                        .findFirst()
                        .orElse(null);
                Actuador calentador = estanque.getActuadores().stream()
                        .filter(a -> a != null && "calentador".equals(a.getTipo()))
                        .findFirst()
                        .orElse(null);

                sensorData.put("id_refrigerador", (enfriador != null && enfriador.getId() != null) ? "ENF" + enfriador.getId() : null);
                sensorData.put("id_calentador", (calentador != null && calentador.getId() != null) ? "CAL" + calentador.getId() : null);
                sensoresInfo.put("ST" + sensor.getId(), sensorData);
                 logger.trace("Datos sensor temperatura {}: {}", sensor.getId(), sensorData);

            } else if ("nitrato".equals(sensor.getTipo())) {
                logger.trace("Procesando sensor de nitrato ID: {}", sensor.getId());
                sensorData.put("nh4no3_minimo", sensor.getUmbralMin());
                sensorData.put("nh4no3_maximo", sensor.getUmbralMax());

                Actuador bomba = estanque.getActuadores().stream()
                        .filter(a -> a != null && "bomba_recirculadora".equals(a.getTipo()))
                        .findFirst()
                        .orElse(null);
                
                sensorData.put("id_bomba", (bomba != null && bomba.getId() != null) ? "BR" + bomba.getId() : null);
                sensoresInfo.put("SN" + sensor.getId(), sensorData);
                logger.trace("Datos sensor nitrato {}: {}", sensor.getId(), sensorData);
            } else {
                 logger.warn("Tipo de sensor desconocido '{}' encontrado en estanque {}, se omitirá.", sensor.getTipo(), estanque.getId());
            }
        }

        pondInfo.put("E"+ estanque.getId(), sensoresInfo);


        try {
            String payload = objectMapper.writeValueAsString(pondInfo);
            String topic = "aquanest/E" + estanque.getId() + "/info";
            mqttPublisher.publishPondInfo("E" + estanque.getId(), payload);
            logger.info("Publicado info del estanque {} al tópico MQTT '{}': {}", estanque.getId(), topic, payload);
        } catch (Exception e) {
            logger.error("Error al serializar a JSON o publicar MQTT para estanque {}: {}", estanque.getId(), e.getMessage(), e);
        }
    }

    public Estanque actualizarEstanque(Long id, EstanqueRequest request, String email) {
        Estanque estanque = findByIdAndValidateOwnership(id, email);
        estanque.setNombre(request.getNombre());
        estanque.setUbicacion(request.getUbicacion());
        estanque.setDimensiones(request.getDimensiones());
        estanque.setTipoAgua(request.getTipoAgua());
        Estanque estanqueActualizado = estanqueRepository.save(estanque);
        return estanqueActualizado;
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