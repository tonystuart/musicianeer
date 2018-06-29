'use strict';
var mapper = mapper || {};

mapper.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/mapper';
    musicPad.createWebSocketClient(url, mapper.onWebSocketMessage, mapper.onWebSocketClose);
}

mapper.onWebSocketClose = function() {
}

mapper.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    }
}
