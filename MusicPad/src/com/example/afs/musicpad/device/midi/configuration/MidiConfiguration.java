// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class MidiConfiguration {

  private OnInitialization onInitialization;
  private OnInput onInput;
  private OnOutput onOutput;

  public OnInitialization getOnInitialization() {
    return onInitialization;
  }

  public OnInput getOnInput() {
    return onInput;
  }

  public OnOutput getOnOutput() {
    return onOutput;
  }

  public void setOnInitialization(OnInitialization onInitialization) {
    if (this.onInitialization != null) {
      throw new IllegalStateException("Line " + (onInitialization.getLineIndex() + 1) + ": onInitialization already set at line " + (this.onInitialization.getLineIndex() + 1));
    }
    this.onInitialization = onInitialization;
  }

  public void setOnInput(OnInput onInput) {
    if (this.onInput != null) {
      throw new IllegalStateException("Line " + (onInput.getLineIndex() + 1) + ": onInput already set at line " + (this.onInput.getLineIndex() + 1));
    }
    this.onInput = onInput;
  }

  public void setOnOutput(OnOutput onOutput) {
    if (this.onOutput != null) {
      throw new IllegalStateException("Line " + (onOutput.getLineIndex() + 1) + ": onOutput already set at line " + (this.onOutput.getLineIndex() + 1));
    }
    this.onOutput = onOutput;
  }

  @Override
  public String toString() {
    return "MidiConfiguration [onInitialization=" + onInitialization + ", onInput=" + onInput + ", onOutput=" + onOutput + "]";
  }
}
