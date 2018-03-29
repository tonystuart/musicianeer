// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp.musicianeer;

import com.example.afs.musicpad.message.OnCommand;
import com.example.afs.musicpad.message.OnDeviceCommand;
import com.example.afs.musicpad.message.OnShadowUpdate;
import com.example.afs.musicpad.message.OnShadowUpdate.Action;
import com.example.afs.musicpad.task.ControllerTask;
import com.example.afs.musicpad.task.MessageBroker;

public class MusicianeerController extends ControllerTask {

  private MusicianeerView musicianeerView;

  public MusicianeerController(MessageBroker broker) {
    super(broker);
    musicianeerView = new MusicianeerView(this);
    subscribe(OnCommand.class, message -> doCommand(message));
    subscribe(OnDeviceCommand.class, message -> doDeviceCommand(message));
  }

  @Override
  protected void doClick(String id) {
    if (id.startsWith("item-")) {
      musicianeerView.selectElement(id, "selected-item");
    }
  }

  @Override
  protected void doInput(String id, String value) {
  }

  @Override
  protected void doLoad() {
    addShadowUpdate(new OnShadowUpdate(Action.REPLACE_CHILDREN, "body", musicianeerView.render()));
  }

  private void doCommand(OnCommand message) {
  }

  private void doDeviceCommand(OnDeviceCommand message) {
  }

}
