// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

import java.util.Iterator;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Parent extends Element implements Iterable<Node> {

  private RandomAccessList<Node> childNodes = new DirectList<>();

  protected Parent(String type) {
    super(type);
  }

  protected Parent(String type, String[] properties) {
    super(type, properties);
    if (childNodes == null) {
      childNodes = new DirectList<>();
    }
  }

  public Parent add(Node child) {
    appendChild(child);
    return this;
  }

  @Override
  public Parent addClickHandler() {
    return (Parent) super.addClickHandler();
  }

  public Parent addMoveSource() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("draggable", "true");
    setProperty("ondragstart", "musicPad.onMoveStart(event)");
    setProperty("ondragend", "musicPad.onMoveEnd(event)");
    return this;
  }

  public Parent addMoveTarget() {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty("ondragover", "musicPad.onMoveOver(event)");
    setProperty("ondrop", "musicPad.onMoveDrop(event)");
    return this;
  }

  public void appendChild(Node childElement) {
    childNodes.add(childElement);
  }

  public void clear() {
    childNodes.clear();
  }

  @SuppressWarnings("unchecked")
  public <T> T getChild(int childIndex) {
    return (T) childNodes.get(childIndex);
  }

  public int getChildCount() {
    return childNodes.size();
  }

  public int indexOf(Node node) {
    return childNodes.indexOf(node);
  }

  public void insertChild(Node newChild, int index) {
    childNodes.add(index, newChild);
  }

  @Override
  public Iterator<Node> iterator() {
    return childNodes.iterator();
  }

  public Parent onClick(String handler) {
    setProperty("onclick", handler);
    return this;
  }

  public void removeChild(int index) {
    childNodes.remove(index);
  }

  @Override
  public void render(StringBuilder s) {
    super.render(s);
    if (childNodes != null) {
      for (Node node : childNodes) {
        node.render(s);
      }
    }
    s.append(format("</%s>\n", getType()));
  }

  public void replaceChild(Element oldChild, Element newChild) {
    int index = childNodes.indexOf(oldChild);
    childNodes.remove(index);
    childNodes.add(index, newChild);
  }

  public void replaceChildren(Node node) {
    clear();
    appendChild(node);
  }

}