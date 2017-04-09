// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.List;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public abstract class Node {

  public enum ReturnState {
    IF_MATCH, IF_NO_MATCH, THEN
  }

  private int lineNumber;
  private RandomAccessList<Node> nodes = new DirectList<>();

  public Node(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public void add(Node child) {
    nodes.add(child);
  }

  public abstract ReturnState execute(Context context);

  public int getLineNumber() {
    return lineNumber;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  @Override
  public String toString() {
    return "Node [lineNumber=" + lineNumber + ", nodes=" + nodes + "]";
  }

  protected void displayError(String message) {
    System.err.println("Line " + (lineNumber + 1) + ": " + message);
  }

  protected ReturnState executeNodes(Context context) {
    int nodeCount = nodes.size();
    ReturnState returnState = ReturnState.THEN;
    for (int i = 0; i < nodeCount && returnState != ReturnState.IF_MATCH; i++) {
      Node node = nodes.get(i);
      returnState = node.execute(context);
    }
    return returnState;
  }

}