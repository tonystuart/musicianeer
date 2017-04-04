// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.DeviceGroup.DeviceInterface;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ConfigurationMessage;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.JsonUtilities;
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

  private MidiDeviceBundle device;
  private BlockingQueue<Message> queue;
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private MidiConfiguration configuration;

  public MidiReader(BlockingQueue<Message> queue, MidiDeviceBundle device) {
    this.queue = queue;
    this.device = device;
    this.configuration = readConfiguration();
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
    for (ConfigurationMessage message : configuration.getInitializers()) {
      try {
        int command = message.getCommand();
        int channel = message.getChannel();
        int data1 = message.getData1();
        int data2 = message.getData2();
        ShortMessage shortMessage = new ShortMessage(command, channel, data1, data2);
        sendToDevice(shortMessage, -1);
      } catch (InvalidMidiDataException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private MidiConfiguration readConfiguration() {
    String home = System.getProperty("user.home");
    String fileName = device.getType() + ".configuration";
    String overridePathName = home + File.separatorChar + ".musicpad" + File.separatorChar + fileName;
    File configurationFile = new File(overridePathName);
    if (configurationFile.isFile() && configurationFile.canRead()) {
      MidiConfiguration configuration = FileUtilities.readJson(overridePathName, MidiConfiguration.class);
      return configuration;
    }
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (inputStream != null) {
      String contents = FileUtilities.read(inputStream);
      MidiConfiguration configuration = JsonUtilities.fromJson(contents, MidiConfiguration.class);
      return configuration;
    }
    return new MidiConfiguration();
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
