declare module foo {
    export class Base {
        base: true;
    }

    export class Extended extends Base {
        extended: true;
    }

    // declare export var foo: Base;
    // declare export var bar: Extended;
}
