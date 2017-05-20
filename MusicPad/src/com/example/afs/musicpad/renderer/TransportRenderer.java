// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.html.Button;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.song.Song;

public class TransportRenderer {

  private Song song;

  public TransportRenderer(Song song) {
    this.song = song;
  }

  public String render() {
    Division detail = new Division();
    detail.setId("transport-detail");

    detail.appendChild(new TextElement("Volume: "));
    detail.appendChild(getGain());
    detail.appendChild(new TextElement("Tempo: "));
    detail.appendChild(getTempo());
    detail.appendChild(new Button("stop", "Stop"));
    detail.appendChild(new Button("play", "Play"));

    String html = detail.render();
    return html;
  }

  private Range getGain() {
    Range range = new Range("gain", 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(Command.GAIN));
    return range;
  }

  private Range getTempo() {
    Range range = new Range("tempo", 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(Command.TEMPO));
    return range;
  }

}
