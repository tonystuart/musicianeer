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

import com.example.afs.musicpad.device.common.InputMap;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class PlayableMap {

  public enum OutputType {
    TICK, MEASURE
  }

  private int autoGroup;
  private int groupDown;

  private final int groupCount;
  private final int soundCount;
  private final int supportedSounds;

  private final Sounds sounds;
  private final Sound[][] inputIndexToSound;
  private final RandomAccessList<Playable> playables;
  private InputMap groupInputMap;
  private InputMap soundInputMap;

  public PlayableMap(InputMap groupInputMap, InputMap soundInputMap, Iterable<Note> notes, OutputType outputType) {
    this.groupInputMap = groupInputMap;
    this.soundInputMap = soundInputMap;
    this.groupCount = groupInputMap.size();
    this.soundCount = soundInputMap.size();
    this.supportedSounds = soundCount * groupCount;
    this.sounds = new Sounds(outputType, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    SoundCount[] sortedSounds = sortByFrequency(uniqueSoundCounts);
    int maxSounds = Math.min(uniqueSoundCounts.size(), supportedSounds);
    inputIndexToSound = assignSoundsToGroups(sortedSounds, maxSounds);
    sortByPitch(inputIndexToSound, maxSounds);
    Map<Sound, String> soundToLegend = assignSoundsToLegend(maxSounds);
    playables = getPlayables(soundToLegend);
  }

  public RandomAccessList<Playable> getPlayables() {
    return playables;
  }

  public Sound onDown(int inputCode) {
    Sound sound;
    int soundIndex = soundInputMap.indexOf(inputCode);
    if (soundIndex != -1) {
      int thisGroup = groupDown != 0 ? groupDown : autoGroup;
      sound = inputIndexToSound[thisGroup][soundIndex];
      //System.out.println("inputCode=" + inputCode + ", soundIndex=" + soundIndex + ", sound=" + sound);
      autoGroup = 0;
    } else {
      sound = null;
      int index = groupInputMap.indexOf(inputCode);
      if (index != -1) {
        groupDown = index;
        autoGroup = groupDown;
      }
    }
    return sound;
  }

  public void onUp(int inputCode) {
    if (groupInputMap.indexOf(inputCode) != -1) {
      groupDown = 0;
    }
  }

  private Sound[][] assignSoundsToGroups(SoundCount[] sortedSounds, int maxSounds) {
    int index = 0;
    Sound[][] inputIndexToSound = new Sound[groupCount][soundCount];
    for (int i = 0; i < groupCount; i++) {
      for (int j = 0; j < soundCount; j++) {
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
    for (int i = 0; i < groupCount && index < maxSounds; i++) {
      for (int j = 0; j < soundCount && index < maxSounds; j++) {
        Sound sound = inputIndexToSound[i][j];
        soundToLegend.put(sound, getLegend(i, j));
        index++;
      }
    }
    return soundToLegend;
  }

  private String getLegend(int groupIndex, int soundIndex) {
    String groupString;
    if (groupIndex == 0) {
      groupString = "";
    } else {
      groupString = groupInputMap.getLabel(groupIndex) + "+";
    }
    return groupString + soundInputMap.getLabel(soundIndex);
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
      Arrays.sort(inputIndexToSound[i], 0, Math.min(soundCount, amountRemaining));
      amountRemaining = Math.max(amountRemaining - soundCount, 0);
    }
  }

}