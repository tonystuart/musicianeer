"use strict";
var cockpit = cockpit || {};

cockpit.ticksPerPixel = 1;

cockpit.onClick = function(event) {
  let id = event.target.id;
}

cockpit.onDeviceDetached = function(response) {
  let notator = document.getElementById("notator-" + response.deviceIndex);
  if (notator) {
    notator.parentNode.removeChild(notator);
  }
}

cockpit.onFooter = function(response) {
  let footer = document.getElementById("footer");
  footer.innerHTML = response.html;
}

cockpit.onHeader = function(response) {
  let header = document.getElementById("header");
  cockpit.title = response.title;
  header.innerHTML = response.html;
  musicPad.appendTemplate("title", "song-options");
  musicPad.appendTemplate("transpose", "transpose-options");
  cockpit.ticksPerPixel = response.ticksPerPixel;
}

cockpit.onLoad = function() {
  musicPad.createWebSocketClient("ws://localhost:8080/v1/cockpit", cockpit.onWebSocketMessage, cockpit.onWebSocketClose);
}

cockpit.onNotatorScroll = function(event) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  let mid = scroller.offsetWidth / 2; // start of svg
  let x1 = scroller.scrollLeft + mid;
  let x2 = musicPad.toSvg(svg, x1);
  let x3 = musicPad.toScreen(svg, x2);
  console.log("x1="+x1+", x2="+x2 + ", x3="+x3);
}

cockpit.onStaff = function(response) {
  let notator = document.createElement("div");
  notator.className = "notator";
  notator.innerHTML = response.staffHtml;
  let channelControls = musicPad.fragmentToElement(response.channelHtml)
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

cockpit.onTemplates = function(response) {
  let templates = document.getElementById('templates');
  templates.innerHTML = "";
  for (let i in response.templates) {
    let templateHtml = response.templates[i];
    let templateElement = musicPad.fragmentToElement(templateHtml);
    templates.appendChild(templateElement);
  }
}

cockpit.onTick = function(tick) {
  let scroller = document.getElementById("notator-scroller");
  let svg = scroller.querySelector("svg");
  if (svg) {
    let scaledTick = tick / cockpit.ticksPerPixel;
    let screenX = musicPad.toScreen(svg, scaledTick);
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

cockpit.onWebSocketClose = function() {
  let scroller = document.getElementById("notator-scroller");
  scroller.scrollLeft = 0;
}

cockpit.onWebSocketMessage = function(json) {
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
  case "OnStaff":
    cockpit.onStaff(response);
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