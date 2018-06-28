// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.Iterator;

import com.example.afs.musicpad.song.Note.NoteBuilder;

public class Transposer implements Iterable<Note> {

  public static class TransposerIterator implements Iterator<Note> {

    private Iterator<Note> iterator;
    private int transposition;

    public TransposerIterator(Iterator<Note> iterator, int transposition) {
      this.iterator = iterator;
      this.transposition = transposition;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Note next() {
      Note note = iterator.next();
      return new NoteBuilder().withNote(note).withMidiNote(note.getMidiNote() + transposition).create();
    }

  }

  private Iterable<Note> iterable;
  private int transposition;

  public Transposer(Iterable<Note> iterable, int transposition) {
    this.iterable = iterable;
    this.transposition = transposition;
  }

  @Override
  public Iterator<Note> iterator() {
    return new TransposerIterator(iterable.iterator(), transposition);
  }

}
