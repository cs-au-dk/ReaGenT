/*interface Foo {
    bar: string,
    foo: Foo
}

export module module {
    function foo(f: Foo): true;
}*/


module.exports = {
    foo: function (f) {
        return typeof f.bar === "string" && typeof f.foo === "object" && typeof f.foo.foo === "object";
    }
};