// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.mqtt;

public class MqttNoteMessage {

  public enum Source {
    BACKGROUND, FOREGROUND
  }

  public enum Type {
    ON, OFF
  }

  private Source source;
  private Type type;
  private int channel;
  private int midiNote;
  private int velocity;

  public MqttNoteMessage(Source source, Type type, int channel, int midiNote, int velocity) {
    this.source = source;
    this.type = type;
    this.channel = channel;
    this.midiNote = midiNote;
    this.velocity = velocity;
  }

  public String asString() {
    return source.ordinal() + "," + type.ordinal() + "," + channel + "," + midiNote + "," + velocity;
  }

  public int getChannel() {
    return channel;
  }

  public int getMidiNote() {
    return midiNote;
  }

  public Source getSource() {
    return source;
  }

  public Type getType() {
    return type;
  }

  public int getVelocity() {
    return velocity;
  }

  @Override
  public String toString() {
    return "MqttNoteMessage [source=" + source + ", type=" + type + ", channel=" + channel + ", midiNote=" + midiNote + ", velocity=" + velocity + "]";
  }
}