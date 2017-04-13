// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import java.util.Iterator;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.task.PausibleSequencerTask;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Value;
import com.example.afs.musicpad.util.Velocity;

public class TransportTask extends BrokerTask<Message> {

  public static final int DEFAULT_PERCENT_VELOCITY = 25;

  private static final long FIRST_NOTE = -1;
  private static final long LAST_NOTE = -1;

  private Synthesizer synthesizer;
  private PausibleSequencerTask<NoteEvent> sequencerTask;
  private NoteEventScheduler noteEventScheduler;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private Song song = Song.DEFAULT;

  private int currentChannel;

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.synthesizer = synthesizer;
    subscribe(OnSongSelected.class, message -> doSongSelected(message.getSong()));
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    noteEventScheduler = new NoteEventScheduler();
    sequencerTask = new PausibleSequencerTask<NoteEvent>(noteEventScheduler, new Broker<>());
    sequencerTask.subscribe(NoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
    sequencerTask.start();
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
    case SET_TRANSPORT_TEMPO:
      doSetPercentTempo(parameter);
      break;
    case SET_TRANSPORT_VELOCITY:
      doSetPercentVelocity(parameter);
      break;
    default:
      break;
    }
  }

  private void doNextMeasure() {
    long tick = noteEventScheduler.getTick();
    int ticksPerMeasure = song.getTicksPerMeasure(tick);
    int measure = (int) (tick / ticksPerMeasure);
    int nextMeasure = measure + 1;
    long baseTick = nextMeasure * ticksPerMeasure;
    System.out.println("Moving from measure " + measure + " to measure " + nextMeasure);
    play(currentChannel, baseTick);
  }

  private void doPause() {
    pause();
  }

  private void doPlay(int channelNumber) {
    int channel = Value.toIndex(channelNumber);
    play(channel, FIRST_NOTE);
  }

  private void doPlayPause(int channelNumber) {
    if (sequencerTask.isPaused()) {
      resume();
    } else {
      int channel = Value.toIndex(channelNumber);
      play(channel, FIRST_NOTE);
    }
  }

  private void doPlaySample(int channelNumber) {
    int channel = Value.toIndex(channelNumber);
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
      if (currentChannel == -1) {
        System.out.println("Moving from measure " + measure + " to measure " + previousMeasure);
        play(currentChannel, toElement.getTick());
        return;
      } else {
        Iterator<Note> iterator = song.getNotes().headSet(toElement, true).descendingIterator();
        while (iterator.hasNext()) {
          Note note = iterator.next();
          if (currentChannel == note.getChannel()) {
            System.out.println("Moving from measure " + measure + " to measure " + previousMeasure);
            play(currentChannel, toElement.getTick());
            return;
          }
        }
      }
    }
  }

  private void doResume() {
    resume();
  }

  private void doSetPercentTempo(int percentTempo) {
    noteEventScheduler.setPercentTempo(percentTempo);
  }

  private void doSetPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
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

  private void play(int channel, long baseTick) {
    play(channel, baseTick, LAST_NOTE);
  }

  private void play(int channel, long baseTick, long baseDuration) {
    stop();
    currentChannel = channel;
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
      for (Note note : song.getNotes().subSet(firstNote, lastNote)) {
        if (channel == -1 || channel == note.getChannel()) {
          long tick = note.getTick();
          long duration = note.getDuration();
          sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_ON, tick, note));
          sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
        }
      }
    }
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
  }
}
