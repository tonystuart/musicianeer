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
import java.util.List;
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
import com.example.afs.musicpad.message.OnDeviceMessages;
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
    performActions(configuration.getInitializationActions());
  }

  private boolean modesMatch(List<Integer> ifModes) {
    if (ifModes == null) {
      return true;
    }
    for (Integer mode : ifModes) {
      if (!currentModes.contains(mode)) {
        return false;
      }
    }
    return true;
  }

  private boolean notModesMatch(List<Integer> ifNotModes) {
    if (ifNotModes == null) {
      return true;
    }
    for (Integer mode : ifNotModes) {
      if (currentModes.contains(mode)) {
        return false;
      }
    }
    return true;
  }

  private void performActions(Action action) {
    List<ChannelMessage> deviceMessages = action.getSendDeviceMessages();
    if (deviceMessages != null) {
      broker.publish(new OnDeviceMessages(deviceMessages));
    }
    List<ChannelMessage> handlerMessages = action.getSendHandlerMessages();
    if (handlerMessages != null) {
      sendHandlerMessages(handlerMessages, true);
    }
    List<HandlerCommand> handlerCommands = action.getSendHandlerCommands();
    if (handlerCommands != null) {
      sendHandlerCommands(handlerCommands);
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
            if (modesMatch(inputAction.getIfModes())) {
              if (notModesMatch(inputAction.getIfNotModes())) {
                Action action = inputAction.getThenDo();
                if (action.getSetMode() != null) {
                  currentModes.add(action.getSetMode());
                }
                if (action.getClearMode() != null) {
                  currentModes.remove(action.getClearMode());
                }
                performActions(action);
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

  private void sendHandlerCommands(List<HandlerCommand> handlerCommands) {
    for (HandlerCommand handlerCommand : handlerCommands) {
      queue.add(new OnCommand(handlerCommand.getCommand(), handlerCommand.getParameter()));
    }
  }

  private void sendHandlerMessages(List<ChannelMessage> handlerMessages, boolean isPress) {
    for (ChannelMessage channelMessage : handlerMessages) {
      int data1 = Value.getInt(channelMessage.getData1());
      if (isPress) {
        queue.add(new OnInputPress(data1));
      } else {
        queue.add(new OnInputRelease(data1));
      }
    }
  }

}
