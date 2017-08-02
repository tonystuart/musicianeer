// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Range extends Input {

  public Range(String... properties) {
    super(properties);
    appendProperty("type", "range");
  }

  @Deprecated
  public Range(String id, int minimum, int maximum, int step, int value) {
    this("." + id);
    setMinimum(minimum);
    setMaximum(maximum);
    setStep(step);
    setValue(value);
  }

  public void setMaximum(Object maximum) {
    appendProperty("max", maximum);
  }

  public void setMinimum(Object minimum) {
    appendProperty("min", minimum);
  }

  public void setStep(Object step) {
    appendProperty("step", step);
  }

}