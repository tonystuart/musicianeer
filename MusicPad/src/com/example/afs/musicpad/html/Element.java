// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public abstract class Element {

  public String render() {
    StringBuilder s = new StringBuilder();
    render(s);
    return s.toString();
  }

  public abstract void render(StringBuilder s);

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    render(s);
    return s.toString();
  }

  protected String format(String template, Object... parameters) {
    return String.format(template, parameters);
  }
}