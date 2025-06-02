package com.example.APIAcuicultura.MQTT;
import com.example.APIAcuicultura.entity.Actuador;
import com.example.APIAcuicultura.entity.Alerta;
import com.example.APIAcuicultura.entity.DatosSensor;
import com.example.APIAcuicultura.entity.LogActuador;
import com.example.APIAcuicultura.entity.Sensor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import com.example.APIAcuicultura.service.DatosSensorService;
import com.example.APIAcuicultura.service.ActuadorService;
import com.example.APIAcuicultura.service.AlertaService;
import com.example.APIAcuicultura.service.LogActuadorService;
import com.example.APIAcuicultura.service.SensorService;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class MQTTConfig {

    private static final Logger logger = LoggerFactory.getLogger(MQTTConfig.class);

    @Value("${mqtt.broker}")
    private String mqttBroker;

    @Value("${mqtt.client.id.api}")
    private String apiClientId;

    @Value("${mqtt.username:#{null}}")
    private String mqttUsername;

    @Value("${mqtt.password:#{null}}")
    private String mqttPassword;

    @Autowired
    private DatosSensorService sensorDataService;
    @Autowired
    private ActuadorService actuadorService;
    @Autowired
    private AlertaService alertService;
    @Autowired
    private SensorService sensorService;
    @Autowired
    private LogActuadorService logActuadorService;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttBroker});

        if (StringUtils.hasText(mqttUsername)) {
            options.setUserName(mqttUsername);
        }
        if (StringUtils.hasText(mqttPassword)) {
            options.setPassword(mqttPassword.toCharArray());
        }

        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);

        factory.setConnectionOptions(options);
        logger.info("Configurando MqttPahoClientFactory para broker: {} con ClientID: {}", mqttBroker, apiClientId);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory clientFactory) {
        String inboundClientId = apiClientId + "-inbound";
        logger.info("Creando MqttPahoMessageDrivenChannelAdapter con ClientID: {}", inboundClientId);

        String[] topics = {
            "aquanest/+/+/data/#",
            "aquanest/+/+/alert/anomalous",
            "aquanest/+/+/response",
            "aquanest/+/+/heartbeat/error",
            "aquanest/+/delete"
        };

        int[] qos = new int[topics.length];
        Arrays.fill(qos, 1);

        MqttPahoMessageDrivenChannelAdapter adapter
                = new MqttPahoMessageDrivenChannelAdapter(inboundClientId, clientFactory, topics);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel());
        adapter.setApplicationEventPublisher(event -> {
        });
        return adapter;
    }

@Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler messageHandler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            if (topic == null) {
                logger.warn("Mensaje MQTT recibido sin tópico.");
                return;
            }

            String payloadString = "";
            try {
                if (message.getPayload() instanceof byte[]) {
                    payloadString = new String((byte[]) message.getPayload());
                } else if (message.getPayload() instanceof String) {
                    payloadString = (String) message.getPayload();
                } else {
                    logger.warn("Payload recibido no es byte[] ni String para tópico '{}'. Intentando toString(). Class: {}", topic, message.getPayload().getClass().getName());
                    payloadString = message.getPayload().toString();
                }
            } catch (Exception e) {
                logger.error("Error al convertir el payload del tópico '{}' a String: {}", topic, e.getMessage());
                return;
            }
            try {
                String[] topicParts = topic.split("/");
                if (topicParts.length < 2 || !topicParts[0].equals("aquanest")) {
                    logger.warn("Tópico con formato inesperado (no empieza con 'aquanest'): {}", topic);
                    return;
                }

                // El ID del estanque siempre viene con el prefijo 'E' según publishEstanqueInfo
                String rawPondId = topicParts[1];
                Optional<Long> pondIdOpt = extractNumericId(rawPondId);

                if (!pondIdOpt.isPresent()) {
                    logger.warn("ID de estanque en tópico '{}' no tiene un formato prefijado válido: {}", topic, rawPondId);
                    return; // No podemos procesar sin un ID de estanque válido
                }
                Long pondId = pondIdOpt.get();
                
                if (topicParts.length == 5 && topicParts[3].equals("data")) {
                    String rawDeviceId = topicParts[2];
                    if (rawDeviceId.startsWith("ST") || rawDeviceId.startsWith("SN")) {
                        handleSensorDataMessage(pondId, rawDeviceId, payloadString); // Llama al handler de datos
                    } else {
                    }
                }
                // Tópico de Response de Actuador: aquanest/E{id_pond}/{Prefijo}{id_actuador}/response (length 4)
                else if (topicParts.length == 4 && topicParts[3].equals("response")) {
                    String rawDeviceId = topicParts[2]; // Actuador prefijado en índice 2
                    if (rawDeviceId.startsWith("ENF") || rawDeviceId.startsWith("CAL") || rawDeviceId.startsWith("BR")) {
                         logger.info("Detectado tópico de estado de actuador: {}", topic);
                         handleActuatorStatusMessage(pondId, rawDeviceId, payloadString);
                    } else {
                         logger.warn("Mensaje en tópico de estado '{}' para estanque {} con ID de dispositivo no de actuador '{}'.", topic, pondId, rawDeviceId);
                    }
                 }
            
                // Tópico de Alerta Anómala: aquanest/E{id_pond}/{Prefijo}{id_sensor}/alert/anomalous (length 5)
                 else if (topicParts.length == 5 && topicParts[3].equals("alert") && topicParts[4].equals("anomalous")) {
                    String rawDeviceId = topicParts[2]; // Dispositivo prefijado en índice 2
                    if (rawDeviceId.startsWith("ST") || rawDeviceId.startsWith("SN")) {
                        logger.info("Detectado tópico de alerta de sensor: {}", topic);
                        // Pasa el rawDeviceId (ej. ST53) al handler
                        handleSensorAlertMessage(pondId, rawDeviceId, payloadString);
                    } else {
                        logger.warn("Mensaje en tópico de alerta anómala '{}' para estanque {} con ID de dispositivo no de sensor '{}'.", topic, pondId, rawDeviceId);
                    }
                }
                
                // Tópico de Error Heartbeat: aquanest/E{id_pond}/{Prefijo}{id_device}/heartbeat/error (length 5)
                 else if (topicParts.length == 5 && topicParts[3].equals("heartbeat") && topicParts[4].equals("error")) {
                    String rawDeviceId = topicParts[2]; // Dispositivo prefijado en índice 2
                    String deviceType = "desconocido";
                    if (rawDeviceId.startsWith("ST") || rawDeviceId.startsWith("SN")) {
                        deviceType = "sensor";
                    } else if (rawDeviceId.startsWith("ENF") || rawDeviceId.startsWith("CAL") || rawDeviceId.startsWith("BR")) {
                        deviceType = "actuador";
                    }
                    logger.info("Detectado tópico de error heartbeat: {}", topic);
                    // Pasa el deviceType detectado y el rawDeviceId al handler
                    handleHeartbeatErrorMessage(pondId, deviceType, rawDeviceId, payloadString);
                }
                 
                // Tópico de Eliminación de Estanque: aquanest/E{id_pond}/delete (length 3)
                else if (topicParts.length == 3 && topicParts[2].equals("delete")) {
                    logger.info("Detectado tópico de eliminación de estanque: {}", topic);
                    handlePondDeleteMessage(pondId, payloadString);
                } 
                                
                else {
                    // Este else solo se alcanza si el tópico NO coincide con NINGUNO de los patrones específicos ANTERIORES
                    logger.warn("Tópico MQTT recibido no coincide con un formato esperado: {}", topic);
                }

            } catch (Exception e) {
                logger.error("Error procesando mensaje MQTT del tópico '{}': {}", topic, e.getMessage(), e);
            }
        };
    }

    private Optional<Long> extractNumericId(String prefixedId) {
        if (prefixedId == null || prefixedId.isEmpty()) {
            return Optional.empty();
        }

        String numericPart = prefixedId;
        // Prefijos ordenados de mayor a menor longitud
        String[] prefixes = {"ENF", "CAL", "BR", "ST", "SN", "E"}; // "ENF", "CAL", "BR", "ST", "SN" van antes que "E"

        for (String prefix : prefixes) {
            if (prefixedId.startsWith(prefix)) {
                if (prefixedId.length() > prefix.length()) {
                    numericPart = prefixedId.substring(prefix.length());
                    break; 
                } else {
                    // La cadena es solo el prefijo sin número (ej. "E", "ST") - inválido
                    logger.warn("ID prefijado es solo el prefijo sin número: {}", prefixedId);
                    return Optional.empty();
                }
            }
        }
        
        if (numericPart.equals(prefixedId)) {
             logger.warn("ID '{}' no empieza con un prefijo válido conocido.", prefixedId);
             return Optional.empty();
        }


        try {
            // Intentar convertir la parte numérica a Long
            return Optional.of(Long.valueOf(numericPart));
        } catch (NumberFormatException e) {
            logger.warn("La parte numérica '{}' del ID '{}' (después del prefijo) no es un número válido.", numericPart, prefixedId);
            return Optional.empty(); // No es un número válido
        }
    }

        private void handleActuatorStatusMessage(Long pondId, String rawActuatorId, String payload) {
        logger.debug("Manejando mensaje de estado de actuador: Estanque {} - Actuador raw '{}' - Payload: '{}'", pondId, rawActuatorId, payload);
        Optional<Long> actuatorDbIdOpt = extractNumericId(rawActuatorId);
        if (!actuatorDbIdOpt.isPresent()) {
            logger.warn("ID de actuador raw '{}' del estanque {} no tiene un formato prefijado válido.", rawActuatorId, pondId);
            return;
        }

        Long actuatorDbId = actuatorDbIdOpt.get();
        try {
            String nuevoEstado = (payload != null && !payload.trim().isEmpty())
                    ? payload.trim().toUpperCase()
                    : "UNKNOWN";

            logger.info("Recibido estado '{}' para Actuador ID DB {} (Raw: {}) en Estanque {}", nuevoEstado, actuatorDbId, rawActuatorId, pondId);

            actuadorService.actualizarEstado(actuatorDbId, nuevoEstado);

            Optional<Actuador> actuatorOpt = actuadorService.findById(actuatorDbId);

            if (actuatorOpt.isPresent()) {
                Actuador actuador = actuatorOpt.get();

                LogActuador log = new LogActuador();
                log.setActuador(actuador); // Enlaza con la entidad Actuador
                log.setAccion("Status Update"); // Describe la acción
                log.setResultado(nuevoEstado); // Guarda el nuevo estado como resultado
                log.setTimestamp(LocalDateTime.now());

                logActuadorService.save(log);
                logger.info("Log de estado de actuador guardado exitosamente para Estanque {} - Actuador ID DB {}", pondId, actuatorDbId);
            } else {
                logger.warn("Actuador con ID DB {} (Raw: {}) no encontrado después de actualizar su estado en el estanque {}. No se pudo guardar el log.", actuatorDbId, rawActuatorId, pondId);
            }
        } catch (RuntimeException e) {
            logger.error("Error durante la actualización de estado o guardado de log para Actuador ID DB '{}' (Raw: {}) en el estanque {}: {}", actuatorDbId, rawActuatorId, pondId, e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al procesar mensaje de estado para Actuador ID DB '{}' (Raw: {}) en estanque {}: {}", actuatorDbId, rawActuatorId, pondId, e.getMessage(), e);
        }
    }


    private void handleSensorDataMessage(Long pondId, String rawSensorId, String payload) {
        Optional<Long> sensorDbIdOpt = extractNumericId(rawSensorId); // Extraer ID numérico del sensor

        if (!sensorDbIdOpt.isPresent()) {
            logger.warn("ID de sensor raw '{}' del estanque {} no tiene un formato prefijado válido.", rawSensorId, pondId);
            return; // Salir si el ID del sensor no es válido
        }
        Long sensorDbId = sensorDbIdOpt.get();

        try {
            Double valor = Double.valueOf(payload);

            Optional<Sensor> sensorOpt = sensorService.findById(sensorDbId);

            if (!sensorOpt.isPresent()) {
                logger.warn("Sensor con ID {} (Raw: {}) no encontrado en la base de datos para el estanque {}. No se guardarán los datos.", sensorDbId, rawSensorId, pondId);
                return;
            }
            Sensor sensor = sensorOpt.get();
            DatosSensor nuevoDato = new DatosSensor();
            nuevoDato.setSensor(sensor); // Asignar la entidad Sensor
            nuevoDato.setValor(valor); // Asignar el valor
            nuevoDato.setTimestamp(LocalDateTime.now()); // Hora de recepción


            sensorDataService.save(nuevoDato);

        } catch (NumberFormatException e) {
            // Manejar el caso en que el payload no sea un número válido
            logger.error("Payload '{}' del tópico de datos para sensor raw '{}' en estanque {} no es un número válido: {}", payload, rawSensorId, pondId, e.getMessage());
        } catch (Exception e) {
            // Capturar cualquier otra excepción durante el proceso
            logger.error("Error al procesar y guardar datos del sensor ID DB '{}' (Raw: {}) en el estanque {}: {}", sensorDbId, rawSensorId, pondId, e.getMessage(), e);
        }
    }

    private void handleSensorAlertMessage(Long pondId, String rawSensorId, String payload) {
        logger.debug("Manejando mensaje de alerta de sensor: Estanque {} - Sensor raw '{}' - Payload: {}", pondId, rawSensorId, payload);
        Optional<Long> sensorDbIdOpt = extractNumericId(rawSensorId);
        if (!sensorDbIdOpt.isPresent()) {
            logger.warn("ID de sensor raw '{}' del estanque {} no tiene un formato prefijado válido.", rawSensorId, pondId);
            return;
        }

        Long sensorDbId = sensorDbIdOpt.get();
        try {
            Optional<Sensor> sensorOpt = sensorService.findById(sensorDbId);
            if (!sensorOpt.isPresent()) {
                logger.warn("Sensor con ID DB {} (Raw: {}) no encontrado para la alerta en el estanque {}. No se creará la alerta.", sensorDbId, rawSensorId, pondId);
                return;
            }

            Sensor sensor = sensorOpt.get();

            Alerta alerta = new Alerta();
            alerta.setSensor(sensor); // Asignar la entidad Sensor
            alerta.setTimestamp(LocalDateTime.now()); // Hora de recepción
            String alertMessageText = "Alerta anómala para sensor " + rawSensorId; // Mensaje por defecto

            try {
                JsonNode alertJson = objectMapper.readTree(payload);
                String specificMessage = alertJson.has("message") ? alertJson.get("message").asText() : "valor fuera de rango";

                // Construir un mensaje más detallado
                alertMessageText = String.format("Alerta anómala para sensor de %s. Mensaje: %s",
                    sensor.getTipo(),specificMessage);

            } catch (Exception jsonParseError) {
                logger.warn("No se pudo parsear el payload de alerta como JSON para el sensor {}: {}", rawSensorId, payload, jsonParseError);
                alertMessageText = String.format("Alerta anómala para sensor %s. Payload no JSON o inesperado: %s", rawSensorId, payload);
            }

            alerta.setMensaje(alertMessageText); // Asignar el mensaje construido

            alertService.save(alerta);
            logger.info("Alerta de sensor guardada exitosamente para Estanque {} - Sensor ID DB {} (Raw: {})", pondId, sensorDbId, rawSensorId);
        } catch (Exception e) {
        logger.error("Error al procesar y guardar alerta del sensor ID DB '{}' (Raw: {}) en el estanque {}: {}", sensorDbId, rawSensorId, pondId, e.getMessage(), e);
        }
    }


    private void handleHeartbeatErrorMessage(Long pondId, String deviceType, String rawDeviceId, String payload) {
        logger.debug("Manejando error de heartbeat: Estanque {} - Tipo detectado '{}' - Dispositivo raw '{}' - Payload: {}", pondId, deviceType, rawDeviceId, payload);
        Optional<Long> deviceDbIdOpt = extractNumericId(rawDeviceId); // Extraer ID numérico

        if (!deviceDbIdOpt.isPresent()) {
            logger.warn("ID de dispositivo raw '{}' para tipo detectado '{}' del estanque {} no tiene un formato prefijado válido.", rawDeviceId, deviceType, pondId);
            return;
        }

        Long deviceDbId = deviceDbIdOpt.get(); // ID numérico del dispositivo

        try {
            if ("sensor".equals(deviceType)) {
                // --- Es un error de Heartbeat de Sensor -> Crear Alerta ---
            logger.debug("Es un error de Heartbeat de Sensor. Creando Alerta.");
                Optional<Sensor> sensorOpt = sensorService.findById(deviceDbId);
                if (!sensorOpt.isPresent()) {
                    logger.warn("Sensor con ID DB {} (Raw: {}) no encontrado para error heartbeat (sensor) en el estanque {}. No se creará la alerta.", deviceDbId, rawDeviceId, pondId);
                   return;
                }

                Sensor sensor = sensorOpt.get();
                Alerta alerta = new Alerta();
                alerta.setSensor(sensor);
                alerta.setTimestamp(LocalDateTime.now());
                String alertMessageText = String.format("Error de Heartbeat para sensor de %s. Mensaje: %s", sensor.getTipo(),
                        (payload != null ? payload.trim().toUpperCase() : "UNKNOWN"));
                alerta.setMensaje(alertMessageText);
                alertService.save(alerta);
                logger.info("Alerta de error de heartbeat de sensor guardada exitosamente para Estanque {} - Sensor ID DB {}", pondId, deviceDbId);
                
              } else if ("actuador".equals(deviceType)) {
                logger.debug("Es un error de Heartbeat de Actuador. Creando LogActuador.");
                Optional<Actuador> actuatorOpt = actuadorService.findById(deviceDbId);
                if (!actuatorOpt.isPresent()) {
                    logger.warn("Actuador con ID DB {} (Raw: {}) no encontrado para error heartbeat (actuador) en el estanque {}. No se guardará el log.", deviceDbId, rawDeviceId, pondId);
                    return;
                }

                Actuador actuador = actuatorOpt.get();
                LogActuador log = new LogActuador();
                log.setActuador(actuador);
                log.setAccion("Heartbeat Error");
                log.setResultado((payload != null ? payload.trim().toUpperCase() : "UNKNOWN"));
                log.setTimestamp(LocalDateTime.now());

            logActuadorService.save(log);
            logger.info("Log de error de heartbeat de actuador guardado exitosamente para Estanque {} - Actuador ID DB {}", pondId, deviceDbId);
            } else {
            logger.warn("Error de heartbeat con tipo de dispositivo detectado desconocido '{}' para dispositivo raw '{}' en estanque {}. Ignorando.", deviceType, rawDeviceId, pondId);
            }


         }  
            catch (Exception e) {   
            logger.error("Error al procesar y guardar error heartbeat del tipo detectado '{}' dispositivo ID DB '{}' (Raw: {}) en estanque {}: {}", deviceType, deviceDbId, rawDeviceId, pondId, e.getMessage(), e);
        }
}


    // Tópico: aquanest/E{id_pond}/delete (length 3)
    private void handlePondDeleteMessage(Long pondId, String payload) {
    logger.debug("Manejando eliminación de estanque: Estanque ID DB {} - Payload: {}", pondId, payload);
        try {
    logger.info("Mensaje de eliminación de estanque procesado (simulado) para Estanque ID DB {}", pondId);
        } catch (Exception e) {
        logger.error("Error al procesar mensaje de eliminación del estanque ID DB {}: {}", pondId, e.getMessage(), e);
        }
}
    
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutboundMessageHandler(MqttPahoClientFactory clientFactory) {
        String outboundClientId = apiClientId + "-outbound";
        logger.info("Creando MqttPahoMessageHandler con ClientID: {}", outboundClientId);

        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(outboundClientId, clientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        messageHandler.setApplicationEventPublisher(event -> {
        });
        return messageHandler;
    }
}
