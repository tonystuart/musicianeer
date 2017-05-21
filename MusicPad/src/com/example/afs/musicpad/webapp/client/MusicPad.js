"use strict";
var musicPad = musicPad || {};

musicPad.ticksPerPixel = 1;
musicPad.connected = false;

musicPad.createWebSocketClient = function() {
  musicPad.ws = new WebSocket("ws://localhost:8080/v1/message");
  musicPad.ws.onopen = function() {
    console.log("ws.onopen: entered");
    musicPad.connected = true;
  }
  musicPad.ws.onmessage = function(message) {
    musicPad.processResponse(message.data);
  }
  musicPad.ws.onclose = function() {
    console.log("ws.onclose: entered, connecting again in 1000 ms.");
    musicPad.connected = false;
    let scroller = document.getElementById("notator-scroller");
    scroller.scrollLeft = 0;
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
  let outputId = "output-select-" + response.deviceIndex;
  musicPad.appendTemplate(programId, "program-options");
  musicPad.appendTemplate(inputId, "input-options");
  musicPad.appendTemplate(outputId, "output-options");
}

musicPad.onNotatorScroll = function(event) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  let mid = scroller.offsetWidth / 2; // start of svg
  let x1 = scroller.scrollLeft + mid;
  let x2 = musicPad.toSvg(svg, x1);
  let x3 = musicPad.toScreen(svg, x2);
  console.log("x1="+x1+", x2="+x2 + ", x3="+x3);
}

musicPad.toSvg = function(svg, x) {
  let screenPoint = svg.createSVGPoint();
  screenPoint.x = x;
  let ctm = svg.getScreenCTM();
  let inverse = ctm.inverse();
  let svgPoint = screenPoint.matrixTransform(inverse);
  return svgPoint.x;
}

musicPad.toScreen = function(svg, x) {
  let svgPoint = svg.createSVGPoint();
  svgPoint.x = x;
  let ctm = svg.getScreenCTM();
  let screenPoint = svgPoint.matrixTransform(ctm);
  return screenPoint.x;
}

musicPad.onTemplates = function(response) {
  let templates = document.getElementById('templates');
  templates.innerHTML = "";
  for (let i in response.templates) {
    let templateHtml = response.templates[i];
    let templateElement = musicPad.fragmentToElement(templateHtml);
    templates.appendChild(templateElement);
  }
}

musicPad.onTick = function(tick) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  if (svg) {
    let scaledTick = tick / musicPad.ticksPerPixel;
    let screenX = musicPad.toScreen(svg, scaledTick);
    let width = scroller.offsetWidth;
    let midPoint = width / 2;
    scroller.scrollLeft += screenX - midPoint;
    console.log("x1="+scaledTick+", x2="+screenX);
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

musicPad.send = function(json) {
  if (musicPad.connected) {
    musicPad.ws.send(json);
  }
}

musicPad.sendCommand = function(command, parameter) {
  console.log("command="+command+", parameter="+parameter);
  musicPad.send(JSON.stringify({type : "OnCommand", command : command, parameter : parameter}));
}

musicPad.sendChannelCommand = function(command, channel, parameter) {
  console.log("command="+command+", channel="+channel+", parameter="+parameter);
  musicPad.send(JSON.stringify({type : "OnChannelCommand", channelCommand : command, channel : channel, parameter: parameter}));
}

musicPad.sendDeviceCommand = function(command, deviceIndex, parameter) {
  console.log("command="+command+", deviceIndex="+deviceIndex+", parameter="+parameter);
  musicPad.send(JSON.stringify({type : "OnDeviceCommand", deviceCommand : command, deviceIndex : deviceIndex, parameter: parameter}));
}


