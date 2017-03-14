// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.util.HashMap;
import java.util.Map;

public class BidirectionalMap<K, V> extends HashMap<K, V> {
  private Map<V, K> toKey = new HashMap<>();

  public K getKey(V value) {
    return toKey.get(value);
  }

  @Override
  public V put(K key, V value) {
    toKey.put(value, key);
    return super.put(key, value);
  }
}