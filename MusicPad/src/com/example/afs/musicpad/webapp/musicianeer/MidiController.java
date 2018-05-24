// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.midi.MidiDeviceBundle;
import com.example.afs.musicpad.device.midi.MidiInputDevice;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;

public class MidiController extends MessageTask {

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
      tsReceiveFromDevice(message, timestamp, port);
    }
  }

  private int channel;
  private int deviceIndex;

  private String deviceName;
  private MidiDeviceBundle deviceBundle;

  public MidiController(MessageBroker messageBroker, String deviceName, MidiDeviceBundle deviceBundle, int deviceIndex) {
    super(messageBroker);
    this.deviceName = deviceName;
    this.deviceBundle = deviceBundle;
    this.deviceIndex = deviceIndex;
    subscribe(OnMidiInputSelected.class, message -> doMidiInputSelected(message));
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceIndex(int deviceIndex) {
    this.deviceIndex = deviceIndex;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  @Override
  public String toString() {
    return "MidiController [channel=" + channel + ", deviceIndex=" + deviceIndex + ", deviceName=" + deviceName + ", deviceBundle=" + deviceBundle + "]";
  }

  @Override
  public synchronized void tsStart() {
    super.tsStart();
    connectDevices();
  }

  @Override
  public synchronized void tsTerminate() {
    disconnectDevices();
    super.tsTerminate();
  }

  private void connectDevices() {
    try {
      System.out.println("MidiController.connectDevices, deviceName=" + deviceName);
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
    System.out.println("MidiController.disconnectDevices, deviceName=" + deviceName);
    for (MidiInputDevice midiInputDevice : deviceBundle.getInputDevices()) {
      MidiDevice midiDevice = midiInputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private void doMidiInputSelected(OnMidiInputSelected message) {
    if (message.getDeviceIndex() == deviceIndex) {
      channel = message.getChannel();
    }
  }

  private void processUnmappedMessage(ShortMessage shortMessage) {
    int command = shortMessage.getCommand();
    int data1 = shortMessage.getData1();
    int data2 = shortMessage.getData2();
    if (command == ShortMessage.NOTE_ON) {
      publish(new OnNoteOn(channel, data1, data2));
    } else if (command == ShortMessage.NOTE_OFF) {
      publish(new OnNoteOff(channel, data1));
    } else if (command == ShortMessage.POLY_PRESSURE) {
      publish(new OnChannelPressure(data1, data2));
    } else if (command == ShortMessage.CONTROL_CHANGE) {
      int control = data1;
      int value = data2;
      publish(new OnControlChange(channel, control, value));
    } else if (command == ShortMessage.PITCH_BEND) {
      // Pitch bend is reported as a signed 14 bit value with MSB in data2 and LSB in data1
      // Options for converting it into values in the range 0 to 16384 include:
      // 1. Use LS(32-14) to set the sign and RS(32-14) to extend the size to produce values in range -8192 to 8192, then add 8192 to get values in range 0 to 16384
      // 2. Recognize that values GT 8192 have their sign bit set, subtract 8192 from them and add 8192 to values LT 8192 to get values in range 0 to 16384
      // We use the second approach
      int value = (data2 << 7) | data1;
      int pitchBend = value >= 8192 ? value - 8192 : value + 8192;
      publish(new OnPitchBend(channel, pitchBend));
    }
  }

  private void tsReceiveFromDevice(MidiMessage message, long timestamp, int port) {
    try {
      if (message instanceof ShortMessage) {
        ShortMessage shortMessage = (ShortMessage) message;
        processUnmappedMessage(shortMessage);
      } else {
        System.out.println("MidiReader.receiveFromDevice: message=" + message);
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.err.println("Ignoring exception");
    }
  }

}
