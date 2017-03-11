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
import com.example.afs.musicpad.util.SequencerTask;

public class Transport {

  private Synthesizer synthesizer;
  private SequencerTask<NoteEvent> sequencerTask;
  private Broker<Message> messageBroker;
  private NoteEventScheduler noteEventScheduler;
  private int percentVolume = 100;

  public Transport(Broker<Message> messageBroker, Synthesizer synthesizer) {
    this.messageBroker = messageBroker;
    this.synthesizer = synthesizer;
    noteEventScheduler = new NoteEventScheduler();
    sequencerTask = new SequencerTask<NoteEvent>(noteEventScheduler, new Broker<>());
    sequencerTask.subscribe(NoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
    sequencerTask.start();
  }

  public void play(Song song, int channel) {
    stop();
    for (Note note : song.getNotes()) {
      if (channel == -1 || channel == note.getChannel()) {
        long tick = note.getTick();
        long duration = note.getDuration();
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_ON, tick, note));
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
      }
    }
  }

  public void setPercentTempo(int percentTempo) {
    noteEventScheduler.setPercentTempo(percentTempo);
  }

  public void setPercentVolume(int percentVolume) {
    this.percentVolume = percentVolume;
  }

  public void stop() {
    sequencerTask.getInputQueue().clear();
    synthesizer.allNotesOff();
    noteEventScheduler.reset();
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {
    case NOTE_OFF:
      synthesizer.releaseKey(note.getChannel(), note.getMidiNote());
      break;
    case NOTE_ON:
      //System.out.println(note);
      synthesizer.changeProgram(note.getChannel(), note.getProgram());
      synthesizer.pressKey(note.getChannel(), note.getMidiNote(), scaleVelocity(note.getVelocity()));
      messageBroker.publish(new TickOccurred(noteEvent.getTick()));
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private int scaleVelocity(int velocity) {
    int scaledVelocity = (velocity * percentVolume) / 100;
    if (scaledVelocity < 0) {
      scaledVelocity = 0;
    } else if (scaledVelocity > 127) {
      scaledVelocity = 127;
    }
    //System.out.println("velocity=" + velocity + ", scaledVelocity=" + scaledVelocity);
    return scaledVelocity;
  }
}
