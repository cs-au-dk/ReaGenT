/*
declare module foo {
    declare export interface Foo {
        gen<T>(t: T): Bar<T>;
    }
    declare export interface Bar<T> {
        value: T;
        notThere: true;
    }
    declare export var foo: Foo;
}

*/

module.exports = {
    foo: {
        gen: function (t) {
            return {
                value: t
            }
        }
    }
}