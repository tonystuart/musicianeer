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

musicPad.getScrollParent = function(node) {
    if (node == null) {
        return null;
    }
    if (node.scrollHeight > node.clientHeight) {
        return node;
    } else {
        return musicPad.getScrollParent(node.parentNode);
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
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            match.scrollTop = 0;
            match.innerHTML = message.value;
        }
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
        let scrollParent = musicPad.getScrollParent(element);
        if (scrollParent) {
            let midpoint = scrollParent.offsetHeight / 2;
            let elementTop = element.offsetTop - scrollParent.offsetTop;
            if (elementTop < scrollParent.scrollTop || (elementTop + element.offsetHeight) > scrollParent.scrollTop + scrollParent.offsetHeight) {
                scrollParent.scrollTop = elementTop - midpoint;
            }
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
    case 'INSERT_ROW':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            let container = document.createElement('tbody');
            container.innerHTML = message.value;
            let content = container.firstElementChild;
            let row = match.insertRow(message.index);
            row.innerHTML = content.innerHTML;
            row.id = content.id;
        }
    }
}

musicPad.onSubmit = function(event) {
    event.preventDefault();
    let form = event.target.closest('form');
    if (form) {
        let fields = form.querySelectorAll('input, select');
        if (fields) {
            let values = {};
            for (let field of fields) {
                if (field.name) {
                    values[field.name] = field.value;
                }
            }
            musicPad.send(JSON.stringify({
                type: 'OnBrowserEvent',
                action: 'SUBMIT',
                id: form.id,
                value: JSON.stringify(values)
            }));
        }
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
