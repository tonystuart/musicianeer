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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedSet;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Node;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.playable.Playable;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeNotator {
  private Song song;
  private Map<Integer, PlayableIterator> playableIterators;
  private Map<Integer, Playable> deviceSustain = new HashMap<>();

  public KaraokeNotator(Song song, NavigableMap<Integer, RandomAccessList<Playable>> devicePlayables) {
    this.song = song;
    this.playableIterators = new HashMap<>();
    for (Entry<Integer, RandomAccessList<Playable>> entry : devicePlayables.entrySet()) {
      int device = entry.getKey();
      RandomAccessList<Playable> playables = entry.getValue();
      playableIterators.put(device, new PlayableIterator(playables));
    }
  }

  public Division createPrompterList() {
    Division division = new Division();
    long tick = 0;
    while (tick < song.getDuration()) {
      int beatsPerMeasure = song.getBeatsPerMeasure(tick);
      long endTick = tick + (beatsPerMeasure * Default.TICKS_PER_BEAT);
      division.appendChild(createLine(tick, endTick));
      tick = endTick;
    }
    optimize(division);
    return division;
  }

  private Division convertToInterlude(Division line) {
    if (line.getChildCount() > 0) {
      Division prompt = (Division) line.getChild(0);
      String id = prompt.getId();
      long tick = Long.parseLong(id);
      line.clear();
      line.appendChild(createInterlude(tick));
    }
    return line;
  }

  private Node createChannelPrompt(long endTick, int deviceIndex) {
    Division division = new Division(".device-" + deviceIndex);
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

  private Node createInterlude(long tick) {
    String text;
    if (tick == 0) {
      text = "[ Intro ]";
    } else {
      text = "[ Interlude ]";
    }
    Division division = new Division("#" + String.valueOf(tick), ".interlude");
    division.add(new TextElement(text));
    return division;
  }

  private Division createLine(long lineBeginTick, long lineEndTick) {
    Division division = new Division(".line");
    for (long promptBeginTick = lineBeginTick; promptBeginTick < lineEndTick; promptBeginTick += Default.RESOLUTION) {
      long promptEndTick = promptBeginTick + Default.RESOLUTION;
      division.appendChild(createPrompt(promptBeginTick, promptEndTick));
    }
    return division;
  }

  private Division createPrompt(long promptBeginTick, long promptEndTick) {
    Division division = new Division("#" + String.valueOf(promptBeginTick), ".prompt");
    for (int deviceIndex : playableIterators.keySet()) {
      division.appendChild(createChannelPrompt(promptEndTick, deviceIndex));
    }
    division.appendChild(createTextPrompt(promptBeginTick, promptEndTick));
    return division;
  }

  private Division createTextPrompt(long promptBeginTick, long promptEndTick) {
    String text = getWords(promptBeginTick, promptEndTick);
    Division textDivision = new Division(".words");
    textDivision.appendChild(new TextElement(text));
    return textDivision;
  }

  private String getText(SortedSet<Word> words) {
    StringBuilder s = new StringBuilder();
    for (Word word : words) {
      s.append(word.getText());
    }
    return s.toString();
  }

  private String getWords(long promptBeginTick, long promptEndTick) {
    SortedSet<Word> words = song.getWords().subSet(new Word(promptBeginTick), new Word(promptEndTick));
    String text = getText(words);
    if (text.length() > 1) {
      char firstChar = text.charAt(0);
      if (firstChar == '\\') {
        text = text.substring(1);
      } else if (firstChar == '/') {
        text = text.substring(1);
      } else if (firstChar == '@') {
        text = "";
      }
    }
    return text;
  }

  private boolean isSignificant(Division parent) {
    for (Node node : parent) {
      if (node instanceof Division) {
        Division division = (Division) node;
        if (isSignificant(division)) {
          return true;
        }
      } else if (node instanceof TextElement) {
        TextElement textElement = (TextElement) node;
        String text = textElement.getText();
        if (text.length() > 0 && !text.equals(".")) {
          return true;
        }
      }
    }
    return false;
  }

  private void optimize(Division division) {
    Division interlude = null;
    Iterator<Node> iterator = division.iterator();
    while (iterator.hasNext()) {
      Division line = (Division) iterator.next();
      if (!isSignificant(line)) {
        if (interlude == null) {
          interlude = convertToInterlude(line);
        } else {
          iterator.remove();
        }
      } else {
        interlude = null;
      }
    }
  }

}
