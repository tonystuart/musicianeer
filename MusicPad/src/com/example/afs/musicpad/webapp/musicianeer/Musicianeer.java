// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.Random;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;

public class Musicianeer extends ServiceTask {

  public static final int LOWEST_NOTE = 36;
  public static final int HIGHEST_NOTE = 84;

  private static final int PLAYER_BASE = Midi.CHANNELS;
  private static final int PLAYER_CHANNELS = Midi.CHANNELS;
  private static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  private static final int mapChannel(int channel) {
    return PLAYER_BASE + channel;
  }

  private int[] defaultPrograms = new int[Midi.CHANNELS];
  private int[] programOverrides = new int[Midi.CHANNELS];

  private Transport transport;
  private Synthesizer synthesizer;
  private MidiLibrary midiLibrary;
  private CurrentSong currentSong;
  private Random random = new Random();

  public Musicianeer(MessageBroker messageBroker) {
    super(messageBroker);
    provide(Services.getMidiLibrary, () -> getMidiLibrary());
    provide(Services.getCurrentSong, () -> getCurrentSong());
    provide(Services.getPercentTempo, () -> getPercentTempo());
    provide(Services.getPercentMasterGain, () -> getPercentMasterGain());
    subscribe(OnPlay.class, message -> doPlay(message));
    subscribe(OnStop.class, message -> doStop(message));
    subscribe(OnNoteOn.class, message -> doNoteOn(message));
    subscribe(OnNoteOff.class, message -> doNoteOff(message));
    subscribe(OnSelectSong.class, message -> doSelectSong(message));
    subscribe(OnProgramChange.class, message -> doProgramChange(message));
    subscribe(OnProgramOverride.class, message -> doProgramOverride(message));
    subscribe(OnSetPercentTempo.class, message -> doSetPercentTempo(message));
    subscribe(OnSetAccompanimentType.class, message -> doSetAccompanimentType(message));
    subscribe(OnSetPercentMasterGain.class, message -> doSetPercentMasterGain(message));
    synthesizer = createSynthesizer();
    transport = new Transport(messageBroker, synthesizer);
    String path = System.getProperty("midiLibraryPath");
    if (path == null) {
      throw new IllegalStateException("midiLibraryPath property not set");
    }
    midiLibrary = new MidiLibrary(path);
    publish(new OnMidiLibrary(midiLibrary));
    if (midiLibrary.size() > 0) {
      selectSong(random.nextInt(midiLibrary.size()));
    }
  }

  private Synthesizer createSynthesizer() {
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
    int processors = Runtime.getRuntime().availableProcessors();
    System.out.println("Musicianeer.createSynthesizer: processors=" + processors);
    Settings settings = Synthesizer.createDefaultSettings();
    settings.set("synth.midi-channels", TOTAL_CHANNELS);
    settings.set("synth.cpu-cores", processors);
    Synthesizer synthesizer = new Synthesizer(settings);
    return synthesizer;
  }

  private void doNoteOff(OnNoteOff message) {
    synthesizer.releaseKey(mapChannel(message.getChannel()), message.getData1());
  }

  private void doNoteOn(OnNoteOn message) {
    synthesizer.pressKey(mapChannel(message.getChannel()), message.getData1(), message.getData2());
  }

  private void doPlay(OnPlay message) {
    if (currentSong != null) {
      transport.play(currentSong.getSong().getNotes());
    }
  }

  private void doProgramChange(OnProgramChange message) {
    int channel = message.getChannel();
    int program = message.getProgram();
    defaultPrograms[channel] = program;
    if (programOverrides[channel] == OnProgramOverride.DEFAULT) {
      setProgram(channel, program);
    }
  }

  private void doProgramOverride(OnProgramOverride message) {
    int channel = message.getChannel();
    int newProgram = message.getProgram();
    programOverrides[channel] = newProgram;
    if (newProgram == OnProgramOverride.DEFAULT) {
      setProgram(channel, defaultPrograms[channel]);
    } else {
      setProgram(channel, newProgram);
    }
  }

  private void doSelectSong(OnSelectSong message) {
    selectSong(message.getSongIndex());
  }

  private void doSetAccompanimentType(OnSetAccompanimentType message) {
    transport.setAccompaniment(message.getAccompanimentType());
  }

  private void doSetPercentMasterGain(OnSetPercentMasterGain message) {
    transport.setPercentGain(message.getPercentMasterGain());
  }

  private void doSetPercentTempo(OnSetPercentTempo message) {
    transport.setPercentTempo(message.getPercentTempo());
  }

  private void doStop(OnStop message) {
    transport.stop();
  }

  private CurrentSong getCurrentSong() {
    return currentSong;
  }

  private int getEasyTransposition(Song song) {
    int melodyChannel = song.getPresumedMelodyChannel();
    int lowestMidiNote = song.getLowestMidiNote(melodyChannel);
    int highestMidiNote = song.getHighestMidiNote(melodyChannel);
    int distanceToWhiteKeys = song.getDistanceToWhiteKeys();
    System.out.println("song=" + song);
    System.out.println("melodyChannel=" + melodyChannel + ", distanceToWhiteKeys=" + distanceToWhiteKeys + ", lowestMidiNote=" + lowestMidiNote + ", highestMidiNote=" + highestMidiNote);
    lowestMidiNote += distanceToWhiteKeys;
    highestMidiNote += distanceToWhiteKeys;
    int octaveTransposition = 0;
    if (lowestMidiNote < LOWEST_NOTE) {
      int octaveOutOfRange = (LOWEST_NOTE - lowestMidiNote) / Midi.SEMITONES_PER_OCTAVE;
      if (octaveOutOfRange > 0) {
        octaveTransposition = octaveOutOfRange * Midi.SEMITONES_PER_OCTAVE;
        System.out.println("keyboardTransposition=" + octaveTransposition);
      }
    } else if (highestMidiNote > HIGHEST_NOTE) {
      int octaveOutOfRange = (lowestMidiNote - LOWEST_NOTE) / Midi.SEMITONES_PER_OCTAVE;
      if (octaveOutOfRange > 0) {
        octaveTransposition = -octaveOutOfRange * Midi.SEMITONES_PER_OCTAVE;
        System.out.println("keyboardTransposition=" + octaveTransposition);
      }
    }
    // distanceToWhiteKeys += keyboardTransposition;
    int easyTransposition = 0;
    if (distanceToWhiteKeys < 0) {
      int minimumTransposition = song.getMinimumTransposition();
      if (Math.abs(distanceToWhiteKeys) < Math.abs(minimumTransposition)) {
        easyTransposition = distanceToWhiteKeys;
      }
    } else if (distanceToWhiteKeys > 0) {
      int maximumTransposition = song.getMaximumTransposition();
      if (distanceToWhiteKeys < maximumTransposition) {
        easyTransposition = distanceToWhiteKeys;
      }
    }
    return easyTransposition;
  }

  private MidiLibrary getMidiLibrary() {
    return midiLibrary;
  }

  private int getPercentMasterGain() {
    return transport.getPercentGain();
  }

  private int getPercentTempo() {
    return transport.getPercentTempo();
  }

  private void playCurrentSong() {
    Song song = currentSong.getSong();
    transport.play(song.getNotes());
    publish(new OnTransportPlay(currentSong));
  }

  private void selectSong(int index) {
    if (index < 0 || index >= midiLibrary.size()) {
      throw new IndexOutOfBoundsException();
    }
    Song song = midiLibrary.readSong(index);
    int easyTransposition = getEasyTransposition(song);
    transport.setCurrentTransposition(easyTransposition);
    currentSong = new CurrentSong(index, song, easyTransposition);
    publish(new OnSongSelected(currentSong));
    playCurrentSong();
  }

  private void setProgram(int channel, int program) {
    synthesizer.changeProgram(channel, program);
    synthesizer.changeProgram(mapChannel(channel), program);
  }

}
