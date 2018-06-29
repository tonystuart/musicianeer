// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.parser;

import com.example.afs.musicianeer.midi.Midi;

public class ChannelDetails {
  private Detail[] channelDetails = new Detail[Midi.CHANNELS];

  public ChannelDetails() {
    for (int i = 0; i < channelDetails.length; i++) {
      channelDetails[i] = new Detail();
    }
  }

  public Detail getDetail(int channel) {
    return channelDetails[channel];
  }

}