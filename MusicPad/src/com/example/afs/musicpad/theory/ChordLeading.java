// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.theory;

import java.util.HashMap;
import java.util.Map;

public class ChordLeading {

  private Map<ScaleBasedChordType, ScaleBasedChordType[]> map = new HashMap<>();

  public ChordLeading(ScaleBasedChordTypes scaleBasedChordTypes) {
    add(scaleBasedChordTypes.I, scaleBasedChordTypes.V, scaleBasedChordTypes.IV, scaleBasedChordTypes.vi, scaleBasedChordTypes.vii, scaleBasedChordTypes.I);
    add(scaleBasedChordTypes.ii, scaleBasedChordTypes.I, scaleBasedChordTypes.iii, scaleBasedChordTypes.vi, scaleBasedChordTypes.ii);
    add(scaleBasedChordTypes.iii, scaleBasedChordTypes.I, scaleBasedChordTypes.IV, scaleBasedChordTypes.vii, scaleBasedChordTypes.iii);
    add(scaleBasedChordTypes.IV, scaleBasedChordTypes.I, scaleBasedChordTypes.IV, scaleBasedChordTypes.ii, scaleBasedChordTypes.iii, scaleBasedChordTypes.vi);
    add(scaleBasedChordTypes.V, scaleBasedChordTypes.I, scaleBasedChordTypes.IV, scaleBasedChordTypes.V, scaleBasedChordTypes.ii, scaleBasedChordTypes.vi);
    add(scaleBasedChordTypes.vi, scaleBasedChordTypes.I, scaleBasedChordTypes.iii, scaleBasedChordTypes.vi);
    add(scaleBasedChordTypes.vii, scaleBasedChordTypes.I, scaleBasedChordTypes.IV, scaleBasedChordTypes.ii, scaleBasedChordTypes.vii);
  }

  public ScaleBasedChordType[] getSources(ScaleBasedChordType target) {
    return map.get(target);
  }

  private void add(ScaleBasedChordType target, ScaleBasedChordType... sources) {
    map.put(target, sources);
  }
}