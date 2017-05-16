// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class Table extends Hypertext {

  public Table() {
    super("table");
  }

  public TableHeader createHeader() {
    TableHeader tableHeader = new TableHeader();
    appendChild(tableHeader);
    return tableHeader;
  }

  public TableRow createRow() {
    TableRow tableRow = new TableRow();
    appendChild(tableRow);
    return tableRow;
  }

}