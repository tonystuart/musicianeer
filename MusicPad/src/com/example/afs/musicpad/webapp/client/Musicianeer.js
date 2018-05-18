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

musicianeer.onStaffMouseDown = function(event) {
    musicianeer.staffMouseDown = true;
}

musicianeer.onStaffMouseUp = function(event) {
    musicianeer.staffMouseDown = false;
}

musicianeer.onStaffScroll = function(event) {
    if (musicianeer.staffMouseDown) {
        const svg = document.querySelector('svg');
        const ctm = svg.getScreenCTM();
        const midPoint = event.target.offsetWidth / 2;
        const left = (event.target.offsetLeft + midPoint + -ctm.e) / ctm.a;
        const tick = left * musicianeer.ticksPerPixel;
        musicPad.send(JSON.stringify({
            type: 'OnBrowserEvent',
            action: 'SCROLL',
            id: event.target.id,
            value: tick
        }));
        console.log('onScroll: svg_left=' + left + ', tick=' + tick);
    }
}

musicianeer.onTick = function(tick) {
    if (!musicianeer.staffMouseDown) {
        const svg = document.querySelector('svg');
        const ctm = svg.getScreenCTM();
        const scroller = document.getElementById('staff-scroller');
        const midPoint = scroller.offsetWidth / 2;
        const pixels = tick / musicianeer.ticksPerPixel;
        const left = pixels * ctm.a;
        console.log('onTick: tick=' + tick + ', dom_left=' + left + ', width=' + scroller.scrollWidth);
        scroller.scrollLeft = left;
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
