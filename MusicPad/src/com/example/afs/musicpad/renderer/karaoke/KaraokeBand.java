// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.io.File;

import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.message.OnKaraokeBandHtml;
import com.example.afs.musicpad.message.OnKaraokeBandHtml.Action;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.task.ServiceTask.Response;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeBand extends ShadowDom implements Response {

  private MessageBroker broker;

  public KaraokeBand(MessageBroker broker) {
    this.broker = broker;
    setRoot(div("#songs", ".content", ".tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("Pick a Song")) //
                .add(div("#song-list-filter", ".hidden"))) //
            .add(div("#song-list") // createSongList
                .addClickHandler()) //
            .add(div(".controls") //
                .add(div("#roulette")//
                    .addClickHandler() //
                    .add(text("Roulette"))) //
                .add(div("#stop")//
                    .addClickHandler() //
                    .add(text("Stop"))) //
                .add(div("#song-select") //
                    .addClickHandler() //
                    .add(text("Select this Song"))))) //
        .add(div(".right") //
            .add(div("#song-details", ".details")))); // createSongDetails
  }

  public void setCurrentSong(Song song, int songIndex) {
    Element songsRight = getElementById("song-details");
    replaceChildren(songsRight, createSongDetails(song));
    Element selectedSong = getElementByClassName("selected-song");
    if (selectedSong != null) {
      removeClass(selectedSong, "selected-song");
    }
    Division songList = getElementById("song-list");
    Division container = songList.getChild(0);
    Element child = container.getChild(songIndex);
    addClass(child, "selected-song");
  }

  public void setSongList(RandomAccessList<File> midiFiles) {
    Division div = createSongList(midiFiles);
    Element songList = getElementById("song-list");
    replaceChildren(songList, div);
  }

  @Override
  protected void onAddClassName(Element element, String className) {
    broker.publish(new OnKaraokeBandHtml(Action.ADD_CLASS, "#" + element.getId(), className.substring(1)));
  }

  @Override
  protected void onRemoveClassName(Element element, String className) {
    broker.publish(new OnKaraokeBandHtml(Action.REMOVE_CLASS, "#" + element.getId(), className.substring(1)));
  }

  @Override
  protected void onReplaceChildren(Parent parent, Element newChild) {
    broker.publish(new OnKaraokeBandHtml(Action.REPLACE_CHILDREN, "#" + parent.getId(), newChild.render()));
  }

  private Element createSongDetails(Song song) {
    return div() //
        .add(nameValue("Title", song.getTitle())) //
        .add(nameValue("Duration", getDuration(song))) //
        .add(nameValue("Parts", song.getActiveChannelCount())) //
        .add(nameValue("Beats per Minute", song.getBeatsPerMinute(0))) //
        .add(nameValue("Time Signature", song.getBeatsPerMeasure(0) + "/" + song.getBeatUnit(0))) //
        .add(nameValue("Predominant Key", getKeyInfo(song))) //
        .add(nameValue("EZ Keyboard Transposition", song.getDistanceToWhiteKeys())) //
        .add(nameValue("Complexity", getComplexity(song))); //
  }

  private Division createSongList(RandomAccessList<File> midiFiles) {
    Division div = new Division();
    int fileCount = midiFiles.size();
    for (int songIndex = 0; songIndex < fileCount; songIndex++) {
      File midiFile = midiFiles.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      div.add(div("#song-index-" + songIndex)//
          .add(text(name)));
    }
    return div;
  }

  private double getComplexity(Song song) {
    double complexity = 0;
    long duration = song.getDuration();
    long measures = duration / song.getTicksPerMeasure(0);
    if (measures > 0) {
      int noteCount = song.getNoteCount();
      complexity = noteCount / measures;
    }
    return complexity;
  }

  private String getDuration(Song song) {
    long tickDuration = song.getDuration();
    long beatDuration = tickDuration / Default.TICKS_PER_BEAT;
    int beatsPerMinute = song.getBeatsPerMinute(0);
    int secondsDuration = (int) ((60 * beatDuration) / beatsPerMinute);
    String duration = String.format("%d:%02d", secondsDuration / 60, secondsDuration % 60);
    return duration;
  }

  private String getKeyInfo(Song song) {
    StringBuilder s = new StringBuilder();
    int[] noteCounts = new int[Midi.SEMITONES_PER_OCTAVE];
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        if (channel != Midi.DRUM) {
          int[] channelNoteCounts = song.getChromaticNoteCounts(channel);
          for (int i = 0; i < noteCounts.length; i++) {
            noteCounts[i] += channelNoteCounts[i];
          }
        }
      }
    }
    KeyScore[] keyScores = KeySignatures.getKeyScores(noteCounts);
    for (int i = 0; i < keyScores.length && s.length() == 0; i++) {
      KeyScore keyScore = keyScores[i];
      int rank = keyScore.getRank();
      if (rank == 1) {
        String key = keyScore.getKey();
        String synopsis = keyScore.getSynopsis();
        int accidentals = keyScore.getAccidentals();
        int triads = keyScore.getTriads();
        int thirds = keyScore.getThirds();
        s.append(String.format("%s (%s) %d / %d / %d", key, synopsis, accidentals, triads, thirds));
      }
    }
    return s.toString();
  }

  private Parent nameValue(String name, Object value) {
    return div(".detail") //
        .add(div(".name") //
            .add(text(name))) //
        .add(div(".value") //
            .add(text(value))); //
  }

}
