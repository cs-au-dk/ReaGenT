/*
declare module foo {
    interface Foo<T> {
        value: T;
    }
    interface Bar extends Foo<boolean> {
        isBar: true;
    }

    declare export var foo: Foo<string>;
    declare export var bar: Bar;
}

*/

module.exports = {
    foo: {
        value: "string"
    },
    bar: {
        value: true,
        isBar: true
    }
}