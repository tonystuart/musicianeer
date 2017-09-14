// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnKeyDown;
import com.example.afs.musicpad.message.OnKeyUp;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.JsonUtilities;

public class MqttPublisher extends BrokerTask<Message> {

  public static class DeviceIndexMessage {
    private int deviceIndex;

    public DeviceIndexMessage() {
    }

    public DeviceIndexMessage(int deviceIndex) {
      this.deviceIndex = deviceIndex;
    }

    public int getDeviceIndex() {
      return deviceIndex;
    }

    @Override
    public String toString() {
      return "DeviceIndexMessage [deviceIndex=" + deviceIndex + "]";
    }

  }

  public static class MqttConfiguration {
    private String brokerUri;
    private String clientId;

    public String getBrokerUri() {
      return brokerUri;
    }

    public String getClientId() {
      return clientId;
    }

    public void setBrokerUri(String brokerUri) {
      this.brokerUri = brokerUri;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    @Override
    public String toString() {
      return "MqttConfiguration [brokerUri=" + brokerUri + ", clientId=" + clientId + "]";
    }

  }

  private static final String MQTT = "mqtt";
  private static final String SOUND_ON = "/keyboard/midi/sound/on";
  private static final String SOUND_OFF = "/keyboard/midi/sound/off";

  private MqttClient client;

  public MqttPublisher(Broker<Message> broker) {
    super(broker);
    MqttConfiguration mqttConfiguration = JsonUtilities.fromJsonFile(MQTT, MqttConfiguration.class);
    if (mqttConfiguration != null) {
      client = connect(mqttConfiguration.getBrokerUri(), mqttConfiguration.getClientId());
      subscribe(OnKeyUp.class, message -> doKeyUp(message));
      subscribe(OnKeyDown.class, message -> doKeyDown(message));
    }
  }

  private MqttClient connect(String brokerUri, String clientId) {
    try {
      System.out.println("MqttPublisher.connect: brokerUri=" + brokerUri + ", clientId=" + clientId);
      MqttClient client = new MqttClient(brokerUri, clientId);
      MqttConnectOptions connectOptions = new MqttConnectOptions();
      connectOptions.setAutomaticReconnect(true);
      connectOptions.setCleanSession(true);
      client.connect(connectOptions);
      return client;
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  private void doKeyDown(OnKeyDown onKeyDown) {
    try {
      String json = JsonUtilities.toJson(new DeviceIndexMessage(onKeyDown.getDeviceIndex()));
      client.publish(SOUND_ON, new MqttMessage(json.getBytes()));
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  private void doKeyUp(OnKeyUp onKeyUp) {
    try {
      String json = JsonUtilities.toJson(new DeviceIndexMessage(onKeyUp.getDeviceIndex()));
      client.publish(SOUND_OFF, new MqttMessage(json.getBytes()));
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }
}
