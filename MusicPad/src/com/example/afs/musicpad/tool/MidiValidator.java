// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.tool;

import java.io.File;

import com.example.afs.musicpad.parser.Listener;
import com.example.afs.musicpad.parser.MidiParser;
import com.example.afs.musicpad.song.Default;

public class MidiValidator {

  public class NoOpListener implements Listener {

    @Override
    public void onBegin(String fileName) {
    }

    @Override
    public void onConcurrency(int channel, int concurrency) {
    }

    @Override
    public void onEnd(String fileName) {
    }

    @Override
    public void onEndCount(int channel, int endCount) {
    }

    @Override
    public void onLyrics(long tick, String lyrics) {
    }

    @Override
    public void onNote(long tick, int channel, int midiNote, int velocity, long duration, int program, int startIndex, int endIndex) {
    }

    @Override
    public void onOccupancy(int channel, int occupancy) {
    }

    @Override
    public void onStartCount(int channel, int startCount) {
    }

    @Override
    public void onTempoChange(long tick, int usecPerQuarterNote, int quarterNotesPerMinute) {
    }

    @Override
    public void onText(long tick, String text) {
    }

    @Override
    public void onTimeSignatureChange(long tick, int beatsPerMeasure, int beatUnit) {
    }

  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: java " + MidiValidator.class.getName() + " midiFiles...");
      System.exit(-1);
    }
    MidiValidator midiValidator = new MidiValidator();
    for (String arg : args) {
      midiValidator.validate(new File(arg));
    }
  }

  private NoOpListener noOpListener = new NoOpListener();

  private void validate(File file) {
    if (file.isDirectory()) {
      for (File childFile : file.listFiles())
        validate(childFile);
    } else {
      String fileName = file.getPath();
      MidiParser midiParser = new MidiParser(noOpListener, Default.RESOLUTION);
      try {
        midiParser.parse(fileName);
        System.out.println("OKAY: " + fileName);
      } catch (RuntimeException e) {
        System.out.println("ERROR: " + fileName);
      }
    }
  }

}
