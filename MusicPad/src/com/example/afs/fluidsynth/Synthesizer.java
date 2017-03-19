// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.fluidsynth;

public class Synthesizer {

  public static class Settings {
    private long settings;

    public Settings() {
      settings = FluidSynth.new_fluid_settings();
    }

    public long getSettings() {
      return settings;
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

  public static Settings createDefaultSettings() {
    Settings settings = new Settings();
    settings.set("audio.driver", "alsa");
    return settings;
  }

  private long synth;

  public Synthesizer() {
    this(createDefaultSettings());
  }

  public Synthesizer(Settings settings) {
    synth = FluidSynth.new_fluid_synth(settings.getSettings());
    FluidSynth.new_fluid_audio_driver(settings.getSettings(), synth);
    FluidSynth.fluid_synth_sfload(synth, "/usr/share/sounds/sf2/FluidR3_GM.sf2", 1);
  }

  public void allNotesOff() {
    for (int channel = 0; channel < 16; channel++) {
      for (int key = 0; key < 128; key++) {
        releaseKey(channel, key);
      }
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

  public void changeProgram(int channel, int program) {
    FluidSynth.fluid_synth_program_change(synth, channel, program);
  }

  public void pressKey(int channel, int key, int velocity) {
    FluidSynth.fluid_synth_noteon(synth, channel, key, velocity);
  }

  public void releaseKey(int channel, int key) {
    FluidSynth.fluid_synth_noteoff(synth, channel, key);
  }

  /**
   * Modifies the gain for the synthesizer.
   * 
   * @param gain
   *          value in the range 0.0 to 10.0
   */
  public void setGain(float gain) {
    FluidSynth.fluid_synth_set_gain(synth, gain);
  }

}
