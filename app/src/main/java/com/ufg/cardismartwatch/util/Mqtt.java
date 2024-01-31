package com.ufg.cardismartwatch.util;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.util.UUID;

public class Mqtt {
    public static final String brokerURI = "18.211.191.131";
//    public static final String brokerURI = "34.198.232.62";

    public static void publishMessage(String topicName, String value) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith().topic(topicName).qos(MqttQos.AT_LEAST_ONCE).payload(value.getBytes()).send();
        client.disconnect();
    }
}
