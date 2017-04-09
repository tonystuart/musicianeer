// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.midi.configuration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Node {

  public enum ReturnState {
    IF_MATCH, IF_NO_MATCH, THEN
  }

  private int lineIndex;
  private List<Node> nodes = new LinkedList<>();

  public Node(int lineIndex) {
    this.lineIndex = lineIndex;
  }

  public void add(Node child) {
    nodes.add(child);
  }

  public abstract ReturnState execute(Context context);

  public int getLineIndex() {
    return lineIndex;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  @Override
  public String toString() {
    return "Node [lineIndex=" + lineIndex + ", nodes=" + nodes + "]";
  }

  protected void displayError(String message) {
    System.err.println("Line " + (lineIndex + 1) + ": " + message);
  }

  protected ReturnState executeNodes(Context context) {
    ReturnState returnState = ReturnState.THEN;
    Iterator<Node> iterator = nodes.iterator();
    while (iterator.hasNext() && returnState != ReturnState.IF_MATCH) {
      Node node = iterator.next();
      returnState = node.execute(context);
    }
    return returnState;
  }

}