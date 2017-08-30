/*
interface Foo {
    // priv: () => number;
}

export module module {
    function create(): Foo;
    function consume(f: Foo): number;
}
*/


module.exports = {
    create: function () {
        return {
            priv: function () {
                return 123;
            }
        }
    },
    consume: function (f) {
        return f.priv();
    }
};