// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import java.util.Set;

import com.example.afs.musicpad.DeviceCommand;
import com.example.afs.musicpad.device.common.DeviceHandler.InputType;
import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.Table;
import com.example.afs.musicpad.html.TableHeader;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.Value;

public class ChannelRenderer {

  private String deviceName;
  private int deviceIndex;
  private Song song;
  private int channel;
  private InputType inputType;
  private OutputType outputType;

  public ChannelRenderer(String deviceName, int deviceIndex, Song song, int channel, InputType inputType, OutputType outputType) {
    this.deviceName = deviceName;
    this.deviceIndex = deviceIndex;
    this.song = song;
    this.channel = channel;
    this.inputType = inputType;
    this.outputType = outputType;
  }

  public String render() {
    Table table = new Table();

    TableHeader header = table.createHeader();
    header.append("Channel");
    header.append("Instrument");
    header.append("Input");
    header.append("Output");
    header.append("Volume");

    TableRow row = table.createRow();
    row.append(getChannelSelect());
    row.append(getProgramSelect());
    row.append(getInputSelect());
    row.append(getOutputSelect());
    row.append(getVolumeRange());

    Division channelControls = new Division();
    channelControls.setClassName("channel-controls");
    channelControls.appendChild(getDeviceName());
    channelControls.appendChild(table);

    String html = channelControls.render();
    return html;
  }

  private Select getChannelSelect() {
    Select select = new Select("channel-select-" + deviceIndex);
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      if (song.getChannelNoteCount(channel) > 0) {
        int channelNumber = Value.toNumber(channel);
        Option option = new Option(Integer.toString(channelNumber), channelNumber, this.channel == channel);
        select.appendChild(option);
      }
    }
    select.appendProperty("onchange", PropertyRenderer.render(DeviceCommand.CHANNEL, deviceIndex));
    return select;
  }

  private Division getDeviceName() {
    Division nameDivision = new Division();
    nameDivision.setClassName("device-name");
    nameDivision.appendChild(new TextElement(deviceName));
    return nameDivision;
  }

  private Select getInputSelect() {
    Select select = new Select("input-select-" + deviceIndex);
    select.appendProperty("value", inputType.ordinal());
    select.appendProperty("onchange", PropertyRenderer.render(DeviceCommand.INPUT, deviceIndex));
    return select;
  }

  private Select getOutputSelect() {
    Select select = new Select("output-select-" + deviceIndex);
    select.appendProperty("value", outputType.ordinal());
    select.appendProperty("onchange", PropertyRenderer.render(DeviceCommand.OUTPUT, deviceIndex));
    return select;
  }

  private Select getProgramSelect() {
    Set<Integer> programs = song.getPrograms(channel);
    int channelProgram = 0;
    if (programs.size() > 0) {
      channelProgram = programs.iterator().next();
    }
    Select select = new Select("program-select-" + deviceIndex);
    select.appendProperty("value", channelProgram);
    select.appendProperty("onchange", PropertyRenderer.render(DeviceCommand.PROGRAM, deviceIndex));
    return select;
  }

  private Range getVolumeRange() {
    Range range = new Range("channel-volume-" + deviceIndex, 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(DeviceCommand.VELOCITY, deviceIndex));
    return range;
  }

}
