// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.frankenmusic.loader;

public class Structure {

  private int line;
  private int stanza;

  public Structure(int stanza, int line) {
    this.stanza = stanza;
    this.line = line;
  }

  public int getLine() {
    return line;
  }

  public int getStanza() {
    return stanza;
  }

  @Override
  public String toString() {
    return "Structure [stanza=" + stanza + ", line=" + line + "]";
  }

}