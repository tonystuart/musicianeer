// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtilities {

  public static String getBaseName(String path) {
    int begin = path.lastIndexOf(File.separatorChar) + 1;
    int end = path.lastIndexOf('.');
    if (end == -1) {
      end = path.length();
    }
    String baseName = path.substring(begin, end);
    return baseName;
  }

  public static String read(File file) {
    return read(file.getPath());
  }

  public static String read(InputStream inputStream) {
    try {
      int rc;
      byte[] buffer = new byte[4096];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      while ((rc = inputStream.read(buffer)) != -1) {
        baos.write(buffer, 0, rc);
      }
      return baos.toString("utf-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String read(String fileName) {
    try (InputStream inputStream = new FileInputStream(fileName)) {
      String contents = read(inputStream);
      return contents;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T readJson(String fileName, Class<T> classOfT) {
    String json = read(fileName);
    T object = JsonUtilities.fromJson(json, classOfT);
    return object;
  }

  public static void write(String fileName, String contents) {
    try (OutputStream outputStream = new FileOutputStream(fileName)) {
      outputStream.write(contents.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(OutputStream outputStream, Object object) {
    try {
      String json = JsonUtilities.toJson(object);
      outputStream.write(json.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeJson(String fileName, Object object) {
    String json = JsonUtilities.toJson(object);
    write(fileName, json);
  }

}
