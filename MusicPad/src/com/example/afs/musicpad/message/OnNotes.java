// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.task.Message;

public class OnNotes implements Message {

  private Iterable<Note> notes;

  public OnNotes(Iterable<Note> notes) {
    this.notes = notes;
  }

  public Iterable<Note> getNotes() {
    return notes;
  }

  @Override
  public String toString() {
    return "OnNotes [notes=" + notes + "]";
  }

}
