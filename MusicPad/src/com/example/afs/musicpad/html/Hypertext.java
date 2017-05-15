// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Hypertext extends Element {
  private String type;
  private String id;
  private String className;
  private RandomAccessList<Element> childElements = new DirectList<>();

  public Hypertext(String type) {
    this.type = type;
  }

  public void append(Element childElement) {
    childElements.add(childElement);
  }

  public RandomAccessList<Element> getChildElements() {
    return childElements;
  }

  public String getClassName() {
    return className;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  @Override
  public void render(StringBuilder s) {
    s.append(format("<%s", type));
    if (id != null) {
      s.append(format(" id='%s'", id));
    }
    if (className != null) {
      s.append(format(" class='%s'", className));
    }
    s.append(">\n");
    for (Element element : childElements) {
      element.render(s);
    }
    s.append(format("</%s>", type));
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Element [type=" + type + ", id=" + id + ", className=" + className + ", childElements=" + childElements + "]";
  }
}