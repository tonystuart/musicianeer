// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.transport;

import java.util.Collections;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicianeer.main.Musicianeer;
import com.example.afs.musicianeer.main.Services;
import com.example.afs.musicianeer.message.OnCueNoteOn;
import com.example.afs.musicianeer.message.OnNoteEvent;
import com.example.afs.musicianeer.message.OnNotes;
import com.example.afs.musicianeer.message.OnPlay;
import com.example.afs.musicianeer.message.OnSeek;
import com.example.afs.musicianeer.message.OnSetAccompanimentType;
import com.example.afs.musicianeer.message.OnSetPercentMasterGain;
import com.example.afs.musicianeer.message.OnSetPercentTempo;
import com.example.afs.musicianeer.message.OnSetPercentVelocity;
import com.example.afs.musicianeer.message.OnStop;
import com.example.afs.musicianeer.message.OnTick;
import com.example.afs.musicianeer.message.OnTransportNoteOff;
import com.example.afs.musicianeer.message.OnTransportNoteOn;
import com.example.afs.musicianeer.message.OnTransportProgramChange;
import com.example.afs.musicianeer.message.OnTransposition;
import com.example.afs.musicianeer.midi.Midi;
import com.example.afs.musicianeer.song.Default;
import com.example.afs.musicianeer.song.Note;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.ServiceTask;
import com.example.afs.musicianeer.transport.NoteEvent.Type;
import com.example.afs.musicianeer.util.DirectList;
import com.example.afs.musicianeer.util.RandomAccessList;
import com.example.afs.musicianeer.util.Range;
import com.example.afs.musicianeer.util.Tick;
import com.example.afs.musicianeer.util.Velocity;

public class Transport extends ServiceTask {

  public enum Whence {
    RELATIVE, ABSOLUTE
  }

  private enum State {
    PLAY, PAUSE, STOP
  }

  public static final int DEFAULT_PERCENT_GAIN = 10;
  public static final int DEFAULT_PERCENT_TEMPO = 50;
  public static final int DEFAULT_PERCENT_VELOCITY = 10;

  private static final long INITIALIZE_ON_NEXT_EVENT = -1;

  private int index;
  private int currentTransposition;
  private int masterProgramOverride = Musicianeer.UNSET;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private long baseTick;
  private long baseTimeMillis;
  private int percentTempo = 100;
  private int appliedPercentTempo = percentTempo;

  private State state;
  private Iterable<Note> notes;
  private Synthesizer synthesizer;
  private RandomAccessList<NoteEvent> noteEvents = new DirectList<>();
  private OnSetAccompanimentType.AccompanimentType accompanimentType = OnSetAccompanimentType.AccompanimentType.FULL;

  public Transport(MessageBroker messageBroker, Synthesizer synthesizer) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    setPercentGain(DEFAULT_PERCENT_GAIN);
    provide(Services.getPercentTempo, () -> getPercentTempo());
    provide(Services.getAccompanimentType, () -> accompanimentType);
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
    if (state == State.PLAY) {
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
    if (state == State.PAUSE) {
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
    if (state == State.PAUSE) {
      stop();
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
    return state == State.PLAY && index < noteEvents.size();
  }

  private void pause() {
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    synthesizer.allNotesOff();
    state = State.PAUSE;
  }

  private void play(Iterable<Note> notes) {
    reset();
    noteEvents.clear();
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
    state = State.PLAY;
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
      if (channel != Midi.DRUM && masterProgramOverride != Musicianeer.UNSET) {
        program = masterProgramOverride;
      }
      if (currentPrograms[channel] != program) {
        // Defer all synthesizer program changes to Musicianeer to avoid piano accompaniment -> user override issues
        // synthesizer.changeProgram(channel, program);
        currentPrograms[channel] = program;
        if (masterProgramOverride == Musicianeer.UNSET) {
          publish(new OnTransportProgramChange(channel, program));
        }
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

  private void processSeekEvent(Type noteOnType, Type noteOffType) {
    NoteEvent noteEvent = noteEvents.get(index);
    Type type = noteEvent.getType();
    if (type == noteOnType) {
      Note note = noteEvent.getNote();
      int channel = note.getChannel();
      publish(new OnTransportNoteOn(channel, getTransposedMidiNote(note, channel)));
    } else if (type == noteOffType) {
      Note note = noteEvent.getNote();
      int channel = note.getChannel();
      publish(new OnTransportNoteOff(channel, getTransposedMidiNote(note, channel)));
    }
  }

  private void reset() {
    tsGetInputQueue().clear();
    synthesizer.allNotesOff();
    baseTick = 0;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    index = 0;
  }

  private void resume() {
    state = State.PLAY;
    baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
    schedule();
  }

  private void schedule() {
    NoteEvent noteEvent = null;
    long currentTimeMillis = System.currentTimeMillis();
    while (index < noteEvents.size() && getEventTimeMillis((noteEvent = noteEvents.get(index++))) <= currentTimeMillis) {
      processNoteEvent(noteEvent);
    }
    if (noteEvent != null) {
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
      index = Math.max(0, index);
      while (index < noteEvents.size() && noteEvents.get(index).getTick() < newTick) {
        processSeekEvent(Type.NOTE_ON, Type.NOTE_OFF);
        index++;
      }
    } else {
      index = Math.min(noteEvents.size() - 1, index);
      while (index > 0 && noteEvents.get(index).getTick() > newTick) {
        processSeekEvent(Type.NOTE_OFF, Type.NOTE_ON);
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
  }

  private void setAccompaniment(OnSetAccompanimentType.AccompanimentType accompanimentType) {
    this.accompanimentType = accompanimentType;
    if (accompanimentType == OnSetAccompanimentType.AccompanimentType.PIANO) {
      masterProgramOverride = 0;
    } else {
      masterProgramOverride = Musicianeer.UNSET;
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
    reset();
    publish(new OnTick(0));
    state = State.STOP;
  }

}