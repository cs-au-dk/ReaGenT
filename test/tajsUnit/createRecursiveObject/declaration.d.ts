interface Foo {
    bar: string,
    foo: Foo
}

export module module {
    function foo(f: Foo): true;
}