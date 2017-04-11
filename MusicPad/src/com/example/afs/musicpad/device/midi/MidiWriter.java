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

import com.example.afs.musicpad.device.common.ControllableGroup.Controllable;
import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.device.midi.configuration.Context;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendDeviceMessage;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class MidiWriter extends BrokerTask<Message> implements Controllable, HasSendDeviceMessage {

  private MidiDeviceBundle device;
  private MidiConfiguration configuration;
  private Context context = new Context();
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private ChannelState[] channelStates = new ChannelState[Midi.CHANNELS];
  private int selectedChannel = -1;

  public MidiWriter(Broker<Message> broker, MidiDeviceBundle device, MidiConfiguration configuration) {
    super(broker);
    this.device = device;
    this.configuration = configuration;
    context.setHasSendDeviceMessage(this);
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
    if (channelState == ChannelState.SELECTED) {
      if (selectedChannel != -1) {
        setChannelState(selectedChannel, channelStates[selectedChannel]);
      }
      selectedChannel = channel;
    } else {
      channelStates[channel] = channelState;
    }
    setChannelState(channel, channelState);
  }

  private void initializeDevice() {
    configuration.getOnInitialization().execute(context);
  }

  private void setChannelState(int channel, ChannelState channelState) {
    int channelNumber = Value.toNumber(channel);
    context.setChannel(channelNumber);
    context.setChannelState(channelState);
    configuration.getOnChannelStatus().execute(context);
  }

}
