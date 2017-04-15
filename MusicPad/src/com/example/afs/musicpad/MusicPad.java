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
import com.example.afs.musicpad.analyzer.AnalyzerTask;
import com.example.afs.musicpad.device.midi.MidiWatcher;
import com.example.afs.musicpad.device.qwerty.QwertyWatcher;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Player;
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
  private MusicLibrary musicLibrary;
  private MidiWatcher midiWatcher;
  private QwertyWatcher qwertyWatcher;
  private TransportTask transportTask;
  private AnalyzerTask analyzerTask;
  private CommandProcessor commandProcessor;
  private WebApp webApp;
  private Synthesizer synthesizer = createSynthesizer();

  public MusicPad(String libraryPath) {
    this.musicLibrary = new MusicLibrary(libraryPath);
    this.broker = new Broker<Message>();
    this.midiWatcher = new MidiWatcher(broker, synthesizer);
    this.qwertyWatcher = new QwertyWatcher(broker, synthesizer);
    this.transportTask = new TransportTask(broker, synthesizer);
    this.analyzerTask = new AnalyzerTask(broker);
    this.webApp = new WebApp(broker);
    this.commandProcessor = new CommandProcessor(broker, synthesizer, musicLibrary);
  }

  private Synthesizer createSynthesizer() {
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", Player.TOTAL_CHANNELS);
    Synthesizer synthesizer = new Synthesizer(settings);
    synthesizer.setChannelType(Player.PLAYER_BASE + Midi.DRUM, FluidSynth.CHANNEL_TYPE_DRUM);
    // Force initialization of drum instrument bank and preset, see fluid_synth.c for info
    synthesizer.changeProgram(Player.PLAYER_BASE + Midi.DRUM, 0);
    return synthesizer;
  }

  private void start() {
    midiWatcher.start();
    qwertyWatcher.start();
    transportTask.start();
    analyzerTask.start();
    commandProcessor.start();
    webApp.start();
  }

}
