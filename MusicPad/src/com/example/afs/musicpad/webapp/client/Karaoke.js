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

karaoke.onChannels = function(message) {
  musicPad.replaceTab('channels', message.html);
}

karaoke.onChannelSelect = function(message) {
  let channelSelector = document.getElementById('channels');
  let item = channelSelector.querySelector('.selected');
  if (item) {
    let deviceIndex = channelSelector.dataset['deviceIndex'];
    let channelIndex = item.dataset['channelIndex'];
    musicPad.sendCommand('STOP', 0);
    musicPad.sendDeviceCommand('CHANNEL', deviceIndex, channelIndex);
  }
}

karaoke.onLoad = function() {
  musicPad.createWebSocketClient('ws://localhost:8080/v1/karaoke', karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onNewSong = function() {
  musicPad.sendCommand('STOP', 0);
  musicPad.selectTab('songs');
}

karaoke.onPrompter = function(message) {
    musicPad.replaceTab('prompter', message.html);
}

karaoke.onRoulette = function() {
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

karaoke.onSongClick = function(item) {
  let songIndex = item.dataset['songIndex'];
  musicPad.sendCommand('SAMPLE_SONG', songIndex);
  musicPad.selectElement(item);
}

karaoke.onSongs = function(message) {
  musicPad.replaceTab('songs', message.html);
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
    let prompter = document.getElementById('prompter');
    if (!prompter) {
      return;
    }
    if (tick == 0) {
        prompter.scrollTop = 0;
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
    case 'OnChannels':
        karaoke.onChannels(message);
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
    let scroller = document.getElementById('prompter');
    let top = tickDivision.offsetTop;
    let height = tickDivision.offsetHeight;
    let bottom = top + height;
    let scrollerBottomVisible = scroller.scrollTop + scroller.offsetHeight;
    if (bottom + height > scrollerBottomVisible) {
        // scroller.scrollTop += tickDivision.offsetTop;
        // This is just a regular immediate scroll with Chrome 58:
        tickDivision.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
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
