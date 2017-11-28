'use strict';
var example = example || {};

example.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/example';
    musicPad.createWebSocketClient(url, example.onWebSocketMessage, example.onWebSocketClose);
}

example.onWebSocketClose = function() {
    example.onTick(0);
}

example.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    }
}
