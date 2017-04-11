// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

public class Parser {

  private int lineIndex;
  private String[] lines;

  public MidiConfiguration parse(String contents) {
    lineIndex = 0;
    lines = contents.split("\\R");
    int expectedIndent = 0;
    MidiConfiguration midiConfiguration = new MidiConfiguration();
    while (lineIndex < lines.length) {
      String line = lines[lineIndex];
      int indent = countIndent(line);
      String[] tokens = line.substring(indent).split("\\s+");
      if (tokens.length == 0) {
        consume();
      } else if (tokens[0].startsWith("#")) {
        consume();
      } else if (indent != expectedIndent) {
        throw new IllegalArgumentException();
      } else if (tokens.length == 2 && tokens[0].equals("on")) {
        consume();
        On on = new On(lineIndex, tokens);
        midiConfiguration.put(tokens[1], on);
        parse(on, expectedIndent + 1);
      } else {
        throw new IllegalArgumentException(formatMessage("Expected on <name>"));
      }
    }
    return midiConfiguration;
  }

  protected int consume() {
    return lineIndex++;
  }

  private int countIndent(String line) {
    int indent = 0;
    for (int i = 0; i < line.length() && line.charAt(i) == ' '; i++) {
      indent++;
    }
    return indent;
  }

  private String formatMessage(String message) {
    return "Line " + (lineIndex + 1) + ": " + message;
  }

  private void parse(Node parent, int expectedIndent) {
    while (lineIndex < lines.length) {
      String line = lines[lineIndex];
      int indent = countIndent(line);
      String[] tokens = line.substring(indent).split("\\s+");
      if (tokens.length == 0 || tokens[0].isEmpty()) {
        consume();
      } else if (tokens[0].startsWith("#")) {
        consume();
      } else if (indent > expectedIndent) {
        throw new IllegalArgumentException(formatMessage("Expected indent of " + expectedIndent + " space(s), got " + indent + " space(s)"));
      } else if (indent < expectedIndent) {
        return;
      } else {
        switch (tokens[0]) {
        case "if":
          consume();
          Node child = new If(lineIndex, tokens);
          parent.add(child);
          parse(child, expectedIndent + 1);
          break;
        case "else":
          consume();
          if (!(parent.getLastNode() instanceof If)) {
            throw new IllegalArgumentException(formatMessage("Expected 'if' to be previous node at this level"));
          }
          child = new Else(lineIndex, tokens);
          parent.add(child);
          parse(child, expectedIndent + 1);
          break;
        case "return":
          consume();
          parent.add(new Return(lineIndex, tokens));
          break;
        case "set":
          consume();
          parent.add(new ThenSet(lineIndex, tokens));
          break;
        case "clear":
          consume();
          parent.add(new ThenClear(lineIndex, tokens));
          break;
        case "sendDeviceMessage":
          consume();
          parent.add(new ThenSendDeviceMessage(lineIndex, tokens));
          break;
        case "sendHandlerCommand":
          consume();
          parent.add(new ThenSendHandlerCommand(lineIndex, tokens));
          break;
        case "sendHandlerMessage":
          consume();
          parent.add(new ThenSendHandlerMessage(lineIndex, tokens));
          break;
        default:
          throw new IllegalArgumentException(formatMessage("Expected directive"));
        }
      }
    }
  }
}