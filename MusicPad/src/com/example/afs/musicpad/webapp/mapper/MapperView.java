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

import javax.sound.midi.ShortMessage;

import com.example.afs.musicpad.device.common.Controller;
import com.example.afs.musicpad.device.midi.MidiConfiguration.InputMessage;
import com.example.afs.musicpad.device.midi.MidiConfiguration.OutputMessage;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.html.TableBody;
import com.example.afs.musicpad.html.TableRow;
import com.example.afs.musicpad.task.ControllerTask;

public class MapperView extends ShadowDom {

  public enum Mapping {
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
  }

  // Device, Channel, Type, Control, Mapping
  // 
  public MapperView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#mapper", ".tab", ".selected-tab") //
        .add(div(".title") //
            .add(text("MIDI Input Mapper"))) //
        .add(table("#mapping-table") //
            .addClickHandler() //
            .add(thead() //
                .add(tr() //
                    .add(th().add(text("Device"))) //
                    .add(th().add(text("Channel"))) //
                    .add(th().add(text("Control"))) //
                    .add(th().add(text("Type"))) //
                    .add(th().add(text("Mapping"))))) //
            .add(tbody("#mapping-body"))) //
        .add(div("#mapping-details", ".details") //
            .add(div() //
                .add(text("Press, rotate or slide an input on your MIDI controller to configure its action.")))) //

    ); //
  }

  public String displayMapping(String deviceType, InputMessage inputMessage, OutputMessage outputMessage) {
    int channel = inputMessage.getChannel();
    int control = inputMessage.getControl();
    int command = inputMessage.getCommand();
    String id = "mapping-" + deviceType + "-" + channel + "-" + control + "-" + command;
    TableRow tableRow = (TableRow) tr("#" + id) //
        .add(td().add(text(deviceType))) //
        .add(td().add(text(channel))) //
        .add(td().add(text(control))) //
        .add(td().add(text(formatCommand(command)))) //
        .add(td().add(text(formatOutputMessage(outputMessage)))) //
    ;
    TableBody tableBody = getElementById("mapping-body");
    int index = tableBody.getChildCount();
    insertRow(tableBody, tableRow, index);
    return id;
  }

  public void renderDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = createDeviceList(deviceControllers);
    Parent songListParent = getElementById("mapper-list");
    replaceChildren(songListParent, div);
  }

  public void renderMappingDetails(String messageType, int channel, int data1, int data2) {
    Parent mappingDetails = getElementById("mapping-details");
    replaceChildren(mappingDetails, createMappingDetails(messageType, channel, data1, data2));
  }

  public void selectCommand() {
    Radio outputCommand = getElementById("output-command");
    setProperty(outputCommand, "checked", true);
  }

  public void selectGroup() {
    Radio outputGroup = getElementById("output-group");
    setProperty(outputGroup, "checked", true);
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

  private Select createMapping() {
    return new Select("#command") //
        .setName("output") //
        .required() //
        .add(optionGroup("Player Settings") //
            .add(option("Select Instrument", Mapping.PLAYER_SELECT_PROGRAM)) //
            .add(option("Previous Instrument", Mapping.PLAYER_PREVIOUS_PROGRAM)) //
            .add(option("Next Instrument", Mapping.PLAYER_NEXT_PROGRAM)) //
            .add(option("Select Volume", Mapping.PLAYER_SELECT_VELOCITY)) //
            .add(option("Decrease Volume", Mapping.PLAYER_DECREASE_VELOCITY)) //
            .add(option("Increase Volume", Mapping.PLAYER_INCREASE_VELOCITY)) //
            .add(option("Select Channel", Mapping.PLAYER_SELECT_CHANNEL)) //
            .add(option("Previous Channel", Mapping.PLAYER_PREVIOUS_CHANNEL)) //
            .add(option("Next Channel", Mapping.PLAYER_NEXT_CHANNEL))) //

        .add(optionGroup("Background Settings") //
            .add(option("Mute", Mapping.BACKGROUND_MUTE)) //
            .add(option("Select Volume", Mapping.BACKGROUND_SELECT_VELOCITY)) //
            .add(option("Decrease Volume", Mapping.BACKGROUND_DECREASE_VELOCITY)) //
            .add(option("Increase Volume", Mapping.BACKGROUND_INCREASE_VELOCITY))) //

        .add(optionGroup("Master Settings") //
            .add(option("Select Volume", Mapping.MASTER_SELECT_VOLUME)) //
            .add(option("Decrease Volume", Mapping.MASTER_DECREASE_VOLUME)) //
            .add(option("Increase Volume", Mapping.MASTER_INCREASE_VOLUME)) //
            .add(option("Override Instrument", Mapping.MASTER_INSTRUMENT))) //

        .add(optionGroup("Transport Settings") //
            .add(option("Play/Resume", Mapping.TRANSPORT_PLAY)) //
            .add(option("Stop/Pause", Mapping.TRANSPORT_STOP)) //
            .add(option("Select Measure", Mapping.TRANSPORT_SELECT_MEASURE)) //
            .add(option("Previous Measure", Mapping.TRANSPORT_PREVIOUS_MEASURE)) //
            .add(option("Next Measure", Mapping.TRANSPORT_NEXT_MEASURE)) //
            .add(option("Select Tempo", Mapping.TRANSPORT_SELECT_TEMPO)) //
            .add(option("Decrease Tempo", Mapping.TRANSPORT_DECREASE_TEMPO)) //
            .add(option("Increase Tempo", Mapping.TRANSPORT_INCREASE_TEMPO))) //

        .add(optionGroup("Library Settings") //
            .add(option("Select Song", Mapping.LIBRARY_SELECT_SONG)) //
            .add(option("Previous Song", Mapping.LIBRARY_PREVIOUS_SONG)) //
            .add(option("Next Song", Mapping.LIBRARY_NEXT_SONG)) //
            .add(option("Select Transpose", Mapping.LIBRARY_SELECT_TRANSPOSE)) //
            .add(option("Transpose Lower", Mapping.LIBRARY_TRANSPOSE_LOWER)) //
            .add(option("Transpose Higher", Mapping.LIBRARY_TRANSPOSE_HIGHER))) //

        .add(optionGroup("Karaoke Settings") //
            .add(option("Play Notes at Tick", Mapping.KARAOKE_TYPE_TICK)) //
            .add(option("Play Notes in Measure", Mapping.KARAOKE_TYPE_MEASURE))); //
  }

  private Node createMappingDetails(String messageType, int channel, int data1, int data2) {
    // NB: fieldset does not support display: flex;
    // See https://stackoverflow.com/questions/28078681/why-cant-fieldset-be-flex-containers`
    return div(".row") //
        .add(form("#command", ".row") //
            .addSubmitHandler() //
            .add(fieldSet() //
                .add(legend() //
                    .add(label() //
                        .add(text("&nbsp;Command&nbsp;")))) //
                .add(createMapping()) //
                .add(submit() //
                    .setValue("Set")))) //
        .add(form("#group", ".row") //
            .addSubmitHandler() //
            .add(fieldSet() //
                .add(legend() //
                    .add(label() //
                        .add(text("&nbsp;Group&nbsp;")))) //
                .add(text("Index")) //
                .add(numberInput() //
                    .setMinimum(0) //
                    .setName("group-index") //
                    .required()) //
                .add(text("Label")) //
                .add(textInput() //
                    .setName("group-label") //
                    .required()) //
                .add(submit() //
                    .setValue("Set")))) //
        .add(form("#sound", ".row") //
            .addSubmitHandler() //
            .add(fieldSet() //
                .add(legend() //
                    .add(label() //
                        .add(text("&nbsp;Sound&nbsp;")))) //
                .add(text("Index")) //
                .add(numberInput() //
                    .setMinimum(0) //
                    .setName("sound-index") //
                    .required()) //
                .add(text("Label")) //
                .add(textInput() //
                    .setName("sound-label") //
                    .required()) //
                .add(submit() //
                    .setValue("Set")))) //
    ; //
  }

  private String formatCommand(int command) {
    String s;
    switch (command) {
    case ShortMessage.NOTE_OFF:
      s = "NOTE_OFF";
      break;
    case ShortMessage.NOTE_ON:
      s = "NOTE_ON";
      break;
    case ShortMessage.POLY_PRESSURE:
      s = "POLY_PRESSURE";
      break;
    case ShortMessage.CONTROL_CHANGE:
      s = "CONTROL_CHANGE";
      break;
    case ShortMessage.PROGRAM_CHANGE:
      s = "PROGRAM_CHANGE";
      break;
    case ShortMessage.CHANNEL_PRESSURE:
      s = "CHANNEL_PRESSURE";
      break;
    case ShortMessage.PITCH_BEND:
      s = "PITCH_BEND";
      break;
    default:
      s = "COMMAND_" + command;
      break;
    }
    return s;
  }

  private String formatOutputMessage(OutputMessage outputMessage) {
    return outputMessage == null ? "Default" : outputMessage.asString();
  }

  private Node option(String text, Mapping mapping) {
    return super.option(text, mapping.name());
  }

}
