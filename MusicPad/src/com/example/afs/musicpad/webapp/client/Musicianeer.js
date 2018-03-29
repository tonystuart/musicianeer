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

musicianeer.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/musicianeer';
    musicPad.createWebSocketClient(url, musicianeer.onWebSocketMessage, musicianeer.onWebSocketClose);
}

musicianeer.onWebSocketClose = function() {
}

musicianeer.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    }
}
