// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.ArrayList;

public class DirectList<T> extends ArrayList<T> implements RandomAccessList<T> {

  public DirectList() {
  }

  public DirectList(int count) {
    super(count);
  }
}
