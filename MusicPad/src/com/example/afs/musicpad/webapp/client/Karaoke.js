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

karaoke.onKaraoke = function(message) {
    let container = document.getElementById('karaoke-scroller');
    container.innerHTML = message.karaoke;
    musicPad.addClassToAllBut("hidden", ".tab", "song-selector-panel")
}

karaoke.onLoad = function() {
    musicPad.createWebSocketClient('ws://localhost:8080/v1/karaoke', karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onPartSelector = function(message) {
  let parts = document.getElementById('parts');
  if (parts != null) {
      parts.parentElement.removeChild(parts);
  }
  document.body.appendChild(musicPad.fragmentToElement(message.html));
  musicPad.selectTab("parts");
}

karaoke.onNewSong = function() {
    musicPad.addClassToAllBut("hidden", ".tab", "song-selector-panel")
}

karaoke.onSongSelector = function(message) {
    let songs = document.getElementById('songs');
    if (songs != null) {
        songs.parentElement.removeChild(songs);
    }
    document.body.appendChild(musicPad.fragmentToElement(message.html));
    musicPad.selectTab("songs");
}

karaoke.onPlaySample = function(message) {
    console.log('onPlaySample');
    let songSelector = document.getElementById('songs');
    let item = songSelector.querySelector('.selected');
    if (item) {
        let songIndex = parseInt(item.id.match(/[0-9]+/)[0]);
        musicPad.sendCommand('SAMPLE', songIndex);
    }
}

karaoke.onSelectSong = function(message) {
  console.log('onSelectSong');
  let songSelector = document.getElementById('songs');
  let item = songSelector.querySelector('.selected');
  if (item) {
      let songIndex = parseInt(item.id.match(/[0-9]+/)[0]);
      musicPad.sendCommand('SONG', songIndex);
  }
}

karaoke.onSelectPart = function(message) {
  console.log('onSelectPart');
  let partSelector = document.getElementById('parts');
  let item = partSelector.querySelector('.selected');
  if (item) {
      let partIndex = parseInt(item.id.match(/[0-9]+/)[0]);
      // TODO: get deviceIndex and channelIndex
      musicPad.sendDeviceCommand('CHANNEL', partIndex);
  }
}

karaoke.onStopSample = function(message) {
    console.log('onStopSample');
    musicPad.sendCommand('STOP');
}

karaoke.onTick = function(tick) {
    if (tick == 0) {
        document.getElementById('karaoke-scroller').scrollTop = 0;
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
    let scroller = document.getElementById('karaoke-scroller');
    scroller.scrollTop = 0;
}

karaoke.onWebSocketMessage = function(json) {
    let message = JSON.parse(json);
    switch (message.type) {
    case 'OnKaraoke':
        karaoke.onKaraoke(message);
        break;
    case 'OnPartSelector':
        karaoke.onPartSelector(message);
        break;
    case 'OnSongSelector':
        karaoke.onSongSelector(message);
        break;
    case 'OnTick':
        karaoke.onTick(message.tick);
        break;
    }
}

karaoke.selectTick = function(tickDivision) {
    if (karaoke.lastTick) {
        karaoke.lastTick.classList.remove('current-tick');
    }
    tickDivision.classList.add('current-tick');
    let scroller = document.getElementById('karaoke-scroller');
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
