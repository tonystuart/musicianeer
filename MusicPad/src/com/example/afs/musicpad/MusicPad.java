// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.musicpad.device.common.DeviceWatcher;
import com.example.afs.musicpad.device.midi.MidiWatcherBehavior;
import com.example.afs.musicpad.device.qwerty.QwertyWatcherBehavior;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnAllTasksStarted;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.renderer.RendererTask;
import com.example.afs.musicpad.transport.TransportTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.webapp.WebApp;

public class MusicPad {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java " + MusicPad.class.getName() + " music-library");
      System.exit(1);
    }
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
    MusicPad musicPad = new MusicPad(args[0]);
    musicPad.start();
  }

  private Broker<Message> broker;
  private Conductor conductor;
  private DeviceWatcher midiWatcher;
  private DeviceWatcher qwertyWatcher;
  private TransportTask transportTask;
  private RendererTask rendererTask;
  private CommandProcessor commandProcessor;
  private WebApp webApp;
  private Synthesizer synthesizer = createSynthesizer();

  public MusicPad(String libraryPath) {
    this.broker = new Broker<Message>();
    this.conductor = new Conductor(broker, libraryPath, synthesizer);
    this.commandProcessor = new CommandProcessor(broker);
    this.midiWatcher = new DeviceWatcher(broker, synthesizer, new MidiWatcherBehavior());
    this.qwertyWatcher = new DeviceWatcher(broker, synthesizer, new QwertyWatcherBehavior());
    this.transportTask = new TransportTask(broker, synthesizer);
    this.rendererTask = new RendererTask(broker);
    this.webApp = new WebApp(broker);
  }

  private Synthesizer createSynthesizer() {
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", Player.TOTAL_CHANNELS);
    Synthesizer synthesizer = new Synthesizer(settings);
    return synthesizer;
  }

  private void start() {
    conductor.start();
    midiWatcher.start();
    qwertyWatcher.start();
    transportTask.start();
    rendererTask.start();
    commandProcessor.start();
    webApp.start();
    broker.publish(new OnAllTasksStarted());
  }

}
