// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public class JsonUtilities {

  private static final Gson GSON = new GsonBuilder() //
      .setPrettyPrinting() //
      .enableComplexMapKeySerialization() //
      .create();

  public static <T> T fromJson(String json, Class<T> classOfT) {
    T object = GSON.fromJson(json, classOfT);
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

}
