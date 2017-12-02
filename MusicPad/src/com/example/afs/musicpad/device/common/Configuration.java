// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.Command;

public class Configuration {

  public static class InputMessage {

    private int command;
    private int channel;
    private int control;

    public InputMessage(ShortMessage shortMessage) {
      command = shortMessage.getCommand();
      channel = shortMessage.getChannel();
      control = shortMessage.getData1();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      InputMessage other = (InputMessage) obj;
      if (channel != other.channel) {
        return false;
      }
      if (command != other.command) {
        return false;
      }
      if (control != other.control) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + channel;
      result = prime * result + command;
      result = prime * result + control;
      return result;
    }

  }

  protected InputMap bankMap;
  protected InputMap noteMap;
  private Map<InputMessage, Command> inputMap = new HashMap<>();

  public Command get(ShortMessage shortMessage) {
    return inputMap.get(new InputMessage(shortMessage));
  }

  public int[] getBankInputCodes() {
    return bankMap.getInputCodes();
  }

  public String getBankLegend(int bankIndex) {
    return bankMap.getLegends()[bankIndex] + "+";
  }

  public int[] getNoteInputCodes() {
    return noteMap.getInputCodes();
  }

  public String getNoteLegend(int noteIndex) {
    return noteMap.getLegends()[noteIndex];
  }

  public void put(ShortMessage shortMessage, Command command) {
    inputMap.put(new InputMessage(shortMessage), command);
  }

}
