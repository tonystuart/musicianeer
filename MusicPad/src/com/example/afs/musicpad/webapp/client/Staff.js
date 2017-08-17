'use strict';
var staff = staff || {};

// NB: ticksPerPixel is initialized when the prompter is received

staff.ticksPerPixel = null;

staff.onLoad = function() {
    musicPad.createWebSocketClient('ws://localhost:8080/v1/staff', staff.onWebSocketMessage, staff.onWebSocketClose);
}

staff.onPrompter = function(message) {
    musicPad.replaceTab('prompter', message.html);
    staff.onTick(0);
}

staff.onTick = function(tick) {
    let scroller = document.getElementById('notator-scroller');
    let svg = scroller.querySelector('svg');
    if (svg) {
        let scaledTick = tick / staff.ticksPerPixel;
        let screenX = musicPad.toScreen(svg, scaledTick);
        let width = scroller.offsetWidth;
        let midPoint = width / 2;
        scroller.scrollLeft += screenX - midPoint;
        console.log('x1=' + scaledTick + ', x2=' + screenX);
    }
}

staff.onWebSocketClose = function() {
    staff.onTick(0);
}

staff.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnStaffPrompter':
        staff.ticksPerPixel = message.ticksPerPixel;
        staff.onPrompter(message);
        break;
    case 'OnTick':
        staff.onTick(message.tick);
        break;
    }
}

