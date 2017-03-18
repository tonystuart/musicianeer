// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnPress;
import com.example.afs.musicpad.message.OnRelease;
import com.example.afs.musicpad.util.ByteArray;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class DeviceReader {

  public static final char NUM_LOCK = 'N';
  public static final char BACK_SPACE = 'B';
  public static final char ENTER = 'E';

  private static final int EV_KEY = 0x01;
  private static final int MAX_LENGTH = 5;

  private String deviceName;
  private Thread deviceReader;

  private BlockingQueue<Message> handlerQueue;
  private StringBuilder left = new StringBuilder();
  private StringBuilder right = new StringBuilder();
  private StringBuilder currentField;
  private boolean isTerminated;
  private boolean isPageDown;

  public DeviceReader(BlockingQueue<Message> handlerQueue, String deviceName) {
    this.handlerQueue = handlerQueue;
    this.deviceName = deviceName;
  }

  public void start() {
    deviceReader = new Thread(() -> run(), deviceName);
    deviceReader.start();
  }

  public void terminate() {
    isTerminated = true;
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

  private Message composeCharPress(int charCode) {
    Message message = null;
    if (charCode == '0') {
      isPageDown = true;
    } else if (charCode == '.') {
      currentField = left;
    } else if (charCode != -1) {
      int buttonIndex = mapCharCodeToButtonIndex(charCode);
      if (buttonIndex != -1) {
        message = new OnPress(buttonIndex);
      }
    }
    return message;
  }

  private Message composeCharRelease(int charCode) {
    Message message = null;
    if (charCode == '0') {
      isPageDown = false;
    } else if (charCode != -1) {
      int buttonIndex = mapCharCodeToButtonIndex(charCode);
      if (buttonIndex != -1) {
        message = new OnRelease(buttonIndex);
      }
    }
    return message;
  }

  private Message composeField(int charCode) {
    Message message = null;
    if ('0' <= charCode && charCode <= '9' && currentField.length() < MAX_LENGTH) {
      currentField.append((char) charCode);
      if (currentField == left) {
        System.out.println("left=" + left);
      } else {
        System.out.println("right=" + right);
      }
    } else if (charCode == ENTER) {
      if (currentField == left) {
        if (left.length() == 0) {
          left.append("0");
        }
        currentField = right;
      } else {
        if (right.length() == 0) {
          right.append("0");
        }
        message = createCommand();
        clear();
      }
    } else {
      clear();
    }
    return message;
  }

  private Message createCommand() {
    Message message = null;
    int commandIndex = parseInteger(left.toString());
    int commandOperand = parseInteger(right.toString());
    Command[] commandValues = Command.values();
    if (commandIndex < commandValues.length) {
      Command command = commandValues[commandIndex];
      message = new OnCommand(command, commandOperand);
    } else {
      System.err.println("Invalid command index " + commandIndex);
    }
    return message;
  }

  private int mapCharCodeToButtonIndex(int charCode) {
    int buttonIndex = CharCode.toIndex(charCode);
    if (buttonIndex != -1 && isPageDown) {
      buttonIndex += CharCode.PAGE_SIZE;
    }
    return buttonIndex;
  }

  private int mapKeyCodeToCharCode(short keyCode) {
    return KeyCode.toCharCode(keyCode);
  }

  private void onKeyPress(short keyCode) {
    Message message = null;
    int charCode = mapKeyCodeToCharCode(keyCode);
    if (currentField == null) {
      message = composeCharPress(charCode);
    } else {
      message = composeField(charCode);
    }
    if (message != null) {
      sendToHandler(message);
    }
  }

  private void onKeyRelease(short keyCode) {
    Message message = null;
    int charCode = mapKeyCodeToCharCode(keyCode);
    if (currentField == null) {
      message = composeCharRelease(charCode);
    }
    if (message != null) {
      sendToHandler(message);
    }
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
      while (!isTerminated) {
        try {
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
        } catch (RuntimeException e) {
          e.printStackTrace();
          System.err.println("DeviceReader.read: ignoring exception");
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e1) {
      throw new RuntimeException(e1);
    }
  }

  private void sendToHandler(Message message) {
    handlerQueue.add(message);
  }

}
