"use strict";
var musicPad = musicPad || {};

musicPad.refreshIntervalMillis = 60000;
musicPad.lastMessageNumber = -1;

musicPad.onDeviceDetached = function(response) {
  let prompter = document.getElementById("prompter-" + response.index);
  if (prompter) {
    prompter.parentNode.removeChild(prompter);
  }
}

musicPad.onMusic = function(response) {
  let prompter = document.createElement("div");
  prompter.id = "prompter-" + response.index;
  prompter.className = "prompter";
  prompter.innerHTML = response.music;
  let music = document.getElementById("music");
  let oldPrompter = document.getElementById(prompter.id);
  if (oldPrompter) {
    music.replaceChild(prompter, oldPrompter);
  } else {
    music.appendChild(prompter);
  }
}

musicPad.onTick = function(tick) {
  let scaledTick = tick / 10;
  let music = document.getElementById("music");
  let width = music.offsetWidth;
  let midPoint = width / 2;
  music.scrollLeft = scaledTick + midPoint;
  console.log("scaledTick="+scaledTick+", width="+width+", midPoint="+midPoint+", scrollLeft="+music.scrollLeft);
}

musicPad.formatText = function(text) {
  if (text.startsWith("\\") || text.startsWith("/")) {
    text = text.substring(1);
  }
  if (text.length > 10) {
    text = text.substring(0, 10);
  }
  return text;
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
