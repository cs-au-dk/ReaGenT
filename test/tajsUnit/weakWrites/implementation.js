/*interface Foo {
    bar: string,
    foo: Foo
}

export module module {
    function foo(f: Foo): true; // <- This is supposed to fail, and therefore return false.
}*/


module.exports = {
    foo: function (f) {
        f.bar = "foo";
        if (!f) {
            return "no";
        }
        if (!f.foo) {
            return "one";
        }
        if (!f.foo.bar) {
            return "two";
        }
        return f.foo.bar === "foo"; // <- I cannot know that it is the same object, it must be made into a summary.
    }
};