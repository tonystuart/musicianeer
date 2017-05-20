// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnTemplates extends Message {

  private String songOptions;
  private String programOptions;
  private String inputOptions;

  public OnTemplates(String songOptions, String programOptions, String inputOptions) {
    this.songOptions = songOptions;
    this.programOptions = programOptions;
    this.inputOptions = inputOptions;
  }

  public String getInputOptions() {
    return inputOptions;
  }

  public String getProgramOptions() {
    return programOptions;
  }

  public String getSongOptions() {
    return songOptions;
  }

  @Override
  public String toString() {
    return "OnTemplates [songOptions=" + songOptions + ", programOptions=" + programOptions + ", inputOptions=" + inputOptions + "]";
  }

}
