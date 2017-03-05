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
      settings = FluidSynth.newFluidSettings();
    }

    public long getSettings() {
      return settings;
    }

    public void set(String name, String value) {
      FluidSynth.fluidSettingsSetstr(settings, name, value);
    }
  }

  static {
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
  }

  private static Settings createDefaultSettings() {
    Settings settings = new Settings();
    settings.set("audio.driver", "alsa");
    // settings.set("synth.chorus.active", "no");
    // settings.set("synth.reverb.active", "no");
    return settings;
  }

  private long synth;

  public Synthesizer() {
    this(createDefaultSettings());
  }

  public Synthesizer(Settings settings) {
    synth = FluidSynth.newFluidSynth(settings.getSettings());
    FluidSynth.newFluidAudioDriver(settings.getSettings(), synth);
    FluidSynth.fluidSynthSfload(synth, "/usr/share/sounds/sf2/FluidR3_GM.sf2", 1);
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
    FluidSynth.fluidSynthPitchBend(synth, channel, val);
  }

  public void changeProgram(int channel, int program) {
    FluidSynth.fluidSynthProgramChange(synth, channel, program);
  }

  public void pressKey(int channel, int key, int velocity) {
    FluidSynth.fluidSynthNoteon(synth, channel, key, velocity);
  }

  public void releaseKey(int channel, int key) {
    FluidSynth.fluidSynthNoteoff(synth, channel, key);
  }

  /**
   * Modifies the gain for the synthesizer.
   * 
   * @param gain
   *          value in the range 0.0 to 10.0
   */
  public void setGain(float gain) {
    FluidSynth.fluidSynthSetGain(synth, gain);
  }

}
