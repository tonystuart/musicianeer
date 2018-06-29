// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.song;

import com.example.afs.musicianeer.midi.Midi;

public class ChannelFacets {

  private Facet[] channelFacets = new Facet[Midi.CHANNELS];

  public ChannelFacets() {
    for (int i = 0; i < channelFacets.length; i++) {
      channelFacets[i] = new Facet();
    }
  }

  public void add(Note note) {
    int channel = note.getChannel();
    int program = note.getProgram();
    int midiNote = note.getMidiNote();
    Facet facet = channelFacets[channel];
    facet.countNote(midiNote);
    if (channel == Midi.DRUM) {
      facet.addProgram(midiNote);
    } else {
      facet.addProgram(program);
    }
  }

  public Facet getFacet(int channel) {
    return channelFacets[channel];
  }
}
