// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.message;

public class OnShadowUpdate extends TypedMessage {

  public enum Action {
    APPEND_CHILD, INSERT_BEFORE, REMOVE_CHILD, REPLACE_CHILDREN, ADD_CLASS, REMOVE_CLASS, ENSURE_VISIBLE, SET_PROPERTY
  }

  private String name;
  private Object value;
  private Action action;
  private String selector;

  public OnShadowUpdate(Action action, String selector) {
    this.action = action;
    this.selector = selector;
  }

  public OnShadowUpdate(Action action, String selector, Object value) {
    this(action, selector, null, value);
  }

  public OnShadowUpdate(Action action, String selector, String name, Object value) {
    this.action = action;
    this.selector = selector;
    this.name = name;
    this.value = value;
  }

  public Action getAction() {
    return action;
  }

  public String getName() {
    return name;
  }

  public String getSelector() {
    return selector;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "OnShadowUpdate [action=" + action + ", selector=" + selector + ", name=" + name + ", value=" + value + "]";
  }

}
