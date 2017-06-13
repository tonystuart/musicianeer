"use strict";
var karaoke = karaoke || {};

karaoke.ticksPerPixel = 1;
karaoke.connected = false;

karaoke.createWebSocketClient = function() {
    karaoke.ws = new WebSocket("ws://localhost:8080/v1/message");
    karaoke.ws.onopen = function() {
        console.log("ws.onopen: entered");
        karaoke.connected = true;
    }
    karaoke.ws.onmessage = function(message) {
        karaoke.processResponse(message.data);
    }
    karaoke.ws.onclose = function() {
        console.log("ws.onclose: entered, connecting again in 1000 ms.");
        karaoke.connected = false;
        let scroller = document.getElementById("karaoke-scroller");
        scroller.scrollTop = 0;
        setTimeout(karaoke.createWebSocketClient, 1000);
    }
}

karaoke.onLoad = function() {
    karaoke.createWebSocketClient();
}

karaoke.onKaraoke = function(response) {
    let container = document.getElementById("karaoke");
    container.innerHTML = response.karaoke;
}

karaoke.onTick = function(tick) {
    if (tick == 0) {
        document.getElementById("karaoke-scroller").scrollTop = 0;
        if (karaoke.lastTick) {
            karaoke.lastTick.classList.remove("current-tick");
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
          let currentTick = document.querySelector(".current-tick");
          if (currentTick) {
            let next = currentTick.nextElementSibling;
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

karaoke.processResponse = function(json) {
    let response = JSON.parse(json);
    switch (response.type) {
    case "OnKaraoke":
        karaoke.onKaraoke(response);
        break;
    case "OnTick":
        karaoke.onTick(response.tick);
        break;
    }
}

karaoke.selectTick = function(tickDivision) {
    if (karaoke.lastTick) {
        karaoke.lastTick.classList.remove("current-tick");
    }
    tickDivision.classList.add("current-tick");
    let scroller = document.getElementById("karaoke-scroller");
    let top = tickDivision.offsetTop;
    let height = tickDivision.offsetHeight;
    let bottom = top + height;
    let scrollerBottomVisible = scroller.scrollTop + scroller.offsetHeight;
    if (bottom + height > scrollerBottomVisible) {
        //scroller.scrollTop += tickDivision.offsetTop;
        // This is just a regular immediate scroll with Chrome 58:
        tickDivision.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
    karaoke.lastTick = tickDivision;
}

karaoke.showTickCountdown = function(tick) {
  let currentTick = document.querySelector(".current-tick");
  if (currentTick) {
    let next = currentTick.nextElementSibling;
    if (next) {
      karaoke.countdown = document.createElement("div");
      karaoke.countdown.className = "countdown";
      // TODO: Pass resolution to client
      karaoke.countdown.innerHTML = Math.floor((next.id - tick) / 512);
      //next.appendChild(karaoke.countdown);
      currentTick.appendChild(karaoke.countdown);
    }
  }
}
