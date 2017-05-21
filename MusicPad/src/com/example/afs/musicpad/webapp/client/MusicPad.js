"use strict";
var musicPad = musicPad || {};

musicPad.ticksPerPixel = 1;

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

musicPad.appendTemplate = function(containerId, templateId) {
  let container = document.getElementById(containerId);
  let template = document.getElementById(templateId); 
  container.appendChild(template.content.cloneNode(true));
  let value = container.getAttribute("value");
  if (value) {
    container.value = value;
  }
}

musicPad.fragmentToElement = function(fragment) {
  let container = document.createElement("div");
  container.innerHTML = fragment;
  return container.firstElementChild;
}

musicPad.onClick = function(event) {
  let id = event.target.id;
}

musicPad.onDeviceDetached = function(response) {
  let notator = document.getElementById("notator-" + response.deviceIndex);
  if (notator) {
    notator.parentNode.removeChild(notator);
  }
}

musicPad.onHeader = function(response) {
  let header = document.getElementById("header");
  musicPad.title = response.title;
  header.innerHTML = response.html;
  musicPad.appendTemplate("title", "song-options");
  musicPad.appendTemplate("transpose", "transpose-options");
  musicPad.ticksPerPixel = response.ticksPerPixel;
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
  let channelControls = musicPad.fragmentToElement(response.channelControls)
  notator.id = "notator-" + response.deviceIndex;
  notator.appendChild(channelControls);
  let oldNotator = document.getElementById(notator.id);
  if (oldNotator) {
    oldNotator.parentElement.replaceChild(notator, oldNotator);
  } else {
    let scroller = document.getElementById("notator-scroller");
    scroller.appendChild(notator);
  }
  let programId = "program-select-" + response.deviceIndex;
  let inputId = "input-select-" + response.deviceIndex;
  musicPad.appendTemplate(programId, "program-options");
  musicPad.appendTemplate(inputId, "input-options");
}

musicPad.onTemplates = function(response) {
  var templates = document.getElementById('templates');
  for (let i in response.templates) {
    let templateHtml = response.templates[i];
    let templateElement = musicPad.fragmentToElement(templateHtml);
    templates.appendChild(templateElement);
  }
}

musicPad.onTick = function(tick) {
  let scaledTick = tick / musicPad.ticksPerPixel;
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
  case "OnTemplates":
    musicPad.onTemplates(response);
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

musicPad.sendCommand = function(command, parameter) {
  console.log("command="+command+", parameter="+parameter);
  musicPad.ws.send(JSON.stringify({type : "OnCommand", command : command, parameter : parameter}));
}

musicPad.sendChannelCommand = function(command, channel, parameter) {
  console.log("command="+command+", channel="+channel+", parameter="+parameter);
  musicPad.ws.send(JSON.stringify({type : "OnChannelCommand", channelCommand : command, channel : channel, parameter: parameter}));
}

musicPad.sendDeviceCommand = function(command, deviceIndex, parameter) {
  console.log("command="+command+", deviceIndex="+deviceIndex+", parameter="+parameter);
  musicPad.ws.send(JSON.stringify({type : "OnDeviceCommand", deviceCommand : command, deviceIndex : deviceIndex, parameter: parameter}));
}


