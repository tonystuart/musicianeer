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
import java.io.IOException;
import java.util.Random;
import java.util.TreeMap;

import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.JsonUtilities;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class MidiLibraryManager extends ServiceTask {

  private static class FileContents {
    private TreeMap<String, SongInfo> songInfoMap;
  }

  private static final int SAVE_COUNT = 50;

  public static String getMidiLibraryPath() {
    return getPath("midiLibraryPath", "musicianeer/midi");
  }

  public static String getSongInfoPath() {
    return getPath("songInfoPath", "musicianeer/midi.v1.json");
  }

  private static String getPath(String systemProperty, String defaultPath) {
    try {
      String path = System.getProperty(systemProperty);
      if (path == null) {
        path = defaultPath;
      }
      String canonicalPath = new File(path).getCanonicalPath();
      return canonicalPath;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int savePending;
  private int currentIndex;
  private int selectedIndex = -1;

  private MidiLibrary midiLibrary;
  private SongInfo mostRecentImport;
  private Random random = new Random();
  private SongInfoFactory songInfoFactory;
  private TreeMap<String, SongInfo> songInfoMap;

  protected MidiLibraryManager(MessageBroker broker) {
    super(broker);
    initializeDirectories();
    FileContents fileContents = JsonUtilities.fromJsonFile(getSongInfoPath(), FileContents.class);
    if (fileContents == null) {
      songInfoMap = new TreeMap<>();
    } else {
      songInfoMap = fileContents.songInfoMap;
    }
    provide(Services.getSongInfoList, () -> getSongInfoList());
    provide(Services.refreshMidiLibrary, () -> refreshMidiLibrary());
    provide(ImportService.class, message -> doImportService(message));
    subscribe(OnSelectSong.class, message -> doSelectSong(message));
    subscribe(OnDeleteMidiFile.class, message -> doDeleteMidiFile(message));
    refreshMidiLibrary();
  }

  @Override
  public synchronized void tsStart() {
    super.tsStart();
    publish(new OnMidiLibraryRefresh(midiLibrary));
  }

  @Override
  protected void onTimeout() throws InterruptedException {
    if (currentIndex < midiLibrary.size()) {
      File midiFile = midiLibrary.get(currentIndex);
      SongInfo songInfo = realizeSongInfo(midiFile);
      publish(new OnSongInfo(songInfo, currentIndex));
      if (songInfo == mostRecentImport || currentIndex == selectedIndex) {
        publish(new OnSelectSong(currentIndex));
        mostRecentImport = null;
        selectedIndex = -1;
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

  private void doDeleteMidiFile(OnDeleteMidiFile message) {
    selectedIndex = midiLibrary.delete(message.getFilename());
    if (selectedIndex == midiLibrary.size()) {
      selectedIndex--; // may end up -1
    }
    refreshMidiLibrary();
  }

  private SongInfo doImportService(Service<SongInfo> message) {
    SongInfo songInfo;
    ImportService importService = (ImportService) message;
    String filename = importService.getFilename();
    File file = new File(filename);
    try {
      songInfo = realizeSongInfo(file);
      mostRecentImport = songInfo;
    } catch (RuntimeException e) {
      file.delete();
      songInfo = null;
    }
    return songInfo;
  }

  private void doSelectSong(OnSelectSong message) {
    int songIndex = message.getSongIndex();
    selectSong(songIndex);
  }

  private RandomAccessList<OnSongInfo> getSongInfoList() {
    RandomAccessList<OnSongInfo> songInfoList = new DirectList<>();
    for (int i = 0; i < currentIndex; i++) {
      File file = midiLibrary.get(i);
      SongInfo songInfo = songInfoMap.get(file.getName());
      songInfoList.add(new OnSongInfo(songInfo, i));
    }
    return songInfoList;
  }

  private void initializeDirectories() {
    String midiLibraryPath = getMidiLibraryPath();
    File midiLibraryFile = new File(midiLibraryPath);
    midiLibraryFile.mkdirs();
    System.out.println("MidiLibraryManager.initializeDirectories: midiLibraryPath=" + midiLibraryPath);
    String songInfoPath = getSongInfoPath();
    File songInfoFile = new File(songInfoPath);
    songInfoFile.getParentFile().mkdirs();
    System.out.println("MidiLibraryManager.initializeDirectories: songInfoPath=" + songInfoPath);
  }

  private SongInfo realizeSongInfo(File midiFile) {
    String fileName = midiFile.getName();
    SongInfo songInfo = songInfoMap.get(fileName);
    if (songInfo == null || songInfo.getTimeLastModified() != midiFile.lastModified()) {
      songInfo = songInfoFactory.getSongInfo(midiFile);
      songInfoMap.put(fileName, songInfo);
      savePending++;
    }
    return songInfo;
  }

  private MidiLibrary refreshMidiLibrary() {
    currentIndex = 0;
    String path = getMidiLibraryPath();
    midiLibrary = new MidiLibrary(path);
    songInfoFactory = new SongInfoFactory(midiLibrary);
    setCallbackTimeout();
    publish(new OnMidiLibraryRefresh(midiLibrary));
    if (selectedIndex == -1) {
      if (mostRecentImport == null) {
        selectedIndex = random.nextInt(Math.min(midiLibrary.size(), 100));
      }
    }
    return midiLibrary;
  }

  private void saveCache() {
    FileContents fileContents = new FileContents();
    fileContents.songInfoMap = songInfoMap;
    JsonUtilities.toJsonFile(getSongInfoPath(), fileContents);
    savePending = 0;
  }

  private void selectSong(int songIndex) {
    if (songIndex < 0 || songIndex >= midiLibrary.size()) {
      throw new IndexOutOfBoundsException();
    }
    Song song = midiLibrary.readSong(songIndex);
    String fileName = song.getFile().getName();
    SongInfo songInfo = songInfoMap.get(fileName);
    CurrentSong currentSong = new CurrentSong(song, songInfo, songIndex);
    publish(new OnSongSelected(currentSong));
  }

  private void setCallbackTimeout() {
    setTimeoutMillis(currentIndex < midiLibrary.size() ? 10 : 0);
  }

}
