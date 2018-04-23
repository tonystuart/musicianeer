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
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.PercentRange;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.task.ControllerTask;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    OFF, GREEN, YELLOW, RED, BLUE
  }

  public MusicianeerView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#musicianeer", ".tab", ".selected-tab") //
        .add(div(".packed-column") //)
            .add(div(".title") //
                .add(text("Musicianeer"))) //
            .add(div(".song") //
                .add(clicker("previous-page", "<<")) //
                .add(clicker("previous-song", "<")) //
                .add(clicker("stop", "STOP")) //
                .add(div("#song-title-container")) // initialized by setSongTitle
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

  public void displaySongTitles(MidiLibrary midiLibrary) {
    Division parent = getElementById("song-title-container");
    Select songTitles = new Select("#song-titles");
    songTitles.addInputHandler();
    int index = 0;
    for (File midiFile : midiLibrary) {
      Option option = new Option(midiFile.getName(), index++);
      songTitles.appendChild(option);
    }
    replaceChildren(parent, songTitles);
  }

  public void resetMidiNoteLeds() {
    for (int i = Musicianeer.LOWEST_NOTE; i <= Musicianeer.HIGHEST_NOTE; i++) {
      setLedState(i, LedState.OFF);
    }
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

  public void setSongTitle(int songIndex) {
    Select songTitles = getElementById("song-titles");
    setProperty(songTitles, "value", songIndex);
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
