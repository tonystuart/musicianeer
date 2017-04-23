"use strict";
var musicPad = musicPad || {};

musicPad.refreshIntervalMillis = 60000;
musicPad.lastMessageNumber = -1;

musicPad.onPrompterData = function(data) {
  var cells = [];
  var words = [];
  var template = document.getElementById("prompter");
  var prompter = template.cloneNode(true);
  prompter.id = "prompter-" + data.index;
  var title = prompter.querySelector(".title");
  title.innerHTML = data.title + " (" + data.channel + ")";
  var table = prompter.querySelector("table");
  var columnCount = (data.highest - data.lowest) + 1;
  var rowCount = Math.floor(data.duration / data.resolution) + 1;
  for (var i = 0; i < rowCount; i++) {
    var row = table.insertRow();
    var rowNumber = row.insertCell();
    rowNumber.innerHTML = i + 1;
    words[i] = row.insertCell();
    cells[i] = [];
    for (var j = 0; j < columnCount; j++) {
      var cell = row.insertCell();
      cells[i][j] = cell;
    }
  }
  for (var w in data.words) {
    var word = data.words[w];
    var row = Math.floor(word.tick / data.resolution);
    words[row].innerHTML = musicPad.formatText(word.text);
  }
  for (var m in data.music) {
    var music = data.music[m];
    var row = Math.floor(music.tick / data.resolution);
    var column = music.note - data.lowest;
    var noteName = data.names[music.note - data.lowest];
    cells[row][column].innerHTML = noteName;
    var count = Math.floor(music.duration / data.resolution);
    for (var i = 1; i < count; i++) {
      cells[row+i][column].innerHTML = "|";
    }
  }
  var prompters = document.getElementById("prompters");
  var oldPrompter = document.getElementById(prompter.id);
  if (oldPrompter) {
    prompters.replaceChild(prompter, oldPrompter);
  } else {
    prompters.appendChild(prompter);
  }
}

musicPad.onTick = function(tick) {
  console.log("tick="+tick);  
  var index = tick / 512; // resolution
  var prompters = document.querySelector("#prompters").childNodes;
  for (var i = 0; i < prompters.length; i++) {
    var prompter = prompters[i];
    var table = prompter.querySelector("table");
    var rows = table.rows;
    var offsetTop = rows[index].offsetTop;
    var music = prompter.querySelector(".music");
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
  var ws = new WebSocket("ws://localhost:8080/v1/message");
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
  case "OnPrompterData":
    musicPad.onPrompterData(response.prompterData);
    break;
  case "OnTick":
    musicPad.onTick(response.tick);
    break;
  }
}
