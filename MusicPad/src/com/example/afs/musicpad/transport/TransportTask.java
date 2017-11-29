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
import com.example.afs.musicpad.message.OnChannelCommand;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnRenderSong;
import com.example.afs.musicpad.message.OnSampleChannel;
import com.example.afs.musicpad.message.OnSampleSong;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.service.Services;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.transport.Transport.Whence;
import com.example.afs.musicpad.util.Range;

public class TransportTask extends ServiceTask {

  private Song song;
  private Transport transport;
  private long seekPosition;

  public TransportTask(MessageBroker broker, Synthesizer synthesizer) {
    super(broker);
    this.transport = new Transport(synthesizer);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnSampleSong.class, message -> doSampleSong(message));
    subscribe(OnRenderSong.class, message -> doRenderSong(message));
    subscribe(OnSampleChannel.class, message -> doSampleChannel(message));
    subscribe(OnChannelCommand.class, message -> doChannelCommand(message));
    provide(Services.getBackgroundVelocity, () -> transport.getPercentVelocity());
    provide(Services.getMasterGain, () -> transport.getPercentGain());
    provide(Services.getTempo, () -> transport.getPercentTempo());
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
    case RESET:
      doReset();
      break;
    case SEEK:
      doSeek(parameter);
      break;
    case SET_BACKGROUND_VELOCITY:
      doSetBackgroundVelocity(parameter);
      break;
    case SET_MASTER_GAIN:
      doSetMidiMasterGain(parameter);
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
    publish(new OnCommand(Command.SET_BACKGROUND_VELOCITY, Math.max(0, transport.getPercentVelocity() - 10)));
  }

  private void doDecreaseMasterGain() {
    publish(new OnCommand(Command.SET_BACKGROUND_VELOCITY, Math.max(0, transport.getPercentGain() - 10)));
  }

  private void doDecreaseTempo() {
    publish(new OnCommand(Command.SET_TEMPO, Math.max(0, transport.getPercentTempo() - 10)));
  }

  private void doIncreaseBackgroundVelocity() {
    publish(new OnCommand(Command.SET_BACKGROUND_VELOCITY, Math.min(100, transport.getPercentVelocity() + 10)));
  }

  private void doIncreaseMasterGain() {
    publish(new OnCommand(Command.SET_MASTER_GAIN, Math.min(100, transport.getPercentGain() + 10)));
  }

  private void doIncreaseTempo() {
    publish(new OnCommand(Command.SET_TEMPO, Math.min(100, transport.getPercentTempo() + 10)));
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
    transport.stop();
    publishTick(0);
  }

  private void doReset() {
    transport.reset();
  }

  private void doSampleChannel(OnSampleChannel message) {
    transport.stop();
    this.song = message.getSong();
    transport.play(new ChannelNotes(song.getNotes(), message.getChannel()));
  }

  private void doSampleSong(OnSampleSong message) {
    song = message.getSong();
    transport.stop();
    transport.play(song.getNotes());
    seekPosition = 0;
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
    transport.setPercentVelocity(velocity);
  }

  private void doSetMidiMasterGain(int gain) {
    transport.setPercentGain(gain);
  }

  private void doSetTempo(int tempo) {
    transport.setPercentTempo(tempo);
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

}
