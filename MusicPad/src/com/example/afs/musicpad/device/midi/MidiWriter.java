// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.ControllableGroup.Controllable;
import com.example.afs.musicpad.device.midi.MidiConfiguration.ChannelMessage;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelInfo;
import com.example.afs.musicpad.message.OnDeviceMessages;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class MidiWriter extends BrokerTask<Message> implements Controllable {

  private MidiDeviceBundle device;
  private MidiConfiguration configuration;
  private RandomAccessList<Receiver> receivers = new DirectList<>();

  public MidiWriter(Broker<Message> broker, MidiDeviceBundle device, MidiConfiguration configuration) {
    super(broker);
    this.device = device;
    this.configuration = configuration;
    subscribe(OnChannelInfo.class, message -> doChannelInfo(message.getChannel(), message.getNoteCount()));
    subscribe(OnDeviceMessages.class, message -> doDeviceMessages(message.getDeviceMessages()));
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

  private void doChannelInfo(int channel, int noteCount) {
    if (noteCount > 0) {
      int channelNumber = Value.toNumber(channel);
    }
  }

  private void doDeviceMessages(List<ChannelMessage> deviceMessages) {
    sendDeviceMessages(deviceMessages);
  }

  private void sendDeviceMessages(List<ChannelMessage> deviceMessages) {
    for (ChannelMessage channelMessage : deviceMessages) {
      try {
        int command = channelMessage.getCommand();
        int channel = channelMessage.getChannel();
        int data1 = Value.getInt(channelMessage.getData1());
        int data2 = Value.getInt(channelMessage.getData2());
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

}
