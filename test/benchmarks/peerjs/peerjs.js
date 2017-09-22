(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){},{}],2:[function(require,module,exports){},{"./negotiator":5,"./util":8,"eventemitter3":9,"reliable":12}],3:[function(require,module,exports){
window.Peer = require('./peer');
window.util = require('./util');
},{"./adapter":1,"./dataconnection":2,"./mediaconnection":4,"./negotiator":5,"./peer":6,"./socket":7,"./util":8,"js-binarypack":10}],4:[function(require,module,exports){},{"./negotiator":5,"./util":8,"eventemitter3":9}],5:[function(require,module,exports){},{"./adapter":1,"./util":8}],6:[function(require,module,exports){
function Peer(id, options) {
  util.setZeroTimeout(function(){});
};
module.exports = Peer;
},{"./dataconnection":2,"./mediaconnection":4,"./socket":7,"./util":8,"eventemitter3":9}],7:[function(require,module,exports){},{"./util":8,"eventemitter3":9}],8:[function(require,module,exports){
var util = {
  setZeroTimeout: (function(global) {
    function setZeroTimeoutPostMessage(fn) {
      global.postMessage(messageName, '*');
    }
    function handleMessage(event) {
      if (event.source == global && event.data == messageName) {}
    }
    if (global.addEventListener) {
      global.addEventListener('message', handleMessage, true);
    } else if (global.attachEvent) {}
    return setZeroTimeoutPostMessage;
  }(window)),
};
module.exports = util;
},{"./adapter":1,"js-binarypack":10}],9:[],10:[function(require,module,exports){},{"./bufferbuilder":11}],11:[function(require,module,exports){},{}],12:[function(require,module,exports){},{"./util":13}],13:[function(require,module,exports){},{"js-binarypack":10}]},{},[3]);