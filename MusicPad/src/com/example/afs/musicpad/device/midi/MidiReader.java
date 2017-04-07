// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.ControllableGroup.Controllable;
import com.example.afs.musicpad.device.midi.MidiConfiguration.Action;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;
import com.example.afs.musicpad.device.midi.MidiConfiguration.HandlerCommand;
import com.example.afs.musicpad.device.midi.MidiConfiguration.InputAction;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Value;

public class MidiReader implements Controllable {

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

  private Broker<Message> broker;
  private MidiDeviceBundle device;
  private BlockingQueue<Message> queue;
  private MidiConfiguration configuration;
  private Set<Integer> currentModes = new HashSet<>();

  public MidiReader(Broker<Message> broker, BlockingQueue<Message> queue, MidiDeviceBundle device, MidiConfiguration configuration) {
    this.broker = broker;
    this.queue = queue;
    this.device = device;
    this.configuration = configuration;
    connectDevices();
    initializeDevices();
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
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void initializeDevices() {
    for (Action action : configuration.getInitializationActions()) {
      performAction(action);
    }
  }

  private void performAction(Action action) {
    Integer setMode = action.getSetMode();
    if (setMode != null) {
      currentModes.add(setMode);
    }
    Integer clearMode = action.getClearMode();
    if (clearMode != null) {
      currentModes.remove(clearMode);
    }
    ChannelMessage deviceMessage = action.getSendDeviceMessage();
    if (deviceMessage != null) {
      broker.publish(new OnDeviceMessage(deviceMessage));
    }
    ChannelMessage handlerMessage = action.getSendHandlerMessage();
    if (handlerMessage != null) {
      int data1 = Value.getInt(handlerMessage.getData1());
      queue.add(new OnInputPress(data1));
    }
    HandlerCommand handlerCommand = action.getSendHandlerCommand();
    if (handlerCommand != null) {
      queue.add(new OnCommand(handlerCommand.getCommand(), handlerCommand.getParameter()));
    }
  }

  private void receiveFromDevice(MidiMessage message, long timestamp, int subDevice) {
    if (message instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) message;
      int command = shortMessage.getCommand();
      int channel = shortMessage.getChannel();
      int data1 = shortMessage.getData1();
      int data2 = shortMessage.getData2();
      for (InputAction inputAction : configuration.getInputActions()) {
        if (inputAction != null) {
          if (inputAction.equals(subDevice, command, channel, data1, data2)) {
            if (inputAction.getIfMode() == null || currentModes.contains(inputAction.getIfMode())) {
              if (inputAction.getIfNotMode() == null || !currentModes.contains(inputAction.getIfNotMode())) {
                Action action = inputAction.getThenDo();
                performAction(action);
                return;
              }
            }
          }
        }
      }
      if (command == ShortMessage.NOTE_ON) {
        queue.add(new OnInputPress(data1));
      } else if (command == ShortMessage.NOTE_OFF) {
        queue.add(new OnInputRelease(data1));
      }
    }
  }

}
