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

import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnMusic.Legend;
import com.example.afs.musicpad.message.OnMusic.Sound;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class NotePlayer extends Player {

  public NotePlayer(DeviceHandler deviceHandler, Song song) {
    super(deviceHandler, song);
    initializeOctave();
  }

  @Override
  public OnMusic getOnSongMusic() {
    List<Sound> songMusicList = new LinkedList<>();
    for (Note note : song.getNotes()) {
      if (note.getChannel() == songChannel) {
        int midiNote = note.getMidiNote();
        long tick = note.getTick();
        int duration = (int) note.getDuration();
        Sound sound = new Sound(tick, midiNote, duration);
        songMusicList.add(sound);
      }
    }
    int lowest = getLowestMidiNote();
    int highest = getHighestMidiNote();
    Legend[] legend = getLegend(lowest, highest);
    OnMusic onMusic = new OnMusic(song, index, songChannel, mappingType, legend, lowest, highest, songMusicList);
    return onMusic;
  }

  @Override
  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

  private void initializeOctave() {
    int octave = inputMapping.getDefaultOctave();
    int lowestMidiNote = getLowestMidiNote();
    int lowestOctave = lowestMidiNote / Midi.SEMITONES_PER_OCTAVE;
    if (lowestOctave < octave) {
      octave = lowestOctave;
    }
    inputMapping.setOctave(octave);
  }

}
