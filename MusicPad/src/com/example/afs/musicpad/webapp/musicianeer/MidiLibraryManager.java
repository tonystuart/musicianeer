// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.io.File;
import java.util.LinkedHashMap;

import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.JsonUtilities;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class MidiLibraryManager extends ServiceTask {

  private static class FileContents {
    private LinkedHashMap<String, SongInfo> songInfoMap;
  }

  private static final String SONG_INFO = "songInfo.v1.json";

  private static final int SAVE_COUNT = 50;

  private int savePending;
  private int currentIndex;
  private MidiLibrary midiLibrary;
  private SongInfoFactory songInfoFactory;
  private LinkedHashMap<String, SongInfo> songInfoMap;

  protected MidiLibraryManager(MessageBroker broker) {
    super(broker);
    FileContents fileContents = JsonUtilities.fromJsonFile(SONG_INFO, FileContents.class);
    if (fileContents == null) {
      songInfoMap = new LinkedHashMap<>();
    } else {
      songInfoMap = fileContents.songInfoMap;
    }
    String path = System.getProperty("midiLibraryPath");
    if (path == null) {
      throw new IllegalStateException("midiLibraryPath property not set");
    }
    midiLibrary = new MidiLibrary(path);
    songInfoFactory = new SongInfoFactory(midiLibrary);
    provide(Services.getMidiLibrary, () -> getMidiLibrary());
    provide(Services.getSongInfoList, () -> songInfoMap.values());
    subscribe(OnSelectSong.class, message -> doSelectSong(message));
    setCallbackTimeout();
  }

  @Override
  public synchronized void tsStart() {
    super.tsStart();
    publish(new OnMidiLibrary(midiLibrary));
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    if (currentIndex < midiLibrary.size()) {
      File midiFile = midiLibrary.get(currentIndex);
      String fileName = midiFile.getName();
      SongInfo songInfo = songInfoMap.get(fileName);
      if (songInfo == null || songInfo.getTimeLastModified() != midiFile.lastModified()) {
        songInfo = songInfoFactory.getSongInfo(currentIndex);
        songInfoMap.put(fileName, songInfo);
        savePending++;
      }
      publish(new OnSongInfo(songInfo));
      if (currentIndex == 0) {
        publish(new OnSelectSong(currentIndex));
      }
      currentIndex++;
    }
    applyCachePolicy();
    setCallbackTimeout();
  }

  private void applyCachePolicy() {
    if (savePending > 0) {
      if (currentIndex == midiLibrary.size() || (savePending == SAVE_COUNT)) {
        System.out.println("applyCachePolicy: currentIndex=" + currentIndex + ", midiLibrary.size()=" + midiLibrary.size() + ", savePending=" + savePending);
        saveCache();
      }
    }
  }

  private void doSelectSong(OnSelectSong message) {
    int songIndex = message.getSongIndex();
    selectSong(songIndex);
  }

  private MidiLibrary getMidiLibrary() {
    return midiLibrary;
  }

  private void saveCache() {
    FileContents fileContents = new FileContents();
    fileContents.songInfoMap = songInfoMap;
    JsonUtilities.toJsonFile(SONG_INFO, fileContents);
    savePending = 0;
  }

  private void selectSong(int songIndex) {
    if (songIndex < 0 || songIndex >= midiLibrary.size()) {
      throw new IndexOutOfBoundsException();
    }
    Song song = midiLibrary.readSong(songIndex);
    String fileName = song.getFile().getName();
    SongInfo songInfo = songInfoMap.get(fileName);
    CurrentSong currentSong = new CurrentSong(song, songInfo);
    publish(new OnSongSelected(currentSong));
  }

  private void setCallbackTimeout() {
    setTimeoutMillis(currentIndex < midiLibrary.size() ? 10 : 0);
  }

}
