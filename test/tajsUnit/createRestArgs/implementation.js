/*
// two of exactly the same, but I pass wrong arguments only the second one.
interface Foo {
    (x: boolean, ...xs: number[]): number;
}
interface Bar {
    (x: boolean, ...xs: number[]): number;
}

export module module {
    function foo(f: Foo): true;
    function bar(b: Bar): true;
}*/

module.exports = {
    foo: function (f) {
        f(true);
        f(false, 2);
        f(true, 2, 3, 4);
        return true;
    },
    bar: function (b) {
        b(true, 1, 2, 3, "NO");
        return true;
    }
};