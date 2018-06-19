/*
declare module foo {
    declare export var foo: number[];
    declare export var bar: string[];
}
*/

module.exports = {
    foo: [123, 345, 657],
    bar: ["foo", "bar", 123] // <- error.
}