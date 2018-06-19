declare module foo {
    declare export class MyKlass<T> {
        constructor (
            t: T
        ): MyKlass<T>;
        value: T;
        isFalse: true;
    }

    declare export var bar: () => MyKlass<string>;
}
