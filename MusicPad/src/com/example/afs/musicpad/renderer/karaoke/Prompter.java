// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.FileUtilities;
import com.example.afs.musicpad.util.RandomAccessList;

public class Prompter {
  private Song song;
  private Map<Integer, Playable> deviceSustain = new HashMap<>();
  private Map<Integer, RandomAccessList<Playable>> devicePlayables;

  public Prompter(Song song, Map<Integer, RandomAccessList<Playable>> devicePlayables) {
    this.song = song;
    this.devicePlayables = devicePlayables;
  }

  public String render() {
    Division division = new Division("#prompter", ".content", ".tab");
    division.appendChild(createLeft());
    division.appendChild(createRight());
    String html = division.render();
    return html;
  }

  private Element createBackToSongsButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Back to Songs"));
    division.appendProperty("onclick", "karaoke.onNewSong()");
    return division;
  }

  private Element createChannel(Map<Integer, PlayableIterator> playableIterators, long endTick, int deviceIndex) {
    Division division = new Division(".player-" + deviceIndex);
    boolean sustain = false;
    Playable playable = deviceSustain.get(deviceIndex);
    if (playable != null) {
      sustain = true;
      if (playable.getEndTick() < endTick) {
        deviceSustain.remove(deviceIndex);
      }
    }
    StringBuilder s = new StringBuilder();
    PlayableIterator playableIterator = playableIterators.get(deviceIndex);
    while ((playable = playableIterator.next(endTick)) != null) {
      if (s.length() > 0) {
        s.append(" ");
      }
      s.append(playable.getLegend());
      if (playable.getEndTick() > endTick) {
        deviceSustain.put(deviceIndex, playable);
      }
    }
    if (s.length() > 0) {
      division.appendChild(new TextElement(s.toString()));
    } else if (sustain) {
      division.appendChild(new TextElement("-"));
    } else {
      division.appendChild(new TextElement("."));
    }
    return division;
  }

  private Element createControls() {
    Division division = new Division(".controls");
    division.appendChild(createBackToSongsButton());
    division.appendChild(createStopButton());
    division.appendChild(createPlayButton());
    return division;
  }

  private Element createDetails() {
    Division division = new Division(".details");
    return division;
  }

  private Element createLeft() {
    Division division = new Division(".left");
    division.appendChild(createTitle());
    division.appendChild(createPrompter());
    division.appendChild(createControls());
    return division;
  }

  private Element createPlayButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Play"));
    division.appendProperty("onclick", "karaoke.onPlay()");
    return division;
  }

  private Division createPrompter() {
    Map<Integer, PlayableIterator> playableIterators = new HashMap<>();
    for (Entry<Integer, RandomAccessList<Playable>> entry : devicePlayables.entrySet()) {
      int device = entry.getKey();
      RandomAccessList<Playable> playables = entry.getValue();
      playableIterators.put(device, new PlayableIterator(playables));
    }
    Division division = new Division("#prompter-list");
    Division stanza = null;
    Division line = null;
    for (long tick = 0; tick < song.getDuration(); tick += Default.RESOLUTION) {
      long endTick = tick + Default.RESOLUTION;
      SortedSet<Word> words = song.getWords().subSet(new Word(tick), new Word(endTick));
      String text = getText(words);
      if (text.length() > 1) {
        char firstChar = text.charAt(0);
        if (firstChar == '\\') {
          //stanza = null;
          //line = null;
          text = text.substring(1);
        } else if (firstChar == '/') {
          //line = null;
          text = text.substring(1);
        } else if (firstChar == '@') {
          text = "";
        }
      }
      boolean isTextPresent = text.length() > 0;
      //if (isTextPresent || isPlayablePresent(playableIterators, endTick)) {
      if (tick % (song.getTicksPerMeasure(tick) * 2) == 0) {
        line = null;
      }
      //if (line != null && line.getChildCount() > 8) {
      //  line = null;
      //}
      if (stanza == null) {
        stanza = new Division(".stanza");
        division.appendChild(stanza);
      }
      if (line == null) {
        line = new Division(".line");
        stanza.appendChild(line);
      }
      Division tickDivision = new Division("#" + String.valueOf(tick), ".tick");
      line.appendChild(tickDivision);
      for (int deviceIndex : devicePlayables.keySet()) {
        tickDivision.appendChild(createChannel(playableIterators, endTick, deviceIndex));
      }
      text = text.replace(" ", "&nbsp;");
      Division textDivision = new Division(".words");
      tickDivision.appendChild(textDivision);
      textDivision.appendChild(new TextElement(text));
      //}
      //if (isLastWordOnLine(words)) {
      //  line = null;
      //}
    }
    return division;
  }

  private Element createRight() {
    Division division = new Division(".right");
    division.appendChild(createDetails());
    return division;
  }

  private Element createStopButton() {
    Division division = new Division();
    division.appendChild(new TextElement("Stop"));
    division.appendProperty("onclick", "karaoke.onStop()");
    return division;
  }

  private Element createTitle() {
    Division division = new Division(".title", FileUtilities.getBaseName(song.getTitle()));
    return division;
  }

  private String getText(SortedSet<Word> words) {
    StringBuilder s = new StringBuilder();
    for (Word word : words) {
      s.append(word.getText());
    }
    return s.toString();
  }

  private boolean isPlayablePresent(Map<Integer, PlayableIterator> playableIterators, long endTick) {
    for (PlayableIterator playableIterator : playableIterators.values()) {
      if (playableIterator.hasNext(endTick)) {
        return true;
      }
    }
    return false;
  }

  private boolean isLastWordOnLine(SortedSet<Word> words) {
    if (words.size() > 0) {
      Word word = song.getWords().higher(words.last());
      if (word != null) {
        String text = word.getText();
        if (text.length() > 0) {
          char firstChar = text.charAt(0);
          if (firstChar == '/' || firstChar == '\\') {
            return true;
          }
        }
      }
    }
    return false;
  }

}
