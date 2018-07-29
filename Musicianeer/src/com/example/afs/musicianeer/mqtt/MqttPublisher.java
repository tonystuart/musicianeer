// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.example.afs.musicianeer.message.OnNoteOff;
import com.example.afs.musicianeer.message.OnNoteOn;
import com.example.afs.musicianeer.message.OnTransportNoteOff;
import com.example.afs.musicianeer.message.OnTransportNoteOn;
import com.example.afs.musicianeer.mqtt.MqttNoteMessage.Source;
import com.example.afs.musicianeer.mqtt.MqttNoteMessage.Type;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.MessageTask;
import com.example.afs.musicianeer.util.JsonUtilities;

public class MqttPublisher extends MessageTask {

  private MqttClient client;

  public MqttPublisher(MessageBroker broker, MqttClient client) {
    super(broker);
    this.client = client;
    subscribe(OnTransportNoteOn.class, message -> doTransportNoteOn(message));
    subscribe(OnTransportNoteOff.class, message -> doTransportNoteOff(message));
    subscribe(OnNoteOn.class, message -> doNoteOn(message));
    subscribe(OnNoteOff.class, message -> doNoteOff(message));
  }

  private void doNoteOff(OnNoteOff message) {
    publishMqttNoteMessage(Source.FOREGROUND, Type.OFF, message.getChannel(), message.getData1(), 0);
  }

  private void doNoteOn(OnNoteOn message) {
    publishMqttNoteMessage(Source.FOREGROUND, Type.ON, message.getChannel(), message.getData1(), message.getData2());
  }

  private void doTransportNoteOff(OnTransportNoteOff message) {
    publishMqttNoteMessage(Source.BACKGROUND, Type.OFF, message.getChannel(), message.getMidiNote(), 0);
  }

  private void doTransportNoteOn(OnTransportNoteOn message) {
    publishMqttNoteMessage(Source.BACKGROUND, Type.ON, message.getChannel(), message.getMidiNote(), message.getVelocity());
  }

  private void publishMqttNoteMessage(Source source, Type type, int channel, int midiNote, int velocity) {
    try {
      MqttNoteMessage noteMessage = new MqttNoteMessage(source, type, channel, midiNote, velocity);
      String csv = noteMessage.asString();
      String json = JsonUtilities.toJson(noteMessage);
      client.publish(Mqtt.NOTE_CSV, csv.getBytes(), 0, false);
      client.publish(Mqtt.NOTE_JSON, json.getBytes(), 0, false);
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }
}
