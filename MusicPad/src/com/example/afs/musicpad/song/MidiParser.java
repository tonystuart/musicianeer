package com.example.afs.musicpad.song;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.example.afs.musicpad.song.Group.NoteProperties;

public class MidiParser {

  public interface Listener {

    void onBegin(String fileName);

    void onChannelUtilization(int channel, int occupancy, int concurrency);

    void onEnd(String fileName);

    void onLyrics(long tick, String lyrics);

    void onNote(long tick, int channel, int note, int velocity, long duration, int instrument, int group);

    void onTempoChange(long tick, int usecPerQuarterNote, int quarterNotesPerMinute);

    void onText(long tick, String text);

    void onTimeSignatureChange(long tick, int beatsPerMeasure, int beatUnit);

  }

  private static final int USEC_PER_MINUTE = 60000000;

  private int groupIndex;
  private Listener listener;
  private int defaultResolution;
  private int[] instruments = new int[Midi.CHANNELS];
  private Group group = new Group();

  public MidiParser(Listener listener, int defaultResolution) {
    this.listener = listener;
    this.defaultResolution = defaultResolution;
  }

  public void parse(String fileName) {
    try {
      File file = new File(fileName);
      Sequence sequence = MidiSystem.getSequence(file);
      listener.onBegin(fileName);
      parse(sequence);
      for (int channel = 0; channel < Midi.CHANNELS; channel++) {
        listener.onChannelUtilization(channel, group.getOccupancy(channel), group.getConcurrency(channel));
      }
      listener.onEnd(fileName);
    } catch (InvalidMidiDataException | IOException e) {
      throw new RuntimeException("Cannot parse " + fileName, e);
    }
  }

  private void parse(Sequence sequence) {
    int resolution = sequence.getResolution();
    double resolutionScale = (double) defaultResolution / resolution;
    for (Track track : sequence.getTracks()) {
      int eventCount = track.size();
      for (int i = 0; i < eventCount; i++) {
        MidiEvent midiEvent = track.get(i);
        MidiMessage midiMessage = midiEvent.getMessage();
        long tick = midiEvent.getTick();
        long normalizedTick = (long) (resolutionScale * tick);
        processMessage(normalizedTick, midiMessage);
      }
    }
  }

  private void processMessage(long tick, MidiMessage midiMessage) {
    if (midiMessage instanceof ShortMessage) {
      processShortMessage(tick, (ShortMessage) midiMessage);
    } else if (midiMessage instanceof MetaMessage) {
      processMetaMessage(tick, (MetaMessage) midiMessage);
    }
  }

  private void processMetaMessage(long tick, MetaMessage message) {
    int type = message.getType();
    if (type == Midi.MM_TEMPO) {
      byte[] bytes = message.getMessage();
      int usecPerQuarterNote = ((bytes[3] & 0xff) << 16) | ((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff);
      int quarterNotesPerMinute = USEC_PER_MINUTE / usecPerQuarterNote;
      listener.onTempoChange(tick, usecPerQuarterNote, quarterNotesPerMinute);
    } else if (type == Midi.MM_TIME_SIGNATURE) {
      byte[] bytes = message.getMessage();
      int beatsPerMeasure = bytes[3] & 0xff;
      int beatUnit = 1 << (bytes[4] & 0xff);
      listener.onTimeSignatureChange(tick, beatsPerMeasure, beatUnit);
    } else if (type == Midi.MM_LYRIC) {
      byte[] data = message.getData();
      String lyrics = new String(data);
      listener.onLyrics(tick, lyrics);
    } else if (type == Midi.MM_TEXT) {
      byte[] data = message.getData();
      String text = new String(data);
      listener.onText(tick, text);
    }
  }

  private void processNoteOff(long tick, ShortMessage message) {
    int channel = message.getChannel();
    int note = message.getData1();
    NoteProperties noteProperties = group.get(channel, note);
    if (noteProperties != null) {
      int instrument = noteProperties.getInstrument();
      int velocity = noteProperties.getVelocity();
      long start = noteProperties.getTick();
      long duration = tick - start;
      if (duration > 0) {
        listener.onNote(start, channel, note, velocity, duration, instrument, groupIndex);
        group.remove(tick, channel, note);
        if (group.allNotesAreOff(channel)) {
          groupIndex++;
        }
      }
    }
  }

  private void processNoteOn(long tick, ShortMessage message) {
    int note = message.getData1();
    int velocity = message.getData2();
    int channel = message.getChannel();
    if (velocity == 0) {
      processNoteOff(tick, message);
      return;
    }
    if (group.get(channel, note) != null) {
      processNoteOff(tick, message);
    }
    group.add(tick, channel, note, velocity, instruments[channel]);
  }

  private void processShortMessage(long tick, ShortMessage message) {
    int command = message.getCommand();
    if (command == ShortMessage.NOTE_OFF) {
      processNoteOff(tick, message);
    } else if (command == ShortMessage.NOTE_ON) {
      processNoteOn(tick, message);
    } else if (command == ShortMessage.PROGRAM_CHANGE) {
      int instrument = message.getData1();
      int channel = message.getChannel();
      instruments[channel] = instrument;
    }
  }

}
