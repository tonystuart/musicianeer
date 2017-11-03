// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.util.Optional;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.jni.Input;
import com.example.afs.musicpad.device.common.DeviceWatcher;
import com.example.afs.musicpad.device.midi.MidiWatcherBehavior;
import com.example.afs.musicpad.device.qwerty.QwertyWatcherBehavior;
import com.example.afs.musicpad.message.OnAllTasksStarted;
import com.example.afs.musicpad.mqtt.MqttBuilder;
import com.example.afs.musicpad.mqtt.MqttPublisher;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.transport.TransportTask;
import com.example.afs.musicpad.webapp.WebServer;

public class MusicPad {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java " + MusicPad.class.getName() + " music-library");
      System.exit(1);
    }
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
    System.loadLibrary(Input.NATIVE_LIBRARY_NAME);
    MusicPad musicPad = new MusicPad(args[0]);
    musicPad.start();
  }

  private Conductor conductor;
  private WebServer webServer;
  private MessageBroker broker;
  private DeviceWatcher midiWatcher;
  private DeviceWatcher qwertyWatcher;
  private TransportTask transportTask;
  private Optional<MqttPublisher> optionalMqttPublisher;
  private CommandProcessor commandProcessor;
  private Synthesizer synthesizer = createSynthesizer();

  public MusicPad(String libraryPath) {
    this.broker = new MessageBroker();
    this.optionalMqttPublisher = new MqttBuilder(broker).create();
    this.conductor = new Conductor(broker, libraryPath);
    this.transportTask = new TransportTask(broker, synthesizer);
    this.commandProcessor = new CommandProcessor(broker);
    this.webServer = new WebServer(broker);
    this.qwertyWatcher = new DeviceWatcher(broker, synthesizer, new QwertyWatcherBehavior());
    this.midiWatcher = new DeviceWatcher(broker, synthesizer, new MidiWatcherBehavior());
  }

  private Synthesizer createSynthesizer() {
    int processors = Runtime.getRuntime().availableProcessors();
    System.out.println("MusicPad.createSynthesizer: processors=" + processors);
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", Player.TOTAL_CHANNELS);
    settings.set("synth.cpu-cores", processors);
    Synthesizer synthesizer = new Synthesizer(settings);
    return synthesizer;
  }

  private void start() {
    if (optionalMqttPublisher.isPresent()) {
      optionalMqttPublisher.get().start();
    }
    conductor.start();
    transportTask.start();
    commandProcessor.start();
    webServer.start();
    midiWatcher.start();
    qwertyWatcher.start();
    broker.publish(new OnAllTasksStarted());
  }

}
