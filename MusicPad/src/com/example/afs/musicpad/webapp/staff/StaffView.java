// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.staff;

import java.util.NavigableMap;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.player.PlayerDetail;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.ControllerTask;

public class StaffView extends ShadowDomBuilder {

  public StaffView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#prompter", ".tab") //
        .add(div(".left") //
            .add(div("#prompter-title", ".title") //
                .add(text("Prompter Title"))) //
            .add(div("#prompter-list", ".list") // renderSong
                .addClickHandler()))); //
  }

  public void renderSong(Song song, NavigableMap<Integer, PlayerDetail> devicePlayerDetail) {
    StaffNotator staffNotator = new StaffNotator(song, devicePlayerDetail);
    Division prompter = staffNotator.createPrompter();
    Parent prompterListParent = getElementById("prompter-list");
    replaceChildren(prompterListParent, prompter, false);
    Parent prompterTitle = getElementById("prompter-title");
    replaceChildren(prompterTitle, text(song.getTitle()));
    selectElement("prompter", "selected-tab");
  }

}
