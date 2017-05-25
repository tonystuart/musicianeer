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
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import com.example.afs.fluidsynth.FluidSynth;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.util.ByteArray;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class QwertyReader {

  private static final int EV_KEY = 0x01;

  private Thread deviceReader;
  private BlockingQueue<Message> queue;
  private DeviceHandler deviceHandler;

  private boolean isTerminated;
  private int[] activeMidiNotes = new int[256]; // NB: KeyEvents VK codes, not midiNotes

  public QwertyReader(BlockingQueue<Message> queue, DeviceHandler deviceHandler) {
    this.queue = queue;
    this.deviceHandler = deviceHandler;
    Arrays.fill(activeMidiNotes, -1);
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

  private void processInputCodeDown(int inputCode) {
    int midiNote = deviceHandler.getInputMapping().onDown(inputCode);
    if (midiNote != -1) {
      activeMidiNotes[inputCode] = midiNote;
      queue.add(new OnNoteOn(midiNote));
    }
  }

  private void processInputCodeUp(int inputCode) {
    deviceHandler.getInputMapping().onUp(inputCode);
    int midiNote = activeMidiNotes[inputCode];
    if (midiNote != -1) {
      queue.add(new OnNoteOff(midiNote));
      activeMidiNotes[inputCode] = -1;
    }
  }

  private void processKeyDown(short keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      processInputCodeDown(inputCode);
    } else {
      // e.g. windows meta key (125)
    }
  }

  private void processKeyUp(short keyCode) {
    if (keyCode < QwertyKeyCodes.inputCodes.length) {
      char inputCode = QwertyKeyCodes.inputCodes[keyCode];
      processInputCodeUp(inputCode);
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
    System.out.println("Terminating QWERTY device " + deviceHandler);
  }

}
