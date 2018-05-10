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

public class SynthesizerSettings {

  private final boolean[] muteSettings;
  private final boolean[] soloSettings;

  public SynthesizerSettings(boolean[] muteSettings, boolean[] soloSettings) {
    this.muteSettings = Arrays.copyOf(muteSettings, muteSettings.length);
    this.soloSettings = Arrays.copyOf(soloSettings, soloSettings.length);
  }

  public boolean[] getMuteSettings() {
    return muteSettings;
  }

  public boolean[] getSoloSettings() {
    return soloSettings;
  }

  @Override
  public String toString() {
    return "SynthesizerSettings [muteSettings=" + Arrays.toString(muteSettings) + ", soloSettings=" + Arrays.toString(soloSettings) + "]";
  }

}
