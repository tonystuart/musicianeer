// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.karaoke;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import com.example.afs.musicpad.analyzer.KeyScore;
import com.example.afs.musicpad.analyzer.KeySignatures;
import com.example.afs.musicpad.html.CheckBox;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.player.Sound;
import com.example.afs.musicpad.player.Sounds;
import com.example.afs.musicpad.player.Sounds.SoundCount;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.RandomAccessList;
import com.example.afs.musicpad.util.Value;

public class KaraokeView extends ShadowDom {

  private int songIndex;
  private int deviceIndex;
  private int channelIndex;

  public KaraokeView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#songs", ".tab", ".selected-tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("Pick a Song")) //
                .add(div("#song-list-filter", ".hidden"))) //
            .add(div("#song-list", ".list") // createSongList
                .addClickHandler()) //
            .add(div(".controls") //
                .add(div("#song-roulette")//
                    .addClickHandler() //
                    .add(text("Roulette"))) //
                .add(div("#song-stop")//
                    .addClickHandler() //
                    .add(text("Stop"))) //
                .add(div("#song-next") //
                    .addClickHandler() //
                    .add(text("Select this Song"))))) //
        .add(div(".right") //
            .add(div("#song-details", ".details")))); // createSongDetails
    add(div("#channels", ".tab") //
        .add(div(".left") //
            .add(div("#channel-title", ".title") //
                .add(text("Player: Pick your Part"))) //
            .add(div("#channel-list", ".list") //
                .addClickHandler()) // createChannelList
            .add(div(".controls") // 
                .add(div("#channel-to-song")//
                    .addClickHandler() //
                    .add(text("Back to Songs"))) //
                .add(div("#channel-stop")//
                    .addClickHandler() //
                    .add(text("Stop"))) //
                .add(div("#channel-next") //
                    .addClickHandler() //
                    .add(text("Select this Part"))))) //
        .add(div(".right") //
            .add(div("#channel-details", ".details")))); // createChannelDetails
    add(div("#prompter", ".tab") //
        .add(div(".left") //
            .add(div("#prompter-title", ".title") //
                .add(text("Prompter Title"))) //
            .add(div("#prompter-list", ".list") // renderSong
                .addClickHandler()) //
            .add(div(".controls") //
                .add(div("#prompter-to-song") //
                    .addClickHandler() //
                    .add(text("Back to Songs"))) //
                .add(div("#prompter-stop") //
                    .addClickHandler() //
                    .add(text("Stop"))) //
                .add(div("#prompter-next") //
                    .addClickHandler() //
                    .add(text("Play")))))
        .add(div(".right") //
            .add(div("#prompter-details", ".details")))); // createPrompterDetails
  }

  public int getChannelIndex() {
    return channelIndex;
  }

  public int getDeviceIndex() {
    return deviceIndex;
  }

  public int getSongIndex() {
    return songIndex;
  }

  public void renderChannelList(Song song, int deviceIndex, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    this.deviceIndex = deviceIndex;
    Division channelList = createChannelList(song, deviceChannelAssignments);
    Parent channelListParent = getElementById("channel-list");
    replaceChildren(channelListParent, channelList);
    int defaultChannel = getDefaultChannel(song, deviceChannelAssignments);
    Element selectedChannel = getElementById("channel-index-" + defaultChannel);
    if (selectedChannel != null) {
      selectChannel(song, defaultChannel);
    }
    Parent channels = getElementById("channels");
    swapClassNameByPrefix(channels, "device-", deviceIndex);
    Parent channelTitle = getElementById("channel-title");
    String name = Utils.getPlayerName(deviceIndex);
    replaceChildren(channelTitle, text(name + ": Pick your Part"));
    selectElement("channels", "selected-tab");
  }

  public void renderSong(Song song, NavigableMap<Integer, PlayerDetail> devicePlayerDetail) {
    KaraokeNotator karaokeNotator = new KaraokeNotator(song, devicePlayerDetail);
    Division prompterList = karaokeNotator.createPrompterList();
    Parent prompterListParent = getElementById("prompter-list");
    replaceChildren(prompterListParent, prompterList, false);
    Division prompterDetails = createPrompterDetails(devicePlayerDetail);
    Parent prompterDetailsParent = getElementById("prompter-details");
    replaceChildren(prompterDetailsParent, prompterDetails);
    Parent prompterTitle = getElementById("prompter-title");
    replaceChildren(prompterTitle, text(song.getTitle()));
    selectElement("prompter", "selected-tab");
  }

  public void renderSongDetails(Song song) {
    Parent songsRight = getElementById("song-details");
    replaceChildren(songsRight, createSongDetails(song));
  }

  public void renderSongList(RandomAccessList<File> midiFiles) {
    Division div = createSongList(midiFiles);
    Parent songListParent = getElementById("song-list");
    replaceChildren(songListParent, div);
  }

  public void selectChannel(Song song, int channelIndex) {
    this.channelIndex = channelIndex;
    Element channelDetails = createChannelDetails(song, channelIndex);
    Parent channelDetailsParent = getElementById("channel-details");
    replaceChildren(channelDetailsParent, channelDetails);
    selectElement("channel-index-" + channelIndex, "selected-channel");
  }

  public void selectSong(int songIndex) {
    this.songIndex = songIndex;
    selectElement("song-index-" + songIndex, "selected-song");
  }

  public void selectSongsTab() {
    selectElement("songs", "selected-tab");
  }

  public void setBackgroundMute(int deviceIndex, int value) {
    CheckBox backgroundMute = getElementById("background-mute-" + deviceIndex);
    if (backgroundMute != null) {
      setProperty(backgroundMute, "checked", value);
    }
  }

  public void setBackgroundMute(Integer deviceIndex, Boolean value) {
    setBackgroundMute(deviceIndex, value ? 1 : 0);
  }

  public void setBackgroundVelocity(int value) {
    Range backgroundVelocity = getElementById("background-velocity");
    if (backgroundVelocity != null) {
      setProperty(backgroundVelocity, "value", value);
    }
  }

  public void setDeviceVelocity(int deviceIndex, int value) {
    Range deviceVelocity = getElementById("device-velocity-" + deviceIndex);
    if (deviceVelocity != null) {
      setProperty(deviceVelocity, "value", value);
    }
  }

  public void setMasterGain(int value) {
    Range masterGain = getElementById("master-gain");
    if (masterGain != null) {
      setProperty(masterGain, "value", value);
    }
  }

  public void setTempo(int value) {
    Range tempo = getElementById("tempo");
    if (tempo != null) {
      setProperty(tempo, "value", value);
    }
  }

  public void updateProgram(int deviceIndex, int programIndex) {
    Division division = getElementById("device-program-" + deviceIndex);
    if (division != null) {
      replaceChildren(division, text(Instruments.getProgramName(programIndex)));
    }
  }

  private Element createChannelDetails(Song song, int channel) {
    return div() //
        .add(nameValue("Title", song.getTitle())) //
        .add(nameValue("Instruments", getProgramNames(song, channel)))//
        .add(nameValue("Percent of Measures Played", getPercentMeasuresPlayed(song, channel) + "%"))//
        .add(nameValue("Percent of Time Tracking Melody", song.getPercentMelody(channel) + "%"))//
        .add(nameValue("Percent of Time Playing", song.getOccupancy(channel) + "%"))//
        .add(nameValue("Average Number of Notes Playing at Once", (double) (song.getConcurrency(channel) / 100)))//
        .add(nameValue("Unique Sounds", getUniqueSounds(song, channel)));
  }

  private Division createChannelList(Song song, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    Division div = div();
    for (int channelindex = 0; channelindex < Midi.CHANNELS; channelindex++) {
      int channelNoteCount = song.getChannelNoteCount(channelindex);
      if (channelNoteCount != 0) {
        List<String> programNames = song.getProgramNames(channelindex);
        div.add(div("#channel-index-" + channelindex) //
            .add(text(getChannelText(channelindex, programNames, deviceChannelAssignments))));
      }
    }
    return div;
  }

  private Division createPrompterDetails(NavigableMap<Integer, PlayerDetail> devicePlayables) {
    Division div = div(".detail-container");
    for (Entry<Integer, PlayerDetail> entry : devicePlayables.entrySet()) {
      int deviceIndex = entry.getKey();
      int channelIndex = entry.getValue().getChannelIndex();
      int programIndex = entry.getValue().getProgramIndex();
      div.add(div(".detail", ".device-" + deviceIndex) // 
          .add(div(".name") //
              .add(text(Utils.getPlayerName(deviceIndex) + " Volume"))) //
          .add(div(".value") // 
              .add(div(".value-content") //
                  .add(range("#device-velocity-" + deviceIndex) //  
                      .addInputHandler()) //
                  .add(div(".channel-program") //
                      .add(div(".device-channel-" + deviceIndex) //
                          .add(text("Channel " + Value.toNumber(channelIndex) + ":&nbsp;"))) //
                      .add(div("#device-program-" + deviceIndex) //
                          .add(text(Instruments.getProgramName(programIndex))))) //
                  .add(label() // 
                      .add(checkbox("#background-mute-" + deviceIndex) //
                          .addCheckHandler()) //
                      .add(text("&nbsp;Mute background"))))));
    }
    div.add(div(".detail") //
        .add(div(".name") //
            .add(text("Background Volume"))) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range("#background-velocity") //
                    .addInputHandler()))));
    div.add(div(".detail") //
        .add(div(".name") //
            .add(text("Master Volume"))) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range("#master-gain") //
                    .addInputHandler()))));
    div.add(div(".detail") //
        .add(div(".name") //
            .add(text("Tempo"))) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range("#tempo") //
                    .addInputHandler()))));

    return div;
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
        .add(nameValue("Current Transposition", song.getTransposition())) //
        .add(nameValue("Complexity", getComplexity(song))); //
  }

  private Division createSongList(RandomAccessList<File> midiFiles) {
    Division div = div();
    int fileCount = midiFiles.size();
    for (int songIndex = 0; songIndex < fileCount; songIndex++) {
      File midiFile = midiFiles.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      div.add(div("#song-index-" + songIndex)//
          .add(text(name)));
    }
    return div;
  }

  private String getChannelText(int channel, List<String> programNames, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    int index = 0;
    int count = 0;
    StringBuilder s = new StringBuilder();
    s.append("Channel " + Value.toNumber(channel) + ": ");
    s.append(programNames.get(0));
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        if (count++ == 0) {
          s.append(" (");
        } else {
          s.append(", ");
        }
        s.append(Utils.getPlayerName(index));
      }
      index++;
    }
    if (count > 0) {
      s.append(")");
    }
    return s.toString();
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

  private int getDefaultChannel(Song song, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    int firstChannel = -1;
    for (int channel : song.getActiveChannels()) {
      if (isFree(channel, deviceChannelAssignments)) {
        return channel;
      }
      if (firstChannel == -1) {
        firstChannel = channel;
      }
    }
    return firstChannel;
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

  private int getPercentMeasuresPlayed(Song song, int channel) {
    Set<Integer> measures = new HashSet<>();
    ChannelNotes notes = new ChannelNotes(song.getNotes(), channel);
    for (Note note : notes) {
      measures.add(note.getMeasure());
    }
    int totalMeasures = (int) (song.getDuration() / song.getTicksPerMeasure(0));
    int percentMeasuresPlayed = 0;
    if (totalMeasures != 0) {
      percentMeasuresPlayed = (100 * measures.size()) / totalMeasures;
    }
    return percentMeasuresPlayed;
  }

  private String getProgramNames(Song song, int channel) {
    StringBuilder s = new StringBuilder();
    for (String programName : song.getProgramNames(channel)) {
      if (s.length() > 0) {
        s.append(", ");
      }
      s.append(programName);
    }
    return s.toString();
  }

  private int getUniqueSounds(Song song, int channel) {
    ChannelNotes notes = new ChannelNotes(song.getNotes(), channel);
    Sounds sounds = new Sounds(OutputType.TICK, notes);
    Map<Sound, SoundCount> uniqueSoundCounts = sounds.getUniqueSoundCounts();
    return uniqueSoundCounts.size();
  }

  private boolean isFree(int channel, NavigableMap<Integer, Integer> deviceChannelAssignments) {
    for (Entry<Integer, Integer> entry : deviceChannelAssignments.entrySet()) {
      if (entry.getValue() == channel) {
        return false;
      }
    }
    return true;
  }

}
