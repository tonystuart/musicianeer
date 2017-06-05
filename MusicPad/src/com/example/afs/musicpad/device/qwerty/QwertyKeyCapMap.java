// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.qwerty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.renderer.Notator.KeyCap;
import com.example.afs.musicpad.renderer.Notator.Slice;
import com.example.afs.musicpad.util.Count;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class QwertyKeyCapMap implements KeyCapMap {

  private static class SoundCount extends Count<Sound> {

    public SoundCount(Sound value) {
      super(value);
    }

    @Override
    public int compareTo(Count<Sound> that) {
      return -super.compareTo(that); // descending sort order
    }

  }

  private final int noteKeyCount;
  private final int registerKeyCount;
  private final int supportedSounds;

  private final String noteKeys;
  private final String registerKeys;
  private final OutputType outputType;
  private final Sound[][] keyIndexToSounds;
  private final RandomAccessList<KeyCap> keyCaps;

  private int autoRegister;
  private int registerDown;

  public QwertyKeyCapMap(String noteKeys, String registerKeys, RandomAccessList<Slice> slices, OutputType outputType) {
    this.noteKeys = noteKeys;
    this.registerKeys = registerKeys;
    this.outputType = outputType;
    this.noteKeyCount = noteKeys.length();
    this.registerKeyCount = registerKeys.length();
    this.supportedSounds = noteKeyCount * registerKeyCount;
    Map<Sound, SoundCount> sounds = getUniqueSoundCounts(slices);
    SoundCount[] sortedSounds = sortByFrequency(sounds);
    int maxSounds = Math.min(sounds.size(), supportedSounds);
    keyIndexToSounds = assignSoundsToRegisters(sortedSounds, maxSounds);
    sortByPitch(keyIndexToSounds, maxSounds);
    Map<Sound, String> soundToLegend = assignSoundsToLegend(keyIndexToSounds, maxSounds);
    keyCaps = getKeyCaps(soundToLegend, slices);
  }

  @Override
  public RandomAccessList<KeyCap> getKeyCaps() {
    return keyCaps;
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

  private Map<Sound, String> assignSoundsToLegend(Sound[][] keyIndexToSound, int maxSounds) {
    int index = 0;
    Map<Sound, String> soundToLegend = new HashMap<>();
    for (int i = 0; i < registerKeyCount && index < maxSounds; i++) {
      for (int j = 0; j < noteKeyCount && index < maxSounds; j++) {
        Sound sound = keyIndexToSound[i][j];
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

  private RandomAccessList<KeyCap> getKeyCaps(Map<Sound, String> soundToLegend, RandomAccessList<Slice> slices) {
    RandomAccessList<KeyCap> keyCaps = new DirectList<>();
    for (Slice slice : slices) {
      Sound sound = slice.getSound();
      String legend = soundToLegend.get(sound);
      if (legend == null) {
        legend = "?";
      }
      KeyCap keyCap = new KeyCap(slice, legend);
      keyCaps.add(keyCap);
    }
    return keyCaps;
  }

  private String getLegend(int register, int digit) {
    String registerString;
    if (register == 0) {
      registerString = "";
    } else {
      registerString = String.valueOf(registerKeys.charAt(register));
    }
    return registerString + String.valueOf(noteKeys.charAt(digit));
  }

  private Map<Sound, SoundCount> getUniqueSoundCounts(RandomAccessList<Slice> slices) {
    Map<Sound, SoundCount> sounds = new HashMap<>();
    for (Slice slice : slices) {
      Sound sound = slice.getSound();
      SoundCount count = sounds.get(sound);
      if (count == null) {
        count = new SoundCount(sound);
        sounds.put(sound, count);
      }
      count.increment();
    }
    return sounds;
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