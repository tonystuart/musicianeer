// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Select extends Parent {

  public Select(String... properties) {
    super("select", properties);
  }

  @Override
  public Select add(Node child) {
    return (Select) super.add(child);
  }

  public Select addChangeHandler() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("onclick", "musicPad.onInput(event, this.value)");
    return this;
  }

  public void setValue(String value) {
    setProperty("value", value);
  }

}
