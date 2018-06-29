// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.html;

// NB: Select and Input cannot be derived from a common Field based class because Select is a Parent

public class Select extends Parent {

  public Select(String... properties) {
    super("select", properties);
  }

  @Override
  public Select add(Node child) {
    return (Select) super.add(child);
  }

  public Select addInputHandler() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("onclick", "musicPad.onInput(event, this.value)");
    return this;
  }

  public Select required() {
    setProperty("required");
    return this;
  }

  public Select setName(Object name) {
    setProperty("name", name);
    return this;
  }

  public Select setValue(String value) {
    setProperty("value", value);
    return this;
  }

}
