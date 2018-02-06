/*
interface Foo {
    foo: true;
}

export module module {
    function makeFoo(): Foo; // <- this returns Foo | undefined. but that is suppresed.
    function useFoo(f: Foo): true; // <- this will fail if recieving "Foo | undefined", which it will do if the above isn't filtered.
}
*/

module.exports = {
    makeFoo: function () {
        var foo = {
            foo: true
        };
        return Math.random() > 0.5 ? foo : undefined;
    },
    useFoo: function (f) {
        if (!f) {
            return false;
        }
        return f.foo;
    }
};