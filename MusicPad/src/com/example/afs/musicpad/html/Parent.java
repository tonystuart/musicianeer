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
  }

  public Parent add(Node child) {
    appendChild(child);
    return this;
  }

  @Override
  public Parent addClickHandler() {
    return (Parent) super.addClickHandler();
  }

  public void appendChild(Node childElement) {
    childNodes.add(childElement);
  }

  public CheckBox checkbox(String... properties) {
    return new CheckBox(properties);
  }

  public void clear() {
    childNodes.clear();
  }

  public Division div(String... properties) {
    return new Division(properties);
  }

  @SuppressWarnings("unchecked")
  public <T> T getChild(int childIndex) {
    return (T) childNodes.get(childIndex);
  }

  public int getChildCount() {
    return childNodes.size();
  }

  public void insertChild(Node newChild, int index) {
    childNodes.add(index, newChild);
  }

  @Override
  public Iterator<Node> iterator() {
    return childNodes.iterator();
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
    if (childNodes != null) {
      for (Node node : childNodes) {
        node.render(s);
      }
    }
    s.append(format("</%s>\n", getType()));
  }

  public void replaceChildren(Node node) {
    clear();
    appendChild(node);
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