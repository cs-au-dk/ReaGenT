/*
// all of this is correct
declare module foo {
    function number(): number;
    function number123(): 123;
    function bool(): boolean;
    function TRUE(): true;
    function FALSE(): false;
    function str(): string;
    function foo(): "foo"
    function undef(): undefined;
    function never(): never;
    function NULL(): null;
}
*/

module.exports = {
    number: function () {
        return Math.random();
    },
    number123: function () {
        return 123;
    },
    bool: function () {
        return true;
    },
    TRUE: function () {
        return true
    },
    FALSE: function () {
        // noinspection JSConstructorReturnsPrimitive
        return false;
    },
    str: function () {
        return Math.random().toString(26).substring(3, 4) + Math.random().toString(26).substring(3, 4) + Math.random().toString(26).substring(3, 4);
    },
    foo: function () {
        return "foo";
    },
    undef: function () {
        var foo;
        return foo;
    },
    never: function () {
        null.method();
    },
    NULL: function () {
        return null;
    }
}