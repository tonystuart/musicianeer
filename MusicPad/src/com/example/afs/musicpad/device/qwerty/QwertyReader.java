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

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.util.ByteArray;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class QwertyReader {

  private static final int EV_KEY = 0x01;

  private Thread deviceReader;
  private DeviceHandler deviceHandler;

  private boolean isTerminated;

  public QwertyReader(DeviceHandler deviceHandler) {
    this.deviceHandler = deviceHandler;
  }

  public void start() {
    deviceReader = new Thread(() -> run(), deviceHandler.getDeviceName());
    deviceReader.start();
  }

  public void terminate() {
    isTerminated = true;
  }

  private void capture(FileInputStream fileInputStream) {
    try {
      Field f = FileDescriptor.class.getDeclaredField("fd");
      if (f != null) {
        f.setAccessible(true);
        Integer fd = (Integer) f.get(fileInputStream.getFD());
        if (fd != null) {
          // See EVIOCGRAB in drivers/input/evdev.c
          FluidSynth.capture(fd, 1);
        }
      }
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void processKeyDown(short keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      if (inputCode == KeyEvent.VK_ESCAPE) {
        deviceHandler.detach();
      }
      deviceHandler.onDown(inputCode);
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void processKeyUp(short keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      deviceHandler.onUp(inputCode);
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void run() {
    try (FileInputStream fileInputStream = new FileInputStream(deviceHandler.getDeviceName())) {
      capture(fileInputStream);
      byte[] buffer = new byte[16];
      while (!isTerminated) {
        try {
          fileInputStream.read(buffer);
          short type = ByteArray.toNativeShort(buffer, 8);
          //System.out.printf("buffer=%s, type=%#x, code=%#x, value=%#x\n", Arrays.toString(buffer), type, code, value);
          if (type == EV_KEY) {
            int value = ByteArray.toNativeInteger(buffer, 12);
            if (value == 0) {
              short code = ByteArray.toNativeShort(buffer, 10);
              processKeyUp(code);
            } else if (value == 1) {
              short code = ByteArray.toNativeShort(buffer, 10);
              processKeyDown(code);
            }
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
    System.out.println("Terminating QWERTY device " + deviceHandler.getDeviceName());
  }

}
