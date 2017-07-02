// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.task.PausibleSequencerTask;
import com.example.afs.musicpad.util.Broker;

public class NoteEventSequencer extends PausibleSequencerTask<NoteEvent> {

  public interface NoteEventProcessor {
    void processNoteEvent(NoteEvent noteEvent);
  }

  public NoteEventSequencer(NoteEventProcessor noteEventProcessor) {
    super(new NoteEventScheduler(), new Broker<>());
    subscribe(NoteEvent.class, noteEvent -> noteEventProcessor.processNoteEvent(noteEvent));
  }

  public int getPercentTempo() {
    return getScheduler().getPercentTempo();
  }

  @Override
  public NoteEventScheduler getScheduler() {
    return (NoteEventScheduler) super.getScheduler();
  }

  public long getTick() {
    return getScheduler().getTick();
  }

  public void reset() {
    getInputQueue().clear();
    setPaused(false);
    getScheduler().resetAll();
  }

  @Override
  public void setPaused(boolean isPaused) {
    super.setPaused(isPaused);
    if (isPaused) {
      getScheduler().resetBaseTime();
    }
  }

  public void setPercentTempo(int percentTempo) {
    getScheduler().setPercentTempo(percentTempo);
  }

}
