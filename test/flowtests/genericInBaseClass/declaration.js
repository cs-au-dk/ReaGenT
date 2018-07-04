declare module foo {
    declare class Foo<T> {
        constructor(t: T): Foo<T>;
        value: T;
    }
    declare class Bar extends Foo<boolean> {
        constructor(): Bar;
        isBar: true;
    }

    declare export var foo: Foo<string>;
    declare export var bar: Bar;
}
