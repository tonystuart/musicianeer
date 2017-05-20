// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Option extends Hypertext {

  public Option() {
    super("option");
  }

  public Option(String text, Object value) {
    this(text, value, false);
  }

  public Option(String text, Object value, boolean isSelected) {
    this();
    setValue(value);
    setText(text);
    if (isSelected) {
      setSelected();
    }
  }

  public void setValue(Object value) {
    appendProperty("value", value);
  }

  private void setSelected() {
    appendProperty("selected");
  }

  private void setText(String text) {
    appendChild(new TextElement(text));
  }

}
