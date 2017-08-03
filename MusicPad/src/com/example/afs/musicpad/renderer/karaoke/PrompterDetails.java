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
import com.example.afs.musicpad.html.Element;
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

  private Element createDetailGroup() {
    Division division = new Division(".detail-container");
    int playerIndex = 0;
    for (Integer deviceIndex : devicePlayables.keySet()) {
      division.add(div(".detail", ".player-" + playerIndex) // 
          .add(div(".name", Utils.getPlayerName(playerIndex) + " Volume")) // 
          .add(div(".value") // 
              .add(div(".value-content") // 
                  .add(range(".device-velocity-" + deviceIndex) //  
                      .property("oninput", CommandRenderer.render(DeviceCommand.VELOCITY, deviceIndex))) //
                  .add(label() // 
                      .add(checkbox(".background-mute-" + deviceIndex) //
                          .property("onclick", CommandRenderer.render(DeviceCommand.MUTE_BACKGROUND, deviceIndex, "this.checked ? 1 : 0"))) //
                      .add(text("Mute background"))))));
      playerIndex++;
    }
    division.add(div(".detail") //
        .add(div(".name", "Background Volume")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".velocity") //
                    .property("oninput", CommandRenderer.render(Command.VELOCITY))))));
    division.add(div(".detail") //
        .add(div(".name", "Master Volume")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".gain") //
                    .property("oninput", CommandRenderer.render(Command.GAIN))))));
    division.add(div(".detail") //
        .add(div(".name", "Tempo")) //
        .add(div(".value") //
            .add(div(".value-content") //
                .add(range(".tempo") //
                    .property("oninput", CommandRenderer.render(Command.TEMPO))))));

    return division;
  }

}
