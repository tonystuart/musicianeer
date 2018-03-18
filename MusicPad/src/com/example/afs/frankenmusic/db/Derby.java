// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.fluidsynth.Synthesizer.Settings;
import com.example.afs.frankenmusic.loader.Neuron;
import com.example.afs.frankenmusic.midi.SequenceBuilder;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.midi.Instruments;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Note.NoteBuilder;
import com.example.afs.musicpad.transport.Transport;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Derby {

  public static class DerbyImpl {

    private int baseTick;
    private int tickLength;
    private int lastEndTick;
    private int lastStartTick;
    private int previousTicks;

    private Transport transport;
    private RandomAccessList<Note> notes;

    public DerbyImpl() {
      Synthesizer synthesizer = createSynthesizer();
      transport = new Transport(synthesizer);
      reset();
    }

    private boolean addNote(Neuron neuron) {
      int duration = neuron.getDuration();
      int tick = neuron.getTick();
      if (tick == -1) {
        tick = lastEndTick;
      }
      if (tick < lastStartTick || tick > (lastEndTick + SequenceBuilder.TICKS_PER_MEASURE)) {
        System.out.println("addNote: recalculating baseTick");
        baseTick = tick;
        previousTicks = tickLength;
      }
      int adjustedTick = previousTicks + (tick - baseTick);
      System.out.println("addNote: baseTick=" + baseTick + ", tick=" + tick + ", previousTicks=" + previousTicks + ", adjustedTick=" + adjustedTick + ", duration=" + duration);
      neuron.setTick(adjustedTick);
      lastStartTick = tick;
      lastEndTick = tick + duration;
      tickLength = Math.max(tickLength, adjustedTick + duration);
      return notes.add(toNote(neuron));
    }

    private int append(int tick, int midiNote, int duration, int velocity, int program, int channel) {
      Neuron neuron = new Neuron();
      neuron.setChannel(channel);
      neuron.setDuration(duration);
      neuron.setNote(midiNote);
      neuron.setProgram(program);
      neuron.setTick(tick);
      neuron.setVelocity(velocity);
      addNote(neuron);
      return notes.size();
    }

    private int copy(int firstId, int lastId) {
      try {
        Connection connection = DriverManager.getConnection("jdbc:default:connection");
        Database database = new Database(connection);
        database.selectAllByClause(neuron -> addNote(neuron), Neuron.class, "where id >= " + firstId + " and id <= " + lastId);
        return notes.size();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    private Synthesizer createSynthesizer() {
      System.loadLibrary(FluidSynth.NATIVE_LIBRARY_NAME);
      int processors = Runtime.getRuntime().availableProcessors();
      System.out.println("Derby.createSynthesizer: processors=" + processors);
      Settings settings = Synthesizer.createDefaultSettings();
      settings.set("synth.midi-channels", Midi.CHANNELS);
      settings.set("synth.cpu-cores", processors);
      Synthesizer synthesizer = new Synthesizer(settings);
      return synthesizer;
    }

    private void play() {
      transport.play(notes);
    }

    private String program(int program) {
      return Instruments.getProgramName(program);
    }

    private void reset() {
      baseTick = 0;
      tickLength = 0;
      lastEndTick = 0;
      lastStartTick = 0;
      previousTicks = 0;
      notes = new DirectList<>();
    }

    private int round(int value, int toNearest) {
      int roundedValue = (((value - (toNearest / 2)) + (toNearest - 1)) / toNearest) * toNearest;
      return roundedValue;
    }

    private void stop() {
      transport.stop();
    }

    private void tempo(int percentTempo) {
      // NB: MusicPad uses 0 to 100% for min to max with 50% for normal
      transport.setPercentTempo(percentTempo / 2);
    }

    private Note toNote(Neuron neuron) {
      Note note = new NoteBuilder() //
          .withChannel(neuron.getChannel()) //
          .withDuration(neuron.getDuration()) //
          .withMidiNote(neuron.getNote()) //
          .withProgram(neuron.getProgram()) //
          .withTick(neuron.getTick()) //
          .withVelocity(neuron.getVelocity()) //
          .create();
      return note;
    }

    private ResultSet transpose(int song, int amount) {
      try {
        Connection connection = DriverManager.getConnection("jdbc:default:connection");
        System.out.println("Database connection is " + connection);
        return null;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static DerbyImpl derbyImpl = new DerbyImpl();

  public static int append(int tick, int note, int duration, int velocity, int program, int channel) {
    return derbyImpl.append(tick, note, duration, velocity, program, channel);
  }

  public static int copy(int firstId, int lastId) {
    return derbyImpl.copy(firstId, lastId);
  }

  public static void play() {
    derbyImpl.play();
  }

  public static String program(int program) {
    return derbyImpl.program(program);
  }

  public static void reset() {
    derbyImpl.reset();
  }

  public static int round(int value, int toNearest) {
    return derbyImpl.round(value, toNearest);
  }

  public static void stop() {
    derbyImpl.stop();
  }

  public static void tempo(int percentTempo) {
    derbyImpl.tempo(percentTempo);
  }

  public static ResultSet transpose(int song, int amount) {
    return derbyImpl.transpose(song, amount);
  }
}
