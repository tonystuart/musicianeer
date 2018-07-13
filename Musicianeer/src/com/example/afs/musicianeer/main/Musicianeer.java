// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import java.util.Arrays;
import java.util.Set;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicianeer.message.OnChannelPressure;
import com.example.afs.musicianeer.message.OnControlChange;
import com.example.afs.musicianeer.message.OnDeleteMidiFile;
import com.example.afs.musicianeer.message.OnMute;
import com.example.afs.musicianeer.message.OnNoteOff;
import com.example.afs.musicianeer.message.OnNoteOn;
import com.example.afs.musicianeer.message.OnNotes;
import com.example.afs.musicianeer.message.OnPitchBend;
import com.example.afs.musicianeer.message.OnProgramOverride;
import com.example.afs.musicianeer.message.OnSetAccompanimentType;
import com.example.afs.musicianeer.message.OnSetAccompanimentType.AccompanimentType;
import com.example.afs.musicianeer.message.OnSetChannelVolume;
import com.example.afs.musicianeer.message.OnSolo;
import com.example.afs.musicianeer.message.OnSongSelected;
import com.example.afs.musicianeer.message.OnStop;
import com.example.afs.musicianeer.message.OnTransportProgramChange;
import com.example.afs.musicianeer.message.OnTransposition;
import com.example.afs.musicianeer.midi.Midi;
import com.example.afs.musicianeer.song.Song;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.task.ServiceTask;
import com.example.afs.musicianeer.transport.Transport;

public class Musicianeer extends ServiceTask {

  public static class CC {
    public static final int MODULATION = 1;
    public static final int VOLUME = 7;
  }

  public static final int UNSET = -1;
  public static final int LOWEST_NOTE = 36;
  public static final int HIGHEST_NOTE = 84;
  public static final int NOTE_COUNT = (HIGHEST_NOTE - LOWEST_NOTE) + 1;

  private static final int PLAYER_BASE = Midi.CHANNELS;
  private static final int PLAYER_CHANNELS = Midi.CHANNELS;
  private static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  private static final int mapChannel(int channel) {
    return PLAYER_BASE + channel;
  }

  private int[] programs = new int[Midi.CHANNELS];
  private int[] programOverrides = new int[Midi.CHANNELS];

  private Transport transport;
  private Synthesizer synthesizer;
  private CurrentSong currentSong;
  private AccompanimentType accompanimentType;

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
    subscribe(OnTransportProgramChange.class, message -> doTransportProgramChange(message));
    subscribe(OnDeleteMidiFile.class, message -> doDeleteMidiFile(message));
    subscribe(OnChannelPressure.class, message -> doChannelPressure(message));
    subscribe(OnProgramOverride.class, message -> doProgramOverride(message));
    subscribe(OnSetAccompanimentType.class, message -> doSetAccompanimentType(message));
  }

  @Override
  public synchronized void tsStart() {
    super.tsStart();
    transport.tsStart();
  }

  private void changeProgramDueToAccompanimentChange() {
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (accompanimentType == AccompanimentType.PIANO) {
        setProgram(channel, 0);
      } else if (programOverrides[channel] != UNSET) {
        setProgram(channel, programOverrides[channel]);
      } else {
        setProgram(channel, programs[channel]);
      }
    }
  }

  private void changeProgramDueToNewSong(int channel, int program) {
    programs[channel] = program;
    System.out.println("DueToNewSong: channel=" + channel + ", program=" + program);
    if (accompanimentType == AccompanimentType.PIANO) {
      setProgram(channel, 0);
    } else if (programOverrides[channel] != UNSET) {
      setProgram(channel, programOverrides[channel]);
    } else {
      setProgram(channel, program);
    }
  }

  private void changeProgramDueToProgramOverride(int channel, int newProgram) {
    if (channel != Midi.DRUM) {
      programOverrides[channel] = newProgram;
      if (newProgram == UNSET) {
        setProgram(channel, programs[channel]);
      } else {
        setProgram(channel, newProgram);
      }
    }
  }

  private void changeProgramDueToTransportProgramChange(int channel, int program) {
    System.out.println("DueToTransport: channel=" + channel + ", program=" + program);
    programs[channel] = program;
    if (programOverrides[channel] == UNSET) {
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

  private void doDeleteMidiFile(OnDeleteMidiFile message) {
    if (currentSong != null && message.getFilename().equals(currentSong.getSong().getFile().getName())) {
      publish(new OnStop());
      currentSong = null;
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

  private void doProgramOverride(OnProgramOverride message) {
    int channel = message.getChannel();
    int newProgram = message.getProgram();
    changeProgramDueToProgramOverride(channel, newProgram);
  }

  private void doSetAccompanimentType(OnSetAccompanimentType message) {
    accompanimentType = message.getAccompanimentType();
    changeProgramDueToAccompanimentChange();
  }

  private void doSolo(OnSolo message) {
    synthesizer.soloChannel(message.getChannel(), message.isSolo());
  }

  private void doSongSelected(OnSongSelected message) {
    currentSong = message.getCurrentSong();
    synthesizer.muteAllChannels(false);
    synthesizer.soloAllChannels(false);
    publish(new OnTransposition(currentSong.getSongInfo().getEasyTransposition()));
    Arrays.fill(programOverrides, UNSET);
    for (int channel : currentSong.getSong().getActiveChannels()) {
      Set<Integer> programs = currentSong.getSong().getPrograms(channel);
      if (programs.size() > 0) {
        int program = programs.iterator().next();
        changeProgramDueToNewSong(channel, program);
      }
    }
    playCurrentSong();
  }

  private void doTransportProgramChange(OnTransportProgramChange message) {
    int channel = message.getChannel();
    int program = message.getProgram();
    changeProgramDueToTransportProgramChange(channel, program);
  }

  private CurrentPrograms getCurrentPrograms() {
    int[] currentPrograms = new int[Midi.CHANNELS];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (programOverrides[channel] == UNSET) {
        currentPrograms[channel] = programs[channel];
      } else {
        currentPrograms[channel] = programOverrides[channel];
      }
    }
    return new CurrentPrograms(currentPrograms);
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
  }

  private void setProgram(int channel, int program) {
    synthesizer.changeProgram(channel, program);
    synthesizer.changeProgram(mapChannel(channel), program);
  }

}
