// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import com.example.afs.musicpad.player.PrompterData;

public class OnPrompterData extends Message {

  private PrompterData prompterData;

  public OnPrompterData() {
  }

  public OnPrompterData(PrompterData prompterData) {
    this.prompterData = prompterData;
  }

  public PrompterData getPrompterData() {
    return prompterData;
  }

  @Override
  public String toString() {
    return "OnPrompterData [prompterData=" + prompterData + "]";
  }

}
