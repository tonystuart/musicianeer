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

  public TransportRenderer(Song song) {
  }

  public String render() {
    Division detail = new Division();
    detail.setId("transport-detail");

    detail.appendChild(new TextElement("Master Volume: "));
    detail.appendChild(getGain());
    detail.appendChild(new TextElement("Backing Volume: "));
    detail.appendChild(getBacking());
    detail.appendChild(new TextElement("Tempo: "));
    detail.appendChild(getTempo());
    detail.appendChild(getBackward());
    detail.appendChild(getForward());
    detail.appendChild(getStop());
    detail.appendChild(getPlay());

    String html = detail.render();
    return html;
  }

  private Range getBacking() {
    Range range = new Range("backing", 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(Command.BACKING));
    return range;
  }

  private Button getBackward() {
    Button button = new Button("backward", "Backward");
    button.appendProperty("onclick", PropertyRenderer.render(Command.BACKWARD, 0));
    return button;
  }

  private Button getForward() {
    Button button = new Button("forward", "Forward");
    button.appendProperty("onclick", PropertyRenderer.render(Command.FORWARD, 0));
    return button;
  }

  private Range getGain() {
    Range range = new Range("gain", 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(Command.GAIN));
    return range;
  }

  private Button getPlay() {
    Button button = new Button("play", "Play");
    button.appendProperty("onclick", PropertyRenderer.render(Command.PLAY_PAUSE, 0));
    return button;
  }

  private Button getStop() {
    Button button = new Button("stop", "Stop");
    button.appendProperty("onclick", PropertyRenderer.render(Command.STOP_PAUSE, 0));
    return button;
  }

  private Range getTempo() {
    Range range = new Range("tempo", 0, 127, 1, 64);
    range.appendProperty("oninput", PropertyRenderer.render(Command.TEMPO));
    return range;
  }

}
