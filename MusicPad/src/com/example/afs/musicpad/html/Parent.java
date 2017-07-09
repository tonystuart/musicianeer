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

public class Parent extends Hypertext implements Iterable<Element> {
  private RandomAccessList<Element> childElements;

  protected Parent(String type) {
    super(type);
  }

  protected Parent(String type, String[] properties) {
    super(type, properties);
  }

  @Override
  public void appendChild(Element childElement) {
    if (childElements == null) {
      childElements = new DirectList<>();
    }
    childElements.add(childElement);
  }

  public void clear() {
    childElements.clear();
  }

  @Override
  public Element getChild(int childIndex) {
    return childElements.get(childIndex);
  }

  @Override
  public int getChildCount() {
    return childElements.size();
  }

  @Override
  public Iterator<Element> iterator() {
    return childElements.iterator();
  }

  @Override
  public void render(StringBuilder s) {
    super.render(s);
    if (childElements != null) {
      for (Element element : childElements) {
        element.render(s);
      }
    }
    s.append(format("</%s>\n", getType()));
  }

  @Override
  protected void processArgument(String text) {
    appendChild(new TextElement(text));
  }
}