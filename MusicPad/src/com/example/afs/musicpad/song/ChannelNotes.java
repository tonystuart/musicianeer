// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

public class ChannelNotes implements Iterator<Note>, Iterable<Note> {

  public static final int ALL_CHANNELS = -1;

  private int channel;
  private Note nextChannelNote;
  private Iterator<Note> iterator;

  public ChannelNotes(NavigableSet<Note> notes, int channel) {
    this.channel = channel;
    this.iterator = notes.iterator();
  }

  @Override
  public boolean hasNext() {
    loadNextChannelNote();
    return nextChannelNote != null;
  }

  @Override
  public Iterator<Note> iterator() {
    return this;
  }

  @Override
  public Note next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    Note note = nextChannelNote;
    nextChannelNote = null;
    return note;
  }

  private void loadNextChannelNote() {
    while (iterator.hasNext() && nextChannelNote == null) {
      Note note = iterator.next();
      if (channel == ALL_CHANNELS || note.getChannel() == channel) {
        nextChannelNote = note;
      }
    }
  }

}