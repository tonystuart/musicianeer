// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.concurrent.BlockingQueue;

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

  public class DeviceReceiver implements Receiver {

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage message, long timestamp) {
      receiveFromDevice(message, timestamp);
    }

  }

  private BlockingQueue<Message> queue;
  private DeviceReceiver deviceInput = new DeviceReceiver();
  private RandomAccessList<Receiver> receivers = new DirectList<>();

  public MidiReader(BlockingQueue<Message> queue, MidiDeviceBundle device) {
    this.queue = queue;
    try {
      for (MidiInputDevice inputDevice : device.getInputDevices()) {
        MidiDevice midiInputDevice = inputDevice.getMidiDevice();
        midiInputDevice.open();
        midiInputDevice.getTransmitter().setReceiver(deviceInput);
      }
      for (MidiOutputDevice outputDevice : device.getOutputDevices()) {
        MidiDevice midiOutputDevice = outputDevice.getMidiDevice();
        midiOutputDevice.open();
        receivers.add(midiOutputDevice.getReceiver());
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
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

  private void receiveFromDevice(MidiMessage message, long timestamp) {
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
