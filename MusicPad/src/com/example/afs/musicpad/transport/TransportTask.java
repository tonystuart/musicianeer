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

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.transport = new Transport(synthesizer, tick -> publishTick(tick));
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnChannelCommand.class, message -> doChannelCommand(message));
  }

  private void doBackward() {
    transport.seek(-Default.TICKS_PER_BEAT * Default.BEATS_PER_MEASURE, Whence.RELATIVE);
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
    case PLAY:
      doPlay(parameter);
      break;
    case STOP:
      doStop();
      break;
    case BACKWARD:
      doBackward();
      break;
    case FORWARD:
      doForward();
      break;
    case SEEK:
      doSeek(parameter);
      break;
    case TEMPO:
      doTempo(parameter);
      break;
    case VELOCITY:
      doSetVelocity(parameter);
      break;
    case GAIN:
      doGain(parameter);
      break;
    case TRANSPOSE_TO:
      doTransposeTo(parameter);
      break;
    default:
      break;
    }
  }

  private void doForward() {
    transport.seek(Default.TICKS_PER_BEAT * Default.BEATS_PER_MEASURE, Whence.RELATIVE);
  }

  private void doGain(int masterGain) {
    float gain = Range.scale(0f, 2f, Midi.MIN_VALUE, Midi.MAX_VALUE, masterGain);
    transport.setGain(gain);
  }

  private void doStop() {
    if (transport.isPaused()) {
      transport.stop();
      publishTick(0);
    } else {
      transport.pause();
    }
  }

  private void doPlay(int channel) {
    if (transport.isPaused()) {
      transport.resume();
    } else {
      transport.play(new ChannelNotes(song.getNotes(), channel));
    }
  }

  private void doSeek(long tick) {
    //play(tick);
  }

  private void doSetVelocity(int velocity) {
    transport.setPercentVelocity(Range.scaleMidiToPercent(velocity));
  }

  private void doSong(OnSong message) {
    transport.stop();
    this.song = message.getSong();
  }

  private void doTempo(int tempo) {
    transport.setPercentTempo(Range.scaleMidiToPercent(tempo));
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
