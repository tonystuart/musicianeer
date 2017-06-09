// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.example.afs.musicpad.device.common.DeviceHandler.InputType;
import com.example.afs.musicpad.device.common.DeviceHandler.OutputType;
import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Span;
import com.example.afs.musicpad.html.TextElement;
import com.example.afs.musicpad.keycap.KeyCap;
import com.example.afs.musicpad.keycap.KeyCapMap;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnChannelUpdate;
import com.example.afs.musicpad.message.OnKaraoke;
import com.example.afs.musicpad.message.OnMidiFiles;
import com.example.afs.musicpad.message.OnSong;
import com.example.afs.musicpad.song.Item;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.RandomAccessList;

public class KaraokeRenderer extends BrokerTask<Message> {

  public static class KeyCapItem extends Item<KeyCapItem> {
    private KeyCap keyCap;
    private int channel;

    public KeyCapItem(KeyCap keyCap, int channel) {
      super(keyCap.getTick());
      this.keyCap = keyCap;
      this.channel = channel;
    }

    public int getChannel() {
      return channel;
    }

    public KeyCap getKeyCap() {
      return keyCap;
    }

    @Override
    public String toString() {
      return "KeyCapItem [keyCap=" + keyCap + ", channel=" + channel + "]";
    }
  }

  private int ticksPerPixel;

  private Song song;
  private RandomAccessList<File> midiFiles;
  private Map<Integer, RandomAccessList<KeyCap>> channelKeyCaps = new HashMap<>();

  public KaraokeRenderer(Broker<Message> broker) {
    super(broker);
    subscribe(OnMidiFiles.class, message -> publishTemplates(message));
    subscribe(OnSong.class, message -> doSong(message));
    subscribe(OnChannelUpdate.class, message -> doChannelUpdate(message));
  }

  public String getKaraoke(NavigableSet<Item<?>> items) {
    Division container = new Division();
    Division stanza = null;
    Division line = null;
    for (Word word : song.getWords()) {
      String text = word.getText();
      if (text.length() > 1) {
        char firstChar = text.charAt(0);
        if (firstChar == '\\') {
          stanza = new Division();
          stanza.setClassName("stanza");
          container.appendChild(stanza);
          line = new Division();
          stanza.appendChild(line);
          text = text.substring(1);
        } else if (firstChar == '/') {
          if (stanza == null) {
            stanza = new Division();
            stanza.setClassName("stanza");
            container.appendChild(stanza);
          }
          line = new Division();
          stanza.appendChild(line);
          text = text.substring(1);
        } else if (line == null) {
          stanza = new Division();
          stanza.setClassName("stanza");
          container.appendChild(stanza);
          line = new Division();
          stanza.appendChild(line);
        }
        Span span = new Span();
        span.appendChild(new TextElement(text));
        line.appendChild(span);
      }
    }
    return container.render();
  }

  private void doChannelUpdate(OnChannelUpdate message) {
    int deviceIndex = message.getDeviceIndex();
    String deviceName = message.getDeviceName();
    int channel = message.getChannel();
    InputType inputType = message.getInputType();
    OutputType outputType = message.getOutputType();
    KeyCapMap keyCapMap = message.getKeyCapMap();
    RandomAccessList<KeyCap> keyCaps = keyCapMap.getKeyCaps();
    channelKeyCaps.put(channel, keyCaps);
    String karaoke = renderKaraoke();
    getBroker().publish(new OnKaraoke(karaoke));
  }

  private void doSong(OnSong message) {
    song = message.getSong();
  }

  private void publishTemplates(OnMidiFiles message) {
  }

  private String renderKaraoke() {
    NavigableSet<Item<?>> items = new TreeSet<>();
    for (Entry<Integer, RandomAccessList<KeyCap>> entry : channelKeyCaps.entrySet()) {
      int channel = entry.getKey();
      RandomAccessList<KeyCap> list = entry.getValue();
      for (KeyCap keyCap : list) {
        KeyCapItem keyCapItem = new KeyCapItem(keyCap, channel);
        items.add(keyCapItem);
      }
    }
    items.addAll(song.getWords());
    String karaoke = getKaraoke(items);
    return karaoke;
  }
}
