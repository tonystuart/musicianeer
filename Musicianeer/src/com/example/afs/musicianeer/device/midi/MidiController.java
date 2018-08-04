// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.device.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicianeer.main.MusicianeerController;
import com.example.afs.musicianeer.main.OnSetDemoMode;
import com.example.afs.musicianeer.message.OnChannelPressure;
import com.example.afs.musicianeer.message.OnControlChange;
import com.example.afs.musicianeer.message.OnMidiInputSelected;
import com.example.afs.musicianeer.message.OnMidiOutputSelected;
import com.example.afs.musicianeer.message.OnNoteOff;
import com.example.afs.musicianeer.message.OnNoteOn;
import com.example.afs.musicianeer.message.OnPitchBend;
import com.example.afs.musicianeer.message.OnResetMidiNoteLeds;
import com.example.afs.musicianeer.message.OnSetChannelVolume;
import com.example.afs.musicianeer.message.OnSetMidiNoteLed;
import com.example.afs.musicianeer.message.OnTransportNoteOff;
import com.example.afs.musicianeer.message.OnTransportNoteOn;
import com.example.afs.musicianeer.midi.Midi;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.MessageTask;
import com.example.afs.musicianeer.util.DirectList;
import com.example.afs.musicianeer.util.RandomAccessList;
import com.example.afs.musicianeer.util.Range;

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

  private static final int OFF = 0;
  private static final int LOW = 1;
  private static final int HIGH = Midi.MAX_VALUE;

  private int deviceIndex;
  private int inputChannel;
  private int outputChannel;
  private boolean isDemoMode;
  private int channelVelocity = MusicianeerController.DEFAULT_VELOCITY;

  private String deviceName;
  private MidiDeviceBundle deviceBundle;
  private RandomAccessList<Receiver> receivers = new DirectList<>();

  public MidiController(MessageBroker messageBroker, String deviceName, MidiDeviceBundle deviceBundle, int deviceIndex) {
    super(messageBroker);
    this.deviceName = deviceName;
    this.deviceBundle = deviceBundle;
    this.deviceIndex = deviceIndex;
    subscribe(OnSetDemoMode.class, message -> doSetDemoMode(message));
    subscribe(OnSetMidiNoteLed.class, message -> doSetMidiNoteLed(message));
    subscribe(OnSetChannelVolume.class, message -> doSetChannelVolume(message));
    subscribe(OnMidiInputSelected.class, message -> doMidiInputSelected(message));
    subscribe(OnMidiOutputSelected.class, message -> doMidiOutputSelected(message));
    subscribe(OnResetMidiNoteLeds.class, message -> doResetMidiNoteLeds(message));
    subscribe(OnTransportNoteOn.class, message -> doTransportNoteOn(message));
    subscribe(OnTransportNoteOff.class, message -> doTransportNoteOff(message));
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public boolean isInputCapable() {
    return deviceBundle.getInputDevices().size() > 0;
  }

  public boolean isOutputCapable() {
    return deviceBundle.getOutputDevices().size() > 0;
  }

  public void setDeviceIndex(int deviceIndex) {
    this.deviceIndex = deviceIndex;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  @Override
  public String toString() {
    return "MidiController [deviceIndex=" + deviceIndex + ", inputChannel=" + inputChannel + ", outputChannel=" + outputChannel + ", channelVelocity=" + channelVelocity + ", deviceName=" + deviceName + ", deviceBundle=" + deviceBundle + "]";
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
      for (MidiOutputDevice midiOutputDevice : deviceBundle.getOutputDevices()) {
        MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
        midiDevice.open();
        receivers.add(midiDevice.getReceiver());
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
      inputChannel = message.getChannel();
    }
  }

  private void doMidiOutputSelected(OnMidiOutputSelected message) {
    if (message.getDeviceIndex() == deviceIndex) {
      resetMidiNoteLeds();
      outputChannel = message.getChannel();
    }
  }

  private void doResetMidiNoteLeds(OnResetMidiNoteLeds message) {
    resetMidiNoteLeds();
  }

  private void doSetChannelVolume(OnSetChannelVolume message) {
    if (message.getChannel() == inputChannel) {
      channelVelocity = message.getVolume();
    }
  }

  private void doSetDemoMode(OnSetDemoMode message) {
    resetMidiNoteLeds();
    isDemoMode = message.isDemoMode();
  }

  private void doSetMidiNoteLed(OnSetMidiNoteLed message) {
    try {
      if (!isDemoMode && (message.getChannel() == outputChannel)) {
        int velocity;
        switch (message.getState()) {
        case HIGH:
          velocity = HIGH;
          break;
        case LOW:
          velocity = LOW;
          break;
        case OFF:
          velocity = OFF;
          break;
        default:
          throw new UnsupportedOperationException();
        }
        ShortMessage shortMessage = new ShortMessage(ShortMessage.NOTE_ON, message.getChannel(), message.getMidiNote(), velocity);
        send(shortMessage);
      }
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void doTransportNoteOff(OnTransportNoteOff message) {
    try {
      if (isDemoMode) {
        ShortMessage shortMessage = new ShortMessage(ShortMessage.NOTE_OFF, message.getChannel(), message.getMidiNote(), OFF);
        send(shortMessage);
      }
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void doTransportNoteOn(OnTransportNoteOn message) {
    try {
      if (isDemoMode) {
        ShortMessage shortMessage = new ShortMessage(ShortMessage.NOTE_ON, message.getChannel(), message.getMidiNote(), HIGH);
        send(shortMessage);
      }
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void processUnmappedMessage(ShortMessage shortMessage) {
    int command = shortMessage.getCommand();
    int data1 = shortMessage.getData1();
    int data2 = shortMessage.getData2();
    if (command == ShortMessage.NOTE_ON) {
      int velocity = Range.scale(channelVelocity, Math.min(Midi.MAX_VALUE, channelVelocity + 32), 0, Midi.MAX_VALUE, data2);
      publish(new OnNoteOn(inputChannel, data1, velocity));
    } else if (command == ShortMessage.NOTE_OFF) {
      publish(new OnNoteOff(inputChannel, data1));
    } else if (command == ShortMessage.POLY_PRESSURE) {
      publish(new OnChannelPressure(data1, data2));
    } else if (command == ShortMessage.CONTROL_CHANGE) {
      int control = data1;
      int value = data2;
      publish(new OnControlChange(inputChannel, control, value));
    } else if (command == ShortMessage.PITCH_BEND) {
      // Pitch bend is reported as a signed 14 bit value with MSB in data2 and LSB in data1
      // Options for converting it into values in the range 0 to 16384 include:
      // 1. Use LS(32-14) to set the sign and RS(32-14) to extend the size to produce values in range -8192 to 8192, then add 8192 to get values in range 0 to 16384
      // 2. Recognize that values GT 8192 have their sign bit set, subtract 8192 from them and add 8192 to values LT 8192 to get values in range 0 to 16384
      // We use the second approach
      int value = (data2 << 7) | data1;
      int pitchBend = value >= 8192 ? value - 8192 : value + 8192;
      publish(new OnPitchBend(inputChannel, pitchBend));
    }
  }

  private void resetMidiNoteLeds() {
    try {
      for (int midiNote = 0; midiNote < Midi.MAX_VALUE; midiNote++) {
        send(new ShortMessage(ShortMessage.NOTE_OFF, 0, midiNote, 0));
      }
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void send(Receiver receiver, ShortMessage shortMessage) {
    receiver.send(shortMessage, -1);
  }

  private void send(ShortMessage shortMessage) {
    receivers.forEach(receiver -> send(receiver, shortMessage));
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
