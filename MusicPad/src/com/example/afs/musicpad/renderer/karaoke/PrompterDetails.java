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

import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.html.CheckBox;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Label;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.TextElement;
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
    Division division = new Division(".detail-group");
    int playerIndex = 0;
    for (Integer deviceIndex : devicePlayables.keySet()) {
      division.appendChild(createVolumeDetail(playerIndex, deviceIndex));
      playerIndex++;
    }
    return division;
  }

  private Element createVolumeDetail(int playerIndex, Integer deviceIndex) {
    Division division = new Division(".detail");
    division.appendChild(createVolumeName(playerIndex));
    division.appendChild(createVolumeValue(deviceIndex));
    return division;
  }

  private Element createVolumeMute(int deviceIndex) {
    CheckBox checkbox = new CheckBox(".background-mute-" + deviceIndex);
    checkbox.appendProperty("onclick", CommandRenderer.render(DeviceCommand.MUTE_BACKGROUND, deviceIndex, "this.checked ? 1 : 0"));
    Label label = new Label();
    label.appendChild(checkbox);
    label.appendChild(new TextElement("Mute background"));
    return label;
  }

  private Element createVolumeName(int playerIndex) {
    Division division = new Division(".name", Utils.getPlayerName(playerIndex) + " Volume");
    return division;
  }

  private Element createVolumeRange(int deviceIndex) {
    Range range = new MidiRange(".device-velocity-" + deviceIndex);
    range.appendProperty("oninput", CommandRenderer.render(DeviceCommand.VELOCITY, deviceIndex));
    return range;
  }

  private Element createVolumeValue(int deviceIndex) {
    Division division = new Division(".value");
    division.appendChild(createVolumeValueGroup(deviceIndex));
    return division;
  }

  private Element createVolumeValueGroup(int deviceIndex) {
    Division division = new Division(".value-group");
    division.appendChild(createVolumeRange(deviceIndex));
    division.appendChild(createVolumeMute(deviceIndex));
    return division;
  }

}
