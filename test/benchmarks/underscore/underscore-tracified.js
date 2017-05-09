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
var vvv_tmp0;
(vvv_tmp0 = this, ((($__.fs.J$__v3189643886_328_3 = function J$__v3189643886_328() {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1, vvv_tmp2, vvv_tmp3, vvv_tmp4, vvv_tmp5, vvv_tmp6, vvv_tmp7, root, previousUnderscore, ArrayProto, ObjProto, FuncProto, push, slice, toString, hasOwnProperty, nativeIsArray, nativeKeys, nativeBind, nativeCreate, Ctor, _, optimizeCb, cb, createAssigner, baseCreate, property, MAX_ARRAY_INDEX, getLength, isArrayLike, createReduce, group, flatten, createPredicateIndexFinder, createIndexFinder, executeBound, hasEnumBug, nonEnumerableProps, collectNonEnumProps, eq, escapeMap, unescapeMap, createEscaper, idCounter, noMatch, escapes, escaper, escapeChar, result;
createReduce = function createReduce(dir) {
    function iterator(obj, iteratee, memo, keys, index, length) {
        for (; index >= 0 && index < length; index += dir) {
            var currentKey = keys ? keys[index] : index;
            memo = iteratee(memo, obj[currentKey], currentKey, obj);
        }
        return memo;
    }
    return function (obj, iteratee, memo, context) {
        iteratee = optimizeCb(iteratee, context, 4);
        var keys = !isArrayLike(obj) && _.keys(obj), length = (keys || obj).length, index = dir > 0 ? 0 : length - 1;
        if (arguments.length < 3) {
            memo = obj[keys ? keys[index] : index];
            index += dir;
        }
        return iterator(obj, iteratee, memo, keys, index, length);
    };
};
createPredicateIndexFinder = function createPredicateIndexFinder(dir) {
    return function (array, predicate, context) {
        predicate = cb(predicate, context);
        var length = getLength(array);
        var index = dir > 0 ? 0 : length - 1;
        for (; index >= 0 && index < length; index += dir) {
            if (predicate(array[index], index, array))
                return index;
        }
        return -1;
    };
};
createIndexFinder = function createIndexFinder(dir, predicateFind, sortedIndex) {
    return function (array, item, idx) {
        var i = 0, length = getLength(array);
        if (typeof idx == 'number') {
            if (dir > 0) {
                i = idx >= 0 ? idx : Math.max(idx + length, i);
            } else {
                length = idx >= 0 ? Math.min(idx + 1, length) : idx + length + 1;
            }
        } else if (sortedIndex && idx && length) {
            idx = sortedIndex(array, item);
            return array[idx] === item ? idx : -1;
        }
        if (item !== item) {
            idx = predicateFind(slice.call(array, i, length), _.isNaN);
            return idx >= 0 ? idx + i : -1;
        }
        for (idx = dir > 0 ? i : length - 1; idx >= 0 && idx < length; idx += dir) {
            if (array[idx] === item)
                return idx;
        }
        return -1;
    };
};
collectNonEnumProps = function collectNonEnumProps(obj, keys) {
    var nonEnumIdx = nonEnumerableProps.length;
    var constructor = obj.constructor;
    var proto = _.isFunction(constructor) && constructor.prototype || ObjProto;
    var prop = 'constructor';
    if (_.has(obj, prop) && !_.contains(keys, prop))
        keys.push(prop);
    while (nonEnumIdx--) {
        prop = nonEnumerableProps[nonEnumIdx];
        if (prop in obj && obj[prop] !== proto[prop] && !_.contains(keys, prop)) {
            keys.push(prop);
        }
    }
};
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
    createReduce = ($__.fs.createReduce_4 = function createReduce(dir) {
var vvv_return, vvv_switch, iterator;
iterator = function iterator(obj, iteratee, memo, keys, index, length) {
    for (; index >= 0 && index < length; index += dir) {
        var currentKey = keys ? keys[index] : index;
        memo = iteratee(memo, obj[currentKey], currentKey, obj);
    }
    return memo;
};
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
dir = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    iterator = ($__.fs.iterator_20 = function iterator(obj, iteratee, memo, keys, index, length) {
var vvv_return, vvv_switch, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], memo = arguments[2], keys = arguments[3], index = arguments[4], length = arguments[5];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
for (; index >= 0 && index < length; index += dir) {
    currentKey = keys ? keys[index] : index;
    memo = iteratee(memo, obj[currentKey], currentKey, obj);
}
return memo;});
    return ($__.fs.J$__v3189643886_38_21 = function J$__v3189643886_38(obj, iteratee, memo, context) {
var vvv_return, vvv_switch, keys, length, index;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], memo = arguments[2], context = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = optimizeCb(iteratee, context, 4);
keys = !isArrayLike(obj) && _.keys(obj);
length = (keys || obj).length;
index = dir > 0 ? 0 : length - 1;
if (arguments.length < 3) {
    memo = obj[keys ? keys[index] : index];
    index += dir;
}
return iterator(obj, iteratee, memo, keys, index, length);});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    iterator = ($__.fs.iterator_22 = function iterator(obj, iteratee, memo, keys, index, length) {
var vvv_return, vvv_switch, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], memo = arguments[2], keys = arguments[3], index = arguments[4], length = arguments[5];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
for (; index >= 0 && index < length; index += dir) {
    currentKey = keys ? keys[index] : index;
    memo = iteratee(memo, obj[currentKey], currentKey, obj);
}
return memo;});
    return ($__.fs.J$__v3189643886_38_23 = function J$__v3189643886_38(obj, iteratee, memo, context) {
var vvv_return, vvv_switch, keys, length, index;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], memo = arguments[2], context = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = optimizeCb(iteratee, context, 4);
keys = !isArrayLike(obj) && _.keys(obj);
length = (keys || obj).length;
index = dir > 0 ? 0 : length - 1;
if (arguments.length < 3) {
    memo = obj[keys ? keys[index] : index];
    index += dir;
}
return iterator(obj, iteratee, memo, keys, index, length);});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (obj, iteratee, memo, context) {
    iteratee = optimizeCb(iteratee, context, 4);
    var keys = !isArrayLike(obj) && _.keys(obj), length = (keys || obj).length, index = dir > 0 ? 0 : length - 1;
    if (arguments.length < 3) {
        memo = obj[keys ? keys[index] : index];
        index += dir;
    }
    return iterator(obj, iteratee, memo, keys, index, length);
};});
    createPredicateIndexFinder = ($__.fs.createPredicateIndexFinder_5 = function createPredicateIndexFinder(dir) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
dir = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v3189643886_135_64 = function J$__v3189643886_135(array, predicate, context) {
var vvv_return, vvv_switch, length, index;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
length = getLength(array);
index = dir > 0 ? 0 : length - 1;
for (; index >= 0 && index < length; index += dir) {
    if (predicate(array[index], index, array))
        return index;
}
return -1;});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    return ($__.fs.J$__v3189643886_135_65 = function J$__v3189643886_135(array, predicate, context) {
var vvv_return, vvv_switch, length, index;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
length = getLength(array);
index = dir > 0 ? 0 : length - 1;
for (; index >= 0 && index < length; index += dir) {
    if (predicate(array[index], index, array))
        return index;
}
return -1;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (array, predicate, context) {
    predicate = cb(predicate, context);
    var length = getLength(array);
    var index = dir > 0 ? 0 : length - 1;
    for (; index >= 0 && index < length; index += dir) {
        if (predicate(array[index], index, array))
            return index;
    }
    return -1;
};});
    createIndexFinder = ($__.fs.createIndexFinder_6 = function createIndexFinder(dir, predicateFind, sortedIndex) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
dir = arguments[0], predicateFind = arguments[1], sortedIndex = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v3189643886_140_67 = function J$__v3189643886_140(array, item, idx) {
var vvv_return, vvv_switch, i, length;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], item = arguments[1], idx = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
i = 0;
length = getLength(array);
if (typeof idx == 'number') {
    if (dir > 0) {
        i = idx >= 0 ? idx : Math.max(idx + length, i);
    } else {
        length = idx >= 0 ? Math.min(idx + 1, length) : idx + length + 1;
    }
} else if (sortedIndex && idx && length) {
    idx = sortedIndex(array, item);
    return array[idx] === item ? idx : -1;
}
if (item !== item) {
    idx = predicateFind(slice.call(array, i, length), _.isNaN);
    return idx >= 0 ? idx + i : -1;
}
for (idx = dir > 0 ? i : length - 1; idx >= 0 && idx < length; idx += dir) {
    if (array[idx] === item)
        return idx;
}
return -1;});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    return ($__.fs.J$__v3189643886_140_68 = function J$__v3189643886_140(array, item, idx) {
var vvv_return, vvv_switch, i, length;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], item = arguments[1], idx = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
i = 0;
length = getLength(array);
if (typeof idx == 'number') {
    if (dir > 0) {
        i = idx >= 0 ? idx : Math.max(idx + length, i);
    } else {
        length = idx >= 0 ? Math.min(idx + 1, length) : idx + length + 1;
    }
} else if (sortedIndex && idx && length) {
    idx = sortedIndex(array, item);
    return array[idx] === item ? idx : -1;
}
if (item !== item) {
    idx = predicateFind(slice.call(array, i, length), _.isNaN);
    return idx >= 0 ? idx + i : -1;
}
for (idx = dir > 0 ? i : length - 1; idx >= 0 && idx < length; idx += dir) {
    if (array[idx] === item)
        return idx;
}
return -1;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (array, item, idx) {
    var i = 0, length = getLength(array);
    if (typeof idx == 'number') {
        if (dir > 0) {
            i = idx >= 0 ? idx : Math.max(idx + length, i);
        } else {
            length = idx >= 0 ? Math.min(idx + 1, length) : idx + length + 1;
        }
    } else if (sortedIndex && idx && length) {
        idx = sortedIndex(array, item);
        return array[idx] === item ? idx : -1;
    }
    if (item !== item) {
        idx = predicateFind(slice.call(array, i, length), _.isNaN);
        return idx >= 0 ? idx + i : -1;
    }
    for (idx = dir > 0 ? i : length - 1; idx >= 0 && idx < length; idx += dir) {
        if (array[idx] === item)
            return idx;
    }
    return -1;
};});
    collectNonEnumProps = ($__.fs.collectNonEnumProps_7 = function collectNonEnumProps(obj, keys) {
var vvv_return, vvv_switch, nonEnumIdx, constructor, proto, prop;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], keys = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
nonEnumIdx = nonEnumerableProps.length;
constructor = obj.constructor;
proto = _.isFunction(constructor) && constructor.prototype || ObjProto;
prop = 'constructor';
if (_.has(obj, prop) && !_.contains(keys, prop))
    keys.push(prop);
while (nonEnumIdx--) {
    prop = nonEnumerableProps[nonEnumIdx];
    if (prop in obj && obj[prop] !== proto[prop] && !_.contains(keys, prop)) {
        keys.push(prop);
    }
}});
    root = this;
    previousUnderscore = TAJS_restrictToType(root._, 'undefined');
    ArrayProto = TAJS_restrictToType(Array.prototype, 'object');
    ObjProto = TAJS_restrictToType(Object.prototype, 'object');
    FuncProto = TAJS_restrictToType(Function.prototype, 'function');
    push = TAJS_restrictToType(ArrayProto.push, 'function');
    slice = TAJS_restrictToType(ArrayProto.slice, 'function');
    toString = TAJS_restrictToType(ObjProto.toString, 'function');
    hasOwnProperty = TAJS_restrictToType(ObjProto.hasOwnProperty, 'function');
    nativeIsArray = TAJS_restrictToType(Array.isArray, 'function');
    nativeKeys = TAJS_restrictToType(Object.keys, 'function');
    nativeBind = TAJS_restrictToType(FuncProto.bind, 'function');
    nativeCreate = TAJS_restrictToType(Object.create, 'function');
    Ctor = ($__.fs.J$__v3189643886_1_8 = function J$__v3189643886_1() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');});
    _ = ($__.fs.J$__v3189643886_3_9 = function J$__v3189643886_3(obj) {
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
if (obj instanceof _)
    return obj;
if (!(this instanceof _))
    return new _(obj);
this._wrapped = obj;});
    typeof exports !== 'undefined';
    root._ = _;
    _.VERSION = '1.8.3';
    optimizeCb = ($__.fs.J$__v3189643886_15_10 = function J$__v3189643886_15(func, context, argCount) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], context = arguments[1], argCount = arguments[2];
switch (vvv_switch) {
case 0:
case 1:
case 2:
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 3);
    context === void 0;
    return func;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (context === void 0)
    return func;
switch (argCount == null ? 3 : argCount) {
case 1:
    return function (value) {
        return func.call(context, value);
    };
case 2:
    return function (value, other) {
        return func.call(context, value, other);
    };
case 3:
    return function (value, index, collection) {
        return func.call(context, value, index, collection);
    };
case 4:
    return function (accumulator, value, index, collection) {
        return func.call(context, accumulator, value, index, collection);
    };
}
return function () {
    return func.apply(context, arguments);
};});
    cb = ($__.fs.J$__v3189643886_17_11 = function J$__v3189643886_17(value, context, argCount) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0], context = arguments[1], argCount = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (value == null)
    return _.identity;
if (_.isFunction(value))
    return optimizeCb(value, context, argCount);
if (_.isObject(value))
    return _.matcher(value);
return _.property(value);});
    _.iteratee = ($__.fs.J$__v3189643886_19_12 = function J$__v3189643886_19(value, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return cb(value, context, Infinity);});
    createAssigner = ($__.fs.J$__v3189643886_23_13 = function J$__v3189643886_23(keysFunc, undefinedOnly) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
keysFunc = arguments[0], undefinedOnly = arguments[1];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v3189643886_21_92 = function J$__v3189643886_21(obj) {
var vvv_return, vvv_switch, length, source, keys, l, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = arguments.length;
if (length < 2 || obj == null)
    return obj;
for (var index = 1; index < length; index++) {
    source = arguments[index];
    keys = keysFunc(source);
    l = keys.length;
    for (var i = 0; i < l; i++) {
        key = keys[i];
        if (!undefinedOnly || obj[key] === void 0)
            obj[key] = source[key];
    }
}
return obj;});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    return ($__.fs.J$__v3189643886_21_93 = function J$__v3189643886_21(obj) {
var vvv_return, vvv_switch, length, source, keys, l, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = arguments.length;
if (length < 2 || obj == null)
    return obj;
for (var index = 1; index < length; index++) {
    source = arguments[index];
    keys = keysFunc(source);
    l = keys.length;
    for (var i = 0; i < l; i++) {
        key = keys[i];
        if (!undefinedOnly || obj[key] === void 0)
            obj[key] = source[key];
    }
}
return obj;});
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    return ($__.fs.J$__v3189643886_21_97 = function J$__v3189643886_21(obj) {
var vvv_return, vvv_switch, length, source, keys, l, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = arguments.length;
if (length < 2 || obj == null)
    return obj;
for (var index = 1; index < length; index++) {
    source = arguments[index];
    keys = keysFunc(source);
    l = keys.length;
    for (var i = 0; i < l; i++) {
        key = keys[i];
        if (!undefinedOnly || obj[key] === void 0)
            obj[key] = source[key];
    }
}
return obj;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (obj) {
    var length = arguments.length;
    if (length < 2 || obj == null)
        return obj;
    for (var index = 1; index < length; index++) {
        var source = arguments[index], keys = keysFunc(source), l = keys.length;
        for (var i = 0; i < l; i++) {
            var key = keys[i];
            if (!undefinedOnly || obj[key] === void 0)
                obj[key] = source[key];
        }
    }
    return obj;
};});
    baseCreate = ($__.fs.J$__v3189643886_25_14 = function J$__v3189643886_25(prototype) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prototype = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!_.isObject(prototype))
    return {};
if (nativeCreate)
    return nativeCreate(prototype);
Ctor.prototype = prototype;
result = new Ctor();
Ctor.prototype = null;
return result;});
    property = ($__.fs.J$__v3189643886_29_15 = function J$__v3189643886_29(key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
key = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v3189643886_27_16 = function J$__v3189643886_27(obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 2);
    return obj == null, (obj[key, 'length'], 7);
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    return obj == null, (obj[key, 'length'], 131);
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    return obj == null, (obj[key, 'length'], 3);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return obj == null ? void 0 : obj[key];});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (obj) {
    return obj == null ? void 0 : obj[key];
};});
    MAX_ARRAY_INDEX = Math['pow'](2, 53) - 1;
    getLength = (property, $__.fs.J$__v3189643886_29_15)('length', 0, true, $__.uid);
    isArrayLike = ($__.fs.J$__v3189643886_31_17 = function J$__v3189643886_31(collection) {
var vvv_return, vvv_switch, length;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
collection = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    length = (getLength, $__.fs.J$__v3189643886_27_16)(collection, 0, true, $__.uid);
    return (typeof length == 'number', length >= 0), length <= MAX_ARRAY_INDEX;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    length = (getLength, $__.fs.J$__v3189643886_27_16)(collection, 1, true, $__.uid);
    return (typeof length == 'number', length >= 0), length <= MAX_ARRAY_INDEX;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    length = (getLength, $__.fs.J$__v3189643886_27_16)(collection, 2, true, $__.uid);
    return (typeof length == 'number', length >= 0), length <= MAX_ARRAY_INDEX;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    length = (getLength, $__.fs.J$__v3189643886_27_16)(collection, 3, true, $__.uid);
    return (typeof length == 'number', length >= 0), length <= MAX_ARRAY_INDEX;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = getLength(collection);
return typeof length == 'number' && length >= 0 && length <= MAX_ARRAY_INDEX;});
    _.each = _.forEach = ($__.fs.J$__v3189643886_33_18 = function J$__v3189643886_33(obj, iteratee, context) {
var vvv_return, vvv_switch, i, length, keys;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    iteratee = (optimizeCb, $__.fs.J$__v3189643886_15_10)((iteratee, $__.fs.J$__v3189643886_242_107), context, 0, true, $__.uid);
    (isArrayLike, $__.fs.J$__v3189643886_31_17)(obj, 0, true, $__.uid);
    i = 0, length = (obj.length, 7);
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 0], 'string'), i, obj, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 1], 'string'), i, obj, 1, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 2], 'string'), i, obj, 2, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 3], 'string'), i, obj, 3, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 4], 'string'), i, obj, 4, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 5], 'string'), i, obj, 5, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_242_107)(TAJS_restrictToType(obj[i, 6], 'string'), i, obj, 6, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    return obj;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    iteratee = (optimizeCb, $__.fs.J$__v3189643886_15_10)((iteratee, $__.fs.J$__v3189643886_310_142), context, 1, true, $__.uid);
    (isArrayLike, $__.fs.J$__v3189643886_31_17)(obj, 1, true, $__.uid);
    i = 0, length = (obj.length, 131);
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 0], 'string'), i, obj, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 1], 'string'), i, obj, 1, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 2], 'string'), i, obj, 2, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 3], 'string'), i, obj, 3, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 4], 'string'), i, obj, 4, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 5], 'string'), i, obj, 5, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 6], 'string'), i, obj, 6, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 7], 'string'), i, obj, 7, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 8], 'string'), i, obj, 8, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 9], 'string'), i, obj, 9, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 10], 'string'), i, obj, 10, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 11], 'string'), i, obj, 11, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 12], 'string'), i, obj, 12, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 13], 'string'), i, obj, 13, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 14], 'string'), i, obj, 14, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 15], 'string'), i, obj, 15, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 16], 'string'), i, obj, 16, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 17], 'string'), i, obj, 17, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 18], 'string'), i, obj, 18, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 19], 'string'), i, obj, 19, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 20], 'string'), i, obj, 20, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 21], 'string'), i, obj, 21, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 22], 'string'), i, obj, 22, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 23], 'string'), i, obj, 23, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 24], 'string'), i, obj, 24, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 25], 'string'), i, obj, 25, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 26], 'string'), i, obj, 26, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 27], 'string'), i, obj, 27, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 28], 'string'), i, obj, 28, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 29], 'string'), i, obj, 29, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 30], 'string'), i, obj, 30, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 31], 'string'), i, obj, 31, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 32], 'string'), i, obj, 32, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 33], 'string'), i, obj, 33, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 34], 'string'), i, obj, 34, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 35], 'string'), i, obj, 35, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 36], 'string'), i, obj, 36, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 37], 'string'), i, obj, 37, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 38], 'string'), i, obj, 38, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 39], 'string'), i, obj, 39, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 40], 'string'), i, obj, 40, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 41], 'string'), i, obj, 41, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 42], 'string'), i, obj, 42, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 43], 'string'), i, obj, 43, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 44], 'string'), i, obj, 44, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 45], 'string'), i, obj, 45, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 46], 'string'), i, obj, 46, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 47], 'string'), i, obj, 47, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 48], 'string'), i, obj, 48, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 49], 'string'), i, obj, 49, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 50], 'string'), i, obj, 50, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 51], 'string'), i, obj, 51, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 52], 'string'), i, obj, 52, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 53], 'string'), i, obj, 53, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 54], 'string'), i, obj, 54, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 55], 'string'), i, obj, 55, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 56], 'string'), i, obj, 56, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 57], 'string'), i, obj, 57, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 58], 'string'), i, obj, 58, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 59], 'string'), i, obj, 59, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 60], 'string'), i, obj, 60, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 61], 'string'), i, obj, 61, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 62], 'string'), i, obj, 62, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 63], 'string'), i, obj, 63, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 64], 'string'), i, obj, 64, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 65], 'string'), i, obj, 65, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 66], 'string'), i, obj, 66, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 67], 'string'), i, obj, 67, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 68], 'string'), i, obj, 68, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 69], 'string'), i, obj, 69, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 70], 'string'), i, obj, 70, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 71], 'string'), i, obj, 71, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 72], 'string'), i, obj, 72, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 73], 'string'), i, obj, 73, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 74], 'string'), i, obj, 74, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 75], 'string'), i, obj, 75, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 76], 'string'), i, obj, 76, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 77], 'string'), i, obj, 77, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 78], 'string'), i, obj, 78, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 79], 'string'), i, obj, 79, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 80], 'string'), i, obj, 80, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 81], 'string'), i, obj, 81, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 82], 'string'), i, obj, 82, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 83], 'string'), i, obj, 83, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 84], 'string'), i, obj, 84, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 85], 'string'), i, obj, 85, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 86], 'string'), i, obj, 86, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 87], 'string'), i, obj, 87, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 88], 'string'), i, obj, 88, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 89], 'string'), i, obj, 89, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 90], 'string'), i, obj, 90, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 91], 'string'), i, obj, 91, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 92], 'string'), i, obj, 92, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 93], 'string'), i, obj, 93, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 94], 'string'), i, obj, 94, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 95], 'string'), i, obj, 95, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 96], 'string'), i, obj, 96, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 97], 'string'), i, obj, 97, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 98], 'string'), i, obj, 98, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 99], 'string'), i, obj, 99, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 100], 'string'), i, obj, 100, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 101], 'string'), i, obj, 101, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 102], 'string'), i, obj, 102, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 103], 'string'), i, obj, 103, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 104], 'string'), i, obj, 104, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 105], 'string'), i, obj, 105, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 106], 'string'), i, obj, 106, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 107], 'string'), i, obj, 107, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 108], 'string'), i, obj, 108, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 109], 'string'), i, obj, 109, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 110], 'string'), i, obj, 110, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 111], 'string'), i, obj, 111, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 112], 'string'), i, obj, 112, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 113], 'string'), i, obj, 113, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 114], 'string'), i, obj, 114, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 115], 'string'), i, obj, 115, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 116], 'string'), i, obj, 116, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 117], 'string'), i, obj, 117, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 118], 'string'), i, obj, 118, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 119], 'string'), i, obj, 119, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 120], 'string'), i, obj, 120, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 121], 'string'), i, obj, 121, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 122], 'string'), i, obj, 122, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 123], 'string'), i, obj, 123, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 124], 'string'), i, obj, 124, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 125], 'string'), i, obj, 125, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 126], 'string'), i, obj, 126, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 127], 'string'), i, obj, 127, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 128], 'string'), i, obj, 128, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 129], 'string'), i, obj, 129, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_310_142)(TAJS_restrictToType(obj[i, 130], 'string'), i, obj, 130, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    return obj;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    iteratee = (optimizeCb, $__.fs.J$__v3189643886_15_10)((iteratee, $__.fs.J$__v3189643886_316_274), context, 2, true, $__.uid);
    (isArrayLike, $__.fs.J$__v3189643886_31_17)(obj, 2, true, $__.uid);
    i = 0, length = (obj.length, 7);
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 0], 'string'), i, obj, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 1], 'string'), i, obj, 1, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 2], 'string'), i, obj, 2, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 3], 'string'), i, obj, 3, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 4], 'string'), i, obj, 4, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 5], 'string'), i, obj, 5, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_316_274)(TAJS_restrictToType(obj[i, 6], 'string'), i, obj, 6, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    return obj;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    iteratee = (optimizeCb, $__.fs.J$__v3189643886_15_10)((iteratee, $__.fs.J$__v3189643886_320_282), context, 3, true, $__.uid);
    (isArrayLike, $__.fs.J$__v3189643886_31_17)(obj, 3, true, $__.uid);
    i = 0, length = (obj.length, 3);
    i < length;
    (iteratee, $__.fs.J$__v3189643886_320_282)(TAJS_restrictToType(obj[i, 0], 'string'), i, obj, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_320_282)(TAJS_restrictToType(obj[i, 1], 'string'), i, obj, 1, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    (iteratee, $__.fs.J$__v3189643886_320_282)(TAJS_restrictToType(obj[i, 2], 'string'), i, obj, 2, true, $__.uid);
    (i = +i + 1) - 1;
    i < length;
    return obj;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = optimizeCb(iteratee, context);
if (isArrayLike(obj)) {
    for (i = 0, length = obj.length; i < length; i++) {
        iteratee(obj[i], i, obj);
    }
} else {
    keys = _.keys(obj);
    for (i = 0, length = keys.length; i < length; i++) {
        iteratee(obj[keys[i]], keys[i], obj);
    }
}
return obj;});
    _.map = _.collect = ($__.fs.J$__v3189643886_35_19 = function J$__v3189643886_35(obj, iteratee, context) {
var vvv_return, vvv_switch, keys, length, results, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = cb(iteratee, context);
keys = !isArrayLike(obj) && _.keys(obj);
length = (keys || obj).length;
results = Array(length);
for (var index = 0; index < length; index++) {
    currentKey = keys ? keys[index] : index;
    results[index] = iteratee(obj[currentKey], currentKey, obj);
}
return results;});
    _.reduce = _.foldl = _.inject = (createReduce, $__.fs.createReduce_4)(1, 0, true, $__.uid);
    _.reduceRight = _.foldr = (createReduce, $__.fs.createReduce_4)(-1, 1, true, $__.uid);
    _.find = _.detect = ($__.fs.J$__v3189643886_41_24 = function J$__v3189643886_41(obj, predicate, context) {
var vvv_return, vvv_switch, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (isArrayLike(obj)) {
    key = _.findIndex(obj, predicate, context);
} else {
    key = _.findKey(obj, predicate, context);
}
if (key !== void 0 && key !== -1)
    return obj[key];});
    _.filter = _.select = ($__.fs.J$__v3189643886_45_25 = function J$__v3189643886_45(obj, predicate, context) {
var vvv_return, vvv_switch, results;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
results = [];
predicate = cb(predicate, context);
_.each(obj, function (value, index, list) {
    if (predicate(value, index, list))
        results.push(value);
});
return results;});
    _.reject = ($__.fs.J$__v3189643886_47_26 = function J$__v3189643886_47(obj, predicate, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.filter(obj, _.negate(cb(predicate)), context);});
    _.every = _.all = ($__.fs.J$__v3189643886_49_27 = function J$__v3189643886_49(obj, predicate, context) {
var vvv_return, vvv_switch, keys, length, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
keys = !isArrayLike(obj) && _.keys(obj);
length = (keys || obj).length;
for (var index = 0; index < length; index++) {
    currentKey = keys ? keys[index] : index;
    if (!predicate(obj[currentKey], currentKey, obj))
        return false;
}
return true;});
    _.some = _.any = ($__.fs.J$__v3189643886_51_28 = function J$__v3189643886_51(obj, predicate, context) {
var vvv_return, vvv_switch, keys, length, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
keys = !isArrayLike(obj) && _.keys(obj);
length = (keys || obj).length;
for (var index = 0; index < length; index++) {
    currentKey = keys ? keys[index] : index;
    if (predicate(obj[currentKey], currentKey, obj))
        return true;
}
return false;});
    _.contains = _.includes = _.include = ($__.fs.J$__v3189643886_53_29 = function J$__v3189643886_53(obj, item, fromIndex, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], item = arguments[1], fromIndex = arguments[2], guard = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!isArrayLike(obj))
    obj = _.values(obj);
if (typeof fromIndex != 'number' || guard)
    fromIndex = 0;
return _.indexOf(obj, item, fromIndex) >= 0;});
    _.invoke = ($__.fs.J$__v3189643886_57_30 = function J$__v3189643886_57(obj, method) {
var vvv_return, vvv_switch, args, isFunc;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], method = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments, 2);
isFunc = _.isFunction(method);
return _.map(obj, function (value) {
    var func = isFunc ? method : value[method];
    return func == null ? func : func.apply(value, args);
});});
    _.pluck = ($__.fs.J$__v3189643886_59_31 = function J$__v3189643886_59(obj, key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], key = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.map(obj, _.property(key));});
    _.where = ($__.fs.J$__v3189643886_61_32 = function J$__v3189643886_61(obj, attrs) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], attrs = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.filter(obj, _.matcher(attrs));});
    _.findWhere = ($__.fs.J$__v3189643886_63_33 = function J$__v3189643886_63(obj, attrs) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], attrs = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.find(obj, _.matcher(attrs));});
    _.max = ($__.fs.J$__v3189643886_67_34 = function J$__v3189643886_67(obj, iteratee, context) {
var vvv_return, vvv_switch, result, lastComputed, value, computed;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = -Infinity;
lastComputed = -Infinity;
if (iteratee == null && obj != null) {
    obj = isArrayLike(obj) ? obj : _.values(obj);
    for (var i = 0, length = obj.length; i < length; i++) {
        value = obj[i];
        if (value > result) {
            result = value;
        }
    }
} else {
    iteratee = cb(iteratee, context);
    _.each(obj, function (value, index, list) {
        computed = iteratee(value, index, list);
        if (computed > lastComputed || computed === -Infinity && result === -Infinity) {
            result = value;
            lastComputed = computed;
        }
    });
}
return result;});
    _.min = ($__.fs.J$__v3189643886_71_35 = function J$__v3189643886_71(obj, iteratee, context) {
var vvv_return, vvv_switch, result, lastComputed, value, computed;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = Infinity;
lastComputed = Infinity;
if (iteratee == null && obj != null) {
    obj = isArrayLike(obj) ? obj : _.values(obj);
    for (var i = 0, length = obj.length; i < length; i++) {
        value = obj[i];
        if (value < result) {
            result = value;
        }
    }
} else {
    iteratee = cb(iteratee, context);
    _.each(obj, function (value, index, list) {
        computed = iteratee(value, index, list);
        if (computed < lastComputed || computed === Infinity && result === Infinity) {
            result = value;
            lastComputed = computed;
        }
    });
}
return result;});
    _.shuffle = ($__.fs.J$__v3189643886_73_36 = function J$__v3189643886_73(obj) {
var vvv_return, vvv_switch, set, length, shuffled;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
set = isArrayLike(obj) ? obj : _.values(obj);
length = set.length;
shuffled = Array(length);
for (var index = 0, rand; index < length; index++) {
    rand = _.random(0, index);
    if (rand !== index)
        shuffled[index] = shuffled[rand];
    shuffled[rand] = set[index];
}
return shuffled;});
    _.sample = ($__.fs.J$__v3189643886_75_37 = function J$__v3189643886_75(obj, n, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], n = arguments[1], guard = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (n == null || guard) {
    if (!isArrayLike(obj))
        obj = _.values(obj);
    return obj[_.random(obj.length - 1)];
}
return _.shuffle(obj).slice(0, Math.max(0, n));});
    _.sortBy = ($__.fs.J$__v3189643886_81_38 = function J$__v3189643886_81(obj, iteratee, context) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = cb(iteratee, context);
return _.pluck(_.map(obj, function (value, index, list) {
    return {
        value: value,
        index: index,
        criteria: iteratee(value, index, list)
    };
}).sort(function (left, right) {
    var a = left.criteria;
    var b = right.criteria;
    if (a !== b) {
        if (a > b || a === void 0)
            return 1;
        if (a < b || b === void 0)
            return -1;
    }
    return left.index - right.index;
}), 'value');});
    group = ($__.fs.J$__v3189643886_87_39 = function J$__v3189643886_87(behavior) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
behavior = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return ($__.fs.J$__v3189643886_85_41 = function J$__v3189643886_85(obj, iteratee, context) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
iteratee = cb(iteratee, context);
_.each(obj, function (value, index) {
    var key = iteratee(value, index, obj);
    behavior(result, value, key);
});
return result;});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    return ($__.fs.J$__v3189643886_85_43 = function J$__v3189643886_85(obj, iteratee, context) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
iteratee = cb(iteratee, context);
_.each(obj, function (value, index) {
    var key = iteratee(value, index, obj);
    behavior(result, value, key);
});
return result;});
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    return ($__.fs.J$__v3189643886_85_45 = function J$__v3189643886_85(obj, iteratee, context) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
iteratee = cb(iteratee, context);
_.each(obj, function (value, index) {
    var key = iteratee(value, index, obj);
    behavior(result, value, key);
});
return result;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function (obj, iteratee, context) {
    var result = {};
    iteratee = cb(iteratee, context);
    _.each(obj, function (value, index) {
        var key = iteratee(value, index, obj);
        behavior(result, value, key);
    });
    return result;
};});
    _.groupBy = (group, $__.fs.J$__v3189643886_87_39)(($__.fs.J$__v3189643886_89_40 = function J$__v3189643886_89(result, value, key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
result = arguments[0], value = arguments[1], key = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_.has(result, key))
    result[key].push(value);
else
    result[key] = [value];}), 0, true, $__.uid);
    _.indexBy = (group, $__.fs.J$__v3189643886_87_39)(($__.fs.J$__v3189643886_91_42 = function J$__v3189643886_91(result, value, key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
result = arguments[0], value = arguments[1], key = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result[key] = value;}), 1, true, $__.uid);
    _.countBy = (group, $__.fs.J$__v3189643886_87_39)(($__.fs.J$__v3189643886_93_44 = function J$__v3189643886_93(result, value, key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
result = arguments[0], value = arguments[1], key = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_.has(result, key))
    result[key]++;
else
    result[key] = 1;}), 2, true, $__.uid);
    _.toArray = ($__.fs.J$__v3189643886_95_46 = function J$__v3189643886_95(obj) {
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
if (!obj)
    return [];
if (_.isArray(obj))
    return slice.call(obj);
if (isArrayLike(obj))
    return _.map(obj, _.identity);
return _.values(obj);});
    _.size = ($__.fs.J$__v3189643886_97_47 = function J$__v3189643886_97(obj) {
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
    return 0;
return isArrayLike(obj) ? obj.length : _.keys(obj).length;});
    _.partition = ($__.fs.J$__v3189643886_101_48 = function J$__v3189643886_101(obj, predicate, context) {
var vvv_return, vvv_switch, pass, fail;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
pass = [];
fail = [];
_.each(obj, function (value, key, obj) {
    (predicate(value, key, obj) ? pass : fail).push(value);
});
return [
    pass,
    fail
];});
    _.first = _.head = _.take = ($__.fs.J$__v3189643886_103_49 = function J$__v3189643886_103(array, n, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], n = arguments[1], guard = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (array == null)
    return void 0;
if (n == null || guard)
    return array[0];
return _.initial(array, array.length - n);});
    _.initial = ($__.fs.J$__v3189643886_105_50 = function J$__v3189643886_105(array, n, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], n = arguments[1], guard = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return slice.call(array, 0, Math.max(0, array.length - (n == null || guard ? 1 : n)));});
    _.last = ($__.fs.J$__v3189643886_107_51 = function J$__v3189643886_107(array, n, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], n = arguments[1], guard = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (array == null)
    return void 0;
if (n == null || guard)
    return array[array.length - 1];
return _.rest(array, Math.max(0, array.length - n));});
    _.rest = _.tail = _.drop = ($__.fs.J$__v3189643886_109_52 = function J$__v3189643886_109(array, n, guard) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], n = arguments[1], guard = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return slice.call(array, n == null || guard ? 1 : n);});
    _.compact = ($__.fs.J$__v3189643886_111_53 = function J$__v3189643886_111(array) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.filter(array, _.identity);});
    flatten = ($__.fs.J$__v3189643886_113_54 = function J$__v3189643886_113(input, shallow, strict, startIndex) {
var vvv_return, vvv_switch, output, idx, value, j, len;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
input = arguments[0], shallow = arguments[1], strict = arguments[2], startIndex = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
output = [];
idx = 0;
for (var i = startIndex || 0, length = getLength(input); i < length; i++) {
    value = input[i];
    if (isArrayLike(value) && (_.isArray(value) || _.isArguments(value))) {
        if (!shallow)
            value = flatten(value, shallow, strict);
        j = 0;
        len = value.length;
        output.length += len;
        while (j < len) {
            output[idx++] = value[j++];
        }
    } else if (!strict) {
        output[idx++] = value;
    }
}
return output;});
    _.flatten = ($__.fs.J$__v3189643886_115_55 = function J$__v3189643886_115(array, shallow) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], shallow = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return flatten(array, shallow, false);});
    _.without = ($__.fs.J$__v3189643886_117_56 = function J$__v3189643886_117(array) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.difference(array, slice.call(arguments, 1));});
    _.uniq = _.unique = ($__.fs.J$__v3189643886_119_57 = function J$__v3189643886_119(array, isSorted, iteratee, context) {
var vvv_return, vvv_switch, result, seen, value, computed;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], isSorted = arguments[1], iteratee = arguments[2], context = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!_.isBoolean(isSorted)) {
    context = iteratee;
    iteratee = isSorted;
    isSorted = false;
}
if (iteratee != null)
    iteratee = cb(iteratee, context);
result = [];
seen = [];
for (var i = 0, length = getLength(array); i < length; i++) {
    value = array[i];
    computed = iteratee ? iteratee(value, i, array) : value;
    if (isSorted) {
        if (!i || seen !== computed)
            result.push(value);
        seen = computed;
    } else if (iteratee) {
        if (!_.contains(seen, computed)) {
            seen.push(computed);
            result.push(value);
        }
    } else if (!_.contains(result, value)) {
        result.push(value);
    }
}
return result;});
    _.union = ($__.fs.J$__v3189643886_121_58 = function J$__v3189643886_121() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.uniq(flatten(arguments, true, true));});
    _.intersection = ($__.fs.J$__v3189643886_123_59 = function J$__v3189643886_123(array) {
var vvv_return, vvv_switch, result, argsLength, item;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = [];
argsLength = arguments.length;
for (var i = 0, length = getLength(array); i < length; i++) {
    item = array[i];
    if (_.contains(result, item))
        continue;
    for (var j = 1; j < argsLength; j++) {
        if (!_.contains(arguments[j], item))
            break;
    }
    if (j === argsLength)
        result.push(item);
}
return result;});
    _.difference = ($__.fs.J$__v3189643886_127_60 = function J$__v3189643886_127(array) {
var vvv_return, vvv_switch, rest;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
rest = flatten(arguments, true, true, 1);
return _.filter(array, function (value) {
    return !_.contains(rest, value);
});});
    _.zip = ($__.fs.J$__v3189643886_129_61 = function J$__v3189643886_129() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.unzip(arguments);});
    _.unzip = ($__.fs.J$__v3189643886_131_62 = function J$__v3189643886_131(array) {
var vvv_return, vvv_switch, length, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = array && _.max(array, getLength).length || 0;
result = Array(length);
for (var index = 0; index < length; index++) {
    result[index] = _.pluck(array, index);
}
return result;});
    _.object = ($__.fs.J$__v3189643886_133_63 = function J$__v3189643886_133(list, values) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
list = arguments[0], values = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
for (var i = 0, length = getLength(list); i < length; i++) {
    if (values) {
        result[list[i]] = values[i];
    } else {
        result[list[i][0]] = list[i][1];
    }
}
return result;});
    _.findIndex = (createPredicateIndexFinder, $__.fs.createPredicateIndexFinder_5)(1, 0, true, $__.uid);
    _.findLastIndex = (createPredicateIndexFinder, $__.fs.createPredicateIndexFinder_5)(-1, 1, true, $__.uid);
    _.sortedIndex = ($__.fs.J$__v3189643886_138_66 = function J$__v3189643886_138(array, obj, iteratee, context) {
var vvv_return, vvv_switch, value, low, high, mid;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
array = arguments[0], obj = arguments[1], iteratee = arguments[2], context = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = cb(iteratee, context, 1);
value = iteratee(obj);
low = 0;
high = getLength(array);
while (low < high) {
    mid = Math.floor((low + high) / 2);
    if (iteratee(array[mid]) < value)
        low = mid + 1;
    else
        high = mid;
}
return low;});
    _.indexOf = (createIndexFinder, $__.fs.createIndexFinder_6)(1, (TAJS_restrictToType(_.findIndex, 'function'), $__.fs.J$__v3189643886_135_64), (TAJS_restrictToType(_.sortedIndex, 'function'), $__.fs.J$__v3189643886_138_66), 0, true, $__.uid);
    _.lastIndexOf = (createIndexFinder, $__.fs.createIndexFinder_6)(-1, (TAJS_restrictToType(_.findLastIndex, 'function'), $__.fs.J$__v3189643886_135_65), 1, true, $__.uid);
    _.range = ($__.fs.J$__v3189643886_143_69 = function J$__v3189643886_143(start, stop, step) {
var vvv_return, vvv_switch, length, range;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
start = arguments[0], stop = arguments[1], step = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (stop == null) {
    stop = start || 0;
    start = 0;
}
step = step || 1;
length = Math.max(Math.ceil((stop - start) / step), 0);
range = Array(length);
for (var idx = 0; idx < length; idx++, start += step) {
    range[idx] = start;
}
return range;});
    executeBound = ($__.fs.J$__v3189643886_145_70 = function J$__v3189643886_145(sourceFunc, boundFunc, context, callingContext, args) {
var vvv_return, vvv_switch, self, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
sourceFunc = arguments[0], boundFunc = arguments[1], context = arguments[2], callingContext = arguments[3], args = arguments[4];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!(callingContext instanceof boundFunc))
    return sourceFunc.apply(context, args);
self = baseCreate(sourceFunc.prototype);
result = sourceFunc.apply(self, args);
if (_.isObject(result))
    return result;
return self;});
    _.bind = ($__.fs.J$__v3189643886_149_71 = function J$__v3189643886_149(func, context) {
var vvv_return, vvv_switch, args, bound;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], context = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (nativeBind && func.bind === nativeBind)
    return nativeBind.apply(func, slice.call(arguments, 1));
if (!_.isFunction(func))
    throw new TypeError('Bind must be called on a function');
args = slice.call(arguments, 2);
bound = function () {
    return executeBound(func, bound, context, this, args.concat(slice.call(arguments)));
};
return bound;});
    _.partial = ($__.fs.J$__v3189643886_153_72 = function J$__v3189643886_153(func) {
var vvv_return, vvv_switch, boundArgs, bound;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    boundArgs = function $__lt0(res) {
        res.length = 2;
        if (res.length != 2)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt0, -2);
    }(slice['call'](arguments, 1));
    bound = ($__.fs.J$__v3189643886_151_76 = function J$__v3189643886_151() {
var vvv_return, vvv_switch, position, length, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
position = 0;
length = boundArgs.length;
args = Array(length);
for (var i = 0; i < length; i++) {
    args[i] = boundArgs[i] === _ ? arguments[position++] : boundArgs[i];
}
while (position < arguments.length)
    args.push(arguments[position++]);
return executeBound(func, bound, this, this, args);});
    return bound;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    boundArgs = function $__lt1(res) {
        res.length = 1;
        if (res.length != 1)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt1, -2);
    }(slice['call'](arguments, 1));
    bound = ($__.fs.J$__v3189643886_151_84 = function J$__v3189643886_151() {
var vvv_return, vvv_switch, position, length, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
position = 0;
length = boundArgs.length;
args = Array(length);
for (var i = 0; i < length; i++) {
    args[i] = boundArgs[i] === _ ? arguments[position++] : boundArgs[i];
}
while (position < arguments.length)
    args.push(arguments[position++]);
return executeBound(func, bound, this, this, args);});
    return bound;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
boundArgs = slice.call(arguments, 1);
bound = function () {
    var position = 0, length = boundArgs.length;
    var args = Array(length);
    for (var i = 0; i < length; i++) {
        args[i] = boundArgs[i] === _ ? arguments[position++] : boundArgs[i];
    }
    while (position < arguments.length)
        args.push(arguments[position++]);
    return executeBound(func, bound, this, this, args);
};
return bound;});
    _.bindAll = ($__.fs.J$__v3189643886_155_73 = function J$__v3189643886_155(obj) {
var vvv_return, vvv_switch, i, length, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = arguments.length;
if (length <= 1)
    throw new Error('bindAll must be passed function names');
for (i = 1; i < length; i++) {
    key = arguments[i];
    obj[key] = _.bind(obj[key], obj);
}
return obj;});
    _.memoize = ($__.fs.J$__v3189643886_159_74 = function J$__v3189643886_159(func, hasher) {
var vvv_return, vvv_switch, memoize;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], hasher = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
memoize = function (key) {
    var cache = memoize.cache;
    var address = '' + (hasher ? hasher.apply(this, arguments) : key);
    if (!_.has(cache, address))
        cache[address] = func.apply(this, arguments);
    return cache[address];
};
memoize.cache = {};
return memoize;});
    _.delay = ($__.fs.J$__v3189643886_163_75 = function J$__v3189643886_163(func, wait) {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], wait = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = slice.call(arguments, 2);
return setTimeout(function () {
    return func.apply(null, args);
}, wait);});
    _.defer = (vvv_tmp0 = _, 'partial', $__.fs.J$__v3189643886_153_72.call(vvv_tmp0, (TAJS_restrictToType(_.delay, 'function'), $__.fs.J$__v3189643886_163_75), (_, $__.fs.J$__v3189643886_3_9), 1, 0, true, $__.uid));
    _.throttle = ($__.fs.J$__v3189643886_169_77 = function J$__v3189643886_169(func, wait, options) {
var vvv_return, vvv_switch, context, args, result, timeout, previous, later;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], wait = arguments[1], options = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
timeout = null;
previous = 0;
if (!options)
    options = {};
later = function () {
    previous = options.leading === false ? 0 : _.now();
    timeout = null;
    result = func.apply(context, args);
    if (!timeout)
        context = args = null;
};
return function () {
    var now = _.now();
    if (!previous && options.leading === false)
        previous = now;
    var remaining = wait - (now - previous);
    context = this;
    args = arguments;
    if (remaining <= 0 || remaining > wait) {
        if (timeout) {
            clearTimeout(timeout);
            timeout = null;
        }
        previous = now;
        result = func.apply(context, args);
        if (!timeout)
            context = args = null;
    } else if (!timeout && options.trailing !== false) {
        timeout = setTimeout(later, remaining);
    }
    return result;
};});
    _.debounce = ($__.fs.J$__v3189643886_175_78 = function J$__v3189643886_175(func, wait, immediate) {
var vvv_return, vvv_switch, timeout, args, context, timestamp, result, later;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], wait = arguments[1], immediate = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
later = function () {
    var last = _.now() - timestamp;
    if (last < wait && last >= 0) {
        timeout = setTimeout(later, wait - last);
    } else {
        timeout = null;
        if (!immediate) {
            result = func.apply(context, args);
            if (!timeout)
                context = args = null;
        }
    }
};
return function () {
    context = this;
    args = arguments;
    timestamp = _.now();
    var callNow = immediate && !timeout;
    if (!timeout)
        timeout = setTimeout(later, wait);
    if (callNow) {
        result = func.apply(context, args);
        context = args = null;
    }
    return result;
};});
    _.wrap = ($__.fs.J$__v3189643886_177_79 = function J$__v3189643886_177(func, wrapper) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
func = arguments[0], wrapper = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _.partial(wrapper, func);});
    _.negate = ($__.fs.J$__v3189643886_181_80 = function J$__v3189643886_181(predicate) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
predicate = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    return !predicate.apply(this, arguments);
};});
    _.compose = ($__.fs.J$__v3189643886_185_81 = function J$__v3189643886_185() {
var vvv_return, vvv_switch, args, start;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = arguments;
start = args.length - 1;
return function () {
    var i = start;
    var result = args[start].apply(this, arguments);
    while (i--)
        result = args[i].call(this, result);
    return result;
};});
    _.after = ($__.fs.J$__v3189643886_189_82 = function J$__v3189643886_189(times, func) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
times = arguments[0], func = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    if (--times < 1) {
        return func.apply(this, arguments);
    }
};});
    _.before = ($__.fs.J$__v3189643886_193_83 = function J$__v3189643886_193(times, func) {
var vvv_return, vvv_switch, memo;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
times = arguments[0], func = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    if (--times > 0) {
        memo = func.apply(this, arguments);
    }
    if (times <= 1)
        func = null;
    return memo;
};});
    _.once = (vvv_tmp1 = _, 'partial', $__.fs.J$__v3189643886_153_72.call(vvv_tmp1, (TAJS_restrictToType(_.before, 'function'), $__.fs.J$__v3189643886_193_83), 2, 1, true, $__.uid));
    hasEnumBug = !($__.os.oid0 = {
        'toString': null
    })['propertyIsEnumerable']('toString');
    nonEnumerableProps = [
        'valueOf',
        'isPrototypeOf',
        'toString',
        'propertyIsEnumerable',
        'hasOwnProperty',
        'toLocaleString'
    ];
    _.keys = ($__.fs.J$__v3189643886_196_85 = function J$__v3189643886_196(obj) {
var vvv_return, vvv_switch, vvv_tmp0, keys, key;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    !(vvv_tmp0 = _, 'isObject', $__.fs.J$__v3189643886_238_106.call(vvv_tmp0, obj, 0, true, $__.uid));
    nativeKeys;
    return function $__lt2(res) {
        res.length = 6;
        if (res.length != 6)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt2, -2);
    }((nativeKeys(obj), [
        '&',
        '<',
        '>',
        '"',
        '\'',
        '`'
    ]));
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    !(vvv_tmp0 = _, 'isObject', $__.fs.J$__v3189643886_238_106.call(vvv_tmp0, obj, 1, true, $__.uid));
    nativeKeys;
    return function $__lt3(res) {
        res.length = 6;
        if (res.length != 6)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt3, -2);
    }((nativeKeys(obj), [
        '&',
        '<',
        '>',
        '"',
        '\'',
        '`'
    ]));
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    !(vvv_tmp0 = _, 'isObject', $__.fs.J$__v3189643886_238_106.call(vvv_tmp0, obj, 2, true, $__.uid));
    nativeKeys;
    return function $__lt4(res) {
        res.length = 6;
        if (res.length != 6)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt4, -2);
    }((nativeKeys(obj), [
        '&amp;',
        '&lt;',
        '&gt;',
        '&quot;',
        '&#x27;',
        '&#x60;'
    ]));
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!_.isObject(obj))
    return [];
if (nativeKeys)
    return nativeKeys(obj);
keys = [];
for (var key in obj)
    if (_.has(obj, key))
    keys.push(key);
if (hasEnumBug)
    collectNonEnumProps(obj, keys);
return keys;});
    _.allKeys = ($__.fs.J$__v3189643886_198_86 = function J$__v3189643886_198(obj) {
var vvv_return, vvv_switch, keys;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!_.isObject(obj))
    return [];
keys = [];
for (var key in obj)
    keys.push(key);
if (hasEnumBug)
    collectNonEnumProps(obj, keys);
return keys;});
    _.values = ($__.fs.J$__v3189643886_200_87 = function J$__v3189643886_200(obj) {
var vvv_return, vvv_switch, keys, length, values;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
keys = _.keys(obj);
length = keys.length;
values = Array(length);
for (var i = 0; i < length; i++) {
    values[i] = obj[keys[i]];
}
return values;});
    _.mapObject = ($__.fs.J$__v3189643886_202_88 = function J$__v3189643886_202(obj, iteratee, context) {
var vvv_return, vvv_switch, keys, length, results, currentKey;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
iteratee = cb(iteratee, context);
keys = _.keys(obj);
length = keys.length;
results = {};
for (var index = 0; index < length; index++) {
    currentKey = keys[index];
    results[currentKey] = iteratee(obj[currentKey], currentKey, obj);
}
return results;});
    _.pairs = ($__.fs.J$__v3189643886_204_89 = function J$__v3189643886_204(obj) {
var vvv_return, vvv_switch, keys, length, pairs;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
keys = _.keys(obj);
length = keys.length;
pairs = Array(length);
for (var i = 0; i < length; i++) {
    pairs[i] = [
        keys[i],
        obj[keys[i]]
    ];
}
return pairs;});
    _.invert = ($__.fs.J$__v3189643886_206_90 = function J$__v3189643886_206(obj) {
var vvv_return, vvv_switch, vvv_tmp0, result, keys, i, length;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    result = $__.os.oid2 = {};
    keys = (vvv_tmp0 = _, 'keys', $__.fs.J$__v3189643886_196_85.call(vvv_tmp0, obj, 0, true, $__.uid));
    i = 0;
    length = (keys.length, 6);
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 0], 'string'), '&'], 'string')] = TAJS_restrictToType(keys[i, 0], 'string');
    (i = +i + 1) - 1;
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 1], 'string'), '<'], 'string'), '&lt;'] = TAJS_restrictToType(keys[i, 1], 'string');
    (i = +i + 1) - 1;
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 2], 'string'), '>'], 'string'), '&gt;'] = TAJS_restrictToType(keys[i, 2], 'string');
    (i = +i + 1) - 1;
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 3], 'string'), '"'], 'string')] = TAJS_restrictToType(keys[i, 3], 'string');
    (i = +i + 1) - 1;
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 4], 'string'), '\''], 'string')] = TAJS_restrictToType(keys[i, 4], 'string');
    (i = +i + 1) - 1;
    i < length;
    (result, $__.os.oid2)[TAJS_restrictToType((obj, $__.os.oid1)[TAJS_restrictToType(keys[i, 5], 'string'), '`'], 'string')] = TAJS_restrictToType(keys[i, 5], 'string');
    (i = +i + 1) - 1;
    i < length;
    return result;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
keys = _.keys(obj);
for (var i = 0, length = keys.length; i < length; i++) {
    result[obj[keys[i]]] = keys[i];
}
return result;});
    _.functions = _.methods = ($__.fs.J$__v3189643886_208_91 = function J$__v3189643886_208(obj) {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1, vvv_tmp2, vvv_tmp3, vvv_tmp4, vvv_tmp5, vvv_tmp6, vvv_tmp7, vvv_tmp8, vvv_tmp9, vvv_tmp10, vvv_tmp11, vvv_tmp12, vvv_tmp13, vvv_tmp14, vvv_tmp15, vvv_tmp16, vvv_tmp17, vvv_tmp18, vvv_tmp19, vvv_tmp20, vvv_tmp21, vvv_tmp22, vvv_tmp23, vvv_tmp24, vvv_tmp25, vvv_tmp26, vvv_tmp27, vvv_tmp28, vvv_tmp29, vvv_tmp30, vvv_tmp31, vvv_tmp32, vvv_tmp33, vvv_tmp34, vvv_tmp35, vvv_tmp36, vvv_tmp37, vvv_tmp38, vvv_tmp39, vvv_tmp40, vvv_tmp41, vvv_tmp42, vvv_tmp43, vvv_tmp44, vvv_tmp45, vvv_tmp46, vvv_tmp47, vvv_tmp48, vvv_tmp49, vvv_tmp50, vvv_tmp51, vvv_tmp52, vvv_tmp53, vvv_tmp54, vvv_tmp55, vvv_tmp56, vvv_tmp57, vvv_tmp58, vvv_tmp59, vvv_tmp60, vvv_tmp61, vvv_tmp62, vvv_tmp63, vvv_tmp64, vvv_tmp65, vvv_tmp66, vvv_tmp67, vvv_tmp68, vvv_tmp69, vvv_tmp70, vvv_tmp71, vvv_tmp72, vvv_tmp73, vvv_tmp74, vvv_tmp75, vvv_tmp76, vvv_tmp77, vvv_tmp78, vvv_tmp79, vvv_tmp80, vvv_tmp81, vvv_tmp82, vvv_tmp83, vvv_tmp84, vvv_tmp85, vvv_tmp86, vvv_tmp87, vvv_tmp88, vvv_tmp89, vvv_tmp90, vvv_tmp91, vvv_tmp92, vvv_tmp93, vvv_tmp94, vvv_tmp95, vvv_tmp96, vvv_tmp97, vvv_tmp98, vvv_tmp99, vvv_tmp100, vvv_tmp101, vvv_tmp102, vvv_tmp103, vvv_tmp104, vvv_tmp105, vvv_tmp106, vvv_tmp107, vvv_tmp108, vvv_tmp109, vvv_tmp110, vvv_tmp111, vvv_tmp112, vvv_tmp113, vvv_tmp114, vvv_tmp115, vvv_tmp116, vvv_tmp117, vvv_tmp118, vvv_tmp119, vvv_tmp120, vvv_tmp121, vvv_tmp122, vvv_tmp123, vvv_tmp124, vvv_tmp125, vvv_tmp126, vvv_tmp127, vvv_tmp128, vvv_tmp129, vvv_tmp130, vvv_tmp131, vvv_tmp132, vvv_tmp133, names, key;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    names = [];
    vvv_tmp0 = obj;
    'VERSION' in vvv_tmp0;
    key = 'VERSION';
    vvv_tmp1 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp1, TAJS_restrictToType(obj[key, 'VERSION'], 'string'), 0, true, $__.uid);
    'iteratee' in vvv_tmp0;
    key = 'iteratee';
    vvv_tmp2 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp2, (TAJS_restrictToType(obj[key, 'iteratee'], 'function'), $__.fs.J$__v3189643886_19_12), 1, true, $__.uid);
    names['push'](key);
    'forEach' in vvv_tmp0;
    key = 'forEach';
    vvv_tmp3 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp3, (TAJS_restrictToType(obj[key, 'forEach'], 'function'), $__.fs.J$__v3189643886_33_18), 2, true, $__.uid);
    names['push'](key);
    'each' in vvv_tmp0;
    key = 'each';
    vvv_tmp4 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp4, (TAJS_restrictToType(obj[key, 'each'], 'function'), $__.fs.J$__v3189643886_33_18), 3, true, $__.uid);
    names['push'](key);
    'collect' in vvv_tmp0;
    key = 'collect';
    vvv_tmp5 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp5, (TAJS_restrictToType(obj[key, 'collect'], 'function'), $__.fs.J$__v3189643886_35_19), 4, true, $__.uid);
    names['push'](key);
    'map' in vvv_tmp0;
    key = 'map';
    vvv_tmp6 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp6, (TAJS_restrictToType(obj[key, 'map'], 'function'), $__.fs.J$__v3189643886_35_19), 5, true, $__.uid);
    names['push'](key);
    'inject' in vvv_tmp0;
    key = 'inject';
    vvv_tmp7 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp7, (TAJS_restrictToType(obj[key, 'inject'], 'function'), $__.fs.J$__v3189643886_38_21), 6, true, $__.uid);
    names['push'](key);
    'foldl' in vvv_tmp0;
    key = 'foldl';
    vvv_tmp8 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp8, (TAJS_restrictToType(obj[key, 'foldl'], 'function'), $__.fs.J$__v3189643886_38_21), 7, true, $__.uid);
    names['push'](key);
    'reduce' in vvv_tmp0;
    key = 'reduce';
    vvv_tmp9 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp9, (TAJS_restrictToType(obj[key, 'reduce'], 'function'), $__.fs.J$__v3189643886_38_21), 8, true, $__.uid);
    names['push'](key);
    'foldr' in vvv_tmp0;
    key = 'foldr';
    vvv_tmp10 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp10, (TAJS_restrictToType(obj[key, 'foldr'], 'function'), $__.fs.J$__v3189643886_38_23), 9, true, $__.uid);
    names['push'](key);
    'reduceRight' in vvv_tmp0;
    key = 'reduceRight';
    vvv_tmp11 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp11, (TAJS_restrictToType(obj[key, 'reduceRight'], 'function'), $__.fs.J$__v3189643886_38_23), 10, true, $__.uid);
    names['push'](key);
    'detect' in vvv_tmp0;
    key = 'detect';
    vvv_tmp12 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp12, (TAJS_restrictToType(obj[key, 'detect'], 'function'), $__.fs.J$__v3189643886_41_24), 11, true, $__.uid);
    names['push'](key);
    'find' in vvv_tmp0;
    key = 'find';
    vvv_tmp13 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp13, (TAJS_restrictToType(obj[key, 'find'], 'function'), $__.fs.J$__v3189643886_41_24), 12, true, $__.uid);
    names['push'](key);
    'select' in vvv_tmp0;
    key = 'select';
    vvv_tmp14 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp14, (TAJS_restrictToType(obj[key, 'select'], 'function'), $__.fs.J$__v3189643886_45_25), 13, true, $__.uid);
    names['push'](key);
    'filter' in vvv_tmp0;
    key = 'filter';
    vvv_tmp15 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp15, (TAJS_restrictToType(obj[key, 'filter'], 'function'), $__.fs.J$__v3189643886_45_25), 14, true, $__.uid);
    names['push'](key);
    'reject' in vvv_tmp0;
    key = 'reject';
    vvv_tmp16 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp16, (TAJS_restrictToType(obj[key, 'reject'], 'function'), $__.fs.J$__v3189643886_47_26), 15, true, $__.uid);
    names['push'](key);
    'all' in vvv_tmp0;
    key = 'all';
    vvv_tmp17 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp17, (TAJS_restrictToType(obj[key, 'all'], 'function'), $__.fs.J$__v3189643886_49_27), 16, true, $__.uid);
    names['push'](key);
    'every' in vvv_tmp0;
    key = 'every';
    vvv_tmp18 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp18, (TAJS_restrictToType(obj[key, 'every'], 'function'), $__.fs.J$__v3189643886_49_27), 17, true, $__.uid);
    names['push'](key);
    'any' in vvv_tmp0;
    key = 'any';
    vvv_tmp19 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp19, (TAJS_restrictToType(obj[key, 'any'], 'function'), $__.fs.J$__v3189643886_51_28), 18, true, $__.uid);
    names['push'](key);
    'some' in vvv_tmp0;
    key = 'some';
    vvv_tmp20 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp20, (TAJS_restrictToType(obj[key, 'some'], 'function'), $__.fs.J$__v3189643886_51_28), 19, true, $__.uid);
    names['push'](key);
    'include' in vvv_tmp0;
    key = 'include';
    vvv_tmp21 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp21, (TAJS_restrictToType(obj[key, 'include'], 'function'), $__.fs.J$__v3189643886_53_29), 20, true, $__.uid);
    names['push'](key);
    'includes' in vvv_tmp0;
    key = 'includes';
    vvv_tmp22 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp22, (TAJS_restrictToType(obj[key, 'includes'], 'function'), $__.fs.J$__v3189643886_53_29), 21, true, $__.uid);
    names['push'](key);
    'contains' in vvv_tmp0;
    key = 'contains';
    vvv_tmp23 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp23, (TAJS_restrictToType(obj[key, 'contains'], 'function'), $__.fs.J$__v3189643886_53_29), 22, true, $__.uid);
    names['push'](key);
    'invoke' in vvv_tmp0;
    key = 'invoke';
    vvv_tmp24 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp24, (TAJS_restrictToType(obj[key, 'invoke'], 'function'), $__.fs.J$__v3189643886_57_30), 23, true, $__.uid);
    names['push'](key);
    'pluck' in vvv_tmp0;
    key = 'pluck';
    vvv_tmp25 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp25, (TAJS_restrictToType(obj[key, 'pluck'], 'function'), $__.fs.J$__v3189643886_59_31), 24, true, $__.uid);
    names['push'](key);
    'where' in vvv_tmp0;
    key = 'where';
    vvv_tmp26 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp26, (TAJS_restrictToType(obj[key, 'where'], 'function'), $__.fs.J$__v3189643886_61_32), 25, true, $__.uid);
    names['push'](key);
    'findWhere' in vvv_tmp0;
    key = 'findWhere';
    vvv_tmp27 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp27, (TAJS_restrictToType(obj[key, 'findWhere'], 'function'), $__.fs.J$__v3189643886_63_33), 26, true, $__.uid);
    names['push'](key);
    'max' in vvv_tmp0;
    key = 'max';
    vvv_tmp28 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp28, (TAJS_restrictToType(obj[key, 'max'], 'function'), $__.fs.J$__v3189643886_67_34), 27, true, $__.uid);
    names['push'](key);
    'min' in vvv_tmp0;
    key = 'min';
    vvv_tmp29 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp29, (TAJS_restrictToType(obj[key, 'min'], 'function'), $__.fs.J$__v3189643886_71_35), 28, true, $__.uid);
    names['push'](key);
    'shuffle' in vvv_tmp0;
    key = 'shuffle';
    vvv_tmp30 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp30, (TAJS_restrictToType(obj[key, 'shuffle'], 'function'), $__.fs.J$__v3189643886_73_36), 29, true, $__.uid);
    names['push'](key);
    'sample' in vvv_tmp0;
    key = 'sample';
    vvv_tmp31 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp31, (TAJS_restrictToType(obj[key, 'sample'], 'function'), $__.fs.J$__v3189643886_75_37), 30, true, $__.uid);
    names['push'](key);
    'sortBy' in vvv_tmp0;
    key = 'sortBy';
    vvv_tmp32 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp32, (TAJS_restrictToType(obj[key, 'sortBy'], 'function'), $__.fs.J$__v3189643886_81_38), 31, true, $__.uid);
    names['push'](key);
    'groupBy' in vvv_tmp0;
    key = 'groupBy';
    vvv_tmp33 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp33, (TAJS_restrictToType(obj[key, 'groupBy'], 'function'), $__.fs.J$__v3189643886_85_41), 32, true, $__.uid);
    names['push'](key);
    'indexBy' in vvv_tmp0;
    key = 'indexBy';
    vvv_tmp34 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp34, (TAJS_restrictToType(obj[key, 'indexBy'], 'function'), $__.fs.J$__v3189643886_85_43), 33, true, $__.uid);
    names['push'](key);
    'countBy' in vvv_tmp0;
    key = 'countBy';
    vvv_tmp35 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp35, (TAJS_restrictToType(obj[key, 'countBy'], 'function'), $__.fs.J$__v3189643886_85_45), 34, true, $__.uid);
    names['push'](key);
    'toArray' in vvv_tmp0;
    key = 'toArray';
    vvv_tmp36 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp36, (TAJS_restrictToType(obj[key, 'toArray'], 'function'), $__.fs.J$__v3189643886_95_46), 35, true, $__.uid);
    names['push'](key);
    'size' in vvv_tmp0;
    key = 'size';
    vvv_tmp37 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp37, (TAJS_restrictToType(obj[key, 'size'], 'function'), $__.fs.J$__v3189643886_97_47), 36, true, $__.uid);
    names['push'](key);
    'partition' in vvv_tmp0;
    key = 'partition';
    vvv_tmp38 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp38, (TAJS_restrictToType(obj[key, 'partition'], 'function'), $__.fs.J$__v3189643886_101_48), 37, true, $__.uid);
    names['push'](key);
    'take' in vvv_tmp0;
    key = 'take';
    vvv_tmp39 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp39, (TAJS_restrictToType(obj[key, 'take'], 'function'), $__.fs.J$__v3189643886_103_49), 38, true, $__.uid);
    names['push'](key);
    'head' in vvv_tmp0;
    key = 'head';
    vvv_tmp40 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp40, (TAJS_restrictToType(obj[key, 'head'], 'function'), $__.fs.J$__v3189643886_103_49), 39, true, $__.uid);
    names['push'](key);
    'first' in vvv_tmp0;
    key = 'first';
    vvv_tmp41 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp41, (TAJS_restrictToType(obj[key, 'first'], 'function'), $__.fs.J$__v3189643886_103_49), 40, true, $__.uid);
    names['push'](key);
    'initial' in vvv_tmp0;
    key = 'initial';
    vvv_tmp42 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp42, (TAJS_restrictToType(obj[key, 'initial'], 'function'), $__.fs.J$__v3189643886_105_50), 41, true, $__.uid);
    names['push'](key);
    'last' in vvv_tmp0;
    key = 'last';
    vvv_tmp43 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp43, (TAJS_restrictToType(obj[key, 'last'], 'function'), $__.fs.J$__v3189643886_107_51), 42, true, $__.uid);
    names['push'](key);
    'drop' in vvv_tmp0;
    key = 'drop';
    vvv_tmp44 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp44, (TAJS_restrictToType(obj[key, 'drop'], 'function'), $__.fs.J$__v3189643886_109_52), 43, true, $__.uid);
    names['push'](key);
    'tail' in vvv_tmp0;
    key = 'tail';
    vvv_tmp45 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp45, (TAJS_restrictToType(obj[key, 'tail'], 'function'), $__.fs.J$__v3189643886_109_52), 44, true, $__.uid);
    names['push'](key);
    'rest' in vvv_tmp0;
    key = 'rest';
    vvv_tmp46 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp46, (TAJS_restrictToType(obj[key, 'rest'], 'function'), $__.fs.J$__v3189643886_109_52), 45, true, $__.uid);
    names['push'](key);
    'compact' in vvv_tmp0;
    key = 'compact';
    vvv_tmp47 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp47, (TAJS_restrictToType(obj[key, 'compact'], 'function'), $__.fs.J$__v3189643886_111_53), 46, true, $__.uid);
    names['push'](key);
    'flatten' in vvv_tmp0;
    key = 'flatten';
    vvv_tmp48 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp48, (TAJS_restrictToType(obj[key, 'flatten'], 'function'), $__.fs.J$__v3189643886_115_55), 47, true, $__.uid);
    names['push'](key);
    'without' in vvv_tmp0;
    key = 'without';
    vvv_tmp49 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp49, (TAJS_restrictToType(obj[key, 'without'], 'function'), $__.fs.J$__v3189643886_117_56), 48, true, $__.uid);
    names['push'](key);
    'unique' in vvv_tmp0;
    key = 'unique';
    vvv_tmp50 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp50, (TAJS_restrictToType(obj[key, 'unique'], 'function'), $__.fs.J$__v3189643886_119_57), 49, true, $__.uid);
    names['push'](key);
    'uniq' in vvv_tmp0;
    key = 'uniq';
    vvv_tmp51 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp51, (TAJS_restrictToType(obj[key, 'uniq'], 'function'), $__.fs.J$__v3189643886_119_57), 50, true, $__.uid);
    names['push'](key);
    'union' in vvv_tmp0;
    key = 'union';
    vvv_tmp52 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp52, (TAJS_restrictToType(obj[key, 'union'], 'function'), $__.fs.J$__v3189643886_121_58), 51, true, $__.uid);
    names['push'](key);
    'intersection' in vvv_tmp0;
    key = 'intersection';
    vvv_tmp53 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp53, (TAJS_restrictToType(obj[key, 'intersection'], 'function'), $__.fs.J$__v3189643886_123_59), 52, true, $__.uid);
    names['push'](key);
    'difference' in vvv_tmp0;
    key = 'difference';
    vvv_tmp54 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp54, (TAJS_restrictToType(obj[key, 'difference'], 'function'), $__.fs.J$__v3189643886_127_60), 53, true, $__.uid);
    names['push'](key);
    'zip' in vvv_tmp0;
    key = 'zip';
    vvv_tmp55 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp55, (TAJS_restrictToType(obj[key, 'zip'], 'function'), $__.fs.J$__v3189643886_129_61), 54, true, $__.uid);
    names['push'](key);
    'unzip' in vvv_tmp0;
    key = 'unzip';
    vvv_tmp56 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp56, (TAJS_restrictToType(obj[key, 'unzip'], 'function'), $__.fs.J$__v3189643886_131_62), 55, true, $__.uid);
    names['push'](key);
    'object' in vvv_tmp0;
    key = 'object';
    vvv_tmp57 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp57, (TAJS_restrictToType(obj[key, 'object'], 'function'), $__.fs.J$__v3189643886_133_63), 56, true, $__.uid);
    names['push'](key);
    'findIndex' in vvv_tmp0;
    key = 'findIndex';
    vvv_tmp58 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp58, (TAJS_restrictToType(obj[key, 'findIndex'], 'function'), $__.fs.J$__v3189643886_135_64), 57, true, $__.uid);
    names['push'](key);
    'findLastIndex' in vvv_tmp0;
    key = 'findLastIndex';
    vvv_tmp59 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp59, (TAJS_restrictToType(obj[key, 'findLastIndex'], 'function'), $__.fs.J$__v3189643886_135_65), 58, true, $__.uid);
    names['push'](key);
    'sortedIndex' in vvv_tmp0;
    key = 'sortedIndex';
    vvv_tmp60 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp60, (TAJS_restrictToType(obj[key, 'sortedIndex'], 'function'), $__.fs.J$__v3189643886_138_66), 59, true, $__.uid);
    names['push'](key);
    'indexOf' in vvv_tmp0;
    key = 'indexOf';
    vvv_tmp61 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp61, (TAJS_restrictToType(obj[key, 'indexOf'], 'function'), $__.fs.J$__v3189643886_140_67), 60, true, $__.uid);
    names['push'](key);
    'lastIndexOf' in vvv_tmp0;
    key = 'lastIndexOf';
    vvv_tmp62 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp62, (TAJS_restrictToType(obj[key, 'lastIndexOf'], 'function'), $__.fs.J$__v3189643886_140_68), 61, true, $__.uid);
    names['push'](key);
    'range' in vvv_tmp0;
    key = 'range';
    vvv_tmp63 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp63, (TAJS_restrictToType(obj[key, 'range'], 'function'), $__.fs.J$__v3189643886_143_69), 62, true, $__.uid);
    names['push'](key);
    'bind' in vvv_tmp0;
    key = 'bind';
    vvv_tmp64 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp64, (TAJS_restrictToType(obj[key, 'bind'], 'function'), $__.fs.J$__v3189643886_149_71), 63, true, $__.uid);
    names['push'](key);
    'partial' in vvv_tmp0;
    key = 'partial';
    vvv_tmp65 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp65, (TAJS_restrictToType(obj[key, 'partial'], 'function'), $__.fs.J$__v3189643886_153_72), 64, true, $__.uid);
    names['push'](key);
    'bindAll' in vvv_tmp0;
    key = 'bindAll';
    vvv_tmp66 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp66, (TAJS_restrictToType(obj[key, 'bindAll'], 'function'), $__.fs.J$__v3189643886_155_73), 65, true, $__.uid);
    names['push'](key);
    'memoize' in vvv_tmp0;
    key = 'memoize';
    vvv_tmp67 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp67, (TAJS_restrictToType(obj[key, 'memoize'], 'function'), $__.fs.J$__v3189643886_159_74), 66, true, $__.uid);
    names['push'](key);
    'delay' in vvv_tmp0;
    key = 'delay';
    vvv_tmp68 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp68, (TAJS_restrictToType(obj[key, 'delay'], 'function'), $__.fs.J$__v3189643886_163_75), 67, true, $__.uid);
    names['push'](key);
    'defer' in vvv_tmp0;
    key = 'defer';
    vvv_tmp69 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp69, (TAJS_restrictToType(obj[key, 'defer'], 'function'), $__.fs.J$__v3189643886_151_76), 68, true, $__.uid);
    names['push'](key);
    'throttle' in vvv_tmp0;
    key = 'throttle';
    vvv_tmp70 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp70, (TAJS_restrictToType(obj[key, 'throttle'], 'function'), $__.fs.J$__v3189643886_169_77), 69, true, $__.uid);
    names['push'](key);
    'debounce' in vvv_tmp0;
    key = 'debounce';
    vvv_tmp71 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp71, (TAJS_restrictToType(obj[key, 'debounce'], 'function'), $__.fs.J$__v3189643886_175_78), 70, true, $__.uid);
    names['push'](key);
    'wrap' in vvv_tmp0;
    key = 'wrap';
    vvv_tmp72 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp72, (TAJS_restrictToType(obj[key, 'wrap'], 'function'), $__.fs.J$__v3189643886_177_79), 71, true, $__.uid);
    names['push'](key);
    'negate' in vvv_tmp0;
    key = 'negate';
    vvv_tmp73 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp73, (TAJS_restrictToType(obj[key, 'negate'], 'function'), $__.fs.J$__v3189643886_181_80), 72, true, $__.uid);
    names['push'](key);
    'compose' in vvv_tmp0;
    key = 'compose';
    vvv_tmp74 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp74, (TAJS_restrictToType(obj[key, 'compose'], 'function'), $__.fs.J$__v3189643886_185_81), 73, true, $__.uid);
    names['push'](key);
    'after' in vvv_tmp0;
    key = 'after';
    vvv_tmp75 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp75, (TAJS_restrictToType(obj[key, 'after'], 'function'), $__.fs.J$__v3189643886_189_82), 74, true, $__.uid);
    names['push'](key);
    'before' in vvv_tmp0;
    key = 'before';
    vvv_tmp76 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp76, (TAJS_restrictToType(obj[key, 'before'], 'function'), $__.fs.J$__v3189643886_193_83), 75, true, $__.uid);
    names['push'](key);
    'once' in vvv_tmp0;
    key = 'once';
    vvv_tmp77 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp77, (TAJS_restrictToType(obj[key, 'once'], 'function'), $__.fs.J$__v3189643886_151_84), 76, true, $__.uid);
    names['push'](key);
    'keys' in vvv_tmp0;
    key = 'keys';
    vvv_tmp78 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp78, (TAJS_restrictToType(obj[key, 'keys'], 'function'), $__.fs.J$__v3189643886_196_85), 77, true, $__.uid);
    names['push'](key);
    'allKeys' in vvv_tmp0;
    key = 'allKeys';
    vvv_tmp79 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp79, (TAJS_restrictToType(obj[key, 'allKeys'], 'function'), $__.fs.J$__v3189643886_198_86), 78, true, $__.uid);
    names['push'](key);
    'values' in vvv_tmp0;
    key = 'values';
    vvv_tmp80 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp80, (TAJS_restrictToType(obj[key, 'values'], 'function'), $__.fs.J$__v3189643886_200_87), 79, true, $__.uid);
    names['push'](key);
    'mapObject' in vvv_tmp0;
    key = 'mapObject';
    vvv_tmp81 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp81, (TAJS_restrictToType(obj[key, 'mapObject'], 'function'), $__.fs.J$__v3189643886_202_88), 80, true, $__.uid);
    names['push'](key);
    'pairs' in vvv_tmp0;
    key = 'pairs';
    vvv_tmp82 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp82, (TAJS_restrictToType(obj[key, 'pairs'], 'function'), $__.fs.J$__v3189643886_204_89), 81, true, $__.uid);
    names['push'](key);
    'invert' in vvv_tmp0;
    key = 'invert';
    vvv_tmp83 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp83, (TAJS_restrictToType(obj[key, 'invert'], 'function'), $__.fs.J$__v3189643886_206_90), 82, true, $__.uid);
    names['push'](key);
    'methods' in vvv_tmp0;
    key = 'methods';
    vvv_tmp84 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp84, (TAJS_restrictToType(obj[key, 'methods'], 'function'), $__.fs.J$__v3189643886_208_91), 83, true, $__.uid);
    names['push'](key);
    'functions' in vvv_tmp0;
    key = 'functions';
    vvv_tmp85 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp85, (TAJS_restrictToType(obj[key, 'functions'], 'function'), $__.fs.J$__v3189643886_208_91), 84, true, $__.uid);
    names['push'](key);
    'extend' in vvv_tmp0;
    key = 'extend';
    vvv_tmp86 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp86, (TAJS_restrictToType(obj[key, 'extend'], 'function'), $__.fs.J$__v3189643886_21_92), 85, true, $__.uid);
    names['push'](key);
    'assign' in vvv_tmp0;
    key = 'assign';
    vvv_tmp87 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp87, (TAJS_restrictToType(obj[key, 'assign'], 'function'), $__.fs.J$__v3189643886_21_93), 86, true, $__.uid);
    names['push'](key);
    'extendOwn' in vvv_tmp0;
    key = 'extendOwn';
    vvv_tmp88 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp88, (TAJS_restrictToType(obj[key, 'extendOwn'], 'function'), $__.fs.J$__v3189643886_21_93), 87, true, $__.uid);
    names['push'](key);
    'findKey' in vvv_tmp0;
    key = 'findKey';
    vvv_tmp89 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp89, (TAJS_restrictToType(obj[key, 'findKey'], 'function'), $__.fs.J$__v3189643886_210_94), 88, true, $__.uid);
    names['push'](key);
    'pick' in vvv_tmp0;
    key = 'pick';
    vvv_tmp90 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp90, (TAJS_restrictToType(obj[key, 'pick'], 'function'), $__.fs.J$__v3189643886_214_95), 89, true, $__.uid);
    names['push'](key);
    'omit' in vvv_tmp0;
    key = 'omit';
    vvv_tmp91 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp91, (TAJS_restrictToType(obj[key, 'omit'], 'function'), $__.fs.J$__v3189643886_218_96), 90, true, $__.uid);
    names['push'](key);
    'defaults' in vvv_tmp0;
    key = 'defaults';
    vvv_tmp92 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp92, (TAJS_restrictToType(obj[key, 'defaults'], 'function'), $__.fs.J$__v3189643886_21_97), 91, true, $__.uid);
    names['push'](key);
    'create' in vvv_tmp0;
    key = 'create';
    vvv_tmp93 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp93, (TAJS_restrictToType(obj[key, 'create'], 'function'), $__.fs.J$__v3189643886_220_98), 92, true, $__.uid);
    names['push'](key);
    'clone' in vvv_tmp0;
    key = 'clone';
    vvv_tmp94 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp94, (TAJS_restrictToType(obj[key, 'clone'], 'function'), $__.fs.J$__v3189643886_222_99), 93, true, $__.uid);
    names['push'](key);
    'tap' in vvv_tmp0;
    key = 'tap';
    vvv_tmp95 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp95, (TAJS_restrictToType(obj[key, 'tap'], 'function'), $__.fs.J$__v3189643886_224_100), 94, true, $__.uid);
    names['push'](key);
    'isMatch' in vvv_tmp0;
    key = 'isMatch';
    vvv_tmp96 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp96, (TAJS_restrictToType(obj[key, 'isMatch'], 'function'), $__.fs.J$__v3189643886_226_101), 95, true, $__.uid);
    names['push'](key);
    'isEqual' in vvv_tmp0;
    key = 'isEqual';
    vvv_tmp97 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp97, (TAJS_restrictToType(obj[key, 'isEqual'], 'function'), $__.fs.J$__v3189643886_230_103), 96, true, $__.uid);
    names['push'](key);
    'isEmpty' in vvv_tmp0;
    key = 'isEmpty';
    vvv_tmp98 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp98, (TAJS_restrictToType(obj[key, 'isEmpty'], 'function'), $__.fs.J$__v3189643886_232_104), 97, true, $__.uid);
    names['push'](key);
    'isElement' in vvv_tmp0;
    key = 'isElement';
    vvv_tmp99 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp99, (TAJS_restrictToType(obj[key, 'isElement'], 'function'), $__.fs.J$__v3189643886_234_105), 98, true, $__.uid);
    names['push'](key);
    'isArray' in vvv_tmp0;
    key = 'isArray';
    vvv_tmp100 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp100, TAJS_restrictToType(obj[key, 'isArray'], 'function'), 99, true, $__.uid);
    names['push'](key);
    'isObject' in vvv_tmp0;
    key = 'isObject';
    vvv_tmp101 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp101, (TAJS_restrictToType(obj[key, 'isObject'], 'function'), $__.fs.J$__v3189643886_238_106), 100, true, $__.uid);
    names['push'](key);
    'isArguments' in vvv_tmp0;
    key = 'isArguments';
    vvv_tmp102 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp102, (TAJS_restrictToType(obj[key, 'isArguments'], 'function'), $__.fs.J$__v3189643886_240_108), 101, true, $__.uid);
    names['push'](key);
    'isFunction' in vvv_tmp0;
    key = 'isFunction';
    vvv_tmp103 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp103, (TAJS_restrictToType(obj[key, 'isFunction'], 'function'), $__.fs.J$__v3189643886_246_115), 102, true, $__.uid);
    names['push'](key);
    'isString' in vvv_tmp0;
    key = 'isString';
    vvv_tmp104 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp104, (TAJS_restrictToType(obj[key, 'isString'], 'function'), $__.fs.J$__v3189643886_240_110), 103, true, $__.uid);
    names['push'](key);
    'isNumber' in vvv_tmp0;
    key = 'isNumber';
    vvv_tmp105 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp105, (TAJS_restrictToType(obj[key, 'isNumber'], 'function'), $__.fs.J$__v3189643886_240_111), 104, true, $__.uid);
    names['push'](key);
    'isDate' in vvv_tmp0;
    key = 'isDate';
    vvv_tmp106 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp106, (TAJS_restrictToType(obj[key, 'isDate'], 'function'), $__.fs.J$__v3189643886_240_112), 105, true, $__.uid);
    names['push'](key);
    'isRegExp' in vvv_tmp0;
    key = 'isRegExp';
    vvv_tmp107 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp107, (TAJS_restrictToType(obj[key, 'isRegExp'], 'function'), $__.fs.J$__v3189643886_240_113), 106, true, $__.uid);
    names['push'](key);
    'isError' in vvv_tmp0;
    key = 'isError';
    vvv_tmp108 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp108, (TAJS_restrictToType(obj[key, 'isError'], 'function'), $__.fs.J$__v3189643886_240_114), 107, true, $__.uid);
    names['push'](key);
    'isFinite' in vvv_tmp0;
    key = 'isFinite';
    vvv_tmp109 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp109, (TAJS_restrictToType(obj[key, 'isFinite'], 'function'), $__.fs.J$__v3189643886_248_116), 108, true, $__.uid);
    names['push'](key);
    'isNaN' in vvv_tmp0;
    key = 'isNaN';
    vvv_tmp110 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp110, (TAJS_restrictToType(obj[key, 'isNaN'], 'function'), $__.fs.J$__v3189643886_250_117), 109, true, $__.uid);
    names['push'](key);
    'isBoolean' in vvv_tmp0;
    key = 'isBoolean';
    vvv_tmp111 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp111, (TAJS_restrictToType(obj[key, 'isBoolean'], 'function'), $__.fs.J$__v3189643886_252_118), 110, true, $__.uid);
    names['push'](key);
    'isNull' in vvv_tmp0;
    key = 'isNull';
    vvv_tmp112 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp112, (TAJS_restrictToType(obj[key, 'isNull'], 'function'), $__.fs.J$__v3189643886_254_119), 111, true, $__.uid);
    names['push'](key);
    'isUndefined' in vvv_tmp0;
    key = 'isUndefined';
    vvv_tmp113 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp113, (TAJS_restrictToType(obj[key, 'isUndefined'], 'function'), $__.fs.J$__v3189643886_256_120), 112, true, $__.uid);
    names['push'](key);
    'has' in vvv_tmp0;
    key = 'has';
    vvv_tmp114 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp114, (TAJS_restrictToType(obj[key, 'has'], 'function'), $__.fs.J$__v3189643886_258_121), 113, true, $__.uid);
    names['push'](key);
    'noConflict' in vvv_tmp0;
    key = 'noConflict';
    vvv_tmp115 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp115, (TAJS_restrictToType(obj[key, 'noConflict'], 'function'), $__.fs.J$__v3189643886_260_122), 114, true, $__.uid);
    names['push'](key);
    'identity' in vvv_tmp0;
    key = 'identity';
    vvv_tmp116 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp116, (TAJS_restrictToType(obj[key, 'identity'], 'function'), $__.fs.J$__v3189643886_262_123), 115, true, $__.uid);
    names['push'](key);
    'constant' in vvv_tmp0;
    key = 'constant';
    vvv_tmp117 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp117, (TAJS_restrictToType(obj[key, 'constant'], 'function'), $__.fs.J$__v3189643886_266_124), 116, true, $__.uid);
    names['push'](key);
    'noop' in vvv_tmp0;
    key = 'noop';
    vvv_tmp118 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp118, (TAJS_restrictToType(obj[key, 'noop'], 'function'), $__.fs.J$__v3189643886_268_125), 117, true, $__.uid);
    names['push'](key);
    'property' in vvv_tmp0;
    key = 'property';
    vvv_tmp119 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp119, (TAJS_restrictToType(obj[key, 'property'], 'function'), $__.fs.J$__v3189643886_29_15), 118, true, $__.uid);
    names['push'](key);
    'propertyOf' in vvv_tmp0;
    key = 'propertyOf';
    vvv_tmp120 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp120, (TAJS_restrictToType(obj[key, 'propertyOf'], 'function'), $__.fs.J$__v3189643886_274_126), 119, true, $__.uid);
    names['push'](key);
    'matches' in vvv_tmp0;
    key = 'matches';
    vvv_tmp121 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp121, (TAJS_restrictToType(obj[key, 'matches'], 'function'), $__.fs.J$__v3189643886_278_127), 120, true, $__.uid);
    names['push'](key);
    'matcher' in vvv_tmp0;
    key = 'matcher';
    vvv_tmp122 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp122, (TAJS_restrictToType(obj[key, 'matcher'], 'function'), $__.fs.J$__v3189643886_278_127), 121, true, $__.uid);
    names['push'](key);
    'times' in vvv_tmp0;
    key = 'times';
    vvv_tmp123 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp123, (TAJS_restrictToType(obj[key, 'times'], 'function'), $__.fs.J$__v3189643886_280_128), 122, true, $__.uid);
    names['push'](key);
    'random' in vvv_tmp0;
    key = 'random';
    vvv_tmp124 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp124, (TAJS_restrictToType(obj[key, 'random'], 'function'), $__.fs.J$__v3189643886_282_129), 123, true, $__.uid);
    names['push'](key);
    'now' in vvv_tmp0;
    key = 'now';
    vvv_tmp125 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp125, TAJS_restrictToType(obj[key, 'now'], 'function'), 124, true, $__.uid);
    names['push'](key);
    'escape' in vvv_tmp0;
    key = 'escape';
    vvv_tmp126 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp126, (TAJS_restrictToType(obj[key, 'escape'], 'function'), $__.fs.J$__v3189643886_288_132), 125, true, $__.uid);
    names['push'](key);
    'unescape' in vvv_tmp0;
    key = 'unescape';
    vvv_tmp127 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp127, (TAJS_restrictToType(obj[key, 'unescape'], 'function'), $__.fs.J$__v3189643886_288_134), 126, true, $__.uid);
    names['push'](key);
    'result' in vvv_tmp0;
    key = 'result';
    vvv_tmp128 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp128, (TAJS_restrictToType(obj[key, 'result'], 'function'), $__.fs.J$__v3189643886_292_135), 127, true, $__.uid);
    names['push'](key);
    'uniqueId' in vvv_tmp0;
    key = 'uniqueId';
    vvv_tmp129 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp129, (TAJS_restrictToType(obj[key, 'uniqueId'], 'function'), $__.fs.J$__v3189643886_294_136), 128, true, $__.uid);
    names['push'](key);
    'templateSettings' in vvv_tmp0;
    key = 'templateSettings';
    vvv_tmp130 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp130, (obj[key, 'templateSettings'], $__.os.oid3), 129, true, $__.uid);
    'template' in vvv_tmp0;
    key = 'template';
    vvv_tmp131 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp131, (TAJS_restrictToType(obj[key, 'template'], 'function'), $__.fs.J$__v3189643886_302_138), 130, true, $__.uid);
    names['push'](key);
    'chain' in vvv_tmp0;
    key = 'chain';
    vvv_tmp132 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp132, (TAJS_restrictToType(obj[key, 'chain'], 'function'), $__.fs.J$__v3189643886_304_139), 131, true, $__.uid);
    names['push'](key);
    'mixin' in vvv_tmp0;
    key = 'mixin';
    vvv_tmp133 = _, 'isFunction', $__.fs.J$__v3189643886_246_115.call(vvv_tmp133, (TAJS_restrictToType(obj[key, 'mixin'], 'function'), $__.fs.J$__v3189643886_312_141), 132, true, $__.uid);
    names['push'](key);
    return function $__lt5(res) {
        res.length = 131;
        if (res.length != 131)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt5, -2);
    }(names['sort']());
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
names = [];
for (var key in obj) {
    if (_.isFunction(obj[key]))
        names.push(key);
}
return names.sort();});
    _.extend = (createAssigner, $__.fs.J$__v3189643886_23_13)((TAJS_restrictToType(_.allKeys, 'function'), $__.fs.J$__v3189643886_198_86), 0, true, $__.uid);
    _.extendOwn = _.assign = (createAssigner, $__.fs.J$__v3189643886_23_13)((TAJS_restrictToType(_.keys, 'function'), $__.fs.J$__v3189643886_196_85), 1, true, $__.uid);
    _.findKey = ($__.fs.J$__v3189643886_210_94 = function J$__v3189643886_210(obj, predicate, context) {
var vvv_return, vvv_switch, keys, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], predicate = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
predicate = cb(predicate, context);
keys = _.keys(obj);
for (var i = 0, length = keys.length; i < length; i++) {
    key = keys[i];
    if (predicate(obj[key], key, obj))
        return key;
}});
    _.pick = ($__.fs.J$__v3189643886_214_95 = function J$__v3189643886_214(object, oiteratee, context) {
var vvv_return, vvv_switch, result, obj, iteratee, keys, key, value;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
object = arguments[0], oiteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = {};
obj = object;
if (obj == null)
    return result;
if (_.isFunction(oiteratee)) {
    keys = _.allKeys(obj);
    iteratee = optimizeCb(oiteratee, context);
} else {
    keys = flatten(arguments, false, false, 1);
    iteratee = function (value, key, obj) {
        return key in obj;
    };
    obj = Object(obj);
}
for (var i = 0, length = keys.length; i < length; i++) {
    key = keys[i];
    value = obj[key];
    if (iteratee(value, key, obj))
        result[key] = value;
}
return result;});
    _.omit = ($__.fs.J$__v3189643886_218_96 = function J$__v3189643886_218(obj, iteratee, context) {
var vvv_return, vvv_switch, keys;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (_.isFunction(iteratee)) {
    iteratee = _.negate(iteratee);
} else {
    keys = _.map(flatten(arguments, false, false, 1), String);
    iteratee = function (value, key) {
        return !_.contains(keys, key);
    };
}
return _.pick(obj, iteratee, context);});
    _.defaults = (createAssigner, $__.fs.J$__v3189643886_23_13)((TAJS_restrictToType(_.allKeys, 'function'), $__.fs.J$__v3189643886_198_86), true, 2, true, $__.uid);
    _.create = ($__.fs.J$__v3189643886_220_98 = function J$__v3189643886_220(prototype, props) {
var vvv_return, vvv_switch, result;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prototype = arguments[0], props = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = baseCreate(prototype);
if (props)
    _.extendOwn(result, props);
return result;});
    _.clone = ($__.fs.J$__v3189643886_222_99 = function J$__v3189643886_222(obj) {
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
if (!_.isObject(obj))
    return obj;
return _.isArray(obj) ? obj.slice() : _.extend({}, obj);});
    _.tap = ($__.fs.J$__v3189643886_224_100 = function J$__v3189643886_224(obj, interceptor) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], interceptor = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
interceptor(obj);
return obj;});
    _.isMatch = ($__.fs.J$__v3189643886_226_101 = function J$__v3189643886_226(object, attrs) {
var vvv_return, vvv_switch, keys, length, obj, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
object = arguments[0], attrs = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
keys = _.keys(attrs);
length = keys.length;
if (object == null)
    return !length;
obj = Object(object);
for (var i = 0; i < length; i++) {
    key = keys[i];
    if (attrs[key] !== obj[key] || !(key in obj))
        return false;
}
return true;});
    eq = ($__.fs.J$__v3189643886_228_102 = function J$__v3189643886_228(a, b, aStack, bStack) {
var vvv_return, vvv_switch, className, areArrays, aCtor, bCtor, length, keys, key;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
a = arguments[0], b = arguments[1], aStack = arguments[2], bStack = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (a === b)
    return a !== 0 || 1 / a === 1 / b;
if (a == null || b == null)
    return a === b;
if (a instanceof _)
    a = a._wrapped;
if (b instanceof _)
    b = b._wrapped;
className = toString.call(a);
if (className !== toString.call(b))
    return false;
switch (className) {
case '[object RegExp]':
case '[object String]':
    return '' + a === '' + b;
case '[object Number]':
    if (+a !== +a)
        return +b !== +b;
    return +a === 0 ? 1 / +a === 1 / b : +a === +b;
case '[object Date]':
case '[object Boolean]':
    return +a === +b;
}
areArrays = className === '[object Array]';
if (!areArrays) {
    if (typeof a != 'object' || typeof b != 'object')
        return false;
    aCtor = a.constructor;
    bCtor = b.constructor;
    if (aCtor !== bCtor && !(_.isFunction(aCtor) && aCtor instanceof aCtor && _.isFunction(bCtor) && bCtor instanceof bCtor) && ('constructor' in a && 'constructor' in b)) {
        return false;
    }
}
aStack = aStack || [];
bStack = bStack || [];
length = aStack.length;
while (length--) {
    if (aStack[length] === a)
        return bStack[length] === b;
}
aStack.push(a);
bStack.push(b);
if (areArrays) {
    length = a.length;
    if (length !== b.length)
        return false;
    while (length--) {
        if (!eq(a[length], b[length], aStack, bStack))
            return false;
    }
} else {
    keys = _.keys(a);
    length = keys.length;
    if (_.keys(b).length !== length)
        return false;
    while (length--) {
        key = keys[length];
        if (!(_.has(b, key) && eq(a[key], b[key], aStack, bStack)))
            return false;
    }
}
aStack.pop();
bStack.pop();
return true;});
    _.isEqual = ($__.fs.J$__v3189643886_230_103 = function J$__v3189643886_230(a, b) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
a = arguments[0], b = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return eq(a, b);});
    _.isEmpty = ($__.fs.J$__v3189643886_232_104 = function J$__v3189643886_232(obj) {
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
    return true;
if (isArrayLike(obj) && (_.isArray(obj) || _.isString(obj) || _.isArguments(obj)))
    return obj.length === 0;
return _.keys(obj).length === 0;});
    _.isElement = ($__.fs.J$__v3189643886_234_105 = function J$__v3189643886_234(obj) {
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
return !!(obj && obj.nodeType === 1);});
    _.isArray = nativeIsArray;
    _.isObject = ($__.fs.J$__v3189643886_238_106 = function J$__v3189643886_238(obj) {
var vvv_return, vvv_switch, type;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
case 1:
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 2);
    type = typeof obj;
    return type === 'function', (type === 'object', !!obj);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
type = typeof obj;
return type === 'function' || type === 'object' && !!obj;});
    vvv_tmp2 = _, 'each', $__.fs.J$__v3189643886_33_18.call(vvv_tmp2, [
        'Arguments',
        'Function',
        'String',
        'Number',
        'Date',
        'RegExp',
        'Error'
    ], ($__.fs.J$__v3189643886_242_107 = function J$__v3189643886_242(name) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    _['is' + name, 'isArguments'] = ($__.fs.J$__v3189643886_240_108 = function J$__v3189643886_240(obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    return toString['call'](obj) === '[object ' + name + ']';
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    _['is' + name, 'isFunction'] = ($__.fs.J$__v3189643886_240_109 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    _['is' + name, 'isString'] = ($__.fs.J$__v3189643886_240_110 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    _['is' + name, 'isNumber'] = ($__.fs.J$__v3189643886_240_111 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    _['is' + name, 'isDate'] = ($__.fs.J$__v3189643886_240_112 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    _['is' + name, 'isRegExp'] = ($__.fs.J$__v3189643886_240_113 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    _['is' + name, 'isError'] = ($__.fs.J$__v3189643886_240_114 = function J$__v3189643886_240(obj) {
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
return toString.call(obj) === '[object ' + name + ']';});
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
_['is' + name] = function (obj) {
    return toString.call(obj) === '[object ' + name + ']';
};}), 0, true, $__.uid);
    !(vvv_tmp3 = _, 'isArguments', $__.fs.J$__v3189643886_240_108.call(vvv_tmp3, arguments, 0, true, $__.uid));
    typeof /./ != 'function', typeof Int8Array != 'object';
    _.isFunction = ($__.fs.J$__v3189643886_246_115 = function J$__v3189643886_246(obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
case 129:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 129);
    return typeof obj == 'function', false;
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
case 8:
case 9:
case 10:
case 11:
case 12:
case 13:
case 14:
case 15:
case 16:
case 17:
case 18:
case 19:
case 20:
case 21:
case 22:
case 23:
case 24:
case 25:
case 26:
case 27:
case 28:
case 29:
case 30:
case 31:
case 32:
case 33:
case 34:
case 35:
case 36:
case 37:
case 38:
case 39:
case 40:
case 41:
case 42:
case 43:
case 44:
case 45:
case 46:
case 47:
case 48:
case 49:
case 50:
case 51:
case 52:
case 53:
case 54:
case 55:
case 56:
case 57:
case 58:
case 59:
case 60:
case 61:
case 62:
case 63:
case 64:
case 65:
case 66:
case 67:
case 68:
case 69:
case 70:
case 71:
case 72:
case 73:
case 74:
case 75:
case 76:
case 77:
case 78:
case 79:
case 80:
case 81:
case 82:
case 83:
case 84:
case 85:
case 86:
case 87:
case 88:
case 89:
case 90:
case 91:
case 92:
case 93:
case 94:
case 95:
case 96:
case 97:
case 98:
case 99:
case 100:
case 101:
case 102:
case 103:
case 104:
case 105:
case 106:
case 107:
case 108:
case 109:
case 110:
case 111:
case 112:
case 113:
case 114:
case 115:
case 116:
case 117:
case 118:
case 119:
case 120:
case 121:
case 122:
case 123:
case 124:
case 125:
case 126:
case 127:
case 128:
case 130:
case 131:
case 132:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(1 <= vvv_switch && vvv_switch <= 132);
    return typeof obj == 'function';
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return typeof obj == 'function' || false;});
    _.isFinite = ($__.fs.J$__v3189643886_248_116 = function J$__v3189643886_248(obj) {
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
return isFinite(obj) && !isNaN(parseFloat(obj));});
    _.isNaN = ($__.fs.J$__v3189643886_250_117 = function J$__v3189643886_250(obj) {
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
return _.isNumber(obj) && obj !== +obj;});
    _.isBoolean = ($__.fs.J$__v3189643886_252_118 = function J$__v3189643886_252(obj) {
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
return obj === true || obj === false || toString.call(obj) === '[object Boolean]';});
    _.isNull = ($__.fs.J$__v3189643886_254_119 = function J$__v3189643886_254(obj) {
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
return obj === null;});
    _.isUndefined = ($__.fs.J$__v3189643886_256_120 = function J$__v3189643886_256(obj) {
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
return obj === void 0;});
    _.has = ($__.fs.J$__v3189643886_258_121 = function J$__v3189643886_258(obj, key) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], key = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return obj != null && hasOwnProperty.call(obj, key);});
    _.noConflict = ($__.fs.J$__v3189643886_260_122 = function J$__v3189643886_260() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
root._ = previousUnderscore;
return this;});
    _.identity = ($__.fs.J$__v3189643886_262_123 = function J$__v3189643886_262(value) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return value;});
    _.constant = ($__.fs.J$__v3189643886_266_124 = function J$__v3189643886_266(value) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
value = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    return value;
};});
    _.noop = ($__.fs.J$__v3189643886_268_125 = function J$__v3189643886_268() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');});
    _.property = property;
    _.propertyOf = ($__.fs.J$__v3189643886_274_126 = function J$__v3189643886_274(obj) {
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
return obj == null ? function () {
} : function (key) {
    return obj[key];
};});
    _.matcher = _.matches = ($__.fs.J$__v3189643886_278_127 = function J$__v3189643886_278(attrs) {
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
attrs = _.extendOwn({}, attrs);
return function (obj) {
    return _.isMatch(obj, attrs);
};});
    _.times = ($__.fs.J$__v3189643886_280_128 = function J$__v3189643886_280(n, iteratee, context) {
var vvv_return, vvv_switch, accum;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
n = arguments[0], iteratee = arguments[1], context = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
accum = Array(Math.max(0, n));
iteratee = optimizeCb(iteratee, context, 1);
for (var i = 0; i < n; i++)
    accum[i] = iteratee(i);
return accum;});
    _.random = ($__.fs.J$__v3189643886_282_129 = function J$__v3189643886_282(min, max) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
min = arguments[0], max = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (max == null) {
    max = min;
    min = 0;
}
return min + Math.floor(Math.random() * (max - min + 1));});
    _.now = TAJS_restrictToType(Date.now, 'function');
    escapeMap = $__.os.oid1 = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        '\'': '&#x27;',
        '`': '&#x60;'
    };
    unescapeMap = (vvv_tmp4 = _, 'invert', $__.fs.J$__v3189643886_206_90.call(vvv_tmp4, escapeMap, 0, true, $__.uid));
    createEscaper = ($__.fs.J$__v3189643886_290_130 = function J$__v3189643886_290(map) {
var vvv_return, vvv_switch, vvv_tmp0, escaper, source, testRegexp, replaceRegexp;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
map = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    escaper = ($__.fs.J$__v3189643886_286_131 = function J$__v3189643886_286(match) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
match = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return map[match];});
    source = '(?:' + (vvv_tmp0 = _, 'keys', $__.fs.J$__v3189643886_196_85.call(vvv_tmp0, map, 1, true, $__.uid))['join']('|') + ')';
    testRegexp = RegExp(source);
    replaceRegexp = RegExp(source, 'g');
    return ($__.fs.J$__v3189643886_288_132 = function J$__v3189643886_288(string) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
string = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
string = string == null ? '' : '' + string;
return testRegexp.test(string) ? string.replace(replaceRegexp, escaper) : string;});
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    escaper = ($__.fs.J$__v3189643886_286_133 = function J$__v3189643886_286(match) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
match = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return map[match];});
    source = '(?:' + (vvv_tmp0 = _, 'keys', $__.fs.J$__v3189643886_196_85.call(vvv_tmp0, map, 2, true, $__.uid))['join']('|') + ')';
    testRegexp = RegExp(source);
    replaceRegexp = RegExp(source, 'g');
    return ($__.fs.J$__v3189643886_288_134 = function J$__v3189643886_288(string) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
string = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
string = string == null ? '' : '' + string;
return testRegexp.test(string) ? string.replace(replaceRegexp, escaper) : string;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
escaper = function (match) {
    return map[match];
};
source = '(?:' + _.keys(map).join('|') + ')';
testRegexp = RegExp(source);
replaceRegexp = RegExp(source, 'g');
return function (string) {
    string = string == null ? '' : '' + string;
    return testRegexp.test(string) ? string.replace(replaceRegexp, escaper) : string;
};});
    _.escape = (createEscaper, $__.fs.J$__v3189643886_290_130)(escapeMap, 0, true, $__.uid);
    _.unescape = (createEscaper, $__.fs.J$__v3189643886_290_130)(unescapeMap, 1, true, $__.uid);
    _.result = ($__.fs.J$__v3189643886_292_135 = function J$__v3189643886_292(object, property, fallback) {
var vvv_return, vvv_switch, value;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
object = arguments[0], property = arguments[1], fallback = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
value = object == null ? void 0 : object[property];
if (value === void 0) {
    value = fallback;
}
return _.isFunction(value) ? value.call(object) : value;});
    idCounter = 0;
    _.uniqueId = ($__.fs.J$__v3189643886_294_136 = function J$__v3189643886_294(prefix) {
var vvv_return, vvv_switch, id;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prefix = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
id = ++idCounter + '';
return prefix ? prefix + id : id;});
    _.templateSettings = $__.os.oid3 = {
        'evaluate': /<%([\s\S]+?)%>/g,
        'interpolate': /<%=([\s\S]+?)%>/g,
        'escape': /<%-([\s\S]+?)%>/g
    };
    noMatch = /(.)^/;
    escapes = $__.os.oid4 = {
        '\'': '\'',
        '\\': '\\',
        '\r': 'r',
        '\n': 'n',
        '\u2028': 'u2028',
        '\u2029': 'u2029'
    };
    escaper = /\\|'|\r|\n|\u2028|\u2029/g;
    escapeChar = ($__.fs.J$__v3189643886_296_137 = function J$__v3189643886_296(match) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
match = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return '\\' + escapes[match];});
    _.template = ($__.fs.J$__v3189643886_302_138 = function J$__v3189643886_302(text, settings, oldSettings) {
var vvv_return, vvv_switch, matcher, index, source, render, template, argument;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
text = arguments[0], settings = arguments[1], oldSettings = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!settings && oldSettings)
    settings = oldSettings;
settings = _.defaults({}, settings, _.templateSettings);
matcher = RegExp([
    (settings.escape || noMatch).source,
    (settings.interpolate || noMatch).source,
    (settings.evaluate || noMatch).source
].join('|') + '|$', 'g');
index = 0;
source = '__p+=\'';
text.replace(matcher, function (match, escape, interpolate, evaluate, offset) {
    source += text.slice(index, offset).replace(escaper, escapeChar);
    index = offset + match.length;
    if (escape) {
        source += '\'+\n((__t=(' + escape + '))==null?\'\':_.escape(__t))+\n\'';
    } else if (interpolate) {
        source += '\'+\n((__t=(' + interpolate + '))==null?\'\':__t)+\n\'';
    } else if (evaluate) {
        source += '\';\n' + evaluate + '\n__p+=\'';
    }
    return match;
});
source += '\';\n';
if (!settings.variable)
    source = 'with(obj||{}){\n' + source + '}\n';
source = 'var __t,__p=\'\',__j=Array.prototype.join,' + 'print=function(){__p+=__j.call(arguments,\'\');};\n' + source + 'return __p;\n';
try {
    render = new Function(settings.variable || 'obj', '_', source);
} catch (e) {
    e.source = source;
    throw e;
}
template = function (data) {
    return render.call(this, data, _);
};
argument = settings.variable || 'obj';
template.source = 'function(' + argument + '){\n' + source + '}';
return template;});
    _.chain = ($__.fs.J$__v3189643886_304_139 = function J$__v3189643886_304(obj) {
var vvv_return, vvv_switch, instance;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
instance = _(obj);
instance._chain = true;
return instance;});
    result = ($__.fs.J$__v3189643886_306_140 = function J$__v3189643886_306(instance, obj) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
instance = arguments[0], obj = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return instance._chain ? _(obj).chain() : obj;});
    _.mixin = ($__.fs.J$__v3189643886_312_141 = function J$__v3189643886_312(obj) {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    vvv_tmp1 = _, 'each', $__.fs.J$__v3189643886_33_18.call(vvv_tmp1, (vvv_tmp0 = _, 'functions', $__.fs.J$__v3189643886_208_91.call(vvv_tmp0, (obj, $__.fs.J$__v3189643886_3_9), 0, true, $__.uid)), ($__.fs.J$__v3189643886_310_142 = function J$__v3189643886_310(name) {
var vvv_return, vvv_switch, func;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    func = _[name, 'after'] = TAJS_restrictToType(obj[name, 'after'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'after'] = ($__.fs.J$__v3189643886_308_143 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    func = _[name, 'all'] = TAJS_restrictToType(obj[name, 'all'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'all'] = ($__.fs.J$__v3189643886_308_144 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    func = _[name, 'allKeys'] = TAJS_restrictToType(obj[name, 'allKeys'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'allKeys'] = ($__.fs.J$__v3189643886_308_145 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    func = _[name, 'any'] = TAJS_restrictToType(obj[name, 'any'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'any'] = ($__.fs.J$__v3189643886_308_146 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    func = _[name, 'assign'] = TAJS_restrictToType(obj[name, 'assign'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'assign'] = ($__.fs.J$__v3189643886_308_147 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    func = _[name, 'before'] = TAJS_restrictToType(obj[name, 'before'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'before'] = ($__.fs.J$__v3189643886_308_148 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    func = _[name, 'bind'] = TAJS_restrictToType(obj[name, 'bind'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'bind'] = ($__.fs.J$__v3189643886_308_149 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 7:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 7);
    func = _[name, 'bindAll'] = TAJS_restrictToType(obj[name, 'bindAll'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'bindAll'] = ($__.fs.J$__v3189643886_308_150 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 8:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 8);
    func = _[name, 'chain'] = TAJS_restrictToType(obj[name, 'chain'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'chain'] = ($__.fs.J$__v3189643886_308_151 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 9:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 9);
    func = _[name, 'clone'] = TAJS_restrictToType(obj[name, 'clone'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'clone'] = ($__.fs.J$__v3189643886_308_152 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 10:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 10);
    func = _[name, 'collect'] = TAJS_restrictToType(obj[name, 'collect'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'collect'] = ($__.fs.J$__v3189643886_308_153 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 11:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 11);
    func = _[name, 'compact'] = TAJS_restrictToType(obj[name, 'compact'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'compact'] = ($__.fs.J$__v3189643886_308_154 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 12:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 12);
    func = _[name, 'compose'] = TAJS_restrictToType(obj[name, 'compose'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'compose'] = ($__.fs.J$__v3189643886_308_155 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 13:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 13);
    func = _[name, 'constant'] = TAJS_restrictToType(obj[name, 'constant'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'constant'] = ($__.fs.J$__v3189643886_308_156 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 14:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 14);
    func = _[name, 'contains'] = TAJS_restrictToType(obj[name, 'contains'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'contains'] = ($__.fs.J$__v3189643886_308_157 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 15:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 15);
    func = _[name, 'countBy'] = TAJS_restrictToType(obj[name, 'countBy'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'countBy'] = ($__.fs.J$__v3189643886_308_158 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 16:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 16);
    func = _[name, 'create'] = TAJS_restrictToType(obj[name, 'create'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'create'] = ($__.fs.J$__v3189643886_308_159 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 17:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 17);
    func = _[name, 'debounce'] = TAJS_restrictToType(obj[name, 'debounce'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'debounce'] = ($__.fs.J$__v3189643886_308_160 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 18:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 18);
    func = _[name, 'defaults'] = TAJS_restrictToType(obj[name, 'defaults'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'defaults'] = ($__.fs.J$__v3189643886_308_161 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 19:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 19);
    func = _[name, 'defer'] = TAJS_restrictToType(obj[name, 'defer'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'defer'] = ($__.fs.J$__v3189643886_308_162 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 20:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 20);
    func = _[name, 'delay'] = TAJS_restrictToType(obj[name, 'delay'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'delay'] = ($__.fs.J$__v3189643886_308_163 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 21:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 21);
    func = _[name, 'detect'] = TAJS_restrictToType(obj[name, 'detect'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'detect'] = ($__.fs.J$__v3189643886_308_164 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 22:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 22);
    func = _[name, 'difference'] = TAJS_restrictToType(obj[name, 'difference'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'difference'] = ($__.fs.J$__v3189643886_308_165 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 23:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 23);
    func = _[name, 'drop'] = TAJS_restrictToType(obj[name, 'drop'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'drop'] = ($__.fs.J$__v3189643886_308_166 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 24:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 24);
    func = _[name, 'each'] = TAJS_restrictToType(obj[name, 'each'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'each'] = ($__.fs.J$__v3189643886_308_167 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 25:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 25);
    func = _[name, 'escape'] = TAJS_restrictToType(obj[name, 'escape'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'escape'] = ($__.fs.J$__v3189643886_308_168 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 26:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 26);
    func = _[name, 'every'] = TAJS_restrictToType(obj[name, 'every'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'every'] = ($__.fs.J$__v3189643886_308_169 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 27:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 27);
    func = _[name, 'extend'] = TAJS_restrictToType(obj[name, 'extend'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'extend'] = ($__.fs.J$__v3189643886_308_170 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 28:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 28);
    func = _[name, 'extendOwn'] = TAJS_restrictToType(obj[name, 'extendOwn'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'extendOwn'] = ($__.fs.J$__v3189643886_308_171 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 29:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 29);
    func = _[name, 'filter'] = TAJS_restrictToType(obj[name, 'filter'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'filter'] = ($__.fs.J$__v3189643886_308_172 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 30:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 30);
    func = _[name, 'find'] = TAJS_restrictToType(obj[name, 'find'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'find'] = ($__.fs.J$__v3189643886_308_173 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 31:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 31);
    func = _[name, 'findIndex'] = TAJS_restrictToType(obj[name, 'findIndex'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'findIndex'] = ($__.fs.J$__v3189643886_308_174 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 32:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 32);
    func = _[name, 'findKey'] = TAJS_restrictToType(obj[name, 'findKey'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'findKey'] = ($__.fs.J$__v3189643886_308_175 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 33:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 33);
    func = _[name, 'findLastIndex'] = TAJS_restrictToType(obj[name, 'findLastIndex'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'findLastIndex'] = ($__.fs.J$__v3189643886_308_176 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 34:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 34);
    func = _[name, 'findWhere'] = TAJS_restrictToType(obj[name, 'findWhere'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'findWhere'] = ($__.fs.J$__v3189643886_308_177 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 35:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 35);
    func = _[name, 'first'] = TAJS_restrictToType(obj[name, 'first'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'first'] = ($__.fs.J$__v3189643886_308_178 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 36:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 36);
    func = _[name, 'flatten'] = TAJS_restrictToType(obj[name, 'flatten'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'flatten'] = ($__.fs.J$__v3189643886_308_179 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 37:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 37);
    func = _[name, 'foldl'] = TAJS_restrictToType(obj[name, 'foldl'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'foldl'] = ($__.fs.J$__v3189643886_308_180 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 38:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 38);
    func = _[name, 'foldr'] = TAJS_restrictToType(obj[name, 'foldr'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'foldr'] = ($__.fs.J$__v3189643886_308_181 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 39:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 39);
    func = _[name, 'forEach'] = TAJS_restrictToType(obj[name, 'forEach'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'forEach'] = ($__.fs.J$__v3189643886_308_182 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 40:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 40);
    func = _[name, 'functions'] = TAJS_restrictToType(obj[name, 'functions'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'functions'] = ($__.fs.J$__v3189643886_308_183 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 41:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 41);
    func = _[name, 'groupBy'] = TAJS_restrictToType(obj[name, 'groupBy'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'groupBy'] = ($__.fs.J$__v3189643886_308_184 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 42:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 42);
    func = _[name, 'has'] = TAJS_restrictToType(obj[name, 'has'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'has'] = ($__.fs.J$__v3189643886_308_185 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 43:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 43);
    func = _[name, 'head'] = TAJS_restrictToType(obj[name, 'head'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'head'] = ($__.fs.J$__v3189643886_308_186 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 44:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 44);
    func = _[name, 'identity'] = TAJS_restrictToType(obj[name, 'identity'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'identity'] = ($__.fs.J$__v3189643886_308_187 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 45:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 45);
    func = _[name, 'include'] = TAJS_restrictToType(obj[name, 'include'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'include'] = ($__.fs.J$__v3189643886_308_188 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 46:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 46);
    func = _[name, 'includes'] = TAJS_restrictToType(obj[name, 'includes'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'includes'] = ($__.fs.J$__v3189643886_308_189 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 47:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 47);
    func = _[name, 'indexBy'] = TAJS_restrictToType(obj[name, 'indexBy'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'indexBy'] = ($__.fs.J$__v3189643886_308_190 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 48:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 48);
    func = _[name, 'indexOf'] = TAJS_restrictToType(obj[name, 'indexOf'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'indexOf'] = ($__.fs.J$__v3189643886_308_191 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 49:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 49);
    func = _[name, 'initial'] = TAJS_restrictToType(obj[name, 'initial'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'initial'] = ($__.fs.J$__v3189643886_308_192 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 50:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 50);
    func = _[name, 'inject'] = TAJS_restrictToType(obj[name, 'inject'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'inject'] = ($__.fs.J$__v3189643886_308_193 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 51:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 51);
    func = _[name, 'intersection'] = TAJS_restrictToType(obj[name, 'intersection'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'intersection'] = ($__.fs.J$__v3189643886_308_194 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 52:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 52);
    func = _[name, 'invert'] = TAJS_restrictToType(obj[name, 'invert'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'invert'] = ($__.fs.J$__v3189643886_308_195 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 53:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 53);
    func = _[name, 'invoke'] = TAJS_restrictToType(obj[name, 'invoke'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'invoke'] = ($__.fs.J$__v3189643886_308_196 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 54:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 54);
    func = _[name, 'isArguments'] = TAJS_restrictToType(obj[name, 'isArguments'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isArguments'] = ($__.fs.J$__v3189643886_308_197 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 55:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 55);
    func = _[name, 'isArray'] = TAJS_restrictToType(obj[name, 'isArray'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isArray'] = ($__.fs.J$__v3189643886_308_198 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 56:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 56);
    func = _[name, 'isBoolean'] = TAJS_restrictToType(obj[name, 'isBoolean'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isBoolean'] = ($__.fs.J$__v3189643886_308_199 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 57:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 57);
    func = _[name, 'isDate'] = TAJS_restrictToType(obj[name, 'isDate'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isDate'] = ($__.fs.J$__v3189643886_308_200 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 58:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 58);
    func = _[name, 'isElement'] = TAJS_restrictToType(obj[name, 'isElement'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isElement'] = ($__.fs.J$__v3189643886_308_201 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 59:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 59);
    func = _[name, 'isEmpty'] = TAJS_restrictToType(obj[name, 'isEmpty'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isEmpty'] = ($__.fs.J$__v3189643886_308_202 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 60:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 60);
    func = _[name, 'isEqual'] = TAJS_restrictToType(obj[name, 'isEqual'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isEqual'] = ($__.fs.J$__v3189643886_308_203 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 61:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 61);
    func = _[name, 'isError'] = TAJS_restrictToType(obj[name, 'isError'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isError'] = ($__.fs.J$__v3189643886_308_204 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 62:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 62);
    func = _[name, 'isFinite'] = TAJS_restrictToType(obj[name, 'isFinite'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isFinite'] = ($__.fs.J$__v3189643886_308_205 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 63:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 63);
    func = _[name, 'isFunction'] = TAJS_restrictToType(obj[name, 'isFunction'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isFunction'] = ($__.fs.J$__v3189643886_308_206 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 64:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 64);
    func = _[name, 'isMatch'] = TAJS_restrictToType(obj[name, 'isMatch'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isMatch'] = ($__.fs.J$__v3189643886_308_207 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 65:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 65);
    func = _[name, 'isNaN'] = TAJS_restrictToType(obj[name, 'isNaN'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isNaN'] = ($__.fs.J$__v3189643886_308_208 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 66:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 66);
    func = _[name, 'isNull'] = TAJS_restrictToType(obj[name, 'isNull'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isNull'] = ($__.fs.J$__v3189643886_308_209 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 67:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 67);
    func = _[name, 'isNumber'] = TAJS_restrictToType(obj[name, 'isNumber'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isNumber'] = ($__.fs.J$__v3189643886_308_210 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 68:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 68);
    func = _[name, 'isObject'] = TAJS_restrictToType(obj[name, 'isObject'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isObject'] = ($__.fs.J$__v3189643886_308_211 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 69:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 69);
    func = _[name, 'isRegExp'] = TAJS_restrictToType(obj[name, 'isRegExp'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isRegExp'] = ($__.fs.J$__v3189643886_308_212 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 70:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 70);
    func = _[name, 'isString'] = TAJS_restrictToType(obj[name, 'isString'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isString'] = ($__.fs.J$__v3189643886_308_213 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 71:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 71);
    func = _[name, 'isUndefined'] = TAJS_restrictToType(obj[name, 'isUndefined'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'isUndefined'] = ($__.fs.J$__v3189643886_308_214 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 72:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 72);
    func = _[name, 'iteratee'] = TAJS_restrictToType(obj[name, 'iteratee'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'iteratee'] = ($__.fs.J$__v3189643886_308_215 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 73:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 73);
    func = _[name, 'keys'] = TAJS_restrictToType(obj[name, 'keys'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'keys'] = ($__.fs.J$__v3189643886_308_216 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 74:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 74);
    func = _[name, 'last'] = TAJS_restrictToType(obj[name, 'last'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'last'] = ($__.fs.J$__v3189643886_308_217 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 75:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 75);
    func = _[name, 'lastIndexOf'] = TAJS_restrictToType(obj[name, 'lastIndexOf'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'lastIndexOf'] = ($__.fs.J$__v3189643886_308_218 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 76:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 76);
    func = _[name, 'map'] = TAJS_restrictToType(obj[name, 'map'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'map'] = ($__.fs.J$__v3189643886_308_219 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 77:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 77);
    func = _[name, 'mapObject'] = TAJS_restrictToType(obj[name, 'mapObject'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'mapObject'] = ($__.fs.J$__v3189643886_308_220 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 78:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 78);
    func = _[name, 'matcher'] = TAJS_restrictToType(obj[name, 'matcher'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'matcher'] = ($__.fs.J$__v3189643886_308_221 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 79:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 79);
    func = _[name, 'matches'] = TAJS_restrictToType(obj[name, 'matches'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'matches'] = ($__.fs.J$__v3189643886_308_222 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 80:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 80);
    func = _[name, 'max'] = TAJS_restrictToType(obj[name, 'max'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'max'] = ($__.fs.J$__v3189643886_308_223 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 81:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 81);
    func = _[name, 'memoize'] = TAJS_restrictToType(obj[name, 'memoize'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'memoize'] = ($__.fs.J$__v3189643886_308_224 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 82:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 82);
    func = _[name, 'methods'] = TAJS_restrictToType(obj[name, 'methods'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'methods'] = ($__.fs.J$__v3189643886_308_225 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 83:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 83);
    func = _[name, 'min'] = TAJS_restrictToType(obj[name, 'min'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'min'] = ($__.fs.J$__v3189643886_308_226 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 84:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 84);
    func = _[name, 'mixin'] = TAJS_restrictToType(obj[name, 'mixin'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'mixin'] = ($__.fs.J$__v3189643886_308_227 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 85:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 85);
    func = _[name, 'negate'] = TAJS_restrictToType(obj[name, 'negate'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'negate'] = ($__.fs.J$__v3189643886_308_228 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 86:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 86);
    func = _[name, 'noConflict'] = TAJS_restrictToType(obj[name, 'noConflict'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'noConflict'] = ($__.fs.J$__v3189643886_308_229 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 87:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 87);
    func = _[name, 'noop'] = TAJS_restrictToType(obj[name, 'noop'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'noop'] = ($__.fs.J$__v3189643886_308_230 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 88:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 88);
    func = _[name, 'now'] = TAJS_restrictToType(obj[name, 'now'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'now'] = ($__.fs.J$__v3189643886_308_231 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 89:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 89);
    func = _[name, 'object'] = TAJS_restrictToType(obj[name, 'object'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'object'] = ($__.fs.J$__v3189643886_308_232 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 90:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 90);
    func = _[name, 'omit'] = TAJS_restrictToType(obj[name, 'omit'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'omit'] = ($__.fs.J$__v3189643886_308_233 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 91:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 91);
    func = _[name, 'once'] = TAJS_restrictToType(obj[name, 'once'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'once'] = ($__.fs.J$__v3189643886_308_234 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 92:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 92);
    func = _[name, 'pairs'] = TAJS_restrictToType(obj[name, 'pairs'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'pairs'] = ($__.fs.J$__v3189643886_308_235 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 93:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 93);
    func = _[name, 'partial'] = TAJS_restrictToType(obj[name, 'partial'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'partial'] = ($__.fs.J$__v3189643886_308_236 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 94:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 94);
    func = _[name, 'partition'] = TAJS_restrictToType(obj[name, 'partition'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'partition'] = ($__.fs.J$__v3189643886_308_237 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 95:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 95);
    func = _[name, 'pick'] = TAJS_restrictToType(obj[name, 'pick'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'pick'] = ($__.fs.J$__v3189643886_308_238 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 96:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 96);
    func = _[name, 'pluck'] = TAJS_restrictToType(obj[name, 'pluck'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'pluck'] = ($__.fs.J$__v3189643886_308_239 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 97:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 97);
    func = _[name, 'property'] = TAJS_restrictToType(obj[name, 'property'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'property'] = ($__.fs.J$__v3189643886_308_240 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 98:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 98);
    func = _[name, 'propertyOf'] = TAJS_restrictToType(obj[name, 'propertyOf'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'propertyOf'] = ($__.fs.J$__v3189643886_308_241 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 99:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 99);
    func = _[name, 'random'] = TAJS_restrictToType(obj[name, 'random'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'random'] = ($__.fs.J$__v3189643886_308_242 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 100:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 100);
    func = _[name, 'range'] = TAJS_restrictToType(obj[name, 'range'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'range'] = ($__.fs.J$__v3189643886_308_243 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 101:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 101);
    func = _[name, 'reduce'] = TAJS_restrictToType(obj[name, 'reduce'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'reduce'] = ($__.fs.J$__v3189643886_308_244 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 102:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 102);
    func = _[name, 'reduceRight'] = TAJS_restrictToType(obj[name, 'reduceRight'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'reduceRight'] = ($__.fs.J$__v3189643886_308_245 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 103:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 103);
    func = _[name, 'reject'] = TAJS_restrictToType(obj[name, 'reject'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'reject'] = ($__.fs.J$__v3189643886_308_246 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 104:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 104);
    func = _[name, 'rest'] = TAJS_restrictToType(obj[name, 'rest'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'rest'] = ($__.fs.J$__v3189643886_308_247 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 105:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 105);
    func = _[name, 'result'] = TAJS_restrictToType(obj[name, 'result'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'result'] = ($__.fs.J$__v3189643886_308_248 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 106:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 106);
    func = _[name, 'sample'] = TAJS_restrictToType(obj[name, 'sample'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'sample'] = ($__.fs.J$__v3189643886_308_249 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 107:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 107);
    func = _[name, 'select'] = TAJS_restrictToType(obj[name, 'select'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'select'] = ($__.fs.J$__v3189643886_308_250 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 108:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 108);
    func = _[name, 'shuffle'] = TAJS_restrictToType(obj[name, 'shuffle'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'shuffle'] = ($__.fs.J$__v3189643886_308_251 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 109:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 109);
    func = _[name, 'size'] = TAJS_restrictToType(obj[name, 'size'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'size'] = ($__.fs.J$__v3189643886_308_252 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 110:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 110);
    func = _[name, 'some'] = TAJS_restrictToType(obj[name, 'some'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'some'] = ($__.fs.J$__v3189643886_308_253 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 111:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 111);
    func = _[name, 'sortBy'] = TAJS_restrictToType(obj[name, 'sortBy'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'sortBy'] = ($__.fs.J$__v3189643886_308_254 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 112:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 112);
    func = _[name, 'sortedIndex'] = TAJS_restrictToType(obj[name, 'sortedIndex'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'sortedIndex'] = ($__.fs.J$__v3189643886_308_255 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 113:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 113);
    func = _[name, 'tail'] = TAJS_restrictToType(obj[name, 'tail'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'tail'] = ($__.fs.J$__v3189643886_308_256 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 114:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 114);
    func = _[name, 'take'] = TAJS_restrictToType(obj[name, 'take'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'take'] = ($__.fs.J$__v3189643886_308_257 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 115:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 115);
    func = _[name, 'tap'] = TAJS_restrictToType(obj[name, 'tap'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'tap'] = ($__.fs.J$__v3189643886_308_258 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 116:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 116);
    func = _[name, 'template'] = TAJS_restrictToType(obj[name, 'template'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'template'] = ($__.fs.J$__v3189643886_308_259 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 117:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 117);
    func = _[name, 'throttle'] = TAJS_restrictToType(obj[name, 'throttle'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'throttle'] = ($__.fs.J$__v3189643886_308_260 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 118:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 118);
    func = _[name, 'times'] = TAJS_restrictToType(obj[name, 'times'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'times'] = ($__.fs.J$__v3189643886_308_261 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 119:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 119);
    func = _[name, 'toArray'] = TAJS_restrictToType(obj[name, 'toArray'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'toArray'] = ($__.fs.J$__v3189643886_308_262 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 120:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 120);
    func = _[name, 'unescape'] = TAJS_restrictToType(obj[name, 'unescape'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'unescape'] = ($__.fs.J$__v3189643886_308_263 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 121:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 121);
    func = _[name, 'union'] = TAJS_restrictToType(obj[name, 'union'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'union'] = ($__.fs.J$__v3189643886_308_264 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 122:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 122);
    func = _[name, 'uniq'] = TAJS_restrictToType(obj[name, 'uniq'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'uniq'] = ($__.fs.J$__v3189643886_308_265 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 123:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 123);
    func = _[name, 'unique'] = TAJS_restrictToType(obj[name, 'unique'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'unique'] = ($__.fs.J$__v3189643886_308_266 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 124:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 124);
    func = _[name, 'uniqueId'] = TAJS_restrictToType(obj[name, 'uniqueId'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'uniqueId'] = ($__.fs.J$__v3189643886_308_267 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 125:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 125);
    func = _[name, 'unzip'] = TAJS_restrictToType(obj[name, 'unzip'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'unzip'] = ($__.fs.J$__v3189643886_308_268 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 126:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 126);
    func = _[name, 'values'] = TAJS_restrictToType(obj[name, 'values'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'values'] = ($__.fs.J$__v3189643886_308_269 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 127:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 127);
    func = _[name, 'where'] = TAJS_restrictToType(obj[name, 'where'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'where'] = ($__.fs.J$__v3189643886_308_270 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 128:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 128);
    func = _[name, 'without'] = TAJS_restrictToType(obj[name, 'without'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'without'] = ($__.fs.J$__v3189643886_308_271 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 129:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 129);
    func = _[name, 'wrap'] = TAJS_restrictToType(obj[name, 'wrap'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'wrap'] = ($__.fs.J$__v3189643886_308_272 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
case 130:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 130);
    func = _[name, 'zip'] = TAJS_restrictToType(obj[name, 'zip'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'zip'] = ($__.fs.J$__v3189643886_308_273 = function J$__v3189643886_308() {
var vvv_return, vvv_switch, args;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
args = [this._wrapped];
push.apply(args, arguments);
return result(this, func.apply(_, args));});
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
func = _[name] = obj[name];
_.prototype[name] = function () {
    var args = [this._wrapped];
    push.apply(args, arguments);
    return result(this, func.apply(_, args));
};}), 1, true, $__.uid);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
_.each(_.functions(obj), function (name) {
    var func = _[name] = obj[name];
    _.prototype[name] = function () {
        var args = [this._wrapped];
        push.apply(args, arguments);
        return result(this, func.apply(_, args));
    };
});});
    vvv_tmp5 = _, 'mixin', $__.fs.J$__v3189643886_312_141.call(vvv_tmp5, (_, $__.fs.J$__v3189643886_3_9), 0, true, $__.uid);
    vvv_tmp6 = _, 'each', $__.fs.J$__v3189643886_33_18.call(vvv_tmp6, [
        'pop',
        'push',
        'reverse',
        'shift',
        'sort',
        'splice',
        'unshift'
    ], ($__.fs.J$__v3189643886_316_274 = function J$__v3189643886_316(name) {
var vvv_return, vvv_switch, method;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    method = TAJS_restrictToType(ArrayProto[name, 'pop'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'pop'] = ($__.fs.J$__v3189643886_314_275 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    method = TAJS_restrictToType(ArrayProto[name, 'push'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'push'] = ($__.fs.J$__v3189643886_314_276 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    method = TAJS_restrictToType(ArrayProto[name, 'reverse'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'reverse'] = ($__.fs.J$__v3189643886_314_277 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 3);
    method = TAJS_restrictToType(ArrayProto[name, 'shift'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'shift'] = ($__.fs.J$__v3189643886_314_278 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 4:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 4);
    method = TAJS_restrictToType(ArrayProto[name, 'sort'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'sort'] = ($__.fs.J$__v3189643886_314_279 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 5:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 5);
    method = TAJS_restrictToType(ArrayProto[name, 'splice'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'splice'] = ($__.fs.J$__v3189643886_314_280 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
case 6:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 6);
    method = TAJS_restrictToType(ArrayProto[name, 'unshift'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'unshift'] = ($__.fs.J$__v3189643886_314_281 = function J$__v3189643886_314() {
var vvv_return, vvv_switch, obj;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
obj = this._wrapped;
method.apply(obj, arguments);
if ((name === 'shift' || name === 'splice') && obj.length === 0)
    delete obj[0];
return result(this, obj);});
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
method = ArrayProto[name];
_.prototype[name] = function () {
    var obj = this._wrapped;
    method.apply(obj, arguments);
    if ((name === 'shift' || name === 'splice') && obj.length === 0)
        delete obj[0];
    return result(this, obj);
};}), 2, true, $__.uid);
    vvv_tmp7 = _, 'each', $__.fs.J$__v3189643886_33_18.call(vvv_tmp7, [
        'concat',
        'join',
        'slice'
    ], ($__.fs.J$__v3189643886_320_282 = function J$__v3189643886_320(name) {
var vvv_return, vvv_switch, method;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    method = TAJS_restrictToType(ArrayProto[name, 'concat'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'concat'] = ($__.fs.J$__v3189643886_318_283 = function J$__v3189643886_318() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return result(this, method.apply(this._wrapped, arguments));});
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    method = TAJS_restrictToType(ArrayProto[name, 'join'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'join'] = ($__.fs.J$__v3189643886_318_284 = function J$__v3189643886_318() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return result(this, method.apply(this._wrapped, arguments));});
    return;
case 2:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 2);
    method = TAJS_restrictToType(ArrayProto[name, 'slice'], 'function');
    TAJS_restrictToType(_.prototype, 'object')[name, 'slice'] = ($__.fs.J$__v3189643886_318_285 = function J$__v3189643886_318() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return result(this, method.apply(this._wrapped, arguments));});
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
method = ArrayProto[name];
_.prototype[name] = function () {
    return result(this, method.apply(this._wrapped, arguments));
};}), 3, true, $__.uid);
    TAJS_restrictToType(_.prototype, 'object').value = ($__.fs.J$__v3189643886_322_286 = function J$__v3189643886_322() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return this._wrapped;});
    TAJS_restrictToType(_.prototype, 'object').valueOf = TAJS_restrictToType(_.prototype, 'object').toJSON = TAJS_restrictToType(TAJS_restrictToType(_.prototype, 'object').value, 'function');
    TAJS_restrictToType(_.prototype, 'object').toString = ($__.fs.J$__v3189643886_324_287 = function J$__v3189643886_324() {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return '' + this._wrapped;});
    typeof define === 'function';
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
root = this;
previousUnderscore = root._;
ArrayProto = Array.prototype;
ObjProto = Object.prototype;
FuncProto = Function.prototype;
push = ArrayProto.push;
slice = ArrayProto.slice;
toString = ObjProto.toString;
hasOwnProperty = ObjProto.hasOwnProperty;
nativeIsArray = Array.isArray;
nativeKeys = Object.keys;
nativeBind = FuncProto.bind;
nativeCreate = Object.create;
Ctor = function () {
};
_ = function (obj) {
    if (obj instanceof _)
        return obj;
    if (!(this instanceof _))
        return new _(obj);
    this._wrapped = obj;
};
if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
        exports = module.exports = _;
    }
    exports._ = _;
} else {
    root._ = _;
}
_.VERSION = '1.8.3';
optimizeCb = function (func, context, argCount) {
    if (context === void 0)
        return func;
    switch (argCount == null ? 3 : argCount) {
    case 1:
        return function (value) {
            return func.call(context, value);
        };
    case 2:
        return function (value, other) {
            return func.call(context, value, other);
        };
    case 3:
        return function (value, index, collection) {
            return func.call(context, value, index, collection);
        };
    case 4:
        return function (accumulator, value, index, collection) {
            return func.call(context, accumulator, value, index, collection);
        };
    }
    return function () {
        return func.apply(context, arguments);
    };
};
cb = function (value, context, argCount) {
    if (value == null)
        return _.identity;
    if (_.isFunction(value))
        return optimizeCb(value, context, argCount);
    if (_.isObject(value))
        return _.matcher(value);
    return _.property(value);
};
_.iteratee = function (value, context) {
    return cb(value, context, Infinity);
};
createAssigner = function (keysFunc, undefinedOnly) {
    return function (obj) {
        var length = arguments.length;
        if (length < 2 || obj == null)
            return obj;
        for (var index = 1; index < length; index++) {
            var source = arguments[index], keys = keysFunc(source), l = keys.length;
            for (var i = 0; i < l; i++) {
                var key = keys[i];
                if (!undefinedOnly || obj[key] === void 0)
                    obj[key] = source[key];
            }
        }
        return obj;
    };
};
baseCreate = function (prototype) {
    if (!_.isObject(prototype))
        return {};
    if (nativeCreate)
        return nativeCreate(prototype);
    Ctor.prototype = prototype;
    var result = new Ctor();
    Ctor.prototype = null;
    return result;
};
property = function (key) {
    return function (obj) {
        return obj == null ? void 0 : obj[key];
    };
};
MAX_ARRAY_INDEX = Math.pow(2, 53) - 1;
getLength = property('length');
isArrayLike = function (collection) {
    var length = getLength(collection);
    return typeof length == 'number' && length >= 0 && length <= MAX_ARRAY_INDEX;
};
_.each = _.forEach = function (obj, iteratee, context) {
    iteratee = optimizeCb(iteratee, context);
    var i, length;
    if (isArrayLike(obj)) {
        for (i = 0, length = obj.length; i < length; i++) {
            iteratee(obj[i], i, obj);
        }
    } else {
        var keys = _.keys(obj);
        for (i = 0, length = keys.length; i < length; i++) {
            iteratee(obj[keys[i]], keys[i], obj);
        }
    }
    return obj;
};
_.map = _.collect = function (obj, iteratee, context) {
    iteratee = cb(iteratee, context);
    var keys = !isArrayLike(obj) && _.keys(obj), length = (keys || obj).length, results = Array(length);
    for (var index = 0; index < length; index++) {
        var currentKey = keys ? keys[index] : index;
        results[index] = iteratee(obj[currentKey], currentKey, obj);
    }
    return results;
};
_.reduce = _.foldl = _.inject = createReduce(1);
_.reduceRight = _.foldr = createReduce(-1);
_.find = _.detect = function (obj, predicate, context) {
    var key;
    if (isArrayLike(obj)) {
        key = _.findIndex(obj, predicate, context);
    } else {
        key = _.findKey(obj, predicate, context);
    }
    if (key !== void 0 && key !== -1)
        return obj[key];
};
_.filter = _.select = function (obj, predicate, context) {
    var results = [];
    predicate = cb(predicate, context);
    _.each(obj, function (value, index, list) {
        if (predicate(value, index, list))
            results.push(value);
    });
    return results;
};
_.reject = function (obj, predicate, context) {
    return _.filter(obj, _.negate(cb(predicate)), context);
};
_.every = _.all = function (obj, predicate, context) {
    predicate = cb(predicate, context);
    var keys = !isArrayLike(obj) && _.keys(obj), length = (keys || obj).length;
    for (var index = 0; index < length; index++) {
        var currentKey = keys ? keys[index] : index;
        if (!predicate(obj[currentKey], currentKey, obj))
            return false;
    }
    return true;
};
_.some = _.any = function (obj, predicate, context) {
    predicate = cb(predicate, context);
    var keys = !isArrayLike(obj) && _.keys(obj), length = (keys || obj).length;
    for (var index = 0; index < length; index++) {
        var currentKey = keys ? keys[index] : index;
        if (predicate(obj[currentKey], currentKey, obj))
            return true;
    }
    return false;
};
_.contains = _.includes = _.include = function (obj, item, fromIndex, guard) {
    if (!isArrayLike(obj))
        obj = _.values(obj);
    if (typeof fromIndex != 'number' || guard)
        fromIndex = 0;
    return _.indexOf(obj, item, fromIndex) >= 0;
};
_.invoke = function (obj, method) {
    var args = slice.call(arguments, 2);
    var isFunc = _.isFunction(method);
    return _.map(obj, function (value) {
        var func = isFunc ? method : value[method];
        return func == null ? func : func.apply(value, args);
    });
};
_.pluck = function (obj, key) {
    return _.map(obj, _.property(key));
};
_.where = function (obj, attrs) {
    return _.filter(obj, _.matcher(attrs));
};
_.findWhere = function (obj, attrs) {
    return _.find(obj, _.matcher(attrs));
};
_.max = function (obj, iteratee, context) {
    var result = -Infinity, lastComputed = -Infinity, value, computed;
    if (iteratee == null && obj != null) {
        obj = isArrayLike(obj) ? obj : _.values(obj);
        for (var i = 0, length = obj.length; i < length; i++) {
            value = obj[i];
            if (value > result) {
                result = value;
            }
        }
    } else {
        iteratee = cb(iteratee, context);
        _.each(obj, function (value, index, list) {
            computed = iteratee(value, index, list);
            if (computed > lastComputed || computed === -Infinity && result === -Infinity) {
                result = value;
                lastComputed = computed;
            }
        });
    }
    return result;
};
_.min = function (obj, iteratee, context) {
    var result = Infinity, lastComputed = Infinity, value, computed;
    if (iteratee == null && obj != null) {
        obj = isArrayLike(obj) ? obj : _.values(obj);
        for (var i = 0, length = obj.length; i < length; i++) {
            value = obj[i];
            if (value < result) {
                result = value;
            }
        }
    } else {
        iteratee = cb(iteratee, context);
        _.each(obj, function (value, index, list) {
            computed = iteratee(value, index, list);
            if (computed < lastComputed || computed === Infinity && result === Infinity) {
                result = value;
                lastComputed = computed;
            }
        });
    }
    return result;
};
_.shuffle = function (obj) {
    var set = isArrayLike(obj) ? obj : _.values(obj);
    var length = set.length;
    var shuffled = Array(length);
    for (var index = 0, rand; index < length; index++) {
        rand = _.random(0, index);
        if (rand !== index)
            shuffled[index] = shuffled[rand];
        shuffled[rand] = set[index];
    }
    return shuffled;
};
_.sample = function (obj, n, guard) {
    if (n == null || guard) {
        if (!isArrayLike(obj))
            obj = _.values(obj);
        return obj[_.random(obj.length - 1)];
    }
    return _.shuffle(obj).slice(0, Math.max(0, n));
};
_.sortBy = function (obj, iteratee, context) {
    iteratee = cb(iteratee, context);
    return _.pluck(_.map(obj, function (value, index, list) {
        return {
            value: value,
            index: index,
            criteria: iteratee(value, index, list)
        };
    }).sort(function (left, right) {
        var a = left.criteria;
        var b = right.criteria;
        if (a !== b) {
            if (a > b || a === void 0)
                return 1;
            if (a < b || b === void 0)
                return -1;
        }
        return left.index - right.index;
    }), 'value');
};
group = function (behavior) {
    return function (obj, iteratee, context) {
        var result = {};
        iteratee = cb(iteratee, context);
        _.each(obj, function (value, index) {
            var key = iteratee(value, index, obj);
            behavior(result, value, key);
        });
        return result;
    };
};
_.groupBy = group(function (result, value, key) {
    if (_.has(result, key))
        result[key].push(value);
    else
        result[key] = [value];
});
_.indexBy = group(function (result, value, key) {
    result[key] = value;
});
_.countBy = group(function (result, value, key) {
    if (_.has(result, key))
        result[key]++;
    else
        result[key] = 1;
});
_.toArray = function (obj) {
    if (!obj)
        return [];
    if (_.isArray(obj))
        return slice.call(obj);
    if (isArrayLike(obj))
        return _.map(obj, _.identity);
    return _.values(obj);
};
_.size = function (obj) {
    if (obj == null)
        return 0;
    return isArrayLike(obj) ? obj.length : _.keys(obj).length;
};
_.partition = function (obj, predicate, context) {
    predicate = cb(predicate, context);
    var pass = [], fail = [];
    _.each(obj, function (value, key, obj) {
        (predicate(value, key, obj) ? pass : fail).push(value);
    });
    return [
        pass,
        fail
    ];
};
_.first = _.head = _.take = function (array, n, guard) {
    if (array == null)
        return void 0;
    if (n == null || guard)
        return array[0];
    return _.initial(array, array.length - n);
};
_.initial = function (array, n, guard) {
    return slice.call(array, 0, Math.max(0, array.length - (n == null || guard ? 1 : n)));
};
_.last = function (array, n, guard) {
    if (array == null)
        return void 0;
    if (n == null || guard)
        return array[array.length - 1];
    return _.rest(array, Math.max(0, array.length - n));
};
_.rest = _.tail = _.drop = function (array, n, guard) {
    return slice.call(array, n == null || guard ? 1 : n);
};
_.compact = function (array) {
    return _.filter(array, _.identity);
};
flatten = function (input, shallow, strict, startIndex) {
    var output = [], idx = 0;
    for (var i = startIndex || 0, length = getLength(input); i < length; i++) {
        var value = input[i];
        if (isArrayLike(value) && (_.isArray(value) || _.isArguments(value))) {
            if (!shallow)
                value = flatten(value, shallow, strict);
            var j = 0, len = value.length;
            output.length += len;
            while (j < len) {
                output[idx++] = value[j++];
            }
        } else if (!strict) {
            output[idx++] = value;
        }
    }
    return output;
};
_.flatten = function (array, shallow) {
    return flatten(array, shallow, false);
};
_.without = function (array) {
    return _.difference(array, slice.call(arguments, 1));
};
_.uniq = _.unique = function (array, isSorted, iteratee, context) {
    if (!_.isBoolean(isSorted)) {
        context = iteratee;
        iteratee = isSorted;
        isSorted = false;
    }
    if (iteratee != null)
        iteratee = cb(iteratee, context);
    var result = [];
    var seen = [];
    for (var i = 0, length = getLength(array); i < length; i++) {
        var value = array[i], computed = iteratee ? iteratee(value, i, array) : value;
        if (isSorted) {
            if (!i || seen !== computed)
                result.push(value);
            seen = computed;
        } else if (iteratee) {
            if (!_.contains(seen, computed)) {
                seen.push(computed);
                result.push(value);
            }
        } else if (!_.contains(result, value)) {
            result.push(value);
        }
    }
    return result;
};
_.union = function () {
    return _.uniq(flatten(arguments, true, true));
};
_.intersection = function (array) {
    var result = [];
    var argsLength = arguments.length;
    for (var i = 0, length = getLength(array); i < length; i++) {
        var item = array[i];
        if (_.contains(result, item))
            continue;
        for (var j = 1; j < argsLength; j++) {
            if (!_.contains(arguments[j], item))
                break;
        }
        if (j === argsLength)
            result.push(item);
    }
    return result;
};
_.difference = function (array) {
    var rest = flatten(arguments, true, true, 1);
    return _.filter(array, function (value) {
        return !_.contains(rest, value);
    });
};
_.zip = function () {
    return _.unzip(arguments);
};
_.unzip = function (array) {
    var length = array && _.max(array, getLength).length || 0;
    var result = Array(length);
    for (var index = 0; index < length; index++) {
        result[index] = _.pluck(array, index);
    }
    return result;
};
_.object = function (list, values) {
    var result = {};
    for (var i = 0, length = getLength(list); i < length; i++) {
        if (values) {
            result[list[i]] = values[i];
        } else {
            result[list[i][0]] = list[i][1];
        }
    }
    return result;
};
_.findIndex = createPredicateIndexFinder(1);
_.findLastIndex = createPredicateIndexFinder(-1);
_.sortedIndex = function (array, obj, iteratee, context) {
    iteratee = cb(iteratee, context, 1);
    var value = iteratee(obj);
    var low = 0, high = getLength(array);
    while (low < high) {
        var mid = Math.floor((low + high) / 2);
        if (iteratee(array[mid]) < value)
            low = mid + 1;
        else
            high = mid;
    }
    return low;
};
_.indexOf = createIndexFinder(1, _.findIndex, _.sortedIndex);
_.lastIndexOf = createIndexFinder(-1, _.findLastIndex);
_.range = function (start, stop, step) {
    if (stop == null) {
        stop = start || 0;
        start = 0;
    }
    step = step || 1;
    var length = Math.max(Math.ceil((stop - start) / step), 0);
    var range = Array(length);
    for (var idx = 0; idx < length; idx++, start += step) {
        range[idx] = start;
    }
    return range;
};
executeBound = function (sourceFunc, boundFunc, context, callingContext, args) {
    if (!(callingContext instanceof boundFunc))
        return sourceFunc.apply(context, args);
    var self = baseCreate(sourceFunc.prototype);
    var result = sourceFunc.apply(self, args);
    if (_.isObject(result))
        return result;
    return self;
};
_.bind = function (func, context) {
    if (nativeBind && func.bind === nativeBind)
        return nativeBind.apply(func, slice.call(arguments, 1));
    if (!_.isFunction(func))
        throw new TypeError('Bind must be called on a function');
    var args = slice.call(arguments, 2);
    var bound = function () {
        return executeBound(func, bound, context, this, args.concat(slice.call(arguments)));
    };
    return bound;
};
_.partial = function (func) {
    var boundArgs = slice.call(arguments, 1);
    var bound = function () {
        var position = 0, length = boundArgs.length;
        var args = Array(length);
        for (var i = 0; i < length; i++) {
            args[i] = boundArgs[i] === _ ? arguments[position++] : boundArgs[i];
        }
        while (position < arguments.length)
            args.push(arguments[position++]);
        return executeBound(func, bound, this, this, args);
    };
    return bound;
};
_.bindAll = function (obj) {
    var i, length = arguments.length, key;
    if (length <= 1)
        throw new Error('bindAll must be passed function names');
    for (i = 1; i < length; i++) {
        key = arguments[i];
        obj[key] = _.bind(obj[key], obj);
    }
    return obj;
};
_.memoize = function (func, hasher) {
    var memoize = function (key) {
        var cache = memoize.cache;
        var address = '' + (hasher ? hasher.apply(this, arguments) : key);
        if (!_.has(cache, address))
            cache[address] = func.apply(this, arguments);
        return cache[address];
    };
    memoize.cache = {};
    return memoize;
};
_.delay = function (func, wait) {
    var args = slice.call(arguments, 2);
    return setTimeout(function () {
        return func.apply(null, args);
    }, wait);
};
_.defer = _.partial(_.delay, _, 1);
_.throttle = function (func, wait, options) {
    var context, args, result;
    var timeout = null;
    var previous = 0;
    if (!options)
        options = {};
    var later = function () {
        previous = options.leading === false ? 0 : _.now();
        timeout = null;
        result = func.apply(context, args);
        if (!timeout)
            context = args = null;
    };
    return function () {
        var now = _.now();
        if (!previous && options.leading === false)
            previous = now;
        var remaining = wait - (now - previous);
        context = this;
        args = arguments;
        if (remaining <= 0 || remaining > wait) {
            if (timeout) {
                clearTimeout(timeout);
                timeout = null;
            }
            previous = now;
            result = func.apply(context, args);
            if (!timeout)
                context = args = null;
        } else if (!timeout && options.trailing !== false) {
            timeout = setTimeout(later, remaining);
        }
        return result;
    };
};
_.debounce = function (func, wait, immediate) {
    var timeout, args, context, timestamp, result;
    var later = function () {
        var last = _.now() - timestamp;
        if (last < wait && last >= 0) {
            timeout = setTimeout(later, wait - last);
        } else {
            timeout = null;
            if (!immediate) {
                result = func.apply(context, args);
                if (!timeout)
                    context = args = null;
            }
        }
    };
    return function () {
        context = this;
        args = arguments;
        timestamp = _.now();
        var callNow = immediate && !timeout;
        if (!timeout)
            timeout = setTimeout(later, wait);
        if (callNow) {
            result = func.apply(context, args);
            context = args = null;
        }
        return result;
    };
};
_.wrap = function (func, wrapper) {
    return _.partial(wrapper, func);
};
_.negate = function (predicate) {
    return function () {
        return !predicate.apply(this, arguments);
    };
};
_.compose = function () {
    var args = arguments;
    var start = args.length - 1;
    return function () {
        var i = start;
        var result = args[start].apply(this, arguments);
        while (i--)
            result = args[i].call(this, result);
        return result;
    };
};
_.after = function (times, func) {
    return function () {
        if (--times < 1) {
            return func.apply(this, arguments);
        }
    };
};
_.before = function (times, func) {
    var memo;
    return function () {
        if (--times > 0) {
            memo = func.apply(this, arguments);
        }
        if (times <= 1)
            func = null;
        return memo;
    };
};
_.once = _.partial(_.before, 2);
hasEnumBug = !{
    toString: null
}.propertyIsEnumerable('toString');
nonEnumerableProps = [
    'valueOf',
    'isPrototypeOf',
    'toString',
    'propertyIsEnumerable',
    'hasOwnProperty',
    'toLocaleString'
];
_.keys = function (obj) {
    if (!_.isObject(obj))
        return [];
    if (nativeKeys)
        return nativeKeys(obj);
    var keys = [];
    for (var key in obj)
        if (_.has(obj, key))
        keys.push(key);
    if (hasEnumBug)
        collectNonEnumProps(obj, keys);
    return keys;
};
_.allKeys = function (obj) {
    if (!_.isObject(obj))
        return [];
    var keys = [];
    for (var key in obj)
        keys.push(key);
    if (hasEnumBug)
        collectNonEnumProps(obj, keys);
    return keys;
};
_.values = function (obj) {
    var keys = _.keys(obj);
    var length = keys.length;
    var values = Array(length);
    for (var i = 0; i < length; i++) {
        values[i] = obj[keys[i]];
    }
    return values;
};
_.mapObject = function (obj, iteratee, context) {
    iteratee = cb(iteratee, context);
    var keys = _.keys(obj), length = keys.length, results = {}, currentKey;
    for (var index = 0; index < length; index++) {
        currentKey = keys[index];
        results[currentKey] = iteratee(obj[currentKey], currentKey, obj);
    }
    return results;
};
_.pairs = function (obj) {
    var keys = _.keys(obj);
    var length = keys.length;
    var pairs = Array(length);
    for (var i = 0; i < length; i++) {
        pairs[i] = [
            keys[i],
            obj[keys[i]]
        ];
    }
    return pairs;
};
_.invert = function (obj) {
    var result = {};
    var keys = _.keys(obj);
    for (var i = 0, length = keys.length; i < length; i++) {
        result[obj[keys[i]]] = keys[i];
    }
    return result;
};
_.functions = _.methods = function (obj) {
    var names = [];
    for (var key in obj) {
        if (_.isFunction(obj[key]))
            names.push(key);
    }
    return names.sort();
};
_.extend = createAssigner(_.allKeys);
_.extendOwn = _.assign = createAssigner(_.keys);
_.findKey = function (obj, predicate, context) {
    predicate = cb(predicate, context);
    var keys = _.keys(obj), key;
    for (var i = 0, length = keys.length; i < length; i++) {
        key = keys[i];
        if (predicate(obj[key], key, obj))
            return key;
    }
};
_.pick = function (object, oiteratee, context) {
    var result = {}, obj = object, iteratee, keys;
    if (obj == null)
        return result;
    if (_.isFunction(oiteratee)) {
        keys = _.allKeys(obj);
        iteratee = optimizeCb(oiteratee, context);
    } else {
        keys = flatten(arguments, false, false, 1);
        iteratee = function (value, key, obj) {
            return key in obj;
        };
        obj = Object(obj);
    }
    for (var i = 0, length = keys.length; i < length; i++) {
        var key = keys[i];
        var value = obj[key];
        if (iteratee(value, key, obj))
            result[key] = value;
    }
    return result;
};
_.omit = function (obj, iteratee, context) {
    if (_.isFunction(iteratee)) {
        iteratee = _.negate(iteratee);
    } else {
        var keys = _.map(flatten(arguments, false, false, 1), String);
        iteratee = function (value, key) {
            return !_.contains(keys, key);
        };
    }
    return _.pick(obj, iteratee, context);
};
_.defaults = createAssigner(_.allKeys, true);
_.create = function (prototype, props) {
    var result = baseCreate(prototype);
    if (props)
        _.extendOwn(result, props);
    return result;
};
_.clone = function (obj) {
    if (!_.isObject(obj))
        return obj;
    return _.isArray(obj) ? obj.slice() : _.extend({}, obj);
};
_.tap = function (obj, interceptor) {
    interceptor(obj);
    return obj;
};
_.isMatch = function (object, attrs) {
    var keys = _.keys(attrs), length = keys.length;
    if (object == null)
        return !length;
    var obj = Object(object);
    for (var i = 0; i < length; i++) {
        var key = keys[i];
        if (attrs[key] !== obj[key] || !(key in obj))
            return false;
    }
    return true;
};
eq = function (a, b, aStack, bStack) {
    if (a === b)
        return a !== 0 || 1 / a === 1 / b;
    if (a == null || b == null)
        return a === b;
    if (a instanceof _)
        a = a._wrapped;
    if (b instanceof _)
        b = b._wrapped;
    var className = toString.call(a);
    if (className !== toString.call(b))
        return false;
    switch (className) {
    case '[object RegExp]':
    case '[object String]':
        return '' + a === '' + b;
    case '[object Number]':
        if (+a !== +a)
            return +b !== +b;
        return +a === 0 ? 1 / +a === 1 / b : +a === +b;
    case '[object Date]':
    case '[object Boolean]':
        return +a === +b;
    }
    var areArrays = className === '[object Array]';
    if (!areArrays) {
        if (typeof a != 'object' || typeof b != 'object')
            return false;
        var aCtor = a.constructor, bCtor = b.constructor;
        if (aCtor !== bCtor && !(_.isFunction(aCtor) && aCtor instanceof aCtor && _.isFunction(bCtor) && bCtor instanceof bCtor) && ('constructor' in a && 'constructor' in b)) {
            return false;
        }
    }
    aStack = aStack || [];
    bStack = bStack || [];
    var length = aStack.length;
    while (length--) {
        if (aStack[length] === a)
            return bStack[length] === b;
    }
    aStack.push(a);
    bStack.push(b);
    if (areArrays) {
        length = a.length;
        if (length !== b.length)
            return false;
        while (length--) {
            if (!eq(a[length], b[length], aStack, bStack))
                return false;
        }
    } else {
        var keys = _.keys(a), key;
        length = keys.length;
        if (_.keys(b).length !== length)
            return false;
        while (length--) {
            key = keys[length];
            if (!(_.has(b, key) && eq(a[key], b[key], aStack, bStack)))
                return false;
        }
    }
    aStack.pop();
    bStack.pop();
    return true;
};
_.isEqual = function (a, b) {
    return eq(a, b);
};
_.isEmpty = function (obj) {
    if (obj == null)
        return true;
    if (isArrayLike(obj) && (_.isArray(obj) || _.isString(obj) || _.isArguments(obj)))
        return obj.length === 0;
    return _.keys(obj).length === 0;
};
_.isElement = function (obj) {
    return !!(obj && obj.nodeType === 1);
};
_.isArray = nativeIsArray || function (obj) {
    return toString.call(obj) === '[object Array]';
};
_.isObject = function (obj) {
    var type = typeof obj;
    return type === 'function' || type === 'object' && !!obj;
};
_.each([
    'Arguments',
    'Function',
    'String',
    'Number',
    'Date',
    'RegExp',
    'Error'
], function (name) {
    _['is' + name] = function (obj) {
        return toString.call(obj) === '[object ' + name + ']';
    };
});
if (!_.isArguments(arguments)) {
    _.isArguments = function (obj) {
        return _.has(obj, 'callee');
    };
}
if (typeof /./ != 'function' && typeof Int8Array != 'object') {
    _.isFunction = function (obj) {
        return typeof obj == 'function' || false;
    };
}
_.isFinite = function (obj) {
    return isFinite(obj) && !isNaN(parseFloat(obj));
};
_.isNaN = function (obj) {
    return _.isNumber(obj) && obj !== +obj;
};
_.isBoolean = function (obj) {
    return obj === true || obj === false || toString.call(obj) === '[object Boolean]';
};
_.isNull = function (obj) {
    return obj === null;
};
_.isUndefined = function (obj) {
    return obj === void 0;
};
_.has = function (obj, key) {
    return obj != null && hasOwnProperty.call(obj, key);
};
_.noConflict = function () {
    root._ = previousUnderscore;
    return this;
};
_.identity = function (value) {
    return value;
};
_.constant = function (value) {
    return function () {
        return value;
    };
};
_.noop = function () {
};
_.property = property;
_.propertyOf = function (obj) {
    return obj == null ? function () {
    } : function (key) {
        return obj[key];
    };
};
_.matcher = _.matches = function (attrs) {
    attrs = _.extendOwn({}, attrs);
    return function (obj) {
        return _.isMatch(obj, attrs);
    };
};
_.times = function (n, iteratee, context) {
    var accum = Array(Math.max(0, n));
    iteratee = optimizeCb(iteratee, context, 1);
    for (var i = 0; i < n; i++)
        accum[i] = iteratee(i);
    return accum;
};
_.random = function (min, max) {
    if (max == null) {
        max = min;
        min = 0;
    }
    return min + Math.floor(Math.random() * (max - min + 1));
};
_.now = Date.now || function () {
    return new Date().getTime();
};
escapeMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    '\'': '&#x27;',
    '`': '&#x60;'
};
unescapeMap = _.invert(escapeMap);
createEscaper = function (map) {
    var escaper = function (match) {
        return map[match];
    };
    var source = '(?:' + _.keys(map).join('|') + ')';
    var testRegexp = RegExp(source);
    var replaceRegexp = RegExp(source, 'g');
    return function (string) {
        string = string == null ? '' : '' + string;
        return testRegexp.test(string) ? string.replace(replaceRegexp, escaper) : string;
    };
};
_.escape = createEscaper(escapeMap);
_.unescape = createEscaper(unescapeMap);
_.result = function (object, property, fallback) {
    var value = object == null ? void 0 : object[property];
    if (value === void 0) {
        value = fallback;
    }
    return _.isFunction(value) ? value.call(object) : value;
};
idCounter = 0;
_.uniqueId = function (prefix) {
    var id = ++idCounter + '';
    return prefix ? prefix + id : id;
};
_.templateSettings = {
    evaluate: /<%([\s\S]+?)%>/g,
    interpolate: /<%=([\s\S]+?)%>/g,
    escape: /<%-([\s\S]+?)%>/g
};
noMatch = /(.)^/;
escapes = {
    '\'': '\'',
    '\\': '\\',
    '\r': 'r',
    '\n': 'n',
    '\u2028': 'u2028',
    '\u2029': 'u2029'
};
escaper = /\\|'|\r|\n|\u2028|\u2029/g;
escapeChar = function (match) {
    return '\\' + escapes[match];
};
_.template = function (text, settings, oldSettings) {
    if (!settings && oldSettings)
        settings = oldSettings;
    settings = _.defaults({}, settings, _.templateSettings);
    var matcher = RegExp([
        (settings.escape || noMatch).source,
        (settings.interpolate || noMatch).source,
        (settings.evaluate || noMatch).source
    ].join('|') + '|$', 'g');
    var index = 0;
    var source = '__p+=\'';
    text.replace(matcher, function (match, escape, interpolate, evaluate, offset) {
        source += text.slice(index, offset).replace(escaper, escapeChar);
        index = offset + match.length;
        if (escape) {
            source += '\'+\n((__t=(' + escape + '))==null?\'\':_.escape(__t))+\n\'';
        } else if (interpolate) {
            source += '\'+\n((__t=(' + interpolate + '))==null?\'\':__t)+\n\'';
        } else if (evaluate) {
            source += '\';\n' + evaluate + '\n__p+=\'';
        }
        return match;
    });
    source += '\';\n';
    if (!settings.variable)
        source = 'with(obj||{}){\n' + source + '}\n';
    source = 'var __t,__p=\'\',__j=Array.prototype.join,' + 'print=function(){__p+=__j.call(arguments,\'\');};\n' + source + 'return __p;\n';
    try {
        var render = new Function(settings.variable || 'obj', '_', source);
    } catch (e) {
        e.source = source;
        throw e;
    }
    var template = function (data) {
        return render.call(this, data, _);
    };
    var argument = settings.variable || 'obj';
    template.source = 'function(' + argument + '){\n' + source + '}';
    return template;
};
_.chain = function (obj) {
    var instance = _(obj);
    instance._chain = true;
    return instance;
};
result = function (instance, obj) {
    return instance._chain ? _(obj).chain() : obj;
};
_.mixin = function (obj) {
    _.each(_.functions(obj), function (name) {
        var func = _[name] = obj[name];
        _.prototype[name] = function () {
            var args = [this._wrapped];
            push.apply(args, arguments);
            return result(this, func.apply(_, args));
        };
    });
};
_.mixin(_);
_.each([
    'pop',
    'push',
    'reverse',
    'shift',
    'sort',
    'splice',
    'unshift'
], function (name) {
    var method = ArrayProto[name];
    _.prototype[name] = function () {
        var obj = this._wrapped;
        method.apply(obj, arguments);
        if ((name === 'shift' || name === 'splice') && obj.length === 0)
            delete obj[0];
        return result(this, obj);
    };
});
_.each([
    'concat',
    'join',
    'slice'
], function (name) {
    var method = ArrayProto[name];
    _.prototype[name] = function () {
        return result(this, method.apply(this._wrapped, arguments));
    };
});
_.prototype.value = function () {
    return this._wrapped;
};
_.prototype.valueOf = _.prototype.toJSON = _.prototype.value;
_.prototype.toString = function () {
    return '' + this._wrapped;
};
if (typeof define === 'function' && define.amd) {
    define('underscore', [], function () {
        return _;
    });
}}, $__.fs.J$__v3189643886_328_3)), $__.fs.J$__v3189643886_328_3).call(vvv_tmp0, 0, true, $__.uid));