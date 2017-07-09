// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class TableHeader extends Parent {

  private TableRow tableRow;

  public TableHeader() {
    super("thead");
    tableRow = new TableRow();
    appendChild(tableRow);
  }

  public TableHeading append(Object value) {
    TableHeading tableHeading = new TableHeading(value);
    tableRow.appendChild(tableHeading);
    return tableHeading;
  }
}