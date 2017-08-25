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
import com.example.afs.musicpad.util.RandomAccessList;

public class Arpeggiator extends NoteEventSequencer {

  public Arpeggiator(NoteEventProcessor noteEventProcessor) {
    super(noteEventProcessor);
  }

  public void play(Sound sound) {
    reset();
    BlockingQueue<NoteEvent> inputQueue = getInputQueue();
    RandomAccessList<Note> notes = sound.getNotes();
    int noteCount = notes.size();
    int lastNoteIndex = noteCount - 1;
    for (int i = 0; i < noteCount; i++) {
      Note note = notes.get(i);
      long tick = note.getTick();
      long duration = note.getDuration();
      if (i == 0) {
        getScheduler().setBaseTick(tick);
      }
      inputQueue.add(new NoteEvent(Type.NOTE_ON, tick, note));
      inputQueue.add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
      if (i == lastNoteIndex) {
        long tickOfNextMeasure = note.getTickOfNextMeasure();
        inputQueue.add(new NoteEvent(Type.TICK, tickOfNextMeasure, note.getBeatsPerMinute()));
      }
    }
  }

}
