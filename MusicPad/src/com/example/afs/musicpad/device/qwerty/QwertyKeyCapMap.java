// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.player.Chord;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.Count;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class QwertyKeyCapMap implements KeyCapMap {

  private static class ChordCount extends Count<Chord> {

    public ChordCount(Chord value) {
      super(value);
    }

    @Override
    public int compareTo(Count<Chord> that) {
      return -super.compareTo(that); // descending sort order
    }

  }

  private final String noteKeys;
  private final String registerKeys;
  private final int noteKeyCount;
  private final int registerKeyCount;
  private final int supportedChords;
  private final Chord[][] keyIndexToChords;
  private final RandomAccessList<KeyCap> keyCaps;

  private int autoRegister;
  private int registerDown;

  public QwertyKeyCapMap(String noteKeys, String registerKeys, RandomAccessList<Slice> slices) {
    this.noteKeys = noteKeys;
    this.registerKeys = registerKeys;
    this.noteKeyCount = noteKeys.length();
    this.registerKeyCount = registerKeys.length();
    this.supportedChords = noteKeyCount * registerKeyCount;
    Map<Chord, ChordCount> chords = getUniqueChordCounts(slices);
    ChordCount[] sortedChords = sortByFrequency(chords);
    int maxChords = Math.min(chords.size(), supportedChords);
    keyIndexToChords = assignChordsToRegisters(sortedChords, maxChords);
    sortByPitch(keyIndexToChords, maxChords);
    Map<Chord, String> chordToLegend = assignChordsToLegend(keyIndexToChords, maxChords);
    keyCaps = getKeyCaps(chordToLegend, slices);
  }

  @Override
  public RandomAccessList<KeyCap> getKeyCaps() {
    return keyCaps;
  }

  @Override
  public Chord onDown(int inputCode) {
    Chord chord;
    int keyIndex = noteKeys.indexOf(inputCode);
    if (keyIndex != -1) {
      int thisRegister = registerDown != 0 ? registerDown : autoRegister;
      chord = keyIndexToChords[thisRegister][keyIndex];
      System.out.println("inputCode=" + inputCode + ", keyIndex=" + keyIndex + ", chord=" + chord);
      autoRegister = 0;
    } else {
      chord = null;
      int index = registerKeys.indexOf(inputCode);
      if (index != -1) {
        registerDown = index;
        autoRegister = registerDown;
      }
    }
    return chord;
  }

  @Override
  public void onUp(int inputCode) {
    if (registerKeys.indexOf(inputCode) != -1) {
      registerDown = 0;
    }
  }

  private Map<Chord, String> assignChordsToLegend(Chord[][] keyIndexToChord, int maxChords) {
    int index = 0;
    Map<Chord, String> chordToLegend = new HashMap<>();
    for (int i = 0; i < registerKeyCount && index < maxChords; i++) {
      for (int j = 0; j < noteKeyCount && index < maxChords; j++) {
        Chord chord = keyIndexToChord[i][j];
        chordToLegend.put(chord, getLegend(i, j));
        index++;
      }
    }
    return chordToLegend;
  }

  private Chord[][] assignChordsToRegisters(ChordCount[] sortedChords, int maxChords) {
    int index = 0;
    Chord[][] keyIndexToChord = new Chord[registerKeyCount][noteKeyCount];
    for (int i = 0; i < registerKeyCount; i++) {
      for (int j = 0; j < noteKeyCount; j++) {
        if (index < maxChords) {
          Chord chord = sortedChords[index].getValue();
          keyIndexToChord[i][j] = chord;
          index++;
        } else {
          keyIndexToChord[i][j] = null;
        }
      }
    }
    return keyIndexToChord;
  }

  private RandomAccessList<KeyCap> getKeyCaps(Map<Chord, String> chordToLegend, RandomAccessList<Slice> slices) {
    RandomAccessList<KeyCap> keyCaps = new DirectList<>();
    for (Slice slice : slices) {
      Chord chord = slice.getChord();
      String legend = chordToLegend.get(chord);
      if (legend == null) {
        legend = "?";
      }
      KeyCap keyCap = new KeyCap(slice, legend);
      keyCaps.add(keyCap);
    }
    return keyCaps;
  }

  private String getLegend(int register, int digit) {
    String registerString;
    if (register == 0) {
      registerString = "";
    } else {
      registerString = String.valueOf(registerKeys.charAt(register));
    }
    return registerString + String.valueOf(noteKeys.charAt(digit));
  }

  private Map<Chord, ChordCount> getUniqueChordCounts(RandomAccessList<Slice> slices) {
    Map<Chord, ChordCount> chords = new HashMap<>();
    for (Slice slice : slices) {
      Chord chord = slice.getChord();
      ChordCount count = chords.get(chord);
      if (count == null) {
        count = new ChordCount(chord);
        chords.put(chord, count);
      }
      count.increment();
    }
    return chords;
  }

  private ChordCount[] sortByFrequency(Map<Chord, ChordCount> chords) {
    int index = 0;
    ChordCount[] sortedChords = new ChordCount[chords.size()];
    for (ChordCount chordCount : chords.values()) {
      sortedChords[index++] = chordCount;
    }
    Arrays.sort(sortedChords);
    return sortedChords;
  }

  private void sortByPitch(Chord[][] keyIndexToChord, int maxChords) {
    int amountRemaining = maxChords;
    for (int i = 0; amountRemaining > 0 && i < keyIndexToChord[i].length; i++) {
      Arrays.sort(keyIndexToChord[i], 0, Math.min(noteKeyCount, amountRemaining));
      amountRemaining = Math.max(amountRemaining - noteKeyCount, 0);
    }
  }

}