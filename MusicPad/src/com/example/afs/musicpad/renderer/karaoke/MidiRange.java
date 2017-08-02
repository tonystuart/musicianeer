// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import com.example.afs.musicpad.html.Range;

public class MidiRange extends Range {
  public MidiRange(String properties) {
    super(properties);
    setMinimum(0);
    setMaximum(127);
    setStep(1);
    setValue(64);
  }
}