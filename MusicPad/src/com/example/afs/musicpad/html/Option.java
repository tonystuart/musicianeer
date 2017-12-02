// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Option extends Parent {

  public Option(String text, Object value) {
    this(text, value, false);
  }

  public Option(String text, Object value, boolean isSelected) {
    super("option");
    setValue(value);
    setText(text);
    if (isSelected) {
      setSelected();
    }
  }

  public Option setValue(Object value) {
    setProperty("value", value);
    return this;
  }

  private Option setSelected() {
    setProperty("selected");
    return this;
  }

  private Option setText(String text) {
    appendChild(new TextElement(text));
    return this;
  }

}
