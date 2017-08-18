interface Foo {
    bar: string,
    foo: Foo
}

export module module {
    function foo(f: Foo): true; // <- This is supposed to fail, and therefore return false.
}