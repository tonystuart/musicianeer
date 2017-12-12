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
import com.example.afs.musicpad.device.midi.MidiConfiguration;
import com.example.afs.musicpad.device.midi.MidiConfiguration.DeviceType;
import com.example.afs.musicpad.device.midi.MidiConfiguration.InputMessage;
import com.example.afs.musicpad.device.midi.MidiConfiguration.InputType;
import com.example.afs.musicpad.device.midi.MidiConfiguration.OutputMessage;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.task.ControllerTask;

public class MapperView extends ShadowDom {

  public enum OutputType {
    DEFAULT, //
    PLAYER_SELECT_PROGRAM, //
    PLAYER_PREVIOUS_PROGRAM, //
    PLAYER_NEXT_PROGRAM, //
    PLAYER_SELECT_VELOCITY, //
    PLAYER_DECREASE_VELOCITY, //
    PLAYER_INCREASE_VELOCITY, //
    PLAYER_SELECT_CHANNEL, //
    PLAYER_PREVIOUS_CHANNEL, //
    PLAYER_NEXT_CHANNEL, //
    BACKGROUND_MUTE, //
    BACKGROUND_SELECT_VELOCITY, //
    BACKGROUND_DECREASE_VELOCITY, //
    BACKGROUND_INCREASE_VELOCITY, //
    MASTER_SELECT_VOLUME, //
    MASTER_DECREASE_VOLUME, //
    MASTER_INCREASE_VOLUME, //
    MASTER_INSTRUMENT, //
    TRANSPORT_PLAY, //
    TRANSPORT_STOP, //
    TRANSPORT_SELECT_MEASURE, //
    TRANSPORT_PREVIOUS_MEASURE, //
    TRANSPORT_NEXT_MEASURE, //
    TRANSPORT_SELECT_TEMPO, //
    TRANSPORT_DECREASE_TEMPO, //
    TRANSPORT_INCREASE_TEMPO, //
    LIBRARY_SELECT_SONG, //
    LIBRARY_PREVIOUS_SONG, //
    LIBRARY_NEXT_SONG, //
    LIBRARY_SELECT_TRANSPOSE, //
    LIBRARY_TRANSPOSE_LOWER, //
    LIBRARY_TRANSPOSE_HIGHER, //
    KARAOKE_TYPE_TICK, //
    KARAOKE_TYPE_MEASURE, //
    KARAOKE_SELECT_GROUP, //
    KARAOKE_SELECT_SOUND, //
  }

  public MapperView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#mapper", ".tab", ".selected-tab") //
        .add(div(".title") //
            .add(text("MIDI Input Mapper"))) //
        .add(div() //
            .add(text("Select Input Device:")) //
            .add(div("#device-type-container"))) //
        .add(div("#mapper-diagram")//
            .addMoveTarget() //
            .add(text("Select an input device, then press, rotate or slide an input on your MIDI controller to configure its action.")))) //
    ; //
  }

  public Parent createMapping(InputMessage inputMessage, OutputMessage outputMessage) {
    int channel = inputMessage.getChannel();
    int control = inputMessage.getControl();
    InputType inputType = inputMessage.getInputType();
    MidiConfiguration.DeviceType deviceType;
    if (outputMessage == null) {
      switch (inputType) {
      case CONTROL:
        deviceType = MidiConfiguration.DeviceType.Rotary;
        break;
      case KEY:
        deviceType = MidiConfiguration.DeviceType.Key;
        break;
      default:
        throw new UnsupportedOperationException(inputType.toString());
      }
    } else {
      deviceType = outputMessage.getDeviceType();
    }
    String id = getMappingId(inputMessage);
    Parent mapping = new Division("#" + id, ".mapping") //
        .addMoveSource() //
        .add(div("Input (" + channel + "/" + control + ")")) //
        .add(createInputSelect(deviceType)) //
        .add(div() //
            .add(div("Output")) //
            .add(createOutputSelect(OutputType.DEFAULT))) //
        .add(div(".mapper-index-label") //
            .add(div("Index")) //
            .add(numberInput().setMinimum(0).setValue(control).required()) //
            .add(div("Label")) //
            .add(textInput().setValue(control).required())); //
    return mapping;
  }

  public void displayDeviceSelector(NavigableMap<Integer, Controller> deviceControllers) {
    Select select = createDeviceSelector(deviceControllers);
    Division deviceTypeContainer = getElementById("device-type-container");
    replaceChildren(deviceTypeContainer, select);
  }

  public String displayMapping(InputMessage inputMessage, OutputMessage outputMessage) {
    Parent mapping = createMapping(inputMessage, outputMessage);
    Division diagram = getElementById("mapper-diagram");
    appendChild(diagram, mapping);
    return mapping.getId();
  }

  public String getMappingId(InputMessage inputMessage) {
    String id = "mapping-" + inputMessage.getChannel() + "-" + inputMessage.getControl();
    return id;
  }

  @Override
  public Parent nameValue(String name, Object value) {
    return div(".row") //
        .add(div(".name") //
            .add(text(name))) //
        .add(div(".value") //
            .add(text("&nbsp;" + value))); //
  }

  public void renderDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = createDeviceList(deviceControllers);
    Parent songListParent = getElementById("mapper-list");
    replaceChildren(songListParent, div);
  }

  public void selectCommand() {
    Radio outputCommand = getElementById("output-command");
    setProperty(outputCommand, "checked", true);
  }

  public void selectGroup() {
    Radio outputGroup = getElementById("output-group");
    setProperty(outputGroup, "checked", true);
  }

  public void selectMapping(String id) {
  }

  public void selectSound() {
    Radio outputSound = getElementById("output-sound");
    setProperty(outputSound, "checked", true);
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

  private Select createDeviceSelector(NavigableMap<Integer, Controller> deviceControllers) {
    Select select = new Select("#device-type");
    select.addInputHandler();
    for (Entry<Integer, Controller> entry : deviceControllers.entrySet()) {
      Option option = new Option(entry.getValue().getDeviceName(), entry.getKey());
      select.appendChild(option);
    }
    return select;
  }

  private Select createInputSelect(DeviceType deviceType) {
    Select select = new Select("#input-select");
    for (DeviceType value : DeviceType.values()) {
      Option option = new Option(value.name(), value.name(), value.equals(deviceType));
      select.appendChild(option);
    }
    return select;
  }

  private Select createOutputSelect(OutputType outputType) {
    return new Select("#output-select") //
        .addInputHandler() //
        .setValue(outputType.name()) //
        .required() //
        .add(optionGroup("Player Settings") //
            .add(option("Default", OutputType.DEFAULT)) //
            .add(option("Select Instrument", OutputType.PLAYER_SELECT_PROGRAM)) //
            .add(option("Previous Instrument", OutputType.PLAYER_PREVIOUS_PROGRAM)) //
            .add(option("Next Instrument", OutputType.PLAYER_NEXT_PROGRAM)) //
            .add(option("Select Volume", OutputType.PLAYER_SELECT_VELOCITY)) //
            .add(option("Decrease Volume", OutputType.PLAYER_DECREASE_VELOCITY)) //
            .add(option("Increase Volume", OutputType.PLAYER_INCREASE_VELOCITY)) //
            .add(option("Select Channel", OutputType.PLAYER_SELECT_CHANNEL)) //
            .add(option("Previous Channel", OutputType.PLAYER_PREVIOUS_CHANNEL)) //
            .add(option("Next Channel", OutputType.PLAYER_NEXT_CHANNEL))) //

        .add(optionGroup("Background Settings") //
            .add(option("Mute", OutputType.BACKGROUND_MUTE)) //
            .add(option("Select Volume", OutputType.BACKGROUND_SELECT_VELOCITY)) //
            .add(option("Decrease Volume", OutputType.BACKGROUND_DECREASE_VELOCITY)) //
            .add(option("Increase Volume", OutputType.BACKGROUND_INCREASE_VELOCITY))) //

        .add(optionGroup("Master Settings") //
            .add(option("Select Volume", OutputType.MASTER_SELECT_VOLUME)) //
            .add(option("Decrease Volume", OutputType.MASTER_DECREASE_VOLUME)) //
            .add(option("Increase Volume", OutputType.MASTER_INCREASE_VOLUME)) //
            .add(option("Override Instrument", OutputType.MASTER_INSTRUMENT))) //

        .add(optionGroup("Transport Settings") //
            .add(option("Play/Resume", OutputType.TRANSPORT_PLAY)) //
            .add(option("Stop/Pause", OutputType.TRANSPORT_STOP)) //
            .add(option("Select Measure", OutputType.TRANSPORT_SELECT_MEASURE)) //
            .add(option("Previous Measure", OutputType.TRANSPORT_PREVIOUS_MEASURE)) //
            .add(option("Next Measure", OutputType.TRANSPORT_NEXT_MEASURE)) //
            .add(option("Select Tempo", OutputType.TRANSPORT_SELECT_TEMPO)) //
            .add(option("Decrease Tempo", OutputType.TRANSPORT_DECREASE_TEMPO)) //
            .add(option("Increase Tempo", OutputType.TRANSPORT_INCREASE_TEMPO))) //

        .add(optionGroup("Library Settings") //
            .add(option("Select Song", OutputType.LIBRARY_SELECT_SONG)) //
            .add(option("Previous Song", OutputType.LIBRARY_PREVIOUS_SONG)) //
            .add(option("Next Song", OutputType.LIBRARY_NEXT_SONG)) //
            .add(option("Select Transpose", OutputType.LIBRARY_SELECT_TRANSPOSE)) //
            .add(option("Transpose Lower", OutputType.LIBRARY_TRANSPOSE_LOWER)) //
            .add(option("Transpose Higher", OutputType.LIBRARY_TRANSPOSE_HIGHER))) //

        .add(optionGroup("Karaoke Settings") //
            .add(option("Play Notes at Tick", OutputType.KARAOKE_TYPE_TICK)) //
            .add(option("Play Notes in Measure", OutputType.KARAOKE_TYPE_MEASURE)) //
            .add(option("Select Group", OutputType.KARAOKE_SELECT_GROUP)) //
            .add(option("Select Sound", OutputType.KARAOKE_SELECT_SOUND))); //
  }

  private Node option(String text, OutputType outputType) {
    return super.option(text, outputType.name());
  }

}
