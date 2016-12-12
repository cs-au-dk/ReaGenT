// SeedRandom, from: http://cdnjs.cloudflare.com/ajax/libs/seedrandom/2.3.10/seedrandom.min.js
!function(a,b,c,d,e,f,g,h,i){function j(a){var b,c=a.length,e=this,f=0,g=e.i=e.j=0,h=e.S=[];for(c||(a=[c++]);d>f;)h[f]=f++;for(f=0;d>f;f++)h[f]=h[g=s&g+a[f%c]+(b=h[f])],h[g]=b;(e.g=function(a){for(var b,c=0,f=e.i,g=e.j,h=e.S;a--;)b=h[f=s&f+1],c=c*d+h[s&(h[f]=h[g=s&g+b])+(h[g]=b)];return e.i=f,e.j=g,c})(d)}function k(a,b){var c,d=[],e=typeof a;if(b&&"object"==e)for(c in a)try{d.push(k(a[c],b-1))}catch(f){}return d.length?d:"string"==e?a:a+"\0"}function l(a,b){for(var c,d=a+"",e=0;e<d.length;)b[s&e]=s&(c^=19*b[s&e])+d.charCodeAt(e++);return n(b)}function m(c){try{return o?n(o.randomBytes(d)):(a.crypto.getRandomValues(c=new Uint8Array(d)),n(c))}catch(e){return[+new Date,a,(c=a.navigator)&&c.plugins,a.screen,n(b)]}}function n(a){return String.fromCharCode.apply(0,a)}var o,p=c.pow(d,e),q=c.pow(2,f),r=2*q,s=d-1,t=c["seed"+i]=function(a,f,g){var h=[];f=1==f?{entropy:!0}:f||{};var o=l(k(f.entropy?[a,n(b)]:null==a?m():a,3),h),s=new j(h);return l(n(s.S),b),(f.pass||g||function(a,b,d){return d?(c[i]=a,b):a})(function(){for(var a=s.g(e),b=p,c=0;q>a;)a=(a+c)*d,b*=d,c=s.g(1);for(;a>=r;)a/=2,b/=2,c>>>=1;return(a+c)/b},o,"global"in f?f.global:this==c)};if(l(c[i](),b),g&&g.exports){g.exports=t;try{o=require("crypto")}catch(u){}}else h&&h.amd&&h(function(){return t})}(this,[],Math,256,6,52,"object"==typeof module&&module,"function"==typeof define&&define,"random");
// require("/mnt/c/Users/erik1/Dropbox/Uni/Ph.D/TAJSCheck/node_modules/seedrandom/seedrandom.js");

// var random = eval("(Math.seedrandom(initialRandomness + ''), Math.random)");
Math.seedrandom(initialRandomness + '');
var random = Math.random;

console.log("Initial random: " + JSON.stringify(initialRandomness));

var require_cache = {};

function loadLibrary(path) {
    try {
        var foo = TAJS_load(path, false, "exports", "module", "require");
    } catch (e) {
        return require(path);
    }

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
    return module.exports;
}

var assertionFailures = [];
var no_value = {};
var testOrderRecording = [];
function assert(cond, path, expected, actual) {
    if (!cond) {
        assertionFailures.push({
            path: path,
            expected: expected,
            actual: actual,
            sequence: testOrderRecording.slice()
        });
    }
    return cond
}
try {
    TAJS_makeContextSensitive(assert, 0);
} catch (e) {
    // Ignored.
}

var printedWarnings = [];
var printedErrors = [];

var print = console.log;
var warn = function (message) {
    printedWarnings.push(message);
};
var error = function (message) {
    printedErrors.push(message);
};
try {
    process.on('uncaughtException', function(err) {
        error((err && err.stack) ? err.stack : err);
    });
} catch(e) {
    // ignored.
}
for (var key in console) {
    console[key] = function () {};
}

function RuntimeError(message) {
    this.message = message;
    Error.call(this, message);
}
RuntimeError.prototype = Object.create(Error.prototype);

// Utility functions.

function arrayIndexCheck(obj, check) {
    if (typeof obj.length !== "number" || obj.length < 0) {
        return false;
    }
    for (var i = 0; i < obj.length; i++) {
        if (!check(obj[i])) {
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

// The below is library code, that enables me to only run the tests, that are actually able to run.

var testsThatCanRun = []; // list of test-indexes
var testsWithUnmetDependencies = {}; // value-index -> {testIndex: number, requirements: []}[]

/**
 *
 * @param index the test index.
 * @param requirements a list of requirements, each requirement is a list of possible value-indexes that satisfy that requirement.
 */
function registerTest (index, requirements) {
    if (requirements.length == 0) {
        testsThatCanRun.push(index);
    } else {
        var registration = {
            "testIndex": index,
            "requirements": requirements
        };
        for (var i = 0; i < requirements.length; i++) {
            var requirementList = requirements[i];
            for (var j = 0; j < requirementList.length; j++) {
                var requirement = requirementList[j];
                if (!testsWithUnmetDependencies[requirement]) {
                    testsWithUnmetDependencies[requirement] = [];
                }
                testsWithUnmetDependencies[requirement].push(registration);
            }
        }
    }
}
function registerValue(valueIndex) {
    var testList = testsWithUnmetDependencies[valueIndex];
    if (!testList) {
        return;
    }
    for (var i = 0; i < testList.length; i++) {
        var test = testList[i];
        test.requirements = test.requirements.filter(function (valueIndexes) {
            return valueIndexes.indexOf(valueIndex) === -1;
        });
    }
    testsWithUnmetDependencies[valueIndex] = testList.filter(function (test) {
        var isEmpty = test.requirements.length == 0;
        if (isEmpty) {
            var testIndex = test.testIndex | 0;
            if (testsThatCanRun.indexOf(testIndex) === -1) {
                testsThatCanRun.push(testIndex);
            }
        }
        return !isEmpty;
    });
}