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

public class Musicianeer {

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

  private int lastProgram;
  private int melodyChannel;
  private int midiNote = -1;
  private int programOverride = 127;

  private Transport transport;
  private SongLibrary songLibrary;
  private Synthesizer synthesizer;
  private MessageBroker messageBroker;
  private Random random = new Random();
  private TrackingType trackingType = TrackingType.LEAD;
  private AccompanimentType accompanimentType = AccompanimentType.FULL;

  public Musicianeer(MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
    synthesizer = createSynthesizer();
    transport = new Transport(synthesizer, tick -> onTick(tick), midiNote -> onMidiNote(midiNote), program -> onProgram(program));
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

  private void onMidiNote(int midiNote) {
  }

  private void onProgram(int program) {
    lastProgram = program;
    if (programOverride == 127) {
      synthesizer.changeProgram(melodyChannel, program);
    }
  }

  private void onTick(long tick) {
  }

  private void setSong(int index) {
    songLibrary.setIndex(index);
    Song song = songLibrary.getSong();
    int distanceToWhiteKeys = song.getDistanceToWhiteKeys();
    System.out.println("distanceToWhiteKeys=" + distanceToWhiteKeys);
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
    int melodyChannel = song.getPresumedMelodyChannel();
    System.out.println("melodyChannel=" + melodyChannel);
    transport.play(song.getNotes(), melodyChannel);
    messageBroker.publish(new OnSong(song));
  }

}
