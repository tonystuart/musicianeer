// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.html;

public class TableRow extends Hypertext {

  public TableRow() {
    super("tr");
  }

  public Element append(Element element) {
    TableData tableData = new TableData();
    tableData.appendChild(element);
    appendChild(tableData);
    return tableData;
  }

  /**
   * Converts value to a string and appends it to the TableRow as TableData.
   * 
   * @param value
   *          value to convert to a string
   * @return the newly created TableData
   */
  public TableData append(Object value) {
    TableData tableData = new TableData(value);
    appendChild(tableData);
    return tableData;
  }

}