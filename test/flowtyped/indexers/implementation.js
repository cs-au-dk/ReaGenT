/*
declare module foo {
    declare export var foo: {
        [number]: string,
        2: "foobar"
    };
    declare export var bar: {
        [string]: number,
        foo: 123
    };
    declare export var baz: {
        [number] : number,
    };
}


*/

module.exports = {
    foo: ["foo", "bar", "foobar"],
    bar: {foo: 123, bar: 321, baz: "foobar"}, // <- last is an error.
    baz: [321, 321, 323]
}