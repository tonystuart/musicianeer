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
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import com.example.afs.jni.Input;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.qwerty.QwertyConfiguration.InputType;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.task.Message;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class QwertyReader {

  private boolean isCommand;
  private boolean isTerminated;

  private Thread deviceReader;
  private QwertyController controller;
  private DeviceHandler deviceHandler;

  public QwertyReader(DeviceHandler deviceHandler, QwertyController qwertyController) {
    this.deviceHandler = deviceHandler;
    this.controller = qwertyController;
  }

  public void start() {
    deviceReader = new Thread(() -> run(), controller.getDeviceName());
    deviceReader.start();
  }

  public void terminate() {
    isTerminated = true;
  }

  private int getFileDescriptor(FileInputStream fileInputStream) {
    try {
      Integer fd;
      Field f = FileDescriptor.class.getDeclaredField("fd");
      if (f == null) {
        throw new IllegalStateException("Expected FileDescriptor to contain fd field");
      } else {
        f.setAccessible(true);
        fd = (Integer) f.get(fileInputStream.getFD());
        if (fd == null) {
          throw new IllegalStateException("Expected FileDescriptor fd field to be non-null");
        }
        return fd;
      }
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void processKeyboardCommand(int inputCode) {
    switch (inputCode) {
    case KeyEvent.VK_ESCAPE:
      publish(new OnCommand(Command.DETACH, deviceHandler.tsGetDeviceIndex()));
      break;
    case KeyEvent.VK_BACK_SPACE:
      publish(new OnCommand(Command.RESET));
      break;
    case 'B':
      publish(new OnCommand(Command.MOVE_BACKWARD));
      break;
    case 'D':
      publish(new OnCommand(Command.DECREASE_MASTER_GAIN, 0));
      break;
    case 'F':
      publish(new OnCommand(Command.MOVE_FORWARD));
      break;
    case 'I':
      publish(new OnCommand(Command.INCREASE_MASTER_GAIN, 0));
      break;
    case 'P':
      publish(new OnCommand(Command.PLAY, ChannelNotes.ALL_CHANNELS));
      break;
    case 'S':
      publish(new OnCommand(Command.STOP, 0));
      break;
    case '0':
      publish(new OnCommand(Command.DECREASE_TEMPO, 0));
      break;
    case '1':
      publish(new OnCommand(Command.INCREASE_TEMPO, 0));
      break;
    case '2':
      publish(new OnCommand(Command.DECREASE_BACKGROUND_VELOCITY, 0));
      break;
    case '3':
      publish(new OnCommand(Command.INCREASE_BACKGROUND_VELOCITY, 0));
      break;
    case '4':
      publish(new OnDeviceCommand(DeviceCommand.DECREASE_PLAYER_VELOCITY, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '5':
      publish(new OnDeviceCommand(DeviceCommand.INCREASE_PLAYER_VELOCITY, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '6':
      publish(new OnDeviceCommand(DeviceCommand.PREVIOUS_CHANNEL, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '7':
      publish(new OnDeviceCommand(DeviceCommand.NEXT_CHANNEL, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '8':
      publish(new OnDeviceCommand(DeviceCommand.PREVIOUS_PROGRAM, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '9':
      publish(new OnDeviceCommand(DeviceCommand.NEXT_PROGRAM, deviceHandler.tsGetDeviceIndex(), 0));
      break;
    case '/':
      controller.getConfiguration().setInputType(InputType.NUMERIC);
      break;
    case '*':
      controller.getConfiguration().setInputType(InputType.ALPHA);
      break;
    case '-':
      publish(new OnDeviceCommand(DeviceCommand.OUTPUT, deviceHandler.tsGetDeviceIndex(), OutputType.MEASURE.ordinal()));
      break;
    case '+':
      publish(new OnDeviceCommand(DeviceCommand.OUTPUT, deviceHandler.tsGetDeviceIndex(), OutputType.TICK.ordinal()));
      break;
    }
  }

  private void processKeyDown(int keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      if (inputCode == KeyEvent.VK_NUM_LOCK) {
        System.out.println("deviceName=" + controller.getDeviceName() + ", deviceHandler.getDeviceIndex()=" + deviceHandler.tsGetDeviceIndex());
        isCommand = true;
      } else if (isCommand) {
        processKeyboardCommand(inputCode);
      } else {
        deviceHandler.tsOnDown(inputCode);
      }
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void processKeyUp(int keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      if (inputCode == KeyEvent.VK_NUM_LOCK) {
        isCommand = false;
      } else if (isCommand) {
      } else {
        deviceHandler.tsOnUp(inputCode);
      }
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void publish(Message message) {
    deviceHandler.tsGetBroker().publish(message);
  }

  private void run() {
    try (FileInputStream fileInputStream = new FileInputStream(controller.getDeviceName())) {
      int fd = getFileDescriptor(fileInputStream);
      int rc = Input.capture(fd, true);
      if (rc == -1) {
        throw new IllegalStateException("Cannot capture input stream for fd " + fd);
      }
      while (!isTerminated) {
        try {
          int code = Input.read_key_code(fd);
          if (code < 0) {
            processKeyDown(-code);
          } else if (code > 0) {
            processKeyUp(code);
          } else {
            throw new IllegalStateException("Cannot read key code from fd " + fd);
          }
        } catch (RuntimeException e) {
          e.printStackTrace();
          System.err.println("Ignoring exception");
        }
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e1) {
      throw new RuntimeException(e1);
    }
    System.out.println("Terminating QWERTY device " + controller.getDeviceName());
  }

}
