// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.analyzer;

public class Concurrency {

  private int occupancy;
  private int polyphony;

  public Concurrency(int occupancy, int polyphony) {
    this.occupancy = occupancy;
    this.polyphony = polyphony;
  }

  public int getOccupancy() {
    return occupancy;
  }

  public int getPolyphony() {
    return polyphony;
  }

  @Override
  public String toString() {
    return "Concurrency [occupancy=" + occupancy + ", polyphony=" + polyphony + "]";
  }

}
