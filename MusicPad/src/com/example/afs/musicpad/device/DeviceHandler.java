// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnPress;
import com.example.afs.musicpad.message.OnRelease;
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
    delegate(OnCommand.class, message -> doCommand(message));
    delegate(OnPress.class, message -> doPress(message.getButtonIndex()));
    delegate(OnRelease.class, message -> doRelease(message.getButtonIndex()));
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
    subscribe(OnTick.class, message -> doTick(message.getTick()));
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

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case SELECT_CHORDS:
      selectChords(parameter);
      break;
    case SELECT_PROGRAM:
      selectProgram(parameter);
      break;
    case SELECT_NOTES:
      selectContour(parameter);
      break;
    case SELECT_DRUMS:
      selectDrums(parameter);
      break;
    case SET_PLAYER_VELOCITY:
      setPercentVelocity(parameter);
      break;
    default:
      getBroker().publish(message);
      break;
    }
  }

  private void doPress(int buttonIndex) {
    player.play(Action.PRESS, buttonIndex);
  }

  private void doRelease(int buttonIndex) {
    player.play(Action.RELEASE, buttonIndex);
  }

  private void doSongSelected(Song song) {
    player.close();
    currentSong = song;
    player = defaultPlayer;
  }

  private void doTick(long tick) {
    player.onTick(tick);
  }

  private void selectChords(int channelNumber) {
    player.close();
    if (currentSong == null) {
      defaultPlayer = new KeyChordPlayer(synthesizer, Keys.CMajor, 0);
      player = defaultPlayer;
    } else if (channelNumber == 10) {
      System.err.println("Cannot select chords for drum channel");
    } else {
      int channelIndex = channelNumber - 1;
      player = new SongChordPlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void selectContour(int channelNumber) {
    player.close();
    if (currentSong == null) {
      defaultPlayer = new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
      player = defaultPlayer;
    } else if (channelNumber == 10) {
      System.err.println("Cannot select contour for drum channel");
    } else {
      int channelIndex = channelNumber - 1;
      player = new SongNotePlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void selectDrums(int kitNumber) {
    player.close();
    if (currentSong == null) {
      int kitIndex = kitNumber - 1;
      defaultPlayer = new GeneralDrumPlayer(synthesizer, kitIndex);
      player = defaultPlayer;
    } else {
      player = new SongDrumPlayer(synthesizer, currentSong);
    }
  }

  private void selectProgram(int programNumber) {
    int programIndex = programNumber - 1;
    player.selectProgram(programIndex);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

}
