"use strict";
var karaoke = karaoke || {};

karaoke.ticksPerPixel = 1;
karaoke.connected = false;

karaoke.createWebSocketClient = function() {
  karaoke.ws = new WebSocket("ws://localhost:8080/v1/message");
  karaoke.ws.onopen = function() {
    console.log("ws.onopen: entered");
    karaoke.connected = true;
  }
  karaoke.ws.onmessage = function(message) {
    karaoke.processResponse(message.data);
  }
  karaoke.ws.onclose = function() {
    console.log("ws.onclose: entered, connecting again in 1000 ms.");
    karaoke.connected = false;
    let scroller = document.getElementById("notator-scroller");
    scroller.scrollLeft = 0;
    setTimeout(karaoke.createWebSocketClient, 1000);
  }
}

karaoke.onLoad = function() {
  karaoke.createWebSocketClient();
}

karaoke.onKaraoke = function(response) {
  let karaoke = document.getElementById("karaoke");
  karaoke.innerHTML = response.karaoke;
}

karaoke.onTick = function(tick) {
}

karaoke.processResponse = function(json) {
  let response = JSON.parse(json);
  switch (response.type) {
  case "OnKaraoke":
    karaoke.onKaraoke(response);
    break;
  case "OnTick":
    karaoke.onTick(response.tick);
    break;
  }
}



