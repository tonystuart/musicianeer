// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.html;

public class Form extends Parent {

  public Form(String... properties) {
    super("form", properties);
  }

  public Form action(String action) {
    setProperty("action", action);
    return this;
  }

  public Form addSubmitHandler() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("onsubmit", "musicPad.onSubmit(event)");
    return this;
  }

  public Form enctype(String enctype) {
    setProperty("enctype", enctype);
    return this;
  }

  public Form method(String method) {
    setProperty("method", method);
    return this;
  }

  public Form target(String target) {
    setProperty("target", target);
    return this;
  }

}