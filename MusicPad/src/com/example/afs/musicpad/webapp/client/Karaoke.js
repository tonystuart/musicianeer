'use strict';
var karaoke = karaoke || {};

karaoke.ticksPerPixel = 1;

karaoke.getNextTick = function(currentTick) {
    let next = currentTick.nextElementSibling;
    if (next) {
        return next;
    }
    let nextRow = currentTick.parentElement.nextElementSibling;
    if (nextRow) {
        next = nextRow.firstElementChild;
        if (next) {
            return next;
        }
    }
    let nextStanza = currentTick.parentElement.parentElement.nextElementSibling;
    if (nextStanza) {
        let firstRow = nextStanza.firstElementChild;
        if (firstRow) {
            next = firstRow.firstElementChild;
            if (next) {
                return next;
            }
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
    let channelIndex = item.dataset['channelIndex'];
    musicPad.sendCommand('SAMPLE_CHANNEL', channelIndex);
    musicPad.selectElement(item);
}

karaoke.onChannelDetails = function(message) {
    let channelDetails = document.getElementById("channel-details");
    if (channelDetails) {
        channelDetails.innerHTML = message.html;
    }
}

karaoke.onChannels = function(message) {
    let channels = musicPad.replaceTab('channels', message.html);
    let defaultChannel = channels.dataset['defaultChannel'];
    let item = channels.querySelector('div[data-channel-index=\'' + defaultChannel + '\']');
    musicPad.sendCommand('SAMPLE_CHANNEL', defaultChannel);
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

karaoke.onDeviceKeyDown = function(message) {
    let channels = document.getElementById('channels');
    if (channels) {
        let deviceIndex = channels.dataset['deviceIndex'];
        if (deviceIndex == message.deviceIndex) {
            let keyboardTest = channels.querySelector(".keyboard-test");
            if (keyboardTest) {
                keyboardTest.innerHTML = message.key;
            }
        }
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

karaoke.onTick = function(tick) {
    let prompterList = document.getElementById('prompter-list');
    if (!prompterList) {
        return;
    }
    if (tick == 0) {
        prompterList.scrollTop = 0;
        if (karaoke.lastTick) {
            karaoke.lastTick.classList.remove('current-tick');
        }
    }
    let tickDivision = document.getElementById(tick);
    if (tickDivision) {
        if (karaoke.countdown) {
            karaoke.countdown.parentElement.removeChild(karaoke.countdown);
            karaoke.countdown = null;
        }
        karaoke.selectTick(tickDivision);
    } else {
        if (karaoke.countdown) {
            let currentTick = document.querySelector('.current-tick');
            if (currentTick) {
                let next = karaoke.getNextTick(currentTick);
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

karaoke.onWebSocketClose = function() {
    karaoke.onTick(0);
}

karaoke.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnPrompter':
        karaoke.onPrompter(message);
        break;
    case 'OnChannelDetails':
        karaoke.onChannelDetails(message);
        break;
    case 'OnChannels':
        karaoke.onChannels(message);
        break;
    case 'OnDeviceKeyDown':
        karaoke.onDeviceKeyDown(message);
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

karaoke.selectTick = function(tickDivision) {
    if (karaoke.lastTick) {
        karaoke.lastTick.classList.remove('current-tick');
    }
    tickDivision.classList.add('current-tick');
    tickDivision.scrollIntoView();
    karaoke.lastTick = tickDivision;
}

karaoke.showTickCountdown = function(tick) {
    let currentTick = document.querySelector('.current-tick');
    if (currentTick) {
        let next = karaoke.getNextTick(currentTick);
        if (next) {
            karaoke.countdown = document.createElement('div');
            karaoke.countdown.className = 'countdown';
            // TODO: Pass resolution to client
            karaoke.countdown.innerHTML = Math.floor((next.id - tick) / 512);
            // next.appendChild(karaoke.countdown);
            currentTick.appendChild(karaoke.countdown);
        }
    }
}
