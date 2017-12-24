// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.jni.FluidSynth;
import com.example.afs.musicpad.Trace;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.player.PlayableMap.OutputType;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.transport.NoteEvent;
import com.example.afs.musicpad.transport.Transport;

public class Player {

  public enum Action {
    PRESS, RELEASE
  }

  public static final int DRUM_CHANNEL_PROGRAM = -1;
  private static final int UNDEFINED_CHANNEL_PROGRAM = -2;

  private static final int PLAYER_BASE = Midi.CHANNELS;
  private static final int PLAYER_CHANNELS = Midi.CHANNELS;
  public static final int TOTAL_CHANNELS = PLAYER_BASE + PLAYER_CHANNELS;

  private int playerChannel;
  private int percentTempo;
  private int currentProgram;
  private boolean isEnabled = true;
  private int masterProgram = Transport.DEFAULT_MASTER_PROGRAM_OFF;
  private int channelProgram = UNDEFINED_CHANNEL_PROGRAM;

  private Synthesizer synthesizer;
  private Arpeggiator arpeggiator;
  private Sound repeatArpeggiation;
  private Sound queuedArpeggiation;
  private OutputType outputType = OutputType.TICK;

  public Player(Synthesizer synthesizer, int deviceIndex) {
    this.synthesizer = synthesizer;
    this.playerChannel = PLAYER_BASE + deviceIndex;
  }

  public void bendPitch(int pitchBend) {
    synthesizer.bendPitch(playerChannel, pitchBend);
  }

  public void changeControl(int control, int value) {
    synthesizer.changeControl(playerChannel, control, value);
  }

  public int getChannelProgram() {
    return channelProgram;
  }

  public OutputType getOutputType() {
    return outputType;
  }

  public void noteOff(int midiNote, int velocity) {
    synthesizer.releaseKey(playerChannel, midiNote);
  }

  public void noteOn(int midiNote, int velocity) {
    int program;
    if (channelProgram != DRUM_CHANNEL_PROGRAM && masterProgram != Transport.DEFAULT_MASTER_PROGRAM_OFF) {
      program = masterProgram;
    } else {
      program = channelProgram;
    }
    if (program != currentProgram) {
      selectProgram(program);
      currentProgram = program;
    }
    synthesizer.pressKey(playerChannel, midiNote, velocity);
  }

  public void play(Action action, Sound sound, int velocity) {
    if (isEnabled) {
      if (action == Action.PRESS && Trace.isTracePlay()) {
        System.out.println("Player.play: soundType=" + sound);
      }
      switch (outputType) {
      case TICK:
        sendToSynthesizer(action, sound, velocity);
        break;
      case MEASURE:
        sendToArpeggiator(action, sound);
        break;
      default:
        throw new UnsupportedOperationException();
      }
    }
  }

  public void reset() {
    if (arpeggiator != null) {
      arpeggiator.setPercentTempo(100);
    }
  }

  public void setChannelProgram(int program) {
    this.channelProgram = program;
  }

  public void setEnabled(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public void setMasterProgram(int masterProgram) {
    this.masterProgram = masterProgram;
  }

  public void setOutputType(OutputType outputType) {
    this.outputType = outputType;
  }

  public void setPercentTempo(int percentTempo) {
    this.percentTempo = percentTempo;
    if (arpeggiator != null) {
      arpeggiator.setPercentTempo(percentTempo);
    }
  }

  private synchronized void processNoteEvent(NoteEvent noteEvent) {
    Note note = noteEvent.getNote();
    int midiNote = note.getMidiNote();
    int velocity = note.getVelocity();
    switch (noteEvent.getType()) {
    case NOTE_OFF:
      noteOff(midiNote, velocity);
      break;
    case NOTE_ON:
      noteOn(midiNote, velocity);
      break;
    case TICK:
      // TICK fills to end-of-measure
      break;
    default:
      throw new UnsupportedOperationException();
    }
    if (!arpeggiator.isPlaying()) {
      if (repeatArpeggiation != null) {
        arpeggiator.play(repeatArpeggiation);
      } else if (queuedArpeggiation != null) {
        arpeggiator.play(queuedArpeggiation);
        queuedArpeggiation = null;
      }
    }
  }

  private void selectProgram(int program) {
    if (program == DRUM_CHANNEL_PROGRAM) {
      synthesizer.setChannelType(playerChannel, FluidSynth.CHANNEL_TYPE_DRUM);
      synthesizer.changeProgram(playerChannel, 0); // initialize fluid_synth.c channel
    } else {
      synthesizer.setChannelType(playerChannel, FluidSynth.CHANNEL_TYPE_MELODIC);
      synthesizer.changeProgram(playerChannel, program);
    }
  }

  private synchronized void sendToArpeggiator(Action action, Sound sound) {
    if (action == Action.PRESS) {
      if (arpeggiator == null) {
        arpeggiator = new Arpeggiator(noteEvent -> processNoteEvent(noteEvent));
        arpeggiator.tsStart();
        if (percentTempo != 0) {
          arpeggiator.setPercentTempo(percentTempo);
        }
      }
      repeatArpeggiation = sound;
      if (arpeggiator.isPlaying()) {
        queuedArpeggiation = sound;
      } else {
        arpeggiator.play(sound);
      }
    } else {
      repeatArpeggiation = null;
    }
  }

  private void sendToSynthesizer(Action action, Sound sound, int velocity) {
    for (Note note : sound.getNotes()) {
      synthesizeNote(action, note.getMidiNote(), velocity);
    }
  }

  private void synthesizeNote(Action action, int midiNote, int velocity) {
    switch (action) {
    case PRESS:
      noteOn(midiNote, velocity);
      break;
    case RELEASE:
      noteOff(midiNote, velocity);
      break;
    default:
      throw new UnsupportedOperationException();
    }
  }

}
