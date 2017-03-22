// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.fluidsynth;

public class FluidSynth {

  public static final String NATIVE_LIBRARY_NAME = "fluidsynth_jni";

  public static final int CHANNEL_TYPE_MELODIC = 0;

  public static final int CHANNEL_TYPE_DRUM = 1;

  public static native int capture(int fd, int value);

  public static native void delete_fluid_audio_driver(long driver);

  public static native void delete_fluid_settings(long settings);

  public static native void delete_fluid_synth(long synth);

  public static native void fluid_settings_setint(long settings, String name, int val);

  public static native void fluid_settings_setstr(long settings, String name, String str);

  public static native int fluid_synth_all_notes_off(long synth, int chan);

  public static native int fluid_synth_all_sounds_off(long synth, int chan);

  public static native int fluid_synth_cc(long synth, int chan, int num, int val);

  public static native int fluid_synth_noteoff(long synth, int chan, int key);

  public static native int fluid_synth_noteon(long synth, int chan, int key, int vel);

  public static native int fluid_synth_pitch_bend(long synth, int chan, int val);

  public static native int fluid_synth_program_change(long synth, int chan, int program);

  public static native long fluid_synth_set_channel_type(long synth, int channel, int type);

  public static native long fluid_synth_set_gain(long synth, float gain);

  public static native long fluid_synth_sfload(long synth, String filename, int resetPresets);

  public static native long new_fluid_audio_driver(long settings, long synth);

  public static native long new_fluid_settings();

  public static native long new_fluid_synth(long settings);

}
