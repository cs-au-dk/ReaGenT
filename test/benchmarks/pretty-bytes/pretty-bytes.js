'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var UNITS = ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

module.exports = function (num) {
    if (!Number.isFinite(num)) {
        throw new TypeError('Expected a finite number, got ' + (typeof num === 'undefined' ? 'undefined' : _typeof(num)) + ': ' + num);
    }

    var neg = num < 0;

    if (neg) {
        num = -num;
    }

    if (num < 1) {
        return (neg ? '-' : '') + num + ' B';
    }

    var exponent = Math.min(Math.floor(Math.log(num) / Math.log(1000)), UNITS.length - 1);
    var numStr = Number((num / Math.pow(1000, exponent)).toPrecision(3));
    var unit = UNITS[exponent];

    return (neg ? '-' : '') + numStr + ' ' + unit;
};