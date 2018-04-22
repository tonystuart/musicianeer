// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;

public class OnSetAccompanimentType implements Message {

  public enum AccompanimentType {
    FULL, PIANO, RHYTHM, DRUMS, SOLO
  }

  private AccompanimentType accompanimentType;

  public OnSetAccompanimentType(AccompanimentType accompanimentType) {
    this.accompanimentType = accompanimentType;
  }

  public AccompanimentType getAccompanimentType() {
    return accompanimentType;
  }

  @Override
  public String toString() {
    return "OnSetAccompanimentType [accompanimentType=" + accompanimentType + "]";
  }

}
