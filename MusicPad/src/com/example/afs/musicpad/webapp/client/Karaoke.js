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
  if (tick == 0) {
    document.getElementById("karaoke-scroller").scrollTop = 0;
    if (karaoke.lastTick) {
      karaoke.lastTick.classList.remove("current-tick");
    }
  }
  let tickDivision = document.getElementById("tick-"+tick);
  if (tickDivision) {
    if (karaoke.lastTick) {
      karaoke.lastTick.classList.remove("current-tick");
    }
    tickDivision.classList.add("current-tick");
    let scroller = document.getElementById("karaoke-scroller");
    let top = tickDivision.offsetTop;
    let height = tickDivision.offsetHeight;
    let bottom = top + height;
    let scrollerBottomVisible = scroller.scrollTop + scroller.offsetHeight;
    if (bottom + height > scrollerBottomVisible) {
      //scroller.scrollTop += tickDivision.offsetTop;
      tickDivision.scrollIntoView({ behavior : "smooth", block : "start"});
    }
    karaoke.lastTick = tickDivision;
  }
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



