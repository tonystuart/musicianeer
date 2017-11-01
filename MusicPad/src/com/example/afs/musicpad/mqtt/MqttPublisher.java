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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnKeyDown;
import com.example.afs.musicpad.message.OnKeyUp;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.JsonUtilities;

public class MqttPublisher extends BrokerTask<Message> {

  private MqttClient client;

  public MqttPublisher(Broker<Message> broker, MqttClient client) {
    super(broker);
    this.client = client;
    subscribe(OnKeyUp.class, message -> doKeyUp(message));
    subscribe(OnKeyDown.class, message -> doKeyDown(message));
  }

  private void doKeyDown(OnKeyDown onKeyDown) {
    try {
      String json = JsonUtilities.toJson(new DeviceIndexMessage(onKeyDown.getDeviceIndex()));
      client.publish(Mqtt.SOUND_ON, new MqttMessage(json.getBytes()));
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  private void doKeyUp(OnKeyUp onKeyUp) {
    try {
      String json = JsonUtilities.toJson(new DeviceIndexMessage(onKeyUp.getDeviceIndex()));
      client.publish(Mqtt.SOUND_OFF, new MqttMessage(json.getBytes()));
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }
}
