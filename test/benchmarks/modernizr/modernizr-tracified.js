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
(((($__.fs.J$__v8196551053_79_3 = function J$__v8196551053_79(window, document, undefined) {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1, vvv_tmp2, vvv_tmp3, vvv_tmp4, classes, tests, ModernizrProto, Modernizr, prefixes, is, testRunner, docElement, isSVG, setClasses, omPrefixes, domPrefixes, hasOwnProp, addTest, cssomPrefixes, atRule, createElement, hasEvent, prefixedCSSValue, cssToDOM, domToCSS, getBody, injectElementWithStyles, mq, testStyles, contains, nativeTestProps, fnBind, testDOMProps, modElem, mStyle, testProps, testProp, testPropsAll, prefixed, prefixedCSS, testAllProps, i;
is = function is(obj, type) {
    return typeof obj === type;
};
testRunner = function testRunner() {
    var featureNames;
    var feature;
    var aliasIdx;
    var result;
    var nameIdx;
    var featureName;
    var featureNameSplit;
    for (var featureIdx in tests) {
        if (tests.hasOwnProperty(featureIdx)) {
            featureNames = [];
            feature = tests[featureIdx];
            if (feature.name) {
                featureNames.push(feature.name.toLowerCase());
                if (feature.options && feature.options.aliases && feature.options.aliases.length) {
                    for (aliasIdx = 0; aliasIdx < feature.options.aliases.length; aliasIdx++) {
                        featureNames.push(feature.options.aliases[aliasIdx].toLowerCase());
                    }
                }
            }
            result = is(feature.fn, 'function') ? feature.fn() : feature.fn;
            for (nameIdx = 0; nameIdx < featureNames.length; nameIdx++) {
                featureName = featureNames[nameIdx];
                featureNameSplit = featureName.split('.');
                if (featureNameSplit.length === 1) {
                    Modernizr[featureNameSplit[0]] = result;
                } else {
                    if (Modernizr[featureNameSplit[0]] && !(Modernizr[featureNameSplit[0]] instanceof Boolean)) {
                        Modernizr[featureNameSplit[0]] = new Boolean(Modernizr[featureNameSplit[0]]);
                    }
                    Modernizr[featureNameSplit[0]][featureNameSplit[1]] = result;
                }
                classes.push((result ? '' : 'no-') + featureNameSplit.join('-'));
            }
        }
    }
};
setClasses = function setClasses(classes) {
    var className = docElement.className;
    var classPrefix = Modernizr._config.classPrefix || '';
    if (isSVG) {
        className = className.baseVal;
    }
    if (Modernizr._config.enableJSClass) {
        var reJS = new RegExp('(^|\\s)' + classPrefix + 'no-js(\\s|$)');
        className = className.replace(reJS, '$1' + classPrefix + 'js$2');
    }
    if (Modernizr._config.enableClasses) {
        className += ' ' + classPrefix + classes.join(' ' + classPrefix);
        isSVG ? docElement.className.baseVal = className : docElement.className = className;
    }
};
addTest = function addTest(feature, test) {
    if (typeof feature == 'object') {
        for (var key in feature) {
            if (hasOwnProp(feature, key)) {
                addTest(key, feature[key]);
            }
        }
    } else {
        feature = feature.toLowerCase();
        var featureNameSplit = feature.split('.');
        var last = Modernizr[featureNameSplit[0]];
        if (featureNameSplit.length == 2) {
            last = last[featureNameSplit[1]];
        }
        if (typeof last != 'undefined') {
            return Modernizr;
        }
        test = typeof test == 'function' ? test() : test;
        if (featureNameSplit.length == 1) {
            Modernizr[featureNameSplit[0]] = test;
        } else {
            if (Modernizr[featureNameSplit[0]] && !(Modernizr[featureNameSplit[0]] instanceof Boolean)) {
                Modernizr[featureNameSplit[0]] = new Boolean(Modernizr[featureNameSplit[0]]);
            }
            Modernizr[featureNameSplit[0]][featureNameSplit[1]] = test;
        }
        setClasses([(!!test && test != false ? '' : 'no-') + featureNameSplit.join('-')]);
        Modernizr._trigger(feature, test);
    }
    return Modernizr;
};
createElement = function createElement() {
    if (typeof document.createElement !== 'function') {
        return document.createElement(arguments[0]);
    } else if (isSVG) {
        return document.createElementNS.call(document, 'http://www.w3.org/2000/svg', arguments[0]);
    } else {
        return document.createElement.apply(document, arguments);
    }
};
cssToDOM = function cssToDOM(name) {
    return name.replace(/([a-z])-([a-z])/g, function (str, m1, m2) {
        return m1 + m2.toUpperCase();
    }).replace(/^-/, '');
};
domToCSS = function domToCSS(name) {
    return name.replace(/([A-Z])/g, function (str, m1) {
        return '-' + m1.toLowerCase();
    }).replace(/^ms-/, '-ms-');
};
getBody = function getBody() {
    var body = document.body;
    if (!body) {
        body = createElement(isSVG ? 'svg' : 'body');
        body.fake = true;
    }
    return body;
};
injectElementWithStyles = function injectElementWithStyles(rule, callback, nodes, testnames) {
    var mod = 'modernizr';
    var style;
    var ret;
    var node;
    var docOverflow;
    var div = createElement('div');
    var body = getBody();
    if (parseInt(nodes, 10)) {
        while (nodes--) {
            node = createElement('div');
            node.id = testnames ? testnames[nodes] : mod + (nodes + 1);
            div.appendChild(node);
        }
    }
    style = createElement('style');
    style.type = 'text/css';
    style.id = 's' + mod;
    (!body.fake ? div : body).appendChild(style);
    body.appendChild(div);
    if (style.styleSheet) {
        style.styleSheet.cssText = rule;
    } else {
        style.appendChild(document.createTextNode(rule));
    }
    div.id = mod;
    if (body.fake) {
        body.style.background = '';
        body.style.overflow = 'hidden';
        docOverflow = docElement.style.overflow;
        docElement.style.overflow = 'hidden';
        docElement.appendChild(body);
    }
    ret = callback(div, rule);
    if (body.fake) {
        body.parentNode.removeChild(body);
        docElement.style.overflow = docOverflow;
        docElement.offsetHeight;
    } else {
        div.parentNode.removeChild(div);
    }
    return !!ret;
};
contains = function contains(str, substr) {
    return !!~('' + str).indexOf(substr);
};
nativeTestProps = function nativeTestProps(props, value) {
    var i = props.length;
    if ('CSS' in window && 'supports' in window.CSS) {
        while (i--) {
            if (window.CSS.supports(domToCSS(props[i]), value)) {
                return true;
            }
        }
        return false;
    } else if ('CSSSupportsRule' in window) {
        var conditionText = [];
        while (i--) {
            conditionText.push('(' + domToCSS(props[i]) + ':' + value + ')');
        }
        conditionText = conditionText.join(' or ');
        return injectElementWithStyles('@supports (' + conditionText + ') { #modernizr { position: absolute; } }', function (node) {
            return getComputedStyle(node, null).position == 'absolute';
        });
    }
    return undefined;
};
fnBind = function fnBind(fn, that) {
    return function () {
        return fn.apply(that, arguments);
    };
};
testDOMProps = function testDOMProps(props, obj, elem) {
    var item;
    for (var i in props) {
        if (props[i] in obj) {
            if (elem === false) {
                return props[i];
            }
            item = obj[props[i]];
            if (is(item, 'function')) {
                return fnBind(item, elem || obj);
            }
            return item;
        }
    }
    return false;
};
testProps = function testProps(props, prefixed, value, skipValueTest) {
    skipValueTest = is(skipValueTest, 'undefined') ? false : skipValueTest;
    if (!is(value, 'undefined')) {
        var result = nativeTestProps(props, value);
        if (!is(result, 'undefined')) {
            return result;
        }
    }
    var afterInit, i, propsLength, prop, before;
    var elems = [
        'modernizr',
        'tspan',
        'samp'
    ];
    while (!mStyle.style && elems.length) {
        afterInit = true;
        mStyle.modElem = createElement(elems.shift());
        mStyle.style = mStyle.modElem.style;
    }
    function cleanElems() {
        if (afterInit) {
            delete mStyle.style;
            delete mStyle.modElem;
        }
    }
    propsLength = props.length;
    for (i = 0; i < propsLength; i++) {
        prop = props[i];
        before = mStyle.style[prop];
        if (contains(prop, '-')) {
            prop = cssToDOM(prop);
        }
        if (mStyle.style[prop] !== undefined) {
            if (!skipValueTest && !is(value, 'undefined')) {
                try {
                    mStyle.style[prop] = value;
                } catch (e) {
                }
                if (mStyle.style[prop] != before) {
                    cleanElems();
                    return prefixed == 'pfx' ? prop : true;
                }
            } else {
                cleanElems();
                return prefixed == 'pfx' ? prop : true;
            }
        }
    }
    cleanElems();
    return false;
};
testPropsAll = function testPropsAll(prop, prefixed, elem, value, skipValueTest) {
    var ucProp = prop.charAt(0).toUpperCase() + prop.slice(1), props = (prop + ' ' + cssomPrefixes.join(ucProp + ' ') + ucProp).split(' ');
    if (is(prefixed, 'string') || is(prefixed, 'undefined')) {
        return testProps(props, prefixed, value, skipValueTest);
    } else {
        props = (prop + ' ' + domPrefixes.join(ucProp + ' ') + ucProp).split(' ');
        return testDOMProps(props, prefixed, elem);
    }
};
testAllProps = function testAllProps(prop, value, skipValueTest) {
    return testPropsAll(prop, undefined, undefined, value, skipValueTest);
};
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
window = arguments[0], document = arguments[1], undefined = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    is = ($__.fs.is_4 = function is(obj, type) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
obj = arguments[0], type = arguments[1];
switch (vvv_switch) {
case 0:
case 1:
case 2:
case 3:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 3);
    return typeof obj === type;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return typeof obj === type;});
    testRunner = ($__.fs.testRunner_5 = function testRunner() {
var vvv_return, vvv_switch, vvv_tmp0, vvv_tmp1, featureNames, feature, aliasIdx, result, nameIdx, featureName, featureNameSplit, featureIdx;
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
    vvv_tmp0 = tests;
    '0' in vvv_tmp0;
    featureIdx = '0';
    tests['hasOwnProperty'](featureIdx);
    featureNames = [];
    feature = (tests[featureIdx, '0'], $__.os.oid2);
    TAJS_restrictToType((feature, $__.os.oid2).name, 'string');
    featureNames['push'](TAJS_restrictToType((feature, $__.os.oid2).name, 'string')['toLowerCase']());
    TAJS_restrictToType((feature, $__.os.oid2).options, 'undefined');
    result = ((is, $__.fs.is_4)(((feature, $__.os.oid2).fn, true), 'function', 2, true, $__.uid), ((feature, $__.os.oid2).fn, true));
    nameIdx = 0;
    nameIdx < (featureNames.length, 1);
    featureName = TAJS_restrictToType(featureNames[nameIdx, 0], 'string');
    featureNameSplit = function $__lt3(res) {
        res.length = 1;
        if (res.length != 1)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt3, -2);
    }(featureName['split']('.'));
    (featureNameSplit.length, 1) === 1;
    Modernizr[TAJS_restrictToType(featureNameSplit[0, 0], 'string'), 'applicationcache'] = result;
    classes['push']((result, '') + featureNameSplit['join']('-'));
    (nameIdx = +nameIdx + 1) - 1;
    nameIdx < (featureNames.length, 1);
    '1' in vvv_tmp0;
    featureIdx = '1';
    tests['hasOwnProperty'](featureIdx);
    featureNames = [];
    feature = (tests[featureIdx, '1'], $__.os.oid5);
    TAJS_restrictToType((feature, $__.os.oid5).name, 'string');
    featureNames['push'](TAJS_restrictToType((feature, $__.os.oid5).name, 'string')['toLowerCase']());
    TAJS_restrictToType((feature, $__.os.oid5).options, 'undefined');
    result = ((is, $__.fs.is_4)((TAJS_restrictToType((feature, $__.os.oid5).fn, 'function'), $__.fs.J$__v8196551053_39_33), 'function', 3, true, $__.uid), (vvv_tmp1 = feature, 'fn', $__.fs.J$__v8196551053_39_33.call(vvv_tmp1, 0, true, $__.uid)));
    nameIdx = 0;
    nameIdx < (featureNames.length, 1);
    featureName = TAJS_restrictToType(featureNames[nameIdx, 0], 'string');
    featureNameSplit = function $__lt4(res) {
        res.length = 1;
        if (res.length != 1)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt4, -2);
    }(featureName['split']('.'));
    (featureNameSplit.length, 1) === 1;
    Modernizr[TAJS_restrictToType(featureNameSplit[0, 0], 'string'), 'audio'] = result;
    classes['push']((result, '') + featureNameSplit['join']('-'));
    (nameIdx = +nameIdx + 1) - 1;
    nameIdx < (featureNames.length, 1);
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
for (var featureIdx in tests) {
    if (tests.hasOwnProperty(featureIdx)) {
        featureNames = [];
        feature = tests[featureIdx];
        if (feature.name) {
            featureNames.push(feature.name.toLowerCase());
            if (feature.options && feature.options.aliases && feature.options.aliases.length) {
                for (aliasIdx = 0; aliasIdx < feature.options.aliases.length; aliasIdx++) {
                    featureNames.push(feature.options.aliases[aliasIdx].toLowerCase());
                }
            }
        }
        result = is(feature.fn, 'function') ? feature.fn() : feature.fn;
        for (nameIdx = 0; nameIdx < featureNames.length; nameIdx++) {
            featureName = featureNames[nameIdx];
            featureNameSplit = featureName.split('.');
            if (featureNameSplit.length === 1) {
                Modernizr[featureNameSplit[0]] = result;
            } else {
                if (Modernizr[featureNameSplit[0]] && !(Modernizr[featureNameSplit[0]] instanceof Boolean)) {
                    Modernizr[featureNameSplit[0]] = new Boolean(Modernizr[featureNameSplit[0]]);
                }
                Modernizr[featureNameSplit[0]][featureNameSplit[1]] = result;
            }
            classes.push((result ? '' : 'no-') + featureNameSplit.join('-'));
        }
    }
}});
    setClasses = ($__.fs.setClasses_6 = function setClasses(classes) {
var vvv_return, vvv_switch, className, classPrefix, reJS;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
classes = arguments[0];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    className = TAJS_restrictToType(docElement.className, 'string');
    classPrefix = (TAJS_restrictToType(((Modernizr._config, $__.os.oid0), $__.os.oid0).classPrefix, 'string'), '');
    isSVG;
    ((Modernizr._config, $__.os.oid0), $__.os.oid0).enableJSClass, true;
    reJS = new RegExp('(^|\\s)' + classPrefix + 'no-js(\\s|$)');
    className = className['replace'](reJS, '$1' + classPrefix + 'js$2');
    ((Modernizr._config, $__.os.oid0), $__.os.oid0).enableClasses, true;
    className = className + (' ' + classPrefix + classes['join'](' ' + classPrefix));
    isSVG, docElement.className = className;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
className = docElement.className;
classPrefix = Modernizr._config.classPrefix || '';
if (isSVG) {
    className = className.baseVal;
}
if (Modernizr._config.enableJSClass) {
    reJS = new RegExp('(^|\\s)' + classPrefix + 'no-js(\\s|$)');
    className = className.replace(reJS, '$1' + classPrefix + 'js$2');
}
if (Modernizr._config.enableClasses) {
    className += ' ' + classPrefix + classes.join(' ' + classPrefix);
    isSVG ? docElement.className.baseVal = className : docElement.className = className;
}});
    addTest = ($__.fs.addTest_7 = function addTest(feature, test) {
var vvv_return, vvv_switch, featureNameSplit, last;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
feature = arguments[0], test = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (typeof feature == 'object') {
    for (var key in feature) {
        if (hasOwnProp(feature, key)) {
            addTest(key, feature[key]);
        }
    }
} else {
    feature = feature.toLowerCase();
    featureNameSplit = feature.split('.');
    last = Modernizr[featureNameSplit[0]];
    if (featureNameSplit.length == 2) {
        last = last[featureNameSplit[1]];
    }
    if (typeof last != 'undefined') {
        return Modernizr;
    }
    test = typeof test == 'function' ? test() : test;
    if (featureNameSplit.length == 1) {
        Modernizr[featureNameSplit[0]] = test;
    } else {
        if (Modernizr[featureNameSplit[0]] && !(Modernizr[featureNameSplit[0]] instanceof Boolean)) {
            Modernizr[featureNameSplit[0]] = new Boolean(Modernizr[featureNameSplit[0]]);
        }
        Modernizr[featureNameSplit[0]][featureNameSplit[1]] = test;
    }
    setClasses([(!!test && test != false ? '' : 'no-') + featureNameSplit.join('-')]);
    Modernizr._trigger(feature, test);
}
return Modernizr;});
    createElement = ($__.fs.createElement_8 = function createElement() {
var vvv_return, vvv_switch, vvv_tmp0;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
switch (vvv_switch) {
case 0:
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(0 <= vvv_switch && vvv_switch <= 1);
    typeof TAJS_restrictToType(document.createElement, 'function') !== 'function';
    isSVG;
    return TAJS_restrictToType(document.createElement, 'function')['call'](document, (vvv_tmp0 = arguments, vvv_tmp0[0]));
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (typeof document.createElement !== 'function') {
    return document.createElement(arguments[0]);
} else if (isSVG) {
    return document.createElementNS.call(document, 'http://www.w3.org/2000/svg', arguments[0]);
} else {
    return document.createElement.apply(document, arguments);
}});
    cssToDOM = ($__.fs.cssToDOM_9 = function cssToDOM(name) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return name.replace(/([a-z])-([a-z])/g, function (str, m1, m2) {
    return m1 + m2.toUpperCase();
}).replace(/^-/, '');});
    domToCSS = ($__.fs.domToCSS_10 = function domToCSS(name) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return name.replace(/([A-Z])/g, function (str, m1) {
    return '-' + m1.toLowerCase();
}).replace(/^ms-/, '-ms-');});
    getBody = ($__.fs.getBody_11 = function getBody() {
var vvv_return, vvv_switch, body;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
body = document.body;
if (!body) {
    body = createElement(isSVG ? 'svg' : 'body');
    body.fake = true;
}
return body;});
    injectElementWithStyles = ($__.fs.injectElementWithStyles_12 = function injectElementWithStyles(rule, callback, nodes, testnames) {
var vvv_return, vvv_switch, mod, style, ret, node, docOverflow, div, body;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
rule = arguments[0], callback = arguments[1], nodes = arguments[2], testnames = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
mod = 'modernizr';
div = createElement('div');
body = getBody();
if (parseInt(nodes, 10)) {
    while (nodes--) {
        node = createElement('div');
        node.id = testnames ? testnames[nodes] : mod + (nodes + 1);
        div.appendChild(node);
    }
}
style = createElement('style');
style.type = 'text/css';
style.id = 's' + mod;
(!body.fake ? div : body).appendChild(style);
body.appendChild(div);
if (style.styleSheet) {
    style.styleSheet.cssText = rule;
} else {
    style.appendChild(document.createTextNode(rule));
}
div.id = mod;
if (body.fake) {
    body.style.background = '';
    body.style.overflow = 'hidden';
    docOverflow = docElement.style.overflow;
    docElement.style.overflow = 'hidden';
    docElement.appendChild(body);
}
ret = callback(div, rule);
if (body.fake) {
    body.parentNode.removeChild(body);
    docElement.style.overflow = docOverflow;
    docElement.offsetHeight;
} else {
    div.parentNode.removeChild(div);
}
return !!ret;});
    contains = ($__.fs.contains_13 = function contains(str, substr) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
str = arguments[0], substr = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return !!~('' + str).indexOf(substr);});
    nativeTestProps = ($__.fs.nativeTestProps_14 = function nativeTestProps(props, value) {
var vvv_return, vvv_switch, i, conditionText;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
props = arguments[0], value = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
i = props.length;
if ('CSS' in window && 'supports' in window.CSS) {
    while (i--) {
        if (window.CSS.supports(domToCSS(props[i]), value)) {
            return true;
        }
    }
    return false;
} else if ('CSSSupportsRule' in window) {
    conditionText = [];
    while (i--) {
        conditionText.push('(' + domToCSS(props[i]) + ':' + value + ')');
    }
    conditionText = conditionText.join(' or ');
    return injectElementWithStyles('@supports (' + conditionText + ') { #modernizr { position: absolute; } }', function (node) {
        return getComputedStyle(node, null).position == 'absolute';
    });
}
return undefined;});
    fnBind = ($__.fs.fnBind_15 = function fnBind(fn, that) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fn = arguments[0], that = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return function () {
    return fn.apply(that, arguments);
};});
    testDOMProps = ($__.fs.testDOMProps_16 = function testDOMProps(props, obj, elem) {
var vvv_return, vvv_switch, item;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
props = arguments[0], obj = arguments[1], elem = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
for (var i in props) {
    if (props[i] in obj) {
        if (elem === false) {
            return props[i];
        }
        item = obj[props[i]];
        if (is(item, 'function')) {
            return fnBind(item, elem || obj);
        }
        return item;
    }
}
return false;});
    testProps = ($__.fs.testProps_17 = function testProps(props, prefixed, value, skipValueTest) {
var vvv_return, vvv_switch, result, afterInit, i, propsLength, prop, before, elems, cleanElems;
cleanElems = function cleanElems() {
    if (afterInit) {
        delete mStyle.style;
        delete mStyle.modElem;
    }
};
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
props = arguments[0], prefixed = arguments[1], value = arguments[2], skipValueTest = arguments[3];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
skipValueTest = is(skipValueTest, 'undefined') ? false : skipValueTest;
if (!is(value, 'undefined')) {
    result = nativeTestProps(props, value);
    if (!is(result, 'undefined')) {
        return result;
    }
}
elems = [
    'modernizr',
    'tspan',
    'samp'
];
while (!mStyle.style && elems.length) {
    afterInit = true;
    mStyle.modElem = createElement(elems.shift());
    mStyle.style = mStyle.modElem.style;
}
propsLength = props.length;
for (i = 0; i < propsLength; i++) {
    prop = props[i];
    before = mStyle.style[prop];
    if (contains(prop, '-')) {
        prop = cssToDOM(prop);
    }
    if (mStyle.style[prop] !== undefined) {
        if (!skipValueTest && !is(value, 'undefined')) {
            try {
                mStyle.style[prop] = value;
            } catch (e) {
            }
            if (mStyle.style[prop] != before) {
                cleanElems();
                return prefixed == 'pfx' ? prop : true;
            }
        } else {
            cleanElems();
            return prefixed == 'pfx' ? prop : true;
        }
    }
}
cleanElems();
return false;});
    testPropsAll = ($__.fs.testPropsAll_18 = function testPropsAll(prop, prefixed, elem, value, skipValueTest) {
var vvv_return, vvv_switch, ucProp, props;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0], prefixed = arguments[1], elem = arguments[2], value = arguments[3], skipValueTest = arguments[4];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
ucProp = prop.charAt(0).toUpperCase() + prop.slice(1);
props = (prop + ' ' + cssomPrefixes.join(ucProp + ' ') + ucProp).split(' ');
if (is(prefixed, 'string') || is(prefixed, 'undefined')) {
    return testProps(props, prefixed, value, skipValueTest);
} else {
    props = (prop + ' ' + domPrefixes.join(ucProp + ' ') + ucProp).split(' ');
    return testDOMProps(props, prefixed, elem);
}});
    testAllProps = ($__.fs.testAllProps_19 = function testAllProps(prop, value, skipValueTest) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0], value = arguments[1], skipValueTest = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return testPropsAll(prop, undefined, undefined, value, skipValueTest);});
    classes = [];
    tests = [];
    ModernizrProto = $__.os.oid1 = {
        '_version': '3.3.1',
        '_config': $__.os.oid0 = {
            'classPrefix': '',
            'enableClasses': true,
            'enableJSClass': true,
            'usePrefixes': true
        },
        '_q': [],
        'on': ($__.fs.J$__v8196551053_3_20 = function J$__v8196551053_3(test, cb) {
var vvv_return, vvv_switch, self;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
test = arguments[0], cb = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
self = this;
setTimeout(function () {
    cb(self[test]);
}, 0);}),
        'addTest': ($__.fs.J$__v8196551053_5_21 = function J$__v8196551053_5(name, fn, options) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    TAJS_invariant(typeof arguments[arguments.length - 1] === 'symbol');
    TAJS_invariant($__.refs.pop.call(arguments) === $__.uid);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
name = arguments[0], fn = arguments[1], options = arguments[2];
switch (vvv_switch) {
case 0:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 0);
    tests['push']($__.os.oid2 = {
        'name': name,
        'fn': fn,
        'options': options
    });
    return;
case 1:
    TAJS_invariant(typeof vvv_switch === 'number');
    TAJS_invariant(vvv_switch === 1);
    tests['push']($__.os.oid5 = {
        'name': name,
        'fn': fn,
        'options': options
    });
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
tests.push({
    name: name,
    fn: fn,
    options: options
});}),
        'addAsyncTest': ($__.fs.J$__v8196551053_7_22 = function J$__v8196551053_7(fn) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
fn = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
tests.push({
    name: null,
    fn: fn
});})
    };
    Modernizr = ($__.fs.J$__v8196551053_9_23 = function J$__v8196551053_9() {
var vvv_return, vvv_switch;
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
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');});
    Modernizr.prototype = ModernizrProto;
    Modernizr = new (Modernizr, $__.fs.J$__v8196551053_9_23)(0, true, $__.uid);
    vvv_tmp0 = Modernizr, 'addTest', $__.fs.J$__v8196551053_5_21.call(vvv_tmp0, 'applicationcache', 'applicationCache' in window, 0, true, $__.uid);
    prefixes = (((((ModernizrProto, $__.os.oid1)._config, $__.os.oid0), $__.os.oid0).usePrefixes, true), function $__lt0(res) {
        res.length = 6;
        if (res.length != 6)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt0, -2);
    }(' -webkit- -moz- -o- -ms- '['split'](' ')));
    (ModernizrProto, $__.os.oid1)._prefixes = prefixes;
    docElement = TAJS_restrictToType(document.documentElement, 'HTMLHtmlElement');
    isSVG = TAJS_restrictToType(docElement.nodeName, 'string')['toLowerCase']() === 'svg';
    omPrefixes = 'Moz O ms Webkit';
    domPrefixes = (((((ModernizrProto, $__.os.oid1)._config, $__.os.oid0), $__.os.oid0).usePrefixes, true), function $__lt1(res) {
        res.length = 4;
        if (res.length != 4)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt1, -2);
    }(omPrefixes['toLowerCase']()['split'](' ')));
    (ModernizrProto, $__.os.oid1)._domPrefixes = domPrefixes;
    (($__.fs.J$__v8196551053_18_24 = function J$__v8196551053_18() {
var vvv_return, vvv_switch, _hasOwnProperty;
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
    _hasOwnProperty = TAJS_restrictToType(($__.os.oid3 = {}, $__.os.oid3).hasOwnProperty, 'function');
    !(is, $__.fs.is_4)(_hasOwnProperty, 'undefined', 0, true, $__.uid), !(is, $__.fs.is_4)(TAJS_restrictToType(_hasOwnProperty.call, 'function'), 'undefined', 1, true, $__.uid);
    hasOwnProp = ($__.fs.J$__v8196551053_14_25 = function J$__v8196551053_14(object, property) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
object = arguments[0], property = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return _hasOwnProperty.call(object, property);});
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
_hasOwnProperty = {}.hasOwnProperty;
if (!is(_hasOwnProperty, 'undefined') && !is(_hasOwnProperty.call, 'undefined')) {
    hasOwnProp = function (object, property) {
        return _hasOwnProperty.call(object, property);
    };
} else {
    hasOwnProp = function (object, property) {
        return property in object && is(object.constructor.prototype[property], 'undefined');
    };
}}), $__.fs.J$__v8196551053_18_24)(0, true, $__.uid);
    (ModernizrProto, $__.os.oid1)._l = $__.os.oid4 = {};
    (ModernizrProto, $__.os.oid1).on = ($__.fs.J$__v8196551053_22_26 = function J$__v8196551053_22(feature, cb) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
feature = arguments[0], cb = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this._l[feature]) {
    this._l[feature] = [];
}
this._l[feature].push(cb);
if (Modernizr.hasOwnProperty(feature)) {
    setTimeout(function () {
        Modernizr._trigger(feature, Modernizr[feature]);
    }, 0);
}});
    (ModernizrProto, $__.os.oid1)._trigger = ($__.fs.J$__v8196551053_26_27 = function J$__v8196551053_26(feature, res) {
var vvv_return, vvv_switch, cbs;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
feature = arguments[0], res = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!this._l[feature]) {
    return;
}
cbs = this._l[feature];
setTimeout(function () {
    var i, cb;
    for (i = 0; i < cbs.length; i++) {
        cb = cbs[i];
        cb(res);
    }
}, 0);
delete this._l[feature];});
    TAJS_restrictToType(Modernizr._q, 'object')['push']((($__.fs.J$__v8196551053_29_28 = function J$__v8196551053_29() {
var vvv_return, vvv_switch;
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
    (ModernizrProto, $__.os.oid1).addTest = addTest;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
ModernizrProto.addTest = addTest;}, $__.fs.J$__v8196551053_29_28_0 = $__.fs.J$__v8196551053_29_28, $__.fs.J$__v8196551053_29_28), $__.fs.J$__v8196551053_29_28_0));
    cssomPrefixes = (((((ModernizrProto, $__.os.oid1)._config, $__.os.oid0), $__.os.oid0).usePrefixes, true), function $__lt2(res) {
        res.length = 4;
        if (res.length != 4)
            TAJS_dumpValue('Possible loss of precision');
        return res;
        ;
        TAJS_makeContextSensitive($__lt2, -2);
    }(omPrefixes['split'](' ')));
    (ModernizrProto, $__.os.oid1)._cssomPrefixes = cssomPrefixes;
    atRule = ($__.fs.J$__v8196551053_31_29 = function J$__v8196551053_31(prop) {
var vvv_return, vvv_switch, length, cssrule, rule, prefix, thisRule;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
length = prefixes.length;
cssrule = window.CSSRule;
if (typeof cssrule === 'undefined') {
    return undefined;
}
if (!prop) {
    return false;
}
prop = prop.replace(/^@/, '');
rule = prop.replace(/-/g, '_').toUpperCase() + '_RULE';
if (rule in cssrule) {
    return '@' + prop;
}
for (var i = 0; i < length; i++) {
    prefix = prefixes[i];
    thisRule = prefix.toUpperCase() + '_' + rule;
    if (thisRule in cssrule) {
        return '@-' + prefix.toLowerCase() + '-' + prop;
    }
}
return false;});
    (ModernizrProto, $__.os.oid1).atRule = atRule;
    hasEvent = (($__.fs.J$__v8196551053_35_30 = function J$__v8196551053_35() {
var vvv_return, vvv_switch, needsFallback, inner;
inner = function inner(eventName, element) {
    var isSupported;
    if (!eventName) {
        return false;
    }
    if (!element || typeof element === 'string') {
        element = createElement(element || 'div');
    }
    eventName = 'on' + eventName;
    isSupported = eventName in element;
    if (!isSupported && needsFallback) {
        if (!element.setAttribute) {
            element = createElement('div');
        }
        element.setAttribute(eventName, '');
        isSupported = typeof element[eventName] === 'function';
        if (element[eventName] !== undefined) {
            element[eventName] = undefined;
        }
        element.removeAttribute(eventName);
    }
    return isSupported;
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
    inner = ($__.fs.inner_31 = function inner(eventName, element) {
var vvv_return, vvv_switch, isSupported;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
eventName = arguments[0], element = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (!eventName) {
    return false;
}
if (!element || typeof element === 'string') {
    element = createElement(element || 'div');
}
eventName = 'on' + eventName;
isSupported = eventName in element;
if (!isSupported && needsFallback) {
    if (!element.setAttribute) {
        element = createElement('div');
    }
    element.setAttribute(eventName, '');
    isSupported = typeof element[eventName] === 'function';
    if (element[eventName] !== undefined) {
        element[eventName] = undefined;
    }
    element.removeAttribute(eventName);
}
return isSupported;});
    needsFallback = !('onblur' in TAJS_restrictToType(document.documentElement, 'HTMLHtmlElement'));
    return inner;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
needsFallback = !('onblur' in document.documentElement);
return inner;}), $__.fs.J$__v8196551053_35_30)(0, true, $__.uid);
    (ModernizrProto, $__.os.oid1).hasEvent = hasEvent;
    prefixedCSSValue = ($__.fs.J$__v8196551053_37_32 = function J$__v8196551053_37(prop, value) {
var vvv_return, vvv_switch, result, elem, style, i;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0], value = arguments[1];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
result = false;
elem = createElement('div');
style = elem.style;
if (prop in style) {
    i = domPrefixes.length;
    style[prop] = value;
    result = style[prop];
    while (i-- && !result) {
        style[prop] = '-' + domPrefixes[i] + '-' + value;
        result = style[prop];
    }
}
if (result === '') {
    result = false;
}
return result;});
    (ModernizrProto, $__.os.oid1).prefixedCSSValue = prefixedCSSValue;
    vvv_tmp1 = Modernizr, 'addTest', $__.fs.J$__v8196551053_5_21.call(vvv_tmp1, 'audio', ($__.fs.J$__v8196551053_39_33 = function J$__v8196551053_39() {
var vvv_return, vvv_switch, elem, bool;
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
    elem = (createElement, $__.fs.createElement_8)('audio', 1, true, $__.uid);
    bool = false;
    bool = !!TAJS_restrictToType(elem.canPlayType, 'function');
    bool = new Boolean(bool);
    bool.ogg = elem['canPlayType']('audio/ogg; codecs="vorbis"')['replace'](/^no$/, '');
    bool.mp3 = elem['canPlayType']('audio/mpeg; codecs="mp3"')['replace'](/^no$/, '');
    bool.opus = elem['canPlayType']('audio/ogg; codecs="opus"');
    bool.wav = elem['canPlayType']('audio/wav; codecs="1"')['replace'](/^no$/, '');
    bool.m4a = elem['canPlayType']('audio/x-m4a;')['replace'](/^no$/, '');
    return bool;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
elem = createElement('audio');
bool = false;
try {
    if (bool = !!elem.canPlayType) {
        bool = new Boolean(bool);
        bool.ogg = elem.canPlayType('audio/ogg; codecs="vorbis"').replace(/^no$/, '');
        bool.mp3 = elem.canPlayType('audio/mpeg; codecs="mp3"').replace(/^no$/, '');
        bool.opus = elem.canPlayType('audio/ogg; codecs="opus"') || elem.canPlayType('audio/webm; codecs="opus"').replace(/^no$/, '');
        bool.wav = elem.canPlayType('audio/wav; codecs="1"').replace(/^no$/, '');
        bool.m4a = (elem.canPlayType('audio/x-m4a;') || elem.canPlayType('audio/aac;')).replace(/^no$/, '');
    }
} catch (e) {
}
return bool;}), 1, true, $__.uid);
    mq = (($__.fs.J$__v8196551053_55_34 = function J$__v8196551053_55() {
var vvv_return, vvv_switch, matchMedia;
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
    matchMedia = TAJS_restrictToType(window.matchMedia, 'function');
    matchMedia;
    return ($__.fs.J$__v8196551053_49_35 = function J$__v8196551053_49(mq) {
var vvv_return, vvv_switch, mql;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
mq = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
mql = matchMedia(mq);
return mql && mql.matches || false;});
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
matchMedia = window.matchMedia || window.msMatchMedia;
if (matchMedia) {
    return function (mq) {
        var mql = matchMedia(mq);
        return mql && mql.matches || false;
    };
}
return function (mq) {
    var bool = false;
    injectElementWithStyles('@media ' + mq + ' { #modernizr { position: absolute; } }', function (node) {
        bool = (window.getComputedStyle ? window.getComputedStyle(node, null) : node.currentStyle).position == 'absolute';
    });
    return bool;
};}), $__.fs.J$__v8196551053_55_34)(0, true, $__.uid);
    (ModernizrProto, $__.os.oid1).mq = mq;
    testStyles = (ModernizrProto, $__.os.oid1).testStyles = injectElementWithStyles;
    modElem = $__.os.oid6 = {
        'elem': (createElement, $__.fs.createElement_8)('modernizr', 0, true, $__.uid)
    };
    TAJS_restrictToType(Modernizr._q, 'object')['push']((($__.fs.J$__v8196551053_65_36 = function J$__v8196551053_65() {
var vvv_return, vvv_switch;
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
    delete modElem.elem;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
delete modElem.elem;}, $__.fs.J$__v8196551053_65_36_1 = $__.fs.J$__v8196551053_65_36, $__.fs.J$__v8196551053_65_36), $__.fs.J$__v8196551053_65_36_1));
    mStyle = $__.os.oid7 = {
        'style': TAJS_restrictToType(TAJS_restrictToType((modElem, $__.os.oid6).elem, 'HTMLUnknownElement').style, 'object')
    };
    TAJS_restrictToType(Modernizr._q, 'object')['unshift']((($__.fs.J$__v8196551053_67_37 = function J$__v8196551053_67() {
var vvv_return, vvv_switch;
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
    delete mStyle.style;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
delete mStyle.style;}, $__.fs.J$__v8196551053_67_37_2 = $__.fs.J$__v8196551053_67_37, $__.fs.J$__v8196551053_67_37), $__.fs.J$__v8196551053_67_37_2));
    testProp = (ModernizrProto, $__.os.oid1).testProp = ($__.fs.J$__v8196551053_71_38 = function J$__v8196551053_71(prop, value, useValue) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0], value = arguments[1], useValue = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
return testProps([prop], undefined, value, useValue);});
    (ModernizrProto, $__.os.oid1).testAllProps = testPropsAll;
    prefixed = (ModernizrProto, $__.os.oid1).prefixed = ($__.fs.J$__v8196551053_74_39 = function J$__v8196551053_74(prop, obj, elem) {
var vvv_return, vvv_switch;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0], obj = arguments[1], elem = arguments[2];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
if (prop.indexOf('@') === 0) {
    return atRule(prop);
}
if (prop.indexOf('-') != -1) {
    prop = cssToDOM(prop);
}
if (!obj) {
    return testPropsAll(prop, 'pfx');
} else {
    return testPropsAll(prop, obj, elem);
}});
    prefixedCSS = (ModernizrProto, $__.os.oid1).prefixedCSS = ($__.fs.J$__v8196551053_76_40 = function J$__v8196551053_76(prop) {
var vvv_return, vvv_switch, prefixedProp;
if (arguments[arguments.length - 1] === $__.uid) {
    $__.refs.pop.call(arguments);
    vvv_return = $__.refs.pop.call(arguments);
    vvv_switch = $__.refs.pop.call(arguments);
}
prop = arguments[0];
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
prefixedProp = prefixed(prop);
return prefixedProp && domToCSS(prefixedProp);});
    (ModernizrProto, $__.os.oid1).testAllProps = testAllProps;
    (testRunner, $__.fs.testRunner_5)(0, true, $__.uid);
    (setClasses, $__.fs.setClasses_6)(classes, 0, true, $__.uid);
    delete ModernizrProto.addTest;
    delete ModernizrProto.addAsyncTest;
    i = 0;
    i < (TAJS_restrictToType(Modernizr._q, 'object').length, 3);
    vvv_tmp2 = TAJS_restrictToType(Modernizr._q, 'object'), i, $__.fs.J$__v8196551053_67_37.call(vvv_tmp2, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < (TAJS_restrictToType(Modernizr._q, 'object').length, 3);
    vvv_tmp3 = TAJS_restrictToType(Modernizr._q, 'object'), i, $__.fs.J$__v8196551053_29_28.call(vvv_tmp3, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < (TAJS_restrictToType(Modernizr._q, 'object').length, 3);
    vvv_tmp4 = TAJS_restrictToType(Modernizr._q, 'object'), i, $__.fs.J$__v8196551053_65_36.call(vvv_tmp4, 0, true, $__.uid);
    (i = +i + 1) - 1;
    i < (TAJS_restrictToType(Modernizr._q, 'object').length, 3);
    window.Modernizr = Modernizr;
    return;
}
if (vvv_return)
    return;
TAJS_dumpValue('Not ' + 'reachable');
classes = [];
tests = [];
ModernizrProto = {
    _version: '3.3.1',
    _config: {
        'classPrefix': '',
        'enableClasses': true,
        'enableJSClass': true,
        'usePrefixes': true
    },
    _q: [],
    on: function (test, cb) {
        var self = this;
        setTimeout(function () {
            cb(self[test]);
        }, 0);
    },
    addTest: function (name, fn, options) {
        tests.push({
            name: name,
            fn: fn,
            options: options
        });
    },
    addAsyncTest: function (fn) {
        tests.push({
            name: null,
            fn: fn
        });
    }
};
Modernizr = function () {
};
Modernizr.prototype = ModernizrProto;
Modernizr = new Modernizr();
Modernizr.addTest('applicationcache', 'applicationCache' in window);
prefixes = ModernizrProto._config.usePrefixes ? ' -webkit- -moz- -o- -ms- '.split(' ') : [
    '',
    ''
];
ModernizrProto._prefixes = prefixes;
;
;
docElement = document.documentElement;
isSVG = docElement.nodeName.toLowerCase() === 'svg';
;
omPrefixes = 'Moz O ms Webkit';
domPrefixes = ModernizrProto._config.usePrefixes ? omPrefixes.toLowerCase().split(' ') : [];
ModernizrProto._domPrefixes = domPrefixes;
(function () {
    var _hasOwnProperty = {}.hasOwnProperty;
    if (!is(_hasOwnProperty, 'undefined') && !is(_hasOwnProperty.call, 'undefined')) {
        hasOwnProp = function (object, property) {
            return _hasOwnProperty.call(object, property);
        };
    } else {
        hasOwnProp = function (object, property) {
            return property in object && is(object.constructor.prototype[property], 'undefined');
        };
    }
}());
ModernizrProto._l = {};
ModernizrProto.on = function (feature, cb) {
    if (!this._l[feature]) {
        this._l[feature] = [];
    }
    this._l[feature].push(cb);
    if (Modernizr.hasOwnProperty(feature)) {
        setTimeout(function () {
            Modernizr._trigger(feature, Modernizr[feature]);
        }, 0);
    }
};
ModernizrProto._trigger = function (feature, res) {
    if (!this._l[feature]) {
        return;
    }
    var cbs = this._l[feature];
    setTimeout(function () {
        var i, cb;
        for (i = 0; i < cbs.length; i++) {
            cb = cbs[i];
            cb(res);
        }
    }, 0);
    delete this._l[feature];
};
Modernizr._q.push(function () {
    ModernizrProto.addTest = addTest;
});
cssomPrefixes = ModernizrProto._config.usePrefixes ? omPrefixes.split(' ') : [];
ModernizrProto._cssomPrefixes = cssomPrefixes;
atRule = function (prop) {
    var length = prefixes.length;
    var cssrule = window.CSSRule;
    var rule;
    if (typeof cssrule === 'undefined') {
        return undefined;
    }
    if (!prop) {
        return false;
    }
    prop = prop.replace(/^@/, '');
    rule = prop.replace(/-/g, '_').toUpperCase() + '_RULE';
    if (rule in cssrule) {
        return '@' + prop;
    }
    for (var i = 0; i < length; i++) {
        var prefix = prefixes[i];
        var thisRule = prefix.toUpperCase() + '_' + rule;
        if (thisRule in cssrule) {
            return '@-' + prefix.toLowerCase() + '-' + prop;
        }
    }
    return false;
};
ModernizrProto.atRule = atRule;
;
hasEvent = function () {
    var needsFallback = !('onblur' in document.documentElement);
    function inner(eventName, element) {
        var isSupported;
        if (!eventName) {
            return false;
        }
        if (!element || typeof element === 'string') {
            element = createElement(element || 'div');
        }
        eventName = 'on' + eventName;
        isSupported = eventName in element;
        if (!isSupported && needsFallback) {
            if (!element.setAttribute) {
                element = createElement('div');
            }
            element.setAttribute(eventName, '');
            isSupported = typeof element[eventName] === 'function';
            if (element[eventName] !== undefined) {
                element[eventName] = undefined;
            }
            element.removeAttribute(eventName);
        }
        return isSupported;
    }
    return inner;
}();
ModernizrProto.hasEvent = hasEvent;
prefixedCSSValue = function (prop, value) {
    var result = false;
    var elem = createElement('div');
    var style = elem.style;
    if (prop in style) {
        var i = domPrefixes.length;
        style[prop] = value;
        result = style[prop];
        while (i-- && !result) {
            style[prop] = '-' + domPrefixes[i] + '-' + value;
            result = style[prop];
        }
    }
    if (result === '') {
        result = false;
    }
    return result;
};
ModernizrProto.prefixedCSSValue = prefixedCSSValue;
Modernizr.addTest('audio', function () {
    var elem = createElement('audio');
    var bool = false;
    try {
        if (bool = !!elem.canPlayType) {
            bool = new Boolean(bool);
            bool.ogg = elem.canPlayType('audio/ogg; codecs="vorbis"').replace(/^no$/, '');
            bool.mp3 = elem.canPlayType('audio/mpeg; codecs="mp3"').replace(/^no$/, '');
            bool.opus = elem.canPlayType('audio/ogg; codecs="opus"') || elem.canPlayType('audio/webm; codecs="opus"').replace(/^no$/, '');
            bool.wav = elem.canPlayType('audio/wav; codecs="1"').replace(/^no$/, '');
            bool.m4a = (elem.canPlayType('audio/x-m4a;') || elem.canPlayType('audio/aac;')).replace(/^no$/, '');
        }
    } catch (e) {
    }
    return bool;
});
;
;
;
;
mq = function () {
    var matchMedia = window.matchMedia || window.msMatchMedia;
    if (matchMedia) {
        return function (mq) {
            var mql = matchMedia(mq);
            return mql && mql.matches || false;
        };
    }
    return function (mq) {
        var bool = false;
        injectElementWithStyles('@media ' + mq + ' { #modernizr { position: absolute; } }', function (node) {
            bool = (window.getComputedStyle ? window.getComputedStyle(node, null) : node.currentStyle).position == 'absolute';
        });
        return bool;
    };
}();
ModernizrProto.mq = mq;
testStyles = ModernizrProto.testStyles = injectElementWithStyles;
;
;
;
;
modElem = {
    elem: createElement('modernizr')
};
Modernizr._q.push(function () {
    delete modElem.elem;
});
mStyle = {
    style: modElem.elem.style
};
Modernizr._q.unshift(function () {
    delete mStyle.style;
});
;
testProp = ModernizrProto.testProp = function (prop, value, useValue) {
    return testProps([prop], undefined, value, useValue);
};
ModernizrProto.testAllProps = testPropsAll;
prefixed = ModernizrProto.prefixed = function (prop, obj, elem) {
    if (prop.indexOf('@') === 0) {
        return atRule(prop);
    }
    if (prop.indexOf('-') != -1) {
        prop = cssToDOM(prop);
    }
    if (!obj) {
        return testPropsAll(prop, 'pfx');
    } else {
        return testPropsAll(prop, obj, elem);
    }
};
prefixedCSS = ModernizrProto.prefixedCSS = function (prop) {
    var prefixedProp = prefixed(prop);
    return prefixedProp && domToCSS(prefixedProp);
};
ModernizrProto.testAllProps = testAllProps;
testRunner();
setClasses(classes);
delete ModernizrProto.addTest;
delete ModernizrProto.addAsyncTest;
for (var i = 0; i < Modernizr._q.length; i++) {
    Modernizr._q[i]();
}
window.Modernizr = Modernizr;
;})), $__.fs.J$__v8196551053_79_3))(window, document, 0, true, $__.uid);