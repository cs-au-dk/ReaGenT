/*
declare module module {
    class Foo {
        constructor(n: number);
    }
    class Bar extends Foo {
        // constructor(s: string); // <- in this case, the constructor is inherited from Foo.
    }

    function foo(f: typeof Foo): true;
}
*/

var Foo = function () {

};

var counter = 0;
var hasSeenConstructed = false;

module.exports = {
    Foo: Foo,
    Bar: Foo,
    Baz: Foo,
    foo: function (f, b) {
        counter++;
        if (f !== Foo) {
            hasSeenConstructed = true;
        }
        if (counter > 100 && !hasSeenConstructed) {
            return "did not see a constructed value in 100 tries";
        }
        new f(123); // <- valid constructor call

        new b("string"); // <- valid constructor call

        new b(123); // invalid constructor call
        return true;
    }
};