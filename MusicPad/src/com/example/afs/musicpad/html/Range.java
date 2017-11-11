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
    setProperty("type", "range");
  }

  public void setMaximum(Object maximum) {
    setProperty("max", maximum);
  }

  public void setMinimum(Object minimum) {
    setProperty("min", minimum);
  }

  public void setStep(Object step) {
    setProperty("step", step);
  }

}