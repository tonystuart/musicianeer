// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.loader;

public class Neuron {
  private int accidentals;
  private int beats;
  private int bpm;
  private int channel;
  private int concurrency;
  private int duration;
  private int id;
  private int line;
  private int major;
  private int measure;
  private int melody;
  private int note;
  private int occupancy;
  private int parts;
  private int program;
  private int seconds;
  private int song;
  private int stanza;
  private int start;
  private int stop;
  private int thirds;
  private int tick;
  private int tonic;
  private int transpose;
  private int triads;
  private int unit;
  private int velocity;

  public int getAccidentals() {
    return accidentals;
  }

  public int getBeats() {
    return beats;
  }

  public int getBpm() {
    return bpm;
  }

  public int getChannel() {
    return channel;
  }

  public int getConcurrency() {
    return concurrency;
  }

  public int getDuration() {
    return duration;
  }

  public int getId() {
    return id;
  }

  public int getLine() {
    return line;
  }

  public int getMajor() {
    return major;
  }

  public int getMeasure() {
    return measure;
  }

  public int getMelody() {
    return melody;
  }

  public int getNote() {
    return note;
  }

  public int getOccupancy() {
    return occupancy;
  }

  public int getParts() {
    return parts;
  }

  public int getProgram() {
    return program;
  }

  public int getSeconds() {
    return seconds;
  }

  public int getSong() {
    return song;
  }

  public int getStanza() {
    return stanza;
  }

  public int getStart() {
    return start;
  }

  public int getStop() {
    return stop;
  }

  public int getThirds() {
    return thirds;
  }

  public int getTick() {
    return tick;
  }

  public int getTonic() {
    return tonic;
  }

  public int getTranspose() {
    return transpose;
  }

  public int getTriads() {
    return triads;
  }

  public int getUnit() {
    return unit;
  }

  public int getVelocity() {
    return velocity;
  }

  public void setAccidentals(int accidentals) {
    this.accidentals = accidentals;
  }

  public void setBeats(int beats) {
    this.beats = beats;
  }

  public void setBpm(int bpm) {
    this.bpm = bpm;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setConcurrency(int concurrency) {
    this.concurrency = concurrency;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public void setMajor(int major) {
    this.major = major;
  }

  public void setMeasure(int measure) {
    this.measure = measure;
  }

  public void setMelody(int melody) {
    this.melody = melody;
  }

  public void setNote(int note) {
    this.note = note;
  }

  public void setOccupancy(int occupancy) {
    this.occupancy = occupancy;
  }

  public void setParts(int parts) {
    this.parts = parts;
  }

  public void setProgram(int program) {
    this.program = program;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  public void setSong(int song) {
    this.song = song;
  }

  public void setStanza(int stanza) {
    this.stanza = stanza;
  }

  public void setStart(int begin) {
    this.start = begin;
  }

  public void setStop(int end) {
    this.stop = end;
  }

  public void setThirds(int thirds) {
    this.thirds = thirds;
  }

  public void setTick(int tick) {
    this.tick = tick;
  }

  public void setTonic(int tonic) {
    this.tonic = tonic;
  }

  public void setTranspose(int transpose) {
    this.transpose = transpose;
  }

  public void setTriads(int triads) {
    this.triads = triads;
  }

  public void setUnit(int unit) {
    this.unit = unit;
  }

  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  @Override
  public String toString() {
    return "Neuron [accidentals=" + accidentals + ", beats=" + beats + ", bpm=" + bpm + ", channel=" + channel + ", concurrency=" + concurrency + ", duration=" + duration + ", id=" + id + ", line=" + line + ", major=" + major + ", measure=" + measure + ", melody=" + melody + ", note=" + note + ", occupancy=" + occupancy + ", parts=" + parts + ", program=" + program + ", seconds=" + seconds + ", song=" + song + ", stanza=" + stanza + ", start=" + start + ", stop=" + stop + ", thirds=" + thirds + ", tick=" + tick + ", tonic=" + tonic + ", transpose=" + transpose + ", triads=" + triads + ", unit=" + unit + ", velocity=" + velocity + "]";
  }
}
