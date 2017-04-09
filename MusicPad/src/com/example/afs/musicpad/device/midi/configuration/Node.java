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

  // TODO: Consider adding support for return control (e.g. conditional, unconditional, return statement)

  private int lineIndex;
  private List<Node> nodes = new LinkedList<>();

  public Node(int lineIndex) {
    this.lineIndex = lineIndex;
  }

  public void add(Node child) {
    nodes.add(child);
  }

  public abstract boolean execute(Context context);

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

  protected boolean executeNodes(Context context) {
    boolean isMatch = false;
    Iterator<Node> iterator = nodes.iterator();
    while (iterator.hasNext() && !isMatch) {
      Node node = iterator.next();
      isMatch = node.execute(context);
    }
    return isMatch;
  }

}