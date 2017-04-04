// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.DeviceGroup.DeviceInterface;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiReader implements DeviceInterface {

  private class MidiReceiver implements Receiver {

    private int subdevice;

    public MidiReceiver(int subdevice) {
      this.subdevice = subdevice;
    }

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage message, long timestamp) {
      receiveFromDevice(message, timestamp, subdevice);
    }
  }

  private BlockingQueue<Message> queue;
  private MidiDeviceBundle device;
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private Properties properties;

  public MidiReader(BlockingQueue<Message> queue, MidiDeviceBundle device) {
    this.queue = queue;
    this.device = device;
    readProperties();
    connectDevices();
    initializeDevices();
  }

  public void sendToDevice(MidiMessage message, long timestamp) {
    for (Receiver receiver : receivers) {
      receiver.send(message, timestamp);
    }
  }

  @Override
  public void start() {
  }

  @Override
  public void terminate() {
  }

  private void connectDevices() {
    try {
      for (MidiInputDevice midiInputDevice : device.getInputDevices()) {
        MidiDevice midiDevice = midiInputDevice.getMidiDevice();
        midiDevice.open();
        midiDevice.getTransmitter().setReceiver(new MidiReceiver(midiInputDevice.getSubdevice()));
      }
      for (MidiOutputDevice midiOutputDevice : device.getOutputDevices()) {
        MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
        midiDevice.open();
        receivers.add(midiDevice.getReceiver());
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void initializeDevices() {
    String property = properties.getProperty("initialize.output");
    try {
      ShortMessage shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, 0x0c, 0x7f);
      sendToDevice(shortMessage, -1);
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void readProperties() {
    properties = new Properties();
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(device.getType() + ".properties")) {
      if (inputStream != null) {
        properties.load(inputStream);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void receiveFromDevice(MidiMessage message, long timestamp, int subdevice) {
    if (message instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) message;
      int type = shortMessage.getCommand();
      int control = shortMessage.getData1();
      if (type == ShortMessage.NOTE_ON) {
        queue.add(new OnInputPress(control));
      } else if (type == ShortMessage.NOTE_OFF) {
        queue.add(new OnInputRelease(control));
      }
    }
  }

}
