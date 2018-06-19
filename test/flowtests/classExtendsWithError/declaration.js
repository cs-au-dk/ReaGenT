declare module foo {
    declare export class Base {
        base: true;
    }

    declare export class Extended extends Base {
        extended: true;
    }

    declare export var foo: Base;
    declare export var bar: Extended;
}
