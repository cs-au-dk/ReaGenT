/*
declare module foo {
    declare export class Base {
        base: true;
    }

    declare export class Extended {
        extended: true;
    }

    declare export var foo: Base;
    declare export var bar: Extended;
}
*/

function Base() {
    this.base = true;
}
function Extended() {
    // this.base = true; // <- Is an error!
    this.extended = true;
}

module.exports = {
    Base: Base,
    Extended: Extended,
    foo: new Base(),
    bar: new Extended()
}