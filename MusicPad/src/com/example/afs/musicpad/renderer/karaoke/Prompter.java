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
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.RandomAccessList;

public class Prompter {
  private Song song;
  private Map<Integer, KeyCap> deviceSustain = new HashMap<>();
  private Map<Integer, RandomAccessList<KeyCap>> deviceKeyCaps;

  public Prompter(Song song, Map<Integer, RandomAccessList<KeyCap>> deviceKeyCaps) {
    this.song = song;
    this.deviceKeyCaps = deviceKeyCaps;
  }

  public String render() {
    Division prompter = new Division("#prompter");
    prompter.appendChild(createPrompter());
    String html = prompter.render();
    return html;
  }

  private Division createPrompter() {
    Map<Integer, KeyCapIterator> keyCapIterators = new HashMap<>();
    for (Entry<Integer, RandomAccessList<KeyCap>> entry : deviceKeyCaps.entrySet()) {
      int device = entry.getKey();
      RandomAccessList<KeyCap> keyCaps = entry.getValue();
      keyCapIterators.put(device, new KeyCapIterator(keyCaps));
    }
    Division container = new Division();
    Division stanza = null;
    Division line = null;
    for (long tick = 0; tick < song.getDuration(); tick += Default.RESOLUTION) {
      long endTick = tick + Default.RESOLUTION;
      SortedSet<Word> words = song.getWords().subSet(new Word(tick), new Word(endTick));
      String text = getText(words);
      if (text.length() > 1) {
        char firstChar = text.charAt(0);
        if (firstChar == '\\') {
          stanza = null;
          line = null;
          text = text.substring(1);
        } else if (firstChar == '/') {
          line = null;
          text = text.substring(1);
        } else if (firstChar == '@') {
          text = "";
        }
      }
      boolean isTextPresent = text.length() > 0;
      if (isTextPresent || isKeyCapPresent(keyCapIterators, endTick)) {
        if (line != null && line.getChildCount() > 32) {
          line = null;
        }
        if (stanza == null) {
          stanza = new Division();
          stanza.setClassName("stanza");
          container.appendChild(stanza);
        }
        if (line == null) {
          line = new Division();
          line.setClassName("line");
          stanza.appendChild(line);
        }
        Division tickDivision = new Division("#" + String.valueOf(tick));
        tickDivision.setClassName("tick");
        line.appendChild(tickDivision);
        for (int deviceIndex : deviceKeyCaps.keySet()) {
          Division channelDivision = new Division();
          channelDivision.setClassName("channel");
          tickDivision.appendChild(channelDivision);
          boolean sustain = false;
          KeyCap keyCap = deviceSustain.get(deviceIndex);
          if (keyCap != null) {
            sustain = true;
            if (keyCap.getEndTick() < endTick) {
              deviceSustain.remove(deviceIndex);
            }
          }
          StringBuilder s = new StringBuilder();
          KeyCapIterator keyCapIterator = keyCapIterators.get(deviceIndex);
          while ((keyCap = keyCapIterator.next(endTick)) != null) {
            if (s.length() > 0) {
              s.append(" ");
            }
            s.append(keyCap.getLegend());
            if (keyCap.getEndTick() > endTick) {
              deviceSustain.put(deviceIndex, keyCap);
            }
          }
          if (s.length() > 0) {
            channelDivision.appendChild(new TextElement(s.toString()));
          } else if (sustain) {
            channelDivision.appendChild(new TextElement("-"));
          }
        }
        text = text.replace(" ", "&nbsp;");
        Division textDivision = new Division();
        textDivision.setClassName("words");
        tickDivision.appendChild(textDivision);
        textDivision.appendChild(new TextElement(text));
      }
    }
    return container;
  }

  private String getText(SortedSet<Word> words) {
    StringBuilder s = new StringBuilder();
    for (Word word : words) {
      s.append(word.getText());
    }
    return s.toString();
  }

  private boolean isKeyCapPresent(Map<Integer, KeyCapIterator> keyCapIterators, long endTick) {
    for (KeyCapIterator keyCapIterator : keyCapIterators.values()) {
      if (keyCapIterator.hasNext(endTick)) {
        return true;
      }
    }
    return false;
  }

}
