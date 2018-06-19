/*
declare module foo {
    declare export var bar: {
        (foo: string) => true
    };
    declare export var baz: {
        (foo: boolean) => true
    };
}

*/

module.exports = {
    bar: function (x) {
        return typeof x === "string";
    },
    baz: function (x) {
        return x; // <- Is an error!
    }
}