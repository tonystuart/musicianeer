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

public class QwertyConfiguration implements Configuration {

  public enum KeyboardType {
    ALPHA, NUMERIC
  }

  private static final InputMap ALPHA_GROUP_MAP = new InputMap(" " + (char) KeyEvent.VK_SHIFT + "123456789");
  private static final InputMap ALPHA_SOUND_MAP = new InputMap("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

  private static final InputMap NUMERIC_GROUP_MAP = new InputMap(" 0/*-+");
  private static final InputMap NUMERIC_SOUND_MAP = new InputMap("123456789");

  private int deviceIndex;
  private MessageBroker broker;

  private InputMap groupMap = new InputMap("@");
  private InputMap soundMap = new InputMap("@");

  public QwertyConfiguration(MessageBroker broker, int deviceIndex) {
    this.broker = broker;
    this.deviceIndex = deviceIndex;
    this.groupMap = NUMERIC_GROUP_MAP;
    this.soundMap = NUMERIC_SOUND_MAP;
  }

  @Override
  public InputMap getGroupInputMap() {
    return groupMap;
  }

  @Override
  public InputMap getSoundInputMap() {
    return soundMap;
  }

  public void setKeyboardType(KeyboardType keyboardType) {
    switch (keyboardType) {
    case ALPHA:
      groupMap = ALPHA_GROUP_MAP;
      soundMap = ALPHA_SOUND_MAP;
      break;
    case NUMERIC:
      groupMap = NUMERIC_GROUP_MAP;
      soundMap = NUMERIC_SOUND_MAP;
      break;
    default:
      throw new UnsupportedOperationException();

    }
    broker.publish(new OnConfigurationChange(deviceIndex));
  }

}
