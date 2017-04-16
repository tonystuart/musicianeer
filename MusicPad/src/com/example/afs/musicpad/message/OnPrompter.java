// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.player.Prompter;

public class OnPrompter extends Message {

  private Prompter prompter;

  public OnPrompter() {
  }

  public OnPrompter(Prompter prompter) {
    this.prompter = prompter;
  }

  public Prompter getPrompter() {
    return prompter;
  }

  @Override
  public String toString() {
    return "OnPrompter [prompter=" + prompter + "]";
  }

}
