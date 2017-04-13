// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.parser;


public interface Listener {

  void onBegin(String fileName);

  void onConcurrency(int channel, int concurrency);

  void onEnd(String fileName);

  void onLyrics(long tick, String lyrics);

  void onNote(long tick, int channel, int midiNote, int velocity, long duration, int program, int group);

  void onOccupancy(int channel, int occupancy);

  void onTempoChange(long tick, int usecPerQuarterNote, int quarterNotesPerMinute);

  void onText(long tick, String text);

  void onTimeSignatureChange(long tick, int beatsPerMeasure, int beatUnit);

}