// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.Map;
import java.util.Map.Entry;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnStaffPrompter;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.util.RandomAccessList;

public class StaffPrompter implements PrompterFactory.Prompter {

  private Song song;
  private Map<Integer, RandomAccessList<Playable>> devicePlayables;

  public StaffPrompter(Song song, Map<Integer, RandomAccessList<Playable>> devicePlayables) {
    this.song = song;
    this.devicePlayables = devicePlayables;
  }

  @Override
  public Message getMessage() {
    String html = render();
    return new OnStaffPrompter(html, PlayableStaff.TICKS_PER_PIXEL);
  }

  public String render() {
    //Division division = new Division("#channel-notators");
    Division division = new Division("#prompter", ".content", ".tab", ".channel-notators");
    division.appendChild(new Division("#notator-cursor"));
    division.appendChild(getNotatorScroller());
    String html = division.render();
    return html;
  }

  private Division getNotator(Entry<Integer, RandomAccessList<Playable>> entry) {
    Division division = new Division(".notator");
    PlayableStaff playableStaff = new PlayableStaff(song, entry.getValue());
    division.appendChild(playableStaff.getStaff());
    return division;
  }

  private Division getNotatorScroller() {
    Division division = new Division("#notator-scroller");
    for (Entry<Integer, RandomAccessList<Playable>> entry : devicePlayables.entrySet()) {
      division.appendChild(getNotator(entry));
    }
    return division;
  }
}
