"use strict";
var cockpit = cockpit || {};

cockpit.ticksPerPixel = 1;
cockpit.connected = false;

cockpit.createWebSocketClient = function() {
  cockpit.ws = new WebSocket("ws://localhost:8080/v1/message");
  cockpit.ws.onopen = function() {
    console.log("ws.onopen: entered");
    cockpit.connected = true;
  }
  cockpit.ws.onmessage = function(message) {
    cockpit.processResponse(message.data);
  }
  cockpit.ws.onclose = function() {
    console.log("ws.onclose: entered, connecting again in 1000 ms.");
    cockpit.connected = false;
    let scroller = document.getElementById("notator-scroller");
    scroller.scrollLeft = 0;
    setTimeout(cockpit.createWebSocketClient, 1000);
  }
}

cockpit.appendTemplate = function(containerId, templateId) {
  let container = document.getElementById(containerId);
  let template = document.getElementById(templateId); 
  container.appendChild(template.content.cloneNode(true));
  let value = container.getAttribute("value");
  if (value) {
    container.value = value;
  }
}

cockpit.fragmentToElement = function(fragment) {
  let container = document.createElement("div");
  container.innerHTML = fragment;
  return container.firstElementChild;
}

cockpit.onClick = function(event) {
  let id = event.target.id;
}

cockpit.onDeviceDetached = function(response) {
  let notator = document.getElementById("notator-" + response.deviceIndex);
  if (notator) {
    notator.parentNode.removeChild(notator);
  }
}

cockpit.onHeader = function(response) {
  let header = document.getElementById("header");
  cockpit.title = response.title;
  header.innerHTML = response.html;
  cockpit.appendTemplate("title", "song-options");
  cockpit.appendTemplate("transpose", "transpose-options");
  cockpit.ticksPerPixel = response.ticksPerPixel;
}

cockpit.onFooter = function(response) {
  let footer = document.getElementById("footer");
  footer.innerHTML = response.html;
}

cockpit.onLoad = function() {
  cockpit.createWebSocketClient();
}

cockpit.onMusic = function(response) {
  let notator = document.createElement("div");
  notator.className = "notator";
  notator.innerHTML = response.html;
  let channelControls = cockpit.fragmentToElement(response.channelControls)
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
  cockpit.appendTemplate(programId, "program-options");
  cockpit.appendTemplate(inputId, "input-options");
  cockpit.appendTemplate(outputId, "output-options");
}

cockpit.onNotatorScroll = function(event) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  let mid = scroller.offsetWidth / 2; // start of svg
  let x1 = scroller.scrollLeft + mid;
  let x2 = cockpit.toSvg(svg, x1);
  let x3 = cockpit.toScreen(svg, x2);
  console.log("x1="+x1+", x2="+x2 + ", x3="+x3);
}

cockpit.toSvg = function(svg, x) {
  let screenPoint = svg.createSVGPoint();
  screenPoint.x = x;
  let ctm = svg.getScreenCTM();
  let inverse = ctm.inverse();
  let svgPoint = screenPoint.matrixTransform(inverse);
  return svgPoint.x;
}

cockpit.toScreen = function(svg, x) {
  let svgPoint = svg.createSVGPoint();
  svgPoint.x = x;
  let ctm = svg.getScreenCTM();
  let screenPoint = svgPoint.matrixTransform(ctm);
  return screenPoint.x;
}

cockpit.onTemplates = function(response) {
  let templates = document.getElementById('templates');
  templates.innerHTML = "";
  for (let i in response.templates) {
    let templateHtml = response.templates[i];
    let templateElement = cockpit.fragmentToElement(templateHtml);
    templates.appendChild(templateElement);
  }
}

cockpit.onTick = function(tick) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  if (svg) {
    let scaledTick = tick / cockpit.ticksPerPixel;
    let screenX = cockpit.toScreen(svg, scaledTick);
    let width = scroller.offsetWidth;
    let midPoint = width / 2;
    scroller.scrollLeft += screenX - midPoint;
    console.log("x1="+scaledTick+", x2="+screenX);
  }
}

cockpit.onTransport = function(response) {
  let transport = document.getElementById("transport");
  transport.innerHTML = response.html;
}

cockpit.processResponse = function(json) {
  let response = JSON.parse(json);
  switch (response.type) {
  case "OnHeader":
    cockpit.onHeader(response);
    break;
  case "OnFooter":
    cockpit.onFooter(response);
    break;
  case "OnTransport":
    cockpit.onTransport(response);
    break;
  case "OnMusic":
    cockpit.onMusic(response);
    break;
  case "OnTemplates":
    cockpit.onTemplates(response);
    break;
  case "OnTick":
    cockpit.onTick(response.tick);
    break;
  case "OnDeviceDetached":
    cockpit.onDeviceDetached(response);
    break;
  }
}

cockpit.request = function(resource) {
  let httpRequest = new XMLHttpRequest();
  httpRequest.onreadystatechange = function() {
    if (httpRequest.readyState == 4 && httpRequest.status == 200) {
      cockpit.processResponse(httpRequest.responseText);
    }
  };
  httpRequest.open("GET", "v1/rest/" + resource, true);
  httpRequest.send();
}

cockpit.send = function(json) {
  if (cockpit.connected) {
    cockpit.ws.send(json);
  }
}

cockpit.sendCommand = function(command, parameter) {
  console.log("command="+command+", parameter="+parameter);
  cockpit.send(JSON.stringify({type : "OnCommand", command : command, parameter : parameter}));
}

cockpit.sendChannelCommand = function(command, channel, parameter) {
  console.log("command="+command+", channel="+channel+", parameter="+parameter);
  cockpit.send(JSON.stringify({type : "OnChannelCommand", channelCommand : command, channel : channel, parameter: parameter}));
}

cockpit.sendDeviceCommand = function(command, deviceIndex, parameter) {
  console.log("command="+command+", deviceIndex="+deviceIndex+", parameter="+parameter);
  cockpit.send(JSON.stringify({type : "OnDeviceCommand", deviceCommand : command, deviceIndex : deviceIndex, parameter: parameter}));
}


