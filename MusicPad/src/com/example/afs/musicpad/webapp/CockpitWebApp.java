// Copyright 2016 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.afs.musicpad.webapp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.example.afs.musicpad.message.Message;
import com.example.afs.musicpad.message.OnDeviceDetached;
import com.example.afs.musicpad.message.OnFooter;
import com.example.afs.musicpad.message.OnHeader;
import com.example.afs.musicpad.message.OnStaff;
import com.example.afs.musicpad.message.OnTemplates;
import com.example.afs.musicpad.message.OnTick;
import com.example.afs.musicpad.message.OnTransport;
import com.example.afs.musicpad.renderer.cockpit.CockpitRenderer;
import com.example.afs.musicpad.util.Broker;

public class CockpitWebApp extends WebApp {

  @SuppressWarnings("unused")
  private static final Logger LOG = Log.getLogger(CockpitWebApp.class);

  private Map<Integer, Message> deviceMusic = new ConcurrentHashMap<>();

  public CockpitWebApp(Broker<Message> broker, CockpitWebAppFactory cockpitWebAppFactory) {
    super(broker, cockpitWebAppFactory);
    setRenderer(new CockpitRenderer(broker));
    subscribe(OnTemplates.class, message -> doStatefulMessage(message));
    subscribe(OnHeader.class, message -> doStatefulMessage(message));
    subscribe(OnFooter.class, message -> doStatefulMessage(message));
    subscribe(OnTransport.class, message -> doStatefulMessage(message));
    subscribe(OnStaff.class, message -> doMusic(message));
    subscribe(OnTick.class, message -> doMessage(message));
    subscribe(OnDeviceDetached.class, message -> doDeviceDetached(message));
  }

  @Override
  public void onWebSocketConnection(WebSocket webSocket) {
    super.onWebSocketConnection(webSocket);
    for (Message musicMessage : deviceMusic.values()) {
      webSocket.write(musicMessage);
    }
  }

  private void doDeviceDetached(OnDeviceDetached message) {
    deviceMusic.remove(message.getDeviceIndex());
    doMessage(message);
  }

  private void doMusic(OnStaff message) {
    deviceMusic.put(message.getDeviceIndex(), message);
    doMessage(message);
  }

}
