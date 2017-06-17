// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DedupeFiles {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: java " + DedupeFiles.class.getName() + " md5sum output");
      System.exit(-1);
    }
    new DedupeFiles().dedupe(args[0]);
  }

  private String chooseBetterName(String left, String right) {
    int leftScore = 0;
    int rightScore = 0;
    int leftLength = left.length();
    int rightLength = right.length();
    int leftUppers = countUppers(left);
    int rightUppers = countUppers(right);
    if (left.contains("-")) {
      leftScore += 1;
    }
    if (right.contains("-")) {
      rightScore += 1;
    }
    if (left.contains("(")) {
      leftScore -= 1;
      leftLength -= 1;
    }
    if (right.contains("(")) {
      rightScore -= 1;
      rightLength -= 1;
    }
    if (left.contains(" ")) {
      leftScore += 1;
    }
    if (right.contains(" ")) {
      rightScore += 1;
    }
    if (left.contains("_")) {
      leftScore -= 2;
    }
    if (right.contains("_")) {
      rightScore -= 2;
    }
    if (left.startsWith("X - ")) {
      leftScore += 2;
    }
    if (right.startsWith("X - ")) {
      rightScore += 2;
    }
    if (leftUppers > rightUppers) {
      leftScore += 2;
    } else if (rightUppers > leftUppers) {
      rightScore += 2;
    }
    if (leftLength > rightLength) {
      leftScore += 3;
    } else if (rightLength > leftLength) {
      rightScore += 3;
    }
    if (left.endsWith(".kar")) {
      leftScore += 4;
    }
    if (right.endsWith(".kar")) {
      rightScore += 4;
    }
    //System.out.println(left + " (" + leftScore + ") vs " + right + " (" + rightScore + ")");
    return leftScore >= rightScore ? left : right;
  }

  private int countUppers(String s) {
    int count = 0;
    int length = s.length();
    for (int i = 0; i < length; i++) {
      if (Character.isUpperCase(s.charAt(i))) {
        count++;
      }
    }
    return count;
  }

  private void dedupe(String md5filename) {
    Map<String, String> map = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(md5filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        int index = line.indexOf("  ");
        if (index != -1) {
          String hash = line.substring(0, index);
          String name = line.substring(index + 2);
          String previous = map.get(hash);
          if (previous == null) {
            map.put(hash, name);
          } else {
            String best = chooseBetterName(previous, name);
            if (!best.equals(previous)) {
              map.put(hash, best);
            }
          }

        }
      }
      for (String name : map.values()) {
        System.out.println(name);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
