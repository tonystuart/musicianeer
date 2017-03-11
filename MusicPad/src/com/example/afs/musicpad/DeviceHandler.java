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
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.CommandEntered;
import com.example.afs.musicpad.message.CommandForwarded;
import com.example.afs.musicpad.message.DigitPressed;
import com.example.afs.musicpad.message.DigitReleased;
import com.example.afs.musicpad.message.KeyPressed;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.SongSelected;
import com.example.afs.musicpad.message.TickOccurred;
import com.example.afs.musicpad.player.ChordPlayer;
import com.example.afs.musicpad.player.NotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.BrokerTask;

public class DeviceHandler extends BrokerTask<Message> {

  public interface DigitAction {
    void onDigit(int channel, int semitone);
  }

  private Player player;
  private Song currentSong;
  private Synthesizer synthesizer;
  private DeviceReader deviceReader;

  protected DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String deviceName) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceReader = new DeviceReader(getInputQueue(), deviceName);
    delegate(CommandEntered.class, message -> onCommand(message.getCommand(), message.getParameter()));
    delegate(KeyPressed.class, message -> onKeyPressed(message.getKey()));
    delegate(DigitPressed.class, message -> onDigitPressed(message.getDigit()));
    delegate(DigitReleased.class, message -> onDigitReleased(message.getDigit()));
    subscribe(SongSelected.class, message -> OnSongSelected(message.getSong()));
    subscribe(TickOccurred.class, message -> onTick(message.getTick()));
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
    if (player != null) {
      player.close();
    }
    if (currentSong != null) {
      int channelIndex = channelNumber - 1;
      player = new ChordPlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectContour(int channelNumber) {
    if (player != null) {
      player.close();
    }
    if (currentSong != null) {
      int channelIndex = channelNumber - 1;
      player = new NotePlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectProgram(int programNumber) {
    if (player != null) {
      int programIndex = programNumber - 1;
      player.selectProgram(programIndex);
    }
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
    default:
      publish(new CommandForwarded(command, parameter));
      break;
    }
  }

  private void onDigitPressed(int digit) {
    if (player != null) {
      player.play(Action.PRESS, digit);
    }
  }

  private void onDigitReleased(int digit) {
    if (player != null) {
      player.play(Action.RELEASE, digit);
    }
  }

  private void onKeyPressed(char key) {
    switch (key) {
    case '-':
      if (player != null) {
        player.selectPreviousPage();
      }
      break;
    case '+':
      if (player != null) {
        player.selectNextPage();
      }
      break;
    }
  }

  private void OnSongSelected(Song song) {
    currentSong = song;
    player = null;
  }

  private void onTick(long tick) {
    if (player != null) {
      player.displayWordsAndMusic(tick);
    }
  }

}
