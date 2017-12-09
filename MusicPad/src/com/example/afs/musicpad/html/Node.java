// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public abstract class Node {

  private Node next;
  private Node previous;
  private Parent parent;
  private Object data;

  public void append(Node node) {
    if (node.getParent() != null) {
      node.remove();
    }
    node.parent = parent;
    if (next == null) {
      parent.setTail(node);
    } else {
      next.previous = node;
    }
    node.next = next;
    next = node;
  }

  @SuppressWarnings("unchecked")
  public <T> T getData() {
    return (T) data;
  }

  public Node getNext() {
    return next;
  }

  public Parent getParent() {
    return parent;
  }

  public Node getPrevious() {
    return previous;
  }

  public void prepend(Node node) {
    if (node.parent != null) {
      node.remove();
    }
    node.parent = parent;
    if (previous == null) {
      parent.setHead(node);
    } else {
      previous.next = node;
    }
    node.previous = previous;
    previous = node;
  }

  public void remove() {
    if (next == null) {
      parent.setTail(previous);
    } else {
      next.previous = previous;
    }
    if (previous == null) {
      parent.setHead(next);
    } else {
      previous.next = next;
    }
  }

  public String render() {
    StringBuilder s = new StringBuilder();
    render(s);
    return s.toString();
  }

  public abstract void render(StringBuilder s);

  @SuppressWarnings("unchecked")
  public <T> T setData(T data) {
    T oldData = (T) this.data;
    this.data = data;
    return oldData;
  }

  public void setParent(Parent parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    render(s);
    return s.toString();
  }

  protected String format(String template, Object... parameters) {
    return String.format(template, parameters);
  }
}