'use strict';
var staff = staff || {};

staff.ticksPerPixel = 5;

staff.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/staff';
    musicPad.createWebSocketClient(url, staff.onWebSocketMessage, staff.onWebSocketClose);
}

staff.onTick = function(tick) {
    let scroller = document.getElementById('staff-scroller');
    if (scroller) {
        let svg = scroller.querySelector('svg');
        if (svg) {
            let scaledTick = tick / staff.ticksPerPixel;
            let screenX = musicPad.toScreen(svg, scaledTick);
            let width = scroller.offsetWidth;
            let midPoint = width / 2;
            scroller.scrollLeft += screenX - midPoint;
        }
    }
}

staff.onWebSocketClose = function() {
    staff.onTick(0);
}

staff.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    case 'OnTick':
        staff.onTick(message.tick);
        break;
    }
}
