interface Foo {
    value: boolean;
}

export module module {
    function foo(): Foo;
    function test(x: Foo): false;
}