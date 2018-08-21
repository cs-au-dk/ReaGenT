/*

interface Foo { // <- library constructed
    foo: true;
    getBaz(): Baz;
    useBaz(b: Baz): true;

}
interface Baz { // <- library constructed.
    baz: true;
    // lib: true;
}

export module module {
    function createBar(f: {
        bar: true // <- client constructed.
    }): Foo;
}
*/

module.exports = {
    createBar: function (f) {
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
            }
        }
    }

};