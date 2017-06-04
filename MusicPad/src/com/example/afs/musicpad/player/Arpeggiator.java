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

import com.example.afs.musicpad.player.Arpeggiation.TickInterval;
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
    int index = 0;
    BlockingQueue<NoteEvent> inputQueue = getInputQueue();
    Arpeggiation arpeggiation = sound.getArpeggiation();
    for (TickInterval tickInterval : arpeggiation.getTickIntervals()) {
      long tick = tickInterval.getStart();
      long duration = tickInterval.getDuration();
      int bpm = tickInterval.getBeatsPerMinute();
      int midiNote = sound.getMidiNotes()[index++];
      Note note = new Note.NoteBuilder().withTick(tick).withMidiNote(midiNote).withBeatsPerMinute(bpm).create();
      inputQueue.add(new NoteEvent(Type.NOTE_ON, tick, note));
      inputQueue.add(new NoteEvent(Type.NOTE_OFF, tick + duration, note));
    }
  }

}
