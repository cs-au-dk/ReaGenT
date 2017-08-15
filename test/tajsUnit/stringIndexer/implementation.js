/*
// all of this is correct
interface Foo {
    [n: string]: number;
    bar : 123;
}

declare function foo() : Foo;*/

module.exports = function () {
    var result = Object.create(null);
    result.foo = 3;
    result.ongpouna = 2;
    result.bar = 123;
    return result;
};