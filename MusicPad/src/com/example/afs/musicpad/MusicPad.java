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
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.transport.TransportTask;
import com.example.afs.musicpad.util.Broker;

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

  private static Settings getSynthesizerSettings() {
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", Player.TOTAL_CHANNELS);
    return settings;
  }

  private Broker<Message> broker;
  private MusicLibrary musicLibrary;
  private DeviceWatcher deviceWatcher;
  private DeviceManager deviceManager;
  private CommandProcessor commandProcessor;
  private TransportTask transportTask;

  private Synthesizer synthesizer = new Synthesizer(getSynthesizerSettings());

  public MusicPad(String libraryPath) {
    this.musicLibrary = new MusicLibrary(libraryPath);
    this.broker = new Broker<Message>();
    this.deviceWatcher = new DeviceWatcher(broker);
    this.deviceManager = new DeviceManager(broker, synthesizer);
    this.commandProcessor = new CommandProcessor(broker, synthesizer, musicLibrary);
    this.transportTask = new TransportTask(broker, synthesizer);
  }

  private void start() {
    commandProcessor.start();
    deviceManager.start();
    deviceWatcher.start();
    transportTask.start();
  }

}
