// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.util;

import java.io.File;
import java.io.IOException;

public class FileCreator {

  private File outputDirectory;
  private int fileNumber;
  private String fileNameTemplate; // e.g. "%05d.mid"

  public FileCreator(String directoryName, String fileNameTemplate) {
    this.fileNameTemplate = fileNameTemplate;
    outputDirectory = new File(directoryName);
    outputDirectory.mkdirs();
    if (!outputDirectory.isDirectory() && !outputDirectory.canWrite()) {
      throw new IllegalArgumentException("Expected writable output directory " + directoryName);
    }
  }

  public File createUniqueFile() {
    try {
      File file;
      do {
        String fileName = String.format(fileNameTemplate, fileNumber++);
        file = new File(outputDirectory, fileName);
      } while (!file.createNewFile());
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}