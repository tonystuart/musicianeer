"use strict";
var musicPad = musicPad || {};

musicPad.songList = null;
musicPad.title = null;

musicPad.createWebSocketClient = function() {
  musicPad.ws = new WebSocket("ws://localhost:8080/v1/message");
  musicPad.ws.onopen = function() {
    console.log("ws.onopen: entered");
  }
  musicPad.ws.onmessage = function(message) {
    musicPad.processResponse(message.data);
  }
  musicPad.ws.onclose = function() {
    console.log("ws.onclose: entered, connecting again in 1000 ms.");
    setTimeout(musicPad.createWebSocketClient, 1000);
  }
}

musicPad.onClick = function(event) {
  let id = event.target.id;
  switch (id) {
  case "play":
    musicPad.ws.send(JSON.stringify({type: "OnCommand", command: "PLAY_PAUSE", parameter: 0}));
    break;
  case "stop":
    musicPad.ws.send(JSON.stringify({type: "OnCommand", command: "STOP_PAUSE", parameter: 0}));
    break;
  default:
    musicPad.ws.send(JSON.stringify({type: "OnClick", id: id}))
    break;
  }
}

musicPad.onDeviceDetached = function(response) {
  let notator = document.getElementById("notator-" + response.deviceIndex);
  if (notator) {
    notator.parentNode.removeChild(notator);
  }
}

musicPad.getElement = function(html) {
  var template = document.createElement('template');
  template.innerHTML = html;
  return template.content.firstChild;
}

musicPad.onHeader = function(response) {
  let header = document.getElementById("header");
  musicPad.title = response.title;
  header.innerHTML = response.html;
  musicPad.setTitleSongList();
}

musicPad.onFooter = function(response) {
  let footer = document.getElementById("footer");
  footer.innerHTML = response.html;
}

musicPad.onLoad = function() {
  musicPad.createWebSocketClient();
}

musicPad.onMusic = function(response) {
  let notator = document.createElement("div");
  notator.className = "notator";
  notator.innerHTML = response.html;
  notator.id = "notator-" + response.deviceIndex;
  let oldNotator = document.getElementById(notator.id);
  if (oldNotator) {
    oldNotator.parentElement.replaceChild(notator, oldNotator);
  } else {
    let scroller = document.getElementById("notator-scroller");
    scroller.appendChild(notator);
  }
}

musicPad.onSongList = function(songList) {
  musicPad.songList = songList;
  musicPad.setTitleSongList();
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

musicPad.onTitleChange = function() {
  let title = document.getElementById("title");
  let titleIndex = title.options[title.selectedIndex].value;
  console.log("titleIndex="+titleIndex);
  musicPad.ws.send(JSON.stringify({type: "OnCommand", command: "SELECT_SONG", parameter: parseInt(titleIndex) + 1}));
}

musicPad.onTransport = function(response) {
  let transport = document.getElementById("transport");
  transport.innerHTML = response.html;
}

musicPad.processResponse = function(json) {
  let response = JSON.parse(json);
  switch (response.type) {
  case "OnHeader":
    musicPad.onHeader(response);
    break;
  case "OnFooter":
    musicPad.onFooter(response);
    break;
  case "OnTransport":
    musicPad.onTransport(response);
    break;
  case "OnMusic":
    musicPad.onMusic(response);
    break;
  case "OnSongList":
    musicPad.onSongList(response.songList);
    break;
  case "OnTick":
    musicPad.onTick(response.tick);
    break;
  case "OnDeviceDetached":
    musicPad.onDeviceDetached(response);
    break;
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

musicPad.setTitleSongList = function() {
  let title = document.getElementById("title");
  if (title && musicPad.songList) {
    title.innerHTML = "";
    for (let i = 0; i < musicPad.songList.length; i++) {
      let option = document.createElement("option");
      option.value = i;
      option.innerHTML = musicPad.songList[i];
      if (musicPad.songList[i] == musicPad.title) {
        option.selected = true;
      }
      title.appendChild(option);
    }
    title.onchange = function(){musicPad.onTitleChange();}
  }
}
