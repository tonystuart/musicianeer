'use strict';
var karaoke = karaoke || {};

// NB: ticksPerPixel is initialized when the prompter is received

karaoke.ticksPerPixel = null;

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

karaoke.onCommand = function(message) {
    switch (message.command) {
    case 'NEW_SONG':
        karaoke.onNewSong();
        break;
    }
}

karaoke.onChannelClick = function(item) {
    let channels = document.getElementById('channels');
    let deviceIndex = channels.dataset['deviceIndex'];
    let channelIndex = item.dataset['channelIndex'];
    if (channelIndex) {
        musicPad.sendDeviceCommand('SAMPLE_CHANNEL', deviceIndex, channelIndex);
        musicPad.selectElement(item);
    }
}

karaoke.onChannelDetails = function(message) {
    let channelDetails = document.getElementById("channel-details");
    if (channelDetails) {
        channelDetails.innerHTML = message.html;
    }
}

karaoke.onChannels = function(message) {
    let channels = musicPad.replaceTab('channels', message.html);
    let deviceIndex = channels.dataset['deviceIndex'];
    let defaultChannel = channels.dataset['defaultChannel'];
    let item = channels.querySelector('div[data-channel-index=\'' + defaultChannel + '\']');
    musicPad.sendDeviceCommand('SAMPLE_CHANNEL', deviceIndex, defaultChannel);
    musicPad.selectElement(item);
}

karaoke.onChannelSelect = function(message) {
    let channels = document.getElementById('channels');
    let item = channels.querySelector('.selected');
    if (item) {
        let deviceIndex = channels.dataset['deviceIndex'];
        let channelIndex = item.dataset['channelIndex'];
        musicPad.sendCommand('STOP', 1);
        musicPad.sendDeviceCommand('CHANNEL', deviceIndex, channelIndex);
    }
}

karaoke.onLoad = function() {
    musicPad.createWebSocketClient('ws://localhost:8080/v1/karaoke', karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onNewSong = function() {
    musicPad.sendCommand('STOP', 1);
    musicPad.selectTab('songs');
}

karaoke.onPlay = function() {
    musicPad.sendCommand('PLAY', -1);
}

karaoke.onPrompter = function(message) {
    musicPad.replaceTab('prompter', message.html);
    karaoke.onTick(0);
}

karaoke.onPrompterClick = function(event) {
    let target = event.target;
    let prompt = target.closest(".prompt") || target.closest(".interlude");
    if (prompt) {
        let tick = prompt.id;
        if (tick) {
            musicPad.sendCommand("SEEK", tick);
        }
    }
}

karaoke.onSongRoulette = function() {
    let songList = document.getElementById("song-list");
    let songCount = songList.childElementCount;
    let songIndex = musicPad.getRandomInt(0, songCount);
    let item = songList.children[songIndex];
    musicPad.sendCommand('SAMPLE_SONG', songIndex);
    musicPad.selectElement(item);
    let midpoint = songList.offsetHeight / 2;
    let itemTop = item.offsetTop - songList.offsetTop;
    songList.scrollTop = itemTop - midpoint;
}

karaoke.onStop = function() {
    musicPad.sendCommand('STOP', 0);
}

karaoke.onSongClick = function(item) {
    let songIndex = item.dataset['songIndex'];
    musicPad.sendCommand('SAMPLE_SONG', songIndex);
    musicPad.selectElement(item);
}

karaoke.onSongDetails = function(message) {
    let songDetails = document.getElementById("song-details");
    if (songDetails) {
        songDetails.innerHTML = message.html;
    }
}

karaoke.onSongs = function(message) {
    musicPad.replaceTab('songs', message.html);
    karaoke.onSongRoulette();
}

karaoke.onSongSelect = function(message) {
    let songSelector = document.getElementById('songs');
    let item = songSelector.querySelector('.selected');
    if (item) {
        let songIndex = item.dataset['songIndex'];
        musicPad.sendCommand('SONG', songIndex);
    }
}

karaoke.scrollStaffPrompter = function(tick) {
    let scroller = document.getElementById("notator-scroller");
    let svg = scroller.querySelector("svg");
    if (svg) {
        let scaledTick = tick / karaoke.ticksPerPixel;
        let screenX = musicPad.toScreen(svg, scaledTick);
        let width = scroller.offsetWidth;
        let midPoint = width / 2;
        scroller.scrollLeft += screenX - midPoint;
        console.log("x1=" + scaledTick + ", x2=" + screenX);
    }
}

karaoke.scrollKaraokePrompter = function(tick) {
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
    let promptDivision = document.getElementById(tick);
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
                    karaoke.countdown.innerHTML = Math.floor((next.id - tick) / 512);
                }
            }
        } else {
            karaoke.showTickCountdown(tick);
        }
    }
}

// NB: onTick is initialized when the prompter is received

karaoke.onTick = function() {}

karaoke.onWebSocketClose = function() {
    karaoke.onTick(0);
}

karaoke.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnChannelDetails':
        karaoke.onChannelDetails(message);
        break;
    case 'OnChannels':
        karaoke.onChannels(message);
        break;
    case 'OnDeviceKeyDown':
        karaoke.onDeviceKeyDown(message);
        break;
    case 'OnKaraokePrompter':
        karaoke.onTick = karaoke.scrollKaraokePrompter;
        karaoke.onPrompter(message);
        break;
    case 'OnStaffPrompter':
        karaoke.ticksPerPixel = message.ticksPerPixel;
        karaoke.onTick = karaoke.scrollStaffPrompter;
        karaoke.onPrompter(message);
        break;
    case 'OnSongDetails':
        karaoke.onSongDetails(message);
        break;
    case 'OnSongs':
        karaoke.onSongs(message);
        break;
    case 'OnTick':
        karaoke.onTick(message.tick);
        break;
    case 'OnCommand':
        karaoke.onCommand(message);
        break;
    }
}

karaoke.selectPrompt = function(promptDivision) {
    if (karaoke.lastPrompt) {
        karaoke.lastPrompt.classList.remove('current-prompt');
    }
    promptDivision.classList.add('current-prompt');
    //promptDivision.scrollIntoView();
    let prompterList = document.getElementById("prompter-list");
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
