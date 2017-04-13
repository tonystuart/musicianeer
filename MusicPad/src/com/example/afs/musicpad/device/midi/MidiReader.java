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

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.common.ControllableGroup.Controllable;
import com.example.afs.musicpad.device.midi.configuration.Context;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendCommand;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendDeviceMessage;
import com.example.afs.musicpad.device.midi.configuration.Context.HasSendHandlerMessage;
import com.example.afs.musicpad.device.midi.configuration.MidiConfiguration;
import com.example.afs.musicpad.device.midi.configuration.Node.ReturnState;
import com.example.afs.musicpad.device.midi.configuration.On;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceMessage;
import com.example.afs.musicpad.message.OnInputPress;
import com.example.afs.musicpad.message.OnInputRelease;
import com.example.afs.musicpad.util.Broker;

public class MidiReader implements Controllable, HasSendCommand, HasSendDeviceMessage, HasSendHandlerMessage {

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
      receiveFromDevice(message, timestamp, port);
    }
  }

  private Broker<Message> broker;
  private MidiDeviceBundle device;
  private BlockingQueue<Message> queue;
  private MidiConfiguration configuration;
  private Context context;

  public MidiReader(Broker<Message> broker, BlockingQueue<Message> queue, MidiDeviceBundle device, MidiConfiguration configuration) {
    this.broker = broker;
    this.queue = queue;
    this.device = device;
    this.configuration = configuration;
    this.context = configuration.getContext();
    context.setHasSendCommand(this);
    context.setHasSendDeviceMessage(this);
    context.setHasSendHandlerMessage(this);
    connectDevices();
  }

  @Override
  public void sendCommand(Command handlerCommand, Integer parameter) {
    queue.add(new OnCommand(handlerCommand, parameter));
  }

  @Override
  public void sendDeviceMessage(int port, int command, int channel, int data1, int data2) {
    broker.publish(new OnDeviceMessage(port, command, channel, data1, data2));
  }

  @Override
  public void sendHandlerMessage(int data1) {
    queue.add(new OnInputPress(data1));
  }

  @Override
  public void start() {
  }

  @Override
  public void terminate() {
    disconnectDevices();
  }

  @Override
  public String toString() {
    return "MidiReader [type=" + device.getType() + ", card=" + device.getCard() + ", unit=" + device.getUnit() + "]";
  }

  private void connectDevices() {
    try {
      for (MidiInputDevice midiInputDevice : device.getInputDevices()) {
        MidiDevice midiDevice = midiInputDevice.getMidiDevice();
        midiDevice.open();
        midiDevice.getTransmitter().setReceiver(new MidiReceiver(midiInputDevice.getPort()));
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void disconnectDevices() {
    for (MidiInputDevice midiInputDevice : device.getInputDevices()) {
      MidiDevice midiDevice = midiInputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private void receiveFromDevice(MidiMessage message, long timestamp, int port) {
    try {
      if (message instanceof ShortMessage) {
        ShortMessage shortMessage = (ShortMessage) message;
        context.setPort(port);
        context.setType(shortMessage.getCommand());
        context.setChannel(shortMessage.getChannel());
        context.setData1(shortMessage.getData1());
        context.setData2(shortMessage.getData2());
        On onInput = configuration.getOn(MidiConfiguration.INPUT);
        if (onInput == null || onInput.execute(context) == ReturnState.IF_NO_MATCH) {
          if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
            queue.add(new OnInputPress(shortMessage.getData1()));
          } else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
            queue.add(new OnInputRelease(shortMessage.getData1()));
          }
        }
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.err.println("Ignoring exception");
    }
  }

}
