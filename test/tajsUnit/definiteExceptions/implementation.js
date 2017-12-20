/*
export module module {
    function foo00(): string; // <- returns bool | string (maybe error).
    function foo0(): string; // <- returns bool (definite error).
    function foo1(): string | number; // <- returns bool (definite error).
    function foo2(): string | boolean; // <- returns bool | number (maybe error).
    function foo3(): {foo: boolean} // returns {foo: string} (definite error)
    function foo4(): {foo: boolean} // returns {foo: string | boolean} (maybe error)
    function foo5(): {foo: boolean} // returns {foo: string} | {foo: boolean} (maybe error)
}*/


var bool = Math.random() > 0.5;
var number = Math.random() * Math.random() + 2 * Math.random();
var string = Math.random() + "[foobar]" + Math.random();

module.exports = {
    foo00: function () {
        return Math.random() > 0.5 ? bool : string;
    },
    foo0: function () {
        return bool;
    },
    foo1: function () {
        return bool;
    },
    foo2: function () {
        return Math.random() > 0.5 ? bool : number;
    },
    foo3: function () {
        return {foo: string};
    },
    foo4: function () {
        return {foo: Math.random() > 0.5 ? string : bool};
    },
    foo5: function () {
        return Math.random() > 0.5 ? {foo: string} : {foo: bool};
    }
};