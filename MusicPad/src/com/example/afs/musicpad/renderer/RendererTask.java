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

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.device.common.InputMapping.MappingType;
import com.example.afs.musicpad.device.midi.configuration.ChannelState;
import com.example.afs.musicpad.html.Option;
import com.example.afs.musicpad.html.Template;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelState;
import com.example.afs.musicpad.message.OnCommand;
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

  private Song currentSong;
  private RandomAccessList<File> midiFiles;

  public RendererTask(Broker<Message> broker) {
    super(broker);
    subscribe(OnCommand.class, message -> doCommand(message.getCommand(), message.getParameter()));
    subscribe(OnMidiFiles.class, message -> publishTemplates(message.getMidiFiles()));
    subscribe(OnSong.class, message -> doSong(message.getSong(), message.getTicksPerPixel()));
  }

  private void doCommand(Command command, int parameter) {
    switch (command) {
    case SHOW_CHANNEL_STATE:
      doShowChannelState();
      break;
    case SHOW_DRUM_INFO:
      doShowDrumInfo();
      break;
    default:
      break;
    }
  }

  private void doShowChannelState() {
    if (currentSong != null) {
      showChannelState(currentSong);
    }
  }

  private void doShowDrumInfo() {
    if (currentSong != null) {
      showDrumInfo(currentSong);
    }
  }

  private void doSong(Song song, int ticksPerPixel) {
    currentSong = song;
    publish(new OnHeader(song.getTitle(), ticksPerPixel, new HeaderRenderer(midiFiles, song).render()));
    publish(new OnFooter(new FooterRenderer(song).render()));
    publish(new OnTransport(new TransportRenderer(song).render()));
  }

  private String getInputOptions() {
    Template template = new Template("input-options");
    MappingType[] mappingTypes = MappingType.values();
    for (int i = 0; i < mappingTypes.length; i++) {
      MappingType mappingType = mappingTypes[i];
      Option option = new Option(mappingType.name(), i);
      template.appendChild(option);
    }
    String inputOptions = template.render();
    return inputOptions;
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
    for (int i = -Midi.SEMITONES_PER_OCTAVE; i <= Midi.SEMITONES_PER_OCTAVE; i++) {
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
    templates.add(getTransposeOptions());
    getBroker().publish(new OnTemplates(templates));
  }

  private void showChannelState(Song song) {
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      int noteCount = song.getChannelNoteCount(channel);
      publish(new OnChannelState(channel, noteCount == 0 ? ChannelState.INACTIVE : ChannelState.ACTIVE));
    }
  }

  private void showDrumInfo(Song song) {
    int drumBeatCount = song.getChannelNoteCount(Midi.DRUM);
    if (drumBeatCount > 0) {
      System.out.println("CHN 9 TOT " + drumBeatCount);
      int[] distinctNoteCount = song.getDistinctNoteCount(Midi.DRUM);
      for (int drum = 0; drum < Midi.NOTES; drum++) {
        int count = distinctNoteCount[drum];
        if (count > 0) {
          System.out.printf("%4d [%s]\n", count, Instruments.getDrumName(drum));
        }
      }
    }
  }
}
