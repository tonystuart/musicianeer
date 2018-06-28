// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.main;

import java.util.Arrays;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.message.OnChannelPressure;
import com.example.afs.musicpad.message.OnControlChange;
import com.example.afs.musicpad.message.OnMute;
import com.example.afs.musicpad.message.OnNoteOff;
import com.example.afs.musicpad.message.OnNoteOn;
import com.example.afs.musicpad.message.OnNotes;
import com.example.afs.musicpad.message.OnPitchBend;
import com.example.afs.musicpad.message.OnProgramChange;
import com.example.afs.musicpad.message.OnProgramOverride;
import com.example.afs.musicpad.message.OnSetChannelVolume;
import com.example.afs.musicpad.message.OnSolo;
import com.example.afs.musicpad.message.OnSongSelected;
import com.example.afs.musicpad.message.OnTransportPlay;
import com.example.afs.musicpad.message.OnTransposition;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.transport.Transport;

public class Musicianeer extends ServiceTask {

  public static class CC {
    public static final int MODULATION = 1;
    public static final int VOLUME = 7;
  }

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
    synthesizer = createSynthesizer();
    transport = new Transport(messageBroker, synthesizer);
    provide(Services.getCurrentSong, () -> getCurrentSong());
    provide(Services.getCurrentPrograms, () -> getCurrentPrograms());
    provide(Services.getSynthesizerSettings, () -> getSynthesizerSettings());
    subscribe(OnMute.class, message -> doMute(message));
    subscribe(OnSolo.class, message -> doSolo(message));
    subscribe(OnNoteOn.class, message -> doNoteOn(message));
    subscribe(OnNoteOff.class, message -> doNoteOff(message));
    subscribe(OnPitchBend.class, message -> doPitchBend(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message));
    subscribe(OnControlChange.class, message -> doControlChange(message));
    subscribe(OnProgramChange.class, message -> doProgramChange(message));
    subscribe(OnChannelPressure.class, message -> doChannelPressure(message));
    subscribe(OnProgramOverride.class, message -> doProgramOverride(message));
  }

  @Override
  public synchronized void tsStart() {
    super.tsStart();
    transport.tsStart();
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

  private void doChannelPressure(OnChannelPressure message) {
    synthesizer.setChannelPressure(mapChannel(message.getChannel()), message.getPressure());
  }

  private void doControlChange(OnControlChange message) {
    int channel = message.getChannel();
    int control = message.getControl();
    int value = message.getValue();
    System.out.println("MIDI CC " + control + " " + value);
    switch (control) {
    case CC.MODULATION:
      synthesizer.changeControl(mapChannel(channel), control, value);
      break;
    case CC.VOLUME:
      publish(new OnSetChannelVolume(channel, value));
      break;
    default:
      break;
    }
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

  private void doPitchBend(OnPitchBend message) {
    synthesizer.bendPitch(mapChannel(message.getChannel()), message.getValue());
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

  private void doSolo(OnSolo message) {
    synthesizer.soloChannel(message.getChannel(), message.isSolo());
  }

  private void doSongSelected(OnSongSelected message) {
    currentSong = message.getCurrentSong();
    synthesizer.muteAllChannels(false);
    synthesizer.soloAllChannels(false);
    publish(new OnTransposition(currentSong.getSongInfo().getEasyTransposition()));
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
    publish(new OnNotes(song.getNotes()));
    publish(new OnTransportPlay(currentSong));
  }

  private void setProgram(int channel, int program) {
    synthesizer.changeProgram(channel, program);
    synthesizer.changeProgram(mapChannel(channel), program);
  }

}
