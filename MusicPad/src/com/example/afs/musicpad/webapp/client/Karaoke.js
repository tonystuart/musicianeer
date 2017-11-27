'use strict';
var karaoke = karaoke || {};

const TICK = 'tick-';

karaoke.getNextPrompt = function(currentPrompt) {
    let next = currentPrompt.nextElementSibling;
    if (next) {
        return next;
    }
    let nextRow = currentPrompt.parentElement.nextElementSibling;
    if (nextRow) {
        next = nextRow.firstElementChild;
        if (next) {
            return next;
        }
    }
    return null;
}

karaoke.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/karaoke';
    musicPad.createWebSocketClient(url, karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onTick = function(tick) {
    let prompterList = document.getElementById('prompter-list');
    if (!prompterList) {
        return;
    }
    if (tick == 0) {
        prompterList.scrollTop = 0;
        if (karaoke.lastPrompt) {
            karaoke.lastPrompt.classList.remove('current-prompt');
            karaoke.lastPrompt = null;
        }
    }
    let promptDivision = document.getElementById(TICK + tick);
    if (promptDivision) {
        if (karaoke.countdown) {
            karaoke.countdown.parentElement.removeChild(karaoke.countdown);
            karaoke.countdown = null;
        }
        karaoke.selectPrompt(promptDivision);
    } else {
        if (karaoke.countdown) {
            let currentPrompt = document.querySelector('.current-prompt');
            if (currentPrompt) {
                let next = karaoke.getNextPrompt(currentPrompt);
                if (next) {
                    // TODO: Pass resolution to client
                    karaoke.countdown.innerHTML = Math.floor((next.id.substring(TICK.length) - tick) / 512);
                }
            }
        } else {
            karaoke.showTickCountdown(tick);
        }
    }
}

karaoke.onTitleFilter = function(inputCode) {
    let filter = document.getElementById('song-list-filter');
    if (filter) {
        if (inputCode == 27) {
            filter.innerHTML = '';
        } else if (inputCode == 8 || inputCode == 127) {
            if (filter.innerHTML.length > 0) {
                filter.innerHTML = filter.innerHTML.substring(0, filter.innerHTML.length - 1);
            }
        } else if (inputCode >= 32 && inputCode < 127) {
            let character = String.fromCharCode(inputCode).toLowerCase();
            filter.innerHTML += character
        }
        console.log('filter=' + filter.innerHTML);
        let pattern = '.*';
        for (let i = 0; i < filter.innerHTML.length; i++) {
            const c = filter.innerHTML.charAt(i);
            if (c == ' ') {
                pattern += '\\b.*';
            } else {
                pattern += c;
            }
        }
        console.log('pattern=' + pattern);
        const regexp = new RegExp(pattern,'i');
        let songList = document.getElementById('song-list').firstElementChild;
        if (songList) {
            let songs = songList.children;
            for (const song of songs) {
                if (regexp.test(song.innerHTML)) {
                    song.classList.remove('hidden');
                } else {
                    song.classList.add('hidden');
                }
            }
        }
        if (filter.innerHTML.length == 0) {
            filter.classList.add('hidden');
        } else {
            filter.classList.remove('hidden');
        }
    }
}

karaoke.onWebSocketClose = function() {
    karaoke.onTick(0);
}

karaoke.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnShadowUpdate':
        musicPad.onShadowUpdate(message);
        break;
    case 'OnTick':
        karaoke.onTick(message.tick);
        break;
    case 'OnTitleFilter':
        karaoke.onTitleFilter(message);
        break;
    }
}

karaoke.selectPrompt = function(promptDivision) {
    if (karaoke.lastPrompt) {
        karaoke.lastPrompt.classList.remove('current-prompt');
    }
    promptDivision.classList.add('current-prompt');
    let prompterList = document.getElementById('prompter-list');
    let midpoint = prompterList.offsetHeight / 2;
    let promptTop = promptDivision.offsetTop - prompterList.offsetTop;
    let promptMidpoint = promptTop + promptDivision.offsetHeight / 2;
    prompterList.scrollTop = promptMidpoint - midpoint;

    karaoke.lastPrompt = promptDivision;
}

karaoke.showTickCountdown = function(tick) {
    let currentPrompt = document.querySelector('.current-prompt');
    if (currentPrompt) {
        let next = karaoke.getNextPrompt(currentPrompt);
        if (next) {
            karaoke.countdown = document.createElement('div');
            karaoke.countdown.className = 'countdown';
            // TODO: Pass resolution to client
            karaoke.countdown.innerHTML = Math.floor((next.id - tick) / 512);
            // next.appendChild(karaoke.countdown);
            currentPrompt.appendChild(karaoke.countdown);
        }
    }
}
