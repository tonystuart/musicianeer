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
import com.example.afs.musicpad.webapp.musicianeer.Musicianeer.AccompanimentType;
import com.example.afs.musicpad.webapp.musicianeer.Musicianeer.SelectType;
import com.example.afs.musicpad.webapp.musicianeer.Musicianeer.TrackingType;
import com.example.afs.musicpad.webapp.musicianeer.MusicianeerView.LedState;

public class MusicianeerController extends ControllerTask {

  public static final int DEFAULT_PERCENT_TEMPO = 100;
  public static final int DEFAULT_PERCENT_GAIN = 50;

  private boolean isDown;
  private int midiNote = -1;
  private int lastMidiNote;
  private Musicianeer musicianeer;
  private MusicianeerView musicianeerView;

  public MusicianeerController(MessageBroker messageBroker) {
    super(messageBroker);
    musicianeerView = new MusicianeerView(this);
    subscribe(OnHit.class, message -> doHit(message));
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnMelodyNote.class, message -> doMelodyNote(message));
    subscribe(OnMidiLibrary.class, message -> doSongLibrary(message));
    musicianeer = new Musicianeer(messageBroker);
    musicianeer.tsStart();
  }

  @Override
  protected void doClick(String id) {
    switch (id) {
    case "drums":
      musicianeer.setAccompaniment(AccompanimentType.DRUMS);
      break;
    case "follow":
      musicianeer.setTracking(TrackingType.FOLLOW);
      break;
    case "full":
      musicianeer.setAccompaniment(AccompanimentType.FULL);
      break;
    case "lead":
      musicianeer.setTracking(TrackingType.LEAD);
      break;
    case "next-song":
      musicianeer.selectSong(SelectType.NEXT);
      break;
    case "next-page":
      musicianeer.selectSong(SelectType.NEXT_PAGE);
      break;
    case "piano":
      musicianeer.setAccompaniment(AccompanimentType.PIANO);
      break;
    case "play":
      musicianeer.play();
      break;
    case "previous-page":
      musicianeer.selectSong(SelectType.PREVIOUS_PAGE);
      break;
    case "previous-song":
      musicianeer.selectSong(SelectType.PREVIOUS);
      break;
    case "rhythm":
      musicianeer.setAccompaniment(AccompanimentType.RHYTHM);
      break;
    case "solo":
      musicianeer.setAccompaniment(AccompanimentType.SOLO);
      break;
    case "stop":
      musicianeer.stop();
      musicianeerView.resetMidiNoteLeds();
      break;
    }
  }

  @Override
  protected void doInput(String id, String value) {
    switch (id) {
    case "tempo":
      musicianeer.setPercentTempo(Integer.parseInt(value));
      break;
    case "instrument":
      musicianeer.setProgramOverride(Integer.parseInt(value));
      break;
    case "song-titles":
      publish(new OnSongIndex(Integer.parseInt(value)));
      break;
    case "volume":
      musicianeer.setPercentGain(Integer.parseInt(value));
      break;
    }
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", musicianeerView.render()));
    musicianeerView.setAlternative("lead");
    musicianeerView.setAlternative("full");
    musicianeer.setPercentGain(DEFAULT_PERCENT_TEMPO);
    musicianeer.setPercentGain(DEFAULT_PERCENT_GAIN);
    musicianeer.loadInitialSong();
  }

  @Override
  protected void doMouseDown(String id) {
    isDown = true;
    if (id.startsWith("midi-note-")) {
      midiNote = Integer.parseInt(id.substring("midi-note-".length()));
      musicianeer.press(midiNote);
      musicianeerView.setLedState(midiNote, LedState.OFF);
    }
  }

  @Override
  protected void doMouseOut(String id) {
    if (midiNote != -1) {
      musicianeer.release(midiNote);
      midiNote = -1;
    }
  }

  @Override
  protected void doMouseOver(String id) {
    if (isDown && id.startsWith("midi-note-")) {
      midiNote = Integer.parseInt(id.substring("midi-note-".length()));
      musicianeer.press(midiNote);
    }
  }

  @Override
  protected void doMouseUp(String id) {
    isDown = false;
    if (midiNote != -1) {
      musicianeer.release(midiNote);
      midiNote = -1;
    }
  }

  private void doHit(OnHit message) {
    musicianeerView.setLedState(message.getMidiNote(), LedState.GREEN);
  }

  private void doMelodyNote(OnMelodyNote message) {
    int midiNote = message.getMidiNote();
    musicianeerView.setLedState(lastMidiNote, LedState.OFF);
    musicianeerView.setLedState(midiNote, LedState.RED);
    lastMidiNote = midiNote;
  }

  private void doSong(OnSong message) {
    musicianeerView.resetMidiNoteLeds();
    musicianeerView.setSongTitle(message.getIndex());
  }

  private void doSongLibrary(OnMidiLibrary message) {
    musicianeerView.displaySongTitles(message.getMidiLibrary());
  }

}
