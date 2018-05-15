// Copyright 2018 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

'use strict';
var musicianeer = musicianeer || {};

musicianeer.ticksPerPixel = 5;
musicianeer.activeKeys = [];

musicianeer.onKeyDown = function(event) {
    const id = event.target.id;
    const keyCode = event.keyCode;
    if (!musicianeer.activeKeys[keyCode]) {
        musicPad.send(JSON.stringify({
            type: 'OnBrowserEvent',
            action: 'KEY_DOWN',
            id: id,
            value: keyCode
        }));
        musicianeer.activeKeys[keyCode] = true;
    }
}

musicianeer.onKeyUp = function(event) {
    const id = event.target.id;
    const keyCode = event.keyCode;
    musicPad.send(JSON.stringify({
        type: 'OnBrowserEvent',
        action: 'KEY_UP',
        id: id,
        value: keyCode
    }));
    musicianeer.activeKeys[keyCode] = false;
}

musicianeer.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/musicianeer';
    musicPad.createWebSocketClient(url, musicianeer.onWebSocketMessage, musicianeer.onWebSocketClose);
}

musicianeer.onTick = function(tick) {
    let scroller = document.getElementById('staff-scroller');
    if (scroller) {
        let svg = scroller.querySelector('svg');
        if (svg) {
            let scaledTick = tick / musicianeer.ticksPerPixel;
            let screenX = musicPad.toScreen(svg, scaledTick);
            let width = scroller.offsetWidth;
            let midPoint = width / 2;
            scroller.scrollLeft += screenX - midPoint;
        }
    }
}

musicianeer.onWebSocketClose = function() {}

musicianeer.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    case 'OnTick':
        musicianeer.onTick(message.tick);
        break;
    default:
        console.log('Unsupported message=' + json);
        break;
    }
}
