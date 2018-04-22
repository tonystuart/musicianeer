// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.task.Message;

public class OnBrowseSong implements Message {

  public enum BrowseType {
    NEXT, NEXT_PAGE, PREVIOUS, PREVIOUS_PAGE
  }

  private BrowseType browseType;

  public OnBrowseSong(BrowseType browseType) {
    this.browseType = browseType;
  }

  public BrowseType getBrowseType() {
    return browseType;
  }

  @Override
  public String toString() {
    return "OnSelectSong [browseType=" + browseType + "]";
  }

}
