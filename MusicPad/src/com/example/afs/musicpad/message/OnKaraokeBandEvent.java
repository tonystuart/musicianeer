// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnKaraokeBandEvent extends TypedMessage {

  public enum Action {
    CLICK
  }

  private Action action;
  private String id;

  public OnKaraokeBandEvent(Action action, String id) {
    this.action = action;
    this.id = id;
  }

  public Action getAction() {
    return action;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "OnKaraokeBandEvent [action=" + action + ", id=" + id + "]";
  }

}
