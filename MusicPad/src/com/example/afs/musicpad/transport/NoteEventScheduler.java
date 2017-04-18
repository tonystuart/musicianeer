// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.transport;

import com.example.afs.musicpad.task.Scheduler;

public class NoteEventScheduler extends TickScheduler implements Scheduler<NoteEvent> {

  public NoteEventScheduler() {
    super(0);
  }

  @Override
  public long getEventTimeMillis(NoteEvent noteEvent) {
    return getEventTimeMillis(noteEvent.getTick(), noteEvent.getBeatsPerMinute());
  }
}