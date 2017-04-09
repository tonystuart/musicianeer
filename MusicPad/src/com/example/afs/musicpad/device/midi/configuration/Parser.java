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
      } else {
        switch (tokens[0]) {
        case "onInitialization":
          consume();
          OnInitialization onInitialization = new OnInitialization(lineIndex);
          midiConfiguration.setOnInitialization(onInitialization);
          parseOnInitialization(onInitialization, expectedIndent + 1);
          break;
        case "onInput":
          consume();
          OnInput onInput = new OnInput(lineIndex);
          midiConfiguration.setOnInput(onInput);
          parseOnInput(onInput, expectedIndent + 1);
          break;
        case "onOutput":
          consume();
          OnOutput onOutput = new OnOutput(lineIndex);
          midiConfiguration.setOnOutput(onOutput);
          parseOnOutput(onOutput, expectedIndent + 1);
          break;
        default:
          throwUnsupportedOperation(tokens[0]);
        }
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

  private void parseOnInitialization(OnInitialization parent, int expectedIndent) {
    while (lineIndex < lines.length) {
      String line = lines[lineIndex];
      int indent = countIndent(line);
      String[] tokens = line.substring(indent).split("\\s+");
      if (tokens.length == 0) {
        consume();
      } else if (tokens[0].startsWith("#")) {
        consume();
      } else if (indent > expectedIndent) {
        throw new IllegalArgumentException("Line " + (lineIndex + 1) + ": incorrect indentation");
      } else if (indent < expectedIndent) {
        return;
      } else {
        switch (tokens[0]) {
        case "clearMode":
          consume();
          parent.add(new ThenClearMode(lineIndex, tokens));
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
        case "setMode":
          consume();
          parent.add(new ThenSetMode(lineIndex, tokens));
          break;
        default:
          throwUnsupportedOperation(tokens[0]);
        }
      }
    }
  }

  private void parseOnInput(Node parent, int expectedIndent) {
    while (lineIndex < lines.length) {
      String line = lines[lineIndex];
      int indent = countIndent(line);
      String[] tokens = line.substring(indent).split("\\s+");
      if (tokens.length == 0) {
        consume();
      } else if (tokens[0].startsWith("#")) {
        consume();
      } else if (indent > expectedIndent) {
        throw new IllegalArgumentException("Line " + (lineIndex + 1) + ": incorrect indentation");
      } else if (indent < expectedIndent) {
        return;
      } else {
        switch (tokens[0]) {
        case "ifChannel":
          consume();
          If child = new IfChannel(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifCommand":
          consume();
          child = new IfCommand(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifData1":
          consume();
          child = new IfData1(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifData2":
          consume();
          child = new IfData2(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifMode":
          consume();
          child = new IfMode(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifNotMode":
          consume();
          child = new IfNotMode(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "ifPort":
          consume();
          child = new IfPort(lineIndex, tokens);
          parent.add(child);
          parseOnInput(child, expectedIndent + 1);
          break;
        case "clearMode":
          consume();
          parent.add(new ThenClearMode(lineIndex, tokens));
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
        case "setMode":
          consume();
          parent.add(new ThenSetMode(lineIndex, tokens));
          break;
        default:
          throwUnsupportedOperation(tokens[0]);
        }
      }
    }
  }

  private void parseOnOutput(Node parent, int expectedIndent) {
    while (lineIndex < lines.length) {
      String line = lines[lineIndex];
      int indent = countIndent(line);
      String[] tokens = line.substring(indent).split("\\s+");
      if (tokens.length == 0) {
        consume();
      } else if (tokens[0].startsWith("#")) {
        consume();
      } else if (indent > expectedIndent) {
        throw new IllegalArgumentException("Line " + (lineIndex + 1) + ": incorrect indentation");
      } else if (indent < expectedIndent) {
        return;
      } else {
        switch (tokens[0]) {
        case "ifChannel":
          consume();
          If child = new IfChannel(lineIndex, tokens);
          parent.add(child);
          parseOnOutput(child, expectedIndent + 1);
          break;
        case "ifState":
          consume();
          child = new IfState(lineIndex, tokens);
          parent.add(child);
          parseOnOutput(child, expectedIndent + 1);
          break;
        case "clearMode":
          consume();
          parent.add(new ThenClearMode(lineIndex, tokens));
          break;
        case "sendDeviceMessage":
          consume();
          parent.add(new ThenSendDeviceMessage(lineIndex, tokens));
          break;
        case "setMode":
          consume();
          parent.add(new ThenSetMode(lineIndex, tokens));
          break;
        default:
          throwUnsupportedOperation(tokens[0]);
        }
      }
    }
  }

  private void throwUnsupportedOperation(String token) {
    throw new UnsupportedOperationException("Line " + (lineIndex + 1) + ": " + token + " (check spelling and capitalization)");
  }
}