"use strict";
var musicPad = musicPad || {};

musicPad.fragmentToElement = function(fragment) {
  let container = document.createElement("div");
  container.innerHTML = fragment;
  return container.firstElementChild;
}

