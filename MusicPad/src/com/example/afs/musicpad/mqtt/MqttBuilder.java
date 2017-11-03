// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.mqtt;

import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.JsonUtilities;

public class MqttBuilder {

  private MessageBroker broker;

  public MqttBuilder(MessageBroker broker) {
    this.broker = broker;
  }

  public Optional<MqttPublisher> create() {
    Optional<MqttPublisher> optionalMqttPublisher = Optional.empty();
    MqttConfiguration mqttConfiguration = JsonUtilities.fromJsonFile(Mqtt.CONFIGURATION, MqttConfiguration.class);
    if (mqttConfiguration != null) {
      try {
        MqttClient client = connect(mqttConfiguration.getBrokerUri(), mqttConfiguration.getClientId());
        MqttPublisher mqttPublisher = new MqttPublisher(broker, client);
        optionalMqttPublisher = Optional.of(mqttPublisher);
      } catch (MqttException e) {
        System.out.println(e);
        System.out.println("MQTT messages will not be published. Delete or rename " + Mqtt.CONFIGURATION + " to disable MQTT.");
      }
    }
    return optionalMqttPublisher;
  }

  private MqttClient connect(String brokerUri, String clientId) throws MqttException {
    System.out.println("MqttBuilder.connect: brokerUri=" + brokerUri + ", clientId=" + clientId);
    MqttClient client = new MqttClient(brokerUri, clientId);
    MqttConnectOptions connectOptions = new MqttConnectOptions();
    connectOptions.setAutomaticReconnect(true);
    connectOptions.setCleanSession(true);
    client.connect(connectOptions);
    return client;
  }

}
