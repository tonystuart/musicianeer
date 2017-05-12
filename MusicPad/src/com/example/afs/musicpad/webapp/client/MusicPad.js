"use strict";
var musicPad = musicPad || {};

musicPad.refreshIntervalMillis = 60000;
musicPad.lastMessageNumber = -1;

musicPad.onDeviceDetached = function(response) {
  let notator = document.getElementById("notator-" + response.index);
  if (notator) {
    notator.parentNode.removeChild(notator);
  }
}

musicPad.getElement = function(html) {
  var template = document.createElement('template');
  template.innerHTML = html;
  return template.content.firstChild;
}

musicPad.onMusic = function(response) {
  let notator = document.createElement("div");
  notator.className = "notator";
  notator.innerHTML = response.music;
  notator.id = "notator-" + response.index;
  let oldNotator = document.getElementById(notator.id);
  if (oldNotator) {
    oldNotator.parentElement.replaceChild(notator, oldNotator);
  } else {
    let scroller = document.getElementById("notator-scroller");
    scroller.appendChild(notator);
  }
}

musicPad.onTick = function(tick) {
  let scaledTick = tick / 10;
  let scroller = document.getElementById("notator-scroller");
  let width = scroller.offsetWidth;
  let midPoint = width / 2;
  let firstNotatorSvg = scroller.querySelector("svg");
  if (firstNotatorSvg) {
    let point = firstNotatorSvg.createSVGPoint();
    point.x = scaledTick;
    point.y = 0;
    let t = point.matrixTransform(firstNotatorSvg.getScreenCTM());
    scroller.scrollLeft += t.x - midPoint;
  }
}

musicPad.onClick = function() {
}

musicPad.onLoad = function() {
  musicPad.createWebSocketClient();
}

musicPad.createWebSocketClient = function() {
  let ws = new WebSocket("ws://localhost:8080/v1/message");
  ws.onopen = function() {
    console.log("ws.onopen: entered");
  }
  ws.onmessage = function(message) {
    musicPad.processResponse(message.data);
  }
  ws.onclose = function() {
    console.log("ws.onclose: entered, connecting again in 1000 ms.");
    setTimeout(musicPad.createWebSocketClient, 1000);
  }
}

musicPad.request = function(resource) {
  let httpRequest = new XMLHttpRequest();
  httpRequest.onreadystatechange = function() {
    if (httpRequest.readyState == 4 && httpRequest.status == 200) {
      musicPad.processResponse(httpRequest.responseText);
    }
  };
  httpRequest.open("GET", "v1/rest/" + resource, true);
  httpRequest.send();
}

musicPad.processResponse = function(json) {
  let response = JSON.parse(json);
  switch (response.type) {
  case "OnDeviceDetached":
    musicPad.onDeviceDetached(response);
    break;
  case "OnMusic":
    musicPad.onMusic(response);
    break;
  case "OnTick":
    musicPad.onTick(response.tick);
    break;
  }
}
