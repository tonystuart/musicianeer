"use strict";
var musicPad = musicPad || {};

musicPad.addClassToAllBut = function(className, selector, id) {
    let nodeList = document.querySelectorAll(selector);
    for (let i = 0; i < nodeList.length; i++) {
        nodeList[i].classList.add(className);
    }
    document.getElementById(id).classList.remove(className);
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

musicPad.fragmentToElement = function(fragment) {
    let container = document.createElement("div");
    container.innerHTML = fragment;
    return container.firstElementChild;
}

musicPad.getRandomInt = function(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min;
}

musicPad.replaceTab = function(id, html) {
    let tab = document.getElementById(id);
    if (tab != null) {
        tab.parentElement.removeChild(tab);
    }
    let element = musicPad.fragmentToElement(html);
    document.body.appendChild(element);
    musicPad.selectTab(id);
    return element;
}

musicPad.request = function(resource, callback) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.owebSocketUrlnreadystatechange = function() {
        if (httpRequest.readyState == 4 && httpRequest.status == 200) {
            callback(httpRequest.responseText);
        }
    }
    httpRequest.open("GET", "v1/rest/" + resource, true);
    httpRequest.send();
}

musicPad.selectElement = function(element) {
    let container = element.parentElement;
    let previous = container.querySelector(".selected");
    if (previous) {
        previous.classList.remove("selected");
    }
    element.classList.add("selected");
}

musicPad.selectTab = function(id) {
    musicPad.addClassToAllBut("hidden", ".tab", id);
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

musicPad.sendChannelCommand = function(command, channel, parameter) {
    console.log("command=" + command + ", channel=" + channel + ", parameter=" + parameter);
    musicPad.send(JSON.stringify({
        type: "OnChannelCommand",
        channelCommand: command,
        channel: channel,
        parameter: parameter
    }));
}

musicPad.sendDeviceCommand = function(command, deviceIndex, parameter) {
    console.log("command=" + command + ", deviceIndex=" + deviceIndex + ", parameter=" + parameter);
    musicPad.send(JSON.stringify({
        type: "OnDeviceCommand",
        deviceCommand: command,
        deviceIndex: deviceIndex,
        parameter: parameter
    }));
}

musicPad.setElementHtml = function(selector, value) {
    let elements = document.querySelectorAll(selector);
    for (const element of elements) {
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

musicPad.setElementValue = function(selector, value) {
    musicPad.setElementProperty(selector, 'value', value);
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

musicPad.toValue = function(index) {
    return index + 1;
}