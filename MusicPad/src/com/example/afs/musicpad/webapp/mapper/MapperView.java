// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.mapper;

import java.util.Map.Entry;
import java.util.NavigableMap;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.task.ControllerTask;

public class MapperView extends ShadowDom {

  public MapperView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#mapper", ".tab", ".selected-tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("Mapper Application"))) //
            .add(div("#device-list", ".list") // renderDeviceList
                .addClickHandler()) //
            .add(div(".controls") //
                .add(div("#mapper-1")//
                    .addClickHandler() //
                    .add(text("Mapper 1"))) //
                .add(div("#mapper-2")//
                    .addClickHandler() //
                    .add(text("Mapper 2"))) //
                .add(div("#mapper-3") //
                    .addClickHandler() //
                    .add(text("Mapper 3"))))) //
        .add(div(".right") //
            .add(div("#mapper-details", ".details")))); // 
  }

  public void renderDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = createDeviceList(deviceControllers);
    Parent songListParent = getElementById("device-list");
    replaceChildren(songListParent, div);
  }

  private Division createDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = div();
    for (Entry<Integer, Controller> entry : deviceControllers.entrySet()) {
      int deviceIndex = entry.getKey();
      Controller controller = entry.getValue();
      div.add(div("#device-" + deviceIndex) //
          .add(text(controller.getDeviceName())));
    }
    return div;
  }

}
