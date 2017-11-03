// Copyright 2017 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.task;

import java.util.concurrent.SynchronousQueue;

import com.example.afs.musicpad.Conductor.MidiFiles;

public class ServiceTask extends MessageTask {

  public class OnServiceRequested implements Message {
    private SynchronousQueue<MidiFiles> rendezvous;

    public OnServiceRequested(SynchronousQueue<MidiFiles> rendezvous) {
      this.rendezvous = rendezvous;
    }

    public SynchronousQueue<MidiFiles> getRendezvous() {
      return rendezvous;
    }

  }

  protected ServiceTask(MessageBroker broker) {
    super(broker);
  }

}
