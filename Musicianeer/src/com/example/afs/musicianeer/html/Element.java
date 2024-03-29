// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.html;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Element extends Node {
  private String type;
  private String id;
  private StringBuilder style;
  private Set<String> classList;
  private Map<String, Object> attributes;
  private boolean isSelfClosingTag;

  protected Element(String type) {
    this.type = type;
  }

  protected Element(String type, String[] properties) {
    this(type);
    for (String property : properties) {
      if (property.length() == 0) {
        throw new IllegalArgumentException("Empty property string");
      }
      char firstChar = property.charAt(0);
      if (firstChar == '#') {
        setId(property.substring(1));
      } else if (firstChar == '.') {
        addClassName(property.substring(1));
      } else {
        processProperty(property);
      }
    }
  }

  public void addClassName(String className) {
    realizeClassList().add(className);
  }

  public Element addClickHandler() {
    addHandler("onClick");
    return this;
  }

  public Element addHandler(String handler) {
    if (getId() == null) {
      throw new IllegalStateException();
    }
    setProperty(handler.toLowerCase(), "musicPad." + handler + "(event)");
    return this;
  }

  public Element addMouseDownHandler() {
    addHandler("onMouseDown");
    return this;
  }

  public Element addMouseOutHandler() {
    addHandler("onMouseOut");
    return this;
  }

  public Element addMouseOverHandler() {
    addHandler("onMouseOver");
    return this;
  }

  public Element addMouseUpHandler() {
    addHandler("onMouseUp");
    return this;
  }

  public Set<String> getClassList() {
    return classList;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public boolean isSelfClosingTag() {
    return isSelfClosingTag;
  }

  public Set<String> realizeClassList() {
    if (classList == null) {
      classList = new HashSet<>();
    }
    return classList;
  }

  @Override
  public void render(StringBuilder s) {
    s.append(format("<%s", type));
    if (id != null) {
      s.append(format(" id='%s'", id));
    }
    if (classList != null) {
      s.append(" class='");
      int index = 0;
      for (String className : classList) {
        if (index++ > 0) {
          s.append(" ");
        }
        s.append(className);
      }
      s.append("'");
    }
    if (style != null) {
      s.append(" style='");
      s.append(style);
      s.append("'");
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
    if (isSelfClosingTag) {
      s.append(" /");
    }
    s.append(">");
  }

  public void setId(String id) {
    HtmlUtilities.validate(id);
    this.id = id;
  }

  public Element setProperty(String name) {
    return setProperty(name, null);
  }

  public Element setProperty(String name, Object value) {
    if (attributes == null) {
      attributes = new HashMap<>();
    }
    HtmlUtilities.validate(name);
    if (value instanceof String) {
      HtmlUtilities.validate((String) value);
    }
    attributes.put(name, value);
    return this;
  }

  public void setSelfClosingTag(boolean isSelfClosingTag) {
    this.isSelfClosingTag = isSelfClosingTag;
  }

  public Element style(String newStyle) {
    if (!newStyle.endsWith(";")) {
      throw new IllegalArgumentException("Expected newStyle to end with semicolon");
    }
    HtmlUtilities.validate(newStyle);
    if (style == null) {
      style = new StringBuilder();
    }
    if (style.length() > 0) {
      style.append(" ");
    }
    style.append(newStyle);
    return this;
  }

  protected void processProperty(String property) {
    // NB: Beware of order of instance initialization:
    // https://docs.oracle.com/javase/specs/jls/se8/html/jls-12.html#jls-12.5
    throw new IllegalArgumentException(property);
  }

}