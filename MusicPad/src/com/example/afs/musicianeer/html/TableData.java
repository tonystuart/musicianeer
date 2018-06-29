// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicianeer.html;

public class TableData extends Parent {

  public TableData(String... properties) {
    super("td", properties);
  }

  public TableData colSpan(int colSpan) {
    // NB: colspan value is not quoted, see https://html.spec.whatwg.org/multipage/tables.html
    setProperty("colspan", colSpan);
    return this;
  }

  public TableData rowSpan(int rowSpan) {
    // NB: rowspan value is not quoted, see https://html.spec.whatwg.org/multipage/tables.html
    setProperty("rowspan", rowSpan);
    return this;
  }

}