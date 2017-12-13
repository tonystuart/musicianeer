// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnShortMessage;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.util.Range;

public class MidiReader {

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

  private MessageBroker broker;
  private MidiDeviceBundle deviceBundle;
  private MidiConfiguration configuration;
  private DeviceHandler deviceHandler;

  public MidiReader(MessageBroker broker, DeviceHandler deviceHandler, MidiDeviceBundle deviceBundle, MidiConfiguration configuration) {
    this.broker = broker;
    this.deviceHandler = deviceHandler;
    this.deviceBundle = deviceBundle;
    this.configuration = configuration;
    connectDevices();
  }

  public void start() {
  }

  public void terminate() {
    disconnectDevices();
  }

  @Override
  public String toString() {
    return "MidiReader [type=" + deviceBundle.getType() + ", card=" + deviceBundle.getCard() + ", unit=" + deviceBundle.getUnit() + "]";
  }

  private void connectDevices() {
    try {
      for (MidiInputDevice midiInputDevice : deviceBundle.getInputDevices()) {
        MidiDevice midiDevice = midiInputDevice.getMidiDevice();
        midiDevice.open();
        midiDevice.getTransmitter().setReceiver(new MidiReceiver(midiInputDevice.getPort()));
      }
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private void disconnectDevices() {
    for (MidiInputDevice midiInputDevice : deviceBundle.getInputDevices()) {
      MidiDevice midiDevice = midiInputDevice.getMidiDevice();
      midiDevice.close();
    }
  }

  private String formatMessage(MidiMessage message) {
    StringBuilder s = new StringBuilder();
    for (byte b : message.getMessage()) {
      if (s.length() > 0) {
        s.append(" ");
      }
      s.append(String.format("%02x", b));
    }
    return s.toString();
  }

  private Object getCommandType(OutputType outputType) {
    Object commandType;
    switch (outputType) {
    case BACKGROUND_DECREASE_VELOCITY:
      commandType = Command.DECREASE_BACKGROUND_VELOCITY;
      break;
    case BACKGROUND_INCREASE_VELOCITY:
      commandType = Command.INCREASE_BACKGROUND_VELOCITY;
      break;
    case BACKGROUND_MUTE:
      commandType = DeviceCommand.MUTE_BACKGROUND;
      break;
    case BACKGROUND_SELECT_VELOCITY:
      commandType = Command.SET_BACKGROUND_VELOCITY;
      break;
    case KARAOKE_TYPE_MEASURE:
      commandType = DeviceCommand.OUTPUT_MEASURE;
      break;
    case KARAOKE_TYPE_TICK:
      commandType = DeviceCommand.OUTPUT_TICK;
      break;
    case LIBRARY_NEXT_SONG:
      commandType = Command.INCREASE_SONG_INDEX;
      break;
    case LIBRARY_PREVIOUS_SONG:
      commandType = Command.DECREASE_SONG_INDEX;
      break;
    case LIBRARY_SELECT_SONG:
      commandType = Command.SET_SONG_INDEX;
      break;
    case LIBRARY_SELECT_TRANSPOSE:
      commandType = Command.SET_TRANSPOSITION;
      break;
    case LIBRARY_TRANSPOSE_HIGHER:
      commandType = Command.INCREASE_TRANSPOSITION;
      break;
    case LIBRARY_TRANSPOSE_LOWER:
      commandType = Command.DECREASE_TRANSPOSITION;
      break;
    case MASTER_DECREASE_VOLUME:
      commandType = Command.DECREASE_MASTER_GAIN;
      break;
    case MASTER_INCREASE_VOLUME:
      commandType = Command.INCREASE_MASTER_GAIN;
      break;
    case MASTER_INSTRUMENT:
      commandType = Command.SET_MASTER_PROGRAM;
      break;
    case MASTER_SELECT_VOLUME:
      commandType = Command.SET_MASTER_GAIN;
      break;
    case PLAYER_DECREASE_VELOCITY:
      commandType = DeviceCommand.DECREASE_PLAYER_VELOCITY;
      break;
    case PLAYER_INCREASE_VELOCITY:
      commandType = DeviceCommand.INCREASE_PLAYER_VELOCITY;
      break;
    case PLAYER_NEXT_CHANNEL:
      commandType = DeviceCommand.NEXT_CHANNEL;
      break;
    case PLAYER_NEXT_PROGRAM:
      commandType = DeviceCommand.NEXT_PROGRAM;
      break;
    case PLAYER_PREVIOUS_CHANNEL:
      commandType = DeviceCommand.PREVIOUS_CHANNEL;
      break;
    case PLAYER_PREVIOUS_PROGRAM:
      commandType = DeviceCommand.PREVIOUS_PROGRAM;
      break;
    case PLAYER_SELECT_CHANNEL:
      commandType = DeviceCommand.SELECT_CHANNEL;
      break;
    case PLAYER_SELECT_PROGRAM:
      commandType = DeviceCommand.PROGRAM;
      break;
    case PLAYER_SELECT_VELOCITY:
      commandType = DeviceCommand.VELOCITY;
      break;
    case TRANSPORT_DECREASE_TEMPO:
      commandType = Command.DECREASE_TEMPO;
      break;
    case TRANSPORT_INCREASE_TEMPO:
      commandType = Command.INCREASE_TEMPO;
      break;
    case TRANSPORT_NEXT_MEASURE:
      commandType = Command.MOVE_FORWARD;
      break;
    case TRANSPORT_PLAY:
      commandType = Command.PLAY;
      break;
    case TRANSPORT_PREVIOUS_MEASURE:
      commandType = Command.MOVE_BACKWARD;
      break;
    case TRANSPORT_SELECT_MEASURE:
      commandType = Command.SEEK;
      break;
    case TRANSPORT_SELECT_TEMPO:
      commandType = Command.SET_TEMPO;
      break;
    case TRANSPORT_STOP:
      commandType = Command.STOP;
      break;
    default:
      throw new UnsupportedOperationException(outputType.name());
    }
    return commandType;
  }

  private void processMappedMessage(ShortMessage shortMessage, OutputMessage outputMessage) {
    OutputType outputType = outputMessage.getOutputType();
    if (outputType == OutputType.KARAOKE_SELECT_GROUP) {
      if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
        deviceHandler.tsOnDown(outputMessage.getIndex(), shortMessage.getData2());
      } else {
        deviceHandler.tsOnUp(outputMessage.getIndex());
      }
    } else if (outputType == OutputType.KARAOKE_SELECT_SOUND) {
      if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
        deviceHandler.tsOnDown(outputMessage.getIndex(), shortMessage.getData2());
      } else {
        deviceHandler.tsOnUp(outputMessage.getIndex());
      }
    } else {
      Object commandType = getCommandType(outputType);
      if (commandType instanceof Command) {
        Command command = (Command) commandType;
        broker.publish(new OnCommand(command, Range.scaleMidiToPercent(shortMessage.getData2())));
      } else if (commandType instanceof DeviceCommand) {
        DeviceCommand deviceCommand = (DeviceCommand) commandType;
        broker.publish(new OnDeviceCommand(deviceCommand, deviceHandler.tsGetDeviceIndex(), Range.scaleMidiToPercent(shortMessage.getData2())));
      }
    }
  }

  private void processUnmappedMessage(ShortMessage shortMessage) {
    int command = shortMessage.getCommand();
    int data1 = shortMessage.getData1();
    int data2 = shortMessage.getData2();
    if (command == ShortMessage.NOTE_ON) {
      deviceHandler.tsOnDown(data1, data2);
    } else if (command == ShortMessage.NOTE_OFF) {
      deviceHandler.tsOnUp(data1);
    } else if (command == ShortMessage.POLY_PRESSURE) {
      deviceHandler.tsOnChannelPressure(data1, data2);
    } else if (command == ShortMessage.CONTROL_CHANGE) {
      int control = data1;
      int value = data2;
      deviceHandler.tsOnControlChangle(control, value);
    } else if (command == ShortMessage.PITCH_BEND) {
      // Pitch bend is reported as a signed 14 bit value with MSB in data2 and LSB in data1
      // Options for converting it into values in the range 0 to 16384 include:
      // 1. Use LS(32-14) to set the sign and RS(32-14) to extend the size to produce values in range -8192 to 8192, then add 8192 to get values in range 0 to 16384
      // 2. Recognize that values GT 8192 have their sign bit set, subtract 8192 from them and add 8192 to values LT 8192 to get values in range 0 to 16384
      // We use the second approach
      int value = (data2 << 7) | data1;
      int pitchBend = value >= 8192 ? value - 8192 : value + 8192;
      deviceHandler.tsOnPitchBend(pitchBend);
    }
  }

  private void tsReceiveFromDevice(MidiMessage message, long timestamp, int port) {
    try {
      if (message instanceof ShortMessage) {
        ShortMessage shortMessage = (ShortMessage) message;
        InputMessage inputMessage = new InputMessage(shortMessage);
        OutputMessage outputMessage = configuration.get(inputMessage);
        if (outputMessage == null) {
          processUnmappedMessage(shortMessage);
        } else {
          processMappedMessage(shortMessage, outputMessage);
        }
        broker.publish(new OnShortMessage(deviceHandler.tsGetDeviceIndex(), shortMessage));
      } else {
        System.out.println("MidiReader.receiveFromDevice: message=" + formatMessage(message));
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.err.println("Ignoring exception");
    }
  }

}
