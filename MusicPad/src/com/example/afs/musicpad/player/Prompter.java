// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.player;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import com.example.afs.musicpad.song.Default;
import com.example.afs.musicpad.song.Note;
import com.example.afs.musicpad.song.Song;
import com.example.afs.musicpad.song.Word;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Prompter {

  public static class PrompterChannel {
    private String title;
    private String device;
    private int channel;
    private int lowestMidiNote;
    private int highestMidiNote;
    private int ticksPerLine;
    private int totalLines;
    private Map<Integer, PrompterLine> prompterLines;

    public int getChannel() {
      return channel;
    }

    public String getDevice() {
      return device;
    }

    public int getHighestMidiNote() {
      return highestMidiNote;
    }

    public int getLowestMidiNote() {
      return lowestMidiNote;
    }

    public Map<Integer, PrompterLine> getPrompterLines() {
      return prompterLines;
    }

    public int getTicksPerLine() {
      return ticksPerLine;
    }

    public String getTitle() {
      return title;
    }

    public int getTotalLines() {
      return totalLines;
    }

    public void setChannel(int channel) {
      this.channel = channel;
    }

    public void setDevice(String device) {
      this.device = device;
    }

    public void setHighestMidiNote(int highestMidiNote) {
      this.highestMidiNote = highestMidiNote;
    }

    public void setLowestMidiNote(int lowestMidiNote) {
      this.lowestMidiNote = lowestMidiNote;
    }

    public void setPrompterLines(Map<Integer, PrompterLine> prompterLines) {
      this.prompterLines = prompterLines;
    }

    public void setTicksPerLine(int ticksPerLine) {
      this.ticksPerLine = ticksPerLine;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public void setTotalLines(int totalLines) {
      this.totalLines = totalLines;
    }

    @Override
    public String toString() {
      return "PrompterChannel [title=" + title + ", device=" + device + ", channel=" + channel + ", lowestMidiNote=" + lowestMidiNote + ", highestMidiNote=" + highestMidiNote + ", ticksPerLine=" + ticksPerLine + ", totalLines=" + totalLines + ", prompterLines=" + prompterLines + "]";
    }

  }

  public static class PrompterLine {
    private String words;
    private RandomAccessList<Integer> newMidiNotes;
    private RandomAccessList<Integer> sustainedMidiNotes;

    public RandomAccessList<Integer> getNewMidiNotes() {
      return newMidiNotes;
    }

    public RandomAccessList<Integer> getSustainedMidiNotes() {
      return sustainedMidiNotes;
    }

    public String getWords() {
      return words;
    }

    public void setNewMidiNotes(RandomAccessList<Integer> newMidiNotes) {
      this.newMidiNotes = newMidiNotes;
    }

    public void setSustainedMidiNotes(RandomAccessList<Integer> sustainedMidiNotes) {
      this.sustainedMidiNotes = sustainedMidiNotes;
    }

    public void setWords(String words) {
      this.words = words;
    }

    @Override
    public String toString() {
      return "PrompterLine [words=" + words + ", newMidiNotes=" + newMidiNotes + ", sustainedMidiNotes=" + sustainedMidiNotes + "]";
    }

  }

  private Song song;
  private int channel;
  private int ticksPerLine = Default.TICKS_PER_BEAT / 2;
  private PrompterChannel prompterChannel = new PrompterChannel();

  public Prompter(Song song, int channel) {
    this.song = song;
    this.channel = channel;
    long lastTick = song.getNotes().last().getTick();
    int totalLines = (int) (lastTick / ticksPerLine);
    prompterChannel.setTitle(song.getName());
    prompterChannel.setChannel(channel);
    prompterChannel.setLowestMidiNote(getLowestMidiNote());
    prompterChannel.setHighestMidiNote(getHighestMidiNote());
    prompterChannel.setPrompterLines(new HashMap<>());
    prompterChannel.setTicksPerLine(ticksPerLine);
    prompterChannel.setTotalLines(totalLines);
    for (long tick = 0; tick <= lastTick; tick += ticksPerLine) {
      appendWords(tick);
      appendMusic(tick);
    }
  }

  public PrompterChannel getPrompterChannel() {
    return prompterChannel;
  }

  private void addNewMidiNote(int lineIndex, int midiNote) {
    PrompterLine prompterLine = getPrompterLine(lineIndex);
    RandomAccessList<Integer> newMidiNotes = prompterLine.getNewMidiNotes();
    if (newMidiNotes == null) {
      newMidiNotes = new DirectList<>();
      prompterLine.setNewMidiNotes(newMidiNotes);
    }
    newMidiNotes.add(midiNote);
  }

  private void addSustainedMidiNote(int lineIndex, int midiNote) {
    PrompterLine prompterLine = getPrompterLine(lineIndex);
    RandomAccessList<Integer> sustainedMidiNotes = prompterLine.getSustainedMidiNotes();
    if (sustainedMidiNotes == null) {
      sustainedMidiNotes = new DirectList<>();
      prompterLine.setSustainedMidiNotes(sustainedMidiNotes);
    }
    sustainedMidiNotes.add(midiNote);
  }

  private void addWords(int lineIndex, String words) {
    PrompterLine prompterLine = getPrompterLine(lineIndex);
    prompterLine.setWords(words);
  }

  private void appendMusic(long tick) {
    SortedSet<Note> notes = song.getNotes().subSet(new Note(tick), new Note(tick + ticksPerLine));
    for (Note note : notes) {
      if (note.getChannel() == channel) {
        long noteStartingTick = note.getTick();
        long noteDuration = note.getDuration();
        long noteEndingTick = noteStartingTick + noteDuration;
        for (long sustainedTick = noteStartingTick; sustainedTick < noteEndingTick; sustainedTick += ticksPerLine) {
          int lineIndex = (int) (sustainedTick / ticksPerLine);
          if (sustainedTick == noteStartingTick) {
            addNewMidiNote(lineIndex, note.getMidiNote());
          } else {
            addSustainedMidiNote(lineIndex, note.getMidiNote());
          }
        }
      }
    }
  }

  private void appendWords(long tick) {
    StringBuilder s = new StringBuilder();
    SortedSet<Word> lineWords = song.getWords().subSet(new Word(tick), new Word(tick + ticksPerLine));
    for (Word word : lineWords) {
      String text = word.getText();
      if (text.startsWith("/") || text.startsWith("\\")) {
        text = text.substring(1);
      }
      if (s.length() > 0) {
        s.append(" ");
      }
      s.append(text.trim());
    }
    if (s.length() > 0) {
      int lineIndex = (int) (tick / ticksPerLine);
      addWords(lineIndex, s.toString());
    }
  }

  private int getHighestMidiNote() {
    int[] noteCounts = song.getDistinctNoteCount(channel);
    for (int midiNote = noteCounts.length - 1; midiNote >= 0; midiNote--) {
      if (noteCounts[midiNote] != 0) {
        return midiNote;
      }
    }
    return -1;
  }

  private int getLowestMidiNote() {
    int[] noteCounts = song.getDistinctNoteCount(channel);
    for (int midiNote = 0; midiNote < noteCounts.length; midiNote++) {
      if (noteCounts[midiNote] != 0) {
        return midiNote;
      }
    }
    return -1;
  }

  private PrompterLine getPrompterLine(int lineIndex) {
    PrompterLine prompterLine = prompterChannel.getPrompterLines().get(lineIndex);
    if (prompterLine == null) {
      prompterLine = new PrompterLine();
      prompterChannel.getPrompterLines().put(lineIndex, prompterLine);
    }
    return prompterLine;
  }

}
