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

    private Transport transport;
    private RandomAccessList<Note> notes;

    public DerbyImpl() {
      Synthesizer synthesizer = createSynthesizer();
      transport = new Transport(synthesizer);
      reset();
    }

    public void play() {
      transport.play(notes);
    }

    public void reset() {
      notes = new DirectList<>();
    }

    private String append(int tick, int midiNote, int duration, int velocity, int program, int channel) {
      Note note = new NoteBuilder() //
          .withTick(tick) //
          .withMidiNote(midiNote) //
          .withDuration(duration) //
          .withVelocity(velocity) //
          .withProgram(program) //
          .withChannel(channel) //
          .create();
      notes.add(note);
      return note.toString();
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

    private String program(int program) {
      return Instruments.getProgramName(program);
    }

    private int round(int value, int toNearest) {
      int roundedValue = (((value - (toNearest / 2)) + (toNearest - 1)) / toNearest) * toNearest;
      return roundedValue;
    }

    private void stop() {
      transport.stop();
    }

    private void tempo(int percentTempo) {
      transport.setPercentTempo(percentTempo);
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

  public static String append(int tick, int note, int duration, int velocity, int program, int channel) {
    return derbyImpl.append(tick, note, duration, velocity, program, channel);
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
