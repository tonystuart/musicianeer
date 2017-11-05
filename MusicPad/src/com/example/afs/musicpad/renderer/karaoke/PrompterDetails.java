// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.NavigableMap;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.renderer.CommandRenderer;
import com.example.afs.musicpad.util.RandomAccessList;

public class PrompterDetails extends Division {
  private NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables;

  public PrompterDetails(NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables) {
    super(".details");
    this.devicePlayables = devicePlayables;
    appendChild(createDetailGroup());
  }

  private Node createDetailGroup() {
    Division division = new Division(".detail-container");
    for (Integer deviceIndex : devicePlayables.keySet()) {
      division.add(div(".detail", ".device-" + deviceIndex) // 
          .add(div(".name", Utils.getPlayerName(deviceIndex) + " Volume")) // 
          .add(div(".value") // 
              .add(div(".value-content") //
                  .add(range(".device-velocity-" + deviceIndex) //  
                      .property("oninput", CommandRenderer.render(DeviceCommand.VELOCITY, deviceIndex))) //
                  .add(div(".channel-program") //
                      .add(div(".device-channel-" + deviceIndex) //
                          .add(text("Channel"))) //
                      .add(div(".device-program-" + deviceIndex) //
                          .add(text("Instrument")))) //
                  .add(label() // 
                      .add(checkbox(".background-mute-" + deviceIndex) //
                          .property("onclick", CommandRenderer.render(DeviceCommand.MUTE_BACKGROUND, deviceIndex, "this.checked ? 1 : 0"))) //
                      .add(text("&nbsp;Mute background"))))));
    }
    division.add(div(".detail") //
        .add(div(".name", "Background Volume")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".background-velocity") //
                    .property("oninput", CommandRenderer.render(Command.SET_BACKGROUND_VELOCITY))))));
    division.add(div(".detail") //
        .add(div(".name", "Master Volume")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".master-gain") //
                    .property("oninput", CommandRenderer.render(Command.SET_MASTER_GAIN))))));
    division.add(div(".detail") //
        .add(div(".name", "Tempo")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".tempo") //
                    .property("oninput", CommandRenderer.render(Command.SET_TEMPO))))));

    return division;
  }

}
