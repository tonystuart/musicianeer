// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.device.common.Configuration;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class PlayableMap {

  public enum OutputType {
    TICK, MEASURE
  }

  private int autoBank;
  private int bankDown;

  private final int supportedSounds;

  protected final int[] bankInputCodes;
  protected final int[] noteInputCodes;

  private final Sounds sounds;
  private final Sound[][] inputIndexToSound;
  private final RandomAccessList<Playable> playables;
  private Configuration configuration;

  public PlayableMap(Configuration configuration, Iterable<Note> notes, OutputType outputType) {
    this.configuration = configuration;
    this.noteInputCodes = configuration.getNoteInputCodes();
    this.bankInputCodes = configuration.getBankInputCodes();
    this.supportedSounds = noteInputCodes.length * bankInputCodes.length;
    this.sounds = new Sounds(outputType, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    SoundCount[] sortedSounds = sortByFrequency(uniqueSoundCounts);
    int maxSounds = Math.min(uniqueSoundCounts.size(), supportedSounds);
    inputIndexToSound = assignSoundsToBanks(sortedSounds, maxSounds);
    sortByPitch(inputIndexToSound, maxSounds);
    Map<Sound, String> soundToLegend = assignSoundsToLegend(maxSounds);
    playables = getPlayables(soundToLegend);
  }

  public RandomAccessList<Playable> getPlayables() {
    return playables;
  }

  public Sound onDown(int inputCode) {
    Sound sound;
    int noteIndex = indexOf(noteInputCodes, inputCode);
    if (noteIndex != -1) {
      int thisBank = bankDown != 0 ? bankDown : autoBank;
      sound = inputIndexToSound[thisBank][noteIndex];
      //System.out.println("inputCode=" + inputCode + ", noteIndex=" + noteIndex + ", sound=" + sound);
      autoBank = 0;
    } else {
      sound = null;
      int index = indexOf(bankInputCodes, inputCode);
      if (index != -1) {
        bankDown = index;
        autoBank = bankDown;
      }
    }
    return sound;
  }

  public void onUp(int inputCode) {
    if (indexOf(bankInputCodes, inputCode) != -1) {
      bankDown = 0;
    }
  }

  private Sound[][] assignSoundsToBanks(SoundCount[] sortedSounds, int maxSounds) {
    int index = 0;
    Sound[][] inputIndexToSound = new Sound[bankInputCodes.length][noteInputCodes.length];
    for (int i = 0; i < bankInputCodes.length; i++) {
      for (int j = 0; j < noteInputCodes.length; j++) {
        if (index < maxSounds) {
          Sound sound = sortedSounds[index].getValue();
          inputIndexToSound[i][j] = sound;
          index++;
        } else {
          inputIndexToSound[i][j] = null;
        }
      }
    }
    return inputIndexToSound;
  }

  private Map<Sound, String> assignSoundsToLegend(int maxSounds) {
    int index = 0;
    Map<Sound, String> soundToLegend = new HashMap<>();
    for (int i = 0; i < bankInputCodes.length && index < maxSounds; i++) {
      for (int j = 0; j < noteInputCodes.length && index < maxSounds; j++) {
        Sound sound = inputIndexToSound[i][j];
        soundToLegend.put(sound, getLegend(i, j));
        index++;
      }
    }
    return soundToLegend;
  }

  private String getLegend(int bankIndex, int noteIndex) {
    String bankString;
    if (bankIndex == 0) {
      bankString = "";
    } else {
      bankString = configuration.getBankLegend(bankIndex);
    }
    return bankString + configuration.getNoteLegend(noteIndex);
  }

  private RandomAccessList<Playable> getPlayables(Map<Sound, String> soundToLegend) {
    RandomAccessList<Playable> playables = new DirectList<>();
    for (Sound sound : sounds) {
      String legend = soundToLegend.get(sound);
      if (legend == null) {
        legend = "?";
      }
      Playable playable = new Playable(sound, legend);
      playables.add(playable);
    }
    return playables;
  }

  private int indexOf(int[] array, int inputCode) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == inputCode) {
        return i;
      }
    }
    return -1;
  }

  private SoundCount[] sortByFrequency(Map<Sound, SoundCount> sounds) {
    int index = 0;
    SoundCount[] sortedSounds = new SoundCount[sounds.size()];
    for (SoundCount soundCount : sounds.values()) {
      sortedSounds[index++] = soundCount;
    }
    Arrays.sort(sortedSounds);
    return sortedSounds;
  }

  private void sortByPitch(Sound[][] inputIndexToSound, int maxSounds) {
    int amountRemaining = maxSounds;
    for (int i = 0; amountRemaining > 0 && i < inputIndexToSound[i].length; i++) {
      Arrays.sort(inputIndexToSound[i], 0, Math.min(noteInputCodes.length, amountRemaining));
      amountRemaining = Math.max(amountRemaining - noteInputCodes.length, 0);
    }
  }

}