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
            .add(div("#zoom-container") //
                .add(range("#zoom-slider") //
                    .addInputHandler()) //
                .add(div("#zoom-label") //
                    .add(text("Zoom")))) //
            .add(div("#prompter-container") //
                .add(div("#prompter-cursor")) //
                .add(div("#staff-scroller"))))); //
  }

  public void renderSong(Song song, NavigableMap<Integer, PlayerDetail> devicePlayerDetail) {
    StaffNotator staffNotator = new StaffNotator(song, devicePlayerDetail);
    Division staffList = staffNotator.createStaffList();
    Parent prompterListParent = getElementById("staff-scroller");
    replaceChildren(prompterListParent, staffList, false);
    Parent prompterTitle = getElementById("prompter-title");
    replaceChildren(prompterTitle, text(song.getTitle()));
    zoom(50);
    selectElement("prompter", "selected-tab");
  }

  public void zoom(int percent) {
    if (percent > 0) {
      double scale = percent / 100d;
      double multiplier = 1 / scale;
      double padding = multiplier * 50;
      double extra = 7 * multiplier;
      StringBuilder s = new StringBuilder();
      s.append(String.format("padding-left: calc(%.2f%% + %.2fpx); ", padding, extra));
      s.append(String.format("padding-right: calc(%.2f%% - %.2fpx); ", padding, extra));
      s.append(String.format("transform: scale(%.2f); ", scale));
      System.out.println("setZoomStyle: style=" + s);
      setProperty(getElementById("staff-list"), "style", s);
      setProperty(getElementById("zoom-slider"), "value", percent);
    }
  }

}
