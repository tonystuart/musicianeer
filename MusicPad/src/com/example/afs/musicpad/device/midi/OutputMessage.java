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
  private double x;
  private double y;

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

  public InputType getInputType() {
    return inputType;
  }

  public String getLabel() {
    return label;
  }

  public OutputType getOutputType() {
    return outputType;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setInputType(InputType inputType) {
    this.inputType = inputType;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setOutputType(OutputType outputType) {
    this.outputType = outputType;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return "OutputMessage [inputType=" + inputType + ", outputType=" + outputType + ", index=" + index + ", label=" + label + ", x=" + x + ", y=" + y + "]";
  }

}