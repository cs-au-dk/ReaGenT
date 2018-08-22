/*

interface Foo { // <- library constructed
    foo: true;
    getBar(): Bar;
    getBaz(): Baz;
    useBaz(b: Baz): true;
}
interface Baz { // <- library constructed.
    baz: true;
    // lib: true;
}
interface Bar {
    bar: true // <- client constructed.
}

export module module {
    function createBar(f: Bar): Foo;
}
*/

module.exports = {
    createBar: function (bar) {
        return {
            foo: true,
            getBaz: function () {
                return {
                    baz: true,
                    lib: true

                }
            },
            useBaz: function (b) {
                return b.lib;
            },
            getBar: function () {
                return bar;
            }
        }
    }

};