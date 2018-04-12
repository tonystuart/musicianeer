// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.task.ControllerTask;

public class MusicianeerView extends ShadowDomBuilder {

  public enum LedState {
    GREEN, OFF, RED
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
                .add(div("#song-title") //
                    .add(text("Title of current song"))) //
                .add(clicker("play", "PLAY")) //
                .add(clicker("next-song", ">")) //
                .add(clicker("next-page", ">>"))) //
            .add(keyboard()) //
            .add(div(".sliders") //
                .add(slider("tempo", MusicianeerController.DEFAULT_PERCENT_TEMPO)) //
                .add(slider("instrument", Midi.MAX_VALUE)) //
                .add(slider("volume", MusicianeerController.DEFAULT_PERCENT_GAIN))) //
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
      removeClass("midi-note-led-" + midiNote, "led-red");
      addClass("midi-note-led-" + midiNote, "led-green");
      break;
    case OFF:
      removeClass("midi-note-led-" + midiNote, "led-red");
      removeClass("midi-note-led-" + midiNote, "led-green");
      break;
    case RED:
      addClass("midi-note-led-" + midiNote, "led-red");
      removeClass("midi-note-led-" + midiNote, "led-green");
      break;
    default:
      break;

    }
  }

  public void setSongTitle(String titleText) {
    Division songTitle = getElementById("song-title");
    replaceChildren(songTitle, text(titleText));
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

  private Division slider(String id, int maximum) {
    Division div = new Division(".slider");
    div.add(text(id));
    Range slider = new Range("#" + id);
    slider.setMaximum(maximum);
    slider.addInputHandler();
    div.add(slider);
    return div;
  }

  private Division whiteKey(int midiNote) {
    return key(midiNote, "white-key");
  }

}
