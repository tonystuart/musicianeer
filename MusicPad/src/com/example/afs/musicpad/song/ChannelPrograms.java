// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.song;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ChannelPrograms {

  public static class ChannelProgram {
    private int channel;
    private int program;

    public ChannelProgram(int channel, int program) {
      this.channel = channel;
      this.program = program;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ChannelPrograms.ChannelProgram other = (ChannelPrograms.ChannelProgram) obj;
      if (channel != other.channel) {
        return false;
      }
      if (program != other.program) {
        return false;
      }
      return true;
    }

    public int getChannel() {
      return channel;
    }

    public int getProgram() {
      return program;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + channel;
      result = prime * result + program;
      return result;
    }

    @Override
    public String toString() {
      return "ChannelProgram [channel=" + channel + ", program=" + program + "]";
    }

  }

  private int[] currentPrograms = new int[Midi.CHANNELS];
  private Set<ChannelPrograms.ChannelProgram> channelPrograms = new HashSet<>();

  public List<String> getProgramNames(int channel) {
    List<String> programNames = new LinkedList<>();
    for (ChannelPrograms.ChannelProgram channelProgram : channelPrograms) {
      if (channelProgram.getChannel() == channel) {
        int program = channelProgram.getProgram();
        String programName = Instruments.getInstrumentName(program) + "/" + program;
        programNames.add(programName);
      }
    }
    return programNames;
  }

  public void save(int channel, int program) {
    if (currentPrograms[channel] != program) {
      channelPrograms.add(new ChannelProgram(channel, program));
      currentPrograms[channel] = program;
    }
  }
}