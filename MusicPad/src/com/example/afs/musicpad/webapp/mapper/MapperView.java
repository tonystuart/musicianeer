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
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Radio;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDom;
import com.example.afs.musicpad.task.ControllerTask;

public class MapperView extends ShadowDom {

  public MapperView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#mapper", ".tab", ".selected-tab") //
        .add(div(".left") //
            .add(div(".title") //
                .add(text("MIDI Input Mapper"))) //
            .add(div("#mapper-list", ".list") // renderDeviceList
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
            .add(div("#message-details", ".details")))); //
  }

  public void renderDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = createDeviceList(deviceControllers);
    Parent songListParent = getElementById("mapper-list");
    replaceChildren(songListParent, div);
  }

  public void renderMessageDetails(String messageType, int channel, int data1, int data2) {
    Parent songsRight = getElementById("message-details");
    replaceChildren(songsRight, createMessageDetails(messageType, channel, data1, data2));
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
        .addChangeHandler() //
        .add(optionGroup("Player Settings") //
            .add(option("Select Instrument", "player-select-program")) //
            .add(option("Previous Instrument", "player-previous-program")) //
            .add(option("Next Instrument", "player-next-program")) //
            .add(option("Select Volume", "player-select-velocity")) //
            .add(option("Decrease Volume", "player-decrease-velocity")) //
            .add(option("Increase Volume", "player-increase-velocity")) //
            .add(option("Select Channel", "player-select-channel")) //
            .add(option("Previous Channel", "player-previous-channel")) //
            .add(option("Next Channel", "player-next-channel"))) //

        .add(optionGroup("Background Settings") //
            .add(option("Mute", "background-mute")) //
            .add(option("Select Volume", "background-select-velocity")) //
            .add(option("Decrease Volume", "background-decrease-velocity")) //
            .add(option("Increase Volume", "background-increase-velocity"))) //

        .add(optionGroup("Master Settings") //
            .add(option("Select Volume", "master-select-volume")) //
            .add(option("Decrease Volume", "master-decrease-volume")) //
            .add(option("Increase Volume", "master-increase-volume")) //
            .add(option("Map to Piano", "master-override"))) //

        .add(optionGroup("Transport Settings") //
            .add(option("Play/Resume", "transport-play")) //
            .add(option("Stop/Pause", "transport-stop")) //
            .add(option("Select Measure", "transport-select-measure")) //
            .add(option("Previous Measure", "transport-previous-measure")) //
            .add(option("Next Measure", "transport-next-measure")) //
            .add(option("Select Tempo", "transport-select-tempo")) //
            .add(option("Decrease Tempo", "transport-decrease-tempo")) //
            .add(option("Increase Tempo", "transport-increase-tempo"))) //

        .add(optionGroup("Library Settings") //
            .add(option("Select Song", "library-select-song")) //
            .add(option("Previous Song", "library-previous-song")) //
            .add(option("Next Song", "library-next-song")) //
            .add(option("Select Transpose", "library-select-transpose")) //
            .add(option("Transpose Lower", "library-transpose-lower")) //
            .add(option("Transpose Higher", "library-transpose-higher"))) //

        .add(optionGroup("Karaoke Settings") //
            .add(option("Play Notes at Tick", "karaoke-type-tick")) //
            .add(option("Play Notes in Measure", "karaoke-type-measure"))); //
  }

  private Node createMessageDetails(String messageType, int channel, int data1, int data2) {
    return div() //
        .add(fieldSet("#input") //
            .add(legend() //
                .add(text("Input")))
            .add(nameValue("Input", messageType)) //
            .add(nameValue("Channel", channel)) //
            .add(nameValue("Data1", data1)) //
            .add(nameValue("Data2", data2)) //
            .add(label() // 
                .add(checkbox("#all-messages") //
                    .addCheckHandler()) //
                .add(text("Show all input messages"))))
        .add(fieldSet("#output") //
            .add(legend() //
                .add(text("Output"))) //
            .add(fieldSet() //
                .add(legend() //
                    .add(label() //
                        .add(radio("#output-command") //
                            .setName("output-type")) //
                        .add(text("&nbsp;Command&nbsp;")))) //
                .add(createMapping())) //
            .add(fieldSet("#group") //
                .add(legend() //
                    .add(label() //
                        .add(radio("#output-group") //
                            .setName("output-type")) //
                        .add(text("&nbsp;Group&nbsp;")))) //
                .add(text("Index")) //
                .add(numberInput("#group-index") //
                    .setMinimum(0) //
                    .addInputHandler()) //
                .add(text("Label")) //
                .add(textInput("#group-label") //
                    .addInputHandler())) //
            .add(fieldSet("#sound") //
                .add(legend() //
                    .add(label() //
                        .add(radio("#output-sound") //
                            .setName("output-type")) //
                        .add(text("&nbsp;Sound&nbsp;")))) //
                .add(text("Index")) //
                .add(numberInput("#sound-index") //
                    .setMinimum(0) //
                    .addInputHandler()) //
                .add(text("Label")) //
                .add(textInput("#sound-label") //
                    .addInputHandler()))) //
    ; //
  }

}
