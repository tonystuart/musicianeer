// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

import java.util.concurrent.atomic.AtomicInteger;

public class Message {

  private static final AtomicInteger NEXT = new AtomicInteger(1);

  private String type = getClass().getSimpleName();
  private long timestamp = System.currentTimeMillis();
  private int number = NEXT.getAndIncrement();

  public int getNumber() {
    return number;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getType() {
    return type;
  }
}
