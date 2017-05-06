"use strict";
var musicPad = musicPad || {};

musicPad.refreshIntervalMillis = 60000;
musicPad.lastMessageNumber = -1;

musicPad.onWords = function(response) {
  let words = document.getElementById("words");
  words.innerHTML = "";
  let spanCount = Math.floor(response.duration / response.resolution) + 1;
  for (let i = 0; i < spanCount; i++) {
    let span = document.createElement("span");
    span.innerHTML = "&nbsp;";
    words.appendChild(span);
  }
  for (let w in response.lyrics) {
    let lyric = response.lyrics[w];
    let index = Math.floor(lyric.tick / response.resolution);
    words.childNodes[index].innerHTML += musicPad.formatText(lyric.lyric);
  }
}

musicPad.onMusic = function(response) {
  let cells = [];
  let template = document.getElementById("prompter");
  let prompter = template.cloneNode(true);
  prompter.id = "prompter-" + response.index;
  prompter.className += " " + response.mappingType;
  let table = prompter.querySelector("table");
  let colgroup = prompter.querySelector("colgroup");
  let tbody = table.querySelector("tbody");
  let columnCount = (response.highest - response.lowest) + 1;
  let rowCount = Math.floor(response.duration / response.resolution) + 1;
  for (let i = 0; i < rowCount; i++) {
    let row = tbody.insertRow();
    let rowNumber = row.insertCell();
    rowNumber.innerHTML = i + 1;
    cells[i] = [];
    for (let j = 0; j < columnCount; j++) {
      let cell = row.insertCell();
      cells[i][j] = cell;
    }
  }
  for (let n in response.legend) {
    let col = document.createElement("col");
    let name = response.legend[n];
    if (name.endsWith("#") || name.startsWith("\u2191")) {
      col.className = "sharp";
    } else {
      col.className = "normal";
    }
    colgroup.appendChild(col);
  }
  console.log("Preparing to initialize music");
  for (let m in response.sounds) {
    let sound = response.sounds[m];
    let row = Math.floor(sound.tick / response.resolution);
    let column = sound.sound - response.lowest;
    let name = response.legend[sound.sound - response.lowest];
    cells[row][column].innerHTML = name;
    let count = Math.floor(sound.duration / response.resolution);
    for (let i = 1; i < count; i++) {
      cells[row+i][column].innerHTML = "|";
    }
  }
  let music = document.getElementById("music");
  let oldPrompter = document.getElementById(prompter.id);
  if (oldPrompter) {
    music.replaceChild(prompter, oldPrompter);
  } else {
    music.appendChild(prompter);
  }
}

musicPad.onTick = function(tick) {
  console.log("tick="+tick);  
  let index = tick / 512; // resolution
  let words = document.getElementById("words");
  let spans = words.children;
  if (index < spans.length) {
    let word = spans.item(index);
    words.scrollLeft = word.offsetLeft;
  }
  let music = document.getElementById("music");
  let prompter = music.firstChild;
  if (prompter) {
    let table = prompter.querySelector("table");
    let rows = table.rows;
    let offsetTop = rows[index].offsetTop;
    music.scrollTop = offsetTop;
    console.log("offsetTop="+offsetTop+", music.scrollTop="+music.scrollTop);
  }
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
    console.log("ws.onmessage: entered");
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
  case "OnWords":
    musicPad.onWords(response);
    break;
  case "OnMusic":
    musicPad.onMusic(response);
    break;
  case "OnTick":
    musicPad.onTick(response.tick);
    break;
  }
}
