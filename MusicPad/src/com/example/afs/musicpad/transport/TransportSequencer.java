// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import java.util.NavigableSet;
import java.util.concurrent.BlockingQueue;

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.transport.NoteEvent.Type;

public class TransportSequencer extends NoteEventSequencer {

  public TransportSequencer(NoteEventProcessor noteEventProcessor) {
    super(noteEventProcessor);
  }

  public void play(Song song, int channel, Note firstNote, Note lastNote) {
    long firstTick = firstNote.getTick();
    getScheduler().setBaseTick(firstTick);
    getScheduler().resetBaseTime();
    BlockingQueue<NoteEvent> inputQueue = getInputQueue();
    NavigableSet<Note> notes = song.getNotes().subSet(firstNote, true, lastNote, false);
    for (Note note : notes) {
      if (channel == -1 || channel == note.getChannel()) {
        long tick = note.getTick();
        long duration = note.getDuration();
        inputQueue.add(new NoteEvent(Type.NOTE_ON, tick, note));
        inputQueue.add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
      }
    }
    for (long tick = 0; tick < lastNote.getTick(); tick += Default.RESOLUTION) {
      int beatsPerMinute;
      Note previousNote = notes.lower(new Note(tick));
      if (previousNote == null) {
        beatsPerMinute = Default.BEATS_PER_MINUTE;
      } else {
        beatsPerMinute = previousNote.getBeatsPerMinute();
      }
      inputQueue.add(new NoteEvent(Type.TICK, tick, beatsPerMinute));
    }
  }

}
