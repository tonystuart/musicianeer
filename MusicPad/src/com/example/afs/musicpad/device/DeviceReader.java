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
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnKeyPress;
import com.example.afs.musicpad.message.OnKeyRelease;
import com.example.afs.musicpad.util.ByteArray;

// See /usr/include/linux/input.h
// See https://www.kernel.org/doc/Documentation/input/input.txt

public class DeviceReader {

  public static class EventDeviceGrabber {
    private FileInputStream fileInputStream;

    public EventDeviceGrabber(FileInputStream fileInputStream) {
      this.fileInputStream = fileInputStream;
    }

    public void setExclusive(boolean isExclusive) {
      try {
        Field f = FileDescriptor.class.getDeclaredField("fd");
        if (f != null) {
          f.setAccessible(true);
          Integer fd = (Integer) f.get(fileInputStream.getFD());
          if (fd != null) {
            // See EVIOCGRAB in drivers/input/evdev.c
            int value = isExclusive ? 1 : 0;
            System.out.println("DeviceReader.setExclusive: value=" + value);
            FluidSynth.capture(fd, value);
          }
        }
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static final int EV_KEY = 0x01;

  private String deviceName;
  private Thread deviceReader;
  private boolean isTerminated;
  private EventDeviceGrabber eventDeviceGrabber;
  private BlockingQueue<Message> handlerQueue;

  public DeviceReader(BlockingQueue<Message> handlerQueue, String deviceName) {
    this.handlerQueue = handlerQueue;
    this.deviceName = deviceName;
  }

  public void setExclusive(boolean isExclusive) {
    eventDeviceGrabber.setExclusive(isExclusive);
  }

  public void start() {
    deviceReader = new Thread(() -> run(), deviceName);
    deviceReader.start();
  }

  public void terminate() {
    isTerminated = true;
  }

  private void run() {
    try (FileInputStream fileInputStream = new FileInputStream(deviceName)) {
      eventDeviceGrabber = new EventDeviceGrabber(fileInputStream);
      eventDeviceGrabber.setExclusive(true);
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
              handlerQueue.add(new OnKeyRelease(code));
            } else if (value == 1) {
              short code = ByteArray.toNativeShort(buffer, 10);
              handlerQueue.add(new OnKeyPress(code));
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

}
