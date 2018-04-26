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

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.PercentRange;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.util.FileUtilities;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    OFF, GREEN, YELLOW, RED, BLUE
  }

  public MusicianeerView(ControllerTask controllerTask, MidiLibrary midiLibrary) {
    super(controllerTask);
    add(div("#musicianeer", ".tab", ".selected-tab") //
        .add(div(".packed-column") //)
            .add(div(".title") //
                .add(text("Musicianeer"))) //
            .add(div("#song") //
                .add(div("#song-list", ".list") //
                    .add(createSongList(midiLibrary)) //
                    .addClickHandler()) //
                .add(div("#song-details", ".details"))) // createSongDetails
            .add(div("#channel-table-container", ".details") //
                .add(table("#channel-table") //
                    .add(thead("#channel-head") //
                        .add(td() //
                            .add(text("Channel"))) //
                        .add(td() //
                            .add(text("Instrument"))) //
                        .add(td() //
                            .add(text("Measures"))) //
                        .add(td() //
                            .add(text("Melody"))) //
                        .add(td() //
                            .add(text("Occupancy"))) //
                        .add(td() //
                            .add(text("Concurrency"))) //
                        .add(td() //
                            .add(text("Notes")))) //
                    .add(tbody("#channel-body")))) // createChannelDetails
            .add(div("#transport") //
                .add(clicker("previous-page", "<<")) //
                .add(clicker("previous-song", "<")) //
                .add(clicker("stop", "STOP")) //
                .add(clicker("play", "PLAY")) //
                .add(clicker("next-song", ">")) //
                .add(clicker("next-page", ">>"))) //
            .add(keyboard()) //
            .add(div(".sliders") //
                .add(percentSlider("tempo", Transport.DEFAULT_PERCENT_TEMPO)) //
                .add(midiSlider("instrument", 50)) // TODO: Update instrument on program change
                .add(percentSlider("volume", Transport.DEFAULT_PERCENT_GAIN))) //
            .add(div(".buttons") //
                .add(fieldSet() //
                    .add(alternative("track", "Lead")) //
                    .add(alternative("track", "Follow"))) //
                .add(fieldSet() //
                    .add(alternative("accompaniment", "Full")) //
                    .add(alternative("accompaniment", "Piano")) //
                    .add(alternative("accompaniment", "Rhythm")) //
                    .add(alternative("accompaniment", "Drums")) //
                    .add(alternative("accompaniment", "Solo"))))) //
        .addMouseUpHandler()); //
  }

  public void renderSongDetails(SongInfo songInfo) {
    Parent songDetails = getElementById("song-details");
    replaceChildren(songDetails, createSongDetails(songInfo));
    Parent channelTable = getElementById("channel-table");
    Parent channelBody = getElementById("channel-body");
    replaceElement(channelTable, channelBody, createChannelBody(songInfo));
  }

  public void renderSongList(MidiLibrary midiLibrary) {
    Division div = createSongList(midiLibrary);
    Parent songListParent = getElementById("song-list");
    replaceChildren(songListParent, div);
  }

  public void resetMidiNoteLeds() {
    for (int i = Musicianeer.LOWEST_NOTE; i <= Musicianeer.HIGHEST_NOTE; i++) {
      setLedState(i, LedState.OFF);
    }
  }

  public void selectChannel(SongInfo songInfo, int channelIndex) {
    selectElement("channel-index-" + channelIndex, "selected-channel");
  }

  public void selectSong(int songIndex) {
    selectElement("song-index-" + songIndex, "selected-song");
  }

  public void setAlternative(String id) {
    Radio radio = getElementById(id);
    if (radio != null) {
      setProperty(radio, "checked", 1);
    }
  }

  public void setLedState(int midiNote, LedState ledState) {
    switch (ledState) {
    case GREEN:
      removeClass("midi-note-led-" + midiNote, "led-yellow");
      removeClass("midi-note-led-" + midiNote, "led-red");
      removeClass("midi-note-led-" + midiNote, "led-blue");
      addClass("midi-note-led-" + midiNote, "led-green");
      break;
    case YELLOW:
      removeClass("midi-note-led-" + midiNote, "led-green");
      removeClass("midi-note-led-" + midiNote, "led-red");
      removeClass("midi-note-led-" + midiNote, "led-blue");
      addClass("midi-note-led-" + midiNote, "led-yellow");
      break;
    case RED:
      removeClass("midi-note-led-" + midiNote, "led-green");
      removeClass("midi-note-led-" + midiNote, "led-yellow");
      removeClass("midi-note-led-" + midiNote, "led-blue");
      addClass("midi-note-led-" + midiNote, "led-red");
      break;
    case BLUE:
      removeClass("midi-note-led-" + midiNote, "led-green");
      removeClass("midi-note-led-" + midiNote, "led-yellow");
      removeClass("midi-note-led-" + midiNote, "led-red");
      addClass("midi-note-led-" + midiNote, "led-blue");
      break;
    case OFF:
      removeClass("midi-note-led-" + midiNote, "led-green");
      removeClass("midi-note-led-" + midiNote, "led-yellow");
      removeClass("midi-note-led-" + midiNote, "led-red");
      removeClass("midi-note-led-" + midiNote, "led-blue");
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  private Parent alternative(String name, String legend) {
    return label() // 
        .add(radio("#" + legend.toLowerCase()) //
            .addCheckHandler() //
            .setName(name) //
            .addClickHandler()) //
        .add(text(legend));
  }

  private Division blackKey(int midiNote) {
    return key(midiNote, "black-key");
  }

  private Element clicker(String id, String legend) {
    return button("#" + id) //
        .setValue(legend) //
        .addClickHandler();
  }

  private Parent createChannelBody(SongInfo songInfo) {
    Parent channelBody = tbody("#channel-body");
    for (int channel : songInfo.getActiveChannels()) {
      channelBody.add(row("#channel-index-" + channel) //
          .add(td() //
              .add(text(channel))) //
          .add(td() //
              .add(text(songInfo.getProgramNames(channel)))) //
          .add(td() //
              .add(text(songInfo.getPercentMeasuresPlayed(channel) + "%"))) //
          .add(td() //
              .add(text(songInfo.getPercentMelody(channel) + "%"))) //
          .add(td() //
              .add(text(songInfo.getOccupancy(channel) + "%"))) //
          .add(td() //
              .add(text(songInfo.getConcurrency(channel) + "%"))) //
          .add(td() //
              .add(text(songInfo.getUniqueSounds(channel)))));
    }
    return channelBody;
  }

  private Element createSongDetails(SongInfo songInfo) {
    return div() //
        .add(nameValue("Title", songInfo.getTitle())) //
        .add(nameValue("Duration", songInfo.getDuration())) //
        .add(nameValue("Parts", songInfo.getParts())) //
        .add(nameValue("Beats per Minute", songInfo.getBeatsPerMinute(0))) //
        .add(nameValue("Time Signature", songInfo.getTimeSignature())) //
        .add(nameValue("Predominant Key", songInfo.getPredominantKey())) //
        .add(nameValue("EZ Keyboard Transposition", songInfo.getDistanceToWhiteKeys())) //
        .add(nameValue("Current Transposition", songInfo.getTransposition())) //
        .add(nameValue("Complexity", songInfo.getComplexity())); //
  }

  private Division createSongList(MidiLibrary midiLibrary) {
    Division div = div();
    int fileCount = midiLibrary.size();
    for (int songIndex = 0; songIndex < fileCount; songIndex++) {
      File midiFile = midiLibrary.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      div.add(div("#song-index-" + songIndex)//
          .add(text(name)));
    }
    return div;
  }

  private Division key(int midiNote, String className) {
    Division key = div("#midi-note-" + midiNote, "." + className);
    key.addMouseDownHandler();
    key.addMouseOutHandler();
    key.addMouseOverHandler();
    key.add(div("#midi-note-led-" + midiNote, "." + className + "-led"));
    return key;
  }

  private Parent keyboard() {
    Division keyParent = null;
    Division keyboard = div(".keyboard");
    int lowestNote = Keyboard.roundToNatural(Musicianeer.LOWEST_NOTE);
    int highestNote = Keyboard.roundToNatural(Musicianeer.HIGHEST_NOTE);
    for (int midiNote = lowestNote; midiNote <= highestNote; midiNote++) {
      if (Keyboard.isNatural(midiNote)) {
        keyParent = div(".key-parent");
        keyboard.add(keyParent);
        keyParent.add(whiteKey(midiNote));
      } else {
        keyParent.add(blackKey(midiNote));
      }
    }
    return keyboard;
  }

  private Division midiSlider(String id, int value) {
    Division div = new Division(".slider");
    div.add(text(id));
    Range slider = new Range("#" + id);
    slider.setMaximum(Midi.MAX_VALUE);
    slider.addInputHandler();
    slider.setValue(value);
    div.add(slider);
    return div;
  }

  private Division percentSlider(String id, int value) {
    Division div = new Division(".slider");
    div.add(text(id));
    Range slider = new PercentRange("#" + id);
    slider.addInputHandler();
    slider.setValue(value);
    div.add(slider);
    return div;
  }

  private Division whiteKey(int midiNote) {
    return key(midiNote, "white-key");
  }

}
