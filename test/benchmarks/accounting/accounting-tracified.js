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
(((($__.fs.J$__v325490696_34_3 = function J$__v325490696_34(root, undefined) {
var vvv_return, vvv_switch, lib, nativeMap, nativeIsArray, toString, isString, isArray, isObject, defaults, map, checkPrecision, checkCurrencyFormat, unformat, toFixed, formatNumber, formatMoney;
isString = function isString(obj) {
    return !!(obj === '' || obj && obj.charCodeAt && obj.substr);
};
isArray = function isArray(obj) {
    return nativeIsArray ? nativeIsArray(obj) : toString.call(obj) === '[object Array]';
};
isObject = function isObject(obj) {
    return toString.call(obj) === '[object Object]';
};
defaults = function defaults(object, defs) {
    var key;
    object = object || {};
    defs = defs || {};
    for (key in defs) {
        if (defs.hasOwnProperty(key)) {
            if (object[key] == null)
                object[key] = defs[key];
        }
    }
    return object;
};
map = function map(obj, iterator, context) {
    var results = [], i, j;
    if (!obj)
        return results;
    if (nativeMap && obj.map === nativeMap)
        return obj.map(iterator, context);
    for (i = 0, j = obj.length; i < j; i++) {
        results[i] = iterator.call(context, obj[i], i, obj);
    }
    return results;
};
checkPrecision = function checkPrecision(val, base) {
    val = Math.round(Math.abs(val));
    return isNaN(val) ? base : val;
};
checkCurrencyFormat = function checkCurrencyFormat(format) {
    var defaults = lib.settings.currency.format;
    if (typeof format === 'function')
        format = format();
    if (isString(format) && format.match('%v')) {
        return {
            pos: format,
            neg: format.replace('-', '').replace('%v', '-%v'),
            zero: format
        };
    } else if (!format || !format.pos || !format.pos.match('%v')) {
        return !isString(defaults) ? defaults : lib.settings.currency.format = {
            pos: defaults,
            neg: defaults.replace('%v', '-%v'),
            zero: defaults
        };
    }
    return format;
};
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
root = arguments[0], undefined = arguments[1];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    isString = ($__.fs.isString_4 = function isString(obj) {
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
return !!(obj === '' || obj && obj.charCodeAt && obj.substr);});
    isArray = ($__.fs.isArray_5 = function isArray(obj) {
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
return nativeIsArray ? nativeIsArray(obj) : toString.call(obj) === '[object Array]';});
    isObject = ($__.fs.isObject_6 = function isObject(obj) {
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
return toString.call(obj) === '[object Object]';});
    defaults = ($__.fs.defaults_7 = function defaults(object, defs) {
var vvv_return, vvv_switch, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
object = arguments[0], defs = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
object = object || {};
defs = defs || {};
for (key in defs) {
    if (defs.hasOwnProperty(key)) {
        if (object[key] == null)
            object[key] = defs[key];
    }
}
return object;});
    map = ($__.fs.map_8 = function map(obj, iterator, context) {
var vvv_return, vvv_switch, results, i, j;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iterator = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
results = [];
if (!obj)
    return results;
if (nativeMap && obj.map === nativeMap)
    return obj.map(iterator, context);
for (i = 0, j = obj.length; i < j; i++) {
    results[i] = iterator.call(context, obj[i], i, obj);
}
return results;});
    checkPrecision = ($__.fs.checkPrecision_9 = function checkPrecision(val, base) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
val = arguments[0], base = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
val = Math.round(Math.abs(val));
return isNaN(val) ? base : val;});
    checkCurrencyFormat = ($__.fs.checkCurrencyFormat_10 = function checkCurrencyFormat(format) {
var vvv_return, vvv_switch, defaults;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
format = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
defaults = lib.settings.currency.format;
if (typeof format === 'function')
    format = format();
if (isString(format) && format.match('%v')) {
    return {
        pos: format,
        neg: format.replace('-', '').replace('%v', '-%v'),
        zero: format
    };
} else if (!format || !format.pos || !format.pos.match('%v')) {
    return !isString(defaults) ? defaults : lib.settings.currency.format = {
        pos: defaults,
        neg: defaults.replace('%v', '-%v'),
        zero: defaults
    };
}
return format;});
    lib = $__.os.oid0 = {};
    (lib, $__.os.oid0).version = '0.3.2';
    (lib, $__.os.oid0).settings = $__.os.oid3 = {
        'currency': $__.os.oid1 = {
            'symbol': '$',
            'format': '%s%v',
            'decimal': '.',
            'thousand': ',',
            'precision': 2,
            'grouping': 3
        },
        'number': $__.os.oid2 = {
            'precision': 0,
            'grouping': 3,
            'thousand': ',',
            'decimal': '.'
        }
    };
    nativeMap = TAJS_restrictToType(TAJS_restrictToType(Array.prototype, 'object').map, 'function');
    nativeIsArray = TAJS_restrictToType(Array.isArray, 'function');
    toString = TAJS_restrictToType(TAJS_restrictToType(Object.prototype, 'object').toString, 'function');
    unformat = (lib, $__.os.oid0).unformat = (lib, $__.os.oid0).parse = ($__.fs.J$__v325490696_10_11 = function J$__v325490696_10(value, decimal) {
var vvv_return, vvv_switch, regex, unformatted;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0], decimal = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (isArray(value)) {
    return map(value, function (val) {
        return unformat(val, decimal);
    });
}
value = value || 0;
if (typeof value === 'number')
    return value;
decimal = decimal || '.';
regex = new RegExp('[^0-9-' + decimal + ']', ['g']);
unformatted = parseFloat(('' + value).replace(/\((.*)\)/, '-$1').replace(regex, '').replace(decimal, '.'));
return !isNaN(unformatted) ? unformatted : 0;});
    toFixed = (lib, $__.os.oid0).toFixed = ($__.fs.J$__v325490696_12_12 = function J$__v325490696_12(value, precision) {
var vvv_return, vvv_switch, power;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0], precision = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
precision = checkPrecision(precision, lib.settings.number.precision);
power = Math.pow(10, precision);
return (Math.round(lib.unformat(value) * power) / power).toFixed(precision);});
    formatNumber = (lib, $__.os.oid0).formatNumber = ($__.fs.J$__v325490696_16_13 = function J$__v325490696_16(number, precision, thousand, decimal) {
var vvv_return, vvv_switch, opts, usePrecision, negative, base, mod;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
number = arguments[0], precision = arguments[1], thousand = arguments[2], decimal = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (isArray(number)) {
    return map(number, function (val) {
        return formatNumber(val, precision, thousand, decimal);
    });
}
number = unformat(number);
opts = defaults(isObject(precision) ? precision : {
    precision: precision,
    thousand: thousand,
    decimal: decimal
}, lib.settings.number);
usePrecision = checkPrecision(opts.precision);
negative = number < 0 ? '-' : '';
base = parseInt(toFixed(Math.abs(number || 0), usePrecision), 10) + '';
mod = base.length > 3 ? base.length % 3 : 0;
return negative + (mod ? base.substr(0, mod) + opts.thousand : '') + base.substr(mod).replace(/(\d{3})(?=\d)/g, '$1' + opts.thousand) + (usePrecision ? opts.decimal + toFixed(Math.abs(number), usePrecision).split('.')[1] : '');});
    formatMoney = (lib, $__.os.oid0).formatMoney = ($__.fs.J$__v325490696_20_14 = function J$__v325490696_20(number, symbol, precision, thousand, decimal, format) {
var vvv_return, vvv_switch, opts, formats, useFormat;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
number = arguments[0], symbol = arguments[1], precision = arguments[2], thousand = arguments[3], decimal = arguments[4], format = arguments[5];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (isArray(number)) {
    return map(number, function (val) {
        return formatMoney(val, symbol, precision, thousand, decimal, format);
    });
}
number = unformat(number);
opts = defaults(isObject(symbol) ? symbol : {
    symbol: symbol,
    precision: precision,
    thousand: thousand,
    decimal: decimal,
    format: format
}, lib.settings.currency);
formats = checkCurrencyFormat(opts.format);
useFormat = number > 0 ? formats.pos : number < 0 ? formats.neg : formats.zero;
return useFormat.replace('%s', opts.symbol).replace('%v', formatNumber(Math.abs(number), checkPrecision(opts.precision), opts.thousand, opts.decimal));});
    (lib, $__.os.oid0).formatColumn = ($__.fs.J$__v325490696_26_15 = function J$__v325490696_26(list, symbol, precision, thousand, decimal, format) {
var vvv_return, vvv_switch, opts, formats, padAfterSymbol, maxLength, formatted;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
list = arguments[0], symbol = arguments[1], precision = arguments[2], thousand = arguments[3], decimal = arguments[4], format = arguments[5];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!list)
    return [];
opts = defaults(isObject(symbol) ? symbol : {
    symbol: symbol,
    precision: precision,
    thousand: thousand,
    decimal: decimal,
    format: format
}, lib.settings.currency);
formats = checkCurrencyFormat(opts.format);
padAfterSymbol = formats.pos.indexOf('%s') < formats.pos.indexOf('%v') ? true : false;
maxLength = 0;
formatted = map(list, function (val, i) {
    if (isArray(val)) {
        return lib.formatColumn(val, opts);
    } else {
        val = unformat(val);
        var useFormat = val > 0 ? formats.pos : val < 0 ? formats.neg : formats.zero, fVal = useFormat.replace('%s', opts.symbol).replace('%v', formatNumber(Math.abs(val), checkPrecision(opts.precision), opts.thousand, opts.decimal));
        if (fVal.length > maxLength)
            maxLength = fVal.length;
        return fVal;
    }
});
return map(formatted, function (val, i) {
    if (isString(val) && val.length < maxLength) {
        return padAfterSymbol ? val.replace(opts.symbol, opts.symbol + new Array(maxLength - val.length + 1).join(' ')) : new Array(maxLength - val.length + 1).join(' ') + val;
    }
    return val;
});});
    typeof exports !== 'undefined';
    typeof define === 'function';
    (lib, $__.os.oid0).noConflict = (($__.fs.J$__v325490696_32_16 = function J$__v325490696_32(oldAccounting) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
oldAccounting = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v325490696_30_17 = function J$__v325490696_30() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
root.accounting = oldAccounting;
lib.noConflict = undefined;
return lib;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    root.accounting = oldAccounting;
    lib.noConflict = undefined;
    return lib;
};}), $__.fs.J$__v325490696_32_16)(TAJS_restrictToType(root.accounting, 'undefined'), 0, true, $__.uid);
    root['accounting', 'accounting'] = lib;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
lib = {};
lib.version = '0.3.2';
lib.settings = {
    currency: {
        symbol: '$',
        format: '%s%v',
        decimal: '.',
        thousand: ',',
        precision: 2,
        grouping: 3
    },
    number: {
        precision: 0,
        grouping: 3,
        thousand: ',',
        decimal: '.'
    }
};
nativeMap = Array.prototype.map;
nativeIsArray = Array.isArray;
toString = Object.prototype.toString;
unformat = lib.unformat = lib.parse = function (value, decimal) {
    if (isArray(value)) {
        return map(value, function (val) {
            return unformat(val, decimal);
        });
    }
    value = value || 0;
    if (typeof value === 'number')
        return value;
    decimal = decimal || '.';
    var regex = new RegExp('[^0-9-' + decimal + ']', ['g']), unformatted = parseFloat(('' + value).replace(/\((.*)\)/, '-$1').replace(regex, '').replace(decimal, '.'));
    return !isNaN(unformatted) ? unformatted : 0;
};
toFixed = lib.toFixed = function (value, precision) {
    precision = checkPrecision(precision, lib.settings.number.precision);
    var power = Math.pow(10, precision);
    return (Math.round(lib.unformat(value) * power) / power).toFixed(precision);
};
formatNumber = lib.formatNumber = function (number, precision, thousand, decimal) {
    if (isArray(number)) {
        return map(number, function (val) {
            return formatNumber(val, precision, thousand, decimal);
        });
    }
    number = unformat(number);
    var opts = defaults(isObject(precision) ? precision : {
            precision: precision,
            thousand: thousand,
            decimal: decimal
        }, lib.settings.number), usePrecision = checkPrecision(opts.precision), negative = number < 0 ? '-' : '', base = parseInt(toFixed(Math.abs(number || 0), usePrecision), 10) + '', mod = base.length > 3 ? base.length % 3 : 0;
    return negative + (mod ? base.substr(0, mod) + opts.thousand : '') + base.substr(mod).replace(/(\d{3})(?=\d)/g, '$1' + opts.thousand) + (usePrecision ? opts.decimal + toFixed(Math.abs(number), usePrecision).split('.')[1] : '');
};
formatMoney = lib.formatMoney = function (number, symbol, precision, thousand, decimal, format) {
    if (isArray(number)) {
        return map(number, function (val) {
            return formatMoney(val, symbol, precision, thousand, decimal, format);
        });
    }
    number = unformat(number);
    var opts = defaults(isObject(symbol) ? symbol : {
            symbol: symbol,
            precision: precision,
            thousand: thousand,
            decimal: decimal,
            format: format
        }, lib.settings.currency), formats = checkCurrencyFormat(opts.format), useFormat = number > 0 ? formats.pos : number < 0 ? formats.neg : formats.zero;
    return useFormat.replace('%s', opts.symbol).replace('%v', formatNumber(Math.abs(number), checkPrecision(opts.precision), opts.thousand, opts.decimal));
};
lib.formatColumn = function (list, symbol, precision, thousand, decimal, format) {
    if (!list)
        return [];
    var opts = defaults(isObject(symbol) ? symbol : {
            symbol: symbol,
            precision: precision,
            thousand: thousand,
            decimal: decimal,
            format: format
        }, lib.settings.currency), formats = checkCurrencyFormat(opts.format), padAfterSymbol = formats.pos.indexOf('%s') < formats.pos.indexOf('%v') ? true : false, maxLength = 0, formatted = map(list, function (val, i) {
            if (isArray(val)) {
                return lib.formatColumn(val, opts);
            } else {
                val = unformat(val);
                var useFormat = val > 0 ? formats.pos : val < 0 ? formats.neg : formats.zero, fVal = useFormat.replace('%s', opts.symbol).replace('%v', formatNumber(Math.abs(val), checkPrecision(opts.precision), opts.thousand, opts.decimal));
                if (fVal.length > maxLength)
                    maxLength = fVal.length;
                return fVal;
            }
        });
    return map(formatted, function (val, i) {
        if (isString(val) && val.length < maxLength) {
            return padAfterSymbol ? val.replace(opts.symbol, opts.symbol + new Array(maxLength - val.length + 1).join(' ')) : new Array(maxLength - val.length + 1).join(' ') + val;
        }
        return val;
    });
};
if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
        exports = module.exports = lib;
    }
    exports.accounting = lib;
} else if (typeof define === 'function' && define.amd) {
    define([], function () {
        return lib;
    });
} else {
    lib.noConflict = function (oldAccounting) {
        return function () {
            root.accounting = oldAccounting;
            lib.noConflict = undefined;
            return lib;
        };
    }(root.accounting);
    root['accounting'] = lib;
}})), $__.fs.J$__v325490696_34_3))(this, 0, true, $__.uid);