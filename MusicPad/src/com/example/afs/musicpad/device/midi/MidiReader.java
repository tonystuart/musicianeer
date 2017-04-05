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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.DeviceGroup.DeviceInterface;
import com.example.afs.musicpad.device.midi.MidiConfiguration.Action;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;
import com.example.afs.musicpad.device.midi.MidiConfiguration.Command;
import com.example.afs.musicpad.device.midi.MidiConfiguration.Input;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
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

  private Set<Integer> currentShifts = new HashSet<>();
  private Set<Integer> currentModes = new HashSet<>();

  public MidiReader(BlockingQueue<Message> queue, MidiDeviceBundle device) {
    this.queue = queue;
    this.device = device;
    this.configuration = readConfiguration();
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
      for (MidiOutputDevice midiOutputDevice : device.getOutputDevices()) {
        MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
        midiDevice.open();
        receivers.add(midiDevice.getReceiver());
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private int getInt(Integer value) {
    return getInt(value, 0);
  }

  private int getInt(Integer value, int defaultValue) {
    return value == null ? defaultValue : value;
  }

  private void initializeDevices() {
    sendDeviceMessages(configuration.getInitializers());
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

  private void receiveFromDevice(MidiMessage message, long timestamp, int subDevice) {
    if (message instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) message;
      int command = shortMessage.getCommand();
      int channel = shortMessage.getChannel();
      int data1 = shortMessage.getData1();
      int data2 = shortMessage.getData2();
      for (Input input : configuration.getInputs()) {
        if (input.equals(subDevice, command, channel, data1, data2)) {
          if (input.getAndIfShift() == null || currentShifts.contains(input.getAndIfShift())) {
            if (input.getAndIfMode() == null || currentModes.contains(input.getAndIfMode())) {
              Action action;
              if (command == ShortMessage.NOTE_ON) {
                action = input.getOnPress();
              } else {
                action = input.getOnRelease();
              }
              if (action.getSetShift() != null) {
                currentShifts.add(action.getSetShift());
              }
              if (action.getClearShift() != null) {
                currentShifts.remove(action.getClearShift());
              }
              if (action.getSetMode() != null) {
                currentModes.add(action.getSetMode());
              }
              if (action.getClearMode() != null) {
                currentModes.remove(action.getClearMode());
              }
              sendDeviceMessages(action.getSendDeviceMessages());
              sendHandlerMessages(action.getSendHandlerMessages(), true);
              sendHandlerCommands(action.getSendHandlerCommands());
              return; // TODO: Rethink this...
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

  private void sendDeviceMessages(List<ChannelMessage> deviceMessages) {
    for (ChannelMessage channelMessage : deviceMessages) {
      try {
        int command = channelMessage.getCommand();
        int channel = channelMessage.getChannel();
        int data1 = getInt(channelMessage.getData1());
        int data2 = getInt(channelMessage.getData2());
        ShortMessage shortMessage = new ShortMessage(command, channel, data1, data2);
        if (channelMessage.getSubDevice() == null) {
          for (Receiver receiver : receivers) {
            receiver.send(shortMessage, -1);
          }
        } else {
          receivers.get(channelMessage.getSubDevice()).send(shortMessage, -1);
        }
      } catch (InvalidMidiDataException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void sendHandlerCommands(List<Command> handlerCommands) {
    for (Command command : handlerCommands) {
      queue.add(new OnCommand(com.example.afs.musicpad.Command.values()[command.getCommand()], command.getParameter()));
    }
  }

  @SuppressWarnings("unused")
  private void sendHandlerMessages(List<ChannelMessage> handlerMessages, boolean isPress) {
    for (ChannelMessage channelMessage : handlerMessages) {
      int command = channelMessage.getCommand();
      int channel = channelMessage.getChannel();
      int data1 = getInt(channelMessage.getData1());
      int data2 = getInt(channelMessage.getData2());
      // ShortMessage shortMessage = new ShortMessage(command, channel, data1, data2);
      if (isPress) {
        queue.add(new OnInputPress(data1));
      } else {
        queue.add(new OnInputRelease(data1));
      }
    }
  }

}
