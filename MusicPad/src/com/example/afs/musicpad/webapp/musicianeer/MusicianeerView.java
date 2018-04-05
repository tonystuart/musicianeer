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

  public MusicianeerView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#musicianeer", ".tab", ".selected-tab") //
        .add(div(".packed-column") //)
            .add(div(".title") //
                .add(text("Musicianeer"))) //
            .add(div(".song") //
                .add(clicker("left-group", "<<")) //
                .add(clicker("left-single", "<")) //
                .add(clicker("stop", "STOP")) //
                .add(div("#song-title") //
                    .add(text("Title of current song"))) //
                .add(clicker("play", "PLAY")) //
                .add(clicker("right-single", ">")) //
                .add(clicker("right-group", ">>"))) //
            .add(div(".keyboard") //
                .add(div(".key-parent") //
                    .add(whiteKey(57)) //
                    .add(blackKey(58))) //
                .add(div(".key-parent") //
                    .add(whiteKey(59))) //
                .add(div(".key-parent") //
                    .add(whiteKey(60)) //
                    .add(blackKey(61))) //
                .add(div(".key-parent") //
                    .add(whiteKey(62)) //
                    .add(blackKey(63))) //
                .add(div(".key-parent") //
                    .add(whiteKey(64))) //
                .add(div(".key-parent") //
                    .add(whiteKey(65))//
                    .add(blackKey(66))) //
                .add(div(".key-parent") //
                    .add(whiteKey(67))//
                    .add(blackKey(68))) //
                .add(div(".key-parent") //
                    .add(whiteKey(69)) //
                    .add(blackKey(70))) //
                .add(div(".key-parent") //
                    .add(whiteKey(71))) //
                .add(div(".key-parent") //
                    .add(whiteKey(72))//
                    .add(blackKey(73))) //
                .add(div(".key-parent") //
                    .add(whiteKey(74))//
                    .add(blackKey(75))) //
                .add(div(".key-parent") //
                    .add(whiteKey(76)))) //
            .add(div(".sliders") //
                .add(slider("tempo", 100)) //
                .add(slider("instrument", Midi.MAX_VALUE)) //
                .add(slider("volume", 100))) //
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

  public void setAlternative(String id) {
    Radio radio = getElementById(id);
    if (radio != null) {
      setProperty(radio, "checked", 1);
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
    key.add(div("." + className + "-led"));
    return key;
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
