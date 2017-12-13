// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

public class OutputMessage {
  private InputType inputType;
  private OutputType outputType;
  private int index;
  private String label;

  public OutputMessage(InputType inputType, OutputType outputType, int index, String label) {
    this.inputType = inputType;
    this.outputType = outputType;
    this.index = index;
    this.label = label;
  }

  public InputType getDeviceType() {
    return inputType;
  }

  public int getIndex() {
    return index;
  }

  public String getLabel() {
    return label;
  }

  public OutputType getOutputType() {
    return outputType;
  }

}