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
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.Analyzer;
import com.example.afs.musicpad.analyzer.ChordFinder;
import com.example.afs.musicpad.analyzer.ChordFinder.Chord;
import com.example.afs.musicpad.analyzer.ChordFinder.ChordType;
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.DeviceAttach;
import com.example.afs.musicpad.message.DeviceDetach;
import com.example.afs.musicpad.message.DigitPressed;
import com.example.afs.musicpad.message.DigitReleased;
import com.example.afs.musicpad.message.PageLeft;
import com.example.afs.musicpad.message.PageRight;
import com.example.afs.musicpad.song.MusicLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.SongBuilder;
import com.example.afs.musicpad.util.MessageBroker;
import com.example.afs.musicpad.util.Task;

// new things
// zero/enter when not in command mode are modulation down/up1550000550
// contour
// save current recording
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
    private ChordType[] chords;

    public int getChannel() {
      return channel;
    }

    public ChordType[] getChords() {
      return chords;
    }

    public int getPage() {
      return page;
    }

    public void setChannel(int channel) {
      this.channel = channel;
    }

    public void setChords(ChordType[] chords) {
      this.chords = chords;
    }

    public void setPage(int page) {
      this.page = page;
    }
  }

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

  private void doSelectChannel(int deviceId, int channel) {
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
      Settings settings = deviceSettings.get(deviceId);
      settings.setChannel(channel);
      settings.setChords(keyToChord);
      System.out.println("chords.size=" + chords.size() + ", chordTypes.size=" + chordSet.size());
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
      doSelectChannel(deviceId, operand);
      break;
    case 3:
      doListSongs(operand);
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
    ChordType[] chords = settings.getChords();
    if (chords != null) {
      int page = settings.getPage();
      int chordIndex = page * 10 + digit;
      if (chordIndex < chords.length) {
        int channel = settings.getChannel();
        ChordType chordType = chords[chordIndex];
        System.out.println(chordType);
        for (int semitone : chordType.getSemitones()) {
          try {
            Thread.sleep(0);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          digitAction.onDigit(channel, semitone);
        }
      }
    }
  }

  private void OnDigitPressed(int deviceId, int digit) {
    OnDigit(deviceId, digit, (channel, semitone) -> synthesizer.pressKey(channel, 60 + semitone, 92));
  }

  private void OnDigitReleased(int deviceId, int digit) {
    OnDigit(deviceId, digit, (channel, semitone) -> synthesizer.releaseKey(channel, 60 + semitone));
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
    int limit = settings.getChords().length / 10;
    if (page < limit) {
      settings.setPage(page + 1);
    }
  }

}
