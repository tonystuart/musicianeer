// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.tool;

import com.example.afs.jni.FluidSynth;

public class FluidSynthTest {

  public static void main(String[] args) throws Exception {
    System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
    long settings = FluidSynth.new_fluid_settings();
    long synth = FluidSynth.new_fluid_synth(settings);
    FluidSynth.fluid_settings_setstr(settings, "audio.driver", "alsa");
    long adriver = FluidSynth.new_fluid_audio_driver(settings, synth);
    FluidSynth.fluid_synth_sfload(synth, "/usr/share/sounds/sf2/FluidR3_GM.sf2", 1);
    for (int i = 0; i < 12; i++) {
      FluidSynth.fluid_synth_noteon(synth, 0, 60 + i, 64);
      Thread.sleep(500);
      FluidSynth.fluid_synth_noteoff(synth, 0, 60 + i);
    }
    FluidSynth.delete_fluid_audio_driver(adriver);
    FluidSynth.delete_fluid_synth(synth);
    FluidSynth.delete_fluid_settings(settings);
  }
}
