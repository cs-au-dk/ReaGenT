/*
// all of this is correct
interface Foo {
    [n: number]: number;
    bar : string;
}

declare function foo() : Foo;*/

module.exports = function () {
    return {
        2: 3,
        6: false, // <- this is wrong
        bar: "foobar"
    };
};