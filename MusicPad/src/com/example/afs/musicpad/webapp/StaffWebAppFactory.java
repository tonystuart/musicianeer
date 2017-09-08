// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

public class StaffWebAppFactory implements WebAppFactory {

  private StaffWebApp staffWebApp;

  public StaffWebAppFactory(StaffWebApp staffWebApp) {
    this.staffWebApp = staffWebApp;
  }

  @Override
  public WebApp getWebApp() {
    return staffWebApp;
  }
}
