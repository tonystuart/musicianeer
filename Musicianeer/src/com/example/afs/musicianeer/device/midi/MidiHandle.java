// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.device.midi;

public class MidiHandle {

  public enum Type {
    INPUT, OUTPUT
  }

  public static final int MIDI_HANDLE_NA = -1;

  private int index;
  private Type type;
  private String name;

  public MidiHandle(int index, Type type, String name) {
    this.index = index;
    this.type = type;
    this.name = name;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "MidiHandle [index=" + index + ", type=" + type + ", name=" + name + "]";
  }
}
