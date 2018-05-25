// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

import com.example.afs.musicpad.task.ControllerTask;

// https://www.w3schools.com/jsref/dom_obj_document.asp
// https://www.w3schools.com/jsref/dom_obj_all.asp

public class ShadowDomBuilder extends ShadowDom {

  public ShadowDomBuilder(ControllerTask controllerTask) {
    super(controllerTask);
  }

  public Button button(String... properties) {
    return new Button(properties);
  }

  public CheckBox checkbox(String... properties) {
    return new CheckBox(properties);
  }

  public Division div(String... properties) {
    return new Division(properties);
  }

  public FieldSet fieldSet(String... properties) {
    return new FieldSet(properties);
  }

  public File file(String... properties) {
    return new File(properties);
  }

  public Form form(String... properties) {
    return new Form(properties);
  }

  public TableHeader header(String... properties) {
    return new TableHeader(properties);
  }

  public Label label(String... properties) {
    return new Label(properties);
  }

  public Legend legend(String... properties) {
    return new Legend(properties);
  }

  public ListItem listItem(String... properties) {
    return new ListItem(properties);
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

  public Option option(String text, Object value) {
    return new Option(text, value);
  }

  public Option option(String text, Object value, boolean isSelected) {
    return new Option(text, value, isSelected);
  }

  public OptionGroup optionGroup(String label) {
    return new OptionGroup(label);
  }

  public OrderedList orderedList(String... properties) {
    return new OrderedList(properties);
  }

  public Radio radio(String... properties) {
    return new Radio(properties);
  }

  public PercentRange range(String... properties) {
    return new PercentRange(properties);
  }

  public TableRow row(String... properties) {
    return new TableRow(properties);
  }

  public Submit submit(String... properties) {
    return new Submit(properties);
  }

  public Table table(String... properties) {
    return new Table(properties);
  }

  public Parent tbody(String... properties) {
    return new TableBody(properties);
  }

  public TableData td(String... properties) {
    return new TableData(properties);
  }

  public TextElement text(Object value) {
    return new TextElement(value);
  }

  public TextInput textInput(String... properties) {
    return new TextInput(properties);
  }

  public TableColumnHeader th(String... properties) {
    return new TableColumnHeader(properties);
  }

  public TableHeader thead(String... properties) {
    return new TableHeader(properties);
  }

  public TableRow tr(String... properties) {
    return new TableRow(properties);
  }

  public UnorderedList unorderedList(String... properties) {
    return new UnorderedList(properties);
  }

}