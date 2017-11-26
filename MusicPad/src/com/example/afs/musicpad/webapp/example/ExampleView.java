// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.example;

import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.task.ControllerTask;

public class ExampleView extends ShadowDom {

  public ExampleView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#example", ".tab", ".selected-tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("Example Application"))) //
            .add(div("#example-list") // createSongList
                .addClickHandler()) //
            .add(div(".controls") //
                .add(div("#example-1")//
                    .addClickHandler() //
                    .add(text("Example 1"))) //
                .add(div("#example-2")//
                    .addClickHandler() //
                    .add(text("Example 2"))) //
                .add(div("#example-3") //
                    .addClickHandler() //
                    .add(text("Example 3"))))) //
        .add(div(".right") //
            .add(div("#example-details", ".details")))); // createSongDetails
  }

}
