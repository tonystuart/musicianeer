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

karaoke.onLoad = function() {
    musicPad.createWebSocketClient("ws://localhost:8080/v1/karaoke", karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onKaraoke = function(response) {
    let container = document.getElementById('karaoke');
    container.innerHTML = response.karaoke;
}

karaoke.onSongSelector = function(response) {
    let songSelector = musicPad.fragmentToElement(response.html);
    let oldSongSelector = document.getElementById('song-selector');
    if (oldSongSelector) {
        oldSongSelector.parentElement.replaceChild(songSelector, oldSongSelector);
    } else {
        document.body.appendChild(songSelector);
    }
}

karaoke.onSongSelectorPlay = function(response) {
  let songSelector = document.getElementById('song-selector');
  let item = songSelector.querySelector('.selected');
  if (item) {
    let songIndex = parseInt(item.id.match(/[0-9]+/)[0]); 
    musicPad.sendCommand('PLAY_SONG', songIndex);
  }
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
    let response = JSON.parse(json);
    switch (response.type) {
    case 'OnKaraoke':
        karaoke.onKaraoke(response);
        break;
    case 'OnSongSelector':
        karaoke.onSongSelector(response);
        break;
    case 'OnTick':
        karaoke.onTick(response.tick);
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
        //scroller.scrollTop += tickDivision.offsetTop;
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
            //next.appendChild(karaoke.countdown);
            currentTick.appendChild(karaoke.countdown);
        }
    }
}
