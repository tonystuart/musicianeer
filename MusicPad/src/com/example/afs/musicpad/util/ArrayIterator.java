// Copyright 2015 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T>, Iterable<T> {

  private T[] array;
  private int offset;

  public ArrayIterator(T[] array) {
    this.array = array;
  }

  @Override
  public boolean hasNext() {
    return offset < array.length;
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }

  @Override
  public T next() {
    return array[offset++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
