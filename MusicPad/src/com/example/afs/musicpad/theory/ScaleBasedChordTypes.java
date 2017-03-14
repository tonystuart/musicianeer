// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

public class ScaleBasedChordTypes {

  private final Key key;

  public final ScaleBasedChordType I;
  public final ScaleBasedChordType ii;
  public final ScaleBasedChordType iii;
  public final ScaleBasedChordType IV;
  public final ScaleBasedChordType V;
  public final ScaleBasedChordType vi;
  public final ScaleBasedChordType vii;

  public final ScaleBasedChordType[] SCALE_BASED_CHORD_TYPES;

  public ScaleBasedChordTypes(Key key) {
    this.key = key;

    I = new ScaleBasedChordType(key, "I", 1);
    ii = new ScaleBasedChordType(key, "ii", 2);
    iii = new ScaleBasedChordType(key, "iii", 3);
    IV = new ScaleBasedChordType(key, "IV", 4);
    V = new ScaleBasedChordType(key, "V", 5);
    vi = new ScaleBasedChordType(key, "vi", 6);
    vii = new ScaleBasedChordType(key, "vii", 7);

    SCALE_BASED_CHORD_TYPES = new ScaleBasedChordType[] {
        I,
        ii,
        iii,
        IV,
        V,
        vi,
        vii
    };
  }

  public ScaleBasedChordType get(int degree) {
    return SCALE_BASED_CHORD_TYPES[degree];
  }

  public Key getKey() {
    return key;
  }

}