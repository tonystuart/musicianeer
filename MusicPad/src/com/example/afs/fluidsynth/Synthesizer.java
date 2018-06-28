// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.fluidsynth;

import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.util.BooleanArray;

public class Synthesizer {

  public static class Settings {

    private static final String SOUND_FONT_FILE_NAME = "/usr/share/sounds/sf2/FluidR3_GM.sf2";

    private long settings;
    private String soundFontFileName;

    public Settings() {
      this(SOUND_FONT_FILE_NAME);
    }

    public Settings(String soundFileFileName) {
      soundFontFileName = soundFileFileName;
      settings = FluidSynth.new_fluid_settings();
    }

    public long getSettings() {
      return settings;
    }

    public String getSoundFontFileName() {
      return soundFontFileName;
    }

    public void set(String name, int value) {
      FluidSynth.fluid_settings_setint(settings, name, value);
    }

    public void set(String name, String value) {
      FluidSynth.fluid_settings_setstr(settings, name, value);
    }
  }

  static {
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
  }

  // https://sourceforge.net/p/fluidsynth/wiki/FluidSettings/
  public static final float MINIMUM_GAIN = 0f;
  public static final float MAXIMUM_GAIN = 10f;
  public static final float DEFAULT_GAIN = 0.200f;
  public static final int PLAYER_TOTAL_CHANNELS = Midi.CHANNELS * 2;

  public static Settings createDefaultSettings() {
    Settings settings = new Settings();
    settings.set("audio.driver", "alsa");
    return settings;
  }

  private long synth;
  private float gain = DEFAULT_GAIN;
  private BooleanArray muteValues = new BooleanArray(Midi.CHANNELS);
  private BooleanArray soloValues = new BooleanArray(Midi.CHANNELS);

  public Synthesizer() {
    this(createDefaultSettings());
  }

  public Synthesizer(Settings settings) {
    synth = FluidSynth.new_fluid_synth(settings.getSettings());
    FluidSynth.new_fluid_audio_driver(settings.getSettings(), synth);
    FluidSynth.fluid_synth_sfload(synth, settings.getSoundFontFileName(), 1);
  }

  public void allNotesOff() {
    for (int i = 0; i < PLAYER_TOTAL_CHANNELS; i++) {
      FluidSynth.fluid_synth_all_notes_off(synth, i);
    }
  }

  /**
   * Modifies the pitch on the channel.
   * 
   * @param channel
   *          channel whose pitch is to be modified
   * @param val
   *          adjustment to pitch in four semitone range (0-16383 with 8192
   *          being center)
   */
  public void bendPitch(int channel, int val) {
    FluidSynth.fluid_synth_pitch_bend(synth, channel, val);
  }

  public void changeControl(int channel, int control, int value) {
    FluidSynth.fluid_synth_cc(synth, channel, control, value);
  }

  public void changeProgram(int channel, int program) {
    FluidSynth.fluid_synth_program_change(synth, channel, program);
  }

  public float getGain() {
    return gain;
  }

  public boolean isMute(int channel) {
    return muteValues.isSet(channel);
  }

  public boolean isSolo(int channel) {
    return soloValues.isSet(channel);
  }

  public void muteAllChannels(boolean isMute) {
    muteValues.setAll(isMute);
  }

  public void muteChannel(int channel, boolean isMute) {
    muteValues.set(channel, isMute);
  }

  public void pressKey(int channel, int key, int velocity) {
    if (isActive(channel)) {
      FluidSynth.fluid_synth_noteon(synth, channel, key, velocity);
    }
  }

  public void releaseKey(int channel, int key) {
    FluidSynth.fluid_synth_noteoff(synth, channel, key);
  }

  public void setChannelPressure(int channel, int pressure) {
    FluidSynth.fluid_synth_channel_pressure(synth, channel, pressure);
  }

  /**
   * Set midi channel type
   * 
   * @param channel
   *          channel number (0 to MIDI channel count - 1)
   * @param type
   *          CHANNEL_TYPE_MELODIC, or CHANNEL_TYPE_DRUM
   */
  public void setChannelType(int channel, int type) {
    FluidSynth.fluid_synth_set_channel_type(synth, channel, type);
  }

  /**
   * Modifies the gain for the synthesizer.
   * 
   * @param gain
   *          value in the range 0.0 to 10.0
   */
  public void setGain(float gain) {
    this.gain = gain;
    FluidSynth.fluid_synth_set_gain(synth, gain);
  }

  public void soloAllChannels(boolean isSolo) {
    soloValues.setAll(isSolo);
  }

  public void soloChannel(int channel, boolean isSolo) {
    soloValues.set(channel, isSolo);
  }

  private boolean isActive(int channel) {
    boolean isActive;
    if (channel >= Midi.CHANNELS) {
      isActive = true;
    } else if (soloValues.getSetCount() == 0) {
      isActive = !muteValues.isSet(channel);
    } else {
      isActive = soloValues.isSet(channel);
    }
    return isActive;
  }
}
