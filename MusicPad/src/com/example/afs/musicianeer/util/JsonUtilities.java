// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class JsonUtilities {

  private static final Gson GSON = new GsonBuilder() //
      .setPrettyPrinting() //
      .enableComplexMapKeySerialization() //
      .create();

  private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
  }.getType();

  public static <T> T fromJson(String json, Class<T> classOfT) {
    T object = GSON.fromJson(json, classOfT);
    return object;

  }

  public static <T> T fromJson(String json, Type type) {
    T object = GSON.fromJson(json, type);
    return object;
  }

  public static <T> T fromJsonFile(String filename, Class<T> classOfT) {
    T object;
    try (JsonReader jsonReader = new JsonReader(new BufferedReader(new FileReader(filename)))) {
      object = GSON.fromJson(jsonReader, classOfT);
    } catch (IOException e) {
      object = null;
    }
    return object;
  }

  public static String toJson(Object object) {
    String json = GSON.toJson(object);
    return json;
  }

  public static void toJsonFile(File file, Object value) {
    toJsonFile(file.getPath(), value);
  }

  public static <T> void toJsonFile(String filename, T value) {
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
      GSON.toJson(value, bufferedWriter);
    } catch (JsonIOException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, String> toMap(String value) {
    Map<String, String> map = JsonUtilities.fromJson(value, MAP_TYPE);
    return map;
  }

}
