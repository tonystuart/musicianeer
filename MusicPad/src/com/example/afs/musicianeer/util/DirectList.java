// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.util;

import java.util.ArrayList;
import java.util.Collection;

public class DirectList<T> extends ArrayList<T> implements RandomAccessList<T> {

  public DirectList() {
  }

  public DirectList(Collection<T> items) {
    super(items);
  }

  public DirectList(int count) {
    super(count);
  }

  public DirectList(Iterable<T> items) {
    for (T item : items) {
      add(item);
    }
  }
}
