// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.example.afs.fluidsynth.Synthesizer;
import com.example.afs.musicpad.device.common.DeviceGroup;
import com.example.afs.musicpad.device.common.DeviceHandler;
import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.task.BrokerTask;
import com.example.afs.musicpad.util.Broker;
import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class PianoWatcher extends BrokerTask<Message> {

  public static class Device {

    private String name;
    private RandomAccessList<InputDevice> inputDevices = new DirectList<>();
    private RandomAccessList<OutputDevice> outputDevices = new DirectList<>();

    public Device(String name) {
      this.name = name;
    }

    public void addInput(InputDevice midiDevice) {
      inputDevices.add(midiDevice);
    }

    public void addOutput(OutputDevice outputDevice) {
      outputDevices.add(outputDevice);
    }

    public RandomAccessList<InputDevice> getInputDevices() {
      return inputDevices;
    }

    public String getName() {
      return name;
    }

    public RandomAccessList<OutputDevice> getOutputDevices() {
      return outputDevices;
    }

    @Override
    public String toString() {
      return "Device [name=" + name + ", inputDevices=" + inputDevices + ", outputDevices=" + outputDevices + "]";
    }

  }

  public static class InputDevice {
    private String name;
    private MidiDevice midiDevice;

    public InputDevice(String name, MidiDevice midiDevice) {
      this.name = name;
      this.midiDevice = midiDevice;
    }

    public MidiDevice getMidiDevice() {
      return midiDevice;
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return "InputDevice [name=" + name + "]";
    }

  }

  public static class OutputDevice {
    private String name;
    private MidiDevice midiDevice;

    public OutputDevice(String name, MidiDevice midiDevice) {
      this.name = name;
      this.midiDevice = midiDevice;
    }

    public MidiDevice getMidiDevice() {
      return midiDevice;
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return "OutputDevice [name=" + name + "]";
    }

  }

  private Synthesizer synthesizer;
  private Map<String, DeviceGroup> oldDevices = new HashMap<>();

  public PianoWatcher(Broker<Message> broker, Synthesizer synthesizer) {
    super(broker, 1000);
    this.synthesizer = synthesizer;
  }

  @Override
  public void onTimeout() throws InterruptedException {
    Map<String, Device> newDevices = getDevices();
    Iterator<Entry<String, DeviceGroup>> oldIterator = oldDevices.entrySet().iterator();
    while (oldIterator.hasNext()) {
      Entry<String, DeviceGroup> oldEntry = oldIterator.next();
      if (!newDevices.containsKey(oldEntry.getKey())) {
        detachDevice(oldEntry.getKey(), oldEntry.getValue());
        oldIterator.remove();
      }
    }
    for (Entry<String, Device> newEntry : newDevices.entrySet()) {
      if (!oldDevices.containsKey(newEntry.getKey())) {
        attachDevice(newEntry.getKey(), newEntry.getValue());
      }
    }
  }

  private void attachDevice(String name, Device device) {
    System.out.println("Attaching MIDI device " + name);
    DeviceHandler deviceHandler = new DeviceHandler(getBroker(), synthesizer);
    PianoReader pianoReader = new PianoReader(deviceHandler.getInputQueue(), device);
    DeviceGroup deviceGroup = new DeviceGroup(deviceHandler, pianoReader);
    oldDevices.put(name, deviceGroup);
    deviceGroup.start();
  }

  private void detachDevice(String name, DeviceGroup deviceGroup) {
    System.out.println("Detaching MIDI device " + name);
    deviceGroup.terminate();
  }

  private Map<String, Device> getDevices() {
    try {
      Map<String, Device> devices = new HashMap<>();
      Info[] deviceDescriptors = MidiSystem.getMidiDeviceInfo();
      for (Info deviceDescriptor : deviceDescriptors) {
        String fullName = deviceDescriptor.getName();
        String name = getName(fullName);
        if (name != null) {
          Device device = devices.get(name);
          if (device == null) {
            device = new Device(name);
            devices.put(name, device);
          }
          MidiDevice midiDevice = MidiSystem.getMidiDevice(deviceDescriptor);
          if (midiDevice.getMaxReceivers() != 0) {
            device.addOutput(new OutputDevice(fullName, midiDevice));
          }
          if (midiDevice.getMaxTransmitters() != 0) {
            device.addInput(new InputDevice(fullName, midiDevice));
          }
        }
      }
      return devices;
    } catch (MidiUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  private String getName(String fullName) {
    String name = null;
    //String p = "^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)$";
    String p = "^(.*) \\[hw\\:([0-9]+),([0-9]+),([0-9]+)\\]$";
    Pattern pattern = Pattern.compile(p);
    Matcher matcher = pattern.matcher(fullName);
    if (matcher.matches()) {
      String type = matcher.group(1);
      String card = matcher.group(2);
      String device = matcher.group(3);
      name = type + "-" + card + "-" + device;
    }
    return name;
  }

}