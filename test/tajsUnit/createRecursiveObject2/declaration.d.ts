interface Foo {
    [index: string]: Foo;
}

export module module {
    function foo(f: Foo): undefined; // <- no
}