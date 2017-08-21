/*
interface Bar extends Foo {
    baz: number;
}

interface Foo {
    bar: string,
    foo: boolean
}

export module module {
    function foo(f: Foo): true;
}*/


module.exports = {
    foo: function (f) {
        if (!f) {
            return "not f";
        }
        if (typeof f.baz !== "number") {
            return "baz is not a number";
        }
        if (typeof f.bar !== "string") {
            return "bar is not a string";
        }
        if (typeof f.foo !== "boolean") {
            return "foo is not a boolean";
        }
        return true;
    }
};