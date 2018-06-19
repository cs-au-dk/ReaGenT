declare module foo {
    declare export var bar: {
        (foo: string) => true
    };
    declare export var baz: {
        (foo: boolean) => true
    };
}
