// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad;

import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.analyzer.ChordFinder.Chord;
import com.example.afs.musicpad.analyzer.ChordFinder.ChordType;
import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.message.Command;
import com.example.afs.musicpad.message.CommandEntered;
import com.example.afs.musicpad.message.CommandForwarded;
import com.example.afs.musicpad.message.DigitPressed;
import com.example.afs.musicpad.message.DigitReleased;
import com.example.afs.musicpad.message.KeyPressed;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.SongSelected;
import com.example.afs.musicpad.player.ChordPlayer;
import com.example.afs.musicpad.player.NotePlayer;
import com.example.afs.musicpad.player.Player;
import com.example.afs.musicpad.player.Player.Action;
import com.example.afs.musicpad.song.Contour;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Line;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.BrokerTask;
import com.example.afs.musicpad.util.RandomAccessList;

public class DeviceHandler extends BrokerTask<Message> {

  public interface DigitAction {
    void onDigit(int channel, int semitone);
  }

  private Player player;
  private Song currentSong;
  private Synthesizer synthesizer;
  private DeviceReader deviceReader;

  protected DeviceHandler(Broker<Message> messageBroker, Synthesizer synthesizer, String deviceName) {
    super(messageBroker);
    this.synthesizer = synthesizer;
    this.deviceReader = new DeviceReader(getInputQueue(), deviceName);
    delegate(CommandEntered.class, message -> onCommand(message.getCommand(), message.getParameter()));
    delegate(KeyPressed.class, message -> OnKeyPressed(message.getKey()));
    delegate(DigitPressed.class, message -> OnDigitPressed(message.getDigit()));
    delegate(DigitReleased.class, message -> OnDigitReleased(message.getDigit()));
    subscribe(SongSelected.class, message -> OnSongSelected(message.getSong()));
  }

  @Override
  public void start() {
    super.start();
    deviceReader.start();
  }

  @Override
  public void terminate() {
    deviceReader.terminate();
    super.terminate();
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

      NavigableSet<Chord> wordChords = chords.subSet(new Chord(lastTick), false, new Chord(tick + gap), true);
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

      NavigableSet<Contour> wordContours = contours.subSet(new Contour(lastTick), false, new Contour(tick + gap), true);
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

  private void doSelectChords(int channelNumber) {
    if (currentSong != null) {
      int channelIndex = channelNumber - 1;
      player = new ChordPlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectContour(int channelNumber) {
    if (currentSong != null) {
      int channelIndex = channelNumber - 1;
      player = new NotePlayer(synthesizer, currentSong, channelIndex);
    }
  }

  private void doSelectProgram(int programNumber) {
    if (player != null) {
      int programIndex = programNumber - 1;
      player.selectProgram(programIndex);
    }
  }

  private void onCommand(int command, int parameter) {
    System.out.println("CommandProcessor.onCommand: command=" + command + ", parameter=" + parameter);
    switch (command) {
    case Command.SELECT_CHORDS:
      doSelectChords(parameter);
      break;
    case Command.SELECT_PROGRAM:
      doSelectProgram(parameter);
      break;
    case Command.SELECT_NOTES:
      doSelectContour(parameter);
      break;
    default:
      publish(new CommandForwarded(command, parameter));
      break;
    }
  }

  private void OnDigitPressed(int digit) {
    if (player != null) {
      player.play(Action.PRESS, digit);
    }
  }

  private void OnDigitReleased(int digit) {
    if (player != null) {
      player.play(Action.RELEASE, digit);
    }
  }

  private void OnKeyPressed(char key) {
    switch (key) {
    case '-':
      if (player != null) {
        player.selectPreviousPage();
      }
      break;
    case '+':
      if (player != null) {
        player.selectNextPage();
      }
      break;
    }
  }

  private void OnSongSelected(Song song) {
    currentSong = song;
    player = null;
  }

}
