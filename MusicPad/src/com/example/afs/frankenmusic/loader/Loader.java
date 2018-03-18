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

  public static class Key {
    private int tonic;
    private int major;
    private int accidentals;
    private int triads;
    private int thirds;

    public Key(int tonic, int major, int accidentals, int triads, int thirds) {
      this.tonic = tonic;
      this.major = major;
      this.accidentals = accidentals;
      this.triads = triads;
      this.thirds = thirds;
    }

    public int getAccidentals() {
      return accidentals;
    }

    public int getMajor() {
      return major;
    }

    public int getThirds() {
      return thirds;
    }

    public int getTonic() {
      return tonic;
    }

    public int getTriads() {
      return triads;
    }

    @Override
    public String toString() {
      return "Key [tonic=" + tonic + ", major=" + major + ", accidentals=" + accidentals + ", triads=" + triads + ", thirds=" + thirds + "]";
    }

  }

  public class Name {

    private int song;
    private String name;

    public Name(int song, String name) {
      this.song = song;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public int getSong() {
      return song;
    }

    @Override
    public String toString() {
      return "SongName [song=" + song + ", name=" + name + "]";
    }

  }

  public static class Structure {

    private int line;
    private int stanza;

    public Structure(int stanza, int line) {
      this.stanza = stanza;
      this.line = line;
    }

    public int getLine() {
      return line;
    }

    public int getStanza() {
      return stanza;
    }

    @Override
    public String toString() {
      return "Structure [stanza=" + stanza + ", line=" + line + "]";
    }

  }

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
        Key key = getKey(song);
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
          Neuron neuron = createNeuron(songIndex, noteIndex, song, note, key, structure);
          database.insert(neuron);
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

  private Neuron createNeuron(int songIndex, int noteIndex, Song song, Note note, Key key, Structure structure) {
    int channel = note.getChannel();
    Neuron neuron = new Neuron();
    neuron.setAccidentals(key.getAccidentals());
    neuron.setBeats(note.getBeatsPerMeasure());
    neuron.setBpm(note.getBeatsPerMinute());
    neuron.setChannel(channel);
    neuron.setConcurrency(song.getConcurrency(channel));
    neuron.setDuration((int) note.getDuration());
    neuron.setId(noteIndex);
    neuron.setLine(structure.getLine());
    neuron.setMajor(key.getMajor());
    neuron.setMeasure(note.getMeasure());
    neuron.setMelody(song.getPercentMelody(channel));
    neuron.setNote(note.getMidiNote());
    neuron.setOccupancy(song.getOccupancy(channel));
    neuron.setParts(song.getActiveChannelCount());
    neuron.setProgram(note.getProgram());
    neuron.setSeconds(song.getSeconds());
    neuron.setSong(songIndex);
    neuron.setStanza(structure.getStanza());
    neuron.setStart(note.getStartIndex());
    neuron.setStop(note.getEndIndex());
    neuron.setThirds(key.getThirds());
    neuron.setTick((int) note.getTick());
    neuron.setTonic(key.getTonic());
    neuron.setTranspose(song.getTransposition());
    neuron.setTriads(key.getTriads());
    neuron.setUnit(note.getBeatUnit());
    neuron.setVelocity(note.getVelocity());
    return neuron;
  }

  private Song createSong(File file) {
    Song song = new Song(file);
    SongListener songListener = new SongListener(song);
    MidiParser midiParser = new MidiParser(songListener, Default.TICKS_PER_BEAT);
    midiParser.parse(file.getPath());
    return song;
  }

  private Key getKey(Song song) {
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
    int tonic = -1;
    int major = -1;
    int accidentals = -1;
    int triads = -1;
    int thirds = -1;
    KeyScore[] keyScores = KeySignatures.getKeyScores(noteCounts);
    for (int i = 0; i < keyScores.length && tonic == -1; i++) {
      KeyScore keyScore = keyScores[i];
      int rank = keyScore.getRank();
      if (rank == 1) {
        tonic = keyScore.getTonic();
        major = keyScore.isMajor() ? 1 : 0;
        accidentals = keyScore.getAccidentals();
        triads = keyScore.getTriads();
        thirds = keyScore.getThirds();
      }
    }
    return new Key(tonic, major, accidentals, triads, thirds);
  }

}
