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
import com.example.afs.musicpad.midi.MidiLibrary;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.musicianeer.MusicianeerView.LedState;
import com.example.afs.musicpad.webapp.musicianeer.OnBrowseSong.BrowseType;
import com.example.afs.musicpad.webapp.musicianeer.OnSetAccompanimentType.AccompanimentType;

public class MusicianeerController extends ControllerTask {

  public static final int MAX_PERCENT_TEMPO = 100;
  public static final int MAX_PERCENT_GAIN = 100;
  private static final int DEFAULT_VELOCITY = 24;

  private int channel;
  private boolean isDown;

  private MusicianeerView musicianeerView;
  private Set<Integer> cueMidiNotes = new HashSet<>();
  private Set<Integer> playerMidiNotes = new HashSet<>();

  public MusicianeerController(MessageBroker messageBroker) {
    super(messageBroker);
    MidiLibrary midiLibrary = request(Services.getMidiLibrary);
    musicianeerView = new MusicianeerView(this, midiLibrary);
    subscribe(OnPlayCurrentSong.class, message -> doPlayCurrentSong(message));
    subscribe(OnMidiLibrary.class, message -> doSongLibrary(message));
    subscribe(OnTransportNoteOn.class, message -> doTransportNoteOn(message));
    subscribe(OnTransportNoteOff.class, message -> doTransportNoteOff(message));
    subscribe(OnCueNoteOn.class, message -> doCueNoteOn(message));
    subscribe(OnCueNoteOff.class, message -> doCueNoteOff(message));
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("song-index-")) {
      publish(new OnSelectSong(Integer.parseInt(id.substring("song-index-".length()))));
    } else if (id.startsWith("channel-index-")) {
      channel = Integer.parseInt(id.substring("channel-index-".length()));
      musicianeerView.resetMidiNoteLeds();
      musicianeerView.selectChannel(channel);
    } else {
      switch (id) {
      case "drums":
        publish(new OnSetAccompanimentType(AccompanimentType.DRUMS));
        break;
      case "full":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.FULL));
        break;
      case "next-song":
        publish(new OnBrowseSong(BrowseType.NEXT));
        break;
      case "next-page":
        publish(new OnBrowseSong(OnBrowseSong.BrowseType.NEXT_PAGE));
        break;
      case "piano":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.PIANO));
        break;
      case "play":
        publish(new OnPlay());
        break;
      case "previous-page":
        publish(new OnBrowseSong(OnBrowseSong.BrowseType.PREVIOUS_PAGE));
        break;
      case "previous-song":
        publish(new OnBrowseSong(OnBrowseSong.BrowseType.PREVIOUS));
        break;
      case "rhythm":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.RHYTHM));
        break;
      case "solo":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.SOLO));
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
    switch (id) {
    case "tempo":
      publish(new OnSetPercentTempo(Integer.parseInt(value)));
      break;
    case "instrument":
      publish(new OnProgramOverride(channel, Integer.parseInt(value)));
      break;
    case "volume":
      publish(new OnSetPercentMasterGain(Integer.parseInt(value)));
      break;
    }
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", musicianeerView.render()));
    musicianeerView.setAlternative("full");
    CurrentSong currentSong = request(Services.getCurrentSong);
    if (currentSong != null) {
      playCurrentSong(currentSong);
    }
  }

  @Override
  protected void doMouseDown(String id) {
    isDown = true;
    if (id.startsWith("midi-note-")) {
      int mouseMidiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, mouseMidiNote, DEFAULT_VELOCITY));
      playerMidiNotes.add(mouseMidiNote);
      if (cueMidiNotes.contains(mouseMidiNote)) {
        musicianeerView.setLedState(mouseMidiNote, LedState.GREEN);
      }
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

  private void doCueNoteOff(OnCueNoteOff message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      cueMidiNotes.remove(midiNote);
      musicianeerView.setLedState(midiNote, LedState.BLUE);
    }
  }

  private void doCueNoteOn(OnCueNoteOn message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      cueMidiNotes.add(midiNote);
      musicianeerView.setLedState(midiNote, LedState.YELLOW);
    }
  }

  private void doPlayCurrentSong(OnPlayCurrentSong message) {
    playCurrentSong(message.getCurrentSong());
  }

  private void doSongLibrary(OnMidiLibrary message) {
    musicianeerView.renderSongList(message.getMidiLibrary());
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

  private void playCurrentSong(CurrentSong currentSong) {
    musicianeerView.resetMidiNoteLeds();
    musicianeerView.selectSong(currentSong.getIndex());
    musicianeerView.renderSongDetails(new SongInfo(currentSong.getSong()));
  }

}
