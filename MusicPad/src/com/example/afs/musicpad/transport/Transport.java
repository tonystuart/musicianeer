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
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.TickOccurred;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.Scheduler;
import com.example.afs.musicpad.util.SequencerTask;

public class Transport {

  private class NoteEventSequencer extends SequencerTask<NoteEvent> {
    private NoteEventSequencer(Scheduler<NoteEvent> scheduler, Broker<NoteEvent> broker) {
      super(scheduler, broker);
    }

    @Override
    protected void run() {
      super.run();
      synthesizer.allNotesOff();
    }
  }

  private Synthesizer synthesizer;
  private SequencerTask<NoteEvent> sequencerTask;
  private Broker<Message> messageBroker;

  public Transport(Broker<Message> messageBroker, Synthesizer synthesizer) {
    this.messageBroker = messageBroker;
    this.synthesizer = synthesizer;
  }

  public void play(Song song, int channel) {
    Broker<NoteEvent> noteBroker = new Broker<>();
    NoteEventScheduler noteEventScheduler = new NoteEventScheduler();
    sequencerTask = new NoteEventSequencer(noteEventScheduler, noteBroker);
    sequencerTask.subscribe(NoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
    sequencerTask.start();
    for (Note note : song.getNotes()) {
      if (channel == -1 || channel == note.getChannel()) {
        long tick = note.getTick();
        long duration = note.getDuration();
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_ON, tick, note));
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
      }
    }
  }

  public void stop() {
    if (sequencerTask != null) {
      sequencerTask.terminate();
    }
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {
    case NOTE_OFF:
      synthesizer.releaseKey(note.getChannel(), note.getMidiNote());
      break;
    case NOTE_ON:
      System.out.println(note);
      synthesizer.changeProgram(note.getChannel(), note.getProgram());
      synthesizer.pressKey(note.getChannel(), note.getMidiNote(), note.getVelocity());
      messageBroker.publish(new TickOccurred(noteEvent.getTick()));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }
}
