// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.Prompter.PrompterChannel;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.JsonUtilities;
import com.example.afs.musicpad.webapp.RestServlet;

public class SongAllNotePlayer extends SongPlayer {

  public SongAllNotePlayer(Synthesizer synthesizer, Song song, int channel) {
    super(synthesizer, song, channel);
    Prompter prompter = new Prompter(song, channel);
    PrompterChannel prompterChannel = prompter.getPrompterChannel();
    String json = JsonUtilities.toJson(prompterChannel);
    System.out.println(json);
    RestServlet.queue.add(prompterChannel);
  }

  @Override
  public void play(Action action, int noteIndex) {
    if (noteIndex >= 0 && noteIndex < Midi.NOTES) {
      playMidiNote(action, noteIndex);
    }
  }

  @Override
  protected String getMusic(long currentTick, long firstTick, long lastTick, int ticksPerCharacter) {
    return "Coming Soon!";
  }

}
