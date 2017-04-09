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
import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.task.PausibleSequencerTask;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Velocity;

public class TransportTask extends BrokerTask<Message> {

  public static final int DEFAULT_PERCENT_VELOCITY = 75;

  private Synthesizer synthesizer;
  private PausibleSequencerTask<NoteEvent> sequencerTask;
  private NoteEventScheduler noteEventScheduler;
  private int percentVelocity = DEFAULT_PERCENT_VELOCITY;
  private Song song;

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
      play(parameter);
      break;
    case PAUSE:
      pause(parameter);
      break;
    case RESUME:
      resume(parameter);
      break;
    case STOP:
      stop();
      break;
    case SET_TRANSPORT_TEMPO:
      setPercentTempo(parameter);
      break;
    case SET_TRANSPORT_VELOCITY:
      setPercentVelocity(parameter);
      break;
    default:
      break;
    }
  }

  private void doSongSelected(Song song) {
    stop();
    this.song = song;
  }

  private void pause(int parameter) {
    sequencerTask.pause();
    noteEventScheduler.resetBaseTime();
    synthesizer.allNotesOff();
  }

  private void play(int channelNumber) {
    if (song != null) {
      stop();
      boolean isFirstTick = true;
      int channel = channelNumber - 1;
      for (Note note : song.getNotes()) {
        if (channel == -1 || channel == note.getChannel()) {
          long tick = note.getTick();
          long duration = note.getDuration();
          if (isFirstTick) {
            noteEventScheduler.setBaseTick(tick);
            noteEventScheduler.resetBaseTime();
            isFirstTick = false;
          }
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

  private void resume(int parameter) {
    sequencerTask.resume();
  }

  private void setPercentTempo(int percentTempo) {
    noteEventScheduler.setPercentTempo(percentTempo);
  }

  private void setPercentVelocity(int percentVelocity) {
    this.percentVelocity = percentVelocity;
  }

  private void stop() {
    sequencerTask.getInputQueue().clear();
    synthesizer.allNotesOff();
    noteEventScheduler.resetAll();
  }
}
