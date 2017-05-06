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
import com.example.afs.musicpad.device.common.InputMapping;
import com.example.afs.musicpad.message.OnMusic;
import com.example.afs.musicpad.message.OnMusic.Legend;
import com.example.afs.musicpad.message.OnMusic.Sound;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;

public class NotePlayer extends Player {

  public NotePlayer(Synthesizer synthesizer, Song song, Device device) {
    super(synthesizer, song, device);
    initializeOctave();
  }

  @Override
  public OnMusic getOnSongMusic() {
    int channel = device.getChannel();
    List<Sound> songMusicList = new LinkedList<>();
    for (Note note : song.getNotes()) {
      if (note.getChannel() == channel) {
        int midiNote = note.getMidiNote();
        long tick = note.getTick();
        int duration = (int) note.getDuration();
        Sound sound = new Sound(tick, midiNote, duration);
        songMusicList.add(sound);
      }
    }
    int lowest = song.getLowestMidiNote(channel);
    int highest = song.getHighestMidiNote(channel);
    Legend[] legend = getLegend(lowest, highest);
    OnMusic onMusic = new OnMusic(song, device, legend, lowest, highest, songMusicList);
    return onMusic;
  }

  @Override
  public void play(Action action, int midiNote) {
    playMidiNote(action, midiNote);
  }

  private void initializeOctave() {
    int channel = device.getChannel();
    InputMapping inputMapping = device.getInputMapping();
    int octave = inputMapping.getDefaultOctave();
    int lowestMidiNote = song.getLowestMidiNote(channel);
    int lowestOctave = lowestMidiNote / Midi.SEMITONES_PER_OCTAVE;
    if (lowestOctave < octave) {
      octave = lowestOctave;
    }
    inputMapping.setOctave(octave);
  }

}
