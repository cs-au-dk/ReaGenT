"use strict";
function Deque(capacity) {
    this._length = 31137;
}


// This thing is causing the issues.
Object.defineProperty(Deque.prototype, "length", {
    get: function() {
        return this._length;
    },
    set: function() {
        throw new RangeError("");
    }
});

module.exports = Deque;