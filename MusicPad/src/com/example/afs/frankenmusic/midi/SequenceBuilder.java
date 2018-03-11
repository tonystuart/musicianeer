// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.midi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.util.FileCreator;
import com.example.afs.musicpad.util.RandomAccessList;

public class SequenceBuilder {

  public static final int TICKS_PER_MEASURE = Default.BEATS_PER_MEASURE * Default.TICKS_PER_BEAT; // TODO: Support more than just default time

  public static long roundTickToNextMeasure(long endingTick) {
    return ((endingTick + (TICKS_PER_MEASURE - 1)) / TICKS_PER_MEASURE) * TICKS_PER_MEASURE;
  }

  public static long roundTickToThisMeasure(long tick) {
    return (tick / TICKS_PER_MEASURE) * TICKS_PER_MEASURE;
  }

  private final Sequence sequence;
  private final Track[] tracks = new Track[Midi.CHANNELS];
  private final FileCreator fileCreator;
  private int[] channelInstruments = new int[Midi.CHANNELS];

  public SequenceBuilder() {
    this("midi", "%05d.mid");
  }

  public SequenceBuilder(String directoryName, String fileNameTemplate) {
    try {
      sequence = new Sequence(Sequence.PPQ, Default.RESOLUTION);
      for (int i = 0; i < Midi.CHANNELS; i++) {
        tracks[i] = sequence.createTrack();
      }
      fileCreator = new FileCreator(directoryName, fileNameTemplate);
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }

  public void addNote(long baseTick, Note note) {
    int channel = note.getChannel();
    long tick = baseTick + note.getTick();
    int midiNote = note.getMidiNote();
    int velocity = note.getVelocity();
    long duration = note.getDuration();
    int program = note.getProgram();
    if (channel != Midi.DRUM) {
      int currentInstrument = channelInstruments[channel];
      if (currentInstrument != program) {
        addProgramChange(tick, channel, program);
        channelInstruments[channel] = program;
      }
    }
    addNoteOnEvent(tick, channel, midiNote, velocity);
    addNoteOffEvent(tick + duration, channel, midiNote);
  }

  public void addNote(Note note) {
    addNote(note.getChannel(), note);
  }

  public void addNoteOffEvent(long tick, int channel, int note) {
    // We preserve a 1:1 mapping of channel to track at the sequence level
    tracks[channel].add(createNoteOffEvent(tick, channel, note));
  }

  public void addNoteOnEvent(long tick, int channel, int note, int velocity) {
    // We preserve a 1:1 mapping of channel to track at the sequence level
    tracks[channel].add(createNoteOnEvent(tick, channel, note, velocity));
  }

  public void addProgramChange(long tick, int channel, int instrument) {
    // We preserve a 1:1 mapping of channel to track at the sequence level
    tracks[channel].add(createProgramChange(tick, channel, instrument));
  }

  public void append(RandomAccessList<Note> notes) {
    long tickLength = sequence.getTickLength();
    long nextMeasure = roundTickToNextMeasure(tickLength);
    for (Note note : notes) {
      addNote(nextMeasure, note);
    }
  }

  public void clear() {
    for (int i = 0; i < Midi.CHANNELS; i++) {
      while (tracks[i].size() > 0) {
        tracks[i].remove(tracks[i].get(0)); // heaven help us
      }
    }
  }

  public Sequence getSequence() {
    return sequence;
  }

  public void save() {
    try {
      File uniqueFile = fileCreator.createUniqueFile();
      MidiSystem.write(sequence, 1, uniqueFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private MidiEvent createNoteOffEvent(long tick, int channel, int note) {
    return createShortMessageEvent(tick, ShortMessage.NOTE_OFF, channel, note, 0);
  }

  private MidiEvent createNoteOnEvent(long tick, int channel, int note, int velocity) {
    return createShortMessageEvent(tick, ShortMessage.NOTE_ON, channel, note, velocity);
  }

  private MidiEvent createProgramChange(long tick, int channel, int instrument) {
    return createShortMessageEvent(tick, ShortMessage.PROGRAM_CHANGE, channel, instrument, 0);
  }

  private MidiEvent createShortMessageEvent(long tick, int command, int channel, int data1, int data2) {
    try {
      ShortMessage message = new ShortMessage();
      message.setMessage(command, channel, data1, data2);
      MidiEvent event = new MidiEvent(message, tick);
      return event;
    } catch (InvalidMidiDataException e) {
      throw new RuntimeException(e);
    }
  }
}