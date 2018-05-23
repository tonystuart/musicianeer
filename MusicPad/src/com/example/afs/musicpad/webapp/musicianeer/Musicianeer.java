// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.Arrays;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.webapp.musicianeer.Transport.Whence;

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
  private CurrentSong currentSong;

  public Musicianeer(MessageBroker messageBroker) {
    super(messageBroker);
    provide(Services.getCurrentSong, () -> getCurrentSong());
    provide(Services.getPercentTempo, () -> getPercentTempo());
    provide(Services.getCurrentPrograms, () -> getCurrentPrograms());
    provide(Services.getPercentMasterGain, () -> getPercentMasterGain());
    provide(Services.getSynthesizerSettings, () -> getSynthesizerSettings());
    subscribe(OnMute.class, message -> doMute(message));
    subscribe(OnPlay.class, message -> doPlay(message));
    subscribe(OnSolo.class, message -> doSolo(message));
    subscribe(OnSeek.class, message -> doSeek(message));
    subscribe(OnStop.class, message -> doStop(message));
    subscribe(OnNoteOn.class, message -> doNoteOn(message));
    subscribe(OnNoteOff.class, message -> doNoteOff(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message));
    subscribe(OnProgramChange.class, message -> doProgramChange(message));
    subscribe(OnTransposition.class, message -> doTransposition(message));
    subscribe(OnSetPercentVelocity.class, message -> doPercentVelocity(message));
    subscribe(OnProgramOverride.class, message -> doProgramOverride(message));
    subscribe(OnSetPercentTempo.class, message -> doSetPercentTempo(message));
    subscribe(OnSetAccompanimentType.class, message -> doSetAccompanimentType(message));
    subscribe(OnSetPercentMasterGain.class, message -> doSetPercentMasterGain(message));
    synthesizer = createSynthesizer();
    transport = new Transport(messageBroker, synthesizer);
    transport.tsStart(); // TODO: Consider instantiating and starting in WebAppMain
  }

  private void changeProgram(int channel, int program) {
    defaultPrograms[channel] = program;
    if (programOverrides[channel] == OnProgramOverride.DEFAULT) {
      setProgram(channel, program);
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
    synthesizer.setChannelType(mapChannel(Midi.DRUM), FluidSynth.CHANNEL_TYPE_DRUM);
    synthesizer.changeProgram(mapChannel(Midi.DRUM), 0); // initialize fluid_synth.c channel
    return synthesizer;
  }

  private void doMute(OnMute message) {
    synthesizer.muteChannel(message.getChannel(), message.isMute());
  }

  private void doNoteOff(OnNoteOff message) {
    synthesizer.releaseKey(mapChannel(message.getChannel()), message.getData1());
  }

  private void doNoteOn(OnNoteOn message) {
    synthesizer.pressKey(mapChannel(message.getChannel()), message.getData1(), message.getData2());
  }

  private void doPercentVelocity(OnSetPercentVelocity message) {
    transport.setPercentVelocity(message.getPercentVelocity());
  }

  private void doPlay(OnPlay message) {
    if (transport.isPaused()) {
      transport.resume();
    } else if (currentSong != null) {
      transport.play(currentSong.getSong().getNotes());
    }
  }

  private void doProgramChange(OnProgramChange message) {
    int channel = message.getChannel();
    int program = message.getProgram();
    changeProgram(channel, program);
  }

  private void doProgramOverride(OnProgramOverride message) {
    int channel = message.getChannel();
    if (channel != Midi.DRUM) {
      int newProgram = message.getProgram();
      programOverrides[channel] = newProgram;
      if (newProgram == OnProgramOverride.DEFAULT) {
        setProgram(channel, defaultPrograms[channel]);
      } else {
        setProgram(channel, newProgram);
      }
    }
  }

  private void doSeek(OnSeek message) {
    transport.seek(message.getTick(), Whence.ABSOLUTE);
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

  private void doSolo(OnSolo message) {
    synthesizer.soloChannel(message.getChannel(), message.isSolo());
  }

  private void doSongSelected(OnSongSelected message) {
    currentSong = message.getCurrentSong();
    synthesizer.muteAllChannels(false);
    synthesizer.soloAllChannels(false);
    transport.setCurrentTransposition(currentSong.getSongInfo().getEasyTransposition());
    Arrays.fill(programOverrides, OnProgramOverride.DEFAULT);
    for (int channel : currentSong.getSong().getActiveChannels()) {
      Set<Integer> programs = currentSong.getSong().getPrograms(channel);
      if (programs.size() > 0) {
        int program = programs.iterator().next();
        changeProgram(channel, program);
      }
    }
    playCurrentSong();
  }

  private void doStop(OnStop message) {
    if (transport.isPaused()) {
      transport.stop();
      publish(new OnTick(0));
    } else {
      transport.pause();
    }
  }

  private void doTransposition(OnTransposition message) {
    transport.setCurrentTransposition(message.getTransposition());
    synthesizer.allNotesOff();
  }

  private CurrentPrograms getCurrentPrograms() {
    int[] programs = new int[Midi.CHANNELS];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (programOverrides[channel] == OnProgramOverride.DEFAULT) {
        programs[channel] = defaultPrograms[channel];
      } else {
        programs[channel] = programOverrides[channel];
      }
    }
    return new CurrentPrograms(programs);
  }

  private CurrentSong getCurrentSong() {
    return currentSong;
  }

  private int getPercentMasterGain() {
    return transport.getPercentGain();
  }

  private int getPercentTempo() {
    return transport.getPercentTempo();
  }

  private SynthesizerSettings getSynthesizerSettings() {
    boolean[] muteSettings = new boolean[Midi.CHANNELS];
    boolean[] soloSettings = new boolean[Midi.CHANNELS];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      muteSettings[channel] = synthesizer.isMute(channel);
      soloSettings[channel] = synthesizer.isSolo(channel);
    }
    SynthesizerSettings synthesizerSettings = new SynthesizerSettings(muteSettings, soloSettings);
    return synthesizerSettings;
  }

  private void playCurrentSong() {
    Song song = currentSong.getSong();
    transport.play(song.getNotes());
    publish(new OnTransportPlay(currentSong));
  }

  private void setProgram(int channel, int program) {
    synthesizer.changeProgram(channel, program);
    synthesizer.changeProgram(mapChannel(channel), program);
  }

}
