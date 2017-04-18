"use strict";
var musicPad = musicPad || {};

musicPad.refreshIntervalMillis = 60000;
musicPad.noteNames = [ "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
musicPad.lastMessageNumber = -1;

musicPad.onPrompter = function(prompter) {
  var cells = [];
  var words = [];
  var table = document.createElement("TABLE");
  var columnCount = (prompter.highest - prompter.lowest) + 1;
  var rowCount = Math.floor(prompter.duration / prompter.resolution) + 20; // +20 due to unknown duration (we need highest ending tick)
  for (var i = 0; i < rowCount; i++) {
    var row = table.insertRow();
    words[i] = row.insertCell();
    cells[i] = [];
    for (var j = 0; j < columnCount; j++) {
      var cell = row.insertCell();
      cells[i][j] = cell;
    }
  }
  for (var w in prompter.words) {
    var word = prompter.words[w];
    var row = Math.floor(word.tick / prompter.resolution);
    words[row].innerHTML = musicPad.formatText(word.text);
  }
  for (var m in prompter.music) {
    var music = prompter.music[m];
    var row = Math.floor(music.tick / prompter.resolution);
    var column = music.note - prompter.lowest;
    var noteName = musicPad.formatNoteName(music.note)
    cells[row][column].innerHTML = noteName;
    var count = Math.floor(music.duration / prompter.resolution);
    for (var i = 1; i < count; i++) {
      cells[row+i][column].innerHTML = "|";
    }
  }
  var prompterContainer = document.getElementById("prompter-container");
  prompterContainer.innerHTML = "";
  prompterContainer.appendChild(table);
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

musicPad.formatNoteName = function(note) {
  return musicPad.noteNames[note % musicPad.noteNames.length];
}

musicPad.onClick = function() {
}

musicPad.onLoad = function() {
  var ws = new WebSocket("ws://localhost:8080/v1/message");
  ws.onopen = function() {
    console.log("ws.onopen: entered");
  }
  ws.onmessage = function(message) {
    console.log("ws.onmessage: entered");
    musicPad.processResponse(message.data);
  }
  ws.onclose = function() {
    console.log("ws.onclose: entered");
  }
}

musicPad.request = function(resource) {
  var httpRequest = new XMLHttpRequest();
  httpRequest.onreadystatechange = function() {
    if (httpRequest.readyState == 4 && httpRequest.status == 200) {
      musicPad.processResponse(httpRequest.responseText);
    }
  };
  httpRequest.open("GET", "v1/rest/" + resource, true);
  httpRequest.send();
}

musicPad.processResponse = function(json) {
  var response = JSON.parse(json);
  switch (response.type) {
  case "OnPrompter":
    musicPad.onPrompter(response.prompter);
  }
}
