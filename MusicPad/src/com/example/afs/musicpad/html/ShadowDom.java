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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.task.ControllerTask;

// https://www.w3schools.com/jsref/dom_obj_document.asp
// https://www.w3schools.com/jsref/dom_obj_all.asp

public class ShadowDom {

  private ControllerTask controllerTask;
  private Division root = new Division();
  private Map<String, Element> ids = new HashMap<>();
  private Map<String, Set<Element>> classes = new HashMap<>();

  public ShadowDom(ControllerTask controllerTask) {
    this.controllerTask = controllerTask;
  }

  public Parent add(Element child) {
    root.appendChild(child);
    addManagedNode(child, true);
    return root;
  }

  public void addClass(Element element, String className) {
    element.realizeClassList().add(className);
    realizeClass(className).add(element);
    onAddClassName(element, className);
  }

  public void addClass(String id, String className) {
    Element element = getElementById(id);
    if (element != null) {
      addClass(element, className);
    }
  }

  public CheckBox checkbox(String... properties) {
    CheckBox checkBox = new CheckBox(properties);
    return checkBox;
  }

  public Division div(String... properties) {
    Division division = new Division(properties);
    return division;
  }

  public void ensureVisible(Element element) {
    onEnsureVisible(element);
  }

  public FieldSet fieldSet(String... properties) {
    return new FieldSet(properties);
  }

  public String findClassNameByPrefix(Parent parent, String prefix) {
    Set<String> classList = parent.getClassList();
    for (String className : classList) {
      if (className.startsWith(prefix)) {
        return className;
      }
    }
    return null;
  }

  public Form form(String... properties) {
    return new Form(properties);
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

  public Label label(String... properties) {
    Label label = new Label(properties);
    return label;
  }

  public Legend legend(String... properties) {
    return new Legend(properties);
  }

  public Parent nameValue(String name, Object value) {
    return div(".detail") //
        .add(div(".name") //
            .add(text(name))) //
        .add(div(".value") //
            .add(text(value))); //
  }

  public NumberInput numberInput(String... properties) {
    return new NumberInput(properties);
  }

  public Option option(String text, String value) {
    return new Option(text, value);
  }

  public OptionGroup optionGroup(String label) {
    return new OptionGroup(label);
  }

  public Radio radio(String... properties) {
    return new Radio(properties);
  }

  public PercentRange range(String... properties) {
    PercentRange percentRange = new PercentRange(properties);
    return percentRange;
  }

  public void removeClass(Element element, String className) {
    Set<String> classList = element.getClassList();
    if (classList != null) {
      classList.remove(className);
      Set<Element> elementsWithClass = classes.get(className);
      if (elementsWithClass != null) {
        elementsWithClass.remove(element);
      }
      onRemoveClassName(element, className);
    }
  }

  public void removeClass(String id, String className) {
    Element element = getElementById(id);
    if (element != null) {
      removeClass(element, className);
    }
  }

  public String render() {
    return root.render();
  }

  public void replaceChildren(Parent parent, Node newChild) {
    replaceChildren(parent, newChild, true);
  }

  public void replaceChildren(Parent parent, Node newChild, boolean isManageDeep) {
    int childCount = parent.getChildCount();
    for (int childIndex = 0; childIndex < childCount; childIndex++) {
      Node oldChild = parent.getChild(childIndex);
      removeManagedNode(oldChild, isManageDeep);
    }
    if (newChild instanceof Element) {
      Element newElement = (Element) newChild;
      addManagedNode(newElement, isManageDeep);
    }
    parent.replaceChildren(newChild);
    onReplaceChildren(parent, newChild);
  }

  public Element selectElement(String id, String className) {
    Element newSelection = swapClassName(id, className);
    ensureVisible(newSelection);
    return newSelection;
  }

  public void setProperty(Element element, String name, Object value) {
    element.setProperty(name, value);
    onSetProperty(element, name, value);
  }

  public Submit submit(String... properties) {
    return new Submit(properties);
  }

  public Element swapClassName(String id, String className) {
    Element newSelection = getElementById(id);
    Element previousSelection = getElementByClassName(className);
    if (previousSelection != newSelection) {
      if (previousSelection != null) {
        removeClass(previousSelection, className);
      }
      addClass(newSelection, className);
    }
    return newSelection;
  }

  public void swapClassNameByPrefix(Parent parent, String prefix, Object suffix) {
    String newClassName = prefix + suffix;
    String oldClassName = findClassNameByPrefix(parent, prefix);
    if (!newClassName.equals(oldClassName)) {
      if (oldClassName != null) {
        removeClass(parent, oldClassName);
      }
      addClass(parent, newClassName);
    }
  }

  public TextElement text(Object value) {
    return new TextElement(value);
  }

  public TextInput textInput(String... properties) {
    return new TextInput(properties);
  }

  protected void onAddClassName(Element element, String className) {
    controllerTask.addShadowUpdate(new OnShadowUpdate(Action.ADD_CLASS, "#" + element.getId(), className));
  }

  protected void onEnsureVisible(Element element) {
    controllerTask.addShadowUpdate(new OnShadowUpdate(Action.ENSURE_VISIBLE, "#" + element.getId(), ""));
  }

  protected void onRemoveClassName(Element element, String className) {
    controllerTask.addShadowUpdate(new OnShadowUpdate(Action.REMOVE_CLASS, "#" + element.getId(), className));
  }

  protected void onReplaceChildren(Parent parent, Node newChild) {
    controllerTask.addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "#" + parent.getId(), newChild.render()));
  }

  protected void onSetProperty(Element element, String name, Object value) {
    controllerTask.addShadowUpdate(new OnShadowUpdate(Action.SET_PROPERTY, "#" + element.getId(), name, value));
  }

  private void addManagedNode(Node node, boolean isManageDeep) {
    if (node instanceof Element) {
      Element element = (Element) node;
      String id = element.getId();
      if (id != null) {
        ids.put(id, element);
      }
      Set<String> classList = element.getClassList();
      if (classList != null) {
        for (String className : classList) {
          realizeClass(className).add(element);
        }
      }
      if (isManageDeep && node instanceof Parent) {
        Parent parent = (Parent) node;
        int childCount = parent.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
          Node child = parent.getChild(childIndex);
          addManagedNode(child, isManageDeep);
        }
      }
    }
  }

  private Set<Element> realizeClass(String className) {
    Set<Element> elementsWithClass = classes.get(className);
    if (elementsWithClass == null) {
      elementsWithClass = new HashSet<>();
      classes.put(className, elementsWithClass);
    }
    return elementsWithClass;
  }

  private void removeManagedNode(Node node, boolean isManageDeep) {
    if (node instanceof Element) {
      Element element = (Element) node;
      String id = element.getId();
      if (id != null) {
        ids.remove(id);
      }
      Set<String> classList = element.getClassList();
      if (classList != null) {
        for (String className : classList) {
          classes.remove(className);
        }
      }
      if (isManageDeep && node instanceof Parent) {
        Parent parent = (Parent) node;
        int childCount = parent.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
          Node child = parent.getChild(childIndex);
          removeManagedNode(child, isManageDeep);
        }
      }
    }
  }

}