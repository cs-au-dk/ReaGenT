/*
declare module foo {
    declare export var foo: (a: string, ...args: Array<number>): true;
}

*/

module.exports = {
    foo: function (str, a, b, c) {
        if (c !== undefined) {
            return typeof c === "number";
        }
        if (b !== undefined) {
            return typeof b === "number";
        }
        if (a !== undefined) {
            return typeof a === "number";
        }
        return typeof str === "string";
    }
}