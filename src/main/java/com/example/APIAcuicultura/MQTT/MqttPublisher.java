package com.example.APIAcuicultura.MQTT;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MqttPublisher {

    private final MessageChannel mqttOutboundChannel;

    public MqttPublisher(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    public void publishPondInfo(String pondId, String payload) {
        String topic = String.format("aquanest/%s/info", pondId);
        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .setHeader("mqtt_qos", 1)
                        .build()
        );
    }

    public void publishPondInfo(String pondId) {
        String topic = String.format("aquanest/%s/info", pondId);
        mqttOutboundChannel.send(MessageBuilder.withPayload("").setHeader("mqtt_topic", topic).build());
    }

    public void publishPondDeletion(String pondId) {
        String topic = String.format("aquanest/%s/delete", pondId);
        mqttOutboundChannel.send(MessageBuilder.withPayload("").setHeader("mqtt_topic", topic).build());
    }

}
