"use strict";

var musicPad = musicPad || {};

musicPad.moveData = {};
musicPad.zIndex = 0;

musicPad.createNode = function(html) {
    let template = document.createElement('template');
    template.innerHTML = html;
    let fragment = document.importNode(template.content, true);
    let newNode = fragment.firstChild;
    return newNode;
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

musicPad.ensureVisible = function(element) {
    const scrollParent = musicPad.getScrollParent(element);
    if (scrollParent) {
        const elementRight = element.offsetLeft + element.offsetWidth;
        const screenRight = scrollParent.scrollLeft + scrollParent.offsetWidth;
        if (element.offsetLeft < scrollParent.scrollLeft) {
            scrollParent.scrollLeft = element.offsetLeft;
        } else if (elementRight > screenRight) {
            scrollParent.scrollLeft = element.offsetLeft + element.offsetWidth - (scrollParent.offsetWidth - 20);
        }
        const elementBottom = element.offsetTop + element.offsetHeight;
        const screenBottom = scrollParent.scrollTop + scrollParent.offsetHeight;
        if (element.offsetTop < scrollParent.scrollTop) {
            scrollParent.scrollTop = element.offsetTop;
        } else if (elementBottom > screenBottom) {
            scrollParent.scrollTop = element.offsetTop + element.offsetHeight - (scrollParent.offsetHeight - 20);
        }
    }
}

musicPad.getScrollParent = function(node) {
    if (node == null) {
        return null;
    }
    if (node.scrollHeight > node.clientHeight || node.scrollWidth > node.clientWidth) {
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

musicPad.onMoveDrop = function(event) {
    event.preventDefault();
    const moveData = musicPad.moveData;
    const currentX = event.currentTarget.scrollLeft + event.x;
    const currentY = event.currentTarget.scrollTop + event.y;
    const deltaX = currentX - moveData.x;
    const deltaY = currentY - moveData.y;
    const element = moveData.element;
    let x = element.offsetLeft + deltaX;
    let y = element.offsetTop + deltaY;
    x = Math.round(x / 5) * 5;
    y = Math.round(y / 5) * 5;
    element.style.left = x + "px";
    element.style.top = y + "px";
    musicPad.ensureVisible(element);
    console.log("onMoveDrop: dropping at x=" + x + ", y=" + y);
    musicPad.send(JSON.stringify({
        type: 'OnBrowserEvent',
        action: 'MOVE',
        id: musicPad.moveData.element.id,
        value: JSON.stringify({
            x: x,
            y: y
        })
    }));
}

musicPad.onMoveEnd = function(event) {
    musicPad.moveData.element.style.visibility = "visible";
    console.log("Making " + musicPad.moveData.element.id + " visible, left=" + musicPad.moveData.element.offsetLeft);
}

musicPad.onMoveOver = function(event) {
    event.preventDefault();
}

musicPad.onMoveStart = function(event) {
    const element = event.target;
    const scrollParent = element.closest(".scrollable");
    musicPad.moveData = {
        x: scrollParent.scrollLeft + event.x,
        y: scrollParent.scrollTop + event.y,
        element: element
    }
    element.style.zIndex = ++musicPad.zIndex;
    window.requestAnimationFrame(function() {
        element.style.visibility = "hidden";
    });
}

musicPad.onShadowUpdate = function(message) {
    let matches = undefined;
    switch (message.action) {
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
        musicPad.ensureVisible(element);
        break;
    case 'SET_PROPERTY':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            if (!match.matches(':active')) {
                match[message.name] = message.value;
            }
        }
        break;
    case 'APPEND_CHILD':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            let newNode = musicPad.createNode(message.value);
            match.appendChild(newNode);
        }
        break;
    case 'INSERT_BEFORE':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            let newNode = musicPad.createNode(message.value);
            match.parentElement.insertBefore(newNode, match);
        }
        break;
    case 'REMOVE_CHILD':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            match.parentElement.removeChild(match);
        }
        break;
    case 'REPLACE_CHILDREN':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            match.scrollTop = 0;
            match.innerHTML = message.value;
        }
        break;
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
