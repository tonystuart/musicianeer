// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.html.CheckBox;
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
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.webapp.musicianeer.ChannelInfoFactory.ChannelInfo;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    OFF, GREEN, YELLOW, RED, BLUE
  }

  public MusicianeerView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#musicianeer") //
        .add(div("#song") //
            .add(div("#song-table-wrapper") //
                .add(table("#song-table") //
                    .add(thead() //
                        .add(th() //
                            .add(text("Song"))) //
                        .add(th() //
                            .add(text("Duration"))) //
                        .add(th() //
                            .add(text("Parts"))) //
                        .add(th() //
                            .add(text("BPM"))) //
                        .add(th() //
                            .add(text("Time"))) //
                        .add(th() //
                            .add(text("Key"))) //
                        .add(th() //
                            .add(text("Transpose"))) //
                        .add(th() //
                            .add(text("Complexity")))) //
                    .add(tbody("#song-body")) //
                    .addClickHandler())) //
            .add(div("#channel-table-wrapper") //
                .add(table("#channel-table") //
                    .add(thead() //
                        .add(th() //
                            .add(text("Part"))) //
                        .add(th() //
                            .add(text("Mute"))) //
                        .add(th() //
                            .add(text("Solo"))) //
                        .add(th() //
                            .add(text("Melody"))) //
                        .add(th() //
                            .add(text("Measures"))) //
                        .add(th() //
                            .add(text("Occupancy"))) //
                        .add(th() //
                            .add(text("Concurrency"))) //
                        .add(th() //
                            .add(text("Notes")))) //
                    .add(tbody("#channel-body"))))) //
        .add(div("#staff-container") //
            .add(div("#staff-cursor")) //
            .add(div("#staff-scroller"))) //
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
        .add(keyboard()) //
        .addMouseUpHandler()); //
  }

  public void renderSongDetails(CurrentSong currentSong) {
    SongInfo songInfo = currentSong.getSongInfo();
    Parent channelTable = getElementById("channel-table");
    Parent channelBody = getElementById("channel-body");
    replaceElement(channelTable, channelBody, createChannelBody(currentSong.getSong()));
    NumberInput transposition = getElementById("transposition");
    setProperty(transposition, "value", songInfo.getEasyTransposition());
  }

  public void renderSongInfo(SongInfo songInfo) {
    Parent songBody = getElementById("song-body");
    appendChild(songBody, createSongTableRow(songInfo));
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

  public void setMute(int channel, boolean isMute) {
    CheckBox checkBox = getElementById("channel-mute-" + channel);
    onSetProperty(checkBox, "checked", isMute);
  }

  public void setSolo(int channel, boolean isSolo) {
    CheckBox checkBox = getElementById("channel-solo-" + channel);
    onSetProperty(checkBox, "checked", isSolo);
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

  private Parent createChannelBody(Song song) {
    ChannelInfoFactory channelInfoFactory = new ChannelInfoFactory(song);
    Parent channelBody = tbody("#channel-body");
    channelBody.addClickHandler();
    for (int channel : song.getActiveChannels()) {
      ChannelInfo channelInfo = channelInfoFactory.getChannelInfo(channel);
      channelBody.add(createChannelTableRow(channel, channelInfo));
    }
    return channelBody;
  }

  private Parent createChannelTableRow(int channel, ChannelInfo channelInfo) {
    return row("#channel-index-" + channel) //
        .add(td() //
            .add(text((channel + 1) + ". " + channelInfo.getProgramNames()))) //
        .add(td() //
            .add(checkbox("#channel-mute-" + channel) //
                .addCheckHandler())) //
        .add(td() //
            .add(checkbox("#channel-solo-" + channel) //
                .addCheckHandler())) //
        .add(td() //
            .add(text(channelInfo.getPercentMelody() + "%"))) //
        .add(td() //
            .add(text(channelInfo.getPercentMeasuresPlayed() + "%"))) //
        .add(td() //
            .add(text(channelInfo.getOccupancy() + "%"))) //
        .add(td() //
            .add(text(channelInfo.getConcurrency() + "%"))) //
        .add(td() //
            .add(text(channelInfo.getUniqueSounds())));
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

  private Parent createSongTableRow(SongInfo songInfo) {
    int songIndex = songInfo.getSongIndex();
    TableRow row = new TableRow("#song-index-" + songIndex);
    row.add(td() //
        .add(text((songInfo.getSongIndex() + 1) + ". " + songInfo.getTitle()))) //
        .add(td() //
            .add(text(songInfo.getDuration())))
        .add(td() //
            .add(text(songInfo.getActiveChannels().length)))
        .add(td() //
            .add(text(songInfo.getBeatsPerMinute())))
        .add(td() //
            .add(text(songInfo.getTimeSignature())))
        .add(td() //
            .add(text(songInfo.getPredominantKey().replace(" ", "&nbsp;"))))
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

  @SuppressWarnings("unused")
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
