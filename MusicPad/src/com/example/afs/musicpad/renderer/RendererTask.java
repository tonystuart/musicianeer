// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.device.common.DeviceHandler.InputType;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnFooter;
import com.example.afs.musicpad.message.OnHeader;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.message.OnTemplates;
import com.example.afs.musicpad.message.OnTransport;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class RendererTask extends BrokerTask<Message> {

  private RandomAccessList<File> midiFiles;

  public RendererTask(Broker<Message> broker) {
    super(broker);
    subscribe(OnMidiFiles.class, message -> publishTemplates(message.getMidiFiles()));
    subscribe(OnSong.class, message -> doSong(message.getSong(), message.getTicksPerPixel()));
  }

  private void doSong(Song song, int ticksPerPixel) {
    publish(new OnHeader(song.getTitle(), ticksPerPixel, new HeaderRenderer(midiFiles, song).render()));
    publish(new OnFooter(new FooterRenderer(song).render()));
    publish(new OnTransport(new TransportRenderer(song).render()));
  }

  private String getInputOptions() {
    Template template = new Template("input-options");
    InputType[] mappingTypes = InputType.values();
    for (int i = 0; i < mappingTypes.length; i++) {
      InputType inputType = mappingTypes[i];
      Option option = new Option(inputType.name(), i);
      template.appendChild(option);
    }
    String inputOptions = template.render();
    return inputOptions;
  }

  private String getOutputOptions() {
    Template template = new Template("output-options");
    DeviceHandler.OutputType[] mappingTypes = DeviceHandler.OutputType.values();
    for (int i = 0; i < mappingTypes.length; i++) {
      DeviceHandler.OutputType outputType = mappingTypes[i];
      Option option = new Option(outputType.name(), i);
      template.appendChild(option);
    }
    String outputOptions = template.render();
    return outputOptions;
  }

  private String getProgramOptions() {
    Template template = new Template("program-options");
    for (int i = 0; i < Midi.PROGRAMS; i++) {
      Option option = new Option(Instruments.getProgramName(i), i);
      template.appendChild(option);
    }
    String programOptions = template.render();
    return programOptions;
  }

  private String getSongOptions(RandomAccessList<File> midiFiles) {
    Template template = new Template("song-options");
    int midiFileCount = midiFiles.size();
    for (int i = 0; i < midiFileCount; i++) {
      String name = midiFiles.get(i).getName();
      Option option = new Option(name, i);
      template.appendChild(option);
    }
    String songOptions = template.render();
    return songOptions;
  }

  private String getTransposeOptions() {
    Template template = new Template("transpose-options");
    for (int i = -2 * Midi.SEMITONES_PER_OCTAVE; i <= 2 * Midi.SEMITONES_PER_OCTAVE; i++) {
      template.appendChild(new Option(Integer.toString(i), i));
    }
    String transposeOptions = template.render();
    return transposeOptions;
  }

  private void publishTemplates(RandomAccessList<File> midiFiles) {
    this.midiFiles = midiFiles;
    List<String> templates = new LinkedList<>();
    templates.add(getSongOptions(midiFiles));
    templates.add(getProgramOptions());
    templates.add(getInputOptions());
    templates.add(getOutputOptions());
    templates.add(getTransposeOptions());
    getBroker().publish(new OnTemplates(templates));
  }

}
