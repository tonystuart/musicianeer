// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

public class Count<T> implements Comparable<Count<T>> {

  private int count;
  private T value;

  public Count(T value) {
    this.value = value;
  }

  @Override
  public int compareTo(Count<T> that) {
    return this.count - that.count;
  }

  public int getCount() {
    return count;
  }

  public T getValue() {
    return value;
  }

  public void increment() {
    count++;
  }

  @Override
  public String toString() {
    return "Count [count=" + count + ", value=" + value + "]";
  }

}