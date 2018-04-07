// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.io.File;
import java.util.Random;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.parser.MidiParser;
import com.example.afs.musicpad.parser.SongListener;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.MessageTask;

public class Musicianeer extends MessageTask {

  public enum AccompanimentType {
    FULL, PIANO, RHYTHM, DRUMS, SOLO
  }

  public enum SelectType {
    NEXT, NEXT_PAGE, PREVIOUS, PREVIOUS_PAGE
  }

  public class SongLibrary {
    private int index;
    private Song song;
    private MidiLibrary midiLibrary;

    public SongLibrary(MidiLibrary midiLibrary) {
      this.midiLibrary = midiLibrary;
    }

    public int getIndex() {
      return index;
    }

    public Song getSong() {
      return song;
    }

    public void setIndex(int index) {
      if (index < 0 || index >= midiLibrary.size()) {
        throw new IndexOutOfBoundsException();
      }
      this.index = index;
      this.song = createSong(midiLibrary.get(index));
    }

    public int size() {
      return midiLibrary.size();
    }

    private Song createSong(File file) {
      Song song = new Song(file);
      SongListener songListener = new SongListener(song);
      MidiParser midiParser = new MidiParser(songListener, Default.TICKS_PER_BEAT);
      midiParser.parse(file.getPath());
      return song;
    }

  }

  public enum TrackingType {
    FOLLOW, LEAD
  }

  public static final int LOWEST_AVAILABLE_NOTE = 57;
  public static final int HIGHEST_AVAILABLE_NOTE = 76;

  private int lastProgram;
  private int melodyChannel;
  private int midiNote = -1;
  private int programOverride = 127;

  private Transport transport;
  private SongLibrary songLibrary;
  private Synthesizer synthesizer;
  private Random random = new Random();
  private TrackingType trackingType = TrackingType.LEAD;
  private AccompanimentType accompanimentType = AccompanimentType.FULL;

  public Musicianeer(MessageBroker messageBroker) {
    super(messageBroker);
    subscribe(OnMelodyNote.class, message -> doMelodyNote(message));
    subscribe(OnProgramChange.class, message -> doProgramChange(message));
    synthesizer = createSynthesizer();
    transport = new Transport(messageBroker, synthesizer);
    String path = System.getProperty("midiLibraryPath");
    if (path == null) {
      throw new IllegalStateException("midiLibraryPath property not set");
    }
    MidiLibrary midiLibrary = new MidiLibrary(path);
    songLibrary = new SongLibrary(midiLibrary);
    if (songLibrary.size() > 0) {
      setSong(random.nextInt(songLibrary.size()));
    }
  }

  public void play() {
    Song song = songLibrary.getSong();
    if (song != null) {
      transport.play(song.getNotes(), melodyChannel);
    }
  }

  public void press(int midiNote) {
    if (this.midiNote != -1) {
      synthesizer.releaseKey(melodyChannel, this.midiNote);
    }
    synthesizer.pressKey(melodyChannel, midiNote, 24);
    this.midiNote = midiNote;
  }

  public void release() {
    if (midiNote != -1) {
      synthesizer.releaseKey(melodyChannel, midiNote);
      midiNote = -1;
    }
  }

  public void selectSong(SelectType selectType) {
    int newIndex;
    if (songLibrary.size() > 0) {
      switch (selectType) {
      case NEXT:
        newIndex = Math.min(songLibrary.size() - 1, songLibrary.getIndex() + 1);
        break;
      case NEXT_PAGE:
        newIndex = Math.min(songLibrary.size() - 1, songLibrary.getIndex() + 10);
        break;
      case PREVIOUS:
        newIndex = Math.max(0, songLibrary.getIndex() - 1);
        break;
      case PREVIOUS_PAGE:
        newIndex = Math.max(0, songLibrary.getIndex() - 10);
        break;
      default:
        throw new UnsupportedOperationException(selectType.name());
      }
      setSong(newIndex);
    }
  }

  public void setAccompaniment(AccompanimentType accompanimentType) {
    transport.setAccompaniment(accompanimentType);
  }

  public void setPercentGain(int percentGain) {
    transport.setPercentGain(percentGain);
  }

  public void setPercentTempo(int percentTempo) {
    transport.setPercentTempo(percentTempo);
  }

  public void setProgramOverride(int program) {
    this.programOverride = program;
    if (program == 127) {
      synthesizer.changeProgram(melodyChannel, lastProgram);
    } else {
      synthesizer.changeProgram(melodyChannel, program);
    }
  }

  public void setTracking(TrackingType trackingType) {
  }

  public void stop() {
    transport.stop();
  }

  private Synthesizer createSynthesizer() {
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
    int processors = Runtime.getRuntime().availableProcessors();
    System.out.println("Musicianeer.createSynthesizer: processors=" + processors);
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", Midi.CHANNELS);
    settings.set("synth.cpu-cores", processors);
    Synthesizer synthesizer = new Synthesizer(settings);
    return synthesizer;
  }

  private void doMelodyNote(OnMelodyNote message) {
  }

  private void doProgramChange(OnProgramChange message) {
    int program = message.getProgram();
    lastProgram = program;
    if (programOverride == 127) {
      synthesizer.changeProgram(melodyChannel, program);
    }
  }

  private void setSong(int index) {
    songLibrary.setIndex(index);
    Song song = songLibrary.getSong();
    int distanceToWhiteKeys = song.getDistanceToWhiteKeys();
    int melodyChannel = song.getPresumedMelodyChannel();
    int lowestMidiNote = song.getLowestMidiNote(melodyChannel);
    int highestMidiNote = song.getHighestMidiNote(melodyChannel);
    System.out.println("song=" + song);
    System.out.println("melodyChannel=" + melodyChannel + ", distanceToWhiteKeys=" + distanceToWhiteKeys + ", lowestMidiNote=" + lowestMidiNote + ", highestMidiNote=" + highestMidiNote);
    lowestMidiNote += distanceToWhiteKeys;
    int octaveOutOfRange = (LOWEST_AVAILABLE_NOTE - lowestMidiNote) / Midi.SEMITONES_PER_OCTAVE;
    if (octaveOutOfRange > 0) {
      distanceToWhiteKeys += octaveOutOfRange * Midi.SEMITONES_PER_OCTAVE;
      System.out.println("new distanceToWhiteKeys=" + distanceToWhiteKeys);
    } else {
      octaveOutOfRange = (highestMidiNote - HIGHEST_AVAILABLE_NOTE) / Midi.SEMITONES_PER_OCTAVE;
      if (octaveOutOfRange > 0) {
        distanceToWhiteKeys -= octaveOutOfRange * Midi.SEMITONES_PER_OCTAVE;
        System.out.println("new distanceToWhiteKeys=" + distanceToWhiteKeys);
      }
    }
    if (distanceToWhiteKeys < 0) {
      int minimumTransposition = song.getMinimumTransposition();
      if (Math.abs(distanceToWhiteKeys) < Math.abs(minimumTransposition)) {
        song.transposeBy(distanceToWhiteKeys);
      }
    } else if (distanceToWhiteKeys > 0) {
      int maximumTransposition = song.getMaximumTransposition();
      if (distanceToWhiteKeys < maximumTransposition) {
        song.transposeBy(distanceToWhiteKeys);
      }
    }
    transport.play(song.getNotes(), melodyChannel);
    publish(new OnSong(song));
  }

}
