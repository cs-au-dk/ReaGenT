/*
interface Foo {
    foo: true;
}
interface Bar extends Foo {
    bar: true;
}


export module module {
    function getFoo(): Foo;
    function getBar(): Bar;
    function foo(f: Foo): true;
    function bar(b: Bar): true;
}
*/

module.exports = {
    getFoo: function () {
        return {
            foo: true,
            isLib: true
        }
    },
    getBar: function () {
        return {
            foo: true,
            bar: true,
            isLib: true
        }
    },
    bar: function (b) { // <- this has the side-effect.
        b.isLib = Math.random() > 0.5;
        return true;
    },
    foo: function (f) { // <- this one gets the error.
        return f.isLib;
    }
};