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
import com.example.afs.musicpad.html.NumberInput;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.PercentRange;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.util.FileUtilities;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    OFF, GREEN, YELLOW, RED, BLUE
  }

  public MusicianeerView(ControllerTask controllerTask, MidiLibrary midiLibrary) {
    super(controllerTask);
    add(div("#musicianeer") //
        .add(div("#song") //
            .add(div("#song-table-wrapper") //
                .add(table("#song-table") //
                    .add(thead("#song-head") //
                        .add(th("#song-number-column") //
                            .add(text("Song"))) //
                        .add(th("#song-title-column") //
                            .add(text("Title"))) //
                        .add(th("#song-duration-column") //
                            .add(text("Duration"))) //
                        .add(th("#song-parts-column") //
                            .add(text("Parts"))) //
                        .add(th("#song-beats-per-minute-column") //
                            .add(text("Beats per Minute"))) //
                        .add(th("#song-time-signature-column") //
                            .add(text("Time Signature"))) //
                        .add(th("#song-presumed-key-column") //
                            .add(text("Presumed Key"))) //
                        .add(th("#song-easy-transposition-column") //
                            .add(text("EZ Transpose"))) //
                        .add(th("#song-complexity-column") //
                            .add(text("Complexity"))) //
                    ) //
                    .add(createSongBody(midiLibrary)) //
                    .addClickHandler())) //
            .add(div("#channel-table-wrapper") //
                .add(table("#channel-table") //
                    .add(thead("#channel-head") //
                        .add(th() //
                            .add(text("Channel"))) //
                        .add(th() //
                            .add(text("Instrument"))) //
                        .add(th() //
                            .add(text("Mute"))) //
                        .add(th() //
                            .add(text("Solo"))) //
                        .add(th() //
                            .add(text("Measures"))) //
                        .add(th() //
                            .add(text("Melody"))) //
                        .add(th() //
                            .add(text("Occupancy"))) //
                        .add(th() //
                            .add(text("Concurrency"))) //
                        .add(th() //
                            .add(text("Total Notes")))) //
                    .add(tbody("#channel-body"))))) //
        .add(div("#controls") //
            .add(clicker("stop", "STOP")) //
            .add(clicker("play", "PLAY")) //
            .add(percentSlider("tempo", Transport.DEFAULT_PERCENT_TEMPO)) //
            .add(percentSlider("volume", Transport.DEFAULT_PERCENT_GAIN)) //
            .add(div(".name-value") //
                .add(text("Transposition:&nbsp;")) //
                .add(numberInput("#transposition"))) //
            .add(div(".name-value") //
                .add(text("Instrument:&nbsp;")) //
                .add(createInstrumentSelect())) //
            .add(fieldSet() //
                .add(legend() //
                    .add(text("Accompaniment"))) //
                .add(alternative("accompaniment", "Full")) //
                .add(alternative("accompaniment", "Piano")) //
                .add(alternative("accompaniment", "Rhythm")) //
                .add(alternative("accompaniment", "Drums")))) //
        .add(div("#staff-container") //
            .add(div("#staff-cursor")) //
            .add(div("#staff-scroller"))) //
        .add(keyboard()) //
        .addMouseUpHandler()); //
  }

  public void renderSongDetails(CurrentSong currentSong) {
    SongInfo songInfo = new SongInfo(currentSong);
    Parent channelTable = getElementById("channel-table");
    Parent channelBody = getElementById("channel-body");
    replaceElement(channelTable, channelBody, createChannelBody(songInfo));
    Parent songInfoBody = getElementById("song-body");
    int songIndex = currentSong.getIndex();
    Parent songTableRow = getElementById("song-index-" + songIndex);
    replaceElement(songInfoBody, songTableRow, createSongTableRow(currentSong, songInfo));
    NumberInput transposition = getElementById("transposition");
    setProperty(transposition, "value", currentSong.getEasyTransposition());
  }

  public void renderSongList(MidiLibrary midiLibrary) {
    Parent songTable = getElementById("song-table");
    Parent songBody = getElementById("song-body");
    replaceElement(songTable, songBody, createSongBody(midiLibrary));
  }

  public void renderStaff(Song song, int channel, int transposition) {
    Engraver engraver = new Engraver();
    Parent staff = engraver.notate(song, channel, transposition);
    Parent staffScroller = getElementById("staff-scroller");
    replaceChildren(staffScroller, staff, false);
  }

  public void resetMidiNoteLeds() {
    for (int i = Musicianeer.LOWEST_NOTE; i <= Musicianeer.HIGHEST_NOTE; i++) {
      setLedState(i, LedState.OFF);
    }
  }

  public void selectChannel(Song song, int channel, int transposition) {
    selectElement("channel-index-" + channel, "selected-channel");
    renderStaff(song, channel, transposition);
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
    channelBody.addClickHandler();
    for (int channel : songInfo.getActiveChannels()) {
      channelBody.add(row("#channel-index-" + channel) //
          .add(td() //
              .add(text(channel))) //
          .add(td() //
              .add(text(songInfo.getProgramNames(channel)))) //
          .add(td() //
              .add(checkbox("#channel-mute-" + channel))) //
          .add(td() //
              .add(checkbox("#channel-mute-" + channel))) //
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

  private Select createInstrumentSelect() {
    Select select = new Select("#instrument-select");
    select.addInputHandler();
    select.required();
    select.add(option("Default", -1));
    int program = 0;
    for (int category = 0; category < 16; category++) {
      select.add(optionGroup(Instruments.getCategoryName(category)));
      for (int j = 0; j < 8; j++) {
        select.add(option(Instruments.getProgramName(program), program));
        program++;
      }
    }
    return select;
  }

  private Parent createSongBody(MidiLibrary midiLibrary) {
    Parent songBody = tbody("#song-body");
    songBody.addClickHandler();
    int fileCount = midiLibrary.size();
    for (int songIndex = 0; songIndex < fileCount; songIndex++) {
      File midiFile = midiLibrary.get(songIndex);
      String name = FileUtilities.getBaseName(midiFile.getPath());
      songBody.add(row("#song-index-" + songIndex) //
          .add(td() //
              .add(text(songIndex))) //
          .add(td() //
              .add(text(name))));
    }
    return songBody;
  }

  private Parent createSongTableRow(CurrentSong currentSong, SongInfo songInfo) {
    int songIndex = currentSong.getIndex();
    TableRow row = new TableRow("#song-index-" + songIndex);
    row.add(td() //
        .add(text(songIndex))) //
        .add(td() //
            .add(text(currentSong.getSong().getTitle()))) //
        .add(td() //
            .add(text(songInfo.getDuration())))
        .add(td() //
            .add(text(songInfo.getParts())))
        .add(td() //
            .add(text(songInfo.getBeatsPerMinute(0))))
        .add(td() //
            .add(text(songInfo.getTimeSignature())))
        .add(td() //
            .add(text(songInfo.getPredominantKey())))
        .add(td() //
            .add(text(songInfo.getEasyTransposition())))
        .add(td() //
            .add(text(songInfo.getComplexity())));
    return row;

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
    div.add(text(Utils.capitalize(id) + ":&nbsp;"));
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
