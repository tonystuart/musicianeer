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
import com.example.afs.musicpad.message.OnKeyPress;
import com.example.afs.musicpad.message.OnKeyRelease;
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

  public class CommandBuilder {

    private static final int MAX_LENGTH = 5;

    private StringBuilder left = new StringBuilder();
    private StringBuilder right = new StringBuilder();
    private StringBuilder currentField;

    private boolean isShift;

    private void clear() {
      left.setLength(0);
      right.setLength(0);
      currentField = null;
    }

    private void composeCharPress(int charCode) {
      if (charCode == '0') {
        isShift = true;
      } else if (charCode == '.') {
        currentField = left;
      } else if (charCode != -1) {
        int buttonIndex = mapCharCodeToButtonIndex(charCode);
        if (buttonIndex != -1) {
          player.play(Action.PRESS, buttonIndex);
        }
      }
    }

    private void composeCharRelease(int charCode) {
      if (charCode == '0') {
        isShift = false;
      } else if (charCode != -1) {
        int buttonIndex = mapCharCodeToButtonIndex(charCode);
        if (buttonIndex != -1) {
          player.play(Action.RELEASE, buttonIndex);
        }
      }
    }

    private void composeField(int charCode) {
      System.out.println("composeField: charCode=" + charCode);
      if ('0' <= charCode && charCode <= '9' && currentField.length() < MAX_LENGTH) {
        currentField.append((char) charCode);
        if (currentField == left) {
          System.out.println("left=" + left);
        } else {
          System.out.println("right=" + right);
        }
      } else if (charCode == InputDevice.ENTER) {
        if (currentField == left) {
          if (left.length() == 0) {
            left.append("0");
          }
          currentField = right;
        } else {
          if (right.length() == 0) {
            right.append("0");
          }
          createCommand();
          clear();
        }
      } else {
        clear();
      }
    }

    private void createCommand() {
      int commandIndex = parseInteger(left.toString());
      int commandOperand = parseInteger(right.toString());
      Command[] commandValues = Command.values();
      if (commandIndex < commandValues.length) {
        Command command = commandValues[commandIndex];
        OnCommand onCommand = new OnCommand(command, commandOperand);
        doCommand(onCommand);
      } else {
        System.err.println("Invalid command index " + commandIndex);
      }
    }

    private void doKeyPress(short keyCode) {
      int charCode = mapKeyCodeToCharCode(keyCode);
      if (currentField == null) {
        composeCharPress(charCode);
      } else {
        composeField(charCode);
      }
    }

    private void doKeyRelease(short keyCode) {
      int charCode = mapKeyCodeToCharCode(keyCode);
      if (currentField == null) {
        composeCharRelease(charCode);
      }
    }

    private int mapCharCodeToButtonIndex(int charCode) {
      int buttonIndex = inputDevice.toIndex(charCode);
      if (buttonIndex != -1 && isShift) {
        buttonIndex += inputDevice.getButtonPageSize();
      }
      return buttonIndex;
    }

    private int mapKeyCodeToCharCode(short keyCode) {
      return inputDevice.toCharCode(keyCode);
    }

    private int parseInteger(String string) {
      int integer;
      try {
        integer = Integer.parseInt(string);
      } catch (NumberFormatException e) {
        integer = 0;
      }
      return integer;
    }
  }

  private Player player;
  private Song currentSong;
  private Synthesizer synthesizer;
  private DeviceReader deviceReader;
  private Player defaultPlayer;
  private CommandBuilder commandBuilder = new CommandBuilder();
  private InputDevice inputDevice = new NumericKeypad();

  protected DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String deviceName) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceReader = new DeviceReader(getInputQueue(), deviceName);
    this.defaultPlayer = new KeyNotePlayer(synthesizer, Keys.CMajor, 0);
    this.player = defaultPlayer;
    delegate(OnKeyPress.class, message -> commandBuilder.doKeyPress(message.getCode()));
    delegate(OnKeyRelease.class, message -> commandBuilder.doKeyRelease(message.getCode()));
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
    case SELECT_NOTES:
      selectContour(parameter);
      break;
    case SELECT_DRUMS:
      selectDrums(parameter);
      break;
    case SELECT_PROGRAM:
      selectProgram(parameter);
      break;
    case SET_PLAYER_VELOCITY:
      setPercentVelocity(parameter);
      break;
    case SET_KEYBOARD_MAPPING:
      setKeyboardMapping(parameter);
      break;
    default:
      getBroker().publish(message);
      break;
    }
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
      player = new SongChordPlayer(synthesizer, currentSong, channelIndex, inputDevice);
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
      player = new SongNotePlayer(synthesizer, currentSong, channelIndex, inputDevice);
    }
  }

  private void selectDrums(int kitNumber) {
    player.close();
    if (currentSong == null) {
      int kitIndex = kitNumber - 1;
      defaultPlayer = new GeneralDrumPlayer(synthesizer, kitIndex);
      player = defaultPlayer;
    } else {
      player = new SongDrumPlayer(synthesizer, currentSong, inputDevice);
    }
  }

  private void selectProgram(int programNumber) {
    int programIndex = programNumber - 1;
    player.selectProgram(programIndex);
  }

  private void setKeyboardMapping(int mapping) {
    switch (mapping) {
    case 1:
      inputDevice = new NumericKeypad();
      break;
    case 2:
      inputDevice = new AlphabeticKeyboard();
      break;
    }
    player.updateInputDevice(inputDevice);
  }

  private void setPercentVelocity(int percentVelocity) {
    player.setPercentVelocity(percentVelocity);
  }

}
