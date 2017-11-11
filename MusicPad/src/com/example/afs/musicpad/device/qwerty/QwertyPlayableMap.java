// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.player.Playable;
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class QwertyPlayableMap implements PlayableMap {

  private int autoRegister;
  private int registerDown;

  private final int noteKeyCount;
  private final int registerKeyCount;
  private final int supportedSounds;

  private final String noteKeys;
  private final String registerKeys;

  private final Sounds sounds;
  private final Sound[][] keyIndexToSounds;
  private final RandomAccessList<Playable> playables;

  public QwertyPlayableMap(Iterable<Note> notes, OutputType outputType, String noteKeys, String registerKeys) {
    this.noteKeys = noteKeys;
    this.registerKeys = registerKeys;
    this.noteKeyCount = noteKeys.length();
    this.registerKeyCount = registerKeys.length();
    this.supportedSounds = noteKeyCount * registerKeyCount;
    this.sounds = new Sounds(outputType, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    SoundCount[] sortedSounds = sortByFrequency(uniqueSoundCounts);
    int maxSounds = Math.min(uniqueSoundCounts.size(), supportedSounds);
    keyIndexToSounds = assignSoundsToRegisters(sortedSounds, maxSounds);
    sortByPitch(keyIndexToSounds, maxSounds);
    Map<Sound, String> soundToLegend = assignSoundsToLegend(maxSounds);
    playables = getPlayables(soundToLegend);
  }

  @Override
  public RandomAccessList<Playable> getPlayables() {
    return playables;
  }

  @Override
  public Sound onDown(int inputCode) {
    Sound sound;
    int keyIndex = noteKeys.indexOf(inputCode);
    if (keyIndex != -1) {
      int thisRegister = registerDown != 0 ? registerDown : autoRegister;
      sound = keyIndexToSounds[thisRegister][keyIndex];
      //System.out.println("inputCode=" + inputCode + ", keyIndex=" + keyIndex + ", sound=" + sound);
      autoRegister = 0;
    } else {
      sound = null;
      int index = registerKeys.indexOf(inputCode);
      if (index != -1) {
        registerDown = index;
        autoRegister = registerDown;
      }
    }
    return sound;
  }

  @Override
  public void onUp(int inputCode) {
    if (registerKeys.indexOf(inputCode) != -1) {
      registerDown = 0;
    }
  }

  private Map<Sound, String> assignSoundsToLegend(int maxSounds) {
    int index = 0;
    Map<Sound, String> soundToLegend = new HashMap<>();
    for (int i = 0; i < registerKeyCount && index < maxSounds; i++) {
      for (int j = 0; j < noteKeyCount && index < maxSounds; j++) {
        Sound sound = keyIndexToSounds[i][j];
        soundToLegend.put(sound, getLegend(i, j));
        index++;
      }
    }
    return soundToLegend;
  }

  private Sound[][] assignSoundsToRegisters(SoundCount[] sortedSounds, int maxSounds) {
    int index = 0;
    Sound[][] keyIndexToSound = new Sound[registerKeyCount][noteKeyCount];
    for (int i = 0; i < registerKeyCount; i++) {
      for (int j = 0; j < noteKeyCount; j++) {
        if (index < maxSounds) {
          Sound sound = sortedSounds[index].getValue();
          keyIndexToSound[i][j] = sound;
          index++;
        } else {
          keyIndexToSound[i][j] = null;
        }
      }
    }
    return keyIndexToSound;
  }

  private String getLegend(int register, int digit) {
    String registerString;
    if (register == 0) {
      registerString = "";
    } else {
      char registerPlayable = registerKeys.charAt(register);
      if (registerPlayable == KeyEvent.VK_SHIFT) {
        registerString = "Shift+";
      } else if (registerPlayable == KeyEvent.VK_CONTROL) {
        registerString = "Ctrl+";
      } else if (registerPlayable == KeyEvent.VK_ALT) {
        registerString = "Alt+";
      } else {
        registerString = String.valueOf(registerPlayable);
      }
    }
    return registerString + String.valueOf(noteKeys.charAt(digit));
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

  private void sortByPitch(Sound[][] keyIndexToSound, int maxSounds) {
    int amountRemaining = maxSounds;
    for (int i = 0; amountRemaining > 0 && i < keyIndexToSound[i].length; i++) {
      Arrays.sort(keyIndexToSound[i], 0, Math.min(noteKeyCount, amountRemaining));
      amountRemaining = Math.max(amountRemaining - noteKeyCount, 0);
    }
  }

}