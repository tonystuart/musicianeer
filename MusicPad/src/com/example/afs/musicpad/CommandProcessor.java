// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.Analyzer;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.analyzer.ChordFinder.Chord;
import com.example.afs.musicpad.analyzer.ChordFinder.ChordType;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.DeviceAttach;
import com.example.afs.musicpad.message.DeviceDetach;
import com.example.afs.musicpad.message.DigitPressed;
import com.example.afs.musicpad.message.DigitReleased;
import com.example.afs.musicpad.message.PageLeft;
import com.example.afs.musicpad.message.PageRight;
import com.example.afs.musicpad.parser.SongBuilder;
import com.example.afs.musicpad.song.Contour;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Line;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.MessageBroker;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Task;

// new things
// zero/enter when not in command mode are modulation down/up1550000550
// contour// save current recording
// clear current recording
// next four measures
// previous four measures
// control
// 1 - play
// 2 - pause/stop
// 3 - stop
// 4 - previous measure
// 5 - next measure
// 6 - loop
// 7 - record
// 8 - previous song
// 9 - next song
// 10 - new clip
// 11 - previous clip
// 12 - next clip
// 13 - transpose to white
// 20 - clip append
// 21 - clip merge
// control, value
// 100 - set song
// 101 - set song page
// 102 - set song index on page
// 103 - set master gain
// 104 - set tempo
// 105 - set note concurrency
// 106 - set note duration
// 107 - set default program
// 108 - set default velocity
// 109 - set loop start (in measures)
// 110 - set loop length (in beats)
// control-range
// 200 to 299 - play next note in track
// 300 to 399 - play note in page
// 400 to 499 - set clip
// 500 to 599 - play clip
// control-range, value
// 600 to 699 - set track program
// 700 to 799 - set track velocity

public class CommandProcessor extends Task {

  public interface DigitAction {
    void onDigit(int channel, int semitone);
  }

  public static class Settings {
    private int channel;
    private int page;
    private ChordType[] keyToChord;
    private TreeSet<Chord> chords;
    private Map<ChordType, String> chordToKey;
    private int[] keyToContour;
    private Map<Integer, String> contourToKey;

    public int getChannel() {
      return channel;
    }

    public TreeSet<Chord> getChords() {
      return chords;
    }

    public Map<ChordType, String> getChordToKey() {
      return chordToKey;
    }

    public Map<Integer, String> getContourToKey() {
      return contourToKey;
    }

    public ChordType[] getKeyToChord() {
      return keyToChord;
    }

    public int[] getKeyToContour() {
      return keyToContour;
    }

    public int getPage() {
      return page;
    }

    public void setChannel(int channel) {
      this.channel = channel;
    }

    public void setChords(TreeSet<Chord> chords) {
      this.chords = chords;
    }

    public void setChordToKey(Map<ChordType, String> chordToKey) {
      this.chordToKey = chordToKey;
    }

    public void setContourToKey(Map<Integer, String> contourToKey) {
      this.contourToKey = contourToKey;
    }

    public void setKeyToChord(ChordType[] keyToChord) {
      this.keyToChord = keyToChord;
    }

    public void setKeyToContour(int[] keyToContour) {
      this.keyToContour = keyToContour;
    }

    public void setPage(int page) {
      this.page = page;
    }
  }

  private static final int OCTAVE_BASE = 48;

  private MusicLibrary musicLibrary;
  private Random random = new Random();
  private Song currentSong;
  private int currentPageIndex = -1;
  private Map<Integer, Settings> deviceSettings = new HashMap<>();
  private Synthesizer synthesizer = new Synthesizer();

  protected CommandProcessor(MessageBroker messageBroker, MusicLibrary musicLibrary) {
    super(messageBroker);
    this.musicLibrary = musicLibrary;
    subscribe(DeviceAttach.class, message -> onDeviceAttach(message.getDeviceId()));
    subscribe(DeviceDetach.class, message -> onDeviceDetach(message.getDeviceId()));
    subscribe(DigitPressed.class, message -> OnDigitPressed(message.getDeviceId(), message.getDigit()));
    subscribe(DigitReleased.class, message -> OnDigitReleased(message.getDeviceId(), message.getDigit()));
    subscribe(Command.class, message -> onCommand(message.getDeviceId(), message.getCommand(), message.getOperand()));
    subscribe(PageLeft.class, message -> onPageLeft(message.getDeviceId()));
    subscribe(PageRight.class, message -> onPageRight(message.getDeviceId()));
  }

  private int compare(ChordType left, ChordType right) {
    int[] leftSemitones = left.getSemitones();
    int[] rightSemitones = right.getSemitones();
    int limit = Math.min(leftSemitones.length, rightSemitones.length);
    for (int i = 0; i < limit; i++) {
      int deltaSemitone = leftSemitones[i] - rightSemitones[i];
      if (deltaSemitone != 0) {
        return deltaSemitone;
      }
    }
    int deltaLength = leftSemitones.length - rightSemitones.length;
    if (deltaLength != 0) {
      return deltaLength;
    }
    return 0;
  }

  private void displayChordLyrics(Line line, TreeSet<Chord> chords, Map<ChordType, String> chordToKey) {
    long ticksPerMeasure = currentSong.getTicksPerMeasure(1);
    long gap = ticksPerMeasure / Default.GAP_BEAT_UNIT;
    long lastTick = -1;
    ChordType lastChordType = null;
    RandomAccessList<Word> words = line.getWords();
    int wordCount = words.size();
    for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
      Word word = words.get(wordIndex);
      long tick = word.getTick();
      if (lastTick == -1) {
        lastTick = (tick / ticksPerMeasure) * ticksPerMeasure - gap;
      }

      NavigableSet<Chord> wordChords = chords.subSet(new Chord(lastTick), false, new Chord(tick), true);
      for (Chord chord : wordChords) {
        ChordType chordType = chord.getChordType();
        if (chordType != lastChordType) {
          String key = chordToKey.get(chordType);
          String name = chordType.getName() + "(" + key + ")";
          if (wordIndex == 0) {
            System.out.print(name + " ");
          } else {
            System.out.print(" " + name);
          }
          lastChordType = chordType;
        }
      }

      String text = word.getText();
      if (text.startsWith("/") || text.startsWith("\\")) {
        text = text.substring(1);
      }
      System.out.print(text);
      lastTick = tick;
    }
    System.out.println();
  }

  private void displayContourLyrics(Line line, TreeSet<Contour> contours, Map<Integer, String> contourToKey) {
    long ticksPerMeasure = currentSong.getTicksPerMeasure(1);
    long gap = ticksPerMeasure / Default.GAP_BEAT_UNIT;
    long lastTick = -1;
    RandomAccessList<Word> words = line.getWords();
    int wordCount = words.size();
    for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
      Word word = words.get(wordIndex);
      long tick = word.getTick();
      if (lastTick == -1) {
        lastTick = (tick / ticksPerMeasure) * ticksPerMeasure - gap;
      }

      NavigableSet<Contour> wordContours = contours.subSet(new Contour(lastTick), false, new Contour(tick), true);
      for (Contour contour : wordContours) {
        int midiNote = contour.getMidiNote();
        String key = contourToKey.get(midiNote);
        String name = Names.getNoteName(midiNote) + "(" + key + ")";
        if (wordIndex == 0) {
          System.out.print(name + " ");
        } else {
          System.out.print(" " + name);
        }
      }

      String text = word.getText();
      if (text.startsWith("/") || text.startsWith("\\")) {
        text = text.substring(1);
      }
      System.out.print(text);
      lastTick = tick;
    }
    System.out.println();
  }

  private void doDisplaySong(int operand) {
    if (currentSong != null) {
      RandomAccessList<Line> lines = currentSong.getLines();
      for (Line line : lines) {
        for (Settings settings : deviceSettings.values()) {
          TreeSet<Chord> chords = settings.getChords();
          Map<Integer, String> contourToKey = settings.getContourToKey();
          if (chords != null && chords.size() > 0) {
            Map<ChordType, String> chordToKey = settings.getChordToKey();
            displayChordLyrics(line, chords, chordToKey);
          } else if (contourToKey != null) {
            TreeSet<Contour> contours = currentSong.getContours(settings.getChannel());
            displayContourLyrics(line, contours, contourToKey);
          }
        }
      }
    }
  }

  private void doListSongs(int pageNumber) {
    int pageIndex;
    if (pageNumber == 0) {
      pageIndex = currentPageIndex + 1;
      pageNumber = pageIndex + 1;
    } else {
      pageIndex = pageNumber - 1;
    }
    System.out.println("Page #" + pageNumber);
    int base = pageIndex * 100;
    int limit = Math.min(base + 100, musicLibrary.size());
    for (int songIndex = base; songIndex < limit; songIndex++) {
      int songNumber = songIndex + 1;
      File midiFile = musicLibrary.getMidiFile(songIndex);
      System.out.println("Song #" + songNumber + ": " + midiFile.getName());
    }
    currentPageIndex = pageIndex;
  }

  private void doSelectChords(int deviceId, int channel) {
    if (currentSong != null) {
      ChordFinder chordFinder = new ChordFinder();
      TreeSet<Chord> chords = chordFinder.getChords(currentSong.getNotes(), channel);
      Set<ChordType> chordSet = new HashSet<>();
      for (Chord chord : chords) {
        ChordType chordType = chord.getChordType();
        chordSet.add(chordType);
      }
      int chordIndex = 0;
      int uniqueChordCount = chordSet.size();
      ChordType[] keyToChord = new ChordType[uniqueChordCount];
      for (ChordType chordType : chordSet) {
        keyToChord[chordIndex] = chordType;
        chordIndex++;
      }
      Arrays.sort(keyToChord, (o1, o2) -> compare(o1, o2));
      Map<ChordType, String> chordToKey = new HashMap<>();
      for (int i = 0; i < keyToChord.length; i++) {
        chordToKey.put(keyToChord[i], Integer.toString(i));
      }
      Settings settings = deviceSettings.get(deviceId);
      settings.setChannel(channel);
      settings.setChords(chords);
      settings.setKeyToChord(keyToChord);
      settings.setChordToKey(chordToKey);
      settings.setPage(0);
      System.out.println("chords.size=" + chords.size() + ", unique chords=" + chordSet.size());
      for (int i = 0; i < keyToChord.length; i++) {
        System.out.println(i + " -> " + keyToChord[i].getName());
      }
    }
  }

  private void doSelectContour(int deviceId, int channel) {
    if (currentSong != null) {
      TreeSet<Contour> contours = currentSong.getContours(channel);
      Set<Integer> contourSet = new HashSet<>();
      for (Contour contour : contours) {
        int midiNote = contour.getMidiNote();
        contourSet.add(midiNote);
      }
      int contourIndex = 0;
      int uniqueContourCount = contourSet.size();
      int[] keyToContour = new int[uniqueContourCount];
      for (int contour : contourSet) {
        keyToContour[contourIndex] = contour;
        contourIndex++;
      }
      Arrays.sort(keyToContour);
      Map<Integer, String> contourToKey = new HashMap<>();
      for (int i = 0; i < keyToContour.length; i++) {
        contourToKey.put(keyToContour[i], Integer.toString(i));
      }
      Settings settings = deviceSettings.get(deviceId);
      settings.setChannel(channel);
      settings.setKeyToContour(keyToContour);
      settings.setContourToKey(contourToKey);
      settings.setPage(0);
      System.out.println("contours.size=" + contours.size() + ", unique contours=" + contourSet.size());
      for (int i = 0; i < keyToContour.length; i++) {
        System.out.println(i + " -> " + Names.getNoteName(keyToContour[i]));
      }
    }
  }

  private void doSelectProgram(int deviceId, int operand) {
    Settings settings = deviceSettings.get(deviceId);
    if (settings != null) {
      int channel = settings.getChannel();
      synthesizer.changeProgram(channel, operand);
    }
  }

  private void doSelectSong(int songNumber) {
    int songIndex;
    if (songNumber == 0) {
      songIndex = random.nextInt(musicLibrary.size());
      songNumber = songIndex + 1;
    } else {
      songIndex = songNumber - 1;
    }
    if (songIndex < musicLibrary.size()) {
      File midiFile = musicLibrary.getMidiFile(songIndex);
      SongBuilder songBuilder = new SongBuilder();
      currentSong = songBuilder.createSong(midiFile);
      System.out.println("Selecting #" + songNumber + ": " + currentSong.getName());
      Analyzer.displaySemitoneCounts(currentSong);
      Analyzer.displayKey(currentSong);
    } else {
      System.out.println("Song " + songNumber + " is out of range");
    }
  }

  private void onCommand(int deviceId, int command, int operand) {
    System.out.println("CommandProcessor.onCommand: command=" + command + ", operand=" + operand);
    switch (command) {
    case 1:
      doSelectSong(operand);
      break;
    case 2:
      doSelectChords(deviceId, operand);
      break;
    case 3:
      doListSongs(operand);
      break;
    case 4:
      doDisplaySong(operand);
      break;
    case 5:
      doSelectProgram(deviceId, operand);
      break;
    case 6:
      doSelectContour(deviceId, operand);
      break;
    }
  }

  private void onDeviceAttach(int deviceId) {
    deviceSettings.put(deviceId, new Settings());
  }

  private void onDeviceDetach(int deviceId) {
    deviceSettings.remove(deviceId);
  }

  private void OnDigit(int deviceId, int digit, DigitAction digitAction) {
    Settings settings = deviceSettings.get(deviceId);
    // TODO: We need a common interface for Chord and Contour!
    ChordType[] chords = settings.getKeyToChord();
    int[] contours = settings.getKeyToContour();
    if (chords != null) {
      int page = settings.getPage();
      int chordIndex = page * 10 + digit;
      if (chordIndex < chords.length) {
        int channel = settings.getChannel();
        ChordType chordType = chords[chordIndex];
        //System.out.println(chordType);
        // TODO: Be careful about using the word semitone when we mean midiNote
        for (int semitone : chordType.getSemitones()) {
          try {
            Thread.sleep(0);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          digitAction.onDigit(channel, OCTAVE_BASE + semitone);
        }
      }
    } else if (contours != null) {
      int page = settings.getPage();
      int contourIndex = page * 10 + digit;
      if (contourIndex < contours.length) {
        int channel = settings.getChannel();
        int midiNote = contours[contourIndex];
        digitAction.onDigit(channel, midiNote);
      }
    }
  }

  private void OnDigitPressed(int deviceId, int digit) {
    OnDigit(deviceId, digit, (channel, semitone) -> synthesizer.pressKey(channel, semitone, 92));
  }

  private void OnDigitReleased(int deviceId, int digit) {
    OnDigit(deviceId, digit, (channel, semitone) -> synthesizer.releaseKey(channel, semitone));
  }

  private void onPageLeft(int deviceId) {
    Settings settings = deviceSettings.get(deviceId);
    int page = settings.getPage();
    if (page > 0) {
      settings.setPage(page - 1);
    }
  }

  private void onPageRight(int deviceId) {
    Settings settings = deviceSettings.get(deviceId);
    int page = settings.getPage();
    int limit = settings.getKeyToChord().length / 10;
    if (page < limit) {
      settings.setPage(page + 1);
    }
  }

}
