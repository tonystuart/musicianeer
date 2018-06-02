// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.analyzer.Names;
import com.example.afs.musicpad.html.CheckBox;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.NumberInput;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.webapp.musicianeer.ChannelInfoFactory.ChannelInfo;
import com.example.afs.musicpad.webapp.musicianeer.MidiHandle.Type;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    OFF, GREEN, YELLOW, RED, BLUE
  }

  public class MidiHandleSelect extends Select {

    public MidiHandleSelect(String id, Iterable<MidiHandle> midiHandles, Type type, int selectedIndex) {
      super("#" + id);
      addInputHandler();
      required();
      add(option("N/A", MidiHandle.MIDI_HANDLE_NA)); // default if no others are selected
      for (MidiHandle midiHandle : midiHandles) {
        if (midiHandle.getType() == type) {
          int index = midiHandle.getIndex();
          add(option(midiHandle.getName(), index, index == selectedIndex));
        }
      }
    }

    public void replace(Parent parent) {
      replaceElement(parent, getElementById(getId()), this, false);
    }
  }

  public MusicianeerView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#musicianeer", ".move-target") //
        .add(div("#song") //
            .add(div("#song-table-wrapper") //
                .add(table("#song-table") //
                    .add(thead() //
                        .add(th() //
                            .add(text("Song"))) //
                        .add(th() //
                            .add(text("Duration"))) //
                        .add(th() //
                            .add(text("Channels"))) //
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
                            .add(text("Channel"))) //
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
            .add(div("#staff-scroller")//
                .setProperty("onmousedown", "musicianeer.onStaffMouseDown(event);") //
                .setProperty("onscroll", "musicianeer.onStaffScroll(event);"))) //
        .add(div("#controls") //
            .add(clicker("stop", "STOP")) //
            .add(clicker("play", "PLAY")) //
            .add(clicker("import", "IMPORT")) //
            .add(slider("Tempo", "tempo", Transport.DEFAULT_PERCENT_TEMPO, 0, 100)) //
            .add(slider("Channel Volume", "channel-volume", MusicianeerController.DEFAULT_VELOCITY, 0, 127)) //
            .add(slider("Background Volume", "background-volume", Transport.DEFAULT_PERCENT_VELOCITY, 0, 100)) //
            .add(slider("Master Volume", "master-volume", Transport.DEFAULT_PERCENT_GAIN, 0, 100)) //
            .add(div(".name-value") //
                .add(text("Transposition:&nbsp;")) //
                .add(numberInput("#transposition") //
                    .addInputHandler())) //
            .add(div("#midi-input-container", ".name-value") //
                .add(text("Input:&nbsp;")) //
                .add(div("#midi-input"))) //
            .add(div("#midi-prompter-container", ".name-value") //
                .add(text("Output:&nbsp;")) //
                .add(div("#midi-prompter"))) //
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
        .add(div("#import-dialog", ".hidden") //
            .add(form("#import-form") //
                .action("/FileUploadServlet") //
                .method("post") //
                .enctype("multipart/form-data") //
                .target("import-response") //
                .add(div("#import-heading") //
                    .add(text("Import"))) //
                .add(div("#import-instructions") //
                    .add(text("Select MIDI (.mid) and Karaoke (.kar) files for upload"))) //
                .add(div("#import-file-input") //
                    .add(file() //
                        .multiple() //
                        .accept(".mid,.midi,.kar") //
                        .setName("files") //
                        .required()) //
                    .add(submit("#import-submit") //
                        .setValue("Import") //
                        .addClickHandler())) //
                .add(iframe("#import-response") //
                    .name("import-response")) //
                .add(div("#import-footer") //
                    .add(button("#import-cancel") //
                        .setValue("Close") //
                        .addClickHandler()) //
        ))) //
        .setProperty("onmouseup", "musicianeer.onStaffMouseUp(event);"));
  }

  public void renderKeyPressed(int midiNote) {
    addClass("midi-note-" + midiNote, "key-pressed");
  }

  public void renderKeyReleased(int midiNote) {
    removeClass("midi-note-" + midiNote, "key-pressed");
  }

  public void renderMidiHandles(Iterable<MidiHandle> midiHandles, int inputDeviceIndex, int prompterDeviceIndex) {
    MidiHandleSelect input = new MidiHandleSelect("midi-input", midiHandles, Type.INPUT, inputDeviceIndex);
    input.replace(getElementById("midi-input-container"));
    MidiHandleSelect prompter = new MidiHandleSelect("midi-prompter", midiHandles, Type.PROMPTER, prompterDeviceIndex);
    prompter.replace(getElementById("midi-prompter-container"));
  }

  public void renderSongDetails(CurrentSong currentSong) {
    SongInfo songInfo = currentSong.getSongInfo();
    Parent channelTable = getElementById("channel-table");
    Parent channelBody = getElementById("channel-body");
    replaceElement(channelTable, channelBody, createChannelBody(currentSong.getSong()));
    NumberInput transposition = getElementById("transposition");
    setProperty(transposition, "value", songInfo.getEasyTransposition());
  }

  public void renderSongInfo(SongInfo songInfo, int songIndex) {
    Parent songBody = getElementById("song-body");
    Parent newSongRow = createSongTableRow(songInfo, songIndex);
    TableRow oldSongRow = getElementById("song-index-" + songIndex);
    if (oldSongRow == null) {
      appendChild(songBody, newSongRow);
    } else {
      replaceChildren(songBody, newSongRow);
    }
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

  public void setChannelVolume(int channelVolume) {
    setProperty(getElementById("channel-volume"), "value", channelVolume);
  }

  public void setImportDialogVisibility(boolean isVisible) {
    if (isVisible) {
      removeClass("import-dialog", "hidden");
    } else {
      addClass("import-dialog", "hidden");
    }
  }

  public void setLedState(int midiNote, LedState ledState) {
    switch (ledState) {
    case GREEN:
      removeClass("midi-led-" + midiNote, "led-yellow");
      removeClass("midi-led-" + midiNote, "led-red");
      removeClass("midi-led-" + midiNote, "led-blue");
      addClass("midi-led-" + midiNote, "led-green");
      break;
    case YELLOW:
      removeClass("midi-led-" + midiNote, "led-green");
      removeClass("midi-led-" + midiNote, "led-red");
      removeClass("midi-led-" + midiNote, "led-blue");
      addClass("midi-led-" + midiNote, "led-yellow");
      break;
    case RED:
      removeClass("midi-led-" + midiNote, "led-green");
      removeClass("midi-led-" + midiNote, "led-yellow");
      removeClass("midi-led-" + midiNote, "led-blue");
      addClass("midi-led-" + midiNote, "led-red");
      break;
    case BLUE:
      removeClass("midi-led-" + midiNote, "led-green");
      removeClass("midi-led-" + midiNote, "led-yellow");
      removeClass("midi-led-" + midiNote, "led-red");
      addClass("midi-led-" + midiNote, "led-blue");
      break;
    case OFF:
      removeClass("midi-led-" + midiNote, "led-green");
      removeClass("midi-led-" + midiNote, "led-yellow");
      removeClass("midi-led-" + midiNote, "led-red");
      removeClass("midi-led-" + midiNote, "led-blue");
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

  public void setMute(int channel, boolean isMute) {
    CheckBox checkBox = getElementById("channel-mute-" + channel);
    onSetProperty(checkBox, "checked", isMute);
  }

  public void setPercentMasterGain(int percentMasterGain) {
    setProperty(getElementById("master-volume"), "value", percentMasterGain);
  }

  public void setPercentTempo(int percentTempo) {
    setProperty(getElementById("tempo"), "value", percentTempo);
  }

  public void setPercentVelocity(int percentVelocity) {
    setProperty(getElementById("background-volume"), "value", percentVelocity);
  }

  public void setProgram(int program) {
    Element instrumentSelector = getElementById("instrument");
    onSetProperty(instrumentSelector, "value", program);
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

  private Parent blackKey(int midiNote) {
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
    String programNames = channelInfo.getProgramNames();
    return row("#channel-index-" + channel) //
        .add(td() //
            .add(text((channel + 1) + ". " + programNames)) //
            .setProperty("title", programNames)) //
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
    Select select = new Select("#instrument");
    select.addInputHandler();
    select.required();
    select.add(option("Default", OnProgramOverride.DEFAULT));
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

  private Parent createSongTableRow(SongInfo songInfo, int songIndex) {
    String title = songInfo.getTitle();
    TableRow row = new TableRow("#song-index-" + songIndex);
    row //
        .add(td() //
            .add(text((songIndex + 1) + ". " + title)) //
            .setProperty("title", title)) //
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

  private Parent key(int midiNote, String className) {
    return (Parent) div("#midi-note-" + midiNote, "." + className) //
        .add(div("#midi-led-" + midiNote, "." + className + "-led")) //
        .addMouseDownHandler() //
        .addMouseOutHandler() //
        .addMouseOverHandler(); //
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

  private Division slider(String label, String id, int value, int minimum, int maximum) {
    Division div = new Division(".slider");
    div.add(text(label + ":"));
    Range slider = new Range("#" + id);
    slider.addInputHandler();
    slider.setValue(value);
    slider.setMinimum(minimum);
    slider.setMaximum(maximum);
    slider.setStep(1);
    div.add(slider);
    return div;
  }

  private Parent whiteKey(int midiNote) {
    return key(midiNote, "white-key") //
        .add(div(".note-label") //
            .add(div(".note-center") //
                .add(div(".note-legend") //
                    .add(text(Character.toString(KeyMap.toLegend(midiNote)))))) //
            .add(div(".note-center") //
                .add(div(".note-name") //
                    .add(text(Names.formatNoteName(midiNote)))))); //
  }

}
