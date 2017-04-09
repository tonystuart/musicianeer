// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

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

  public static float scale(float dataMinimum, float dataMaximum, float controlMinimum, float controlMaximum, float controlValue) {
    float dataRange = dataMaximum - dataMinimum;
    float controlRange = controlMaximum - controlMinimum;
    float scaledControlValue = (controlValue - controlMinimum) / controlRange;
    float scaledValue = dataMinimum + (scaledControlValue * dataRange);
    if (scaledValue < dataMinimum) {
      scaledValue = dataMinimum;
    } else if (scaledValue > dataMaximum) {
      scaledValue = dataMaximum;
    }
    System.out.println("control=" + controlValue + " in " + controlMinimum + " to " + controlMaximum + " is " + scaledValue + " in range " + dataMinimum + " to " + dataMaximum);
    return scaledValue;
  }

  public static int scale(int dataMinimum, int dataMaximum, int controlMinimum, int controlMaximum, int controlValue) {
    int dataRange = dataMaximum - dataMinimum;
    int controlRange = controlMaximum - controlMinimum;
    double scaledControlValue = (double) (controlValue - controlMinimum) / controlRange;
    int scaledValue = (int) (dataMinimum + (scaledControlValue * dataRange));
    if (scaledValue < dataMinimum) {
      scaledValue = dataMinimum;
    } else if (scaledValue > dataMaximum) {
      scaledValue = dataMaximum;
    }
    System.out.println("control=" + controlValue + " in " + controlMinimum + " to " + controlMaximum + " is " + scaledValue + " in range " + dataMinimum + " to " + dataMaximum);
    return scaledValue;
  }

}
