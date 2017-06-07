// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.example.afs.musicpad.util.DirectList;
import com.example.afs.musicpad.util.RandomAccessList;

public class Hypertext extends Element {
  private String type;
  private String id;
  private String className;
  private Map<String, Object> attributes;
  private RandomAccessList<Element> childElements;

  public Hypertext(String type) {
    this.type = type;
  }

  public void appendChild(Element childElement) {
    if (childElements == null) {
      childElements = new DirectList<>();
    }
    childElements.add(childElement);
  }

  public void appendProperty(String name) {
    appendProperty(name, null);
  }

  public void appendProperty(String name, Object value) {
    if (attributes == null) {
      attributes = new HashMap<>();
    }
    attributes.put(name, value);
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
    if (attributes != null) {
      for (Entry<String, Object> entry : attributes.entrySet()) {
        String name = entry.getKey();
        Object value = entry.getValue();
        if (value == null) {
          s.append(format(" %s", name));
        } else {
          if (value instanceof Number) {
            s.append(format(" %s=%s", name, value.toString())); // integer or floating point
          } else {
            s.append(format(" %s='%s'", name, value.toString())); // value must not contain single quote
          }
        }
      }
    }
    s.append(">");
    if (childElements != null) {
      for (Element element : childElements) {
        element.render(s);
      }
    }
    s.append(format("</%s>\n", type));
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