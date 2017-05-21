// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

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
import com.example.afs.musicpad.task.PausibleSequencerTask;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Velocity;

public class TransportTask extends BrokerTask<Message> {

  public static final int DEFAULT_PERCENT_VELOCITY = 25;
  private static final float DEFAULT_GAIN = 5 * Synthesizer.DEFAULT_GAIN;

  private static final long FIRST_NOTE = -1;
  private static final long LAST_NOTE = -1;

  private Song song;
  private Synthesizer synthesizer;
  private NoteEventScheduler noteEventScheduler;
  private PausibleSequencerTask<NoteEvent> sequencerTask;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.synthesizer = synthesizer;
    synthesizer.setGain(DEFAULT_GAIN);
    subscribe(OnSong.class, message -> doSongSelected(message.getSong()));
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    subscribe(OnChannelCommand.class, message -> doChannelCommand(message.getChannelCommand(), message.getChannel(), message.getParameter()));
    noteEventScheduler = new NoteEventScheduler();
    sequencerTask = new PausibleSequencerTask<NoteEvent>(noteEventScheduler, new Broker<>());
    sequencerTask.subscribe(NoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
    sequencerTask.start();
  }

  private void doChannelCommand(ChannelCommand command, int channel, int parameter) {
    System.out.println("TransportTask.doChannelCommand: command=" + command + ", channel=" + channel + ", parameter=" + parameter);
  }

  private void doCommand(Command command, int parameter) {
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
    case PREVIOUS_MEASURE:
      doPreviousMeasure();
      break;
    case NEXT_MEASURE:
      doNextMeasure();
      break;
    case SEEK:
      doSeek(parameter);
      break;
    case TEMPO:
      doTempo(parameter);
      break;
    case SET_TRANSPORT_VELOCITY:
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

  private void doGain(int masterGain) {
    float gain = Range.scale(0f, 2f, Midi.MIN_VALUE, Midi.MAX_VALUE, masterGain);
    synthesizer.setGain(gain);
  }

  private void doNextMeasure() {
    long tick = noteEventScheduler.getTick();
    int ticksPerMeasure = song.getTicksPerMeasure(tick);
    int measure = (int) (tick / ticksPerMeasure);
    int nextMeasure = measure + 1;
    long baseTick = nextMeasure * ticksPerMeasure;
    System.out.println("Moving from measure " + measure + " to measure " + nextMeasure);
    play(baseTick);
  }

  private void doPause() {
    pause();
  }

  private void doPlay(int channel) {
    play(FIRST_NOTE);
  }

  private void doPlayPause(int channel) {
    if (sequencerTask.isPaused()) {
      resume();
    } else {
      play(FIRST_NOTE);
    }
  }

  private void doPlaySample(int channel) {
    play(channel, FIRST_NOTE, song.getBeatsPerMeasure(0) * Default.TICKS_PER_BEAT * 2);
  }

  private void doPreviousMeasure() {
    long tick = noteEventScheduler.getTick();
    int ticksPerMeasure = song.getTicksPerMeasure(tick);
    int measure = (int) (tick / ticksPerMeasure);
    int previousMeasure = measure - 1;
    long baseTick = previousMeasure * ticksPerMeasure;
    Note toElement = song.getNotes().floor(new Note(baseTick));
    if (toElement != null) {
      System.out.println("Moving from measure " + measure + " to measure " + previousMeasure);
      play(toElement.getTick());
      return;
    }
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

  private void doSongSelected(Song song) {
    stop();
    this.song = song;
  }

  private void doStop() {
    stop();
  }

  private void doStopPause() {
    if (sequencerTask.isPaused()) {
      stop();
    } else {
      pause();
    }
  }

  private void doTempo(int tempo) {
    noteEventScheduler.setPercentTempo(Range.scaleMidiToPercent(tempo));
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

  private void pause() {
    sequencerTask.setPaused(true);
    noteEventScheduler.resetBaseTime();
    synthesizer.allNotesOff();
  }

  private void play(int channel, long baseTick, long baseDuration) {
    stop();
    Note firstNote = findFirstNote(channel, baseTick);
    if (firstNote != null) {
      long firstTick = firstNote.getTick();
      noteEventScheduler.setBaseTick(firstTick);
      noteEventScheduler.resetBaseTime();
      Note lastNote;
      if (baseDuration == LAST_NOTE) {
        lastNote = song.getNotes().last();
      } else {
        lastNote = new Note(firstTick + baseDuration);
      }
      BlockingQueue<NoteEvent> inputQueue = sequencerTask.getInputQueue();
      NavigableSet<Note> notes = song.getNotes().subSet(firstNote, true, lastNote, false);
      for (Note note : notes) {
        if (channel == -1 || channel == note.getChannel()) {
          long tick = note.getTick();
          long duration = note.getDuration();
          inputQueue.add(new NoteEvent(Type.NOTE_ON, tick, note));
          inputQueue.add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
        }
      }
      for (long tick = 0; tick < lastNote.getTick(); tick += Default.RESOLUTION) {
        int beatsPerMinute;
        Note previousNote = notes.lower(new Note(tick));
        if (previousNote == null) {
          beatsPerMinute = Default.BEATS_PER_MINUTE;
        } else {
          beatsPerMinute = previousNote.getBeatsPerMinute();
        }
        inputQueue.add(new NoteEvent(Type.TICK, tick, beatsPerMinute));
      }
    }
  }

  private void play(long baseTick) {
    play(-1, baseTick, LAST_NOTE);
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {
    case NOTE_OFF:
      synthesizer.releaseKey(note.getChannel(), note.getMidiNote());
      break;
    case NOTE_ON:
      synthesizer.changeProgram(note.getChannel(), note.getProgram());
      synthesizer.pressKey(note.getChannel(), note.getMidiNote(), Velocity.scale(note.getVelocity(), percentVelocity));
      break;
    case TICK:
      getBroker().publish(new OnTick(noteEvent.getTick()));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void resume() {
    sequencerTask.setPaused(false);
  }

  private void stop() {
    sequencerTask.getInputQueue().clear();
    sequencerTask.setPaused(false);
    synthesizer.allNotesOff();
    noteEventScheduler.resetAll();
    getBroker().publish(new OnTick(0));
  }
}
