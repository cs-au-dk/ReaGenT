
var require_cache = {};
var random = Math.random;

function loadLibrary(path) {
    if (require_cache[path]) {
        return require_cache[path];
    }
    function nestedRequire() {
        throw new Error();
    }
    var utilFunction = TAJS_load(path, false, "exports", "module", "require");
    var exports = {};
    var module = {
        "require": nestedRequire,
        "exports": exports
    };
    utilFunction(exports, module, nestedRequire);
    require_cache[path] = module.exports;
    return module.exports;
}

var print = function () {};

var no_value = {noValueMarker: true};
var testOrderRecording = [];
function assert(cond, path, expected, actual, iteration, descrip) {
    if (!failOnAny && typeof actual === "object" && actual && actual._any) {
        return true;
    }
    if (path === "mockFunctionForFirstMatchPolicy") {
        return cond;
    }

    TAJS_record(path + " | " + expected, cond);
    TAJS_record(path + " | " + expected + " | value", actual);

    return cond;
}

TAJS_makeContextSensitive(assert, 0);
TAJS_makeContextSensitive(assert, 1);
TAJS_makeContextSensitive(assert, 2);

var error = function (message) {
    // Nothing currently.
};

for (var key in console) {
    console[key] = function () {};
}

function RuntimeError(message) {
    error("RuntimeError: " + message);
    message = " " + message;
    this.message = message;
    Error.call(this, message);
}
RuntimeError.prototype = Object.create(Error.prototype);

// The below is library code, that enables me to only run the tests, that are actually able to run.

function registerTest (index, requirements) {
    // Do nothing in TAJS.
}
function registerValue(valueIndex) {
    return; // With TAJS this is GREATLY simplified.
}

function testCalled(number) {
    // Do nothing in this static analysis.
}

var i = 0;
function selectTest() {
    return TAJS_make("AnyNum")
}

// Utility functions.

function extend(result) {
    var changedBase = false;

    if (arguments.length === 1) {
        throw new RuntimeError("IntersectionType: nothing to intersect")
    }

    // A pre-check, to see if we are trying to construct the same primitive multiple times. In principle unsound, but it only happens (that i know of) when we have recursively defined intersection types, where there in reality is only one primitive, it is just duplicated.
    var typesOfs = {};
    for (var i = 1; i < arguments.length; i++) {
        var type = typeof arguments[i];
        var prevValue = typesOfs[type];
        typesOfs[type] = prevValue ? prevValue + 1 : 1;
    }
    if (Object.keys(typesOfs).length === 1 && !typesOfs.object && !typesOfs.function) {
        return arguments[1]; // <- Just returning the first of them, since they are kinda equal.
    }
    if (Object.keys(typesOfs).length > 1) {
        if (!(Object.keys(typesOfs).length === 2 && typesOfs.object && typesOfs.function)) {
            throw new RuntimeError("IntersectionType of primitives, will not do this.");
        }
    }

    for (var i = 1; i < arguments.length; i++) {
        var obj = arguments[i];
        if (obj.__proto__.constructor !== Object) {
            if (changedBase) {
                throw new RuntimeError("Cannot construct this IntersectionType")
            }
            changedBase = true;
            result = obj;
        }
    }


    for (var i = 1; i < arguments.length; i++) {
        var obj = arguments[i];
        if (obj !== result) {
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    result[key] = obj[key];
                }
            }
        }
    }
    return result;
}

function arrayIndexCheck (obj, check) {
    var keys = getAllKeys(obj).filter(function (e) {
        return Number(e) + "" === e;
    });

    for (var i = 0; i < keys.length; i++) {
        if (!(check(obj[keys[i]]))) {
            return false;
        }
    }

    return true;
}

function numberIndexCheck(obj, check) {
    for (var key in obj) {
        //noinspection JSUnfilteredForInLoop (It is supposed to be that way, only the object-prototype is excluded).
        if (Number(key) + "" === key && !check(obj[key])) {
            return false;
        }
    }
    return true;
}

function getAllKeys(obj) {
    var result = [];
    for (var key in obj) {
        result.push(key);
    }
    return result;
}

function stringIndexCheck(obj, check) {
    for (var key in obj) {
        //noinspection JSUnfilteredForInLoop (It is supposed to be that way, the object-prototype is exluded because it's properties are non-enumerable).
        if (!check(obj[key])) {
            return false;
        }
    }
    return true;
}

function checkRestArgs(args, fromIndex, check) {
    for (var i = fromIndex; i < args.length; i++) {
        if (!check(args[i])) {
            return false;
        }
    }

    return true;
}