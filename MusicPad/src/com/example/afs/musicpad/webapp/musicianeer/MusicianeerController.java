// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;
import com.example.afs.musicpad.webapp.musicianeer.MusicianeerView.LedState;
import com.example.afs.musicpad.webapp.musicianeer.OnBrowseSong.BrowseType;
import com.example.afs.musicpad.webapp.musicianeer.OnSetAccompanimentType.AccompanimentType;

public class MusicianeerController extends ControllerTask {

  public static final int MAX_PERCENT_TEMPO = 100;
  public static final int MAX_PERCENT_GAIN = 100;
  private static final int DEFAULT_VELOCITY = 24;

  private boolean isDown;
  private int channel;
  private int midiNote = -1;
  private int expectedMidiNote;
  private Musicianeer musicianeer;
  private MusicianeerView musicianeerView;

  public MusicianeerController(MessageBroker messageBroker) {
    super(messageBroker);
    musicianeerView = new MusicianeerView(this);
    subscribe(OnPlayCurrentSong.class, message -> doSong(message));
    subscribe(OnMidiLibrary.class, message -> doSongLibrary(message));
    subscribe(OnTransportNote.class, message -> doTransportNote(message));
    musicianeer = new Musicianeer(messageBroker);
    musicianeer.tsStart();
  }

  @Override
  protected void doClick(String id) {
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

  @Override
  protected void doInput(String id, String value) {
    switch (id) {
    case "tempo":
      publish(new OnSetPercentTempo(Integer.parseInt(value)));
      break;
    case "instrument":
      publish(new OnProgramOverride(channel, Integer.parseInt(value)));
      break;
    case "song-titles":
      publish(new OnSelectSong(Integer.parseInt(value)));
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
  }

  @Override
  protected void doMouseDown(String id) {
    isDown = true;
    if (id.startsWith("midi-note-")) {
      midiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, midiNote, DEFAULT_VELOCITY));
      if (midiNote == expectedMidiNote) {
        musicianeerView.setLedState(midiNote, LedState.GREEN);
      } else {
        musicianeerView.setLedState(midiNote, LedState.OFF);
      }
    }
  }

  @Override
  protected void doMouseOut(String id) {
    if (midiNote != -1) {
      publish(new OnNoteOff(channel, midiNote));
      midiNote = -1;
    }
  }

  @Override
  protected void doMouseOver(String id) {
    if (isDown && id.startsWith("midi-note-")) {
      midiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, midiNote, DEFAULT_VELOCITY));
    }
  }

  @Override
  protected void doMouseUp(String id) {
    isDown = false;
    if (midiNote != -1) {
      publish(new OnNoteOff(channel, midiNote));
      midiNote = -1;
    }
  }

  private void doSong(OnPlayCurrentSong message) {
    musicianeerView.resetMidiNoteLeds();
    musicianeerView.setSongTitle(message.getCurrentSong().getIndex());
  }

  private void doSongLibrary(OnMidiLibrary message) {
    musicianeerView.displaySongTitles(message.getMidiLibrary());
  }

  private void doTransportNote(OnTransportNote message) {
    int midiNote = message.getMidiNote();
    musicianeerView.setLedState(expectedMidiNote, LedState.OFF);
    musicianeerView.setLedState(midiNote, LedState.RED);
    if (message.getChannel() == channel) {
      expectedMidiNote = midiNote;
    }
  }

}
