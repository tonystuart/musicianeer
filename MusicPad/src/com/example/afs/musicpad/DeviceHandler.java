// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.musicpad.message.BendDown;
import com.example.afs.musicpad.message.BendUp;
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.DigitPressed;
import com.example.afs.musicpad.message.DigitReleased;
import com.example.afs.musicpad.message.PageDown;
import com.example.afs.musicpad.message.PageLeft;
import com.example.afs.musicpad.message.PageRight;
import com.example.afs.musicpad.message.PageUp;
import com.example.afs.musicpad.util.ByteArray;
import com.example.afs.musicpad.util.MessageBroker;
import com.example.afs.musicpad.util.MessageBroker.Message;
import com.example.afs.musicpad.util.Task;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class DeviceHandler extends Task {

  private static final int EV_KEY = 0x01;
  private static final char NUM_LOCK = 'a';
  private static final char BACK_SPACE = 'b';
  private static final char ENTER = 'c';
  private static final int MAX_LENGTH = 5;

  private int deviceId;
  private String deviceName;
  private Thread deviceReader;

  private StringBuilder left = new StringBuilder();
  private StringBuilder right = new StringBuilder();
  private StringBuilder currentField;

  protected DeviceHandler(MessageBroker messageBroker, int deviceId, String deviceName) {
    super(messageBroker);
    this.deviceId = deviceId;
    this.deviceName = deviceName;
  }

  @Override
  public void start() {
    super.start();
    deviceReader = new Thread(() -> run(), deviceName);
    deviceReader.start();
  }

  private void capture(FileInputStream inputStream, int value) {
    try {
      Field f = FileDescriptor.class.getDeclaredField("fd");
      if (f != null) {
        f.setAccessible(true);
        Integer fd = (Integer) f.get(inputStream.getFD());
        if (fd != null) {
          FluidSynth.capture(fd, value);
        }
      }
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void clear() {
    left.setLength(0);
    right.setLength(0);
    currentField = null;
  }

  private char mapKeyCode(short code) {
    char value = 0;
    switch (code) {
    case 69:
      value = NUM_LOCK;
      break;
    case 98:
      value = '/';
      break;
    case 55:
      value = '*';
      break;
    case 14:
      value = BACK_SPACE;
      break;
    case 71:
      value = '7';
      break;
    case 72:
      value = '8';
      break;
    case 73:
      value = '9';
      break;
    case 74:
      value = '-';
      break;
    case 75:
      value = '4';
      break;
    case 76:
      value = '5';
      break;
    case 77:
      value = '6';
      break;
    case 78:
      value = '+';
      break;
    case 79:
      value = '1';
      break;
    case 80:
      value = '2';
      break;
    case 81:
      value = '3';
      break;
    case 96:
      value = ENTER;
      break;
    case 82:
      value = '0';
      break;
    case 83:
      value = '.';
    }
    return value;
  }

  private void onCancel() {
    clear();
  }

  private void onKeyPress(short keyCode) {
    Message message = null;
    char charCode = mapKeyCode(keyCode);
    //System.out.println("onKeyPress: keyCode=" + keyCode + ", charCode=" + charCode);
    if (currentField == null) {
      if ('1' <= charCode && charCode <= '9') {
        message = new DigitPressed(deviceId, charCode);
      } else if (charCode == '/') {
        message = new PageUp();
      } else if (charCode == '*') {
        message = new PageDown();
      } else if (charCode == '-') {
        message = new PageLeft(deviceId);
      } else if (charCode == '+') {
        message = new PageRight(deviceId);
      } else if (charCode == '0') {
        message = new BendDown(deviceId);
      } else if (charCode == ENTER) {
        message = new BendUp(deviceId);
      } else if (charCode == '.') {
        currentField = left;
      }
    } else {
      if ('0' <= charCode && charCode <= '9' && currentField.length() < MAX_LENGTH) {
        currentField.append(charCode);
        if (currentField == left) {
          System.out.println("left=" + left);
        } else {
          System.out.println("right=" + right);
        }
      } else if (charCode == ENTER) {
        message = onOkay();
      } else {
        onCancel();
      }
    }
    if (message != null) {
      getMessageBroker().publish(message);
    }
  }

  private void onKeyRelease(short keyCode) {
    Message message = null;
    char charCode = mapKeyCode(keyCode);
    //System.out.println("onKeyRelease: keyCode=" + keyCode + ", charCode=" + charCode);
    if (currentField == null) {
      if ('1' <= charCode && charCode <= '9') {
        message = new DigitReleased(deviceId, charCode);
      }
    }
    if (message != null) {
      getMessageBroker().publish(message);
    }
  }

  private Message onOkay() {
    Message message = null;
    if (currentField == left) {
      if (left.length() == 0) {
        left.append("0");
      }
      currentField = right;
    } else {
      if (right.length() == 0) {
        right.append("0");
      }
      message = new Command(deviceId, parseInteger(left.toString()), parseInteger(right.toString()));
      clear();
    }
    return message;
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

  private void run() {
    try (FileInputStream inputStream = new FileInputStream(deviceName)) {
      capture(inputStream, 1);
      byte[] buffer = new byte[16];
      while (!isTerminated()) {
        inputStream.read(buffer);
        short type = ByteArray.toNativeShort(buffer, 8);
        //System.out.printf("buffer=%s, type=%#x, code=%#x, value=%#x\n", Arrays.toString(buffer), type, code, value);
        if (type == EV_KEY) {
          int value = ByteArray.toNativeInteger(buffer, 12);
          if (value == 0) {
            short code = ByteArray.toNativeShort(buffer, 10);
            onKeyRelease(code);
          } else if (value == 1) {
            short code = ByteArray.toNativeShort(buffer, 10);
            onKeyPress(code);
          }
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e1) {
      throw new RuntimeException(e1);
    }
  }

}
