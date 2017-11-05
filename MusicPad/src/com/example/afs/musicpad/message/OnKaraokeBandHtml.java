// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnKaraokeBandHtml extends TypedMessage {

  public enum Action {
    REPLACE_CHILDREN, ADD_CLASS, REMOVE_CLASS
  }

  private String html;
  private Action action;
  private String selector;

  public OnKaraokeBandHtml(Action action, String selector, String html) {
    this.action = action;
    this.selector = selector;
    this.html = html;
  }

  public Action getAction() {
    return action;
  }

  public String getHtml() {
    return html;
  }

  public String getSelector() {
    return selector;
  }

  @Override
  public String toString() {
    return "OnKaraokeBandHtml [action=" + action + ", selector=" + selector + ", html=" + html + "]";
  }

}
