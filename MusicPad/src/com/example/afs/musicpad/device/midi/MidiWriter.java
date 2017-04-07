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
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelState;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelStatus;
import com.example.afs.musicpad.device.midi.MidiConfiguration.OutputAction;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class MidiWriter extends BrokerTask<Message> implements Controllable {

  private MidiDeviceBundle device;
  private MidiConfiguration configuration;
  private RandomAccessList<Receiver> receivers = new DirectList<>();
  private int selectedChannelNumber;
  private ChannelState[] channelStates = new ChannelState[Midi.CHANNELS];

  public MidiWriter(Broker<Message> broker, MidiDeviceBundle device, MidiConfiguration configuration) {
    super(broker);
    this.device = device;
    this.configuration = configuration;
    subscribe(OnChannelState.class, message -> doChannelState(message.getChannel(), message.getChannelState()));
    subscribe(OnDeviceMessage.class, message -> doDeviceMessage(message.getDeviceMessage()));
    connectDevices();
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

  private void doChannelState(int channel, ChannelState channelState) {
    if (channelState == ChannelState.SELECTED) {
      if (selectedChannelNumber != 0) {
        setChannelState(selectedChannelNumber, channelStates[selectedChannelNumber]);
      }
      selectedChannelNumber = channel;
    } else {
      channelStates[channel] = channelState;
    }
    setChannelState(channel, channelState);
  }

  private void doDeviceMessage(ChannelMessage deviceMessage) {
    sendDeviceMessage(deviceMessage);
  }

  private void sendDeviceMessage(ChannelMessage deviceMessage) {
    try {
      int command = deviceMessage.getCommand();
      int channel = deviceMessage.getChannel();
      int data1 = Value.getInt(deviceMessage.getData1());
      int data2 = Value.getInt(deviceMessage.getData2());
      ShortMessage shortMessage = new ShortMessage(command, channel, data1, data2);
      if (deviceMessage.getSubDevice() == null) {
        for (Receiver receiver : receivers) {
          receiver.send(shortMessage, -1);
        }
      } else {
        receivers.get(deviceMessage.getSubDevice()).send(shortMessage, -1);
      }
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  private void setChannelState(int channel, ChannelState channelState) {
    int channelNumber = Value.toNumber(channel);
    for (OutputAction outputAction : configuration.getOutputActions()) {
      if (outputAction != null) {
        ChannelStatus ifChannelStatus = outputAction.getIfChannelStatus();
        if (ifChannelStatus != null) {
          if (ifChannelStatus.getChannelNumber() != null) {
            if (ifChannelStatus.getChannelNumber() == channelNumber) {
              if (ifChannelStatus.getState() == channelState) {
                sendDeviceMessage(outputAction.getThenSendDeviceMessage());
              }
            }
          }
        }
      }
    }
  }

}
