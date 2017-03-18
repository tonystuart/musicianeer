// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.DeviceReader;
import com.example.afs.musicpad.message.OnInput;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnRelease;
import com.example.afs.musicpad.message.OnPress;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.player.GeneralDrumPlayer;
import com.example.afs.musicpad.player.KeyChordPlayer;
import com.example.afs.musicpad.player.KeyNotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.player.SongChordPlayer;
import com.example.afs.musicpad.player.SongDrumPlayer;
import com.example.afs.musicpad.player.SongNotePlayer;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.theory.Keys;
import com.example.afs.musicpad.util.Broker;

public class DeviceHandler extends BrokerTask<Message> {

  private Player player;
  private Song currentSong;
  private Synthesizer synthesizer;
  private DeviceReader deviceReader;
  private Player defaultPlayer;

  protected DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String deviceName) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceReader = new DeviceReader(getInputQueue(), deviceName);
    this.defaultPlayer = new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
    this.player = defaultPlayer;
    delegate(OnInput.class, message -> onCommand(message.getCommand(), message.getParameter()));
    delegate(OnPress.class, message -> onButtonPress(message.getButtonIndex()));
    delegate(OnRelease.class, message -> onButtonRelease(message.getButtonIndex()));
    subscribe(OnSongSelected.class, message -> OnSongSelected(message.getSong()));
    subscribe(OnTick.class, message -> onTick(message.getTick()));
  }

  @Override
  public void start() {
    super.start();
    deviceReader.start();
  }

  @Override
  public void terminate() {
    deviceReader.terminate();
    super.terminate();
  }

  private void doSelectChords(int channelNumber) {
    player.close();
    if (channelNumber == 0 || currentSong == null) {
      defaultPlayer = new KeyChordPlayer(synthesizer, Keys.CMajor, 0);
      player = defaultPlayer;
    } else if (channelNumber == 10) {
      System.err.println("Cannot select chords for drum channel");
    } else {
      int channelIndex = channelNumber - 1;
      player = new SongChordPlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectContour(int channelNumber) {
    player.close();
    if (channelNumber == 0 || currentSong == null) {
      defaultPlayer = new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
      player = defaultPlayer;
    } else if (channelNumber == 10) {
      System.err.println("Cannot select contour for drum channel");
    } else {
      int channelIndex = channelNumber - 1;
      player = new SongNotePlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectDrums(int kitNumber) {
    player.close();
    if (currentSong == null) {
      int kitIndex = kitNumber - 1;
      defaultPlayer = new GeneralDrumPlayer(synthesizer, kitIndex);
      player = defaultPlayer;
    } else {
      player = new SongDrumPlayer(synthesizer, currentSong);
    }
  }

  private void doSelectProgram(int programNumber) {
    int programIndex = programNumber - 1;
    player.selectProgram(programIndex);
  }

  private void onCommand(int command, int parameter) {
    switch (command) {
    case Command.SELECT_CHORDS:
      doSelectChords(parameter);
      break;
    case Command.SELECT_PROGRAM:
      doSelectProgram(parameter);
      break;
    case Command.SELECT_NOTES:
      doSelectContour(parameter);
      break;
    case Command.SELECT_DRUMS:
      doSelectDrums(parameter);
      break;
    default:
      publish(new OnCommand(command, parameter));
      break;
    }
  }

  private void onButtonRelease(int buttonIndex) {
    player.play(Action.RELEASE, buttonIndex);
  }

  private void onButtonPress(int buttonIndex) {
    player.play(Action.PRESS, buttonIndex);
  }

  private void OnSongSelected(Song song) {
    player.close();
    currentSong = song;
    player = defaultPlayer;
  }

  private void onTick(long tick) {
    player.onTick(tick);
  }

}
