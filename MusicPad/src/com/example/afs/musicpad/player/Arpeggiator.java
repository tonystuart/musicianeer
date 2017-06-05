// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.concurrent.BlockingQueue;

import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.transport.NoteEvent;
import com.example.afs.musicpad.transport.NoteEvent.Type;
import com.example.afs.musicpad.transport.NoteEventSequencer;

public class Arpeggiator extends NoteEventSequencer {

  public Arpeggiator(NoteEventProcessor noteEventProcessor) {
    super(noteEventProcessor);
  }

  public void play(Sound sound) {
    reset();
    boolean firstNote = true;
    BlockingQueue<NoteEvent> inputQueue = getInputQueue();
    for (Note note : sound.getNotes()) {
      long tick = note.getTick();
      long duration = note.getDuration();
      if (firstNote) {
        firstNote = false;
        getScheduler().setBaseTick(tick);
      }
      inputQueue.add(new NoteEvent(Type.NOTE_ON, tick, note));
      inputQueue.add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
    }
  }

}
