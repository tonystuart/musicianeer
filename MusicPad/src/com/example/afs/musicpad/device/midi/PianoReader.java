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
import com.example.afs.musicpad.device.midi.PianoWatcher.Device;
import com.example.afs.musicpad.device.midi.PianoWatcher.InputDevice;
import com.example.afs.musicpad.device.midi.PianoWatcher.OutputDevice;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class PianoReader implements DeviceInterface {

  public class PianoInput implements Receiver {

    @Override
    public void close() {
    }

    @Override
    public void send(MidiMessage message, long timestamp) {
      receiveFromPiano(message, timestamp);
    }

  }

  private PianoInput pianoInput = new PianoInput();
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private BlockingQueue<Message> queue;

  public PianoReader(BlockingQueue<Message> queue, Device device) {
    this.queue = queue;
    try {
      for (InputDevice inputDevice : device.getInputDevices()) {
        MidiDevice midiInputDevice = inputDevice.getMidiDevice();
        midiInputDevice.open();
        midiInputDevice.getTransmitter().setReceiver(pianoInput);
      }
      for (OutputDevice outputDevice : device.getOutputDevices()) {
        MidiDevice midiOutputDevice = outputDevice.getMidiDevice();
        midiOutputDevice.open();
        receivers.add(midiOutputDevice.getReceiver());
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendToPiano(MidiMessage message, long timestamp) {
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

  private void receiveFromPiano(MidiMessage message, long timestamp) {
    if (message instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) message;
      int type = shortMessage.getCommand();
      int control = shortMessage.getData1();
      int value = shortMessage.getData2();
      if (type == ShortMessage.NOTE_ON) {
        queue.add(new OnInputPress(control));
      } else if (type == ShortMessage.NOTE_OFF) {
        queue.add(new OnInputRelease(control));
      }
    }
  }

}
