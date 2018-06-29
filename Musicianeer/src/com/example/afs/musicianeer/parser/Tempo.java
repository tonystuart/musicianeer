// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.parser;

public class Tempo {
  private int usecPerQuarterNote;
  private int quarterNotesPerMinute;

  public Tempo(int usecPerQuarterNote, int quarterNotesPerMinute) {
    this.usecPerQuarterNote = usecPerQuarterNote;
    this.quarterNotesPerMinute = quarterNotesPerMinute;
  }

  public int getQuarterNotesPerMinute() {
    return quarterNotesPerMinute;
  }

  public int getUsecPerQuarterNote() {
    return usecPerQuarterNote;
  }

  @Override
  public String toString() {
    return "Tempo [usecPerQuarterNote=" + usecPerQuarterNote + ", quarterNotesPerMinute=" + quarterNotesPerMinute + "]";
  }
}