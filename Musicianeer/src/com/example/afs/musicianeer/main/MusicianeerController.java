// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.main;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicianeer.analyzer.TranspositionFinder.EasyTransposition;
import com.example.afs.musicianeer.device.midi.MidiHandle;
import com.example.afs.musicianeer.device.midi.MidiHandle.Type;
import com.example.afs.musicianeer.main.MusicianeerView.LedState;
import com.example.afs.musicianeer.message.OnCueNoteOn;
import com.example.afs.musicianeer.message.OnDeleteMidiFile;
import com.example.afs.musicianeer.message.OnMidiHandles;
import com.example.afs.musicianeer.message.OnMidiInputSelected;
import com.example.afs.musicianeer.message.OnMidiLibraryRefresh;
import com.example.afs.musicianeer.message.OnMidiOutputSelected;
import com.example.afs.musicianeer.message.OnMute;
import com.example.afs.musicianeer.message.OnNoteOff;
import com.example.afs.musicianeer.message.OnNoteOn;
import com.example.afs.musicianeer.message.OnPlay;
import com.example.afs.musicianeer.message.OnProgramOverride;
import com.example.afs.musicianeer.message.OnResetMidiNoteLeds;
import com.example.afs.musicianeer.message.OnSeek;
import com.example.afs.musicianeer.message.OnSelectChannel;
import com.example.afs.musicianeer.message.OnSelectSong;
import com.example.afs.musicianeer.message.OnSetAccompanimentType;
import com.example.afs.musicianeer.message.OnSetAccompanimentType.AccompanimentType;
import com.example.afs.musicianeer.message.OnSetChannelVolume;
import com.example.afs.musicianeer.message.OnSetMidiNoteLed;
import com.example.afs.musicianeer.message.OnSetPercentMasterGain;
import com.example.afs.musicianeer.message.OnSetPercentTempo;
import com.example.afs.musicianeer.message.OnSetPercentVelocity;
import com.example.afs.musicianeer.message.OnShadowUpdate;
import com.example.afs.musicianeer.message.OnShadowUpdate.Action;
import com.example.afs.musicianeer.message.OnSolo;
import com.example.afs.musicianeer.message.OnSongInfo;
import com.example.afs.musicianeer.message.OnSongSelected;
import com.example.afs.musicianeer.message.OnStop;
import com.example.afs.musicianeer.message.OnTransportNoteOff;
import com.example.afs.musicianeer.message.OnTransportNoteOn;
import com.example.afs.musicianeer.message.OnTransposition;
import com.example.afs.musicianeer.midi.Midi;
import com.example.afs.musicianeer.midi.SongInfoFactory.SongInfo;
import com.example.afs.musicianeer.task.ControllerTask;
import com.example.afs.musicianeer.task.MessageBroker;
import com.example.afs.musicianeer.util.RandomAccessList;

public class MusicianeerController extends ControllerTask {

  public static final int DEFAULT_VELOCITY = 64;

  private static final String PIANO_BAR = "Micro-";

  private int channel;
  private boolean isDown;
  private boolean isShift;
  private int currentMeasure;
  private int currentTransposition;
  private int channelVelocity = DEFAULT_VELOCITY;
  private int inputDeviceIndex = MidiHandle.MIDI_HANDLE_NA;
  private int outputDeviceIndex = MidiHandle.MIDI_HANDLE_NA;

  private int[] channelTranspositions;
  private int[] transportCueCount = new int[Midi.NOTES];
  private boolean[] transportNoteOn = new boolean[Midi.NOTES];

  private CurrentSong currentSong;
  private MusicianeerView musicianeerView;
  private Set<Integer> playerMidiNotes = new HashSet<>();
  private Map<String, Integer> activeKeys = new HashMap<>();
  private String deleteFilename;

  public MusicianeerController(MessageBroker messageBroker) {
    super(messageBroker);
    musicianeerView = new MusicianeerView(this);
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("song-index-")) {
      publish(new OnSelectSong(Integer.parseInt(id.substring("song-index-".length()))));
    } else if (id.startsWith("channel-index-")) {
      int newChannel = Integer.parseInt(id.substring("channel-index-".length()));
      publish(new OnSelectChannel(channel, newChannel));
      setCurrentChannel(newChannel); // NB: must be handled locally
    } else {
      switch (id) {
      case "delete-cancel":
        musicianeerView.showDeleteDialogBox(false);
        break;
      case "delete-okay":
        publish(new OnDeleteMidiFile(deleteFilename));
        musicianeerView.showDeleteDialogBox(false);
        break;
      case "drums":
        publish(new OnSetAccompanimentType(AccompanimentType.DRUMS));
        break;
      case "full":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.FULL));
        break;
      case "import":
        musicianeerView.showImportDialogBox(true);
        break;
      case "import-cancel":
        musicianeerView.showImportDialogBox(false);
        break;
      case "piano":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.PIANO));
        break;
      case "play":
        publish(new OnPlay());
        break;
      case "rhythm":
        publish(new OnSetAccompanimentType(OnSetAccompanimentType.AccompanimentType.RHYTHM));
        break;
      case "stop":
        publish(new OnStop());
        break;
      }
    }
  }

  @Override
  protected void doInput(String id, String value) {
    if (id.startsWith("channel-mute-")) {
      renderMute(Integer.parseInt(id.substring("channel-mute-".length())), Integer.parseInt(value));
    } else if (id.startsWith("channel-solo-")) {
      renderSolo(Integer.parseInt(id.substring("channel-solo-".length())), Integer.parseInt(value));
    } else if (id.equals("midi-input")) {
      inputDeviceIndex = Integer.parseInt(value);
      publish(new OnMidiInputSelected(channel, inputDeviceIndex));
    } else if (id.equals("midi-output")) {
      outputDeviceIndex = Integer.parseInt(value);
      publish(new OnMidiOutputSelected(channel, outputDeviceIndex));
    }
    switch (id) {
    case "background-volume":
      publish(new OnSetPercentVelocity(Integer.parseInt(value)));
      break;
    case "channel-volume":
      publish(new OnSetChannelVolume(channel, Integer.parseInt(value)));
      break;
    case "instrument":
      publish(new OnProgramOverride(channel, Integer.parseInt(value)));
      break;
    case "master-volume":
      publish(new OnSetPercentMasterGain(Integer.parseInt(value)));
      break;
    case "tempo":
      publish(new OnSetPercentTempo(Integer.parseInt(value)));
      break;
    case "transposition":
      publish(new OnTransposition(Integer.parseInt(value)));
      break;
    }
  }

  @Override
  protected void doKeyDown(String id, String value) {
    int keyCode = Integer.parseInt(value);
    if (keyCode == KeyEvent.VK_SHIFT) {
      isShift = true;
    } else if (keyCode == KeyMap.DELETE) {
      deleteFilename = currentSong.getSong().getFile().getName();
      musicianeerView.setDeleteText(deleteFilename);
      musicianeerView.showDeleteDialogBox(true);
    } else if (isShift && keyCode == '1') {
      publish(new OnSetDemoMode(true));
    } else if (isShift && keyCode == '2') {
      publish(new OnSetDemoMode(false));
    } else {
      int midiNote = KeyMap.toMidiNote(keyCode);
      if (midiNote != KeyMap.UNDEFINED) {
        if (isShift) {
          midiNote++;
        }
        activeKeys.put(value, midiNote);
        publish(new OnNoteOn(channel, midiNote, channelVelocity));
      }
    }
  }

  @Override
  protected void doKeyUp(String id, String value) {
    int keyCode = Integer.parseInt(value);
    if (keyCode == KeyEvent.VK_SHIFT) {
      isShift = false;
    } else {
      Integer midiNote = activeKeys.remove(value);
      if (midiNote != null) {
        publish(new OnNoteOff(channel, midiNote));
      }
    }
  }

  @Override
  protected void doLoad() {
    // NB: We are single threaded by virtue of our input queue
    subscribe(OnPlay.class, message -> doPlay(message));
    subscribe(OnSeek.class, message -> doSeek(message));
    subscribe(OnMute.class, message -> doMute(message));
    subscribe(OnSolo.class, message -> doSolo(message));
    subscribe(OnNoteOn.class, message -> doNoteOn(message));
    subscribe(OnNoteOff.class, message -> doNoteOff(message));
    subscribe(OnSongInfo.class, message -> doSongInfo(message));
    subscribe(OnCueNoteOn.class, message -> doCueNoteOn(message));
    subscribe(OnMidiHandles.class, message -> doMidiHandles(message));
    subscribe(OnSongSelected.class, message -> doSongSelected(message));
    subscribe(OnTransposition.class, message -> doTransposition(message));
    subscribe(OnSetPercentTempo.class, message -> doSetPercentTempo(message));
    subscribe(OnTransportNoteOn.class, message -> doTransportNoteOn(message));
    subscribe(OnTransportNoteOff.class, message -> doTransportNoteOff(message));
    subscribe(OnSetChannelVolume.class, message -> doSetChannelVolume(message));
    subscribe(OnMidiLibraryRefresh.class, message -> doMidiLibraryRefresh(message));
    subscribe(OnSetPercentVelocity.class, message -> doSetPercentVelocity(message));
    subscribe(OnSetPercentMasterGain.class, message -> doSetPercentMasterGain(message));
    subscribe(OnSetAccompanimentType.class, message -> doSetAccompanimentType(message));
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", musicianeerView.render()));
    musicianeerView.setAccompanimentType(request(Services.getAccompanimentType));
    RandomAccessList<OnSongInfo> songInfoList = request(Services.getSongInfoList);
    for (OnSongInfo onSongInfo : songInfoList) {
      renderSongInfo(onSongInfo.getSongInfo(), onSongInfo.getSongIndex());
    }
    CurrentSong initialSong = request(Services.getCurrentSong);
    if (initialSong == null) {
      musicianeerView.showImportDialogBox(true);
    } else {
      initializeCurrentSong(initialSong);
      initializeSynthesizerSettings();
    }
    renderMidiHandles(request(Services.getMidiHandles));
  }

  @Override
  protected void doMouseDown(String id) {
    isDown = true;
    if (id.startsWith("midi-note-")) {
      int mouseMidiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, mouseMidiNote, channelVelocity));
      playerMidiNotes.add(mouseMidiNote);
    }
  }

  @Override
  protected void doMouseOut(String id) {
    playerMidiNotes.forEach(midiNote -> publish(new OnNoteOff(channel, midiNote)));
    playerMidiNotes.clear();
  }

  @Override
  protected void doMouseOver(String id) {
    if (isDown && id.startsWith("midi-note-")) {
      int mouseMidiNote = Integer.parseInt(id.substring("midi-note-".length()));
      publish(new OnNoteOn(channel, mouseMidiNote, channelVelocity));
      playerMidiNotes.add(mouseMidiNote);
    }
  }

  @Override
  protected void doMouseUp(String id) {
    isDown = false;
    playerMidiNotes.forEach(midiNote -> publish(new OnNoteOff(channel, midiNote)));
    playerMidiNotes.clear();
  }

  @Override
  protected void doScroll(String id, String value) {
    if (id.equals("staff-scroller")) {
      publish(new OnSeek((long) Double.parseDouble(value)));
    }
  }

  private void doCueNoteOn(OnCueNoteOn message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      if (!transportNoteOn[midiNote]) {
        setMidiNoteLed(channel, midiNote, LedState.LOW);
      }
      transportCueCount[midiNote]++;
    }
  }

  private void doMidiHandles(OnMidiHandles message) {
    renderMidiHandles(message.getMidiHandles());
  }

  private void doMidiLibraryRefresh(OnMidiLibraryRefresh message) {
    // As an alternative to redrawing the song table, we truncate it and let OnSongInfo replace the remaining rows
    int midiLibrarySize = message.getMidiLibrary().getMidiFiles().size();
    musicianeerView.truncateSongTable(midiLibrarySize);
    if (midiLibrarySize == 0) {
      musicianeerView.showImportDialogBox(true);
    }
  }

  private void doMute(OnMute message) {
    musicianeerView.setMute(message.getChannel(), message.isMute());
  }

  private void doNoteOff(OnNoteOff message) {
    if (message.getChannel() == channel) {
      musicianeerView.renderKeyReleased(message.getData1());
    }
  }

  private void doNoteOn(OnNoteOn message) {
    if (message.getChannel() == channel) {
      musicianeerView.renderKeyPressed(message.getData1());
    }
  }

  private void doPlay(OnPlay message) {
    resetMidiNoteLeds(); // TODO: Remove this if we remove cue notes
  }

  private void doSeek(OnSeek message) {
    //resetTransportNoteState(); // TODO: Remove this if we remove cue notes
  }

  private void doSetAccompanimentType(OnSetAccompanimentType message) {
    musicianeerView.setAccompanimentType(message.getAccompanimentType());
  }

  private void doSetChannelVolume(OnSetChannelVolume message) {
    if (message.getChannel() == channel) {
      channelVelocity = message.getVolume();
      musicianeerView.setChannelVolume(channelVelocity);
    }
  }

  private void doSetPercentMasterGain(OnSetPercentMasterGain message) {
    musicianeerView.setPercentMasterGain(message.getPercentMasterGain());
  }

  private void doSetPercentTempo(OnSetPercentTempo message) {
    musicianeerView.setPercentTempo(message.getPercentTempo());
  }

  private void doSetPercentVelocity(OnSetPercentVelocity message) {
    musicianeerView.setPercentVelocity(message.getPercentVelocity());
  }

  private void doSolo(OnSolo message) {
    musicianeerView.setSolo(message.getChannel(), message.isSolo());
  }

  private void doSongInfo(OnSongInfo message) {
    renderSongInfo(message.getSongInfo(), message.getSongIndex());
  }

  private void doSongSelected(OnSongSelected message) {
    initializeCurrentSong(message.getCurrentSong());
  }

  private void doTransportNoteOff(OnTransportNoteOff message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      transportNoteOn[midiNote] = false;
      if (--transportCueCount[midiNote] > 0) {
        setMidiNoteLed(channel, midiNote, LedState.LOW);
      } else {
        setMidiNoteLed(channel, midiNote, LedState.OFF);
      }
    }
  }

  private void doTransportNoteOn(OnTransportNoteOn message) {
    if (message.getChannel() == channel) {
      int midiNote = message.getMidiNote();
      transportNoteOn[midiNote] = true;
      setMidiNoteLed(channel, midiNote, LedState.HIGH);
    }
    int measure = message.getMeasure();
    if (measure != currentMeasure) {
      currentMeasure = measure;
      musicianeerView.setMeasure(channel, measure);
    }
  }

  private void doTransposition(OnTransposition message) {
    currentTransposition = message.getTransposition();
    musicianeerView.renderStaff(currentSong.getSong(), channel, getTransposition());
    resetMidiNoteLeds();
  }

  private int findBestFit(Iterable<MidiHandle> midiHandles, Type type, int index) {
    MidiHandle first = null;
    for (MidiHandle midiHandle : midiHandles) {
      boolean isPianoBar = midiHandle.getName().startsWith(PIANO_BAR);
      if (midiHandle.getType() == type && //
          ((type == Type.INPUT && !isPianoBar) || //
              (type == Type.OUTPUT && isPianoBar))) {
        if (first == null) {
          if (index == MidiHandle.MIDI_HANDLE_NA) {
            return midiHandle.getIndex();
          }
          first = midiHandle;
        }
        if (index != MidiHandle.MIDI_HANDLE_NA && midiHandle.getIndex() == index) {
          return midiHandle.getIndex();
        }
      }
    }
    return first == null ? MidiHandle.MIDI_HANDLE_NA : first.getIndex();
  }

  private int getTransposition() {
    int transposition = channelTranspositions[channel];
    if (channel != Midi.DRUM) {
      transposition += currentTransposition;
    }
    return transposition;
  }

  private void initializeCurrentSong(CurrentSong currentSong) {
    this.currentSong = currentSong;
    EasyTransposition easyTransposition = currentSong.getSongInfo().getEasyTransposition();
    currentTransposition = easyTransposition.getSongTransposition();
    channelTranspositions = easyTransposition.getChannelTranspositions();
    resetMidiNoteLeds();
    musicianeerView.selectSong(currentSong.getSongIndex());
    musicianeerView.renderSongDetails(currentSong);
    setCurrentChannel(currentSong.getSongInfo().getActiveChannels()[0]);
  }

  private void initializeSynthesizerSettings() {
    SynthesizerSettings synthesizerSettings = request(Services.getSynthesizerSettings);
    boolean[] muteSettings = synthesizerSettings.getMuteSettings();
    boolean[] soloSettings = synthesizerSettings.getSoloSettings();
    for (int channel = 0; channel < Midi.CHANNELS; channel++) {
      boolean isMute = muteSettings[channel];
      if (isMute) {
        musicianeerView.setMute(channel, isMute);
      }
      boolean isSolo = soloSettings[channel];
      if (isSolo) {
        musicianeerView.setSolo(channel, isSolo);
      }
    }
  }

  private void renderMidiHandles(Iterable<MidiHandle> midiHandles) {
    inputDeviceIndex = findBestFit(midiHandles, Type.INPUT, inputDeviceIndex);
    outputDeviceIndex = findBestFit(midiHandles, Type.OUTPUT, outputDeviceIndex);
    musicianeerView.renderMidiHandles(midiHandles, inputDeviceIndex, outputDeviceIndex);
    publish(new OnMidiInputSelected(channel, inputDeviceIndex));
    publish(new OnMidiOutputSelected(channel, outputDeviceIndex));
  }

  private void renderMute(int channel, int value) {
    boolean isMute = value > 0;
    publish(new OnMute(channel, isMute));
  }

  private void renderSolo(int channel, int value) {
    boolean isSolo = value > 0;
    publish(new OnSolo(channel, isSolo));
  }

  private void renderSongInfo(SongInfo songInfo, int songIndex) {
    musicianeerView.renderSongInfo(songInfo, songIndex);
  }

  private void resetMidiNoteLeds() {
    Arrays.fill(transportCueCount, 0);
    Arrays.fill(transportNoteOn, false);
    musicianeerView.resetMidiNoteLeds();
    publish(new OnResetMidiNoteLeds());
  }

  private void setCurrentChannel(int channel) {
    this.channel = channel;
    resetMidiNoteLeds();
    musicianeerView.selectChannel(currentSong.getSong(), channel, getTransposition());
    musicianeerView.setMeasure(channel, currentMeasure);
    CurrentPrograms currentPrograms = request(Services.getCurrentPrograms);
    if (currentPrograms != null) {
      int currentProgram;
      if (channel == Midi.DRUM) {
        currentProgram = Musicianeer.UNSET;
      } else {
        currentProgram = currentPrograms.getPrograms()[channel];
      }
      musicianeerView.setProgram(currentProgram);
    }
    if (inputDeviceIndex != MidiHandle.MIDI_HANDLE_NA) {
      publish(new OnMidiInputSelected(channel, inputDeviceIndex));
    }
    if (outputDeviceIndex != MidiHandle.MIDI_HANDLE_NA) {
      publish(new OnMidiOutputSelected(channel, outputDeviceIndex));
    }
  }

  private void setMidiNoteLed(int channel, int midiNote, LedState state) {
    musicianeerView.setMidiNoteLed(channel, midiNote, state);
    publish(new OnSetMidiNoteLed(channel, midiNote, state));
  }

}
