// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.renderer.karaoke;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.html.Division;
import com.example.afs.musicpad.html.Element;
import com.example.afs.musicpad.html.Parent;
import com.example.afs.musicpad.html.TextElement;

// https://www.w3schools.com/jsref/dom_obj_document.asp
// https://www.w3schools.com/jsref/dom_obj_all.asp

public class ShadowDom {

  private Map<String, Element> ids = new HashMap<>();
  private Map<String, Set<Element>> classes = new HashMap<>();
  private Element root;

  public void addClass(Element element, String className) {
    element.realizeClassList().add(className);
    realizeClass(className).add(element);
    onAddClassName(element, className);
  }

  public Division div(String... properties) {
    Division division = new Division(properties);
    String id = division.getId();
    if (id != null) {
      ids.put(id, division);
    }
    Set<String> classList = division.getClassList();
    if (classList != null) {
      for (String className : classList) {
        realizeClass(className).add(division);
      }
    }
    return division;
  }

  public Element getElementByClassName(String className) {
    Element element;
    Set<Element> elementsWithClass = classes.get(className);
    if (elementsWithClass == null || elementsWithClass.size() != 1) {
      element = null;
    } else {
      element = elementsWithClass.iterator().next();
    }
    return element;
  }

  @SuppressWarnings("unchecked")
  public <T> T getElementById(String id) {
    return (T) ids.get(id);
  }

  public Set<Element> getElementsByClassName(String className) {
    return classes.get(className);
  }

  public void removeClass(Element element, String className) {
    Set<String> classList = element.getClassList();
    if (classList != null) {
      classList.remove(className);
      Set<Element> elementsWithClass = classes.get(className);
      elementsWithClass.remove(element);
      onRemoveClassName(element, className);
    }
  }

  public String render() {
    return root.render();
  }

  public void replaceChildren(Element element, Element newChild) {
    if (element instanceof Parent) {
      Parent parent = (Parent) element;
      parent.replaceChildren(newChild);
      onReplaceChildren(parent, newChild);
    }
  }

  public void setRoot(Element root) {
    this.root = root;
  }

  public TextElement text(Object value) {
    return new TextElement(value);
  }

  protected void onAddClassName(Element element, String className) {
  }

  protected void onRemoveClassName(Element element, String className) {
  }

  protected void onReplaceChildren(Parent parent, Element newChild) {
  }

  private Set<Element> realizeClass(String className) {
    Set<Element> elementsWithClass = classes.get(className);
    if (elementsWithClass == null) {
      elementsWithClass = new HashSet<>();
      classes.put(className, elementsWithClass);
    }
    return elementsWithClass;
  }
}