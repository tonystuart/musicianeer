// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.awt.event.KeyEvent;

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.message.OnConfigurationChange;
import com.example.afs.musicpad.task.MessageBroker;

public class QwertyConfiguration extends Configuration {

  public enum InputType {
    ALPHA, NUMERIC
  }

  private static final InputMap ALPHA_BANK_MAP = new InputMap(" " + (char) KeyEvent.VK_SHIFT + "123456789");
  private static final InputMap ALPHA_NOTE_MAP = new InputMap("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

  private static final InputMap NUMERIC_BANK_MAP = new InputMap(" 0/*-+");
  private static final InputMap NUMERIC_NOTE_MAP = new InputMap("123456789");

  private MessageBroker broker;
  private int deviceIndex;

  public QwertyConfiguration(MessageBroker broker, int deviceIndex) {
    this.broker = broker;
    this.deviceIndex = deviceIndex;
    this.bankMap = NUMERIC_BANK_MAP;
    this.noteMap = NUMERIC_NOTE_MAP;
  }

  public void setInputType(InputType inputType) {
    switch (inputType) {
    case ALPHA:
      bankMap = ALPHA_BANK_MAP;
      noteMap = ALPHA_NOTE_MAP;
      break;
    case NUMERIC:
      bankMap = NUMERIC_BANK_MAP;
      noteMap = NUMERIC_NOTE_MAP;
      break;
    default:
      throw new UnsupportedOperationException();

    }
    broker.publish(new OnConfigurationChange(deviceIndex));
  }

}
