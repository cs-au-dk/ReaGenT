/*

interface Foo {
    dirty: number; // <- is number|string in one object, number|bool in anoter. (the number is always 123 though).
}

export module module {
    function getFoo(): Foo;
    function useFoo(f: Foo): true; // this must test that strong updates can be made on f, and that dirty === 123.
}
*/

module.exports = {
    getFoo: function () {
        var dirty = 123;
        if (Math.random() > 0.5) {
            if (Math.random() > 0.5) {
                dirty = "str";
            }
            return {
                dirty: dirty,
                foo: true
            }
        } else {
            if (Math.random() > 0.5) {
                dirty = true;
            }
            return { // <- different object label compared to above.
                dirty: dirty,
                bar: true
            }
        }
    },
    useFoo: function (foo) {
        if (foo.dirty !== 123) {
            return false;
        }
        foo.bar = 312;
        foo.bar = 1337;
        return true;
        // return foo.bar !== 312; // strong update.
    }
};