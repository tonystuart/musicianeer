// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.html.Button;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.song.Song;

public class TransportRenderer {

  private Song song;

  public TransportRenderer(Song song) {
    this.song = song;
  }

  public String render() {
    Division detail = new Division();
    detail.setId("transport-detail");

    detail.appendChild(new Button("tempo-factor", "Tempo Factor"));
    detail.appendChild(new Button("master-volume", "Master Volume"));
    detail.appendChild(new Button("previous", "Previous"));
    detail.appendChild(new Button("next", "Next"));
    detail.appendChild(new Button("stop", "Stop"));
    detail.appendChild(new Button("play", "Play"));

    StringBuilder s = new StringBuilder();
    detail.render(s);
    return s.toString();
  }

}
