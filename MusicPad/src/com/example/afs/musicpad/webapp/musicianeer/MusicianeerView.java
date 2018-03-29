// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.task.ControllerTask;

public class MusicianeerView extends ShadowDomBuilder {

  public MusicianeerView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#musicianeer", ".tab", ".selected-tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("Musicianeer Application"))) //
            .add(div("#musicianeer-list", ".list") //
                .addClickHandler() //
                .add(div() //
                    .add(div("#item-1") //
                        .add(text("Item 1"))) //
                    .add(div("#item-2") //
                        .add(text("Item 2"))) //
                    .add(div("#item-3") //
                        .add(text("Item 3"))))) //
            .add(div(".controls") //
                .add(div("#musicianeer-1")//
                    .addClickHandler() //
                    .add(text("Musicianeer 1"))) //
                .add(div("#musicianeer-2")//
                    .addClickHandler() //
                    .add(text("Musicianeer 2"))) //
                .add(div("#musicianeer-3") //
                    .addClickHandler() //
                    .add(text("Musicianeer 3"))))) //
        .add(div(".right") //
            .add(div("#musicianeer-details", ".details")))); // createSongDetails
  }

}
