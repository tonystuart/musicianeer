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

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.device.midi.configuration.Context;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendDeviceMessage;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.device.midi.configuration.On;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class MidiWriter extends BrokerTask<Message> implements HasSendDeviceMessage {

  private Context context;
  private MidiDeviceBundle device;
  private MidiConfiguration configuration;
  private RandomAccessList<Receiver> receivers = new DirectList<>();

  public MidiWriter(Broker<Message> broker, MidiDeviceBundle device, MidiConfiguration configuration) {
    super(broker);
    this.device = device;
    this.configuration = configuration;
    this.context = configuration.getContext();
    context.setHasSendDeviceMessage(this);
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    subscribe(OnChannelState.class, message -> doChannelState(message.getChannel(), message.getChannelState()));
    subscribe(OnDeviceMessage.class, message -> sendDeviceMessage(message.getPort(), message.getCommand(), message.getChannel(), message.getData1(), message.getData2()));
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
    return "MidiWriter [type=" + device.getType() + ", card=" + device.getCard() + ", unit=" + device.getUnit() + "]";
  }

  private void connectDevices() {
    try {
      for (MidiOutputDevice midiOutputDevice : device.getOutputDevices()) {
        MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
        midiDevice.open();
        receivers.add(midiDevice.getReceiver());
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void disconnectDevices() {
    for (MidiOutputDevice midiOutputDevice : device.getOutputDevices()) {
      MidiDevice midiDevice = midiOutputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private void doChannelState(int channel, ChannelState channelState) {
    setChannelState(channel, channelState);
  }

  private void doCommand(Command command, int parameter) {
    context.set("command", command);
    context.set("parameter", parameter);
    On onCommand = configuration.getOn(MidiConfiguration.COMMAND);
    if (onCommand != null) {
      onCommand.execute(context);
    }
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

}
