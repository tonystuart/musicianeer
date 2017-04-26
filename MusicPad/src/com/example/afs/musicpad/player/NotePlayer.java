// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.LinkedList;
import java.util.List;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.Device;
import com.example.afs.musicpad.player.PrompterData.BrowserMusic;
import com.example.afs.musicpad.player.PrompterData.BrowserWords;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class NotePlayer extends Player {

  public NotePlayer(Synthesizer synthesizer, Song song, Device device) {
    super(synthesizer, song, device);
  }

  @Override
  public PrompterData getPrompterData() {
    int channel = device.getChannel();
    List<BrowserWords> words = getWords();
    List<BrowserMusic> music = new LinkedList<>();
    for (Note note : song.getNotes()) {
      if (note.getChannel() == channel) {
        int midiNote = note.getMidiNote();
        long tick = note.getTick();
        int duration = (int) note.getDuration();
        BrowserMusic browserMusic = new BrowserMusic(tick, midiNote, duration);
        music.add(browserMusic);
      }
    }
    int lowest = song.getLowestMidiNote(channel);
    int highest = song.getHighestMidiNote(channel);
    String[] legend = getLegend(lowest, highest);
    PrompterData prompterData = new PrompterData(song, device, legend, lowest, highest, words, music);
    return prompterData;
  }

  @Override
  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

}
