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
        if (Math.random() > 0.5) {
            return {
                foo: false, // go away
                bar: 123
            }
        } else {
            return {
                foo: true,
                bar: 321 // use this!
            }
        }
    },
    useFoo: function (f) {
        return f.bar === 321;
    }
};