// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.loader;

import java.io.File;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.example.afs.frankenmusic.db.Database;
import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.parser.MidiParser;
import com.example.afs.musicpad.parser.SongListener;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;

public class Loader {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java " + Loader.class.getName() + " directory");
      System.exit(-1);
    }
    Loader loader = new Loader();
    loader.buildDatabase(args[0]);
  }

  private Database database;

  public Loader() {
    database = new Database("jdbc:derby://localhost:1527/Music;create=true;");
  }

  private void buildDatabase(String path) {
    int songIndex = 0;
    int noteIndex = 0;
    long startMillis = System.currentTimeMillis();
    MidiLibrary midiLibrary = new MidiLibrary(path);
    for (File midiFile : midiLibrary) {
      try {
        Song song = createSong(midiFile);
        NavigableMap<Integer, Structure> measureStructure = new TreeMap<>();
        measureStructure.put(-1, new Structure(0, 0)); // ensure there is a floor for any key
        TreeSet<Word> words = song.getWords();
        int stanza = 0;
        int line = 0;
        for (Word word : words) {
          long tick = word.getTick();
          String text = word.getText();
          if (text.length() > 0) {
            char firstChar = text.charAt(0);
            if (firstChar == '\\') {
              stanza++;
              line++;
            } else if (firstChar == '/') {
              line++;
            }
          }
          // This is not quite right, but it's consistent with Note.getMeasure()
          int measure = (int) (tick / (song.getBeatsPerMeasure(tick) * Default.TICKS_PER_BEAT));
          Structure structure = new Structure(stanza, line);
          measureStructure.put(measure, structure);
        }
        KeyScore keyScore = getKeyScore(song);
        Integer distanceToWhiteKeys = song.getDistanceToWhiteKeys();
        if (distanceToWhiteKeys != null) {
          int transpose = distanceToWhiteKeys;
          song.transposeTo(transpose);
        }
        database.setAutoCommit(false);
        Name name = new Name(songIndex, midiFile.getName());
        database.insert(name);
        for (Note note : song.getNotes()) {
          int measure = note.getMeasure();
          Structure structure = measureStructure.floorEntry(measure).getValue();
          Notable notable = createNotable(songIndex, noteIndex, song, note, keyScore, structure);
          database.insert(notable);
          noteIndex++;
        }
        database.setAutoCommit(true);
        long memoryInUse = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long elapsedMillis = System.currentTimeMillis() - startMillis;
        System.out.println(songIndex + " " + song.getTitle() + " " + noteIndex + " " + memoryInUse + " " + (elapsedMillis / 1000));
        songIndex++;
      } catch (RuntimeException e) {
        System.err.println("Cannot store " + midiFile);
        e.printStackTrace();
      }
    }
  }

  private Notable createNotable(int songIndex, int noteIndex, Song song, Note note, KeyScore keyScore, Structure structure) {
    int channel = note.getChannel();
    Notable notable = new Notable();
    notable.setAccidentals(keyScore.getAccidentals());
    notable.setBeats(note.getBeatsPerMeasure());
    notable.setBpm(note.getBeatsPerMinute());
    notable.setChannel(channel);
    notable.setConcurrency(song.getConcurrency(channel));
    notable.setDuration((int) note.getDuration());
    notable.setId(noteIndex);
    notable.setLine(structure.getLine());
    notable.setMajor(keyScore.isMajor() ? 1 : 0);
    notable.setMeasure(note.getMeasure());
    notable.setMelody(song.getPercentMelody(channel));
    notable.setNote(note.getMidiNote());
    notable.setOccupancy(song.getOccupancy(channel));
    notable.setParts(song.getActiveChannelCount());
    notable.setProgram(note.getProgram());
    notable.setSeconds(song.getSeconds());
    notable.setSong(songIndex);
    notable.setStanza(structure.getStanza());
    notable.setStart(note.getStartIndex());
    notable.setStop(note.getEndIndex());
    notable.setThirds(keyScore.getThirds());
    notable.setTick((int) note.getTick());
    notable.setTonic(keyScore.getTonic());
    notable.setTranspose(song.getTransposition());
    notable.setTriads(keyScore.getTriads());
    notable.setUnit(note.getBeatUnit());
    notable.setVelocity(note.getVelocity());
    return notable;
  }

  private Song createSong(File file) {
    Song song = new Song(file);
    SongListener songListener = new SongListener(song);
    MidiParser midiParser = new MidiParser(songListener, Default.TICKS_PER_BEAT);
    midiParser.parse(file.getPath());
    return song;
  }

  private KeyScore getKeyScore(Song song) {
    int[] noteCounts = new int[Midi.SEMITONES_PER_OCTAVE];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] channelNoteCounts = song.getChromaticNoteCounts(channel);
          for (int i = 0; i < noteCounts.length; i++) {
            noteCounts[i] += channelNoteCounts[i];
          }
        }
      }
    }
    KeyScore topKeyScore = null;
    KeyScore[] keyScores = KeySignatures.getKeyScores(noteCounts);
    for (int i = 0; i < keyScores.length && topKeyScore == null; i++) {
      KeyScore keyScore = keyScores[i];
      int rank = keyScore.getRank();
      if (rank == 1) {
        topKeyScore = keyScore;
      }
    }
    return topKeyScore;
  }

}
