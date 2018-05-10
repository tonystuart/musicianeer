package com.example.afs.musicpad.webapp.musicianeer;

import java.util.Arrays;

public class BooleanArray {

  private int setCount;
  private boolean[] values;

  public BooleanArray(int size) {
    this.values = new boolean[size];
  }

  public int getSetCount() {
    return setCount;
  }

  public boolean isSet(int channel) {
    return values[channel];
  }

  public void set(int channel, boolean value) {
    if (value) {
      if (!values[channel]) {
        setCount++;
        values[channel] = true;
      }
    } else {
      if (values[channel]) {
        setCount--;
        values[channel] = false;
      }
    }
  }

  public void setAll(boolean value) {
    Arrays.fill(values, value);
    setCount = value ? values.length : 0;
  }

  @Override
  public String toString() {
    return "BooleanArray [setCount=" + setCount + ", values=" + Arrays.toString(values) + "]";
  }
}
