// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnBrowserEvent extends TypedMessage {

  public enum Action {
    CLICK, INPUT, KEY_DOWN, KEY_UP, LOAD, MOUSE_DOWN, MOUSE_OUT, MOUSE_OVER, MOUSE_UP, MOVE, SUBMIT
  }

  private String id;
  private Action action;
  private String value;

  public OnBrowserEvent(Action action) {
    this(action, null, null);
  }

  public OnBrowserEvent(Action action, String id) {
    this(action, id, null);
  }

  public OnBrowserEvent(Action action, String id, String value) {
    this.action = action;
    this.id = id;
    this.value = value;
  }

  public Action getAction() {
    return action;
  }

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "OnBrowserEvent [action=" + action + ", id=" + id + ", value=" + value + "]";
  }

}
