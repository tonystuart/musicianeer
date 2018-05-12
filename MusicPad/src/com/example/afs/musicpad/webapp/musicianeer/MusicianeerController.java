// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import java.util.HashSet;
import java.util.Set;

import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.midi.Midi;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.musicianeer.MusicianeerView.LedState;
import com.example.afs.musicpad.webapp.musicianeer.OnSetAccompanimentType.AccompanimentType;
import com.example.afs.musicpad.webapp.musicianeer.SongInfoFactory.SongInfo;

public class MusicianeerController extends ControllerTask {

  public static final int MAX_PERCENT_TEMPO = 100;
  public static final int MAX_PERCENT_GAIN = 100;
  private static final int DEFAULT_VELOCITY = 24;

  private int channel;
  private int loadIndex;
  private boolean isDown;
  private int transposition;
  private int inputDeviceIndex = MidiHandle.MIDI_HANDLE_NA;

  private CurrentSong currentSong;
  private MusicianeerView musicianeerView;
  private Set<Integer> playerMidiNotes = new HashSet<>();

  public MusicianeerController(MessageBroker messageBroker) {
    super(messageBroker);
    musicianeerView = new MusicianeerView(this);
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("song-index-")) {
      publish(new OnSelectSong(Integer.parseInt(id.substring("song-index-".length()))));
    } else if (id.startsWith("channel-index-")) {
      renderChannel(Integer.parseInt(id.substring("channel-index-".length())));
    } else {
      switch (id) {
      case "drums":
        publish(new OnSetAccompanimentType(AccompanimentType.DRUMS));
        break;
      case "full":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.FULL));
        break;
      case "piano":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.PIANO));
        break;
      case "play":
        publish(new OnPlay());
        break;
      case "rhythm":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.RHYTHM));
        break;
      case "stop":
        publish(new OnStop());
        musicianeerView.resetMidiNoteLeds();
        break;
      }
    }
  }

  @Override
  protected void doInput(String id, String value) {
    if (id.startsWith("channel-mute-")) {
      renderMute(Integer.parseInt(id.substring("channel-mute-".length())), Integer.parseInt(value));
    } else if (id.startsWith("channel-solo-")) {
      renderSolo(Integer.parseInt(id.substring("channel-solo-".length())), Integer.parseInt(value));
    } else if (id.equals("midi-input")) {
      inputDeviceIndex = Integer.parseInt(value);
      publish(new OnMidiInputSelected(channel, inputDeviceIndex));
    }
    switch (id) {
    case "instrument":
      publish(new OnProgramOverride(channel, Integer.parseInt(value)));
      break;
    case "tempo":
      publish(new OnSetPercentTempo(Integer.parseInt(value)));
      break;
    case "transposition":
      publish(new OnTransposition(Integer.parseInt(value)));
      break;
    case "volume":
      publish(new OnSetPercentMasterGain(Integer.parseInt(value)));
      break;
    }
  }

  @Override
  protected void doLoad() {
    // NB: We are single threaded by virtue of our input queue
    subscribe(OnMute.class, message -> doMute(message));
    subscribe(OnSolo.class, message -> doSolo(message));
    subscribe(OnSongInfo.class, message -> doSongInfo(message));
    subscribe(OnMidiHandles.class, message -> doMidiHandles(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message));
    subscribe(OnTransposition.class, message -> doTransposition(message));
    subscribe(OnTransportPlay.class, message -> doTransportPlay(message));
    subscribe(OnTransportNoteOn.class, message -> doTransportNoteOn(message));
    subscribe(OnTransportNoteOff.class, message -> doTransportNoteOff(message));
    subscribe(OnCueNoteOn.class, message -> doCueNoteOn(message));
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", musicianeerView.render()));
    musicianeerView.setAlternative("full");
    Iterable<SongInfo> songInfoList = request(Services.getSongInfoList);
    for (SongInfo songInfo : songInfoList) {
      renderSongInfo(songInfo);
      loadIndex = songInfo.getSongIndex();
    }
    CurrentSong initialSong = request(Services.getCurrentSong);
    if (initialSong != null) {
      initializeCurrentSong(initialSong);
      initializeSynthesizerSettings();
    }
    renderMidiHandles(request(Services.getMidiHandles));
  }

  @Override
  protected void doMouseDown(String id) {
    isDown = true;
    if (id.startsWith("midi-note-")) {
      int mouseMidiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, mouseMidiNote, DEFAULT_VELOCITY));
      playerMidiNotes.add(mouseMidiNote);
    }
  }

  @Override
  protected void doMouseOut(String id) {
    playerMidiNotes.forEach(midiNote -> publish(new OnNoteOff(channel, midiNote)));
    playerMidiNotes.clear();
  }

  @Override
  protected void doMouseOver(String id) {
    if (isDown && id.startsWith("midi-note-")) {
      int mouseMidiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, mouseMidiNote, DEFAULT_VELOCITY));
      playerMidiNotes.add(mouseMidiNote);
    }
  }

  @Override
  protected void doMouseUp(String id) {
    isDown = false;
    playerMidiNotes.forEach(midiNote -> publish(new OnNoteOff(channel, midiNote)));
    playerMidiNotes.clear();
  }

  private void doCueNoteOn(OnCueNoteOn message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      musicianeerView.setLedState(midiNote, LedState.YELLOW);
    }
  }

  private void doMidiHandles(OnMidiHandles message) {
    renderMidiHandles(message.getMidiHandles());
  }

  private void doMute(OnMute message) {
    musicianeerView.setMute(message.getChannel(), message.isMute());
  }

  private void doSolo(OnSolo message) {
    musicianeerView.setSolo(message.getChannel(), message.isSolo());
  }

  private void doSongInfo(OnSongInfo message) {
    // Exclude songs received after listening but before processing initial load
    if (message.getSongInfo().getSongIndex() > loadIndex) {
      renderSongInfo(message.getSongInfo());
    } else {
      System.err.println("Discarding duplicate songInfo received during initial load " + message.getSongInfo());
    }
  }

  private void doSongSelected(OnSongSelected message) {
    initializeCurrentSong(message.getCurrentSong());
  }

  private void doTransportNoteOff(OnTransportNoteOff message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      musicianeerView.setLedState(midiNote, LedState.OFF);
    }
  }

  private void doTransportNoteOn(OnTransportNoteOn message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      if (!playerMidiNotes.contains(midiNote)) {
        musicianeerView.setLedState(midiNote, LedState.RED);
      }
    }
  }

  private void doTransportPlay(OnTransportPlay message) {
    playCurrentSong(message.getCurrentSong());
  }

  private void doTransposition(OnTransposition message) {
    this.transposition = message.getTransposition();
    musicianeerView.renderStaff(currentSong.getSong(), channel, transposition);
    musicianeerView.resetMidiNoteLeds();
  }

  private void initializeCurrentSong(CurrentSong currentSong) {
    this.currentSong = currentSong;
    SongInfo songInfo = currentSong.getSongInfo();
    this.transposition = songInfo.getEasyTransposition();
    musicianeerView.resetMidiNoteLeds();
    musicianeerView.selectSong(songInfo.getSongIndex());
    musicianeerView.renderSongDetails(currentSong);
    renderChannel(currentSong.getSongInfo().getActiveChannels()[0]);
  }

  private void initializeSynthesizerSettings() {
    SynthesizerSettings synthesizerSettings = request(Services.getSynthesizerSettings);
    boolean[] muteSettings = synthesizerSettings.getMuteSettings();
    boolean[] soloSettings = synthesizerSettings.getSoloSettings();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      boolean isMute = muteSettings[channel];
      if (isMute) {
        musicianeerView.setMute(channel, isMute);
      }
      boolean isSolo = soloSettings[channel];
      if (isSolo) {
        musicianeerView.setSolo(channel, isSolo);
      }
    }
  }

  private void playCurrentSong(CurrentSong currentSong) {
    musicianeerView.resetMidiNoteLeds();
  }

  private void renderChannel(int channel) {
    this.channel = channel;
    musicianeerView.resetMidiNoteLeds();
    musicianeerView.selectChannel(currentSong.getSong(), channel, transposition);
    CurrentPrograms currentPrograms = request(Services.getCurrentPrograms);
    if (currentPrograms != null) {
      int currentProgram;
      if (channel == Midi.DRUM) {
        currentProgram = OnProgramOverride.DEFAULT;
      } else {
        currentProgram = currentPrograms.getPrograms()[channel];
      }
      musicianeerView.setProgram(currentProgram);
    }
    if (inputDeviceIndex != MidiHandle.MIDI_HANDLE_NA) {
      publish(new OnMidiInputSelected(channel, inputDeviceIndex));
    }
  }

  private void renderMidiHandles(Iterable<MidiHandle> midiHandles) {
    musicianeerView.renderMidiHandles(midiHandles);
  }

  private void renderMute(int channel, int value) {
    boolean isMute = value > 0;
    publish(new OnMute(channel, isMute));
  }

  private void renderSolo(int channel, int value) {
    boolean isSolo = value > 0;
    publish(new OnSolo(channel, isSolo));
  }

  private void renderSongInfo(SongInfo songInfo) {
    musicianeerView.renderSongInfo(songInfo);
  }

}
