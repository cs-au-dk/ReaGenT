/*interface Foo {
    [index: string]: Foo;
}

export module module {
    function foo(f: Foo): undefined; // <- no
}*/

module.exports = {
    foo: function (f) {
        return f.foo; // <- should not return undefined.
    }
};