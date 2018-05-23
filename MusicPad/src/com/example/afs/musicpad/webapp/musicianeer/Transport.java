// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Tick;
import com.example.afs.musicpad.util.Velocity;
import com.example.afs.musicpad.webapp.musicianeer.OnNoteEvent.Type;

public class Transport extends MessageTask {

  public interface NoteEventProcessor {
    void processNoteEvent(OnNoteEvent noteEvent);
  }

  public enum Whence {
    RELATIVE, ABSOLUTE
  }

  public static final int DEFAULT_PERCENT_GAIN = 10;
  public static final int DEFAULT_PERCENT_TEMPO = 50;
  public static final int DEFAULT_PERCENT_VELOCITY = 10;

  public static final int DEFAULT_MASTER_PROGRAM_OFF = Midi.MAX_VALUE;
  public static final long INITIALIZE_ON_NEXT_EVENT = -1;

  private int currentTransposition;
  private int masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private Synthesizer synthesizer;
  private Deque<OnNoteEvent> reviewQueue = new LinkedList<>();
  private OnSetAccompanimentType.AccompanimentType accompanimentType = OnSetAccompanimentType.AccompanimentType.FULL;

  private long baseTick;
  private long baseTimeMillis;
  private int percentTempo = 100;
  private int appliedPercentTempo = percentTempo;
  private boolean isPaused;
  private PriorityBlockingQueue<Message> inputQueue;

  public Transport(MessageBroker messageBroker, Synthesizer synthesizer) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    setPercentGain(DEFAULT_PERCENT_GAIN);
    subscribe(OnNoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
  }

  public void allNotesOff() {
    synthesizer.allNotesOff();
  }

  public void clear() {
    tsGetInputQueue().clear();
    setPaused(false);
    resetAll();
    synthesizer.allNotesOff();
    reviewQueue.clear();
  }

  public long getEventTimeMillis(long noteTick, int beatsPerMinute) {
    if (baseTimeMillis == INITIALIZE_ON_NEXT_EVENT || noteTick < baseTick) {
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

  public long getEventTimeMillis(OnNoteEvent noteEvent) {
    return getEventTimeMillis(noteEvent.getTick(), noteEvent.getBeatsPerMinute());
  }

  public int getPercentGain() {
    return (int) Range.scale(0, 100, Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, synthesizer.getGain());
  }

  public int getPercentTempo() {
    return Range.scale(0, 100, 0, 200, percentTempo);
  }

  public boolean isPaused() {
    return isPaused;
  }

  public boolean isPlaying() {
    return !isPaused && tsGetInputQueue().size() > 0;
  }

  public void pause() {
    setPaused(true);
    synthesizer.allNotesOff();
  }

  public void play(Iterable<Note> notes) {
    clear();
    long firstTick = -1;
    long lastTick = -1;
    long metronomeTick = -1;
    int beatsPerMinute = Default.BEATS_PER_MINUTE;
    for (Note note : notes) {
      long beginTick = note.getTick();
      long duration = note.getDuration();
      if (firstTick == -1) {
        firstTick = beginTick;
        setBaseTick(firstTick);
      }
      long beginTickRoundedUp = ((beginTick + 1) / Default.RESOLUTION) * Default.RESOLUTION;
      while (metronomeTick < beginTickRoundedUp) {
        metronomeTick = ((metronomeTick + Default.RESOLUTION + 1) / Default.RESOLUTION) * Default.RESOLUTION;
        inputQueue.add(new OnNoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
      }
      long endTick = beginTick + duration;
      lastTick = Math.max(lastTick, endTick);
      inputQueue.add(new OnNoteEvent(Type.NOTE_ON, beginTick, note));
      inputQueue.add(new OnNoteEvent(Type.CUE_NOTE_ON, beginTick - 1024, note));
      inputQueue.add(new OnNoteEvent(Type.NOTE_OFF, endTick, note));
      beatsPerMinute = note.getBeatsPerMinute();
    }
    long lastTickRoundedUp = ((lastTick + 1) / Default.RESOLUTION) * Default.RESOLUTION;
    while (metronomeTick < lastTickRoundedUp) {
      metronomeTick = ((metronomeTick + Default.RESOLUTION + 1) / Default.RESOLUTION) * Default.RESOLUTION;
      inputQueue.add(new OnNoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
    }
  }

  public void resetAll() {
    this.baseTick = 0;
    this.baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  public void resetBaseTime() {
    this.baseTimeMillis = INITIALIZE_ON_NEXT_EVENT;
  }

  public void resume() {
    setPaused(false);
  }

  public void seek(long tick, Whence whence) {
    boolean wasPlaying = isPlaying();
    pause();
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
      boolean done = false;
      do {
        Message message = inputQueue.poll();
        if (message == null) {
          done = true;
        }
        if (message instanceof OnNoteEvent) {
          OnNoteEvent onNoteEvent = (OnNoteEvent) message;
          if (onNoteEvent.getTick() < newTick) {
            reviewQueue.add(onNoteEvent);
          } else {
            inputQueue.add(onNoteEvent);
            done = true;
          }
        } else {
          // TODO: Dispatch the message?
        }
      } while (!done);
    } else {
      boolean isInRange = true;
      Iterator<OnNoteEvent> iterator = reviewQueue.descendingIterator();
      while (iterator.hasNext() && isInRange) {
        OnNoteEvent noteEvent = iterator.next();
        long noteEventTick = noteEvent.getTick();
        isInRange = noteEventTick > newTick;
        if (isInRange) {
          inputQueue.add(noteEvent);
          iterator.remove();
        }
      }
    }
    setBaseTick(newTick);
    if (wasPlaying) {
      resume();
    } else {
      publish(new OnTick(newTick));
    }
    publish(new OnSeekFinished(tick, whence));
  }

  public void setAccompaniment(OnSetAccompanimentType.AccompanimentType accompanimentType) {
    this.accompanimentType = accompanimentType;
    if (accompanimentType == OnSetAccompanimentType.AccompanimentType.PIANO) {
      masterProgram = 0;
    } else {
      masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
    }
  }

  public void setBaseTimeMillis(long baseTimeMillis) {
    this.baseTimeMillis = baseTimeMillis;
  }

  public void setCurrentTransposition(int currentTransposition) {
    this.currentTransposition = currentTransposition;
  }

  public void setMasterProgram(int masterProgram) {
    this.masterProgram = masterProgram;
  }

  public void setPaused(boolean isPaused) {
    this.isPaused = isPaused;
    if (isPaused) {
      resetBaseTime();
    }
  }

  public void setPercentGain(int gain) {
    synthesizer.setGain(Range.scale(Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, 0, 100, gain));
  }

  public void setPercentTempo(int percentTempo) {
    this.percentTempo = Range.scale(0, 200, 0, 100, percentTempo);
    if (percentTempo == 0) {
      appliedPercentTempo = 1;
    } else {
      appliedPercentTempo = this.percentTempo;
    }
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  public void stop() {
    clear();
  }

  @Override
  protected BlockingQueue<Message> createInputQueue() {
    inputQueue = new PriorityBlockingQueue<>();
    return inputQueue;
  }

  @Override
  protected void processMessage(Message message) throws InterruptedException {
    if (message instanceof OnNoteEvent) {
      long currentTimestamp = System.currentTimeMillis();
      long eventTimestamp = getEventTimeMillis((OnNoteEvent) message);
      if (eventTimestamp > currentTimestamp) {
        long sleepInterval = eventTimestamp - currentTimestamp;
        Thread.sleep(sleepInterval);
      }
      super.processMessage(message);
      while (isPaused && !isTerminated()) {
        Thread.sleep(250);
      }
    } else {
      super.processMessage(message);
    }
  }

  private int getTransposedMidiNote(Note note, int channel) {
    int midiNote = note.getMidiNote();
    if (currentTransposition != 0 && channel != Midi.DRUM) {
      midiNote += currentTransposition;
    }
    return midiNote;
  }

  private void processNoteEvent(OnNoteEvent noteEvent) {
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
    reviewQueue.add(noteEvent);
  }

  private void setBaseTick(long baseTick) {
    this.baseTick = baseTick;
    resetBaseTime();
  }

}