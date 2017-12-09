// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Parent extends Element {

  private Node head;
  private Node tail;

  protected Parent(String type) {
    super(type);
  }

  protected Parent(String type, String[] properties) {
    super(type, properties);
  }

  public Parent add(Node child) {
    appendChild(child);
    return this;
  }

  @Override
  public Parent addClickHandler() {
    return (Parent) super.addClickHandler();
  }

  public void appendChild(Node node) {
    if (node.getParent() != null || node.getPrevious() != null || node.getNext() != null) {
      throw new IllegalArgumentException("Node is already attached");
    }
    if (tail == null) {
      head = tail = node;
      node.setParent(this);
    } else {
      tail.append(node);
    }
  }

  public CheckBox checkbox(String... properties) {
    return new CheckBox(properties);
  }

  public void clear() {
    for (Node child = head; child != null; child = child.getNext()) {
      child.setParent(null);
    }
    head = tail = null;
  }

  public Division div(String... properties) {
    return new Division(properties);
  }

  public Node getHead() {
    return head;
  }

  public Node getTail() {
    return tail;
  }

  public Label label(String... properties) {
    return new Label(properties);
  }

  public Parent onClick(String handler) {
    setProperty("onclick", handler);
    return this;
  }

  @Override
  public Parent property(String name, Object value) {
    return (Parent) super.property(name, value);
  }

  public PercentRange range(String... properties) {
    return new PercentRange(properties);
  }

  @Override
  public void render(StringBuilder s) {
    super.render(s);
    for (Node child = head; child != null; child = child.getNext()) {
      child.render(s);
    }
    s.append(format("</%s>\n", getType()));
  }

  public void replaceChildren(Node node) {
    clear();
    appendChild(node);
  }

  public void setHead(Node head) {
    this.head = head;
  }

  public void setTail(Node tail) {
    this.tail = tail;
  }

  public TextElement text(String text) {
    return new TextElement(text);
  }

  @Override
  protected void processProperty(String property) {
    if (property.startsWith("$")) {
      appendChild(new TextElement(property.substring(1)));
    } else {
      throw new IllegalArgumentException(property);
    }
  }

}