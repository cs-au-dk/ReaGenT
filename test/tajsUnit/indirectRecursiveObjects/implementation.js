/*interface Foo {
    isFoo: true;
    foo: string,
    Bar: Bar
}

interface Bar {
    isBar: true;
    bar: string,
    Foo: Foo;
}

export module module {
    function foo(f: Foo): true;
}*/


module.exports = {
    foo: function (foo) {
        if (typeof foo.foo !== "string") {
            return "foo.foo not a string ";
        }
        if (typeof foo.Bar !== "object") {
            return "foo.bar not an object, it is: " + foo.Bar;
        }
        if (typeof foo.Bar.bar !== "string") {
            return "foo.bar.bar not a string";
        }
        if (typeof foo.Bar.Foo !== "object") {
            return "foo.bar.foo is not an object";
        }
        if (typeof foo.Bar.Foo.foo !== "string") {
            return "foo.bar.foo.foo is not an string";
        }
        return true;
    }
};