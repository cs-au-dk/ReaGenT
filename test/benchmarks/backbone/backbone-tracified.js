Object.defineProperty(typeof window === 'undefined' ? global : window, '$__', { configurable: false, enumerable: false, value: {
    fs: {},
    os: {},
    functionRegExp: /^function .*\([\s\S]*\) {[\s\S]*\}$/g,
    refs: {
        pop: Array.prototype.pop,
        slice: Array.prototype.slice
    },
    uid: Symbol('bHvKvd19fGrk'),
    w: window
}});
(((($__.fs.J$__v257300249_3_288 = function J$__v257300249_3(factory) {
var vvv_return, vvv_switch, root, _, $;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
factory = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    root = ((typeof self == 'object', TAJS_restrictToType(self.self, 'object') === self), self);
    typeof define === 'function';
    typeof exports !== 'undefined';
    root.Backbone = (factory, $__.fs.J$__v257300249_297_289)(root, $__.os.oid5 = {}, (TAJS_restrictToType(root._, 'function'), $__.fs.J$__v7733624767_3_9), (((TAJS_restrictToType(root.jQuery, 'undefined'), TAJS_restrictToType(root.Zepto, 'undefined')), TAJS_restrictToType(root.ender, 'undefined')), TAJS_restrictToType(root.$, 'undefined')), 0, true, $__.uid);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
root = typeof self == 'object' && self.self === self && self || typeof global == 'object' && global.global === global && global;
if (typeof define === 'function' && define.amd) {
    define([
        'underscore',
        'jquery',
        'exports'
    ], function (_, $, exports) {
        root.Backbone = factory(root, exports, _, $);
    });
} else if (typeof exports !== 'undefined') {
    _ = require('underscore');
    try {
        $ = require('jquery');
    } catch (e) {
    }
    factory(root, exports, _, $);
} else {
    root.Backbone = factory(root, {}, root._, root.jQuery || root.Zepto || root.ender || root.$);
}})), $__.fs.J$__v257300249_3_288))((($__.fs.J$__v257300249_297_289 = function J$__v257300249_297(root, Backbone, _, $) {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1, vvv_tmp2, vvv_tmp3, vvv_tmp4, vvv_tmp5, previousBackbone, slice, addMethod, addUnderscoreMethods, cb, modelMatcher, Events, eventSplitter, eventsApi, internalOn, onApi, offApi, onceMap, triggerApi, triggerEvents, Model, modelMethods, Collection, setOptions, addOptions, splice, collectionMethods, View, delegateEventSplitter, viewOptions, methodMap, Router, optionalParam, namedParam, splatParam, escapeRegExp, History, routeStripper, rootStripper, pathStripper, extend, urlError, wrapError;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
root = arguments[0], Backbone = arguments[1], _ = arguments[2], $ = arguments[3];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    previousBackbone = TAJS_restrictToType(root.Backbone, 'undefined');
    slice = TAJS_restrictToType(TAJS_restrictToType(Array.prototype, 'object').slice, 'function');
    (Backbone, $__.os.oid5).VERSION = '1.3.3';
    (Backbone, $__.os.oid5).$ = $;
    (Backbone, $__.os.oid5).noConflict = ($__.fs.J$__v257300249_5_290 = function J$__v257300249_5() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
root.Backbone = previousBackbone;
return this;});
    (Backbone, $__.os.oid5).emulateHTTP = false;
    (Backbone, $__.os.oid5).emulateJSON = false;
    addMethod = ($__.fs.J$__v257300249_17_291 = function J$__v257300249_17(length, method, attribute) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
length = arguments[0], method = arguments[1], attribute = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    length === 1;
    return ($__.fs.J$__v257300249_7_334 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    length === 1;
    return ($__.fs.J$__v257300249_7_335 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    length === 1;
    return ($__.fs.J$__v257300249_7_336 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    length === 1;
    return ($__.fs.J$__v257300249_7_337 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_338 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_339 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    length === 1;
    return ($__.fs.J$__v257300249_7_340 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 7:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 7);
    length === 1;
    return ($__.fs.J$__v257300249_7_341 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 8:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 8);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_376 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 9:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 9);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_377 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 10:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 10);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_378 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 11:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 11);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_379 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 12:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 12);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_380 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 13:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 13);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_381 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 14:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 14);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_382 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 15:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 15);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_383 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 16:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 16);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_384 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 17:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 17);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_385 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 18:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 18);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_386 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 19:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 19);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_387 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 20:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 20);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_388 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 21:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 21);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_389 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 22:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 22);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_390 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 23:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 23);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_391 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 24:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 24);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_392 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 25:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 25);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_393 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 26:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 26);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_394 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 27:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 27);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_395 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 28:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 28);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_396 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 29:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 29);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_397 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 30:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 30);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_398 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 31:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 31);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_399 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 32:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 32);
    length === 1;
    return ($__.fs.J$__v257300249_7_400 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 33:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 33);
    length === 1;
    return ($__.fs.J$__v257300249_7_401 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 34:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 34);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_402 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 35:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 35);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_403 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 36:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 36);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_404 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 37:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 37);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_405 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 38:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 38);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_406 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 39:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 39);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_407 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 40:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 40);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_408 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 41:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 41);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_409 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 42:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 42);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_410 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 43:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 43);
    length === 1 === 2 === 3 === 4;
    return ($__.fs.J$__v257300249_15_411 = function J$__v257300249_15() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments);
args.unshift(this[attribute]);
return _[method].apply(_, args);});
case 44:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 44);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_412 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 45:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 45);
    length === 1;
    return ($__.fs.J$__v257300249_7_413 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 46:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 46);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_414 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 47:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 47);
    length === 1;
    return ($__.fs.J$__v257300249_7_415 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 48:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 48);
    length === 1;
    return ($__.fs.J$__v257300249_7_416 = function J$__v257300249_7() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute]);});
case 49:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 49);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_417 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 50:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 50);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_418 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 51:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 51);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_419 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 52:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 52);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_420 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 53:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 53);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_421 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 54:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 54);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_422 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 55:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 55);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_423 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
case 56:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 56);
    length === 1 === 2 === 3;
    return ($__.fs.J$__v257300249_11_424 = function J$__v257300249_11(iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _[method](this[attribute], cb(iteratee, this), context);});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
switch (length) {
case 1:
    return function () {
        return _[method](this[attribute]);
    };
case 2:
    return function (value) {
        return _[method](this[attribute], value);
    };
case 3:
    return function (iteratee, context) {
        return _[method](this[attribute], cb(iteratee, this), context);
    };
case 4:
    return function (iteratee, defaultVal, context) {
        return _[method](this[attribute], cb(iteratee, this), defaultVal, context);
    };
default:
    return function () {
        var args = slice.call(arguments);
        args.unshift(this[attribute]);
        return _[method].apply(_, args);
    };
}});
    addUnderscoreMethods = ($__.fs.J$__v257300249_21_292 = function J$__v257300249_21(Class, methods, attribute) {
var vvv_return, vvv_switch, vvv_tmp0;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
Class = arguments[0], methods = arguments[1], attribute = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    vvv_tmp0 = _, 'each', $__.fs.J$__v7733624767_33_18.call(vvv_tmp0, methods, ($__.fs.J$__v257300249_19_333 = function J$__v257300249_19(length, method) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
length = arguments[0], method = arguments[1];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    TAJS_restrictToType(_[method, 'keys'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'keys'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 0, true, $__.uid);
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    TAJS_restrictToType(_[method, 'values'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'values'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 1, true, $__.uid);
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    TAJS_restrictToType(_[method, 'pairs'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'pairs'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 2, true, $__.uid);
    return;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    TAJS_restrictToType(_[method, 'invert'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'invert'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 3, true, $__.uid);
    return;
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    TAJS_restrictToType(_[method, 'pick'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'pick'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 4, true, $__.uid);
    return;
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    TAJS_restrictToType(_[method, 'omit'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'omit'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 5, true, $__.uid);
    return;
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    TAJS_restrictToType(_[method, 'chain'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'chain'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 6, true, $__.uid);
    return;
case 7:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 7);
    TAJS_restrictToType(_[method, 'isEmpty'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'isEmpty'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 7, true, $__.uid);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_[method])
    Class.prototype[method] = addMethod(length, method, attribute);}), 4, true, $__.uid);
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    vvv_tmp0 = _, 'each', $__.fs.J$__v7733624767_33_18.call(vvv_tmp0, methods, ($__.fs.J$__v257300249_19_375 = function J$__v257300249_19(length, method) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
length = arguments[0], method = arguments[1];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    TAJS_restrictToType(_[method, 'forEach'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'forEach'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 8, true, $__.uid);
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    TAJS_restrictToType(_[method, 'each'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'each'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 9, true, $__.uid);
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    TAJS_restrictToType(_[method, 'map'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'map'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 10, true, $__.uid);
    return;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    TAJS_restrictToType(_[method, 'collect'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'collect'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 11, true, $__.uid);
    return;
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    TAJS_restrictToType(_[method, 'reduce'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'reduce'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 12, true, $__.uid);
    return;
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    TAJS_restrictToType(_[method, 'foldl'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'foldl'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 13, true, $__.uid);
    return;
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    TAJS_restrictToType(_[method, 'inject'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'inject'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 14, true, $__.uid);
    return;
case 7:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 7);
    TAJS_restrictToType(_[method, 'reduceRight'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'reduceRight'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 15, true, $__.uid);
    return;
case 8:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 8);
    TAJS_restrictToType(_[method, 'foldr'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'foldr'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 16, true, $__.uid);
    return;
case 9:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 9);
    TAJS_restrictToType(_[method, 'find'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'find'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 17, true, $__.uid);
    return;
case 10:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 10);
    TAJS_restrictToType(_[method, 'detect'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'detect'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 18, true, $__.uid);
    return;
case 11:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 11);
    TAJS_restrictToType(_[method, 'filter'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'filter'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 19, true, $__.uid);
    return;
case 12:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 12);
    TAJS_restrictToType(_[method, 'select'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'select'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 20, true, $__.uid);
    return;
case 13:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 13);
    TAJS_restrictToType(_[method, 'reject'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'reject'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 21, true, $__.uid);
    return;
case 14:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 14);
    TAJS_restrictToType(_[method, 'every'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'every'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 22, true, $__.uid);
    return;
case 15:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 15);
    TAJS_restrictToType(_[method, 'all'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'all'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 23, true, $__.uid);
    return;
case 16:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 16);
    TAJS_restrictToType(_[method, 'some'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'some'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 24, true, $__.uid);
    return;
case 17:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 17);
    TAJS_restrictToType(_[method, 'any'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'any'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 25, true, $__.uid);
    return;
case 18:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 18);
    TAJS_restrictToType(_[method, 'include'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'include'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 26, true, $__.uid);
    return;
case 19:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 19);
    TAJS_restrictToType(_[method, 'includes'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'includes'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 27, true, $__.uid);
    return;
case 20:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 20);
    TAJS_restrictToType(_[method, 'contains'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'contains'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 28, true, $__.uid);
    return;
case 21:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 21);
    TAJS_restrictToType(_[method, 'invoke'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'invoke'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 29, true, $__.uid);
    return;
case 22:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 22);
    TAJS_restrictToType(_[method, 'max'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'max'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 30, true, $__.uid);
    return;
case 23:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 23);
    TAJS_restrictToType(_[method, 'min'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'min'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 31, true, $__.uid);
    return;
case 24:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 24);
    TAJS_restrictToType(_[method, 'toArray'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'toArray'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 32, true, $__.uid);
    return;
case 25:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 25);
    TAJS_restrictToType(_[method, 'size'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'size'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 33, true, $__.uid);
    return;
case 26:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 26);
    TAJS_restrictToType(_[method, 'first'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'first'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 34, true, $__.uid);
    return;
case 27:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 27);
    TAJS_restrictToType(_[method, 'head'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'head'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 35, true, $__.uid);
    return;
case 28:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 28);
    TAJS_restrictToType(_[method, 'take'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'take'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 36, true, $__.uid);
    return;
case 29:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 29);
    TAJS_restrictToType(_[method, 'initial'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'initial'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 37, true, $__.uid);
    return;
case 30:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 30);
    TAJS_restrictToType(_[method, 'rest'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'rest'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 38, true, $__.uid);
    return;
case 31:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 31);
    TAJS_restrictToType(_[method, 'tail'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'tail'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 39, true, $__.uid);
    return;
case 32:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 32);
    TAJS_restrictToType(_[method, 'drop'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'drop'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 40, true, $__.uid);
    return;
case 33:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 33);
    TAJS_restrictToType(_[method, 'last'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'last'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 41, true, $__.uid);
    return;
case 34:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 34);
    TAJS_restrictToType(_[method, 'without'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'without'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 42, true, $__.uid);
    return;
case 35:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 35);
    TAJS_restrictToType(_[method, 'difference'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'difference'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 43, true, $__.uid);
    return;
case 36:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 36);
    TAJS_restrictToType(_[method, 'indexOf'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'indexOf'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 44, true, $__.uid);
    return;
case 37:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 37);
    TAJS_restrictToType(_[method, 'shuffle'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'shuffle'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 45, true, $__.uid);
    return;
case 38:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 38);
    TAJS_restrictToType(_[method, 'lastIndexOf'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'lastIndexOf'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 46, true, $__.uid);
    return;
case 39:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 39);
    TAJS_restrictToType(_[method, 'isEmpty'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'isEmpty'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 47, true, $__.uid);
    return;
case 40:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 40);
    TAJS_restrictToType(_[method, 'chain'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'chain'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 48, true, $__.uid);
    return;
case 41:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 41);
    TAJS_restrictToType(_[method, 'sample'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'sample'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 49, true, $__.uid);
    return;
case 42:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 42);
    TAJS_restrictToType(_[method, 'partition'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'partition'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 50, true, $__.uid);
    return;
case 43:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 43);
    TAJS_restrictToType(_[method, 'groupBy'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'groupBy'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 51, true, $__.uid);
    return;
case 44:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 44);
    TAJS_restrictToType(_[method, 'countBy'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'countBy'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 52, true, $__.uid);
    return;
case 45:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 45);
    TAJS_restrictToType(_[method, 'sortBy'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'sortBy'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 53, true, $__.uid);
    return;
case 46:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 46);
    TAJS_restrictToType(_[method, 'indexBy'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'indexBy'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 54, true, $__.uid);
    return;
case 47:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 47);
    TAJS_restrictToType(_[method, 'findIndex'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'findIndex'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 55, true, $__.uid);
    return;
case 48:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 48);
    TAJS_restrictToType(_[method, 'findLastIndex'], 'function');
    TAJS_restrictToType(Class.prototype, 'object')[method, 'findLastIndex'] = (addMethod, $__.fs.J$__v257300249_17_291)(length, method, attribute, 56, true, $__.uid);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_[method])
    Class.prototype[method] = addMethod(length, method, attribute);}), 5, true, $__.uid);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
_.each(methods, function (length, method) {
    if (_[method])
        Class.prototype[method] = addMethod(length, method, attribute);
});});
    cb = ($__.fs.J$__v257300249_25_293 = function J$__v257300249_25(iteratee, instance) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], instance = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_.isFunction(iteratee))
    return iteratee;
if (_.isObject(iteratee) && !instance._isModel(iteratee))
    return modelMatcher(iteratee);
if (_.isString(iteratee))
    return function (model) {
    return model.get(iteratee);
};
return iteratee;});
    modelMatcher = ($__.fs.J$__v257300249_29_294 = function J$__v257300249_29(attrs) {
var vvv_return, vvv_switch, matcher;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
matcher = _.matches(attrs);
return function (model) {
    return matcher(model.attributes);
};});
    Events = (Backbone, $__.os.oid5).Events = $__.os.oid6 = {};
    eventSplitter = /\s+/;
    eventsApi = ($__.fs.J$__v257300249_31_295 = function J$__v257300249_31(iteratee, events, name, callback, opts) {
var vvv_return, vvv_switch, i, names;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
iteratee = arguments[0], events = arguments[1], name = arguments[2], callback = arguments[3], opts = arguments[4];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
i = 0;
if (name && typeof name === 'object') {
    if (callback !== void 0 && 'context' in opts && opts.context === void 0)
        opts.context = callback;
    for (names = (_.keys(name)); i < names.length; i++) {
        events = eventsApi(iteratee, events, names[i], name[names[i]], opts);
    }
} else if (name && eventSplitter.test(name)) {
    for (names = (name.split(eventSplitter)); i < names.length; i++) {
        events = iteratee(events, names[i], callback, opts);
    }
} else {
    events = iteratee(events, name, callback, opts);
}
return events;});
    (Events, $__.os.oid6).on = ($__.fs.J$__v257300249_33_296 = function J$__v257300249_33(name, callback, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0], callback = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return internalOn(this, name, callback, context);});
    internalOn = ($__.fs.J$__v257300249_35_297 = function J$__v257300249_35(obj, name, callback, context, listening) {
var vvv_return, vvv_switch, listeners;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], name = arguments[1], callback = arguments[2], context = arguments[3], listening = arguments[4];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj._events = eventsApi(onApi, obj._events || {}, name, callback, {
    context: context,
    ctx: obj,
    listening: listening
});
if (listening) {
    listeners = obj._listeners || (obj._listeners = {});
    listeners[listening.id] = listening;
}
return obj;});
    (Events, $__.os.oid6).listenTo = ($__.fs.J$__v257300249_37_298 = function J$__v257300249_37(obj, name, callback) {
var vvv_return, vvv_switch, id, listeningTo, listening, thisId;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], name = arguments[1], callback = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!obj)
    return this;
id = obj._listenId || (obj._listenId = _.uniqueId('l'));
listeningTo = this._listeningTo || (this._listeningTo = {});
listening = listeningTo[id];
if (!listening) {
    thisId = this._listenId || (this._listenId = _.uniqueId('l'));
    listening = listeningTo[id] = {
        obj: obj,
        objId: id,
        id: thisId,
        listeningTo: listeningTo,
        count: 0
    };
}
internalOn(obj, name, callback, this, listening);
return this;});
    onApi = ($__.fs.J$__v257300249_39_299 = function J$__v257300249_39(events, name, callback, options) {
var vvv_return, vvv_switch, handlers, context, ctx, listening;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
events = arguments[0], name = arguments[1], callback = arguments[2], options = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (callback) {
    handlers = events[name] || (events[name] = []);
    context = options.context;
    ctx = options.ctx;
    listening = options.listening;
    if (listening)
        listening.count++;
    handlers.push({
        callback: callback,
        context: context,
        ctx: context || ctx,
        listening: listening
    });
}
return events;});
    (Events, $__.os.oid6).off = ($__.fs.J$__v257300249_41_300 = function J$__v257300249_41(name, callback, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0], callback = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this._events)
    return this;
this._events = eventsApi(offApi, this._events, name, callback, {
    context: context,
    listeners: this._listeners
});
return this;});
    (Events, $__.os.oid6).stopListening = ($__.fs.J$__v257300249_43_301 = function J$__v257300249_43(obj, name, callback) {
var vvv_return, vvv_switch, listeningTo, ids, listening;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], name = arguments[1], callback = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
listeningTo = this._listeningTo;
if (!listeningTo)
    return this;
ids = obj ? [obj._listenId] : _.keys(listeningTo);
for (var i = 0; i < ids.length; i++) {
    listening = listeningTo[ids[i]];
    if (!listening)
        break;
    listening.obj.off(name, callback, this);
}
return this;});
    offApi = ($__.fs.J$__v257300249_45_302 = function J$__v257300249_45(events, name, callback, options) {
var vvv_return, vvv_switch, i, listening, context, listeners, ids, names, handlers, remaining, handler;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
events = arguments[0], name = arguments[1], callback = arguments[2], options = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!events)
    return;
i = 0;
context = options.context;
listeners = options.listeners;
if (!name && !callback && !context) {
    ids = _.keys(listeners);
    for (; i < ids.length; i++) {
        listening = listeners[ids[i]];
        delete listeners[listening.id];
        delete listening.listeningTo[listening.objId];
    }
    return;
}
names = name ? [name] : _.keys(events);
for (; i < names.length; i++) {
    name = names[i];
    handlers = events[name];
    if (!handlers)
        break;
    remaining = [];
    for (var j = 0; j < handlers.length; j++) {
        handler = handlers[j];
        if (callback && callback !== handler.callback && callback !== handler.callback._callback || context && context !== handler.context) {
            remaining.push(handler);
        } else {
            listening = handler.listening;
            if (listening && --listening.count === 0) {
                delete listeners[listening.id];
                delete listening.listeningTo[listening.objId];
            }
        }
    }
    if (remaining.length) {
        events[name] = remaining;
    } else {
        delete events[name];
    }
}
return events;});
    (Events, $__.os.oid6).once = ($__.fs.J$__v257300249_47_303 = function J$__v257300249_47(name, callback, context) {
var vvv_return, vvv_switch, events;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0], callback = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
events = eventsApi(onceMap, {}, name, callback, _.bind(this.off, this));
if (typeof name === 'string' && context == null)
    callback = void 0;
return this.on(events, callback, context);});
    (Events, $__.os.oid6).listenToOnce = ($__.fs.J$__v257300249_49_304 = function J$__v257300249_49(obj, name, callback) {
var vvv_return, vvv_switch, events;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], name = arguments[1], callback = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
events = eventsApi(onceMap, {}, name, callback, _.bind(this.stopListening, this, obj));
return this.listenTo(obj, events);});
    onceMap = ($__.fs.J$__v257300249_53_305 = function J$__v257300249_53(map, name, callback, offer) {
var vvv_return, vvv_switch, once;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
map = arguments[0], name = arguments[1], callback = arguments[2], offer = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (callback) {
    once = map[name] = _.once(function () {
        offer(name, once);
        callback.apply(this, arguments);
    });
    once._callback = callback;
}
return map;});
    (Events, $__.os.oid6).trigger = ($__.fs.J$__v257300249_55_306 = function J$__v257300249_55(name) {
var vvv_return, vvv_switch, length, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this._events)
    return this;
length = Math.max(0, arguments.length - 1);
args = Array(length);
for (var i = 0; i < length; i++)
    args[i] = arguments[i + 1];
eventsApi(triggerApi, this._events, name, void 0, args);
return this;});
    triggerApi = ($__.fs.J$__v257300249_57_307 = function J$__v257300249_57(objEvents, name, callback, args) {
var vvv_return, vvv_switch, events, allEvents;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
objEvents = arguments[0], name = arguments[1], callback = arguments[2], args = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (objEvents) {
    events = objEvents[name];
    allEvents = objEvents.all;
    if (events && allEvents)
        allEvents = allEvents.slice();
    if (events)
        triggerEvents(events, args);
    if (allEvents)
        triggerEvents(allEvents, [name].concat(args));
}
return objEvents;});
    triggerEvents = ($__.fs.J$__v257300249_59_308 = function J$__v257300249_59(events, args) {
var vvv_return, vvv_switch, ev, i, l, a1, a2, a3;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
events = arguments[0], args = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
i = -1;
l = events.length;
a1 = args[0];
a2 = args[1];
a3 = args[2];
switch (args.length) {
case 0:
    while (++i < l)
        (ev = events[i]).callback.call(ev.ctx);
    return;
case 1:
    while (++i < l)
        (ev = events[i]).callback.call(ev.ctx, a1);
    return;
case 2:
    while (++i < l)
        (ev = events[i]).callback.call(ev.ctx, a1, a2);
    return;
case 3:
    while (++i < l)
        (ev = events[i]).callback.call(ev.ctx, a1, a2, a3);
    return;
default:
    while (++i < l)
        (ev = events[i]).callback.apply(ev.ctx, args);
    return;
}});
    (Events, $__.os.oid6).bind = TAJS_restrictToType((Events, $__.os.oid6).on, 'function');
    (Events, $__.os.oid6).unbind = TAJS_restrictToType((Events, $__.os.oid6).off, 'function');
    vvv_tmp0 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp0, Backbone, Events, 0, true, $__.uid);
    Model = (Backbone, $__.os.oid5).Model = ($__.fs.J$__v257300249_61_309 = function J$__v257300249_61(attributes, options) {
var vvv_return, vvv_switch, attrs, defaults;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attributes = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
attrs = attributes || {};
options || (options = {});
this.cid = _.uniqueId(this.cidPrefix);
this.attributes = {};
if (options.collection)
    this.collection = options.collection;
if (options.parse)
    attrs = this.parse(attrs, options) || {};
defaults = _.result(this, 'defaults');
attrs = _.defaults(_.extend({}, defaults, attrs), defaults);
this.set(attrs, options);
this.changed = {};
this.initialize.apply(this, arguments);});
    vvv_tmp1 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp1, TAJS_restrictToType(Model.prototype, 'object'), Events, $__.os.oid7 = {
        'changed': null,
        'validationError': null,
        'idAttribute': 'id',
        'cidPrefix': 'c',
        'initialize': ($__.fs.J$__v257300249_63_310 = function J$__v257300249_63() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');}),
        'toJSON': ($__.fs.J$__v257300249_65_311 = function J$__v257300249_65(options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.clone(this.attributes);}),
        'sync': ($__.fs.J$__v257300249_67_312 = function J$__v257300249_67() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return Backbone.sync.apply(this, arguments);}),
        'get': ($__.fs.J$__v257300249_69_313 = function J$__v257300249_69(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.attributes[attr];}),
        'escape': ($__.fs.J$__v257300249_71_314 = function J$__v257300249_71(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.escape(this.get(attr));}),
        'has': ($__.fs.J$__v257300249_73_315 = function J$__v257300249_73(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.get(attr) != null;}),
        'matches': ($__.fs.J$__v257300249_75_316 = function J$__v257300249_75(attrs) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return !!_.iteratee(attrs, this)(this.attributes);}),
        'set': ($__.fs.J$__v257300249_77_317 = function J$__v257300249_77(key, val, options) {
var vvv_return, vvv_switch, attrs, unset, silent, changes, changing, current, changed, prev;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
key = arguments[0], val = arguments[1], options = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (key == null)
    return this;
if (typeof key === 'object') {
    attrs = key;
    options = val;
} else {
    (attrs = {})[key] = val;
}
options || (options = {});
if (!this._validate(attrs, options))
    return false;
unset = options.unset;
silent = options.silent;
changes = [];
changing = this._changing;
this._changing = true;
if (!changing) {
    this._previousAttributes = _.clone(this.attributes);
    this.changed = {};
}
current = this.attributes;
changed = this.changed;
prev = this._previousAttributes;
for (var attr in attrs) {
    val = attrs[attr];
    if (!_.isEqual(current[attr], val))
        changes.push(attr);
    if (!_.isEqual(prev[attr], val)) {
        changed[attr] = val;
    } else {
        delete changed[attr];
    }
    unset ? delete current[attr] : current[attr] = val;
}
if (this.idAttribute in attrs)
    this.id = this.get(this.idAttribute);
if (!silent) {
    if (changes.length)
        this._pending = options;
    for (var i = 0; i < changes.length; i++) {
        this.trigger('change:' + changes[i], this, current[changes[i]], options);
    }
}
if (changing)
    return this;
if (!silent) {
    while (this._pending) {
        options = this._pending;
        this._pending = false;
        this.trigger('change', this, options);
    }
}
this._pending = false;
this._changing = false;
return this;}),
        'unset': ($__.fs.J$__v257300249_79_318 = function J$__v257300249_79(attr, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.set(attr, void 0, _.extend({}, options, {
    unset: true
}));}),
        'clear': ($__.fs.J$__v257300249_81_319 = function J$__v257300249_81(options) {
var vvv_return, vvv_switch, attrs;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
attrs = {};
for (var key in this.attributes)
    attrs[key] = void 0;
return this.set(attrs, _.extend({}, options, {
    unset: true
}));}),
        'hasChanged': ($__.fs.J$__v257300249_83_320 = function J$__v257300249_83(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (attr == null)
    return !_.isEmpty(this.changed);
return _.has(this.changed, attr);}),
        'changedAttributes': ($__.fs.J$__v257300249_85_321 = function J$__v257300249_85(diff) {
var vvv_return, vvv_switch, old, changed, val;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
diff = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!diff)
    return this.hasChanged() ? _.clone(this.changed) : false;
old = this._changing ? this._previousAttributes : this.attributes;
changed = {};
for (var attr in diff) {
    val = diff[attr];
    if (_.isEqual(old[attr], val))
        continue;
    changed[attr] = val;
}
return _.size(changed) ? changed : false;}),
        'previous': ($__.fs.J$__v257300249_87_322 = function J$__v257300249_87(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (attr == null || !this._previousAttributes)
    return null;
return this._previousAttributes[attr];}),
        'previousAttributes': ($__.fs.J$__v257300249_89_323 = function J$__v257300249_89() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.clone(this._previousAttributes);}),
        'fetch': ($__.fs.J$__v257300249_93_324 = function J$__v257300249_93(options) {
var vvv_return, vvv_switch, model, success;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = _.extend({
    parse: true
}, options);
model = this;
success = options.success;
options.success = function (resp) {
    var serverAttrs = options.parse ? model.parse(resp, options) : resp;
    if (!model.set(serverAttrs, options))
        return false;
    if (success)
        success.call(options.context, model, resp, options);
    model.trigger('sync', model, resp, options);
};
wrapError(this, options);
return this.sync('read', this, options);}),
        'save': ($__.fs.J$__v257300249_97_325 = function J$__v257300249_97(key, val, options) {
var vvv_return, vvv_switch, attrs, wait, model, success, attributes, method, xhr;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
key = arguments[0], val = arguments[1], options = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (key == null || typeof key === 'object') {
    attrs = key;
    options = val;
} else {
    (attrs = {})[key] = val;
}
options = _.extend({
    validate: true,
    parse: true
}, options);
wait = options.wait;
if (attrs && !wait) {
    if (!this.set(attrs, options))
        return false;
} else if (!this._validate(attrs, options)) {
    return false;
}
model = this;
success = options.success;
attributes = this.attributes;
options.success = function (resp) {
    model.attributes = attributes;
    var serverAttrs = options.parse ? model.parse(resp, options) : resp;
    if (wait)
        serverAttrs = _.extend({}, attrs, serverAttrs);
    if (serverAttrs && !model.set(serverAttrs, options))
        return false;
    if (success)
        success.call(options.context, model, resp, options);
    model.trigger('sync', model, resp, options);
};
wrapError(this, options);
if (attrs && wait)
    this.attributes = _.extend({}, attributes, attrs);
method = this.isNew() ? 'create' : options.patch ? 'patch' : 'update';
if (method === 'patch' && !options.attrs)
    options.attrs = attrs;
xhr = this.sync(method, this, options);
this.attributes = attributes;
return xhr;}),
        'destroy': ($__.fs.J$__v257300249_103_326 = function J$__v257300249_103(options) {
var vvv_return, vvv_switch, model, success, wait, destroy, xhr;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = options ? _.clone(options) : {};
model = this;
success = options.success;
wait = options.wait;
destroy = function () {
    model.stopListening();
    model.trigger('destroy', model, model.collection, options);
};
options.success = function (resp) {
    if (wait)
        destroy();
    if (success)
        success.call(options.context, model, resp, options);
    if (!model.isNew())
        model.trigger('sync', model, resp, options);
};
xhr = false;
if (this.isNew()) {
    _.defer(options.success);
} else {
    wrapError(this, options);
    xhr = this.sync('delete', this, options);
}
if (!wait)
    destroy();
return xhr;}),
        'url': ($__.fs.J$__v257300249_105_327 = function J$__v257300249_105() {
var vvv_return, vvv_switch, base, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
base = _.result(this, 'urlRoot') || _.result(this.collection, 'url') || urlError();
if (this.isNew())
    return base;
id = this.get(this.idAttribute);
return base.replace(/[^\/]$/, '$&/') + encodeURIComponent(id);}),
        'parse': ($__.fs.J$__v257300249_107_328 = function J$__v257300249_107(resp, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
resp = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return resp;}),
        'clone': ($__.fs.J$__v257300249_109_329 = function J$__v257300249_109() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return new this.constructor(this.attributes);}),
        'isNew': ($__.fs.J$__v257300249_111_330 = function J$__v257300249_111() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return !this.has(this.idAttribute);}),
        'isValid': ($__.fs.J$__v257300249_113_331 = function J$__v257300249_113(options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this._validate({}, _.extend({}, options, {
    validate: true
}));}),
        '_validate': ($__.fs.J$__v257300249_115_332 = function J$__v257300249_115(attrs, options) {
var vvv_return, vvv_switch, error;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!options.validate || !this.validate)
    return true;
attrs = _.extend({}, this.attributes, attrs);
error = this.validationError = this.validate(attrs, options) || null;
if (!error)
    return true;
this.trigger('invalid', this, error, _.extend(options, {
    validationError: error
}));
return false;})
    }, 1, true, $__.uid);
    modelMethods = $__.os.oid8 = {
        'keys': 1,
        'values': 1,
        'pairs': 1,
        'invert': 1,
        'pick': 0,
        'omit': 0,
        'chain': 1,
        'isEmpty': 1
    };
    (addUnderscoreMethods, $__.fs.J$__v257300249_21_292)((Model, $__.fs.J$__v257300249_61_309), modelMethods, 'attributes', 0, true, $__.uid);
    Collection = (Backbone, $__.os.oid5).Collection = ($__.fs.J$__v257300249_117_342 = function J$__v257300249_117(models, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options || (options = {});
if (options.model)
    this.model = options.model;
if (options.comparator !== void 0)
    this.comparator = options.comparator;
this._reset();
this.initialize.apply(this, arguments);
if (models)
    this.reset(models, _.extend({
    silent: true
}, options));});
    setOptions = $__.os.oid9 = {
        'add': true,
        'remove': true,
        'merge': true
    };
    addOptions = $__.os.oid10 = {
        'add': true,
        'remove': false
    };
    splice = ($__.fs.J$__v257300249_119_343 = function J$__v257300249_119(array, insert, at) {
var vvv_return, vvv_switch, tail, length, i;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], insert = arguments[1], at = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
at = Math.min(Math.max(at, 0), array.length);
tail = Array(array.length - at);
length = insert.length;
for (i = 0; i < tail.length; i++)
    tail[i] = array[i + at];
for (i = 0; i < length; i++)
    array[i + at] = insert[i];
for (i = 0; i < tail.length; i++)
    array[i + length + at] = tail[i];});
    vvv_tmp2 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp2, TAJS_restrictToType(Collection.prototype, 'object'), Events, $__.os.oid11 = {
        'model': Model,
        'initialize': ($__.fs.J$__v257300249_121_344 = function J$__v257300249_121() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');}),
        'toJSON': ($__.fs.J$__v257300249_125_345 = function J$__v257300249_125(options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.map(function (model) {
    return model.toJSON(options);
});}),
        'sync': ($__.fs.J$__v257300249_127_346 = function J$__v257300249_127() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return Backbone.sync.apply(this, arguments);}),
        'add': ($__.fs.J$__v257300249_129_347 = function J$__v257300249_129(models, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.set(models, _.extend({
    merge: false
}, options, addOptions));}),
        'remove': ($__.fs.J$__v257300249_131_348 = function J$__v257300249_131(models, options) {
var vvv_return, vvv_switch, singular, removed;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = _.extend({}, options);
singular = !_.isArray(models);
models = singular ? [models] : models.slice();
removed = this._removeModels(models, options);
if (!options.silent && removed.length) {
    options.changes = {
        added: [],
        merged: [],
        removed: removed
    };
    this.trigger('update', this, options);
}
return singular ? removed[0] : removed;}),
        'set': ($__.fs.J$__v257300249_135_349 = function J$__v257300249_135(models, options) {
var vvv_return, vvv_switch, singular, at, set, toAdd, toMerge, toRemove, modelMap, add, merge, remove, sort, sortable, sortAttr, model, i, existing, attrs, orderChanged, replace;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (models == null)
    return;
options = _.extend({}, setOptions, options);
if (options.parse && !this._isModel(models)) {
    models = this.parse(models, options) || [];
}
singular = !_.isArray(models);
models = singular ? [models] : models.slice();
at = options.at;
if (at != null)
    at = +at;
if (at > this.length)
    at = this.length;
if (at < 0)
    at += this.length + 1;
set = [];
toAdd = [];
toMerge = [];
toRemove = [];
modelMap = {};
add = options.add;
merge = options.merge;
remove = options.remove;
sort = false;
sortable = this.comparator && at == null && options.sort !== false;
sortAttr = _.isString(this.comparator) ? this.comparator : null;
for (i = 0; i < models.length; i++) {
    model = models[i];
    existing = this.get(model);
    if (existing) {
        if (merge && model !== existing) {
            attrs = this._isModel(model) ? model.attributes : model;
            if (options.parse)
                attrs = existing.parse(attrs, options);
            existing.set(attrs, options);
            toMerge.push(existing);
            if (sortable && !sort)
                sort = existing.hasChanged(sortAttr);
        }
        if (!modelMap[existing.cid]) {
            modelMap[existing.cid] = true;
            set.push(existing);
        }
        models[i] = existing;
    } else if (add) {
        model = models[i] = this._prepareModel(model, options);
        if (model) {
            toAdd.push(model);
            this._addReference(model, options);
            modelMap[model.cid] = true;
            set.push(model);
        }
    }
}
if (remove) {
    for (i = 0; i < this.length; i++) {
        model = this.models[i];
        if (!modelMap[model.cid])
            toRemove.push(model);
    }
    if (toRemove.length)
        this._removeModels(toRemove, options);
}
orderChanged = false;
replace = !sortable && add && remove;
if (set.length && replace) {
    orderChanged = this.length !== set.length || _.some(this.models, function (m, index) {
        return m !== set[index];
    });
    this.models.length = 0;
    splice(this.models, set, 0);
    this.length = this.models.length;
} else if (toAdd.length) {
    if (sortable)
        sort = true;
    splice(this.models, toAdd, at == null ? this.length : at);
    this.length = this.models.length;
}
if (sort)
    this.sort({
    silent: true
});
if (!options.silent) {
    for (i = 0; i < toAdd.length; i++) {
        if (at != null)
            options.index = at + i;
        model = toAdd[i];
        model.trigger('add', model, this, options);
    }
    if (sort || orderChanged)
        this.trigger('sort', this, options);
    if (toAdd.length || toRemove.length || toMerge.length) {
        options.changes = {
            added: toAdd,
            removed: toRemove,
            merged: toMerge
        };
        this.trigger('update', this, options);
    }
}
return singular ? models[0] : models;}),
        'reset': ($__.fs.J$__v257300249_137_350 = function J$__v257300249_137(models, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = options ? _.clone(options) : {};
for (var i = 0; i < this.models.length; i++) {
    this._removeReference(this.models[i], options);
}
options.previousModels = this.models;
this._reset();
models = this.add(models, _.extend({
    silent: true
}, options));
if (!options.silent)
    this.trigger('reset', this, options);
return models;}),
        'push': ($__.fs.J$__v257300249_139_351 = function J$__v257300249_139(model, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.add(model, _.extend({
    at: this.length
}, options));}),
        'pop': ($__.fs.J$__v257300249_141_352 = function J$__v257300249_141(options) {
var vvv_return, vvv_switch, model;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
model = this.at(this.length - 1);
return this.remove(model, options);}),
        'unshift': ($__.fs.J$__v257300249_143_353 = function J$__v257300249_143(model, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.add(model, _.extend({
    at: 0
}, options));}),
        'shift': ($__.fs.J$__v257300249_145_354 = function J$__v257300249_145(options) {
var vvv_return, vvv_switch, model;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
model = this.at(0);
return this.remove(model, options);}),
        'slice': ($__.fs.J$__v257300249_147_355 = function J$__v257300249_147() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return slice.apply(this.models, arguments);}),
        'get': ($__.fs.J$__v257300249_149_356 = function J$__v257300249_149(obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (obj == null)
    return void 0;
return this._byId[obj] || this._byId[this.modelId(obj.attributes || obj)] || obj.cid && this._byId[obj.cid];}),
        'has': ($__.fs.J$__v257300249_151_357 = function J$__v257300249_151(obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.get(obj) != null;}),
        'at': ($__.fs.J$__v257300249_153_358 = function J$__v257300249_153(index) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
index = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (index < 0)
    index += this.length;
return this.models[index];}),
        'where': ($__.fs.J$__v257300249_155_359 = function J$__v257300249_155(attrs, first) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0], first = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this[first ? 'find' : 'filter'](attrs);}),
        'findWhere': ($__.fs.J$__v257300249_157_360 = function J$__v257300249_157(attrs) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.where(attrs, true);}),
        'sort': ($__.fs.J$__v257300249_159_361 = function J$__v257300249_159(options) {
var vvv_return, vvv_switch, comparator, length;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
comparator = this.comparator;
if (!comparator)
    throw new Error('Cannot sort a set without a comparator');
options || (options = {});
length = comparator.length;
if (_.isFunction(comparator))
    comparator = _.bind(comparator, this);
if (length === 1 || _.isString(comparator)) {
    this.models = this.sortBy(comparator);
} else {
    this.models.sort(comparator);
}
if (!options.silent)
    this.trigger('sort', this, options);
return this;}),
        'pluck': ($__.fs.J$__v257300249_161_362 = function J$__v257300249_161(attr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attr = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.map(attr + '');}),
        'fetch': ($__.fs.J$__v257300249_165_363 = function J$__v257300249_165(options) {
var vvv_return, vvv_switch, success, collection;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = _.extend({
    parse: true
}, options);
success = options.success;
collection = this;
options.success = function (resp) {
    var method = options.reset ? 'reset' : 'set';
    collection[method](resp, options);
    if (success)
        success.call(options.context, collection, resp, options);
    collection.trigger('sync', collection, resp, options);
};
wrapError(this, options);
return this.sync('read', this, options);}),
        'create': ($__.fs.J$__v257300249_169_364 = function J$__v257300249_169(model, options) {
var vvv_return, vvv_switch, wait, collection, success;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options = options ? _.clone(options) : {};
wait = options.wait;
model = this._prepareModel(model, options);
if (!model)
    return false;
if (!wait)
    this.add(model, options);
collection = this;
success = options.success;
options.success = function (m, resp, callbackOpts) {
    if (wait)
        collection.add(m, callbackOpts);
    if (success)
        success.call(callbackOpts.context, m, resp, callbackOpts);
};
model.save(null, options);
return model;}),
        'parse': ($__.fs.J$__v257300249_171_365 = function J$__v257300249_171(resp, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
resp = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return resp;}),
        'clone': ($__.fs.J$__v257300249_173_366 = function J$__v257300249_173() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return new this.constructor(this.models, {
    model: this.model,
    comparator: this.comparator
});}),
        'modelId': ($__.fs.J$__v257300249_175_367 = function J$__v257300249_175(attrs) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return attrs[this.model.prototype.idAttribute || 'id'];}),
        '_reset': ($__.fs.J$__v257300249_177_368 = function J$__v257300249_177() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.length = 0;
this.models = [];
this._byId = {};}),
        '_prepareModel': ($__.fs.J$__v257300249_179_369 = function J$__v257300249_179(attrs, options) {
var vvv_return, vvv_switch, model;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attrs = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (this._isModel(attrs)) {
    if (!attrs.collection)
        attrs.collection = this;
    return attrs;
}
options = options ? _.clone(options) : {};
options.collection = this;
model = new this.model(attrs, options);
if (!model.validationError)
    return model;
this.trigger('invalid', this, model.validationError, options);
return false;}),
        '_removeModels': ($__.fs.J$__v257300249_181_370 = function J$__v257300249_181(models, options) {
var vvv_return, vvv_switch, removed, model, index, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
models = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
removed = [];
for (var i = 0; i < models.length; i++) {
    model = this.get(models[i]);
    if (!model)
        continue;
    index = this.indexOf(model);
    this.models.splice(index, 1);
    this.length--;
    delete this._byId[model.cid];
    id = this.modelId(model.attributes);
    if (id != null)
        delete this._byId[id];
    if (!options.silent) {
        options.index = index;
        model.trigger('remove', model, this, options);
    }
    removed.push(model);
    this._removeReference(model, options);
}
return removed;}),
        '_isModel': ($__.fs.J$__v257300249_183_371 = function J$__v257300249_183(model) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return model instanceof Model;}),
        '_addReference': ($__.fs.J$__v257300249_185_372 = function J$__v257300249_185(model, options) {
var vvv_return, vvv_switch, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this._byId[model.cid] = model;
id = this.modelId(model.attributes);
if (id != null)
    this._byId[id] = model;
model.on('all', this._onModelEvent, this);}),
        '_removeReference': ($__.fs.J$__v257300249_187_373 = function J$__v257300249_187(model, options) {
var vvv_return, vvv_switch, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
delete this._byId[model.cid];
id = this.modelId(model.attributes);
if (id != null)
    delete this._byId[id];
if (this === model.collection)
    delete model.collection;
model.off('all', this._onModelEvent, this);}),
        '_onModelEvent': ($__.fs.J$__v257300249_189_374 = function J$__v257300249_189(event, model, collection, options) {
var vvv_return, vvv_switch, prevId, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
event = arguments[0], model = arguments[1], collection = arguments[2], options = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (model) {
    if ((event === 'add' || event === 'remove') && collection !== this)
        return;
    if (event === 'destroy')
        this.remove(model, options);
    if (event === 'change') {
        prevId = this.modelId(model.previousAttributes());
        id = this.modelId(model.attributes);
        if (prevId !== id) {
            if (prevId != null)
                delete this._byId[prevId];
            if (id != null)
                this._byId[id] = model;
        }
    }
}
this.trigger.apply(this, arguments);})
    }, 2, true, $__.uid);
    collectionMethods = $__.os.oid12 = {
        'forEach': 3,
        'each': 3,
        'map': 3,
        'collect': 3,
        'reduce': 0,
        'foldl': 0,
        'inject': 0,
        'reduceRight': 0,
        'foldr': 0,
        'find': 3,
        'detect': 3,
        'filter': 3,
        'select': 3,
        'reject': 3,
        'every': 3,
        'all': 3,
        'some': 3,
        'any': 3,
        'include': 3,
        'includes': 3,
        'contains': 3,
        'invoke': 0,
        'max': 3,
        'min': 3,
        'toArray': 1,
        'size': 1,
        'first': 3,
        'head': 3,
        'take': 3,
        'initial': 3,
        'rest': 3,
        'tail': 3,
        'drop': 3,
        'last': 3,
        'without': 0,
        'difference': 0,
        'indexOf': 3,
        'shuffle': 1,
        'lastIndexOf': 3,
        'isEmpty': 1,
        'chain': 1,
        'sample': 3,
        'partition': 3,
        'groupBy': 3,
        'countBy': 3,
        'sortBy': 3,
        'indexBy': 3,
        'findIndex': 3,
        'findLastIndex': 3
    };
    (addUnderscoreMethods, $__.fs.J$__v257300249_21_292)((Collection, $__.fs.J$__v257300249_117_342), collectionMethods, 'models', 1, true, $__.uid);
    View = (Backbone, $__.os.oid5).View = ($__.fs.J$__v257300249_191_425 = function J$__v257300249_191(options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.cid = _.uniqueId('view');
_.extend(this, _.pick(options, viewOptions));
this._ensureElement();
this.initialize.apply(this, arguments);});
    delegateEventSplitter = /^(\S+)\s*(.*)$/;
    viewOptions = [
        'model',
        'collection',
        'el',
        'id',
        'attributes',
        'className',
        'tagName',
        'events'
    ];
    vvv_tmp3 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp3, TAJS_restrictToType(View.prototype, 'object'), Events, $__.os.oid13 = {
        'tagName': 'div',
        '$': ($__.fs.J$__v257300249_193_426 = function J$__v257300249_193(selector) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
selector = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this.$el.find(selector);}),
        'initialize': ($__.fs.J$__v257300249_195_427 = function J$__v257300249_195() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');}),
        'render': ($__.fs.J$__v257300249_197_428 = function J$__v257300249_197() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this;}),
        'remove': ($__.fs.J$__v257300249_199_429 = function J$__v257300249_199() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this._removeElement();
this.stopListening();
return this;}),
        '_removeElement': ($__.fs.J$__v257300249_201_430 = function J$__v257300249_201() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.$el.remove();}),
        'setElement': ($__.fs.J$__v257300249_203_431 = function J$__v257300249_203(element) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
element = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.undelegateEvents();
this._setElement(element);
this.delegateEvents();
return this;}),
        '_setElement': ($__.fs.J$__v257300249_205_432 = function J$__v257300249_205(el) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
el = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.$el = el instanceof Backbone.$ ? el : Backbone.$(el);
this.el = this.$el[0];}),
        'delegateEvents': ($__.fs.J$__v257300249_207_433 = function J$__v257300249_207(events) {
var vvv_return, vvv_switch, method, match;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
events = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
events || (events = _.result(this, 'events'));
if (!events)
    return this;
this.undelegateEvents();
for (var key in events) {
    method = events[key];
    if (!_.isFunction(method))
        method = this[method];
    if (!method)
        continue;
    match = key.match(delegateEventSplitter);
    this.delegate(match[1], match[2], _.bind(method, this));
}
return this;}),
        'delegate': ($__.fs.J$__v257300249_209_434 = function J$__v257300249_209(eventName, selector, listener) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
eventName = arguments[0], selector = arguments[1], listener = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.$el.on(eventName + '.delegateEvents' + this.cid, selector, listener);
return this;}),
        'undelegateEvents': ($__.fs.J$__v257300249_211_435 = function J$__v257300249_211() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (this.$el)
    this.$el.off('.delegateEvents' + this.cid);
return this;}),
        'undelegate': ($__.fs.J$__v257300249_213_436 = function J$__v257300249_213(eventName, selector, listener) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
eventName = arguments[0], selector = arguments[1], listener = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.$el.off(eventName + '.delegateEvents' + this.cid, selector, listener);
return this;}),
        '_createElement': ($__.fs.J$__v257300249_215_437 = function J$__v257300249_215(tagName) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
tagName = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return document.createElement(tagName);}),
        '_ensureElement': ($__.fs.J$__v257300249_217_438 = function J$__v257300249_217() {
var vvv_return, vvv_switch, attrs;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this.el) {
    attrs = _.extend({}, _.result(this, 'attributes'));
    if (this.id)
        attrs.id = _.result(this, 'id');
    if (this.className)
        attrs['class'] = _.result(this, 'className');
    this.setElement(this._createElement(_.result(this, 'tagName')));
    this._setAttributes(attrs);
} else {
    this.setElement(_.result(this, 'el'));
}}),
        '_setAttributes': ($__.fs.J$__v257300249_219_439 = function J$__v257300249_219(attributes) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
attributes = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.$el.attr(attributes);})
    }, 3, true, $__.uid);
    (Backbone, $__.os.oid5).sync = ($__.fs.J$__v257300249_225_440 = function J$__v257300249_225(method, model, options) {
var vvv_return, vvv_switch, type, params, beforeSend, error, xhr;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
method = arguments[0], model = arguments[1], options = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
type = methodMap[method];
_.defaults(options || (options = {}), {
    emulateHTTP: Backbone.emulateHTTP,
    emulateJSON: Backbone.emulateJSON
});
params = {
    type: type,
    dataType: 'json'
};
if (!options.url) {
    params.url = _.result(model, 'url') || urlError();
}
if (options.data == null && model && (method === 'create' || method === 'update' || method === 'patch')) {
    params.contentType = 'application/json';
    params.data = JSON.stringify(options.attrs || model.toJSON(options));
}
if (options.emulateJSON) {
    params.contentType = 'application/x-www-form-urlencoded';
    params.data = params.data ? {
        model: params.data
    } : {};
}
if (options.emulateHTTP && (type === 'PUT' || type === 'DELETE' || type === 'PATCH')) {
    params.type = 'POST';
    if (options.emulateJSON)
        params.data._method = type;
    beforeSend = options.beforeSend;
    options.beforeSend = function (xhr) {
        xhr.setRequestHeader('X-HTTP-Method-Override', type);
        if (beforeSend)
            return beforeSend.apply(this, arguments);
    };
}
if (params.type !== 'GET' && !options.emulateJSON) {
    params.processData = false;
}
error = options.error;
options.error = function (xhr, textStatus, errorThrown) {
    options.textStatus = textStatus;
    options.errorThrown = errorThrown;
    if (error)
        error.call(options.context, xhr, textStatus, errorThrown);
};
xhr = options.xhr = Backbone.ajax(_.extend(params, options));
model.trigger('request', model, xhr, options);
return xhr;});
    methodMap = $__.os.oid14 = {
        'create': 'POST',
        'update': 'PUT',
        'patch': 'PATCH',
        'delete': 'DELETE',
        'read': 'GET'
    };
    (Backbone, $__.os.oid5).ajax = ($__.fs.J$__v257300249_227_441 = function J$__v257300249_227() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return Backbone.$.ajax.apply(Backbone.$, arguments);});
    Router = (Backbone, $__.os.oid5).Router = ($__.fs.J$__v257300249_229_442 = function J$__v257300249_229(options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
options || (options = {});
if (options.routes)
    this.routes = options.routes;
this._bindRoutes();
this.initialize.apply(this, arguments);});
    optionalParam = /\((.*?)\)/g;
    namedParam = /(\(\?)?:\w+/g;
    splatParam = /\*\w+/g;
    escapeRegExp = /[\-{}\[\]+?.,\\\^$|#\s]/g;
    vvv_tmp4 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp4, TAJS_restrictToType(Router.prototype, 'object'), Events, $__.os.oid15 = {
        'initialize': ($__.fs.J$__v257300249_231_443 = function J$__v257300249_231() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');}),
        'route': ($__.fs.J$__v257300249_235_444 = function J$__v257300249_235(route, name, callback) {
var vvv_return, vvv_switch, router;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
route = arguments[0], name = arguments[1], callback = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!_.isRegExp(route))
    route = this._routeToRegExp(route);
if (_.isFunction(name)) {
    callback = name;
    name = '';
}
if (!callback)
    callback = this[name];
router = this;
Backbone.history.route(route, function (fragment) {
    var args = router._extractParameters(route, fragment);
    if (router.execute(callback, args, name) !== false) {
        router.trigger.apply(router, ['route:' + name].concat(args));
        router.trigger('route', name, args);
        Backbone.history.trigger('route', router, name, args);
    }
});
return this;}),
        'execute': ($__.fs.J$__v257300249_237_445 = function J$__v257300249_237(callback, args, name) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
callback = arguments[0], args = arguments[1], name = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (callback)
    callback.apply(this, args);}),
        'navigate': ($__.fs.J$__v257300249_239_446 = function J$__v257300249_239(fragment, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fragment = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
Backbone.history.navigate(fragment, options);
return this;}),
        '_bindRoutes': ($__.fs.J$__v257300249_241_447 = function J$__v257300249_241() {
var vvv_return, vvv_switch, route, routes;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this.routes)
    return;
this.routes = _.result(this, 'routes');
routes = _.keys(this.routes);
while ((route = routes.pop()) != null) {
    this.route(route, this.routes[route]);
}}),
        '_routeToRegExp': ($__.fs.J$__v257300249_245_448 = function J$__v257300249_245(route) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
route = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
route = route.replace(escapeRegExp, '\\$&').replace(optionalParam, '(?:$1)?').replace(namedParam, function (match, optional) {
    return optional ? match : '([^/?]+)';
}).replace(splatParam, '([^?]*?)');
return new RegExp('^' + route + '(?:\\?([\\s\\S]*))?$');}),
        '_extractParameters': ($__.fs.J$__v257300249_249_449 = function J$__v257300249_249(route, fragment) {
var vvv_return, vvv_switch, params;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
route = arguments[0], fragment = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
params = route.exec(fragment).slice(1);
return _.map(params, function (param, i) {
    if (i === params.length - 1)
        return param || null;
    return param ? decodeURIComponent(param) : null;
});})
    }, 4, true, $__.uid);
    History = (Backbone, $__.os.oid5).History = ($__.fs.J$__v257300249_251_450 = function J$__v257300249_251() {
var vvv_return, vvv_switch, vvv_tmp0;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    this.handlers = [];
    this.checkUrl = (vvv_tmp0 = _, 'bind', $__.fs.J$__v7733624767_149_71.call(vvv_tmp0, (TAJS_restrictToType(this.checkUrl, 'function'), $__.fs.J$__v257300249_277_461), this, 0, true, $__.uid));
    typeof window !== 'undefined';
    this.location = TAJS_restrictToType(window.location, 'object');
    this.history = TAJS_restrictToType(window.history, 'object');
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.handlers = [];
this.checkUrl = _.bind(this.checkUrl, this);
if (typeof window !== 'undefined') {
    this.location = window.location;
    this.history = window.history;
}});
    routeStripper = /^[#\/]|\s+$/g;
    rootStripper = /^\/+|\/+$/g;
    pathStripper = /#.*$/;
    History.started = false;
    vvv_tmp5 = _, 'extend', $__.fs.J$__v7733624767_21_92.call(vvv_tmp5, TAJS_restrictToType(History.prototype, 'object'), Events, $__.os.oid16 = {
        'interval': 50,
        'atRoot': ($__.fs.J$__v257300249_253_451 = function J$__v257300249_253() {
var vvv_return, vvv_switch, path;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
path = this.location.pathname.replace(/[^\/]$/, '$&/');
return path === this.root && !this.getSearch();}),
        'matchRoot': ($__.fs.J$__v257300249_255_452 = function J$__v257300249_255() {
var vvv_return, vvv_switch, path, rootPath;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
path = this.decodeFragment(this.location.pathname);
rootPath = path.slice(0, this.root.length - 1) + '/';
return rootPath === this.root;}),
        'decodeFragment': ($__.fs.J$__v257300249_257_453 = function J$__v257300249_257(fragment) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fragment = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return decodeURI(fragment.replace(/%25/g, '%2525'));}),
        'getSearch': ($__.fs.J$__v257300249_259_454 = function J$__v257300249_259() {
var vvv_return, vvv_switch, match;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
match = this.location.href.replace(/#.*/, '').match(/\?.+/);
return match ? match[0] : '';}),
        'getHash': ($__.fs.J$__v257300249_261_455 = function J$__v257300249_261(window) {
var vvv_return, vvv_switch, match;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
window = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
match = (window || this).location.href.match(/#(.*)$/);
return match ? match[1] : '';}),
        'getPath': ($__.fs.J$__v257300249_263_456 = function J$__v257300249_263() {
var vvv_return, vvv_switch, path;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
path = this.decodeFragment(this.location.pathname + this.getSearch()).slice(this.root.length - 1);
return path.charAt(0) === '/' ? path.slice(1) : path;}),
        'getFragment': ($__.fs.J$__v257300249_265_457 = function J$__v257300249_265(fragment) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fragment = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (fragment == null) {
    if (this._usePushState || !this._wantsHashChange) {
        fragment = this.getPath();
    } else {
        fragment = this.getHash();
    }
}
return fragment.replace(routeStripper, '');}),
        'start': ($__.fs.J$__v257300249_269_458 = function J$__v257300249_269(options) {
var vvv_return, vvv_switch, rootPath, body, iWindow, addEventListener;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
options = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (History.started)
    throw new Error('Backbone.history has already been started');
History.started = true;
this.options = _.extend({
    root: '/'
}, this.options, options);
this.root = this.options.root;
this._wantsHashChange = this.options.hashChange !== false;
this._hasHashChange = 'onhashchange' in window && (document.documentMode === void 0 || document.documentMode > 7);
this._useHashChange = this._wantsHashChange && this._hasHashChange;
this._wantsPushState = !!this.options.pushState;
this._hasPushState = !!(this.history && this.history.pushState);
this._usePushState = this._wantsPushState && this._hasPushState;
this.fragment = this.getFragment();
this.root = ('/' + this.root + '/').replace(rootStripper, '/');
if (this._wantsHashChange && this._wantsPushState) {
    if (!this._hasPushState && !this.atRoot()) {
        rootPath = this.root.slice(0, -1) || '/';
        this.location.replace(rootPath + '#' + this.getPath());
        return true;
    } else if (this._hasPushState && this.atRoot()) {
        this.navigate(this.getHash(), {
            replace: true
        });
    }
}
if (!this._hasHashChange && this._wantsHashChange && !this._usePushState) {
    this.iframe = document.createElement('iframe');
    this.iframe.src = 'javascript:0';
    this.iframe.style.display = 'none';
    this.iframe.tabIndex = -1;
    body = document.body;
    iWindow = body.insertBefore(this.iframe, body.firstChild).contentWindow;
    iWindow.document.open();
    iWindow.document.close();
    iWindow.location.hash = '#' + this.fragment;
}
addEventListener = window.addEventListener || function (eventName, listener) {
    return attachEvent('on' + eventName, listener);
};
if (this._usePushState) {
    addEventListener('popstate', this.checkUrl, false);
} else if (this._useHashChange && !this.iframe) {
    addEventListener('hashchange', this.checkUrl, false);
} else if (this._wantsHashChange) {
    this._checkUrlInterval = setInterval(this.checkUrl, this.interval);
}
if (!this.options.silent)
    return this.loadUrl();}),
        'stop': ($__.fs.J$__v257300249_273_459 = function J$__v257300249_273() {
var vvv_return, vvv_switch, removeEventListener;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
removeEventListener = window.removeEventListener || function (eventName, listener) {
    return detachEvent('on' + eventName, listener);
};
if (this._usePushState) {
    removeEventListener('popstate', this.checkUrl, false);
} else if (this._useHashChange && !this.iframe) {
    removeEventListener('hashchange', this.checkUrl, false);
}
if (this.iframe) {
    document.body.removeChild(this.iframe);
    this.iframe = null;
}
if (this._checkUrlInterval)
    clearInterval(this._checkUrlInterval);
History.started = false;}),
        'route': ($__.fs.J$__v257300249_275_460 = function J$__v257300249_275(route, callback) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
route = arguments[0], callback = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
this.handlers.unshift({
    route: route,
    callback: callback
});}),
        'checkUrl': ($__.fs.J$__v257300249_277_461 = function J$__v257300249_277(e) {
var vvv_return, vvv_switch, current;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
e = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
current = this.getFragment();
if (current === this.fragment && this.iframe) {
    current = this.getHash(this.iframe.contentWindow);
}
if (current === this.fragment)
    return false;
if (this.iframe)
    this.navigate(current);
this.loadUrl();}),
        'loadUrl': ($__.fs.J$__v257300249_281_462 = function J$__v257300249_281(fragment) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fragment = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this.matchRoot())
    return false;
fragment = this.fragment = this.getFragment(fragment);
return _.some(this.handlers, function (handler) {
    if (handler.route.test(fragment)) {
        handler.callback(fragment);
        return true;
    }
});}),
        'navigate': ($__.fs.J$__v257300249_283_463 = function J$__v257300249_283(fragment, options) {
var vvv_return, vvv_switch, rootPath, url, iWindow;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fragment = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!History.started)
    return false;
if (!options || options === true)
    options = {
    trigger: !!options
};
fragment = this.getFragment(fragment || '');
rootPath = this.root;
if (fragment === '' || fragment.charAt(0) === '?') {
    rootPath = rootPath.slice(0, -1) || '/';
}
url = rootPath + fragment;
fragment = this.decodeFragment(fragment.replace(pathStripper, ''));
if (this.fragment === fragment)
    return;
this.fragment = fragment;
if (this._usePushState) {
    this.history[options.replace ? 'replaceState' : 'pushState']({}, document.title, url);
} else if (this._wantsHashChange) {
    this._updateHash(this.location, fragment, options.replace);
    if (this.iframe && fragment !== this.getHash(this.iframe.contentWindow)) {
        iWindow = this.iframe.contentWindow;
        if (!options.replace) {
            iWindow.document.open();
            iWindow.document.close();
        }
        this._updateHash(iWindow.location, fragment, options.replace);
    }
} else {
    return this.location.assign(url);
}
if (options.trigger)
    return this.loadUrl(fragment);}),
        '_updateHash': ($__.fs.J$__v257300249_285_464 = function J$__v257300249_285(location, fragment, replace) {
var vvv_return, vvv_switch, href;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
location = arguments[0], fragment = arguments[1], replace = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (replace) {
    href = location.href.replace(/(javascript:|#).*$/, '');
    location.replace(href + '#' + fragment);
} else {
    location.hash = '#' + fragment;
}})
    }, 5, true, $__.uid);
    (Backbone, $__.os.oid5).history = new (History, $__.fs.J$__v257300249_251_450)(0, true, $__.uid);
    extend = ($__.fs.J$__v257300249_289_466 = function J$__v257300249_289(protoProps, staticProps) {
var vvv_return, vvv_switch, parent, child;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
protoProps = arguments[0], staticProps = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
parent = this;
if (protoProps && _.has(protoProps, 'constructor')) {
    child = protoProps.constructor;
} else {
    child = function () {
        return parent.apply(this, arguments);
    };
}
_.extend(child, parent, staticProps);
child.prototype = _.create(parent.prototype, protoProps);
child.prototype.constructor = child;
child.__super__ = parent.prototype;
return child;});
    Model.extend = Collection.extend = Router.extend = View.extend = History.extend = extend;
    urlError = ($__.fs.J$__v257300249_291_467 = function J$__v257300249_291() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
throw new Error('A "url" property or function must be specified');});
    wrapError = ($__.fs.J$__v257300249_295_468 = function J$__v257300249_295(model, options) {
var vvv_return, vvv_switch, error;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
model = arguments[0], options = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
error = options.error;
options.error = function (resp) {
    if (error)
        error.call(options.context, model, resp, options);
    model.trigger('error', model, resp, options);
};});
    return Backbone;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
previousBackbone = root.Backbone;
slice = Array.prototype.slice;
Backbone.VERSION = '1.3.3';
Backbone.$ = $;
Backbone.noConflict = function () {
    root.Backbone = previousBackbone;
    return this;
};
Backbone.emulateHTTP = false;
Backbone.emulateJSON = false;
addMethod = function (length, method, attribute) {
    switch (length) {
    case 1:
        return function () {
            return _[method](this[attribute]);
        };
    case 2:
        return function (value) {
            return _[method](this[attribute], value);
        };
    case 3:
        return function (iteratee, context) {
            return _[method](this[attribute], cb(iteratee, this), context);
        };
    case 4:
        return function (iteratee, defaultVal, context) {
            return _[method](this[attribute], cb(iteratee, this), defaultVal, context);
        };
    default:
        return function () {
            var args = slice.call(arguments);
            args.unshift(this[attribute]);
            return _[method].apply(_, args);
        };
    }
};
addUnderscoreMethods = function (Class, methods, attribute) {
    _.each(methods, function (length, method) {
        if (_[method])
            Class.prototype[method] = addMethod(length, method, attribute);
    });
};
cb = function (iteratee, instance) {
    if (_.isFunction(iteratee))
        return iteratee;
    if (_.isObject(iteratee) && !instance._isModel(iteratee))
        return modelMatcher(iteratee);
    if (_.isString(iteratee))
        return function (model) {
        return model.get(iteratee);
    };
    return iteratee;
};
modelMatcher = function (attrs) {
    var matcher = _.matches(attrs);
    return function (model) {
        return matcher(model.attributes);
    };
};
Events = Backbone.Events = {};
eventSplitter = /\s+/;
eventsApi = function (iteratee, events, name, callback, opts) {
    var i = 0, names;
    if (name && typeof name === 'object') {
        if (callback !== void 0 && 'context' in opts && opts.context === void 0)
            opts.context = callback;
        for (names = (_.keys(name)); i < names.length; i++) {
            events = eventsApi(iteratee, events, names[i], name[names[i]], opts);
        }
    } else if (name && eventSplitter.test(name)) {
        for (names = (name.split(eventSplitter)); i < names.length; i++) {
            events = iteratee(events, names[i], callback, opts);
        }
    } else {
        events = iteratee(events, name, callback, opts);
    }
    return events;
};
Events.on = function (name, callback, context) {
    return internalOn(this, name, callback, context);
};
internalOn = function (obj, name, callback, context, listening) {
    obj._events = eventsApi(onApi, obj._events || {}, name, callback, {
        context: context,
        ctx: obj,
        listening: listening
    });
    if (listening) {
        var listeners = obj._listeners || (obj._listeners = {});
        listeners[listening.id] = listening;
    }
    return obj;
};
Events.listenTo = function (obj, name, callback) {
    if (!obj)
        return this;
    var id = obj._listenId || (obj._listenId = _.uniqueId('l'));
    var listeningTo = this._listeningTo || (this._listeningTo = {});
    var listening = listeningTo[id];
    if (!listening) {
        var thisId = this._listenId || (this._listenId = _.uniqueId('l'));
        listening = listeningTo[id] = {
            obj: obj,
            objId: id,
            id: thisId,
            listeningTo: listeningTo,
            count: 0
        };
    }
    internalOn(obj, name, callback, this, listening);
    return this;
};
onApi = function (events, name, callback, options) {
    if (callback) {
        var handlers = events[name] || (events[name] = []);
        var context = options.context, ctx = options.ctx, listening = options.listening;
        if (listening)
            listening.count++;
        handlers.push({
            callback: callback,
            context: context,
            ctx: context || ctx,
            listening: listening
        });
    }
    return events;
};
Events.off = function (name, callback, context) {
    if (!this._events)
        return this;
    this._events = eventsApi(offApi, this._events, name, callback, {
        context: context,
        listeners: this._listeners
    });
    return this;
};
Events.stopListening = function (obj, name, callback) {
    var listeningTo = this._listeningTo;
    if (!listeningTo)
        return this;
    var ids = obj ? [obj._listenId] : _.keys(listeningTo);
    for (var i = 0; i < ids.length; i++) {
        var listening = listeningTo[ids[i]];
        if (!listening)
            break;
        listening.obj.off(name, callback, this);
    }
    return this;
};
offApi = function (events, name, callback, options) {
    if (!events)
        return;
    var i = 0, listening;
    var context = options.context, listeners = options.listeners;
    if (!name && !callback && !context) {
        var ids = _.keys(listeners);
        for (; i < ids.length; i++) {
            listening = listeners[ids[i]];
            delete listeners[listening.id];
            delete listening.listeningTo[listening.objId];
        }
        return;
    }
    var names = name ? [name] : _.keys(events);
    for (; i < names.length; i++) {
        name = names[i];
        var handlers = events[name];
        if (!handlers)
            break;
        var remaining = [];
        for (var j = 0; j < handlers.length; j++) {
            var handler = handlers[j];
            if (callback && callback !== handler.callback && callback !== handler.callback._callback || context && context !== handler.context) {
                remaining.push(handler);
            } else {
                listening = handler.listening;
                if (listening && --listening.count === 0) {
                    delete listeners[listening.id];
                    delete listening.listeningTo[listening.objId];
                }
            }
        }
        if (remaining.length) {
            events[name] = remaining;
        } else {
            delete events[name];
        }
    }
    return events;
};
Events.once = function (name, callback, context) {
    var events = eventsApi(onceMap, {}, name, callback, _.bind(this.off, this));
    if (typeof name === 'string' && context == null)
        callback = void 0;
    return this.on(events, callback, context);
};
Events.listenToOnce = function (obj, name, callback) {
    var events = eventsApi(onceMap, {}, name, callback, _.bind(this.stopListening, this, obj));
    return this.listenTo(obj, events);
};
onceMap = function (map, name, callback, offer) {
    if (callback) {
        var once = map[name] = _.once(function () {
            offer(name, once);
            callback.apply(this, arguments);
        });
        once._callback = callback;
    }
    return map;
};
Events.trigger = function (name) {
    if (!this._events)
        return this;
    var length = Math.max(0, arguments.length - 1);
    var args = Array(length);
    for (var i = 0; i < length; i++)
        args[i] = arguments[i + 1];
    eventsApi(triggerApi, this._events, name, void 0, args);
    return this;
};
triggerApi = function (objEvents, name, callback, args) {
    if (objEvents) {
        var events = objEvents[name];
        var allEvents = objEvents.all;
        if (events && allEvents)
            allEvents = allEvents.slice();
        if (events)
            triggerEvents(events, args);
        if (allEvents)
            triggerEvents(allEvents, [name].concat(args));
    }
    return objEvents;
};
triggerEvents = function (events, args) {
    var ev, i = -1, l = events.length, a1 = args[0], a2 = args[1], a3 = args[2];
    switch (args.length) {
    case 0:
        while (++i < l)
            (ev = events[i]).callback.call(ev.ctx);
        return;
    case 1:
        while (++i < l)
            (ev = events[i]).callback.call(ev.ctx, a1);
        return;
    case 2:
        while (++i < l)
            (ev = events[i]).callback.call(ev.ctx, a1, a2);
        return;
    case 3:
        while (++i < l)
            (ev = events[i]).callback.call(ev.ctx, a1, a2, a3);
        return;
    default:
        while (++i < l)
            (ev = events[i]).callback.apply(ev.ctx, args);
        return;
    }
};
Events.bind = Events.on;
Events.unbind = Events.off;
_.extend(Backbone, Events);
Model = Backbone.Model = function (attributes, options) {
    var attrs = attributes || {};
    options || (options = {});
    this.cid = _.uniqueId(this.cidPrefix);
    this.attributes = {};
    if (options.collection)
        this.collection = options.collection;
    if (options.parse)
        attrs = this.parse(attrs, options) || {};
    var defaults = _.result(this, 'defaults');
    attrs = _.defaults(_.extend({}, defaults, attrs), defaults);
    this.set(attrs, options);
    this.changed = {};
    this.initialize.apply(this, arguments);
};
_.extend(Model.prototype, Events, {
    changed: null,
    validationError: null,
    idAttribute: 'id',
    cidPrefix: 'c',
    initialize: function () {
    },
    toJSON: function (options) {
        return _.clone(this.attributes);
    },
    sync: function () {
        return Backbone.sync.apply(this, arguments);
    },
    get: function (attr) {
        return this.attributes[attr];
    },
    escape: function (attr) {
        return _.escape(this.get(attr));
    },
    has: function (attr) {
        return this.get(attr) != null;
    },
    matches: function (attrs) {
        return !!_.iteratee(attrs, this)(this.attributes);
    },
    set: function (key, val, options) {
        if (key == null)
            return this;
        var attrs;
        if (typeof key === 'object') {
            attrs = key;
            options = val;
        } else {
            (attrs = {})[key] = val;
        }
        options || (options = {});
        if (!this._validate(attrs, options))
            return false;
        var unset = options.unset;
        var silent = options.silent;
        var changes = [];
        var changing = this._changing;
        this._changing = true;
        if (!changing) {
            this._previousAttributes = _.clone(this.attributes);
            this.changed = {};
        }
        var current = this.attributes;
        var changed = this.changed;
        var prev = this._previousAttributes;
        for (var attr in attrs) {
            val = attrs[attr];
            if (!_.isEqual(current[attr], val))
                changes.push(attr);
            if (!_.isEqual(prev[attr], val)) {
                changed[attr] = val;
            } else {
                delete changed[attr];
            }
            unset ? delete current[attr] : current[attr] = val;
        }
        if (this.idAttribute in attrs)
            this.id = this.get(this.idAttribute);
        if (!silent) {
            if (changes.length)
                this._pending = options;
            for (var i = 0; i < changes.length; i++) {
                this.trigger('change:' + changes[i], this, current[changes[i]], options);
            }
        }
        if (changing)
            return this;
        if (!silent) {
            while (this._pending) {
                options = this._pending;
                this._pending = false;
                this.trigger('change', this, options);
            }
        }
        this._pending = false;
        this._changing = false;
        return this;
    },
    unset: function (attr, options) {
        return this.set(attr, void 0, _.extend({}, options, {
            unset: true
        }));
    },
    clear: function (options) {
        var attrs = {};
        for (var key in this.attributes)
            attrs[key] = void 0;
        return this.set(attrs, _.extend({}, options, {
            unset: true
        }));
    },
    hasChanged: function (attr) {
        if (attr == null)
            return !_.isEmpty(this.changed);
        return _.has(this.changed, attr);
    },
    changedAttributes: function (diff) {
        if (!diff)
            return this.hasChanged() ? _.clone(this.changed) : false;
        var old = this._changing ? this._previousAttributes : this.attributes;
        var changed = {};
        for (var attr in diff) {
            var val = diff[attr];
            if (_.isEqual(old[attr], val))
                continue;
            changed[attr] = val;
        }
        return _.size(changed) ? changed : false;
    },
    previous: function (attr) {
        if (attr == null || !this._previousAttributes)
            return null;
        return this._previousAttributes[attr];
    },
    previousAttributes: function () {
        return _.clone(this._previousAttributes);
    },
    fetch: function (options) {
        options = _.extend({
            parse: true
        }, options);
        var model = this;
        var success = options.success;
        options.success = function (resp) {
            var serverAttrs = options.parse ? model.parse(resp, options) : resp;
            if (!model.set(serverAttrs, options))
                return false;
            if (success)
                success.call(options.context, model, resp, options);
            model.trigger('sync', model, resp, options);
        };
        wrapError(this, options);
        return this.sync('read', this, options);
    },
    save: function (key, val, options) {
        var attrs;
        if (key == null || typeof key === 'object') {
            attrs = key;
            options = val;
        } else {
            (attrs = {})[key] = val;
        }
        options = _.extend({
            validate: true,
            parse: true
        }, options);
        var wait = options.wait;
        if (attrs && !wait) {
            if (!this.set(attrs, options))
                return false;
        } else if (!this._validate(attrs, options)) {
            return false;
        }
        var model = this;
        var success = options.success;
        var attributes = this.attributes;
        options.success = function (resp) {
            model.attributes = attributes;
            var serverAttrs = options.parse ? model.parse(resp, options) : resp;
            if (wait)
                serverAttrs = _.extend({}, attrs, serverAttrs);
            if (serverAttrs && !model.set(serverAttrs, options))
                return false;
            if (success)
                success.call(options.context, model, resp, options);
            model.trigger('sync', model, resp, options);
        };
        wrapError(this, options);
        if (attrs && wait)
            this.attributes = _.extend({}, attributes, attrs);
        var method = this.isNew() ? 'create' : options.patch ? 'patch' : 'update';
        if (method === 'patch' && !options.attrs)
            options.attrs = attrs;
        var xhr = this.sync(method, this, options);
        this.attributes = attributes;
        return xhr;
    },
    destroy: function (options) {
        options = options ? _.clone(options) : {};
        var model = this;
        var success = options.success;
        var wait = options.wait;
        var destroy = function () {
            model.stopListening();
            model.trigger('destroy', model, model.collection, options);
        };
        options.success = function (resp) {
            if (wait)
                destroy();
            if (success)
                success.call(options.context, model, resp, options);
            if (!model.isNew())
                model.trigger('sync', model, resp, options);
        };
        var xhr = false;
        if (this.isNew()) {
            _.defer(options.success);
        } else {
            wrapError(this, options);
            xhr = this.sync('delete', this, options);
        }
        if (!wait)
            destroy();
        return xhr;
    },
    url: function () {
        var base = _.result(this, 'urlRoot') || _.result(this.collection, 'url') || urlError();
        if (this.isNew())
            return base;
        var id = this.get(this.idAttribute);
        return base.replace(/[^\/]$/, '$&/') + encodeURIComponent(id);
    },
    parse: function (resp, options) {
        return resp;
    },
    clone: function () {
        return new this.constructor(this.attributes);
    },
    isNew: function () {
        return !this.has(this.idAttribute);
    },
    isValid: function (options) {
        return this._validate({}, _.extend({}, options, {
            validate: true
        }));
    },
    _validate: function (attrs, options) {
        if (!options.validate || !this.validate)
            return true;
        attrs = _.extend({}, this.attributes, attrs);
        var error = this.validationError = this.validate(attrs, options) || null;
        if (!error)
            return true;
        this.trigger('invalid', this, error, _.extend(options, {
            validationError: error
        }));
        return false;
    }
});
modelMethods = {
    keys: 1,
    values: 1,
    pairs: 1,
    invert: 1,
    pick: 0,
    omit: 0,
    chain: 1,
    isEmpty: 1
};
addUnderscoreMethods(Model, modelMethods, 'attributes');
Collection = Backbone.Collection = function (models, options) {
    options || (options = {});
    if (options.model)
        this.model = options.model;
    if (options.comparator !== void 0)
        this.comparator = options.comparator;
    this._reset();
    this.initialize.apply(this, arguments);
    if (models)
        this.reset(models, _.extend({
        silent: true
    }, options));
};
setOptions = {
    add: true,
    remove: true,
    merge: true
};
addOptions = {
    add: true,
    remove: false
};
splice = function (array, insert, at) {
    at = Math.min(Math.max(at, 0), array.length);
    var tail = Array(array.length - at);
    var length = insert.length;
    var i;
    for (i = 0; i < tail.length; i++)
        tail[i] = array[i + at];
    for (i = 0; i < length; i++)
        array[i + at] = insert[i];
    for (i = 0; i < tail.length; i++)
        array[i + length + at] = tail[i];
};
_.extend(Collection.prototype, Events, {
    model: Model,
    initialize: function () {
    },
    toJSON: function (options) {
        return this.map(function (model) {
            return model.toJSON(options);
        });
    },
    sync: function () {
        return Backbone.sync.apply(this, arguments);
    },
    add: function (models, options) {
        return this.set(models, _.extend({
            merge: false
        }, options, addOptions));
    },
    remove: function (models, options) {
        options = _.extend({}, options);
        var singular = !_.isArray(models);
        models = singular ? [models] : models.slice();
        var removed = this._removeModels(models, options);
        if (!options.silent && removed.length) {
            options.changes = {
                added: [],
                merged: [],
                removed: removed
            };
            this.trigger('update', this, options);
        }
        return singular ? removed[0] : removed;
    },
    set: function (models, options) {
        if (models == null)
            return;
        options = _.extend({}, setOptions, options);
        if (options.parse && !this._isModel(models)) {
            models = this.parse(models, options) || [];
        }
        var singular = !_.isArray(models);
        models = singular ? [models] : models.slice();
        var at = options.at;
        if (at != null)
            at = +at;
        if (at > this.length)
            at = this.length;
        if (at < 0)
            at += this.length + 1;
        var set = [];
        var toAdd = [];
        var toMerge = [];
        var toRemove = [];
        var modelMap = {};
        var add = options.add;
        var merge = options.merge;
        var remove = options.remove;
        var sort = false;
        var sortable = this.comparator && at == null && options.sort !== false;
        var sortAttr = _.isString(this.comparator) ? this.comparator : null;
        var model, i;
        for (i = 0; i < models.length; i++) {
            model = models[i];
            var existing = this.get(model);
            if (existing) {
                if (merge && model !== existing) {
                    var attrs = this._isModel(model) ? model.attributes : model;
                    if (options.parse)
                        attrs = existing.parse(attrs, options);
                    existing.set(attrs, options);
                    toMerge.push(existing);
                    if (sortable && !sort)
                        sort = existing.hasChanged(sortAttr);
                }
                if (!modelMap[existing.cid]) {
                    modelMap[existing.cid] = true;
                    set.push(existing);
                }
                models[i] = existing;
            } else if (add) {
                model = models[i] = this._prepareModel(model, options);
                if (model) {
                    toAdd.push(model);
                    this._addReference(model, options);
                    modelMap[model.cid] = true;
                    set.push(model);
                }
            }
        }
        if (remove) {
            for (i = 0; i < this.length; i++) {
                model = this.models[i];
                if (!modelMap[model.cid])
                    toRemove.push(model);
            }
            if (toRemove.length)
                this._removeModels(toRemove, options);
        }
        var orderChanged = false;
        var replace = !sortable && add && remove;
        if (set.length && replace) {
            orderChanged = this.length !== set.length || _.some(this.models, function (m, index) {
                return m !== set[index];
            });
            this.models.length = 0;
            splice(this.models, set, 0);
            this.length = this.models.length;
        } else if (toAdd.length) {
            if (sortable)
                sort = true;
            splice(this.models, toAdd, at == null ? this.length : at);
            this.length = this.models.length;
        }
        if (sort)
            this.sort({
            silent: true
        });
        if (!options.silent) {
            for (i = 0; i < toAdd.length; i++) {
                if (at != null)
                    options.index = at + i;
                model = toAdd[i];
                model.trigger('add', model, this, options);
            }
            if (sort || orderChanged)
                this.trigger('sort', this, options);
            if (toAdd.length || toRemove.length || toMerge.length) {
                options.changes = {
                    added: toAdd,
                    removed: toRemove,
                    merged: toMerge
                };
                this.trigger('update', this, options);
            }
        }
        return singular ? models[0] : models;
    },
    reset: function (models, options) {
        options = options ? _.clone(options) : {};
        for (var i = 0; i < this.models.length; i++) {
            this._removeReference(this.models[i], options);
        }
        options.previousModels = this.models;
        this._reset();
        models = this.add(models, _.extend({
            silent: true
        }, options));
        if (!options.silent)
            this.trigger('reset', this, options);
        return models;
    },
    push: function (model, options) {
        return this.add(model, _.extend({
            at: this.length
        }, options));
    },
    pop: function (options) {
        var model = this.at(this.length - 1);
        return this.remove(model, options);
    },
    unshift: function (model, options) {
        return this.add(model, _.extend({
            at: 0
        }, options));
    },
    shift: function (options) {
        var model = this.at(0);
        return this.remove(model, options);
    },
    slice: function () {
        return slice.apply(this.models, arguments);
    },
    get: function (obj) {
        if (obj == null)
            return void 0;
        return this._byId[obj] || this._byId[this.modelId(obj.attributes || obj)] || obj.cid && this._byId[obj.cid];
    },
    has: function (obj) {
        return this.get(obj) != null;
    },
    at: function (index) {
        if (index < 0)
            index += this.length;
        return this.models[index];
    },
    where: function (attrs, first) {
        return this[first ? 'find' : 'filter'](attrs);
    },
    findWhere: function (attrs) {
        return this.where(attrs, true);
    },
    sort: function (options) {
        var comparator = this.comparator;
        if (!comparator)
            throw new Error('Cannot sort a set without a comparator');
        options || (options = {});
        var length = comparator.length;
        if (_.isFunction(comparator))
            comparator = _.bind(comparator, this);
        if (length === 1 || _.isString(comparator)) {
            this.models = this.sortBy(comparator);
        } else {
            this.models.sort(comparator);
        }
        if (!options.silent)
            this.trigger('sort', this, options);
        return this;
    },
    pluck: function (attr) {
        return this.map(attr + '');
    },
    fetch: function (options) {
        options = _.extend({
            parse: true
        }, options);
        var success = options.success;
        var collection = this;
        options.success = function (resp) {
            var method = options.reset ? 'reset' : 'set';
            collection[method](resp, options);
            if (success)
                success.call(options.context, collection, resp, options);
            collection.trigger('sync', collection, resp, options);
        };
        wrapError(this, options);
        return this.sync('read', this, options);
    },
    create: function (model, options) {
        options = options ? _.clone(options) : {};
        var wait = options.wait;
        model = this._prepareModel(model, options);
        if (!model)
            return false;
        if (!wait)
            this.add(model, options);
        var collection = this;
        var success = options.success;
        options.success = function (m, resp, callbackOpts) {
            if (wait)
                collection.add(m, callbackOpts);
            if (success)
                success.call(callbackOpts.context, m, resp, callbackOpts);
        };
        model.save(null, options);
        return model;
    },
    parse: function (resp, options) {
        return resp;
    },
    clone: function () {
        return new this.constructor(this.models, {
            model: this.model,
            comparator: this.comparator
        });
    },
    modelId: function (attrs) {
        return attrs[this.model.prototype.idAttribute || 'id'];
    },
    _reset: function () {
        this.length = 0;
        this.models = [];
        this._byId = {};
    },
    _prepareModel: function (attrs, options) {
        if (this._isModel(attrs)) {
            if (!attrs.collection)
                attrs.collection = this;
            return attrs;
        }
        options = options ? _.clone(options) : {};
        options.collection = this;
        var model = new this.model(attrs, options);
        if (!model.validationError)
            return model;
        this.trigger('invalid', this, model.validationError, options);
        return false;
    },
    _removeModels: function (models, options) {
        var removed = [];
        for (var i = 0; i < models.length; i++) {
            var model = this.get(models[i]);
            if (!model)
                continue;
            var index = this.indexOf(model);
            this.models.splice(index, 1);
            this.length--;
            delete this._byId[model.cid];
            var id = this.modelId(model.attributes);
            if (id != null)
                delete this._byId[id];
            if (!options.silent) {
                options.index = index;
                model.trigger('remove', model, this, options);
            }
            removed.push(model);
            this._removeReference(model, options);
        }
        return removed;
    },
    _isModel: function (model) {
        return model instanceof Model;
    },
    _addReference: function (model, options) {
        this._byId[model.cid] = model;
        var id = this.modelId(model.attributes);
        if (id != null)
            this._byId[id] = model;
        model.on('all', this._onModelEvent, this);
    },
    _removeReference: function (model, options) {
        delete this._byId[model.cid];
        var id = this.modelId(model.attributes);
        if (id != null)
            delete this._byId[id];
        if (this === model.collection)
            delete model.collection;
        model.off('all', this._onModelEvent, this);
    },
    _onModelEvent: function (event, model, collection, options) {
        if (model) {
            if ((event === 'add' || event === 'remove') && collection !== this)
                return;
            if (event === 'destroy')
                this.remove(model, options);
            if (event === 'change') {
                var prevId = this.modelId(model.previousAttributes());
                var id = this.modelId(model.attributes);
                if (prevId !== id) {
                    if (prevId != null)
                        delete this._byId[prevId];
                    if (id != null)
                        this._byId[id] = model;
                }
            }
        }
        this.trigger.apply(this, arguments);
    }
});
collectionMethods = {
    forEach: 3,
    each: 3,
    map: 3,
    collect: 3,
    reduce: 0,
    foldl: 0,
    inject: 0,
    reduceRight: 0,
    foldr: 0,
    find: 3,
    detect: 3,
    filter: 3,
    select: 3,
    reject: 3,
    every: 3,
    all: 3,
    some: 3,
    any: 3,
    include: 3,
    includes: 3,
    contains: 3,
    invoke: 0,
    max: 3,
    min: 3,
    toArray: 1,
    size: 1,
    first: 3,
    head: 3,
    take: 3,
    initial: 3,
    rest: 3,
    tail: 3,
    drop: 3,
    last: 3,
    without: 0,
    difference: 0,
    indexOf: 3,
    shuffle: 1,
    lastIndexOf: 3,
    isEmpty: 1,
    chain: 1,
    sample: 3,
    partition: 3,
    groupBy: 3,
    countBy: 3,
    sortBy: 3,
    indexBy: 3,
    findIndex: 3,
    findLastIndex: 3
};
addUnderscoreMethods(Collection, collectionMethods, 'models');
View = Backbone.View = function (options) {
    this.cid = _.uniqueId('view');
    _.extend(this, _.pick(options, viewOptions));
    this._ensureElement();
    this.initialize.apply(this, arguments);
};
delegateEventSplitter = /^(\S+)\s*(.*)$/;
viewOptions = [
    'model',
    'collection',
    'el',
    'id',
    'attributes',
    'className',
    'tagName',
    'events'
];
_.extend(View.prototype, Events, {
    tagName: 'div',
    $: function (selector) {
        return this.$el.find(selector);
    },
    initialize: function () {
    },
    render: function () {
        return this;
    },
    remove: function () {
        this._removeElement();
        this.stopListening();
        return this;
    },
    _removeElement: function () {
        this.$el.remove();
    },
    setElement: function (element) {
        this.undelegateEvents();
        this._setElement(element);
        this.delegateEvents();
        return this;
    },
    _setElement: function (el) {
        this.$el = el instanceof Backbone.$ ? el : Backbone.$(el);
        this.el = this.$el[0];
    },
    delegateEvents: function (events) {
        events || (events = _.result(this, 'events'));
        if (!events)
            return this;
        this.undelegateEvents();
        for (var key in events) {
            var method = events[key];
            if (!_.isFunction(method))
                method = this[method];
            if (!method)
                continue;
            var match = key.match(delegateEventSplitter);
            this.delegate(match[1], match[2], _.bind(method, this));
        }
        return this;
    },
    delegate: function (eventName, selector, listener) {
        this.$el.on(eventName + '.delegateEvents' + this.cid, selector, listener);
        return this;
    },
    undelegateEvents: function () {
        if (this.$el)
            this.$el.off('.delegateEvents' + this.cid);
        return this;
    },
    undelegate: function (eventName, selector, listener) {
        this.$el.off(eventName + '.delegateEvents' + this.cid, selector, listener);
        return this;
    },
    _createElement: function (tagName) {
        return document.createElement(tagName);
    },
    _ensureElement: function () {
        if (!this.el) {
            var attrs = _.extend({}, _.result(this, 'attributes'));
            if (this.id)
                attrs.id = _.result(this, 'id');
            if (this.className)
                attrs['class'] = _.result(this, 'className');
            this.setElement(this._createElement(_.result(this, 'tagName')));
            this._setAttributes(attrs);
        } else {
            this.setElement(_.result(this, 'el'));
        }
    },
    _setAttributes: function (attributes) {
        this.$el.attr(attributes);
    }
});
Backbone.sync = function (method, model, options) {
    var type = methodMap[method];
    _.defaults(options || (options = {}), {
        emulateHTTP: Backbone.emulateHTTP,
        emulateJSON: Backbone.emulateJSON
    });
    var params = {
        type: type,
        dataType: 'json'
    };
    if (!options.url) {
        params.url = _.result(model, 'url') || urlError();
    }
    if (options.data == null && model && (method === 'create' || method === 'update' || method === 'patch')) {
        params.contentType = 'application/json';
        params.data = JSON.stringify(options.attrs || model.toJSON(options));
    }
    if (options.emulateJSON) {
        params.contentType = 'application/x-www-form-urlencoded';
        params.data = params.data ? {
            model: params.data
        } : {};
    }
    if (options.emulateHTTP && (type === 'PUT' || type === 'DELETE' || type === 'PATCH')) {
        params.type = 'POST';
        if (options.emulateJSON)
            params.data._method = type;
        var beforeSend = options.beforeSend;
        options.beforeSend = function (xhr) {
            xhr.setRequestHeader('X-HTTP-Method-Override', type);
            if (beforeSend)
                return beforeSend.apply(this, arguments);
        };
    }
    if (params.type !== 'GET' && !options.emulateJSON) {
        params.processData = false;
    }
    var error = options.error;
    options.error = function (xhr, textStatus, errorThrown) {
        options.textStatus = textStatus;
        options.errorThrown = errorThrown;
        if (error)
            error.call(options.context, xhr, textStatus, errorThrown);
    };
    var xhr = options.xhr = Backbone.ajax(_.extend(params, options));
    model.trigger('request', model, xhr, options);
    return xhr;
};
methodMap = {
    'create': 'POST',
    'update': 'PUT',
    'patch': 'PATCH',
    'delete': 'DELETE',
    'read': 'GET'
};
Backbone.ajax = function () {
    return Backbone.$.ajax.apply(Backbone.$, arguments);
};
Router = Backbone.Router = function (options) {
    options || (options = {});
    if (options.routes)
        this.routes = options.routes;
    this._bindRoutes();
    this.initialize.apply(this, arguments);
};
optionalParam = /\((.*?)\)/g;
namedParam = /(\(\?)?:\w+/g;
splatParam = /\*\w+/g;
escapeRegExp = /[\-{}\[\]+?.,\\\^$|#\s]/g;
_.extend(Router.prototype, Events, {
    initialize: function () {
    },
    route: function (route, name, callback) {
        if (!_.isRegExp(route))
            route = this._routeToRegExp(route);
        if (_.isFunction(name)) {
            callback = name;
            name = '';
        }
        if (!callback)
            callback = this[name];
        var router = this;
        Backbone.history.route(route, function (fragment) {
            var args = router._extractParameters(route, fragment);
            if (router.execute(callback, args, name) !== false) {
                router.trigger.apply(router, ['route:' + name].concat(args));
                router.trigger('route', name, args);
                Backbone.history.trigger('route', router, name, args);
            }
        });
        return this;
    },
    execute: function (callback, args, name) {
        if (callback)
            callback.apply(this, args);
    },
    navigate: function (fragment, options) {
        Backbone.history.navigate(fragment, options);
        return this;
    },
    _bindRoutes: function () {
        if (!this.routes)
            return;
        this.routes = _.result(this, 'routes');
        var route, routes = _.keys(this.routes);
        while ((route = routes.pop()) != null) {
            this.route(route, this.routes[route]);
        }
    },
    _routeToRegExp: function (route) {
        route = route.replace(escapeRegExp, '\\$&').replace(optionalParam, '(?:$1)?').replace(namedParam, function (match, optional) {
            return optional ? match : '([^/?]+)';
        }).replace(splatParam, '([^?]*?)');
        return new RegExp('^' + route + '(?:\\?([\\s\\S]*))?$');
    },
    _extractParameters: function (route, fragment) {
        var params = route.exec(fragment).slice(1);
        return _.map(params, function (param, i) {
            if (i === params.length - 1)
                return param || null;
            return param ? decodeURIComponent(param) : null;
        });
    }
});
History = Backbone.History = function () {
    this.handlers = [];
    this.checkUrl = _.bind(this.checkUrl, this);
    if (typeof window !== 'undefined') {
        this.location = window.location;
        this.history = window.history;
    }
};
routeStripper = /^[#\/]|\s+$/g;
rootStripper = /^\/+|\/+$/g;
pathStripper = /#.*$/;
History.started = false;
_.extend(History.prototype, Events, {
    interval: 50,
    atRoot: function () {
        var path = this.location.pathname.replace(/[^\/]$/, '$&/');
        return path === this.root && !this.getSearch();
    },
    matchRoot: function () {
        var path = this.decodeFragment(this.location.pathname);
        var rootPath = path.slice(0, this.root.length - 1) + '/';
        return rootPath === this.root;
    },
    decodeFragment: function (fragment) {
        return decodeURI(fragment.replace(/%25/g, '%2525'));
    },
    getSearch: function () {
        var match = this.location.href.replace(/#.*/, '').match(/\?.+/);
        return match ? match[0] : '';
    },
    getHash: function (window) {
        var match = (window || this).location.href.match(/#(.*)$/);
        return match ? match[1] : '';
    },
    getPath: function () {
        var path = this.decodeFragment(this.location.pathname + this.getSearch()).slice(this.root.length - 1);
        return path.charAt(0) === '/' ? path.slice(1) : path;
    },
    getFragment: function (fragment) {
        if (fragment == null) {
            if (this._usePushState || !this._wantsHashChange) {
                fragment = this.getPath();
            } else {
                fragment = this.getHash();
            }
        }
        return fragment.replace(routeStripper, '');
    },
    start: function (options) {
        if (History.started)
            throw new Error('Backbone.history has already been started');
        History.started = true;
        this.options = _.extend({
            root: '/'
        }, this.options, options);
        this.root = this.options.root;
        this._wantsHashChange = this.options.hashChange !== false;
        this._hasHashChange = 'onhashchange' in window && (document.documentMode === void 0 || document.documentMode > 7);
        this._useHashChange = this._wantsHashChange && this._hasHashChange;
        this._wantsPushState = !!this.options.pushState;
        this._hasPushState = !!(this.history && this.history.pushState);
        this._usePushState = this._wantsPushState && this._hasPushState;
        this.fragment = this.getFragment();
        this.root = ('/' + this.root + '/').replace(rootStripper, '/');
        if (this._wantsHashChange && this._wantsPushState) {
            if (!this._hasPushState && !this.atRoot()) {
                var rootPath = this.root.slice(0, -1) || '/';
                this.location.replace(rootPath + '#' + this.getPath());
                return true;
            } else if (this._hasPushState && this.atRoot()) {
                this.navigate(this.getHash(), {
                    replace: true
                });
            }
        }
        if (!this._hasHashChange && this._wantsHashChange && !this._usePushState) {
            this.iframe = document.createElement('iframe');
            this.iframe.src = 'javascript:0';
            this.iframe.style.display = 'none';
            this.iframe.tabIndex = -1;
            var body = document.body;
            var iWindow = body.insertBefore(this.iframe, body.firstChild).contentWindow;
            iWindow.document.open();
            iWindow.document.close();
            iWindow.location.hash = '#' + this.fragment;
        }
        var addEventListener = window.addEventListener || function (eventName, listener) {
            return attachEvent('on' + eventName, listener);
        };
        if (this._usePushState) {
            addEventListener('popstate', this.checkUrl, false);
        } else if (this._useHashChange && !this.iframe) {
            addEventListener('hashchange', this.checkUrl, false);
        } else if (this._wantsHashChange) {
            this._checkUrlInterval = setInterval(this.checkUrl, this.interval);
        }
        if (!this.options.silent)
            return this.loadUrl();
    },
    stop: function () {
        var removeEventListener = window.removeEventListener || function (eventName, listener) {
            return detachEvent('on' + eventName, listener);
        };
        if (this._usePushState) {
            removeEventListener('popstate', this.checkUrl, false);
        } else if (this._useHashChange && !this.iframe) {
            removeEventListener('hashchange', this.checkUrl, false);
        }
        if (this.iframe) {
            document.body.removeChild(this.iframe);
            this.iframe = null;
        }
        if (this._checkUrlInterval)
            clearInterval(this._checkUrlInterval);
        History.started = false;
    },
    route: function (route, callback) {
        this.handlers.unshift({
            route: route,
            callback: callback
        });
    },
    checkUrl: function (e) {
        var current = this.getFragment();
        if (current === this.fragment && this.iframe) {
            current = this.getHash(this.iframe.contentWindow);
        }
        if (current === this.fragment)
            return false;
        if (this.iframe)
            this.navigate(current);
        this.loadUrl();
    },
    loadUrl: function (fragment) {
        if (!this.matchRoot())
            return false;
        fragment = this.fragment = this.getFragment(fragment);
        return _.some(this.handlers, function (handler) {
            if (handler.route.test(fragment)) {
                handler.callback(fragment);
                return true;
            }
        });
    },
    navigate: function (fragment, options) {
        if (!History.started)
            return false;
        if (!options || options === true)
            options = {
            trigger: !!options
        };
        fragment = this.getFragment(fragment || '');
        var rootPath = this.root;
        if (fragment === '' || fragment.charAt(0) === '?') {
            rootPath = rootPath.slice(0, -1) || '/';
        }
        var url = rootPath + fragment;
        fragment = this.decodeFragment(fragment.replace(pathStripper, ''));
        if (this.fragment === fragment)
            return;
        this.fragment = fragment;
        if (this._usePushState) {
            this.history[options.replace ? 'replaceState' : 'pushState']({}, document.title, url);
        } else if (this._wantsHashChange) {
            this._updateHash(this.location, fragment, options.replace);
            if (this.iframe && fragment !== this.getHash(this.iframe.contentWindow)) {
                var iWindow = this.iframe.contentWindow;
                if (!options.replace) {
                    iWindow.document.open();
                    iWindow.document.close();
                }
                this._updateHash(iWindow.location, fragment, options.replace);
            }
        } else {
            return this.location.assign(url);
        }
        if (options.trigger)
            return this.loadUrl(fragment);
    },
    _updateHash: function (location, fragment, replace) {
        if (replace) {
            var href = location.href.replace(/(javascript:|#).*$/, '');
            location.replace(href + '#' + fragment);
        } else {
            location.hash = '#' + fragment;
        }
    }
});
Backbone.history = new History();
extend = function (protoProps, staticProps) {
    var parent = this;
    var child;
    if (protoProps && _.has(protoProps, 'constructor')) {
        child = protoProps.constructor;
    } else {
        child = function () {
            return parent.apply(this, arguments);
        };
    }
    _.extend(child, parent, staticProps);
    child.prototype = _.create(parent.prototype, protoProps);
    child.prototype.constructor = child;
    child.__super__ = parent.prototype;
    return child;
};
Model.extend = Collection.extend = Router.extend = View.extend = History.extend = extend;
urlError = function () {
    throw new Error('A "url" property or function must be specified');
};
wrapError = function (model, options) {
    var error = options.error;
    options.error = function (resp) {
        if (error)
            error.call(options.context, model, resp, options);
        model.trigger('error', model, resp, options);
    };
};
return Backbone;})), 0, true, $__.uid);