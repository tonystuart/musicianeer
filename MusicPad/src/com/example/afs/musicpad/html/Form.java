// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Form extends Parent {

  public Form(String... properties) {
    super("form", properties);
  }

  public Form addSubmitHandler() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("onsubmit", "musicPad.onSubmit(event)");
    return this;
  }

}