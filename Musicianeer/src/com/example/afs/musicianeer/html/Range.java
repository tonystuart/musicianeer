// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.html;

public class Range extends NumericInput {

  public Range(String... properties) {
    super(properties);
    setProperty("type", "range");
  }

  public void setStep(Object step) {
    setProperty("step", step);
  }

}