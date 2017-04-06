// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.device.common;

public class ControllableGroup {

  public interface Controllable {
    public void start();

    public void terminate();
  }

  private Controllable[] devices;

  public ControllableGroup(Controllable... devices) {
    this.devices = devices;
  }

  public void start() {
    for (Controllable device : devices) {
      device.start();
    }
  }

  public void terminate() {
    for (Controllable device : devices) {
      device.terminate();
    }
  }

}
