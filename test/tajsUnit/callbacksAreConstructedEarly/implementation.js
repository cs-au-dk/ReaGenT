/*
// should be constructed early.
interface Callback {
    (): number;
}

// should never be constructed.
interface Foo {
    test: boolean;
}

export module module {
    function takesFoo(f: Foo): Callback;
    function takesSomeFoo(f: Foo): Callback;
    function takesCallback(c: Callback): Foo;
    function takesAFoo(f: Foo): Callback;
    function takesAFooHereAreMoreWords(f: Foo): Callback;
}*/

var takesFoo = function (f) {
    if (!f.isFoo) {
        return "Was a constructed Foo";
    }
    return function () {
        return 2;
    }
};
module.exports = {
    takesFoo: takesFoo,
    takesAFoo: takesFoo,
    takesSomeFoo: takesFoo,
    takesAFooHereAreMoreWords: takesFoo,
    takesCallback: function (c) {
        return {
            test: true,
            isFoo: true
        }
    }
};