// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtilities {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static <T> T fromJson(String json, Class<T> classOfT) {
    T object = GSON.fromJson(json, classOfT);
    return object;
  }

  public static String toJson(Object object) {
    String json = GSON.toJson(object);
    return json;
  }

}
