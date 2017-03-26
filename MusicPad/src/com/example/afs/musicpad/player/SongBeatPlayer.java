// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.CommandProcessor;
import com.example.afs.musicpad.analyzer.BeatFinder;
import com.example.afs.musicpad.analyzer.BeatFinder.Beat;
import com.example.afs.musicpad.analyzer.BeatFinder.BeatNote;
import com.example.afs.musicpad.analyzer.BeatFinder.BeatNotes;
import com.example.afs.musicpad.analyzer.BeatFinder.Beats;
import com.example.afs.musicpad.device.CharCode;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;

public class SongBeatPlayer extends SongPlayer {

  private Beats beats;
  private BeatNotes[] buttonIndexToBeat;
  private Map<BeatNotes, String> beatToKeySequence;

  public SongBeatPlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    BeatFinder beatFinder = new BeatFinder();
    beats = beatFinder.findBeats(song.getNotes());
    buttonIndexToBeat = getUniqueBeatNotes(beats);
    beatToKeySequence = new HashMap<>();
    System.out.println("Total beats: " + beats.size() + ", Unique beats: " + buttonIndexToBeat.length);
    for (int buttonIndex = 0; buttonIndex < buttonIndexToBeat.length; buttonIndex++) {
      BeatNotes BeatNotes = buttonIndexToBeat[buttonIndex];
      String keySequence = CharCode.fromIndexToSequence(buttonIndex);
      beatToKeySequence.put(BeatNotes, keySequence);
      System.out.println(keySequence + " -> " + BeatNotes);
    }
    setTitle("Channel " + (channel + 1) + " Beats");
  }

  @Override
  public int getUniqueCount() {
    return buttonIndexToBeat.length;
  }

  @Override
  public void play(Action action, int beatIndex) {
    if (beatIndex < buttonIndexToBeat.length) {
      BeatNotes beatNotes = buttonIndexToBeat[beatIndex];
      playMidiBeat(action, Default.OCTAVE_SEMITONE, beatNotes);
    }
  }

  @Override
  protected String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    StringBuilder s = new StringBuilder();
    int currentBeat = (int) (currentTick / Default.TICKS_PER_BEAT);
    int firstBeat = (int) (firstTick / Default.TICKS_PER_BEAT);
    int lastBeat = (int) (lastTick / Default.TICKS_PER_BEAT);
    for (int i = firstBeat; i < lastBeat; i++) {
      Beat beat = beats.get(i);
      BeatNotes beatNotes = beat.getBeatNotes();
      if (currentBeat == i) {
        s.append(">");
      } else {
        s.append(" ");
      }
      if (beatNotes.size() == 0) {
        s.append(".");
      } else {
        String keySequence = beatToKeySequence.get(beatNotes);
        if (keySequence.length() > 1) {
          //System.out.println("Squeezing " + keySequence.length() + " characters into space for one character");
        }
        s.append(keySequence);
      }
    }
    return s.toString();
  }

  protected void playMidiBeat(Action action, int octave, BeatNotes beatNotes) {
    if (action == Action.PRESS && CommandProcessor.isTracePlay()) {
      System.out.println("Player.play: beatNotes=" + beatNotes);
    }
    for (BeatNote beatNote : beatNotes) {
      try {
        int commonNote = beatNote.getCommonNote();
        int midiNote = octave + commonNote;
        synthesizeNote(action, midiNote);
        Thread.sleep(0);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private BeatNotes[] getUniqueBeatNotes(Beats beats) {
    Set<BeatNotes> treeSet = new TreeSet<>();
    for (Beat beat : beats) {
      BeatNotes beatNotes = beat.getBeatNotes();
      treeSet.add(beatNotes);
    }
    int beatIndex = 0;
    int uniqueBeatCount = treeSet.size();
    BeatNotes[] uniqueBeatNotess = new BeatNotes[uniqueBeatCount];
    for (BeatNotes uniqueBeat : treeSet) {
      uniqueBeatNotess[beatIndex] = uniqueBeat;
      beatIndex++;
    }
    return uniqueBeatNotess;
  }

  @SuppressWarnings("unused")
  private void sample(Synthesizer synthesizer) {
    for (Beat beat : beats) {
      BeatNotes beatNotes = beat.getBeatNotes();
      System.out.println(beatNotes);
      for (BeatNote beatNote : beatNotes) {
        int commonNote = beatNote.getCommonNote();
        synthesizer.pressKey(0, Default.OCTAVE_SEMITONE + commonNote, 96);
      }
      try {
        Thread.sleep(125);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      for (BeatNote beatNote : beatNotes) {
        int commonNote = beatNote.getCommonNote();
        synthesizer.releaseKey(0, Default.OCTAVE_SEMITONE + commonNote);
      }
    }
  }

}
