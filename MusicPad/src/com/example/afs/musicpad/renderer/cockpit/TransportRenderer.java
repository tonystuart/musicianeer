// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.cockpit;

import com.example.afs.musicpad.Command;
import com.example.afs.musicpad.html.Button;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Range;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.renderer.CommandRenderer;
import com.example.afs.musicpad.song.ChannelNotes;
import com.example.afs.musicpad.song.Song;

public class TransportRenderer {

  public TransportRenderer(Song song) {
  }

  public String render() {
    Division detail = new Division();
    detail.setId("transport-detail");

    detail.appendChild(getLeft());
    detail.appendChild(getRight());

    String html = detail.render();
    return html;
  }

  private Division getBackingDivision() {
    Division division = new Division();
    division.appendChild(new TextElement("Backing Volume: "));
    division.appendChild(getBackingRange());
    return division;
  }

  private Range getBackingRange() {
    Range range = new Range("backing", 0, 127, 1, 64);
    range.appendProperty("oninput", CommandRenderer.render(Command.VELOCITY));
    return range;
  }

  private Button getBackward() {
    Button button = new Button("backward", "Backward");
    button.appendProperty("onclick", CommandRenderer.render(Command.BACKWARD, 0));
    return button;
  }

  private Button getForward() {
    Button button = new Button("forward", "Forward");
    button.appendProperty("onclick", CommandRenderer.render(Command.FORWARD, 0));
    return button;
  }

  private Division getGainDivision() {
    Division division = new Division();
    division.appendChild(new TextElement("Master Volume: "));
    division.appendChild(getGainRange());
    return division;
  }

  private Range getGainRange() {
    Range range = new Range("gain", 0, 127, 1, 64);
    range.appendProperty("oninput", CommandRenderer.render(Command.GAIN));
    return range;
  }

  private Division getLeft() {
    Division division = new Division("#transport-left");
    division.appendChild(getGainDivision());
    division.appendChild(getBackingDivision());
    division.appendChild(getTempoDivision());
    return division;
  }

  private Button getPlay() {
    Button button = new Button("play", "Play");
    button.appendProperty("onclick", CommandRenderer.render(Command.PLAY, ChannelNotes.ALL_CHANNELS));
    return button;
  }

  private Button getReattach() {
    Button button = new Button("reattach", "Reattach");
    button.appendProperty("onclick", CommandRenderer.render(Command.REATTACH, 0));
    return button;
  }

  private Division getRight() {
    Division division = new Division("#transport-right");
    division.appendChild(getReattach());
    division.appendChild(getBackward());
    division.appendChild(getForward());
    division.appendChild(getStop());
    division.appendChild(getPlay());
    return division;
  }

  private Button getStop() {
    Button button = new Button("stop", "Stop");
    button.appendProperty("onclick", CommandRenderer.render(Command.STOP, 0));
    return button;
  }

  private Division getTempoDivision() {
    Division division = new Division();
    division.appendChild(new TextElement("Tempo: "));
    division.appendChild(getTempoRange());
    return division;
  }

  private Range getTempoRange() {
    Range range = new Range("tempo", 0, 127, 1, 64);
    range.appendProperty("oninput", CommandRenderer.render(Command.TEMPO));
    return range;
  }

}
