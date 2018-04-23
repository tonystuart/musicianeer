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

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.task.Message;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.transport.NoteEvent;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.transport.NoteEventSequencer;
import com.example.afs.musicpad.util.Range;
import com.example.afs.musicpad.util.Velocity;

// NB: All methods are single threaded via Musicianeer
public class Transport {

  public enum Whence {
    RELATIVE, ABSOLUTE
  }

  public static final int DEFAULT_PERCENT_GAIN = 10;
  public static final int DEFAULT_PERCENT_TEMPO = 50;
  public static final int DEFAULT_PERCENT_VELOCITY = 10;
  public static final int DEFAULT_MASTER_PROGRAM_OFF = Midi.MAX_VALUE;

  private int masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private Synthesizer synthesizer;
  private MessageBroker messageBroker;
  private NoteEventSequencer sequencer;
  private Deque<NoteEvent> reviewQueue = new LinkedList<>();
  private OnSetAccompanimentType.AccompanimentType accompanimentType = OnSetAccompanimentType.AccompanimentType.FULL;

  public Transport(MessageBroker messageBroker, Synthesizer synthesizer) {
    this.messageBroker = messageBroker;
    this.synthesizer = synthesizer;
    this.sequencer = new NoteEventSequencer(noteEvent -> processNoteEvent(noteEvent));
    setPercentGain(DEFAULT_PERCENT_GAIN);
    sequencer.tsStart();
  }

  public void allNotesOff() {
    synthesizer.allNotesOff();
  }

  public int getMasterProgram() {
    return masterProgram;
  }

  public int getPercentGain() {
    return (int) Range.scale(0, 100, Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, synthesizer.getGain());
  }

  public int getPercentTempo() {
    return Range.scale(0, 100, 0, 200, sequencer.getPercentTempo());
  }

  public int getPercentVelocity() {
    return percentVelocity;
  }

  public long getTick() {
    return sequencer.getTick();
  }

  public boolean isEmpty() {
    return sequencer.tsGetInputQueue().size() == 0;
  }

  public boolean isPaused() {
    return sequencer.isPaused();
  }

  public void muteAllChannels(boolean isMuted) {
    synthesizer.muteAllChannels(isMuted);
  }

  public void pause() {
    sequencer.setPaused(true);
    synthesizer.allNotesOff();
  }

  public void play(Iterable<Note> notes) {
    clear();
    long firstTick = -1;
    long lastTick = -1;
    long metronomeTick = -1;
    BlockingQueue<NoteEvent> inputQueue = sequencer.tsGetInputQueue();
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
        inputQueue.add(new NoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
      }
      long endTick = beginTick + duration;
      lastTick = Math.max(lastTick, endTick);
      inputQueue.add(new NoteEvent(Type.NOTE_ON, beginTick, note));
      inputQueue.add(new NoteEvent(Type.CUE_NOTE_ON, beginTick - 1024, note));
      inputQueue.add(new NoteEvent(Type.NOTE_OFF, endTick, note));
      inputQueue.add(new NoteEvent(Type.CUE_NOTE_OFF, endTick - 1024, note));
      beatsPerMinute = note.getBeatsPerMinute();
    }
    long lastTickRoundedUp = ((lastTick + 1) / Default.RESOLUTION) * Default.RESOLUTION;
    while (metronomeTick < lastTickRoundedUp) {
      metronomeTick = ((metronomeTick + Default.RESOLUTION + 1) / Default.RESOLUTION) * Default.RESOLUTION;
      inputQueue.add(new NoteEvent(Type.TICK, metronomeTick, beatsPerMinute));
    }
  }

  public void resume() {
    sequencer.setPaused(false);
  }

  public void seek(long tick, Whence whence) {
    boolean wasPlaying = sequencer.isPlaying();
    pause();
    long newTick;
    long currentTick = getTick();
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
    BlockingQueue<NoteEvent> inputQueue = sequencer.tsGetInputQueue();
    if (newTick > currentTick) {
      NoteEvent noteEvent;
      while ((noteEvent = inputQueue.peek()) != null && noteEvent.getTick() < newTick) {
        reviewQueue.add(noteEvent);
        inputQueue.poll();
      }
    } else {
      boolean isInRange = true;
      Iterator<NoteEvent> iterator = reviewQueue.descendingIterator();
      while (iterator.hasNext() && isInRange) {
        NoteEvent noteEvent = iterator.next();
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
  }

  public void setAccompaniment(OnSetAccompanimentType.AccompanimentType accompanimentType) {
    this.accompanimentType = accompanimentType;
    if (accompanimentType == OnSetAccompanimentType.AccompanimentType.PIANO) {
      masterProgram = 0;
    } else {
      masterProgram = DEFAULT_MASTER_PROGRAM_OFF;
    }
  }

  public void setMasterProgram(int masterProgram) {
    this.masterProgram = masterProgram;
  }

  public void setPercentGain(int gain) {
    synthesizer.setGain(Range.scale(Synthesizer.MINIMUM_GAIN, Synthesizer.MAXIMUM_GAIN, 0, 100, gain));
  }

  public void setPercentTempo(int percentTempo) {
    sequencer.setPercentTempo(Range.scale(0, 200, 0, 100, percentTempo));
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  public void stop() {
    clear();
  }

  private void clear() {
    sequencer.clear();
    synthesizer.allNotesOff();
    reviewQueue.clear();
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {

    case NOTE_OFF: {
      int channel = note.getChannel();
      int midiNote = note.getMidiNote();
      synthesizer.releaseKey(channel, midiNote);
      publish(new OnTransportNoteOff(channel, midiNote));
      break;
    }
    case NOTE_ON: {
      int channel = note.getChannel();
      int midiNote = note.getMidiNote();
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
      publish(new OnTransportNoteOn(channel, midiNote));
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
      case SOLO:
        break;
      default:
        break;
      }
      break;
    }
    case TICK:
      publish(new OnTick(noteEvent.getTick()));
      break;
    case CUE_NOTE_OFF:
      publish(new OnCueNoteOff(note.getChannel(), note.getMidiNote()));
      break;
    case CUE_NOTE_ON:
      publish(new OnCueNoteOn(note.getChannel(), note.getMidiNote()));
      break;
    default:
      throw new UnsupportedOperationException();
    }
    reviewQueue.add(noteEvent);
  }

  private void publish(Message message) {
    messageBroker.publish(message);
  }

  private void setBaseTick(long baseTick) {
    sequencer.getScheduler().setBaseTick(baseTick);
    sequencer.getScheduler().resetBaseTime();
  }

}