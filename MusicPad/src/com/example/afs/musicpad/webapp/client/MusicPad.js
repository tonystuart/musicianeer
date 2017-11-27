"use strict";
var musicPad = musicPad || {};

musicPad.createWebSocketClient = function(webSocketUrl, onMessageCallback, onCloseCallback) {
    musicPad.ws = new WebSocket(webSocketUrl);
    musicPad.ws.onopen = function() {
        console.log("ws.onopen: entered");
        musicPad.connected = true;
    }
    musicPad.ws.onmessage = function(message) {
        onMessageCallback(message.data);
    }
    musicPad.ws.onclose = function() {
        console.log("ws.onclose: entered, connecting again in 1000 ms.");
        musicPad.connected = false;
        if (onCloseCallback) {
            onCloseCallback();
        }
        setTimeout(function() {
            musicPad.createWebSocketClient(webSocketUrl, onMessageCallback, onCloseCallback);
        }, 1000);
    }
}

musicPad.onClick = function(event) {
  let id = event.target.id;
  if (!id) {
      id = event.target.closest('[id]').id;
  }
  musicPad.send(JSON.stringify({
      type: 'OnBrowserEvent',
      action: 'CLICK',
      id: id
  }));
}

musicPad.onInput = function(event, value) {
  const id = event.target.id;
  musicPad.send(JSON.stringify({
      type: 'OnBrowserEvent',
      action: 'INPUT',
      id: id,
      value: value
  }));
}

musicPad.onShadowUpdate = function(message) {
  let matches = undefined;
  switch (message.action) {
  case 'REPLACE_CHILDREN':
      musicPad.setElementHtml(message.selector, message.value);
      break;
  case 'ADD_CLASS':
      matches = document.querySelectorAll(message.selector);
      for (const match of matches) {
          match.classList.add(message.value);
      }
      break;
  case 'REMOVE_CLASS':
      matches = document.querySelectorAll(message.selector);
      for (const match of matches) {
          match.classList.remove(message.value);
      }
      break;
  case 'ENSURE_VISIBLE':
      let element = document.querySelector(message.selector);
      let grandparent = element.parentElement.parentElement;
      let midpoint = grandparent.offsetHeight / 2;
      let elementTop = element.offsetTop - grandparent.offsetTop;
      if (elementTop < grandparent.scrollTop || (elementTop + element.offsetHeight) > grandparent.scrollTop + grandparent.offsetHeight) {
          grandparent.scrollTop = elementTop - midpoint;
      }
      break;
  case 'SET_PROPERTY':
      matches = document.querySelectorAll(message.selector);
      for (const match of matches) {
          if (!match.matches(':active')) {
              match[message.name] = message.value;
          }
      }
      break;
  }
}

musicPad.send = function(json) {
    if (musicPad.connected) {
        musicPad.ws.send(json);
    }
}

musicPad.sendCommand = function(command, parameter) {
    console.log("command=" + command + ", parameter=" + parameter);
    musicPad.send(JSON.stringify({
        type: "OnCommand",
        command: command,
        parameter: parameter
    }));
}

musicPad.setElementHtml = function(selector, value) {
    let elements = document.querySelectorAll(selector);
    for (const element of elements) {
        element.scrollTop = 0;
        element.innerHTML = value;
    }
}

musicPad.setElementProperty = function(selector, property, value) {
    let elements = document.querySelectorAll(selector);
    for (const element of elements) {
        if (!element.matches(':active')) {
            element[property] = value;
        }
    }
}

musicPad.toScreen = function(svg, x) {
    let svgPoint = svg.createSVGPoint();
    svgPoint.x = x;
    let ctm = svg.getScreenCTM();
    let screenPoint = svgPoint.matrixTransform(ctm);
    return screenPoint.x;
}

musicPad.toSvg = function(svg, x) {
    let screenPoint = svg.createSVGPoint();
    screenPoint.x = x;
    let ctm = svg.getScreenCTM();
    let inverse = ctm.inverse();
    let svgPoint = screenPoint.matrixTransform(inverse);
    return svgPoint.x;
}

