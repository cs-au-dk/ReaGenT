/*interface Foo {
    bar: string,
    foo: Foo
}

export module module {
    function foo(f: Foo): true;
}*/


module.exports = {
    foo: function (f) {
        if (typeof f.bar !== "string") {
            return "f.bar not string";
        }
        if (typeof f.foo !== "object") {
            return "f.foo not object";
        }
        if (typeof f.foo.foo !== "object") {
            return "f.foo.foo not object";
        }
        return true;
    }
};