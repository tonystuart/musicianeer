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

  private int channel = 0;
  private int midiNote = -1;
  private Transport transport;
  private SongLibrary songLibrary;
  private Synthesizer synthesizer;
  private MessageBroker messageBroker;
  private Random random = new Random();
  private TrackingType trackingType = TrackingType.LEAD;
  private AccompanimentType accompanimentType = AccompanimentType.FULL;
  private int program;

  public Musicianeer(MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
    synthesizer = createSynthesizer();
    transport = new Transport(synthesizer);
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
      transport.play(song.getNotes());
    }
  }

  public void press(int midiNote) {
    if (this.midiNote != -1) {
      synthesizer.releaseKey(channel, this.midiNote);
    }
    synthesizer.pressKey(channel, midiNote, 24);
    this.midiNote = midiNote;
  }

  public void release() {
    if (midiNote != -1) {
      synthesizer.releaseKey(channel, midiNote);
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

  public void setProgram(int program) {
    this.program = program;
    synthesizer.changeProgram(17, program);
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

  private void onTick(long tick) {
  }

  private void setSong(int index) {
    songLibrary.setIndex(index);
    Song song = songLibrary.getSong();
    transport.play(song.getNotes(), tick -> onTick(tick));
    messageBroker.publish(new OnSong(song));
  }

}
