// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.midi;

import java.io.File;
import java.util.Iterator;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class MidiLibrary implements Iterable<File> {

  private final RandomAccessList<File> midiFiles;

  public MidiLibrary(String path) {
    this.midiFiles = new DirectList<>();
    listMidiFiles(midiFiles, new File(path));
    if (midiFiles.size() == 0) {
      throw new IllegalArgumentException(path + " does not contain any .mid or .kar files");
    }
    midiFiles.sort((o1, o2) -> o1.getPath().compareTo(o2.getPath()));
  }

  public File get(int fileIndex) {
    return midiFiles.get(fileIndex);
  }

  public RandomAccessList<File> getMidiFiles() {
    return midiFiles;
  }

  @Override
  public Iterator<File> iterator() {
    return midiFiles.iterator();
  }

  public int size() {
    return midiFiles.size();
  }

  private boolean isMidiFile(String name) {
    String lowerCaseName = name.toLowerCase();
    boolean isMidi = lowerCaseName.endsWith(".mid") || lowerCaseName.endsWith(".midi") || lowerCaseName.endsWith(".kar");
    return isMidi;
  }

  private void listMidiFiles(RandomAccessList<File> midiFiles, File parent) {
    if (!parent.isDirectory() || !parent.canRead()) {
      throw new IllegalArgumentException(parent + " is not a readable directory");
    }
    File[] files = parent.listFiles((dir, name) -> isMidiFile(name));
    for (File file : files) {
      if (file.isFile()) {
        midiFiles.add(file);
      } else if (file.isDirectory()) {
        listMidiFiles(midiFiles, file);
      }
    }
  }
}