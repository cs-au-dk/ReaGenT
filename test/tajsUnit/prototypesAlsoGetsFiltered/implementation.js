/*

interface Foo {
    foo: true;
}

export module module {
    function createFoo(): Foo;
    function useFoo(f: Foo): true;
}*/

module.exports = {
    createFoo: function () {
        var dirty = true;
        if (Math.random() > 0.5) {
            dirty = 123;
        }
        return {
            __proto__ : {
                foo: dirty,
                bar: true
            }
        }
    },
    useFoo: function (f) {
        return f.foo === true && f.bar;
    }
};