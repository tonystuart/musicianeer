// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import com.example.afs.musicpad.midi.Midi;

public class Range {

  public static int conform(int inputValue, int minimum, int maximum) {
    int outputValue = inputValue;
    if (outputValue < minimum) {
      outputValue = minimum;
    } else if (outputValue > maximum) {
      outputValue = maximum;
    }
    return outputValue;
  }

  public static int conformWithin(int inputValue, int minimum, int maximum) {
    return conform(inputValue, minimum, maximum - 1);
  }

  public static float scale(float targetMinimum, float targetMaximum, float sourceMinimum, float sourceMaximum, float sourceValue) {
    float targetRange = targetMaximum - targetMinimum;
    float sourceRange = sourceMaximum - sourceMinimum;
    float scaledSourceValue = (sourceValue - sourceMinimum) / sourceRange;
    float scaledValue = targetMinimum + (scaledSourceValue * targetRange);
    if (scaledValue < targetMinimum) {
      scaledValue = targetMinimum;
    } else if (scaledValue > targetMaximum) {
      scaledValue = targetMaximum;
    }
    //System.out.println("source=" + sourceValue + " in " + sourceMinimum + " to " + sourceMaximum + " is " + scaledValue + " in range " + targetMinimum + " to " + targetMaximum);
    return scaledValue;
  }

  public static int scale(int targetMinimum, int targetMaximum, int sourceMinimum, int sourceMaximum, int sourceValue) {
    int targetRange = targetMaximum - targetMinimum;
    int sourceRange = sourceMaximum - sourceMinimum;
    double scaledSourceValue = (double) (sourceValue - sourceMinimum) / sourceRange;
    int scaledValue = (int) (targetMinimum + (scaledSourceValue * targetRange));
    if (scaledValue < targetMinimum) {
      scaledValue = targetMinimum;
    } else if (scaledValue > targetMaximum) {
      scaledValue = targetMaximum;
    }
    //System.out.println("source=" + sourceValue + " in " + sourceMinimum + " to " + sourceMaximum + " is " + scaledValue + " in range " + targetMinimum + " to " + targetMaximum);
    return scaledValue;
  }

  public static int scaleMidiToPercent(int midiValue) {
    return scale(0, 100, Midi.MIN_VALUE, Midi.MAX_VALUE, midiValue);
  }

  public static int scalePercentToMidi(int percentValue) {
    return scale(Midi.MIN_VALUE, Midi.MAX_VALUE, 0, 100, percentValue);
  }

}
