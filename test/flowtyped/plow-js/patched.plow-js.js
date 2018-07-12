(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
        typeof define === 'function' && define.amd ? define(['exports'], factory) :
            (factory((global.plow = global.plow || {})));
}(this, (function (exports) { 'use strict';

    var reduceOps = function reduceOps(ops) {
        return function (subject) {
            return ops.every(function (op) {
                return op(subject);
            });
        };
    };

//
// Performs all passed operations until one of them returns false
//
    var index = (function () {
        for (var _len = arguments.length, ops = Array(_len), _key = 0; _key < _len; _key++) {
            ops[_key] = arguments[_key];
        }

        if (typeof ops[ops.length - 1] !== 'function') {
            return reduceOps(ops.slice(0, -1))(ops[ops.length - 1]);
        }

        return reduceOps(ops);
    });

//
// Allows to create function that can be called with
// argument lists and/or curried
//
    var createPolymorphFunction = (function (func) {
        return function () {
            for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
                args[_key] = arguments[_key];
            }

            return args.reduce(function (func, arg) {
                return func(arg);
            }, func);
        };
    });

//
// Resolves dot-separated string paths to arrays
//
    var resolveObjectPath = (function (path) {
        if (Array.isArray(path)) {
            return path;
        }

        if (typeof path === 'number') {
            return [path];
        }

        return path.split('.').map(function (part) {
            var partAsInteger = parseInt(part);

            if (!isNaN(partAsInteger) && String(partAsInteger) === part) {
                return partAsInteger;
            }

            return part;
        });
    });

//
// Returns a value from an object structure, addressed by `path`
//
    var $get$2 = createPolymorphFunction(function (path) {
        //
        // This function returns the path, if it is neither
        // an array nor a string nor a number
        //
        if (typeof path !== 'string' && typeof path !== 'number' && !Array.isArray(path)) {
            return path;
        }

        return function (subject) {
            if (subject && typeof subject.getIn === 'function') {
                return subject.getIn(resolveObjectPath(path));
            }

            return resolveObjectPath(path).reduce(function (subject, part) {
                return subject && subject[part];
            }, subject);
        };
    });

    var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) {
        return typeof obj;
    } : function (obj) {
        return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
    };



















    var defineProperty = function (obj, key, value) {
        if (key in obj) {
            Object.defineProperty(obj, key, {
                value: value,
                enumerable: true,
                configurable: true,
                writable: true
            });
        } else {
            obj[key] = value;
        }

        return obj;
    };





















    var slicedToArray = function () {
        function sliceIterator(arr, i) {
            var _arr = [];
            var _n = true;
            var _d = false;
            var _e = undefined;

            try {
                for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) {
                    _arr.push(_s.value);

                    if (i && _arr.length === i) break;
                }
            } catch (err) {
                _d = true;
                _e = err;
            } finally {
                try {
                    if (!_n && _i["return"]) _i["return"]();
                } finally {
                    if (_d) throw _e;
                }
            }

            return _arr;
        }

        return function (arr, i) {
            if (Array.isArray(arr)) {
                return arr;
            } else if (Symbol.iterator in Object(arr)) {
                return sliceIterator(arr, i);
            } else {
                throw new TypeError("Invalid attempt to destructure non-iterable instance");
            }
        };
    }();













    var toConsumableArray = function (arr) {
        if (Array.isArray(arr)) {
            for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) arr2[i] = arr[i];

            return arr2;
        } else {
            return Array.from(arr);
        }
    };

//
// Checks if a structure contains an item
//
    var $contains$2 = createPolymorphFunction(function (value) {
        return function (path) {
            return function (subject) {
                var object = $get$2(path, subject);

                if ((typeof object === 'undefined' ? 'undefined' : _typeof(object)) !== 'object' || !Boolean(object)) {
                    return false;
                }

                if (typeof object.includes === 'function') {
                    return object.includes(value);
                }

                return Array.isArray(object) ? object.indexOf(value) !== -1 : Object.keys(object).some(function (key) {
                    return object[key] === value;
                });
            };
        };
    });

//
// Counts values in objects, arrays or strings
//
    var index$1 = createPolymorphFunction(function (path) {
        return function (subject) {
            var object = $get$2(path, subject);

            if (!object) {
                return 0;
            }

            if (typeof object.count === 'function') {
                return object.count();
            }

            if (Array.isArray(object) || typeof object === 'string') {
                return object.length;
            }

            if ((typeof object === 'undefined' ? 'undefined' : _typeof(object)) === 'object') {
                return Object.keys(object).length;
            }

            return 0;
        };
    });

//
// Returns the head of an array
//
    var index$2 = createPolymorphFunction(function (path) {
        return function (subject) {
            var target = $get$2(path, subject);

            if (target && typeof target.first === 'function') {
                return target.first();
            }

            if (!target || !Array.isArray(target)) {
                console.warn('$head expects the target to be an array, got ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + ' instead');
                return null;
            }

            return target[0];
        };
    });

//
// Returns the last item of an array
//
    var index$3 = createPolymorphFunction(function (path) {
        return function (subject) {
            var target = $get$2(path, subject);

            if (target && typeof target.last === 'function') {
                return target.last();
            }

            if (!target || !Array.isArray(target)) {
                console.warn('$last expects the target to be an array, got ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + ' instead');
                return null;
            }

            return target[target.length - 1];
        };
    });

//
// Returns a structure of values in `subject`, addressed by a property path or a mapper function
//
    var index$4 = createPolymorphFunction(function (mapper) {
        return function (path) {
            return function (subject) {
                var object = $get$2(path, subject);

                if (object && (typeof object === 'undefined' ? 'undefined' : _typeof(object)) === 'object') {
                    if (typeof object.map === 'function') {
                        return object.map($get$2(mapper));
                    }

                    var result = {};

                    Object.keys(object).forEach(function (key) {
                        result[key] = $get$2(mapper, object[key]);
                    });

                    return result;
                }
            };
        };
    });

//
// Returns the boolean opposite of the result of the passed function
//
    var index$5 = createPolymorphFunction(function (op) {
        return function (subject) {
            return !Boolean(op(subject));
        };
    });

    var reduceOps$1 = function reduceOps(ops) {
        return function (subject) {
            return ops.some(function (op) {
                return op(subject);
            });
        };
    };

//
// Performs all passed operations until one of them returns false
//
    var index$6 = (function () {
        for (var _len = arguments.length, ops = Array(_len), _key = 0; _key < _len; _key++) {
            ops[_key] = arguments[_key];
        }

        if (typeof ops[ops.length - 1] !== 'function') {
            return reduceOps$1(ops.slice(0, -1))(ops[ops.length - 1]);
        }

        return reduceOps$1(ops);
    });

//
// Returns the head of an array
//
    var index$7 = createPolymorphFunction(function (path) {
        return function (subject) {
            var target = $get$2(path, subject);

            if (target && typeof target.rest === 'function') {
                return target.rest();
            }

            if (!target || !Array.isArray(target)) {
                console.warn('$tail expects the target to be an array, got ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + ' instead');
                return null;
            }

            return target.slice(1);
        };
    });

//
// Returns a value from an object structure, addressed by a path that is a
// value within the same object
//
    var index$8 = createPolymorphFunction(function (path) {
        //
        // This function returns the path, if it is neither
        // an array nor a string nor a number
        //
        if (typeof path !== 'string' && typeof path !== 'number' && !Array.isArray(path)) {
            return path;
        }

        return function (subject) {
            return $get$2($get$2(path, subject), subject);
        };
    });

//
// Helper function to peform the necessary recursion
//
    var recursivelySetValueInObject = function recursivelySetValueInObject(object, value, path) {
        if (path.length === 0) {
            return value;
        }

        //
        // Create missing path targets
        //
        if (typeof object === 'undefined') {
            if (typeof path[0] === 'number') {
                object = [];
            } else {
                object = {};
            }
        }

        //
        // Make sure, that array elements are always inserted at the last position, if the path exceeds the length
        // of the array
        //
        if (typeof path[0] === 'number' && Array.isArray(object) && object.length < path[0]) {
            path[0] = object.length;
        }

        object[path[0]] = recursivelySetValueInObject(object[path[0]], value, path.slice(1));

        return object;
    };

//
// Sets a value inside an object and returns the resulting object
//
    var $set$1 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                if (typeof subject !== 'undefined') {
                    if (typeof subject.setIn === 'function') {
                        return subject.setIn(resolveObjectPath(path), value);
                    }
                    var object = JSON.parse(JSON.stringify(subject));
                    return recursivelySetValueInObject(object, value, resolveObjectPath(path));
                }

                return subject;
            };
        };
    });

    var traverse = function traverse(actor) {
        return function (subject) {
            if ((typeof subject === 'undefined' ? 'undefined' : _typeof(subject)) === 'object') {
                return Object.keys(subject).reduce(function (subject, key) {
                    return Object.assign({}, subject, defineProperty({}, key, traverse(actor)(subject[key])));
                }, subject);
            }

            return actor(subject);
        };
    };

//
// Deeply applies the actor function to each member of the subject
//
    var $traverse$1 = createPolymorphFunction(traverse);

//
// Transforms the subject into a new shape
//
    var index$9 = createPolymorphFunction(function (shape) {
        return function (subject) {
            if (Object.keys(shape).length === 0) {
                return {};
            }

            return $traverse$1(function (val) {
                return typeof val === 'function' ? val(subject) : val;
            }, shape);
        };
    });

    var $and$1 = index;
    var $contains$1 = $contains$2;
    var $count$1 = index$1;
    var $get$1 = $get$2;
    var $head$1 = index$2;
    var $last$1 = index$3;
    var $map$1 = index$4;
    var $not$1 = index$5;
    var $or$1 = index$6;
    var $tail$1 = index$7;

    var $resolve$1 = index$8;
    var $transform$1 = index$9;

    var $add$2 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                var target = $get$2(path, subject);

                if ((typeof target === 'undefined' ? 'undefined' : _typeof(target)) !== 'object') {
                    console.warn('Cannot add an item to a ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + '.');
                    return subject;
                }

                if (Array.isArray(target)) {
                    return $set$1(path, [].concat(toConsumableArray(target), [value]), subject);
                }

                if (typeof target.push === 'function') {
                    return $set$1(path, target.push(value), subject);
                }

                if (typeof target.add === 'function') {
                    return $set$1(path, target.add(value), subject);
                }

                if ((typeof value === 'undefined' ? 'undefined' : _typeof(value)) !== 'object') {
                    console.warn('Only objects can be added to objects.');
                    return subject;
                }

                var keys = Object.keys(value);

                if (keys.length !== 1) {
                    console.warn('Only objects with exactly one key can be added to objects.');
                    return subject;
                }

                var _keys = slicedToArray(keys, 1),
                    key = _keys[0];

                if (typeof target[key] !== 'undefined') {
                    console.warn('Cannot add {' + key + ': ' + value[key].toString() + '} to ' + resolveObjectPath(path).join('.') + ', because it is already set.');
                    return subject;
                }

                return $set$1([].concat(toConsumableArray(resolveObjectPath(path)), [key]), value[key], subject);
            };
        };
    });

//
// Removes an item from an array or object, addressed by its path
//
    var index$10 = createPolymorphFunction(function (path) {
        return function (subject) {
            if (subject && typeof subject.deleteIn === 'function') {
                return subject.deleteIn(resolveObjectPath(path));
            }
            var resolvedPath = resolveObjectPath(path);
            var parentPath = resolvedPath.slice(0, -1);
            var key = resolvedPath[resolvedPath.length - 1];
            var parent = $get$2(parentPath, subject);

            if ((typeof parent === 'undefined' ? 'undefined' : _typeof(parent)) !== 'object') {
                console.warn('Cannot drop an item from a ' + (typeof parent === 'undefined' ? 'undefined' : _typeof(parent)) + '.');
                return subject;
            }

            if (Array.isArray(parent)) {
                return $set$1(parentPath, parent.filter(function (item, index) {
                    return index !== key;
                }), subject);
            }

            return $set$1(parentPath, Object.keys(parent).filter(function (index) {
                return index !== key;
            }).reduce(function (newObject, key) {
                newObject[key] = parent[key];
                return newObject;
            }, {}), subject);
        };
    });

    var index$11 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                if (subject && subject.mergeIn === 'function') {
                    return subject.mergeIn(resolveObjectPath(path), value);
                }
                return Object.keys(value).reduce(function (subject, key) {
                    return $set$1([].concat(toConsumableArray(resolveObjectPath(path)), [key]), value[key], subject);
                }, subject);
            };
        };
    });

    var index$12 = createPolymorphFunction(function (path) {
        return function (subject) {
            var target = $get$2(path, subject);

            if (target && !Array.isArray(target) && typeof target.pop === 'function') {
                return $set$1(path, target.pop(), subject);
            }

            if (!Array.isArray(target)) {
                console.warn('Cannot pop an item from a ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + '.');
                return subject;
            }

            return $set$1(path, [].concat(toConsumableArray(target.slice(0, -1))), subject);
        };
    });

//
// Removes all occurences of an item from an array or an object,
// adressed by its value
//
    var $remove$2 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                var object = $get$2(path, subject);

                if ((typeof object === 'undefined' ? 'undefined' : _typeof(object)) !== 'object') {
                    console.warn('Cannot remove an item from a ' + (typeof object === 'undefined' ? 'undefined' : _typeof(object)) + '.');
                    return subject;
                }

                if (typeof object.filter === 'function') {
                    return $set$1(path, object.filter(function (item) {
                        return item !== value;
                    }), subject);
                }

                return $set$1(path, Object.keys(object).filter(function (key) {
                    return object[key] !== value;
                }).reduce(function (newObject, key) {
                    newObject[key] = object[key];
                    return newObject;
                }, {}), subject);
            };
        };
    });

    var index$13 = createPolymorphFunction(function (path) {
        return function (subject) {
            var target = $get$2(path, subject);

            if (target && !Array.isArray(target) && typeof target.shift === 'function') {
                return $set$1(path, target.shift(), subject);
            }

            if (!Array.isArray(target)) {
                console.warn('Cannot shift an item from a ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + '.');
                return subject;
            }

            return $set$1(path, [].concat(toConsumableArray(target.slice(1))), subject);
        };
    });

    var index$14 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                var target = $get$2(path, subject);

                if (target && !Array.isArray(target) && typeof target.unshift === 'function') {
                    return $set$1(path, target.unshift(value), subject);
                }

                if ((typeof target === 'undefined' ? 'undefined' : _typeof(target)) !== 'object') {
                    console.warn('Cannot unshift an item to a ' + (typeof target === 'undefined' ? 'undefined' : _typeof(target)) + '.');
                    return subject;
                }

                if (Array.isArray(target)) {
                    return $set$1(path, [value].concat(toConsumableArray(target)), subject);
                }

                if ((typeof value === 'undefined' ? 'undefined' : _typeof(value)) !== 'object') {
                    console.warn('Only objects can be added to objects.');
                    return subject;
                }

                var keys = Object.keys(value);

                if (keys.length !== 1) {
                    console.warn('Only objects with exactly one key can be added to objects.');
                    return subject;
                }

                var _keys = slicedToArray(keys, 1),
                    key = _keys[0];

                if (typeof target[key] !== 'undefined') {
                    console.warn('Cannot add {' + key + ': ' + value[key].toString() + '} to ' + resolveObjectPath(path).join('.') + ', because it is already set.');
                    return subject;
                }

                return $set$1([].concat(toConsumableArray(resolveObjectPath(path)), [key]), value[key], subject);
            };
        };
    });

    var merge = function merge(path, value, subject) {
        return Object.keys(value).reduce(function (subject, key) {
            if (_typeof(value[key]) === 'object') {
                return merge([].concat(toConsumableArray(path), [key]), value[key], subject);
            }

            return $set$1([].concat(toConsumableArray(resolveObjectPath(path)), [key]), value[key], subject);
        }, subject);
    };

//
// Deeply merges two objects
//
    var index$15 = createPolymorphFunction(function (path) {
        return function (value) {
            return function (subject) {
                return subject && typeof subject.mergeDeepIn === 'function' ? subject.mergeDeepIn(resolveObjectPath(path), value) : merge(resolveObjectPath(path), value, subject);
            };
        };
    });

//
// Helper function to create empty values for various types
//
    var getEmptyValue = function getEmptyValue(value) {
        if (typeof value === 'string') {
            return '';
        }

        return null;
    };

//
// Performs different toggle mechanisms, depending on what type lies behind
// path in subject
//
// 1. Boolean: Will turn true to false and false to true
// 2. Array: Will add a value if not present and remove it otherwise
// 3. Other: Will set the target to value if it doesn't equal value or to fallback otherwise. If fallback is not set,
//           it will be replaced by a type-dependent empty value
//
    var index$16 = createPolymorphFunction(function (path) {
        return function (value) {
            var $target = $get$2(path);

            //
            // Check for opportunity of boolean toggle
            //
            if ((typeof value === 'undefined' ? 'undefined' : _typeof(value)) === 'object') {
                var target = $target(value);

                if (typeof target === 'boolean') {
                    return $set$1(path, !target, value);
                }
            } else if (typeof value === 'undefined') {
                return value;
            }

            return function (fallback) {
                //
                // Check for opportunity of array item toggle
                //
                if ((typeof fallback === 'undefined' ? 'undefined' : _typeof(fallback)) === 'object') {
                    var _target = $target(fallback);

                    //
                    // Handle Immutable JS
                    //
                    if (_target && typeof _target.delete === 'function') {
                        if ($contains$2(value, path, fallback)) {
                            // List
                            if (typeof _target.push === 'function') {
                                return $remove$2(path, value, fallback);
                            }

                            // Sets
                            return $set$1(path, _target.delete(value), fallback);
                        }

                        // List
                        if (typeof _target.push === 'function') {
                            return $set$1(path, _target.push(value), fallback);
                        }

                        // Sets
                        if (typeof _target.add === 'function') {
                            return $set$1(path, _target.add(value), fallback);
                        }
                    }

                    if (Array.isArray(_target)) {
                        if ($contains$2(value, path, fallback)) {
                            return $remove$2(path, value, fallback);
                        }

                        return $add$2(path, value, fallback);
                    }

                    //
                    // Perform value toggle with empty fallback
                    //
                    return $set$1(path, _target === value ? getEmptyValue(value) : value, fallback);
                } else if (typeof fallback === 'undefined') {
                    return fallback;
                }

                return function (subject) {
                    //
                    // Perform value toggle
                    //
                    typeof fallback === 'undefined' && (fallback = getEmptyValue(value));
                    return $set$1(path, $target(subject) === value ? fallback : value, subject);
                };
            };
        };
    });

    var $add$1 = $add$2;
    var $drop$1 = index$10;
    var $override$1 = index$11;
    var $pop$1 = index$12;
    var $remove$1 = $remove$2;
    var $set$2 = $set$1;
    var $shift$1 = index$13;
    var $unshift$1 = index$14;

    var $merge$1 = index$15;
    var $toggle$1 = index$16;

    var reduceOps$2 = function reduceOps(ops) {
        return function (subject) {
            return ops.reduce(function (subject, nextOp) {
                return nextOp(subject);
            }, subject);
        };
    };

//
// Performs all passed operations like a reversed compose
//
    var index$17 = (function () {
        for (var _len = arguments.length, ops = Array(_len), _key = 0; _key < _len; _key++) {
            ops[_key] = arguments[_key];
        }

        if (typeof ops[ops.length - 1] !== 'function') {
            return reduceOps$2(ops.slice(0, -1))(ops[ops.length - 1]);
        }

        return reduceOps$2(ops);
    });

//
// Passes the results of all functions in the first parameter to the finisher function and returns
// the finishers result
//
    var index$18 = createPolymorphFunction(function (ops) {
        return function (finisher) {
            return function (subject) {
                return finisher.apply(undefined, toConsumableArray(ops.map(function (op) {
                    return op(subject);
                })).concat([subject]));
            };
        };
    });

    var $all$1 = index$17;
    var $summarize$1 = index$18;
    var $traverse$2 = $traverse$1;

//
// Logs and returns the subject
//
    var index$19 = createPolymorphFunction(function (path) {
        if (typeof path !== 'string') {
            console.log('[Plow JS Log]', 'no path given');
            console.log('[Plow JS Log]', path);
            return path;
        }

        return function (subject) {
            var target = $get$2(path, subject);

            console.log('[Plow JS Log]', path);
            console.log('[Plow JS Log]', target);
            return subject;
        };
    });

    var $log$1 = index$19;

    var utils = {
        createPolymorphFunction: createPolymorphFunction,
        resolveObjectPath: resolveObjectPath
    };

    var $and = $and$1;
    var $contains = $contains$1;
    var $count = $count$1;
    var $get = $get$1;
    var $head = $head$1;
    var $last = $last$1;
    var $map = $map$1;
    var $not = $not$1;
    var $or = $or$1;
    var $tail = $tail$1;
    var $resolve = $resolve$1;
    var $transform = $transform$1;

    var $add = $add$1;
    var $drop = $drop$1;
    var $override = $override$1;
    var $pop = $pop$1;
    var $remove = $remove$1;
    var $set = $set$2;
    var $shift = $shift$1;
    var $unshift = $unshift$1;
    var $merge = $merge$1;
    var $toggle = $toggle$1;

    var $all = $all$1;
    var $summarize = $summarize$1;
    var $traverse = $traverse$2;

    var $log = $log$1;

    exports.$and = $and;
    exports.$contains = $contains;
    exports.$count = $count;
    exports.$get = $get;
    exports.$head = $head;
    exports.$last = $last;
    exports.$map = $map;
    exports.$not = $not;
    exports.$or = $or;
    exports.$tail = $tail;
    exports.$resolve = $resolve;
    exports.$transform = $transform;
    exports.$add = $add;
    exports.$drop = $drop;
    exports.$override = $override;
    exports.$pop = $pop;
    exports.$remove = $remove;
    exports.$set = $set;
    exports.$shift = $shift;
    exports.$unshift = $unshift;
    exports.$merge = $merge;
    exports.$toggle = $toggle;
    exports.$all = $all;
    exports.$summarize = $summarize;
    exports.$traverse = $traverse;
    exports.$log = $log;
    exports.utils = utils;

    Object.defineProperty(exports, '__esModule', { value: true });

})));