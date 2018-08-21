/*


interface Foo { // <- library constructed
    foo: true;
    // lib: true;
}
interface Bar { // <- client constructed!
    bar: true;
}

export module module {
    function getFoo(): Foo;
    function useFoo(f: Foo): true;
    function createBar(f: Bar): true;
}
*/

module.exports = {
    getFoo: function (x) {
        return {
            foo:true,
            lib: true
        };
    },
    useFoo: function (f) {
        return f.lib;
    },
    createBar: function (b) {
        return b.bar;
    }

};