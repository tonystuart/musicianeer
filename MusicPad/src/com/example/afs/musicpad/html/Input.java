// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Input extends Element {

  public Input(String... properties) {
    super("input", properties);
  }

  public Input addInputHandler() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    appendProperty("oninput", "karaoke.onInput(event, this.value)");
    return this;
  }

  public void setValue(Object value) {
    appendProperty("value", value);
  }
}
