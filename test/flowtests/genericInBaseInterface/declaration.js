declare module foo {
    declare interface Foo<T> {
        value: T;
    }
    declare interface Bar extends Foo<boolean> {
        isBar: true;
    }

    declare export var foo: Foo<string>;
    declare export var bar: Bar;
}
