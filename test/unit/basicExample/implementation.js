/*
interface Foo {
    bar: Foo;
    baz: boolean;
}
declare var module: Foo;
*/

module.exports = {
    bar: {
        bar: {
            bar: null,
            baz: false
        },
        baz: true
    },
    baz: true
};