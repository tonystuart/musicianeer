// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.ChannelCommand;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelCommand;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnReport;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.transport.Transport.Whence;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;

public class TransportTask extends BrokerTask<Message> {

  private Song song;
  private Transport transport;
  private long seekPosition;

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.transport = new Transport(synthesizer);
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnChannelCommand.class, message -> doChannelCommand(message));
  }

  private void doChannelCommand(OnChannelCommand message) {
    ChannelCommand command = message.getChannelCommand();
    int channel = message.getChannel();
    int parameter = message.getParameter();
    System.out.println("TransportTask.doChannelCommand: command=" + command + ", channel=" + channel + ", parameter=" + parameter);
  }

  private void doCommand(OnCommand message) {
    Command command = message.getCommand();
    int parameter = message.getParameter();
    switch (command) {
    case DECREASE_BACKGROUND_VELOCITY:
      doDecreaseBackgroundVelocity();
      break;
    case DECREASE_MASTER_GAIN:
      doDecreaseMasterGain();
      break;
    case DECREASE_TEMPO:
      doDecreaseTempo();
      break;
    case INCREASE_BACKGROUND_VELOCITY:
      doIncreaseBackgroundVelocity();
      break;
    case INCREASE_MASTER_GAIN:
      doIncreaseMasterGain();
      break;
    case INCREASE_TEMPO:
      doIncreaseTempo();
      break;
    case MOVE_BACKWARD:
      doMoveBackward();
      break;
    case MOVE_FORWARD:
      doMoveForward();
      break;
    case PLAY:
      doPlay(parameter);
      break;
    case REPORT:
      doReport();
      break;
    case SEEK:
      doSeek(parameter);
      break;
    case SET_BACKGROUND_VELOCITY:
      doSetBackgroundVelocity(parameter);
      break;
    case SET_MASTER_GAIN:
      doMasterGain(parameter);
      break;
    case SET_TEMPO:
      doSetTempo(parameter);
      break;
    case STOP:
      doStop(parameter);
      break;
    case TRANSPOSE_TO:
      doTransposeTo(parameter);
      break;
    default:
      break;
    }
  }

  private void doDecreaseBackgroundVelocity() {
    setBackgroundVelocity(Math.max(0, transport.getPercentVelocity() - 10));
  }

  private void doDecreaseMasterGain() {
    float currentGain = transport.getGain();
    float newGain = currentGain - 0.2f;
    if (newGain >= 0) {
      setMasterGain(newGain);
    }
  }

  private void doDecreaseTempo() {
    setPercentTempo(Math.max(0, transport.getPercentTempo() - 10));
  }

  private void doIncreaseBackgroundVelocity() {
    setBackgroundVelocity(Math.max(0, transport.getPercentVelocity() + 10));
  }

  private void doIncreaseMasterGain() {
    float currentGain = transport.getGain();
    float newGain = currentGain + 0.2f;
    if (newGain <= Synthesizer.MAXIMUM_GAIN) {
      setMasterGain(newGain);
    }
  }

  private void doIncreaseTempo() {
    setPercentTempo(Math.min(200, transport.getPercentTempo() + 10));
  }

  private void doMasterGain(int masterGain) {
    float gain = Range.scale(0f, 2f, Midi.MIN_VALUE, Midi.MAX_VALUE, masterGain);
    setMasterGain(gain);
  }

  private void doMoveBackward() {
    transport.seek(-Default.TICKS_PER_BEAT * Default.BEATS_PER_MEASURE, Whence.RELATIVE);
  }

  private void doMoveForward() {
    transport.seek(Default.TICKS_PER_BEAT * Default.BEATS_PER_MEASURE, Whence.RELATIVE);
  }

  private void doPlay(int channel) {
    if (transport.isPaused()) {
      transport.resume();
    } else {
      publishTick(0);
      transport.play(new ChannelNotes(song.getNotes(), channel), tick -> publishTick(tick));
    }
    if (seekPosition != 0) {
      transport.seek(seekPosition, Whence.ABSOLUTE);
      seekPosition = 0;
    }
  }

  private void doRenderSong(OnRenderSong message) {
    publishTick(0);
  }

  private void doReport() {
    reportBackgroundVelocity();
    reportMasterGain();
    reportTempo();
  }

  private void doSampleChannel(OnSampleChannel message) {
    transport.stop();
    this.song = message.getSong();
    transport.play(new ChannelNotes(song.getNotes(), message.getChannel()));
  }

  private void doSampleSong(OnSampleSong message) {
    transport.stop();
    this.song = message.getSong();
    transport.play(song.getNotes());
  }

  private void doSeek(long tick) {
    if (transport.isEmpty()) {
      publishTick(tick);
      seekPosition = tick;
    } else {
      publishTick(tick); // sets previous tick for interlude
      transport.seek(tick, Whence.ABSOLUTE);
    }
  }

  private void doSetBackgroundVelocity(int velocity) {
    setBackgroundVelocity(velocity);
  }

  private void doSetTempo(int tempo) {
    setPercentTempo(Range.scaleMidiToPercent(tempo));
  }

  private void doSong(OnSong message) {
    transport.stop();
    //transport.muteAllChannels(false);
    song = message.getSong();
    seekPosition = 0;
  }

  private void doStop(int parameter) {
    if (transport.isPaused() || parameter == 1) {
      transport.stop();
      publishTick(0);
    } else {
      transport.pause();
    }
  }

  private void doTransposeTo(int midiTransposition) {
    // Dynamic transposition for use with rotary control... does not update display
    int transposition = Range.scale(-24, 24, Midi.MIN_VALUE, Midi.MAX_VALUE, midiTransposition);
    song.transposeTo(transposition);
    transport.allNotesOff(); // turn off notes that were playing before transpose
  }

  private void publishTick(long tick) {
    getBroker().publish(new OnTick(tick));
  }

  private void reportBackgroundVelocity() {
    int percentVelocity = transport.getPercentVelocity();
    int backgroundVelocity = Range.scalePercentToMidi(percentVelocity);
    publish(new OnReport(Command.SET_BACKGROUND_VELOCITY, backgroundVelocity));
  }

  private void reportMasterGain() {
    float gain = transport.getGain();
    int masterGain = (int) Range.scale(Midi.MIN_VALUE, Midi.MAX_VALUE, 0f, 2f, gain);
    publish(new OnReport(Command.SET_MASTER_GAIN, masterGain));
  }

  private void reportTempo() {
    int percentTempo = transport.getPercentTempo();
    int tempo = Range.scalePercentToMidi(percentTempo);
    publish(new OnReport(Command.SET_TEMPO, tempo));
  }

  private void setBackgroundVelocity(int velocity) {
    transport.setPercentVelocity(Range.scaleMidiToPercent(velocity));
    reportBackgroundVelocity();
  }

  private void setMasterGain(float gain) {
    transport.setGain(gain);
    reportMasterGain();
  }

  private void setPercentTempo(int percentTempo) {
    transport.setPercentTempo(percentTempo);
    reportTempo();
  }

}
