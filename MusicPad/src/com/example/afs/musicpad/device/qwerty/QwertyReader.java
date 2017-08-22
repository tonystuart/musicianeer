// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import com.example.afs.jni.Input;
import com.example.afs.musicpad.device.common.DeviceHandler;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class QwertyReader {

  private boolean isTerminated;

  private Thread deviceReader;
  private DeviceHandler deviceHandler;

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

  private void processKeyDown(int keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      deviceHandler.onDown(inputCode);
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void processKeyUp(int keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      deviceHandler.onUp(inputCode);
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void run() {
    try (FileInputStream fileInputStream = new FileInputStream(deviceHandler.getDeviceName())) {
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
    System.out.println("Terminating QWERTY device " + deviceHandler.getDeviceName());
  }

}
