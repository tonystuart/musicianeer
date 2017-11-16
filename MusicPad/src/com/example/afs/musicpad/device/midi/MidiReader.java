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

import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
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
  private Player player;
  private int deviceIndex;

  public MidiReader(MessageBroker broker, Player player, int deviceIndex, MidiDeviceBundle deviceBundle, MidiConfiguration configuration) {
    this.broker = broker;
    this.player = player;
    this.deviceIndex = deviceIndex;
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
        System.out.println("MidiReader.receiveFromDevice: message=" + formatMessage(message) + ", command=" + shortMessage.getCommand() + ", channel=" + shortMessage.getChannel() + ", data1=" + shortMessage.getData1() + ", data2=" + shortMessage.getData2());
        if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
          player.play(Action.PRESS, shortMessage.getData1());
        } else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
          player.play(Action.RELEASE, shortMessage.getData1());
        } else if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
          int control = shortMessage.getData1();
          int value = shortMessage.getData2();
          player.changeControl(control, value);
        } else if (shortMessage.getCommand() == ShortMessage.PITCH_BEND) {
          // Pitch bend is reported as a signed 14 bit value with MSB in data2 and LSB in data1
          // Options for converting it into values in the range 0 to 16384 include:
          // 1. Use LS(32-14) to set the sign and RS(32-14) to extend the size to produce values in range -8192 to 8192, then add 8192 to get values in range 0 to 16384
          // 2. Recognize that values GT 8192 have their sign bit set, subtract 8192 from them and add 8192 to values LT 8192 to get values in range 0 to 16384
          // We use the second approach
          int value = (shortMessage.getData2() << 7) | shortMessage.getData1();
          int pitchBend = value >= 8192 ? value - 8192 : value + 8192;
          player.bendPitch(pitchBend);
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
