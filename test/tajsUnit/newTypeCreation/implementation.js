/*
type NumToString = (n: number) => string; // has a hidden "isLib" property.

// I can get NumToString, but I cannot get a clean Foo.
// This also has "bar" property that is: true.
interface Foo {
    foo: NumToString;
    dirty: number; // <- is always number|string.
}

export module module {
    function getFoo(): Foo;
    function useFoo(f: Foo): true; // this must test that the NumToString is library-constructed! And that the "bar" property is present.
}
*/

module.exports = {
    getFoo: function () {
        var dirty = 123;
        if (Math.random() > 0.5) {
            dirty = "str";
        }
        var numToString = function (n) {
            return "foo" + n;
        }
        numToString.isLib = true;

        return {
            foo: numToString,
            dirty: dirty,
            bar: true
        }
    },
    useFoo: function (foo) {
        if (!foo.bar) {
            return false;
        }
        if (!foo.foo.isLib) {
            return false;
        }
        if (typeof foo.foo(123) !== "string") {
            return false;
        }
        return foo.dirty == 123; // after str has been filtered, this should always be 123.
    }
};