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
import com.example.afs.musicpad.device.midi.InputMessage;
import com.example.afs.musicpad.device.midi.InputType;
import com.example.afs.musicpad.device.midi.OutputMessage;
import com.example.afs.musicpad.device.midi.OutputType;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.Select;
import com.example.afs.musicpad.html.ShadowDomBuilder;
import com.example.afs.musicpad.player.PlayableMap;
import com.example.afs.musicpad.task.ControllerTask;

public class MapperView extends ShadowDomBuilder {

  public MapperView(ControllerTask controllerTask) {
    super(controllerTask);
    add(div("#mapper", ".tab", ".selected-tab") //
        .add(div(".title") //
            .add(text("MIDI Input Mapper"))) //
        .add(div("#device-prompt") //
            .add(text("Select Input Device:")) //
            .add(div("#device-type-container"))) //
        .add(div("#mapper-diagram-container") //
            .add(div("#mapper-diagram-instructions") //
                .add(orderedList() //
                    .add(listItem() //
                        .add(text("Select an input device")))
                    .add(listItem() //
                        .add(text("Press, rotate or slide an input on your MIDI controller to configure its action")))
                    .add(listItem() //
                        .add(text("Drag the new action to its correct position relative to other mappings"))))))); //
  }

  public Parent createMapping(InputMessage inputMessage, OutputMessage outputMessage) {
    int channel = inputMessage.getChannel();
    int control = inputMessage.getControl();
    InputType inputType = outputMessage.getInputType();
    OutputType outputType = outputMessage.getOutputType();
    String id = getMappingId(inputMessage);
    Parent mapping = new Division("#" + id, ".mapping") //
        .addMoveSource() //
        .addClickHandler() //
        .add(div("#input-" + id, ".header") //
            .add(text("Input (" + channel + "/" + control + ")")) //
            .add(div("#close-" + id, ".close-button") //
                .add(text("X")))) //
        .add(createInputType("input-type-" + id, inputType)) //
        .add(div() //
            .add(text("Output"))) //
        .add(createOutputType("output-type-" + id, outputType)) //
        .add(div(".index-label", ".column") //
            .add(div() //
                .add(text("Index"))) //
            .add(numberInput("#index-" + id) //
                .setMinimum(PlayableMap.DEFAULT_GROUP + 1) //
                .setValue(outputMessage.getIndex()) //
                .addInputHandler() //
                .required()) //
            .add(div() //
                .add(text("Label"))) //
            .add(textInput("#label-" + id) //
                .setValue(outputMessage.getLabel()) //
                .addInputHandler() //
                .required())); //
    mapping.setData(new MappingData(inputMessage, outputMessage));
    mapping.setProperty("style", "left: " + outputMessage.getX() + "px; top: " + outputMessage.getY() + "px;");
    if (isGroupOrSound(outputType)) {
      mapping.addClassName("index-label-visible");
    }
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

  public void displayMappings(NavigableMap<InputMessage, OutputMessage> inputMap) {
    Parent mapperDiagram = createMapperDiagram();
    for (Entry<InputMessage, OutputMessage> entry : inputMap.entrySet()) {
      Parent mapping = createMapping(entry.getKey(), entry.getValue());
      mapperDiagram.appendChild(mapping);
    }
    Parent mapperDiagramContainer = getElementById("mapper-diagram-container");
    replaceChildren(mapperDiagramContainer, mapperDiagram);
  }

  public String getMappingId(InputMessage inputMessage) {
    String id = "mapping-" + inputMessage.getChannel() + "-" + inputMessage.getControl() + (inputMessage.isKey() ? "-k" : "-c");
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

  public MappingData removeMapping(String mappingId) {
    Parent mapping = getElementById(mappingId);
    MappingData mappingData = mapping.getData();
    Division diagram = getElementById("mapper-diagram");
    remove(diagram, mapping);
    return mappingData;
  }

  public void renderDeviceList(NavigableMap<Integer, Controller> deviceControllers) {
    Division div = createDeviceList(deviceControllers);
    Parent songListParent = getElementById("mapper-list");
    replaceChildren(songListParent, div);
  }

  public void selectMapping(String id) {
    selectElement(id, "selected-mapping");
  }

  public void selectMapping(String id, InputMessage inputMessage, ShortMessage shortMessage) {
    int channel = inputMessage.getChannel();
    int control = inputMessage.getControl();
    int command = shortMessage.getCommand();
    int value = shortMessage.getData2();
    Parent labelElement = getElementById("input-" + id);
    String label = "Input (" + channel + "/" + control + "/" + command + "/" + value + ")";
    replaceChildren(labelElement, div("#input-" + id) //
        .add(text(label))); //
    selectMapping(id);
  }

  public void setOutputType(String mappingId, OutputType outputType) {
    if (isGroupOrSound(outputType)) {
      addClass(mappingId, "index-label-visible");
    } else {
      removeClass(mappingId, "index-label-visible");
    }
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

  private Select createInputType(String id, InputType inputType) {
    int selected = inputType.ordinal();
    return new Select("#" + id) //
        .addInputHandler() //
        .required() //
        .add(option("Button", InputType.BUTTON, selected)) //
        .add(option("Key", InputType.KEY, selected)) //
        .add(option("Pad", InputType.PAD, selected)) //
        .add(option("Pedal", InputType.PEDAL, selected)) //
        .add(option("Rotary", InputType.ROTARY, selected)) //
        .add(option("Slider", InputType.SLIDER, selected)) //
        .add(option("Wheel", InputType.WHEEL, selected)) //
    ;
  }

  private Parent createMapperDiagram() {
    Parent mapperDiagram = div("#mapper-diagram", ".scrollable")//
        .addMoveTarget();
    return mapperDiagram;
  }

  private Select createOutputType(String id, OutputType outputType) {
    int selected = outputType.ordinal();
    return new Select("#" + id) //
        .addInputHandler() //
        .required() //
        .add(optionGroup("Player Settings") //
            .add(option("Default", OutputType.DEFAULT, selected)) //
            .add(option("Select Instrument", OutputType.PLAYER_SELECT_PROGRAM, selected)) //
            .add(option("Previous Instrument", OutputType.PLAYER_PREVIOUS_PROGRAM, selected)) //
            .add(option("Next Instrument", OutputType.PLAYER_NEXT_PROGRAM, selected)) //
            .add(option("Select Volume", OutputType.PLAYER_SELECT_VELOCITY, selected)) //
            .add(option("Decrease Volume", OutputType.PLAYER_DECREASE_VELOCITY, selected)) //
            .add(option("Increase Volume", OutputType.PLAYER_INCREASE_VELOCITY, selected)) //
            .add(option("Select Channel", OutputType.PLAYER_SELECT_CHANNEL, selected)) //
            .add(option("Previous Channel", OutputType.PLAYER_PREVIOUS_CHANNEL, selected)) //
            .add(option("Next Channel", OutputType.PLAYER_NEXT_CHANNEL, selected))) //

        .add(optionGroup("Background Settings") //
            .add(option("Mute", OutputType.BACKGROUND_MUTE, selected)) //
            .add(option("Select Volume", OutputType.BACKGROUND_SELECT_VELOCITY, selected)) //
            .add(option("Decrease Volume", OutputType.BACKGROUND_DECREASE_VELOCITY, selected)) //
            .add(option("Increase Volume", OutputType.BACKGROUND_INCREASE_VELOCITY, selected))) //

        .add(optionGroup("Master Settings") //
            .add(option("Select Volume", OutputType.MASTER_SELECT_VOLUME, selected)) //
            .add(option("Decrease Volume", OutputType.MASTER_DECREASE_VOLUME, selected)) //
            .add(option("Increase Volume", OutputType.MASTER_INCREASE_VOLUME, selected)) //
            .add(option("Override Instrument", OutputType.MASTER_INSTRUMENT, selected))) //

        .add(optionGroup("Transport Settings") //
            .add(option("Play/Resume", OutputType.TRANSPORT_PLAY, selected)) //
            .add(option("Stop/Pause", OutputType.TRANSPORT_STOP, selected)) //
            .add(option("Select Measure", OutputType.TRANSPORT_SELECT_MEASURE, selected)) //
            .add(option("Previous Measure", OutputType.TRANSPORT_PREVIOUS_MEASURE, selected)) //
            .add(option("Next Measure", OutputType.TRANSPORT_NEXT_MEASURE, selected)) //
            .add(option("Select Tempo", OutputType.TRANSPORT_SELECT_TEMPO, selected)) //
            .add(option("Decrease Tempo", OutputType.TRANSPORT_DECREASE_TEMPO, selected)) //
            .add(option("Increase Tempo", OutputType.TRANSPORT_INCREASE_TEMPO, selected))) //

        .add(optionGroup("Library Settings") //
            .add(option("Select Song", OutputType.LIBRARY_SELECT_SONG, selected)) //
            .add(option("Previous Song", OutputType.LIBRARY_PREVIOUS_SONG, selected)) //
            .add(option("Next Song", OutputType.LIBRARY_NEXT_SONG, selected)) //
            .add(option("Select Transpose", OutputType.LIBRARY_SELECT_TRANSPOSE, selected)) //
            .add(option("Transpose Lower", OutputType.LIBRARY_TRANSPOSE_LOWER, selected)) //
            .add(option("Transpose Higher", OutputType.LIBRARY_TRANSPOSE_HIGHER, selected))) //

        .add(optionGroup("Karaoke Settings") //
            .add(option("Play Notes at Tick", OutputType.KARAOKE_TYPE_TICK, selected)) //
            .add(option("Play Notes in Measure", OutputType.KARAOKE_TYPE_MEASURE, selected)) //
            .add(option("Select Group", OutputType.KARAOKE_SELECT_GROUP, selected)) //
            .add(option("Select Sound", OutputType.KARAOKE_SELECT_SOUND, selected))); //
  }

  private boolean isGroupOrSound(OutputType outputType) {
    return outputType == OutputType.KARAOKE_SELECT_GROUP || outputType == OutputType.KARAOKE_SELECT_SOUND;
  }

  private Option option(String text, Enum<?> optionEnum, int selected) {
    int ordinal = optionEnum.ordinal();
    Option option = super.option(text, ordinal);
    if (ordinal == selected) {
      option.setProperty("selected");
    }
    return option;
  }

}
