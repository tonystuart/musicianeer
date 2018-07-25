// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import com.example.afs.musicianeer.device.midi.MidiWatcher;
import com.example.afs.musicianeer.mqtt.MqttBuilder;
import com.example.afs.musicianeer.mqtt.MqttPublisher;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.webapp.WebServer;

public class MusicianeerMain {
  public static void main(String[] args) {
    MessageBroker messageBroker = new MessageBroker();
    MidiLibraryManager midiLibraryManager = new MidiLibraryManager(messageBroker);
    MidiWatcher midiWatcher = new MidiWatcher(messageBroker);
    Musicianeer musicianeer = new Musicianeer(messageBroker);
    WebServer webServer = new WebServer(messageBroker);
    midiLibraryManager.tsStart();
    midiWatcher.tsStart();
    musicianeer.tsStart();
    webServer.tsStart();
    startOptionalMqttPublisher(messageBroker);
  }

  private static void startOptionalMqttPublisher(MessageBroker messageBroker) {
    MqttPublisher mqttPublisher = new MqttBuilder(messageBroker).createFromOptionalConfiguration();
    if (mqttPublisher != null) {
      mqttPublisher.tsStart();
    }
  }
}