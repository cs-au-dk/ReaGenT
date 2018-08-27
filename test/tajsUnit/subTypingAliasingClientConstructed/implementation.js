/*
interface Foo {
    foo: true;
}
interface Bar extends Foo {
    bar: true;
}


export module module {
    function foo(f: Foo): false;
    function bar(b: Bar): true;
}
*/
var bar = null;
var foo = null;


module.exports = {
    foo: function (f) {
        foo = f;
        if (foo != null && bar != null) {
            return foo === bar;
        }
        throw new Error();
    },
    bar: function (b) {
        bar = b;
        return true;
    }
};