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
import com.example.afs.musicpad.message.OnPlay;
import com.example.afs.musicpad.message.OnStop;
import com.example.afs.musicpad.message.OnTempo;
import com.example.afs.musicpad.message.OnVolume;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.task.SequencerTask;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.util.Broker;

public class TransportTask extends BrokerTask<Message> {

  private Synthesizer synthesizer;
  private SequencerTask<NoteEvent> sequencerTask;
  private NoteEventScheduler noteEventScheduler;
  private int percentVolume = 100;

  public TransportTask(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker);
    this.synthesizer = synthesizer;
    subscribe(OnPlay.class, message -> onPlay(message.getSong(), message.getChannel()));
    subscribe(OnStop.class, message -> onStop());
    subscribe(OnVolume.class, message -> onVolume(message.getPercentVolume()));
    subscribe(OnTempo.class, message -> onTempo(message.getPercentTempo()));
    noteEventScheduler = new NoteEventScheduler();
    sequencerTask = new SequencerTask<NoteEvent>(noteEventScheduler, new Broker<>());
    sequencerTask.subscribe(NoteEvent.class, noteEvent -> processNoteEvent(noteEvent));
    sequencerTask.start();
  }

  private void onPlay(Song song, int channel) {
    onStop();
    for (Note note : song.getNotes()) {
      if (channel == -1 || channel == note.getChannel()) {
        long tick = note.getTick();
        long duration = note.getDuration();
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_ON, tick, note));
        sequencerTask.getInputQueue().add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
      }
    }
  }

  private void onStop() {
    sequencerTask.getInputQueue().clear();
    synthesizer.allNotesOff();
    noteEventScheduler.reset();
  }

  private void onTempo(int percentTempo) {
    noteEventScheduler.setPercentTempo(percentTempo);
  }

  private void onVolume(int percentVolume) {
    this.percentVolume = percentVolume;
  }

  private void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    switch (noteEvent.getType()) {
    case NOTE_OFF:
      synthesizer.releaseKey(note.getChannel(), note.getMidiNote());
      break;
    case NOTE_ON:
      synthesizer.changeProgram(note.getChannel(), note.getProgram());
      synthesizer.pressKey(note.getChannel(), note.getMidiNote(), scaleVelocity(note.getVelocity()));
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
    return scaledVelocity;
  }
}
