// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.ChannelCommand;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelCommand;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Velocity;

public class TransportTask extends BrokerTask<Message> {

  private enum Direction {
    BACKWARD, FORWARD
  }

  private static final int DEFAULT_PERCENT_VELOCITY = 25;
  private static final float DEFAULT_GAIN = 5 * Synthesizer.DEFAULT_GAIN;
  private static final long FIRST_NOTE = -1;
  static final long LAST_NOTE = -1;

  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private Song song;
  private Synthesizer synthesizer;
  private TransportSequencer sequencer;

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.synthesizer = synthesizer;
    synthesizer.setGain(DEFAULT_GAIN);
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnChannelCommand.class, message -> doChannelCommand(message));
    this.sequencer = new TransportSequencer(noteEvent -> processNoteEvent(noteEvent));
    sequencer.start();
  }

  private void doBackward() {
    move(Direction.BACKWARD);
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
    case PLAY_PAUSE:
      doPlayPause(parameter);
      break;
    case PLAY_SAMPLE:
      doPlaySample(parameter);
      break;
    case PAUSE:
      doPause();
      break;
    case RESUME:
      doResume();
      break;
    case STOP:
      doStop();
      break;
    case STOP_PAUSE:
      doStopPause();
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
    move(Direction.FORWARD);
  }

  private void doGain(int masterGain) {
    float gain = Range.scale(0f, 2f, Midi.MIN_VALUE, Midi.MAX_VALUE, masterGain);
    synthesizer.setGain(gain);
  }

  private void doPause() {
    pause();
  }

  private void doPlay(int channel) {
    play(FIRST_NOTE);
    sequencer.setPaused(false);
  }

  private void doPlayPause(int channel) {
    if (sequencer.isPaused()) {
      resume();
    } else {
      play(FIRST_NOTE);
    }
  }

  private void doPlaySample(int channel) {
    play(channel, FIRST_NOTE, song.getBeatsPerMeasure(0) * Default.TICKS_PER_BEAT * 2);
    sequencer.setPaused(false);
  }

  private void doResume() {
    resume();
  }

  private void doSeek(long tick) {
    //play(tick);
  }

  private void doSetVelocity(int velocity) {
    this.percentVelocity = Range.scaleMidiToPercent(velocity);
  }

  private void doSong(OnSong message) {
    stop();
    this.song = message.getSong();
  }

  private void doStop() {
    stop();
  }

  private void doStopPause() {
    if (sequencer.isPaused()) {
      stop();
    } else {
      pause();
    }
  }

  private void doTempo(int tempo) {
    sequencer.setPercentTempo(Range.scaleMidiToPercent(tempo));
  }

  private void doTransposeTo(int midiTransposition) {
    // Dynamic transposition for use with rotary control... does not update display
    int transposition = Range.scale(-24, 24, Midi.MIN_VALUE, Midi.MAX_VALUE, midiTransposition);
    song.transposeTo(transposition);
    synthesizer.allNotesOff(); // turn off notes that were playing before transpose
  }

  private Note findFirstNote(int channel, long baseTick) {
    Note fromElement;
    TreeSet<Note> notes = song.getNotes();
    if (notes.size() > 0) {
      if (baseTick == FIRST_NOTE) {
        fromElement = notes.first();
      } else {
        fromElement = notes.ceiling(new Note(baseTick));
      }
      if (channel == -1) {
        return fromElement;
      }
      for (Note note : notes.tailSet(fromElement)) {
        if (channel == note.getChannel()) {
          return note;
        }
      }
    }
    return null;
  }

  private Note findLastNote(Note firstNote, long baseDuration) {
    Note lastNote;
    if (baseDuration == TransportTask.LAST_NOTE) {
      lastNote = song.getNotes().last();
    } else {
      lastNote = new Note(firstNote.getTick() + baseDuration);
    }
    return lastNote;
  }

  private void move(Direction direction) {
    sequencer.setPaused(true);
    long oldTick = sequencer.getTick();
    int ticksPerMeasure = song.getTicksPerMeasure(oldTick);
    int measure;
    int nextMeasure;
    switch (direction) {
    case BACKWARD:
      measure = (int) ((oldTick - 1) / ticksPerMeasure);
      nextMeasure = measure - 1;
      break;
    case FORWARD:
      measure = (int) ((oldTick + 1) / ticksPerMeasure);
      nextMeasure = measure + 1;
      break;
    default:
      throw new UnsupportedOperationException();
    }
    long newTick = nextMeasure * ticksPerMeasure;
    System.out.println("oldTick=" + oldTick + ", newTick=" + newTick);
    play(newTick);
    publishTick(newTick);
  }

  private void pause() {
    sequencer.setPaused(true);
    synthesizer.allNotesOff();
  }

  private void play(int channel, long baseTick, long baseDuration) {
    reset();
    Note firstNote = findFirstNote(channel, baseTick);
    if (firstNote != null) {
      Note lastNote = findLastNote(firstNote, baseDuration);
      sequencer.play(song, channel, firstNote, lastNote);
    }
  }

  private void play(long baseTick) {
    play(-1, baseTick, LAST_NOTE);
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {
    case NOTE_OFF: {
      int channel = note.getChannel();
      int midiNote = note.getMidiNote();
      synthesizer.releaseKey(channel, midiNote);
      break;
    }
    case NOTE_ON: {
      int channel = note.getChannel();
      int midiNote = note.getMidiNote();
      int velocity = note.getVelocity();
      int program = note.getProgram();
      if (currentPrograms[channel] != program) {
        synthesizer.changeProgram(channel, program);
        currentPrograms[channel] = program;
        // TODO: Publish this for Player
      }
      int scaledVelocity = Velocity.scale(velocity, percentVelocity);
      synthesizer.pressKey(channel, midiNote, scaledVelocity);
      break;
    }
    case TICK:
      publishTick(noteEvent.getTick());
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void publishTick(long tick) {
    getBroker().publish(new OnTick(tick));
  }

  private void reset() {
    sequencer.reset();
    synthesizer.allNotesOff();
  }

  private void resume() {
    sequencer.setPaused(false);
  }

  private void stop() {
    reset();
    publishTick(0);
  }
}
