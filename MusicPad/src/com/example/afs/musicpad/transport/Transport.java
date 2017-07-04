// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Velocity;

public class Transport {

  public interface TickHandler {
    void onTick(long tick);
  }

  public enum Whence {
    RELATIVE, ABSOLUTE
  }

  private static final int DEFAULT_PERCENT_VELOCITY = 25;
  private static final float DEFAULT_GAIN = 5 * Synthesizer.DEFAULT_GAIN;

  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private int[] currentPrograms = new int[Midi.CHANNELS];

  private Synthesizer synthesizer;
  private NoteEventSequencer sequencer;
  private TickHandler tickHandler;
  private Deque<NoteEvent> reviewQueue = new LinkedList<>();
  private float gain;

  public Transport(Synthesizer synthesizer) {
    this(synthesizer, null);
  }

  public Transport(Synthesizer synthesizer, TickHandler tickHandler) {
    this.synthesizer = synthesizer;
    this.tickHandler = tickHandler;
    this.sequencer = new NoteEventSequencer(noteEvent -> processNoteEvent(noteEvent));
    setGain(DEFAULT_GAIN);
    sequencer.start();
  }

  public void allNotesOff() {
    synthesizer.allNotesOff();
  }

  public float getGain() {
    return gain;
  }

  public int getPercentTempo() {
    return sequencer.getPercentTempo();
  }

  public int getPercentVelocity() {
    return percentVelocity;
  }

  public long getTick() {
    return sequencer.getTick();
  }

  public boolean isPaused() {
    return sequencer.isPaused();
  }

  public void pause() {
    sequencer.setPaused(true);
    synthesizer.allNotesOff();
  }

  public void play(Iterable<Note> notes) {
    reset();
    long firstTick = -1;
    long lastTick = -1;
    long metronomeTick = -1;
    BlockingQueue<NoteEvent> inputQueue = sequencer.getInputQueue();
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
      inputQueue.add(new NoteEvent(Type.NOTE_OFF, endTick, note));
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
    BlockingQueue<NoteEvent> inputQueue = sequencer.getInputQueue();
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
      fireTick(newTick);
    }
  }

  public void setGain(float gain) {
    this.gain = gain;
    synthesizer.setGain(gain);
  }

  public void setPercentTempo(int percentTempo) {
    sequencer.setPercentTempo(percentTempo);
  }

  public void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  public void stop() {
    reset();
  }

  private void fireTick(long tick) {
    if (tickHandler != null) {
      tickHandler.onTick(tick);
    }
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
      fireTick(noteEvent.getTick());
      break;
    default:
      throw new UnsupportedOperationException();
    }
    reviewQueue.add(noteEvent);
  }

  private void reset() {
    sequencer.reset();
    synthesizer.allNotesOff();
    reviewQueue.clear();
  }

  private void setBaseTick(long baseTick) {
    sequencer.getScheduler().setBaseTick(baseTick);
    sequencer.getScheduler().resetBaseTime();
  }

}