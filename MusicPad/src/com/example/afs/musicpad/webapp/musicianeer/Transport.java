// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.Collections;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.transport.NoteEvent;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Tick;
import com.example.afs.musicpad.util.Velocity;

public class Transport extends ServiceTask {

  public enum Whence {
    RELATIVE, ABSOLUTE
  }

  public static final int DEFAULT_PERCENT_GAIN = 10;
  public static final int DEFAULT_PERCENT_TEMPO = 50;
  public static final int DEFAULT_PERCENT_VELOCITY = 10;

  public static final int DEFAULT_MASTER_PROGRAM_OFF = Midi.MAX_VALUE;
  public static final long INITIALIZE_ON_NEXT_EVENT = -1;

  private int index;
  private int currentTransposition;
  private int masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private long baseTick;
  private boolean isPaused;
  private long baseTimeMillis;
  private int percentTempo = 100;
  private int appliedPercentTempo = percentTempo;

  private Iterable<Note> notes;
  private Synthesizer synthesizer;
  private RandomAccessList<NoteEvent> noteEvents = new DirectList<>();
  private OnSetAccompanimentType.AccompanimentType accompanimentType = OnSetAccompanimentType.AccompanimentType.FULL;

  public Transport(MessageBroker messageBroker, Synthesizer synthesizer) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    setPercentGain(DEFAULT_PERCENT_GAIN);
    provide(Services.getPercentTempo, () -> getPercentTempo());
    provide(Services.getPercentMasterGain, () -> getPercentMasterGain());
    subscribe(OnPlay.class, message -> doPlay(message));
    subscribe(OnStop.class, message -> doStop(message));
    subscribe(OnSeek.class, message -> doSeek(message));
    subscribe(OnNotes.class, message -> doNotes(message));
    subscribe(OnNoteEvent.class, message -> doNoteEvent(message));
    subscribe(OnTransposition.class, message -> doTransposition(message));
    subscribe(OnSetPercentTempo.class, message -> doSetPercentTempo(message));
    subscribe(OnSetPercentVelocity.class, message -> doPercentVelocity(message));
    subscribe(OnSetAccompanimentType.class, message -> doSetAccompanimentType(message));
    subscribe(OnSetPercentMasterGain.class, message -> doSetPercentMasterGain(message));
  }

  private void clear() {
    noteEvents.clear();
    tsGetInputQueue().clear();
    synthesizer.allNotesOff();
    isPaused = false;
    baseTick = 0;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  private void doNoteEvent(OnNoteEvent message) {
    long currentTimestamp = System.currentTimeMillis();
    NoteEvent noteEvent = message.getNoteEvent();
    long eventTimestamp = getEventTimeMillis(noteEvent);
    if (eventTimestamp > currentTimestamp) {
      long sleepInterval = eventTimestamp - currentTimestamp;
      try {
        Thread.sleep(sleepInterval);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    processNoteEvent(noteEvent);
    if (!isPaused) {
      schedule();
    }
  }

  private void doNotes(OnNotes message) {
    notes = message.getNotes();
    play(notes);
  }

  private void doPercentVelocity(OnSetPercentVelocity message) {
    this.percentVelocity = message.getPercentVelocity();
  }

  private void doPlay(OnPlay message) {
    if (isPaused) {
      resume();
    } else if (notes != null) {
      play(notes);
    }
  }

  private void doSeek(OnSeek message) {
    seek(message.getTick(), Whence.ABSOLUTE);
  }

  private void doSetAccompanimentType(OnSetAccompanimentType message) {
    setAccompaniment(message.getAccompanimentType());
  }

  private void doSetPercentMasterGain(OnSetPercentMasterGain message) {
    setPercentGain(message.getPercentMasterGain());
  }

  private void doSetPercentTempo(OnSetPercentTempo message) {
    setPercentTempo(message.getPercentTempo());
  }

  private void doStop(OnStop message) {
    if (isPaused) {
      stop();
      publish(new OnTick(0));
    } else {
      pause();
    }
  }

  private void doTransposition(OnTransposition message) {
    setCurrentTransposition(message.getTransposition());
    synthesizer.allNotesOff();
  }

  private long getEventTimeMillis(long noteTick, int beatsPerMinute) {
    if (baseTimeMillis == INITIALIZE_ON_NEXT_EVENT) {
      baseTimeMillis = System.currentTimeMillis();
    }
    long deltaTick = noteTick - baseTick;
    deltaTick = (deltaTick * 100) / appliedPercentTempo;
    long deltaMillis = Tick.convertTickToMillis(beatsPerMinute, deltaTick);
    long eventTimeMillis = baseTimeMillis + deltaMillis;
    // Update base values to handle changes in beats per minute
    baseTick = noteTick;
    baseTimeMillis = eventTimeMillis;
    return eventTimeMillis;
  }

  private long getEventTimeMillis(NoteEvent noteEvent) {
    return getEventTimeMillis(noteEvent.getTick(), noteEvent.getBeatsPerMinute());
  }

  private int getPercentMasterGain() {
    return (int) Range.scale(0, 100, Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, synthesizer.getGain());
  }

  private int getPercentTempo() {
    return Range.scale(0, 100, 0, 200, percentTempo);
  }

  private int getTransposedMidiNote(Note note, int channel) {
    int midiNote = note.getMidiNote();
    if (currentTransposition != 0 && channel != Midi.DRUM) {
      midiNote += currentTransposition;
    }
    return midiNote;
  }

  private boolean isPlaying() {
    return !isPaused && index < noteEvents.size();
  }

  private void pause() {
    isPaused = true;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    synthesizer.allNotesOff();
  }

  private void play(Iterable<Note> notes) {
    clear();
    index = 0;
    long firstTick = -1;
    long lastTick = -1;
    long metronomeTick = -1;
    int beatsPerMinute = Default.BEATS_PER_MINUTE;
    for (Note note : notes) {
      long beginTick = note.getTick();
      long duration = note.getDuration();
      if (firstTick == -1) {
        firstTick = beginTick;
        baseTick = firstTick;
        baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
      }
      long beginTickRoundedUp = ((beginTick + 1) / Default.RESOLUTION) * Default.RESOLUTION;
      while (metronomeTick < beginTickRoundedUp) {
        metronomeTick = ((metronomeTick + Default.RESOLUTION + 1) / Default.RESOLUTION) * Default.RESOLUTION;
        noteEvents.add(new NoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
      }
      long endTick = beginTick + duration;
      lastTick = Math.max(lastTick, endTick);
      noteEvents.add(new NoteEvent(Type.NOTE_ON, beginTick, note));
      noteEvents.add(new NoteEvent(Type.CUE_NOTE_ON, beginTick - 1024, note));
      noteEvents.add(new NoteEvent(Type.NOTE_OFF, endTick, note));
      beatsPerMinute = note.getBeatsPerMinute();
    }
    long lastTickRoundedUp = ((lastTick + 1) / Default.RESOLUTION) * Default.RESOLUTION;
    while (metronomeTick < lastTickRoundedUp) {
      metronomeTick = ((metronomeTick + Default.RESOLUTION + 1) / Default.RESOLUTION) * Default.RESOLUTION;
      noteEvents.add(new NoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
    }
    Collections.sort(noteEvents);
    schedule();
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {

    case NOTE_OFF: {
      int channel = note.getChannel();
      int midiNote = getTransposedMidiNote(note, channel);
      synthesizer.releaseKey(channel, midiNote);
      publish(new OnTransportNoteOff(channel, midiNote));
      break;
    }
    case NOTE_ON: {
      int channel = note.getChannel();
      int midiNote = getTransposedMidiNote(note, channel);
      int velocity = note.getVelocity();
      int program = note.getProgram();
      if (channel != Midi.DRUM && masterProgram != DEFAULT_MASTER_PROGRAM_OFF) {
        program = masterProgram;
      }
      if (currentPrograms[channel] != program) {
        synthesizer.changeProgram(channel, program);
        currentPrograms[channel] = program;
        publish(new OnProgramChange(channel, program));
      }
      int scaledVelocity = Velocity.scale(velocity, percentVelocity);
      switch (accompanimentType) {
      case DRUMS:
        if (channel == Midi.DRUM) {
          synthesizer.pressKey(channel, midiNote, scaledVelocity);
        }
        break;
      case FULL:
        synthesizer.pressKey(channel, midiNote, scaledVelocity);
        break;
      case PIANO:
        // Handled via master program
        synthesizer.pressKey(channel, midiNote, scaledVelocity);
        break;
      case RHYTHM:
        if (channel == Midi.DRUM || (currentPrograms[channel] >= 32 && currentPrograms[channel] < 40)) {
          synthesizer.pressKey(channel, midiNote, scaledVelocity);
        }
        break;
      default:
        break;
      }
      publish(new OnTransportNoteOn(channel, midiNote));
      break;
    }
    case TICK:
      publish(new OnTick(noteEvent.getTick()));
      break;
    case CUE_NOTE_ON:
      int channel = note.getChannel();
      int midiNote = getTransposedMidiNote(note, channel);
      publish(new OnCueNoteOn(channel, midiNote));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private void resume() {
    isPaused = false;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    schedule();
  }

  private void schedule() {
    if (index < noteEvents.size()) {
      NoteEvent noteEvent = noteEvents.get(index++);
      tsGetInputQueue().add(new OnNoteEvent(noteEvent));
    }
  }

  private void seek(long tick, Whence whence) {
    boolean wasPlaying = isPlaying();
    pause();
    tsGetInputQueue().clear(); // e.g. scheduled note events from previous seeks
    long newTick;
    long currentTick = baseTick;
    switch (whence) {
    case RELATIVE:
      newTick = currentTick + tick;
      break;
    case ABSOLUTE:
      newTick = tick;
      break;
    default:
      throw new UnsupportedOperationException();
    }
    if (newTick > currentTick) {
      while (noteEvents.get(index).getTick() < newTick && index < noteEvents.size()) {
        index++;
      }
    } else {
      while (noteEvents.get(index).getTick() > newTick && index > 0) {
        index--;
      }
    }
    baseTick = newTick;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    if (wasPlaying) {
      resume();
    } else {
      publish(new OnTick(newTick));
    }
    publish(new OnSeekFinished(tick, whence));
  }

  private void setAccompaniment(OnSetAccompanimentType.AccompanimentType accompanimentType) {
    this.accompanimentType = accompanimentType;
    if (accompanimentType == OnSetAccompanimentType.AccompanimentType.PIANO) {
      masterProgram = 0;
    } else {
      masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
    }
  }

  private void setCurrentTransposition(int currentTransposition) {
    this.currentTransposition = currentTransposition;
  }

  private void setPercentGain(int gain) {
    synthesizer.setGain(Range.scale(Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, 0, 100, gain));
  }

  private void setPercentTempo(int percentTempo) {
    this.percentTempo = Range.scale(0, 200, 0, 100, percentTempo);
    if (percentTempo == 0) {
      appliedPercentTempo = 1;
    } else {
      appliedPercentTempo = this.percentTempo;
    }
  }

  private void stop() {
    clear();
  }

}