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

