// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.task.MessageBroker;

public class MidiReader {

  private class MidiReceiver implements Receiver {

    private int port;

    public MidiReceiver(int port) {
      this.port = port;
    }

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage message, long timestamp) {
      receiveFromDevice(message, timestamp, port);
    }
  }

  private MessageBroker broker;
  private MidiDeviceBundle deviceBundle;
  private MidiConfiguration configuration;
  private DeviceHandler deviceHandler;

  public MidiReader(MessageBroker broker, DeviceHandler deviceHandler, MidiDeviceBundle deviceBundle, MidiConfiguration configuration) {
    this.broker = broker;
    this.deviceHandler = deviceHandler;
    this.deviceBundle = deviceBundle;
    this.configuration = configuration;
    connectDevices();
  }

  public void start() {
  }

  public void terminate() {
    disconnectDevices();
  }

  @Override
  public String toString() {
    return "MidiReader [type=" + deviceBundle.getType() + ", card=" + deviceBundle.getCard() + ", unit=" + deviceBundle.getUnit() + "]";
  }

  private void connectDevices() {
    try {
      for (MidiInputDevice midiInputDevice : deviceBundle.getInputDevices()) {
        MidiDevice midiDevice = midiInputDevice.getMidiDevice();
        midiDevice.open();
        midiDevice.getTransmitter().setReceiver(new MidiReceiver(midiInputDevice.getPort()));
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void disconnectDevices() {
    for (MidiInputDevice midiInputDevice : deviceBundle.getInputDevices()) {
      MidiDevice midiDevice = midiInputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private String formatMessage(MidiMessage message) {
    StringBuilder s = new StringBuilder();
    for (byte b : message.getMessage()) {
      if (s.length() > 0) {
        s.append(" ");
      }
      s.append(String.format("%02x", b));
    }
    return s.toString();
  }

  private void receiveFromDevice(MidiMessage message, long timestamp, int port) {
    try {
      if (message instanceof ShortMessage) {
        ShortMessage shortMessage = (ShortMessage) message;
        int command = shortMessage.getCommand();
        int data1 = shortMessage.getData1();
        int data2 = shortMessage.getData2();
        System.out.println("MidiReader.receiveFromDevice: message=" + formatMessage(message) + ", command=" + command + ", channel=" + shortMessage.getChannel() + ", data1=" + data1 + ", data2=" + data2);
        if (command == ShortMessage.NOTE_ON) {
          deviceHandler.onDown(data1, data2);
        } else if (command == ShortMessage.NOTE_OFF) {
          deviceHandler.onUp(data1);
        } else if (command == ShortMessage.POLY_PRESSURE) {
          deviceHandler.onChannelPressure(data1, data2);
        } else if (command == ShortMessage.CONTROL_CHANGE) {
          int control = data1;
          int value = data2;
          deviceHandler.getPlayer().changeControl(control, value);
        } else if (command == ShortMessage.PITCH_BEND) {
          // Pitch bend is reported as a signed 14 bit value with MSB in data2 and LSB in data1
          // Options for converting it into values in the range 0 to 16384 include:
          // 1. Use LS(32-14) to set the sign and RS(32-14) to extend the size to produce values in range -8192 to 8192, then add 8192 to get values in range 0 to 16384
          // 2. Recognize that values GT 8192 have their sign bit set, subtract 8192 from them and add 8192 to values LT 8192 to get values in range 0 to 16384
          // We use the second approach
          int value = (data2 << 7) | data1;
          int pitchBend = value >= 8192 ? value - 8192 : value + 8192;
          deviceHandler.getPlayer().bendPitch(pitchBend);
        }
      } else {
        System.out.println("MidiReader.receiveFromDevice: message=" + formatMessage(message));
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.err.println("Ignoring exception");
    }
  }

}
