// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.device.midi.configuration.Context;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendDeviceMessage;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.device.midi.configuration.On;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class MidiWriter extends BrokerTask<Message> implements HasSendDeviceMessage {

  private int deviceIndex;
  private Context context;
  private MidiDeviceBundle deviceBundle;
  private MidiConfiguration configuration;
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private Song song;

  public MidiWriter(Broker<Message> broker, MidiDeviceBundle deviceBundle, MidiConfiguration configuration, int deviceIndex) {
    super(broker);
    this.deviceBundle = deviceBundle;
    this.configuration = configuration;
    this.deviceIndex = deviceIndex;
    this.context = configuration.getContext();
    context.setHasSendDeviceMessage(this);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
    subscribe(OnDeviceMessage.class, message -> doDeviceMessage(message));
    subscribe(OnSong.class, message -> doSong(message));
    connectDevices();
  }

  @Override
  public void sendDeviceMessage(int port, int command, int channel, int data1, int data2) {
    try {
      ShortMessage shortMessage = new ShortMessage(command, channel, data1, data2);
      receivers.get(port).send(shortMessage, -1);
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void start() {
    super.start();
    initializeDevice();
  }

  @Override
  public void terminate() {
    super.terminate();
    disconnectDevices();
  }

  @Override
  public String toString() {
    return "MidiWriter [type=" + deviceBundle.getType() + ", card=" + deviceBundle.getCard() + ", unit=" + deviceBundle.getUnit() + "]";
  }

  private void connectDevices() {
    try {
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
    for (MidiOutputDevice midiOutputDevice : deviceBundle.getOutputDevices()) {
      MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private void doCommand(OnCommand message) {
    context.set("command", message.getCommand());
    context.set("parameter", message.getParameter());
    On onCommand = configuration.getOn(MidiConfiguration.COMMAND);
    if (onCommand != null) {
      onCommand.execute(context);
    }
  }

  private void doDeviceCommand(OnDeviceCommand message) {
    if (message.getDeviceIndex() == deviceIndex) {
      switch (message.getDeviceCommand()) {
      case CHANNEL:
        updateChannelState(message.getParameter());
        break;
      default:
        break;
      }
    }
  }

  private void doDeviceMessage(OnDeviceMessage message) {
    int port = message.getPort();
    int command = message.getCommand();
    int channel = message.getChannel();
    int data1 = message.getData1();
    int data2 = message.getData2();
    sendDeviceMessage(port, command, channel, data1, data2);
  }

  private void doSong(OnSong message) {
    this.song = message.getSong();
    updateChannelState(ChannelNotes.ALL_CHANNELS);
  }

  private void initializeDevice() {
    On onInitialization = configuration.getOn(MidiConfiguration.INITIALIZATION);
    if (onInitialization != null) {
      onInitialization.execute(context);
    }
  }

  private void setChannelState(int channel, ChannelState channelState) {
    int channelNumber = Value.toNumber(channel);
    context.setStatusChannel(channelNumber);
    context.setChannelState(channelState);
    On onChannelStatus = configuration.getOn(MidiConfiguration.CHANNEL_STATUS);
    if (onChannelStatus != null) {
      onChannelStatus.execute(context);
    }
  }

  private void updateChannelState(int assignedChannel) {
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      boolean isChannelActive = song != null && song.getChannelNoteCount(channel) > 0;
      ChannelState channelState;
      if (channel == assignedChannel) {
        channelState = ChannelState.SELECTED;
      } else {
        if (isChannelActive) {
          channelState = ChannelState.INACTIVE;
        } else {
          channelState = ChannelState.ACTIVE;
        }
      }
      setChannelState(channel, channelState);
    }
  }

}
