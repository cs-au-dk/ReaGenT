declare module foo {
    declare export var foo: {
        [number]: string,
        2: "foobar"
    };
    declare export var bar: {
        [string]: number,
        foo: 123
    };
    declare export var baz: {
        [number] : number,
    };
}
