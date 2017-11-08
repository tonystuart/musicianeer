'use strict';
var karaoke = karaoke || {};

karaoke.onClick = function(event) {
  const id = event.target.id;
  console.log("onClick=" + id);
  musicPad.send(JSON.stringify({
      type: "OnKaraokeBandEvent",
      action: "CLICK",
      id: id
  }));
}

karaoke.onInput = function(event, value) {
  const id = event.target.id;
  console.log("onInput=" + id);
  musicPad.send(JSON.stringify({
      type: "OnKaraokeBandEvent",
      action: "INPUT",
      id: id,
      value: value
  }));
}

karaoke.onKaraokeBandHtml = function(message) {
    let matches;
    switch (message.action) {
    case 'REPLACE_CHILDREN':
        musicPad.setElementHtml(message.selector, message.html);
        break;
    case 'ADD_CLASS':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            match.classList.add(message.html);
        }
        break;
    case 'REMOVE_CLASS':
        matches = document.querySelectorAll(message.selector);
        for (const match of matches) {
            match.classList.remove(message.html);
        }
        break;
    }
}


// Legacy Implementation

karaoke.clearTitleFilter = function() {
    karaoke.onTitleFilter({
        inputCode: 27
    });
}

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

karaoke.getProgramName = function(program) {
    let programName = null;
    let programOptions = document.getElementById('program-options');
    if (programOptions) {
        let option = programOptions.content.querySelector('option[value=\'' + program + '\']');
        if (option) {
            programName = option.label;
        } else if (program == -1) {
            programName = 'Drums';
        }
    } else {
        programName = 'Instrument ' + program;
    }
    return programName;
}

karaoke.onBackToSongs = function() {
    musicPad.selectTab('songs');
    musicPad.sendCommand('STOP', 1);
    musicPad.sendCommand('FILTER_TITLES', 1);
    musicPad.synchronize({
        state: 'BACK_TO_SONGS'
    });
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
    let channelDetails = document.getElementById('channel-details');
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

karaoke.onDeviceReport = function(message) {
    console.log('message.deviceCommand=' + message.deviceCommand + ', deviceIndex=' + message.deviceIndex + ', parameter=' + message.parameter);
    switch (message.deviceCommand) {
    case 'CHANNEL':
        musicPad.setElementHtml('.device-channel-' + message.deviceIndex, 'Channel ' + musicPad.toValue(message.parameter) + ':&nbsp;');
        break;
    case 'MUTE_BACKGROUND':
        musicPad.setElementProperty('.background-mute-' + message.deviceIndex, 'checked', message.parameter ? true : false);
        break;
    case 'PROGRAM':
        musicPad.setElementHtml('.device-program-' + message.deviceIndex, karaoke.getProgramName(message.parameter));
        break;
    case 'VELOCITY':
        musicPad.setElementValue('.device-velocity-' + message.deviceIndex, message.parameter);
        break;
    }
}

karaoke.onLoad = function() {
    let url = 'ws://' + location.host + '/v1/karaoke';
    musicPad.createWebSocketClient(url, karaoke.onWebSocketMessage, karaoke.onWebSocketClose);
}

karaoke.onPlay = function() {
    musicPad.sendCommand('PLAY', -1);
}

karaoke.onPrompter = function(message) {
    musicPad.replaceTab('prompter', message.html);
    karaoke.onTick(0);
    musicPad.sendCommand('REPORT', 0);
}

karaoke.onPrompterClick = function(event) {
    let target = event.target;
    let prompt = target.closest('.prompt') || target.closest('.interlude');
    if (prompt) {
        let tick = prompt.id;
        if (tick) {
            musicPad.sendCommand('SEEK', tick);
        }
    }
}

karaoke.onReport = function(message) {
    console.log('message.command=' + message.command + ', parameter=' + message.parameter);
    switch (message.command) {
    case 'SET_BACKGROUND_VELOCITY':
        musicPad.setElementValue('.background-velocity', message.parameter);
        break;
    case 'SET_MASTER_GAIN':
        musicPad.setElementValue('.master-gain', message.parameter);
        break;
    case 'SET_TEMPO':
        musicPad.setElementValue('.tempo', message.parameter);
        break;
    }
}

karaoke.onSongClick = function(item) {
    let songIndex = item.dataset['songIndex'];
    if (songIndex) {
        musicPad.synchronize({
            state: 'SONG_CLICK',
            songIndex: songIndex
        });
        musicPad.sendCommand('SAMPLE_SONG', songIndex);
        musicPad.selectElement(item);
    }
}

karaoke.onSongDetails = function(message) {
    let songDetails = document.getElementById('song-details');
    if (songDetails) {
        songDetails.innerHTML = message.html;
    }
}

karaoke.onSongRoulette = function() {
    karaoke.clearTitleFilter();
    karaoke.selectRandomSong();
}

karaoke.onSongs = function(message) {
    musicPad.replaceTab('songs', message.html);
    karaoke.selectRandomSong();
    musicPad.sendCommand('FILTER_TITLES', 1);
}

karaoke.onSongSelect = function(message) {
    let songSelector = document.getElementById('songs');
    let item = songSelector.querySelector('.selected');
    if (item) {
        let songIndex = item.dataset['songIndex'];
        musicPad.sendCommand('SELECT_SONG', songIndex);
    }
}

karaoke.onStop = function() {
    musicPad.sendCommand('STOP', 0);
}

karaoke.onSynchronize = function(message) {
    let properties = message.properties;
    if (properties.state == 'BACK_TO_SONGS') {
        karaoke.synchronizeBackToSongs(properties);
    } else if (properties.state == 'SONG_CLICK' || properties.state == 'RANDOM_SONG') {
        karaoke.synchronizeSongClick(properties);
    }
}

karaoke.onTemplates = function(response) {
    for (const templateHtml of response.templates) {
        let template = musicPad.fragmentToElement(templateHtml);
        let existingTemplate = document.getElementById(template.id);
        if (existingTemplate) {
            existingTemplate.parentNode.removeChild(existingTemplate);
        }
        document.body.appendChild(template);
    }
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

karaoke.onTitleFilter = function(message) {
    let filter = document.getElementById('song-list-filter');
    if (filter) {
        let inputCode = message.inputCode;
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
        console.log("filter=" + filter.innerHTML);
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
        let songList = document.getElementById('song-list');
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
    case 'OnKaraokeBandHtml':
        karaoke.onKaraokeBandHtml(message);
        break;
    case 'OnChannelDetails':
        karaoke.onChannelDetails(message);
        break;
    case 'OnChannels':
        karaoke.onChannels(message);
        break;
    case 'OnDeviceReport':
        karaoke.onDeviceReport(message);
        break;
    case 'OnKaraokePrompter':
        karaoke.onPrompter(message);
        break;
    case 'OnReport':
        karaoke.onReport(message);
        break;
    case 'OnSongDetails':
        karaoke.onSongDetails(message);
        break;
    case 'OnSongs':
        karaoke.onSongs(message);
        break;
    case 'OnSynchronize':
        karaoke.onSynchronize(message);
        break;
    case 'OnTemplates':
        karaoke.onTemplates(message);
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
    //promptDivision.scrollIntoView();
    let prompterList = document.getElementById('prompter-list');
    let midpoint = prompterList.offsetHeight / 2;
    let promptTop = promptDivision.offsetTop - prompterList.offsetTop;
    let promptMidpoint = promptTop + promptDivision.offsetHeight / 2;
    prompterList.scrollTop = promptMidpoint - midpoint;

    karaoke.lastPrompt = promptDivision;
}

karaoke.selectRandomSong = function() {
    let songList = document.getElementById('song-list');
    let songCount = songList.childElementCount;
    let songIndex = musicPad.getRandomInt(0, songCount);
    let item = songList.children[songIndex];
    musicPad.synchronize({
        state: 'RANDOM_SONG',
        songIndex: songIndex
    });
    musicPad.sendCommand('SAMPLE_SONG', songIndex);
    musicPad.selectElement(item);
    let midpoint = songList.offsetHeight / 2;
    let itemTop = item.offsetTop - songList.offsetTop;
    songList.scrollTop = itemTop - midpoint;
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

karaoke.synchronizeBackToSongs = function(properties) {
    musicPad.selectTab('songs');
}

karaoke.synchronizeSongClick = function(properties) {
    let songList = document.getElementById('song-list');
    if (songList) {
        let songIndex = properties.songIndex;
        let item = songList.querySelector('[data-song-index=\'' + songIndex + '\']')
        if (item) {
            if (!item.matches(".selected")) {
                if (item.matches(".hidden")) {
                    karaoke.clearTitleFilter();
                }
                musicPad.selectElement(item);
                let midpoint = songList.offsetHeight / 2;
                let itemTop = item.offsetTop - songList.offsetTop;
                songList.scrollTop = itemTop - midpoint;
            }
        }
    }
}
